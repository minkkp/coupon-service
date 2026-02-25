# 선착순 쿠폰 발급 시스템

Redis + Lua를 활용해 대규모 요청 환경에서 한정 수량의 쿠폰을 정확하게 발급하고,  
AWS EC2 환경에 Docker 기반으로 배포하여 Blue/Green 무중단 배포 및  
Prometheus + Grafana 모니터링까지 구성한 프로젝트입니다.
<br><br>

## 🛠️ 기술 스택

### Backend
- Spring Boot, JPA
- Redis (Lua Script)
- PostgreSQL

### Infra / DevOps
- AWS EC2
- Docker / Docker Compose
- Nginx (Reverse Proxy)
- GitHub Actions (CI/CD)
- Blue/Green Deployment (Shell Script 기반)
- Prometheus + Grafana (메트릭 수집 & 모니터링)
<br><br>

## 📌 프로젝트 목적

- 대규모 트래픽 환경에서 한정 수량 쿠폰을 정확하게 발급
- 트래픽 증가, Redis 장애, 운영 복구까지 고려한 설계 경험
- 쿠폰 과발급 / 중복 발급 방지
<br><br>

## 🧠 핵심 설계 요약

- Redis + Lua 기반 선착순 제어 (원자 처리)  
  → 단일 Redis 연산으로 동시 요청 상황에서도 안정적인 발급 보장
- 쿠폰 발급 처리와 이력 저장을 분리하여 병목 제거
- Redis 장애 시 Fail-Fast 전략 적용  
  → 대체 처리 없이 즉시 실패 처리하여 정합성 유지  
  → DB에 저장된 발급 이력을 기준으로 Redis 재고를 다시 맞추는 동기화 API 제공
- 이벤트 종료 시 Redis 키를 즉시 삭제하고, 누락을 대비해 만료 시간(TTL)도 함께 설정
<br><br>

## 🏗️ 아키텍처 개요

- **Redis**
  - 쿠폰 재고 및 사용자 요청 관리
  - Lua 스크립트로 중복 요청 확인과 쿠폰 재고 감소를 원자적으로 처리
    
- **DB**
  - 쿠폰 발급 성공 시 이력을 비동기 저장

- **Application**
  - 쿠폰 발급 흐름 오케스트레이션
  - 이벤트 생명주기(open / close / sync) 관리
<br><br>

## 🧩 핵심 코드 구성

- CouponFacadeService  
  → 쿠폰 발급 전체 흐름 오케스트레이션

- RedisCouponService  
  → Redis + Lua 기반 쿠폰 발급 제어

- EventService  
  → 이벤트 생명주기 및 운영(open / close / sync) 관리

- AsyncCouponIssueService  
  → 발급 이력을 비동기 DB 저장
<br><br>

## 🔄 쿠폰 발급 흐름

1. 이벤트(쿠폰) 상태 및 기간 검증 (DB)
2. Redis + Lua를 통한 선착순 발급 처리
   - 사용자 중복 요청 체크
   - 쿠폰 재고 체크 및 감소
3. 발급 성공 시 DB에 비동기로 내역 저장
<br><br>

## 🚨 장애 · 이슈 대응

### Redis 장애
- Redis 오류 발생 시 Fail-Fast 전략으로 대체 처리 없이 즉시 실패 처리하여 재고 정합성 유지

### DB 비동기 저장 실패
- 운영자가 동기화 API 호출하여 DB를 기준(Source of Truth)으로 Redis 재고 복구

### 이벤트 종료 처리
- 이벤트 종료 시 Redis 키 즉시 제거
- 만료 시간(TTL) 설정에 따라 이벤트 자동 종료 처리 (2차 안전장치)
<br><br>

## 🗂️ ERD

쿠폰 발급 성능과 정합성을 우선시하여  
쿠폰 발급 흐름에 직접적으로 필요한 테이블만 최소한으로 설계

- 사용자(User) 정보는 발급 로직에 직접 사용하지 않아  
  쿠폰 발급 도메인과 연관관계를 맺지 않음
- 이벤트(Event)는 발급 조건(기간, 수량)을 관리하는 기준 엔티티
- 쿠폰(Coupon)은 이벤트를 통해 발급되는 혜택 정보
- 쿠폰발급이력(CouponIssue)은 발급 성공 이력만 저장하며,  
  실제 발급 여부 판단은 Redis에서 처리하고  
  (user_id, event_id) 유니크 제약을 적용해 대량 데이터 환경에서도 중복 발급 방지

<img width="884" height="496" alt="image" src="https://github.com/user-attachments/assets/25de7201-f409-46fb-b8bc-dfbe9c765b80" />
<br><br>

## ⚡ 부하 테스트 (k6)

### 테스트 조건
- 시나리오: 한정 수량 선착순 쿠폰 발급
- 요청 방식: constant-arrival-rate (1초 단위로 계속 요청)
- 초당 요청 수: 약 2,800
- 테스트 시간: 60초
- 최대 VU: 3,000
- 한정 수량 쿠폰: 100,000

### 테스트 결과
- 총 요청 수(total_request): 113,286
- 발급 성공(success): 100,000
- 재고 부족(sold_out): 13,286
- 과발급: 0
- HTTP 요청 실패: 0%
- coupon_issue 테이블에 발급 이력 100,000행 확인
- 동시 요청 환경에서도 쿠폰 수량(100,000)을 초과하지 않고 정확하게 발급됨을 확인

<img width="592" height="366" alt="image" src="https://github.com/user-attachments/assets/2fb7c5fc-dc09-4556-afd4-6ae2148cf7dc" />
<br><br>

### 성능 분석
- 초당 2,800 요청 환경에서 실제 초당 처리량(RPS)은 1,500~1,800 수준으로 확인
- 평균 HTTP 요청-응답 시간은 약 1.5초로 측정
- Redis의 단일 스레드 특성으로 인해 요청 대기가 발생하여 성능에 영향을 준 것으로 판단
<br><br>

## 🚀 배포 및 운영 경험

### 🔹 EC2 + Docker Compose 기반 인프라 구성
- 단일 EC2 인스턴스에서 Docker Compose로 전체 스택 구성
  - PostgreSQL
  - Redis
  - Blue / Green 애플리케이션 컨테이너
  - Nginx Reverse Proxy
  - Prometheus
  - Grafana
- 컨테이너별 CPU / Memory 제한 설정으로 자원 독점 방지

### 🔹 Blue/Green 무중단 배포 구현
- coupon-service-blue / coupon-service-green 이중 컨테이너 운영
- 신규 버전 기동 후 `/actuator/health` 기반 헬스 체크 검증
- 성공 시 Nginx upstream 동적 변경
- Graceful Shutdown 설정(60초)으로 요청 유실 방지

### 🔹 CI/CD 자동화
- main 브랜치 push 시 GitHub Actions 실행
- 테스트 통과 후 Docker 이미지 빌드
- github.sha 기반 버전 태깅
- DockerHub 푸시 후 EC2 SSH 접속 자동 배포

### 🔹 모니터링 환경 구축
- Spring Boot Actuator + Micrometer 연동
- Prometheus를 통한 JVM / HTTP / Redis 메트릭 수집
- Grafana 대시보드 구성
- Redis 메모리 사용량 및 요청 처리 지표 시각화
<br><br>
## ✍️ 마무리
선착순 쿠폰 발급을 구현하며 단순 기능 구현을 넘어  
동시성 제어, 장애 대응, 무중단 배포, 모니터링까지  
운영 환경을 고려한 시스템 설계를 경험할 수 있었습니다.
<br><br>

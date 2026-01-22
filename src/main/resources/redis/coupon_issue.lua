-- KEYS[1] = users key
-- KEYS[2] = stock key
-- ARGV[1] = userId

-- 중복 참여 체크
if redis.call("SISMEMBER", KEYS[1], ARGV[1]) == 1 then
  return -1
end

-- 재고 확인
local stock = tonumber(redis.call("GET", KEYS[2]))
if not stock or stock <= 0 then
  return 0  -- SOLD_OUT
end

-- 재고 감소 + 참여 기록 (원자 처리)
redis.call("DECR", KEYS[2])
redis.call("SADD", KEYS[1], ARGV[1])

return 1 -- SUCCESS
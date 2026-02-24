#!/bin/bash
set -e

BLUE_SERVICE="coupon-service-blue"
GREEN_SERVICE="coupon-service-green"
NGINX_SERVICE="nginx"
UPSTREAM_FILE="./upstream.inc"

echo "========================================="
echo "Starting Blue-Green Deployment"
echo "========================================="

docker compose up -d nginx

if docker compose exec -T $NGINX_SERVICE \
    cat /etc/nginx/conf.d/upstream.inc | grep -q "$BLUE_SERVICE"; then
    CURRENT="blue"
    TARGET="green"
    TARGET_PORT=8082
else
    CURRENT="green"
    TARGET="blue"
    TARGET_PORT=8081
fi

echo "Current active environment: $CURRENT"
echo "Target environment: $TARGET"

echo "Starting $TARGET container..."
docker compose up -d coupon-service-$TARGET

echo "Performing health check on port $TARGET_PORT..."

MAX_RETRY=30
COUNT=0

until curl -fs http://localhost:$TARGET_PORT/actuator/health | grep -q "UP"; do
  COUNT=$((COUNT+1))

  if [ $COUNT -ge $MAX_RETRY ]; then
    echo "Health check failed. Rolling back..."
    docker compose stop coupon-service-$TARGET
    exit 1
  fi

  sleep 3
done

echo "Health check passed."

echo "Switching nginx upstream to $TARGET..."

echo "set \$service_url coupon-service-$TARGET;" > $UPSTREAM_FILE
docker compose exec -T $NGINX_SERVICE sh -c \ "cat > /etc/nginx/conf.d/upstream.inc" < $UPSTREAM_FILE

if ! docker compose exec -T $NGINX_SERVICE nginx -t; then
  echo "Nginx config test failed. Rolling back..."
  docker compose stop coupon-service-$TARGET
  exit 1
fi

docker compose exec -T $NGINX_SERVICE nginx -s reload
echo "Traffic successfully switched to $TARGET"

sleep 5

echo "Stopping previous container: $CURRENT"
docker compose stop coupon-service-$CURRENT

echo "========================================="
echo "Blue-Green Deployment Completed"
echo "========================================="
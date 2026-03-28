#!/bin/bash

# JWT 및 Redis Refresh Token 상태 확인 테스트
# Phase 2: Stateless Authentication with Redis Refresh Token

BASE_URL="http://localhost:8080"
COOKIE_JAR="jwt_cookies.txt"

# Function to make curl request and separate headers from body
do_curl() {
  local method=$1
  local url=$2
  shift 2
  curl -s -D - "$@" "$url" -X "$method"
}

rm -f "$COOKIE_JAR"

echo "=== JWT & Redis Refresh Token Test ==="
echo ""

echo "=== 1. Login (testuser) ==="
LOGIN_RESPONSE=$(do_curl POST "$BASE_URL/api/v1/login" \
  -c "$COOKIE_JAR" \
  -d "username=testuser&password=password123")

ACCESS_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -i "Authorization:" | awk '{print $2}' | tr -d '\r')
echo "Login successful. Access Token received."
echo

echo "=== 2. Check Redis for Refresh Token (Key: RT:testuser) ==="
# Using auth-redis container name from docker-compose.yml
docker exec auth-redis redis-cli keys 'RT:*'
echo "Token Value in Redis:"
docker exec auth-redis redis-cli get 'RT:testuser'
echo ""

echo "=== 3. Refresh Token (Verify Redis Update) ==="
REFRESH_RESPONSE=$(do_curl POST "$BASE_URL/api/v1/refresh" \
  -b "$COOKIE_JAR" \
  -c "$COOKIE_JAR")

NEW_ACCESS_TOKEN=$(echo "$REFRESH_RESPONSE" | grep -i "Authorization:" | awk '{print $2}' | tr -d '\r')
echo "Refresh successful. New Access Token received."
echo

echo "=== 4. Check Redis again (Rotation Check) ==="
docker exec auth-redis redis-cli get 'RT:testuser'
echo ""

echo "=== 5. Logout ==="
do_curl POST "$BASE_URL/api/v1/logout" \
  -b "$COOKIE_JAR" \
  -H "Authorization: Bearer $NEW_ACCESS_TOKEN"
echo

echo "=== 6. Check Redis after logout (Should be deleted if LogoutHandler is implemented) ==="
docker exec auth-redis redis-cli keys 'RT:*'

rm -f "$COOKIE_JAR"

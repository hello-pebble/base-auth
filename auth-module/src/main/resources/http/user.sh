#!/bin/bash

# Base Auth API Test (Signup, Login, Me)
# Phase 2: JWT Based Authentication

BASE_URL="http://localhost:8080"
COOKIE_JAR="cookies.txt"

# Function to make curl request and separate headers from body
do_curl() {
  local method=$1
  local url=$2
  shift 2
  curl -s -D - "$@" "$url" -X "$method"
}

rm -f "$COOKIE_JAR"

echo "=== 1. Sign Up (testuser3) ==="
do_curl POST "$BASE_URL/api/v1/users/signup" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser3",
    "password": "password123"
  }'
echo

echo "=== 2. Login (testuser3) ==="
# Login and save Access Token from Authorization header
LOGIN_RESPONSE=$(do_curl POST "$BASE_URL/api/v1/login" \
  -c "$COOKIE_JAR" \
  -d "username=testuser3&password=password123")

ACCESS_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -i "Authorization:" | awk '{print $2}' | tr -d '\r')

echo "Access Token: $ACCESS_TOKEN"
echo

echo "=== 3. Get My Info (Authenticated with Bearer Token) ==="
do_curl GET "$BASE_URL/api/v1/users/me" \
  -H "Authorization: Bearer $ACCESS_TOKEN"
echo

echo "=== 4. Get My Info (Without Token - Should Fail 403/401) ==="
do_curl GET "$BASE_URL/api/v1/users/me"
echo

rm -f "$COOKIE_JAR"

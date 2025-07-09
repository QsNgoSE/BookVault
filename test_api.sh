#!/bin/bash

# Get fresh token
echo "Getting fresh token..."
TOKEN=$(curl -s -X POST https://auth-service-production-744b.up.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "vu@email.com", "password": "abc123"}' | jq -r '.data.token')

echo "Token: $TOKEN"

# Test book creation
echo "Testing book creation..."
RESPONSE=$(curl -s -w "\nHTTP_STATUS:%{http_code}" -X POST https://book-service-production-4444.up.railway.app/api/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"title":"API Test Book","author":"Test Author","description":"Testing the updated API","price":25.99,"stockQuantity":5,"categoryNames":["Fiction"]}')

echo "Response: $RESPONSE" 
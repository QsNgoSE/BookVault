#!/bin/bash

echo "🔧 BookVault Admin Account Test Script"
echo "====================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}This script will help you test admin account creation and login${NC}"
echo ""

# Check if services are running
echo -e "${YELLOW}1. Checking if auth service is running...${NC}"
if curl -s http://localhost:8082/actuator/health > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Auth service is running${NC}"
else
    echo -e "${RED}❌ Auth service is not running. Please start it first:${NC}"
    echo "   cd backend && docker-compose up -d"
    exit 1
fi

# Test admin login
echo -e "${YELLOW}2. Testing admin login...${NC}"
echo "Attempting to login with default admin credentials:"
echo "Email: admin@bookvault.com"
echo "Password: admin123"

LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@bookvault.com",
    "password": "admin123"
  }')

if echo "$LOGIN_RESPONSE" | grep -q "token"; then
    echo -e "${GREEN}✅ Admin login successful!${NC}"
    echo "Response: $LOGIN_RESPONSE"
    
    # Extract token for further testing
    TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    
    if [ ! -z "$TOKEN" ]; then
        echo -e "${YELLOW}3. Testing admin endpoints...${NC}"
        
        # Test admin dashboard stats
        STATS_RESPONSE=$(curl -s -H "Authorization: Bearer $TOKEN" \
          http://localhost:8082/api/auth/admin/dashboard/stats)
        
        if echo "$STATS_RESPONSE" | grep -q "totalUsers"; then
            echo -e "${GREEN}✅ Admin dashboard stats accessible${NC}"
            echo "Stats: $STATS_RESPONSE"
        else
            echo -e "${RED}❌ Cannot access admin dashboard stats${NC}"
        fi
        
        # Test get users endpoint
        USERS_RESPONSE=$(curl -s -H "Authorization: Bearer $TOKEN" \
          http://localhost:8082/api/auth/admin/users)
        
        if echo "$USERS_RESPONSE" | grep -q "content"; then
            echo -e "${GREEN}✅ Admin users endpoint accessible${NC}"
        else
            echo -e "${RED}❌ Cannot access admin users endpoint${NC}"
        fi
    fi
else
    echo -e "${RED}❌ Admin login failed${NC}"
    echo "Response: $LOGIN_RESPONSE"
    echo ""
    echo -e "${YELLOW}Possible solutions:${NC}"
    echo "1. Default admin might not be created yet - restart auth service"
    echo "2. Register a new admin account manually via the web interface"
    echo "3. Check database logs for errors"
fi

echo ""
echo -e "${BLUE}📋 Admin Account Information:${NC}"
echo "Default Admin Email: admin@bookvault.com"
echo "Default Admin Password: admin123"
echo "Admin Panel URL: http://localhost:3000/admin.html"
echo ""
echo -e "${YELLOW}⚠️  Remember to change the default password after first login!${NC}" 
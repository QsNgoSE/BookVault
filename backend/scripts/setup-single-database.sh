#!/bin/bash
set -e

echo "🗄️ Setting up single shared database for BookVault..."

# Create the main bookvault database (ignore error if exists)
docker exec bookvault-postgres psql -U bookvault -d postgres -c "CREATE DATABASE bookvault;" 2>/dev/null || echo "Database 'bookvault' already exists"

# Add required extensions
docker exec bookvault-postgres psql -U bookvault -d bookvault -c "
CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";
CREATE EXTENSION IF NOT EXISTS \"pgcrypto\"; 
CREATE EXTENSION IF NOT EXISTS \"pg_trgm\";
"

echo "✅ Single database 'bookvault' is ready!"
echo "📊 All services will now use: postgresql://localhost:5432/bookvault" 
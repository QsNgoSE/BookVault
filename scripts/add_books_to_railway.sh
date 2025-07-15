#!/bin/bash

# Script to add book data to Railway PostgreSQL database
# Uses the Railway connection parameters provided

set -e  # Exit on any error

echo "🚀 Starting book data insertion to Railway PostgreSQL..."

# Check if SQL file exists
if [ ! -f "init-100-books.sql" ]; then
    echo "❌ SQL file not found: init-100-books.sql"
    exit 1
fi

# Set database connection parameters for Railway
export PGHOST="maglev.proxy.rlwy.net"
export PGPORT="43721"
export PGDATABASE="railway"
export PGUSER="postgres"
export PGPASSWORD="LyxjyafXTDmZBQgGaufLHvYJwdNbTwuR"

echo "📊 Database connection parameters:"
echo "  Host: $PGHOST"
echo "  Port: $PGPORT"
echo "  Database: $PGDATABASE"
echo "  User: $PGUSER"

# Test database connection
echo "🔌 Testing database connection..."
if psql -c "SELECT 1;" > /dev/null 2>&1; then
    echo "✅ Successfully connected to Railway PostgreSQL database"
else
    echo "❌ Failed to connect to database"
    exit 1
fi

# Execute SQL file
echo "📚 Executing SQL file to add books..."
if psql -f "init-100-books.sql"; then
    echo "✅ Successfully executed SQL file"
else
    echo "❌ Failed to execute SQL file"
    exit 1
fi

# Verify books were added
echo "📊 Verifying books were added..."
BOOK_COUNT=$(psql -t -c "SELECT COUNT(*) FROM books;" | xargs)
echo "📚 Total books in database: $BOOK_COUNT"

if [ "$BOOK_COUNT" -gt 0 ]; then
    echo "✅ Verification successful - books are in the database"
    echo "🎉 Book data insertion completed successfully!"
else
    echo "⚠️  Verification failed - please check the database manually"
    exit 1
fi 
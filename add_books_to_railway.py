#!/usr/bin/env python3
"""
Script to add book data to Railway PostgreSQL database
Uses the environment variables provided for database connection
"""

import os
import psycopg2
import sys
from pathlib import Path

def get_database_connection():
    """Create database connection using Railway parameters"""
    try:
        # Database connection parameters for Railway
        db_params = {
            'host': 'maglev.proxy.rlwy.net',
            'port': 43721,
            'database': 'railway',
            'user': 'postgres',
            'password': 'LyxjyafXTDmZBQgGaufLHvYJwdNbTwuR'
        }
        
        # Create connection
        conn = psycopg2.connect(**db_params)
        print("✅ Successfully connected to Railway PostgreSQL database")
        return conn
    except Exception as e:
        print(f"❌ Failed to connect to database: {e}")
        sys.exit(1)

def execute_sql_file(conn, sql_file_path):
    """Execute SQL file on the database"""
    try:
        # Read SQL file
        with open(sql_file_path, 'r') as file:
            sql_content = file.read()
        
        # Create cursor
        cursor = conn.cursor()
        
        # Execute SQL
        print("📚 Executing SQL file to add books...")
        cursor.execute(sql_content)
        
        # Commit changes
        conn.commit()
        
        # Get the number of affected rows (approximate for INSERT statements)
        print(f"✅ Successfully executed SQL file")
        
        cursor.close()
        return True
        
    except Exception as e:
        print(f"❌ Failed to execute SQL file: {e}")
        conn.rollback()
        return False

def verify_books_added(conn):
    """Verify that books were added successfully"""
    try:
        cursor = conn.cursor()
        cursor.execute("SELECT COUNT(*) FROM books")
        count = cursor.fetchone()[0]
        cursor.close()
        
        print(f"📊 Total books in database: {count}")
        return count > 0
        
    except Exception as e:
        print(f"❌ Failed to verify books: {e}")
        return False

def main():
    """Main function"""
    print("🚀 Starting book data insertion to Railway PostgreSQL...")
    
    # Check if SQL file exists
    sql_file = Path("init-100-books.sql")
    if not sql_file.exists():
        print(f"❌ SQL file not found: {sql_file}")
        sys.exit(1)
    
    # Connect to database
    conn = get_database_connection()
    
    try:
        # Execute SQL file
        if execute_sql_file(conn, sql_file):
            print("✅ Book data insertion completed successfully!")
            
            # Verify books were added
            if verify_books_added(conn):
                print("✅ Verification successful - books are in the database")
            else:
                print("⚠️  Verification failed - please check the database manually")
        else:
            print("❌ Book data insertion failed")
            sys.exit(1)
            
    finally:
        conn.close()
        print("🔌 Database connection closed")

if __name__ == "__main__":
    main() 
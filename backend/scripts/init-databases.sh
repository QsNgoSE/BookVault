#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    -- Create databases for each microservice
    CREATE DATABASE bookvault_auth;
    CREATE DATABASE bookvault_users;
    CREATE DATABASE bookvault_books;
    CREATE DATABASE bookvault_orders;
    CREATE DATABASE bookvault_notifications;
    
    -- Grant privileges
    GRANT ALL PRIVILEGES ON DATABASE bookvault_auth TO $POSTGRES_USER;
    GRANT ALL PRIVILEGES ON DATABASE bookvault_users TO $POSTGRES_USER;
    GRANT ALL PRIVILEGES ON DATABASE bookvault_books TO $POSTGRES_USER;
    GRANT ALL PRIVILEGES ON DATABASE bookvault_orders TO $POSTGRES_USER;
    GRANT ALL PRIVILEGES ON DATABASE bookvault_notifications TO $POSTGRES_USER;
    
    -- Create extensions for each database
    \c bookvault_auth;
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    CREATE EXTENSION IF NOT EXISTS "pgcrypto";
    
    \c bookvault_users;
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    CREATE EXTENSION IF NOT EXISTS "pgcrypto";
    
    \c bookvault_books;
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    CREATE EXTENSION IF NOT EXISTS "pgcrypto";
    CREATE EXTENSION IF NOT EXISTS "pg_trgm";
    
    \c bookvault_orders;
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    CREATE EXTENSION IF NOT EXISTS "pgcrypto";
    
    \c bookvault_notifications;
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    CREATE EXTENSION IF NOT EXISTS "pgcrypto";
EOSQL

echo "All databases created successfully!" 
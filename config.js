/**
 * BookVault Configuration
 * Automatically detects environment and sets appropriate API URLs
 */

const BookVaultConfig = {
    // Auto-detect environment
    environment: window.location.hostname.includes('github.io') ? 'production' : 'development',
    
    // Development API Configuration (localhost)
    development: {
      AUTH_SERVICE_URL: 'https://auth-service-production-744b.up.railway.app/api',
      BOOK_SERVICE_URL: 'https://book-service-production-4444.up.railway.app/api',
      ORDER_SERVICE_URL: 'https://book-service-production-4444.up.railway.app/api', // Combined with book service
      BASE_URL: 'https://auth-service-production-744b.up.railway.app/api',
    },
    
    // Production API Configuration (Railway)
    production: {
        AUTH_SERVICE_URL: 'https://auth-service-production-744b.up.railway.app/api',
        BOOK_SERVICE_URL: 'https://book-service-production-4444.up.railway.app/api',
        ORDER_SERVICE_URL: 'https://book-service-production-4444.up.railway.app/api', // Combined with book service
        BASE_URL: 'https://auth-service-production-744b.up.railway.app/api',
    },
    
    // Common configuration
    ENDPOINTS: {
        // Authentication
        AUTH: {
            LOGIN: '/auth/login',
            REGISTER: '/auth/register',
            PROFILE: '/auth/profile',
            VALIDATE: '/auth/validate',
            HEALTH: '/auth/health'
        },
        
        // Books
        BOOKS: {
            BASE: '/books',
            SEARCH: '/books/search',
            CATEGORIES: '/books/categories',
            FEATURED: '/books/featured',
            BESTSELLERS: '/books/bestsellers',
            NEW_RELEASES: '/books/new-releases',
            BY_CATEGORY: '/books/category',
            BY_AUTHOR: '/books/author',
            FILTER: '/books/filter',
            BY_ISBN: '/books/isbn',
            BY_SELLER: '/books/seller'
        },
        
        // Users
        USERS: {
            PROFILE: '/users/profile',
            ORDERS: '/users/orders',
            WISHLIST: '/users/wishlist',
            FAVORITES: '/users/favorites',
            RECOMMENDATIONS: '/users/recommendations',
            ADDRESSES: '/users/addresses'
        },
        
        // Orders
        ORDERS: {
            BASE: '/orders',
            CREATE: '/orders/create',
            HISTORY: '/orders/history',
            TRACK: '/orders/track',
            CANCEL: '/orders/cancel'
        },
        
        // Admin
        ADMIN: {
            USERS: '/admin/users',
            BOOKS: '/admin/books',
            ORDERS: '/admin/orders',
            ANALYTICS: '/admin/analytics'
        }
    },
    
    // App Configuration
    APP: {
        NAME: 'BookVault',
        VERSION: '1.0.0',
        
        // UI Configuration
        PAGINATION: {
            BOOKS_PER_PAGE: 12,
            ORDERS_PER_PAGE: 10,
            USERS_PER_PAGE: 15
        },
        
        // File Upload
        UPLOAD: {
            MAX_FILE_SIZE: 5 * 1024 * 1024, // 5MB
            ALLOWED_FORMATS: ['jpg', 'jpeg', 'png', 'pdf']
        },
        
        // Search Configuration
        SEARCH: {
            MIN_QUERY_LENGTH: 2,
            DEBOUNCE_DELAY: 300
        }
    }
};

// Get current environment configuration
const getCurrentConfig = () => {
    const env = BookVaultConfig.environment;
    return {
        ...BookVaultConfig[env],
        ...BookVaultConfig.ENDPOINTS,
        ...BookVaultConfig.APP
    };
};

// Export for use in other files
const API_CONFIG = getCurrentConfig();

// For debugging
console.log('BookVault Environment:', BookVaultConfig.environment);
console.log('API Configuration:', API_CONFIG);

// Export for different module systems
if (typeof module !== 'undefined' && module.exports) {
    module.exports = BookVaultConfig;
} else if (typeof window !== 'undefined') {
    window.BookVaultConfig = BookVaultConfig;
}

// Freeze the configuration to prevent accidental modifications
if (typeof Object.freeze === 'function') {
    Object.freeze(BookVaultConfig);
} 
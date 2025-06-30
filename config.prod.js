/**
 * BookVault Production Configuration
 * Production-ready configuration for deployed backend services
 */

const BookVaultConfig = {
    // Production API Configuration
    API: {
        // Production service URLs - Update these with your deployed service URLs
        AUTH_SERVICE_URL: 'https://bookvault-auth.railway.app/api', // Update with your Railway URL
        BOOK_SERVICE_URL: 'https://bookvault-books.railway.app/api', // Update with your Railway URL
        ORDER_SERVICE_URL: 'https://bookvault-auth.railway.app/api', // Combined with auth service
        
        // Fallback base URL
        BASE_URL: 'https://bookvault-auth.railway.app/api',
        
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
            
            // Users (Combined with Auth service)
            USERS: {
                PROFILE: '/users/profile',
                ORDERS: '/users/orders',
                WISHLIST: '/users/wishlist',
                FAVORITES: '/users/favorites',
                RECOMMENDATIONS: '/users/recommendations',
                ADDRESSES: '/users/addresses'
            },
            
            // Sellers (Combined with Auth service)
            SELLERS: {
                DASHBOARD: '/sellers/dashboard',
                BOOKS: '/sellers/books',
                ORDERS: '/sellers/orders',
                ANALYTICS: '/sellers/analytics',
                PROFILE: '/sellers/profile'
            },
            
            // Admin (Combined with Auth service)
            ADMIN: {
                DASHBOARD: '/admin/dashboard',
                USERS: '/admin/users',
                SELLERS: '/admin/sellers',
                BOOKS: '/admin/books',
                ORDERS: '/admin/orders',
                ANALYTICS: '/admin/analytics',
                SETTINGS: '/admin/settings'
            },
            
            // Orders (Combined with Auth service)
            ORDERS: {
                BASE: '/orders',
                CREATE: '/orders',
                PAYMENT: '/orders/{id}/payment',
                CANCEL: '/orders/{id}/cancel',
                TRACK: '/orders/{id}/track'
            },
            
            // Cart (Combined with Auth service)
            CART: {
                BASE: '/cart',
                ADD: '/cart/add',
                REMOVE: '/cart/remove',
                UPDATE: '/cart/update',
                CLEAR: '/cart/clear'
            }
        },
        
        // Request timeout in milliseconds
        TIMEOUT: 30000,
        
        // Retry configuration
        RETRY: {
            ATTEMPTS: 3,
            DELAY: 1000
        }
    },
    
    // App Settings
    APP: {
        NAME: 'BookVault',
        VERSION: '1.0.0',
        DESCRIPTION: 'Modern Online Book Marketplace',
        
        // Pagination
        PAGINATION: {
            DEFAULT_PAGE_SIZE: 12,
            MAX_PAGE_SIZE: 50
        },
        
        // Search
        SEARCH: {
            MIN_QUERY_LENGTH: 2,
            DEBOUNCE_DELAY: 300
        },
        
        // Image settings
        IMAGES: {
            PLACEHOLDER: '/asset/img/books/placeholder.jpg',
            MAX_UPLOAD_SIZE: 5 * 1024 * 1024, // 5MB
            ALLOWED_TYPES: ['image/jpeg', 'image/png', 'image/webp']
        },
        
        // Local storage keys
        STORAGE_KEYS: {
            AUTH_TOKEN: 'bookvault_auth_token',
            USER_ROLE: 'bookvault_user_role',
            USER_PROFILE: 'bookvault_user_profile',
            CART: 'bookvault_cart',
            WISHLIST: 'bookvault_wishlist',
            PREFERENCES: 'bookvault_preferences'
        },
        
        // Currency
        CURRENCY: {
            CODE: 'USD',
            SYMBOL: '$',
            DECIMAL_PLACES: 2
        }
    },
    
    // Features flags for production
    FEATURES: {
        ENABLE_WISHLISTS: true,
        ENABLE_REVIEWS: true,
        ENABLE_RECOMMENDATIONS: false, // Disabled for simplicity
        ENABLE_SOCIAL_LOGIN: false,
        ENABLE_CART_PERSISTENCE: true,
        ENABLE_OFFLINE_MODE: false,
        ENABLE_PWA: false,
        ENABLE_ANALYTICS: false // Disabled for free tier
    }
};

// Export for use in HTML pages
if (typeof module !== 'undefined' && module.exports) {
    module.exports = BookVaultConfig;
} 
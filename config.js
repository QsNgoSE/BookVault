/**
 * BookVault Frontend Configuration
 * Centralized configuration for API endpoints, app settings, and environment variables
 */

const BookVaultConfig = {
    // API Configuration - Updated for Real Backend Integration
    API: {
        // Individual service URLs (microservices architecture)
        AUTH_SERVICE_URL: 'http://localhost:8082/api',
        BOOK_SERVICE_URL: 'http://localhost:8083/api', 
        ORDER_SERVICE_URL: 'http://localhost:8084/api',
        
        // Legacy base URL for compatibility
        BASE_URL: (typeof process !== 'undefined' && process.env && process.env.NODE_ENV === 'production') 
            ? 'https://api.bookvault.com/api' 
            : 'http://localhost:8082/api',
        
        ENDPOINTS: {
            // Authentication - Real Auth Service
            AUTH: {
                LOGIN: '/auth/login',
                REGISTER: '/auth/register',
                PROFILE: '/auth/profile',
                VALIDATE: '/auth/validate',
                HEALTH: '/auth/health'
            },
            
            // Books - Real Book Service
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
            
            // Sellers
            SELLERS: {
                DASHBOARD: '/sellers/dashboard',
                BOOKS: '/sellers/books',
                ORDERS: '/sellers/orders',
                ANALYTICS: '/sellers/analytics',
                PROFILE: '/sellers/profile'
            },
            
            // Admin - Updated to match backend endpoints
            ADMIN: {
                STATS: '/auth/admin/dashboard/stats',
                DASHBOARD: '/auth/admin/dashboard',
                USERS: '/auth/admin/users',
                SELLERS: '/auth/admin/sellers',
                BOOKS: '/auth/admin/books',
                ORDERS: '/auth/admin/orders',
                ANALYTICS: '/auth/admin/analytics',
                SETTINGS: '/auth/admin/settings'
            },
            
            // Orders
            ORDERS: {
                BASE: '/orders',
                CREATE: '/orders',
                PAYMENT: '/orders/{id}/payment',
                CANCEL: '/orders/{id}/cancel',
                TRACK: '/orders/{id}/track'
            },
            
            // Cart
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
        
        // Date formats
        DATE_FORMATS: {
            DISPLAY: 'MMM DD, YYYY',
            INPUT: 'YYYY-MM-DD',
            DATETIME: 'MMM DD, YYYY HH:mm'
        },
        
        // Currency
        CURRENCY: {
            CODE: 'USD',
            SYMBOL: '$',
            DECIMAL_PLACES: 2
        }
    },
    
    // UI Configuration
    UI: {
        // Theme colors
        COLORS: {
            PRIMARY: '#667eea',
            SECONDARY: '#764ba2',
            SUCCESS: '#2ecc71',
            ERROR: '#e74c3c',
            WARNING: '#f39c12',
            INFO: '#3498db'
        },
        
        // Animation durations (ms)
        ANIMATIONS: {
            FAST: 200,
            NORMAL: 300,
            SLOW: 500
        },
        
        // Breakpoints
        BREAKPOINTS: {
            XS: 576,
            SM: 768,
            MD: 992,
            LG: 1200,
            XL: 1400
        },
        
        // Toast notifications
        TOAST: {
            DURATION: 5000,
            POSITION: 'top-right'
        }
    },
    
    // Features flags
    FEATURES: {
        ENABLE_WISHLISTS: true,
        ENABLE_REVIEWS: true,
        ENABLE_RECOMMENDATIONS: true,
        ENABLE_SOCIAL_LOGIN: false, // Will be handled later
        ENABLE_CART_PERSISTENCE: true,
        ENABLE_OFFLINE_MODE: false,
        ENABLE_PWA: false,
        ENABLE_ANALYTICS: true
    },
    
    // Third-party integrations
    INTEGRATIONS: {
        // Google Maps (for contact page)
        GOOGLE_MAPS: {
            API_KEY: (typeof process !== 'undefined' && process.env && process.env.GOOGLE_MAPS_API_KEY) || '',
            DEFAULT_LOCATION: {
                lat: 40.7128,
                lng: -74.0060,
                zoom: 15
            }
        },
        
        // Analytics
        ANALYTICS: {
            GOOGLE_ANALYTICS_ID: (typeof process !== 'undefined' && process.env && process.env.GOOGLE_ANALYTICS_ID) || '',
            ENABLE_TRACKING: (typeof process !== 'undefined' && process.env && process.env.NODE_ENV === 'production') || false
        },
        
        // Payment gateways (for future implementation)
        PAYMENT: {
            STRIPE_PUBLIC_KEY: (typeof process !== 'undefined' && process.env && process.env.STRIPE_PUBLIC_KEY) || '',
            PAYPAL_CLIENT_ID: (typeof process !== 'undefined' && process.env && process.env.PAYPAL_CLIENT_ID) || ''
        }
    },
    
    // Error messages
    ERRORS: {
        NETWORK: 'Network error. Please check your connection and try again.',
        AUTHENTICATION: 'Authentication failed. Please login again.',
        AUTHORIZATION: 'You are not authorized to perform this action.',
        NOT_FOUND: 'The requested resource was not found.',
        SERVER_ERROR: 'Server error. Please try again later.',
        VALIDATION: 'Please check your input and try again.',
        TIMEOUT: 'Request timed out. Please try again.'
    },
    
    // Success messages
    SUCCESS: {
        LOGIN: 'Successfully logged in!',
        LOGOUT: 'Successfully logged out!',
        REGISTER: 'Account created successfully!',
        PROFILE_UPDATE: 'Profile updated successfully!',
        BOOK_ADDED: 'Book added successfully!',
        BOOK_UPDATED: 'Book updated successfully!',
        ORDER_PLACED: 'Order placed successfully!',
        WISHLIST_ADDED: 'Added to wishlist!',
        WISHLIST_REMOVED: 'Removed from wishlist!'
    },
    
    // Validation rules
    VALIDATION: {
        EMAIL: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
        PASSWORD: {
            MIN_LENGTH: 8,
            REQUIRE_UPPERCASE: true,
            REQUIRE_LOWERCASE: true,
            REQUIRE_NUMBERS: true,
            REQUIRE_SPECIAL: false
        },
        PHONE: /^\+?[\d\s\-\(\)]+$/,
        ISBN: /^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$/
    }
};

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
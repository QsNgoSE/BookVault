/**
 * BookVault - Main JavaScript File
 * Handles frontend interactions, API calls, and dynamic content
 */

// Configuration - Updated for Real Backend Integration
const CONFIG = {
    // Microservices URLs
    AUTH_SERVICE_URL: 'http://localhost:8082',
    BOOK_SERVICE_URL: 'http://localhost:8083',
    ORDER_SERVICE_URL: 'http://localhost:8084',
    
    // Legacy API base URL for compatibility
    API_BASE_URL: 'http://localhost:8082',
    
    ENDPOINTS: {
        // Real backend endpoints
        AUTH: {
            LOGIN: '/api/auth/login',
            REGISTER: '/api/auth/register',
            PROFILE: '/api/auth/profile',
            VALIDATE: '/api/auth/validate'
        },
        BOOKS: {
            BASE: '/api/books',
            SEARCH: '/api/books/search',
            CATEGORIES: '/api/books/categories',
            FEATURED: '/api/books/featured',
            BESTSELLERS: '/api/books/bestsellers',
            NEW_RELEASES: '/api/books/new-releases',
            BY_CATEGORY: '/api/books/category',
            BY_AUTHOR: '/api/books/author',
            FILTER: '/api/books/filter'
        }
    }
};

// Utility Functions
const Utils = {
    // Format currency
    formatCurrency: (amount) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        }).format(amount);
    },

    // Format date
    formatDate: (dateString) => {
        return new Date(dateString).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    },

    // Show loading spinner
    showLoading: (elementId) => {
        const element = document.getElementById(elementId);
        if (element) {
            element.innerHTML = '<div class="text-center"><div class="spinner-border" role="status"><span class="visually-hidden">Loading...</span></div></div>';
        }
    },

    // Hide loading spinner
    hideLoading: (elementId) => {
        const element = document.getElementById(elementId);
        if (element) {
            element.innerHTML = '';
        }
    },

    // Show error message
    showError: (message, containerId = 'error-container') => {
        const container = document.getElementById(containerId);
        if (container) {
            container.innerHTML = `
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    ${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            `;
        }
    },

    // Show success message
    showSuccess: (message, containerId = 'success-container') => {
        const container = document.getElementById(containerId);
        if (container) {
            container.innerHTML = `
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    ${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            `;
        }
    }
};

// API Service - Updated for Real Backend Integration
const APIService = {
    // Generic API call method with service-specific URLs
    async makeRequest(endpoint, options = {}, serviceUrl = null) {
        // Determine service URL based on endpoint
        let baseUrl = serviceUrl;
        if (!baseUrl) {
            if (endpoint.includes('/auth/')) {
                baseUrl = CONFIG.AUTH_SERVICE_URL;
            } else if (endpoint.includes('/books/')) {
                baseUrl = CONFIG.BOOK_SERVICE_URL;
            } else {
                baseUrl = CONFIG.API_BASE_URL;
            }
        }
        
        const url = `${baseUrl}${endpoint}`;
        const token = localStorage.getItem('bookvault_auth_token');
        
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
                ...(token && { 'Authorization': `Bearer ${token}` })
            }
        };

        try {
            console.log(`Making API request to: ${url}`);
            const response = await fetch(url, { ...defaultOptions, ...options });
            
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
            }
            
            const data = await response.json();
            console.log('API response:', data);
            
            // Handle our backend's ApiResponse format
            if (data.success !== undefined) {
                return data.success ? data.data : Promise.reject(new Error(data.message));
            }
            
            return data;
        } catch (error) {
            console.error('API request failed:', error);
            throw error;
        }
    },

    // Book-related API calls - Updated for Real Book Service
    books: {
        getAll: (page = 0, size = 12) => APIService.makeRequest(`${CONFIG.ENDPOINTS.BOOKS.BASE}?page=${page}&size=${size}`),
        getById: (id) => APIService.makeRequest(`${CONFIG.ENDPOINTS.BOOKS.BASE}/${id}`),
        search: (query, page = 0, size = 12) => APIService.makeRequest(`${CONFIG.ENDPOINTS.BOOKS.SEARCH}?q=${encodeURIComponent(query)}&page=${page}&size=${size}`),
        getByCategory: (category, page = 0, size = 12) => APIService.makeRequest(`${CONFIG.ENDPOINTS.BOOKS.BY_CATEGORY}/${category}?page=${page}&size=${size}`),
        getFeatured: (page = 0, size = 12) => APIService.makeRequest(`${CONFIG.ENDPOINTS.BOOKS.FEATURED}?page=${page}&size=${size}`),
        getBestsellers: (page = 0, size = 12) => APIService.makeRequest(`${CONFIG.ENDPOINTS.BOOKS.BESTSELLERS}?page=${page}&size=${size}`),
        getNewReleases: (page = 0, size = 12) => APIService.makeRequest(`${CONFIG.ENDPOINTS.BOOKS.NEW_RELEASES}?page=${page}&size=${size}`),
        getCategories: () => APIService.makeRequest(CONFIG.ENDPOINTS.BOOKS.CATEGORIES),
        filter: (filters, page = 0, size = 12) => {
            const params = new URLSearchParams({ page, size, ...filters });
            return APIService.makeRequest(`${CONFIG.ENDPOINTS.BOOKS.FILTER}?${params}`);
        }
    },

    // Authentication API calls - Updated for Real Auth Service
    auth: {
        login: (credentials) => APIService.makeRequest(CONFIG.ENDPOINTS.AUTH.LOGIN, {
            method: 'POST',
            body: JSON.stringify(credentials)
        }),
        register: (userData) => APIService.makeRequest(CONFIG.ENDPOINTS.AUTH.REGISTER, {
            method: 'POST',
            body: JSON.stringify(userData)
        }),
        getProfile: (userId) => APIService.makeRequest(`${CONFIG.ENDPOINTS.AUTH.PROFILE}/${userId}`),
        updateProfile: (userId, data) => APIService.makeRequest(`${CONFIG.ENDPOINTS.AUTH.PROFILE}/${userId}`, {
            method: 'PUT',
            body: JSON.stringify(data)
        }),
        validateToken: (token) => APIService.makeRequest(CONFIG.ENDPOINTS.AUTH.VALIDATE, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` }
        })
    }
};

// Book Management - Updated for Real Backend
const BookManager = {
    // Load books on listing page with pagination support
    async loadBooks(containerId = 'books-container', category = null, page = 0, size = 12) {
        Utils.showLoading(containerId);
        
        try {
            const response = category 
                ? await APIService.books.getByCategory(category, page, size)
                : await APIService.books.getAll(page, size);
            
            // Handle paginated response from backend
            const books = response.content || response;
            this.displayBooks(books, containerId);
            
            // Display pagination if available
            if (response.totalPages && response.totalPages > 1) {
                this.displayPagination(response, containerId + '-pagination', category);
            }
        } catch (error) {
            Utils.showError('Failed to load books. Please try again later.');
            console.error('Error loading books:', error);
        }
    },

    // Display books in grid format - Updated for real data structure
    displayBooks(books, containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;

        if (!books || books.length === 0) {
            container.innerHTML = '<div class="col-12"><p class="text-center">No books found.</p></div>';
            return;
        }

        const booksHTML = books.map(book => `
            <div class="col-md-4 col-sm-6 mb-4">
                <div class="card h-100">
                    <img src="${book.coverImageUrl || '/asset/img/books/placeholder.jpg'}" class="card-img-top" alt="${book.title}" style="height: 300px; object-fit: cover;">
                    <div class="card-body d-flex flex-column">
                        <h5 class="card-title">${book.title}</h5>
                        <p class="card-text text-muted">by ${book.author}</p>
                        <p class="card-text">${Utils.formatCurrency(book.price)}</p>
                        ${book.rating ? `<div class="mb-2">
                            <span class="text-warning">${'★'.repeat(Math.floor(book.rating))}${'☆'.repeat(5-Math.floor(book.rating))}</span>
                            <small class="text-muted">(${book.reviewCount || 0} reviews)</small>
                        </div>` : ''}
                        <div class="mt-auto">
                            <div class="d-flex justify-content-between">
                                <a href="book-details.html?id=${book.id}" class="btn btn-primary">View Details</a>
                                <button class="btn btn-outline-secondary" onclick="BookManager.addToWishlist('${book.id}')">
                                    <i class="bi bi-heart"></i>
                                </button>
                            </div>
                            ${book.stockQuantity <= 0 ? '<small class="text-danger">Out of Stock</small>' : 
                              book.stockQuantity < 10 ? `<small class="text-warning">${book.stockQuantity} left</small>` : ''}
                        </div>
                    </div>
                </div>
            </div>
        `).join('');

        container.innerHTML = booksHTML;
    },

    // Display pagination controls
    displayPagination(response, containerId, category = null) {
        const container = document.getElementById(containerId);
        if (!container) return;

        const { page, totalPages, first, last } = response;
        let paginationHTML = '<nav><ul class="pagination justify-content-center">';
        
        // Previous button
        paginationHTML += `<li class="page-item ${first ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="BookManager.loadBooks('books-container', '${category}', ${page - 1})">Previous</a>
        </li>`;
        
        // Page numbers
        for (let i = Math.max(0, page - 2); i <= Math.min(totalPages - 1, page + 2); i++) {
            paginationHTML += `<li class="page-item ${i === page ? 'active' : ''}">
                <a class="page-link" href="#" onclick="BookManager.loadBooks('books-container', '${category}', ${i})">${i + 1}</a>
            </li>`;
        }
        
        // Next button
        paginationHTML += `<li class="page-item ${last ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="BookManager.loadBooks('books-container', '${category}', ${page + 1})">Next</a>
        </li>`;
        
        paginationHTML += '</ul></nav>';
        container.innerHTML = paginationHTML;
    },

    // Add book to wishlist
    async addToWishlist(bookId) {
        try {
            await APIService.makeRequest(`${CONFIG.ENDPOINTS.USERS}/wishlist`, {
                method: 'POST',
                body: JSON.stringify({ bookId })
            });
            Utils.showSuccess('Book added to wishlist!');
        } catch (error) {
            Utils.showError('Failed to add book to wishlist.');
            console.error('Error adding to wishlist:', error);
        }
    },

    // Search books
    async searchBooks(query, containerId = 'search-results') {
        if (!query.trim()) return;
        
        Utils.showLoading(containerId);
        
        try {
            const books = await APIService.books.search(query);
            this.displayBooks(books, containerId);
        } catch (error) {
            Utils.showError('Search failed. Please try again.');
            console.error('Error searching books:', error);
        }
    }
};

// User Management
const UserManager = {
    // Load user dashboard data
    async loadDashboard() {
        try {
            const [profile, orders, wishlist] = await Promise.all([
                APIService.users.getProfile(),
                APIService.users.getOrders(),
                APIService.users.getWishlist()
            ]);

            this.displayProfile(profile);
            this.displayOrders(orders);
            this.displayWishlist(wishlist);
        } catch (error) {
            Utils.showError('Failed to load dashboard data.');
            console.error('Error loading dashboard:', error);
        }
    },

    // Display user profile
    displayProfile(profile) {
        const container = document.getElementById('user-profile');
        if (!container || !profile) return;

        container.innerHTML = `
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Profile Information</h5>
                    <p><strong>Name:</strong> ${profile.firstName} ${profile.lastName}</p>
                    <p><strong>Email:</strong> ${profile.email}</p>
                    <p><strong>Member since:</strong> ${Utils.formatDate(profile.createdAt)}</p>
                </div>
            </div>
        `;
    },

    // Display user orders
    displayOrders(orders) {
        const container = document.getElementById('user-orders');
        if (!container) return;

        if (!orders || orders.length === 0) {
            container.innerHTML = '<p class="text-muted">No orders found.</p>';
            return;
        }

        const ordersHTML = orders.map(order => `
            <div class="card mb-3">
                <div class="card-body">
                    <h6 class="card-title">Order #${order.id}</h6>
                    <p class="card-text">Date: ${Utils.formatDate(order.createdAt)}</p>
                    <p class="card-text">Total: ${Utils.formatCurrency(order.total)}</p>
                    <p class="card-text">Status: <span class="badge bg-primary">${order.status}</span></p>
                </div>
            </div>
        `).join('');

        container.innerHTML = ordersHTML;
    },

    // Display wishlist
    displayWishlist(wishlist) {
        const container = document.getElementById('user-wishlist');
        if (!container) return;

        if (!wishlist || wishlist.length === 0) {
            container.innerHTML = '<p class="text-muted">No items in wishlist.</p>';
            return;
        }

        BookManager.displayBooks(wishlist, 'user-wishlist');
    }
};

// Authentication Management - Updated for Real Backend
const AuthManager = {
    // Check if user is logged in
    isLoggedIn() {
        return localStorage.getItem('bookvault_auth_token') !== null;
    },

    // Get current user from localStorage
    getCurrentUser() {
        const userProfile = localStorage.getItem('bookvault_user_profile');
        if (userProfile) {
            try {
                return JSON.parse(userProfile);
            } catch (error) {
                console.error('Error parsing user profile:', error);
                return null;
            }
        }
        return null;
    },

    // Handle login form submission
    async handleLogin(event) {
        event.preventDefault();
        
        const form = event.target;
        const email = form.email.value;
        const password = form.password.value;

        try {
            const response = await APIService.auth.login({ email, password });
            
            if (response.token) {
                // Store auth data with proper keys
                localStorage.setItem('bookvault_auth_token', response.token);
                localStorage.setItem('bookvault_user_profile', JSON.stringify({
                    id: response.userId,
                    email: response.email,
                    firstName: response.firstName,
                    lastName: response.lastName,
                    role: response.role
                }));
                
                Utils.showSuccess('Login successful! Redirecting...');
                
                // Redirect based on role
                setTimeout(() => {
                    switch (response.role) {
                        case 'ADMIN':
                            window.location.href = 'admin.html';
                            break;
                        case 'SELLER':
                            window.location.href = 'seller.html';
                            break;
                        default:
                            window.location.href = 'user.html';
                    }
                }, 1500);
            }
        } catch (error) {
            Utils.showError(error.message || 'Login failed. Please check your credentials.');
            console.error('Login error:', error);
        }
    },

    // Handle registration form submission
    async handleRegister(event) {
        event.preventDefault();
        
        const form = event.target;
        const password = form.password.value;
        const confirmPassword = form.confirmPassword?.value;

        // Validation
        if (confirmPassword && password !== confirmPassword) {
            Utils.showError('Passwords do not match.');
            return;
        }

        if (password.length < 6) {
            Utils.showError('Password must be at least 6 characters long.');
            return;
        }

        const userData = {
            firstName: form.firstName.value,
            lastName: form.lastName.value,
            email: form.email.value,
            password: password,
            phone: form.phone?.value || '',
            role: form.role ? form.role.value : 'USER'
        };

        try {
            const response = await APIService.auth.register(userData);
            
            // Auto-login after successful registration
            if (response.token) {
                localStorage.setItem('bookvault_auth_token', response.token);
                localStorage.setItem('bookvault_user_profile', JSON.stringify({
                    id: response.userId,
                    email: response.email,
                    firstName: response.firstName,
                    lastName: response.lastName,
                    role: response.role
                }));
                
                Utils.showSuccess('Registration successful! Redirecting...');
                
                setTimeout(() => {
                    switch (response.role) {
                        case 'ADMIN':
                            window.location.href = 'admin.html';
                            break;
                        case 'SELLER':
                            window.location.href = 'seller.html';
                            break;
                        default:
                            window.location.href = 'user.html';
                    }
                }, 2000);
            }
        } catch (error) {
            Utils.showError(error.message || 'Registration failed. Please try again.');
            console.error('Registration error:', error);
        }
    },

    // Handle logout
    async logout() {
        try {
            // Clear all stored data
            localStorage.removeItem('bookvault_auth_token');
            localStorage.removeItem('bookvault_user_profile');
            localStorage.removeItem('bookvault_cart');
            localStorage.removeItem('bookvault_wishlist');
            
            Utils.showSuccess('Logged out successfully!');
            
            setTimeout(() => {
                window.location.href = 'index.html';
            }, 1000);
        } catch (error) {
            console.error('Logout error:', error);
            // Force logout even if there's an error
            localStorage.clear();
            window.location.href = 'index.html';
        }
    }
};

// Page-specific initialization
const PageManager = {
    // Initialize based on current page
    init() {
        const currentPage = window.location.pathname.split('/').pop();
        
        switch (currentPage) {
            case 'index.html':
            case '':
                this.initHomePage();
                break;
            case 'booklisting.html':
                this.initBookListingPage();
                break;
            case 'book-details.html':
                this.initBookDetailsPage();
                break;
            case 'user.html':
                this.initUserDashboard();
                break;
            case 'login.html':
                this.initLoginPage();
                break;
            case 'register.html':
                this.initRegisterPage();
                break;
        }

        this.initCommonFeatures();
    },

    // Initialize home page
    initHomePage() {
        // Load featured books
        BookManager.loadBooks('featured-books');
        
        // Handle search form
        const searchForm = document.getElementById('search-form');
        if (searchForm) {
            searchForm.addEventListener('submit', (e) => {
                e.preventDefault();
                const query = document.getElementById('search-input').value;
                if (query.trim()) {
                    window.location.href = `booklisting.html?search=${encodeURIComponent(query)}`;
                }
            });
        }
    },

    // Initialize book listing page
    initBookListingPage() {
        const urlParams = new URLSearchParams(window.location.search);
        const searchQuery = urlParams.get('search');
        const category = urlParams.get('category');

        if (searchQuery) {
            BookManager.searchBooks(searchQuery);
        } else if (category) {
            BookManager.loadBooks('books-container', category);
        } else {
            BookManager.loadBooks('books-container');
        }
    },

    // Initialize book details page
    initBookDetailsPage() {
        const urlParams = new URLSearchParams(window.location.search);
        const bookId = urlParams.get('id');
        
        if (bookId) {
            this.loadBookDetails(bookId);
        }
    },

    // Initialize user dashboard
    initUserDashboard() {
        if (!AuthManager.isLoggedIn()) {
            window.location.href = 'login.html';
            return;
        }
        
        UserManager.loadDashboard();
    },

    // Initialize login page
    initLoginPage() {
        const loginForm = document.getElementById('login-form');
        if (loginForm) {
            loginForm.addEventListener('submit', AuthManager.handleLogin);
        }
    },

    // Initialize register page
    initRegisterPage() {
        const registerForm = document.getElementById('register-form');
        if (registerForm) {
            registerForm.addEventListener('submit', AuthManager.handleRegister);
        }
    },

    // Initialize common features
    initCommonFeatures() {
        // Handle logout buttons
        const logoutButtons = document.querySelectorAll('.logout-btn');
        logoutButtons.forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                AuthManager.logout();
            });
        });

        // Update navigation based on authentication status
        this.updateNavigation();
    },

    // Update navigation based on authentication
    updateNavigation() {
        const isLoggedIn = AuthManager.isLoggedIn();
        const userRole = localStorage.getItem('userRole');

        // Show/hide navigation items based on auth status
        const authNavItems = document.querySelectorAll('.auth-nav');
        const guestNavItems = document.querySelectorAll('.guest-nav');

        authNavItems.forEach(item => {
            item.style.display = isLoggedIn ? 'block' : 'none';
        });

        guestNavItems.forEach(item => {
            item.style.display = isLoggedIn ? 'none' : 'block';
        });

        // Show role-specific navigation
        if (isLoggedIn && userRole) {
            const roleNavItems = document.querySelectorAll(`.${userRole.toLowerCase()}-nav`);
            roleNavItems.forEach(item => {
                item.style.display = 'block';
            });
        }
    },

    // Load book details
    async loadBookDetails(bookId) {
        try {
            const book = await APIService.books.getById(bookId);
            this.displayBookDetails(book);
        } catch (error) {
            Utils.showError('Failed to load book details.');
            console.error('Error loading book details:', error);
        }
    },

    // Display book details
    displayBookDetails(book) {
        const container = document.getElementById('book-details');
        if (!container || !book) return;

        container.innerHTML = `
            <div class="row">
                <div class="col-md-4">
                    <img src="${book.coverImage || '/asset/img/books/placeholder.jpg'}" class="img-fluid" alt="${book.title}">
                </div>
                <div class="col-md-8">
                    <h1>${book.title}</h1>
                    <p class="lead">by ${book.author}</p>
                    <p class="h4 text-primary">${Utils.formatCurrency(book.price)}</p>
                    <p>${book.description}</p>
                    <div class="mb-3">
                        <strong>Category:</strong> ${book.category}<br>
                        <strong>ISBN:</strong> ${book.isbn}<br>
                        <strong>Published:</strong> ${Utils.formatDate(book.publishedDate)}
                    </div>
                    <div class="btn-group" role="group">
                        <button class="btn btn-primary btn-lg">Add to Cart</button>
                        <button class="btn btn-outline-secondary" onclick="BookManager.addToWishlist(${book.id})">
                            <i class="bi bi-heart"></i> Wishlist
                        </button>
                    </div>
                </div>
            </div>
        `;
    }
};

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    PageManager.init();
});

// Export for global access
window.BookVault = {
    Utils,
    APIService,
    BookManager,
    UserManager,
    AuthManager,
    PageManager
}; 
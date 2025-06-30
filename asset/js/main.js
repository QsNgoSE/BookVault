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
        },
        ADMIN: {
            USERS: '/api/admin/users',
            SELLERS: '/api/admin/sellers',
            DASHBOARD: '/api/admin/dashboard',
            STATS: '/api/admin/dashboard/stats'
        },
        ORDERS: {
            BASE: '/api/orders',
            USER: '/api/orders/user',
            ADMIN: '/api/admin/orders'
        }
    }
};

// Shopping Cart Management
const CartManager = {
    // Get cart from localStorage
    getCart() {
        const cart = localStorage.getItem('bookvault_cart');
        return cart ? JSON.parse(cart) : [];
    },

    // Save cart to localStorage
    saveCart(cart) {
        localStorage.setItem('bookvault_cart', JSON.stringify(cart));
        this.updateCartUI();
    },

    // Add item to cart
    addToCart(book, quantity = 1) {
        console.log('🛍️ AddToCart called with:', { 
            book: { id: book.id, title: book.title, author: book.author, price: book.price }, 
            quantity 
        });
        
        const cart = this.getCart();
        console.log('📦 Current cart before add:', cart);
        
        const existingItem = cart.find(item => item.id === book.id);
        
        if (existingItem) {
            console.log(`📝 Found existing item: ${existingItem.title}, current qty: ${existingItem.quantity}, adding: ${quantity}`);
            existingItem.quantity += quantity;
            console.log(`📝 New quantity: ${existingItem.quantity}`);
            this.showCartNotification(`Updated quantity to ${existingItem.quantity}!`);
        } else {
            console.log(`🆕 Adding new item to cart`);
            const newItem = {
                id: book.id,
                title: book.title,
                author: book.author,
                price: book.price,
                imageUrl: book.imageUrl || 'asset/img/books/placeholder.jpg',
                quantity: quantity
            };
            console.log('🆕 New item:', newItem);
            cart.push(newItem);
            this.showCartNotification('Item added to cart!');
        }
        
        console.log('📦 Cart after add:', cart);
        this.saveCart(cart);
        this.updateCartUI();
        return true;
    },

    // Remove item from cart
    removeFromCart(bookId) {
        const cart = this.getCart().filter(item => item.id !== bookId);
        this.saveCart(cart);
        this.updateCartUI();
        this.showCartNotification('Item removed from cart!');
        
        // Refresh cart display if currently shown
        const cartContainer = document.getElementById('cart-container');
        if (cartContainer) {
            this.displayCart('cart-container');
        }
        
        const modalCartContainer = document.getElementById('modal-cart-container');
        if (modalCartContainer) {
            this.displayCart('modal-cart-container');
        }
    },

    // Update item quantity
    updateQuantity(bookId, quantity) {
        console.log(`🔄 UpdateQuantity called: item ${bookId}, new quantity ${quantity}`);
        const cart = this.getCart();
        const item = cart.find(item => item.id === bookId);
        
        if (item) {
            console.log(`📝 Found item: ${item.title}, current quantity: ${item.quantity}`);
            if (quantity <= 0) {
                console.log('🗑️ Quantity <= 0, removing item');
                this.removeFromCart(bookId);
            } else {
                item.quantity = quantity;
                this.saveCart(cart);
                this.updateCartUI();
                console.log(`✅ Updated item quantity to ${quantity}`);
                
                // Refresh cart display if currently shown - use setTimeout to avoid race conditions
                setTimeout(() => {
                    const cartContainer = document.getElementById('cart-container');
                    if (cartContainer) {
                        console.log('🔄 Refreshing main cart display');
                        this.displayCart('cart-container');
                    }
                    
                    const modalCartContainer = document.getElementById('modal-cart-container');
                    if (modalCartContainer) {
                        console.log('🔄 Refreshing modal cart display');
                        this.displayCart('modal-cart-container');
                    }
                }, 50);
            }
        } else {
            console.log(`❌ Item ${bookId} not found in cart`);
        }
    },

    // Clear cart
    clearCart() {
        localStorage.removeItem('bookvault_cart');
        this.updateCartUI();
    },

    // Get cart total
    getCartTotal() {
        return this.getCart().reduce((total, item) => total + (item.price * item.quantity), 0);
    },

    // Get cart item count
    getCartItemCount() {
        return this.getCart().reduce((count, item) => count + item.quantity, 0);
    },

    // Update cart UI elements
    updateCartUI() {
        const itemCount = this.getCartItemCount();
        
        // Update specific cart badges by ID (for our enhanced navigation)
        const specificBadges = [
            document.getElementById('cart-badge'),
            document.getElementById('guest-cart-badge')
        ];
        
        specificBadges.forEach(badge => {
            if (badge) {
                if (itemCount > 0) {
                    badge.textContent = itemCount;
                    badge.classList.remove('d-none');
                } else {
                    badge.classList.add('d-none');
                }
            }
        });
        
        // Also handle any cart icons without specific IDs (legacy support)
        const cartIcons = document.querySelectorAll('.cart-icon, .bi-cart');
        cartIcons.forEach(icon => {
            const parent = icon.closest('a') || icon.parentElement;
            let badge = parent.querySelector('.cart-badge:not(#cart-badge):not(#guest-cart-badge)');
            
            if (itemCount > 0) {
                if (!badge) {
                    badge = document.createElement('span');
                    badge.className = 'cart-badge badge bg-warning text-dark';
                    badge.style.cssText = 'position: absolute; top: -8px; right: -8px; min-width: 18px; height: 18px; border-radius: 50%; font-size: 11px; line-height: 18px;';
                    parent.style.position = 'relative';
                    parent.appendChild(badge);
                }
                badge.textContent = itemCount;
            } else if (badge) {
                badge.remove();
            }
        });
        
        console.log('🛒 Cart UI updated - Item count:', itemCount);
    },

    // Show cart notification
    showCartNotification(message) {
        const notification = document.createElement('div');
        notification.className = 'alert alert-success position-fixed';
        notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 250px;';
        notification.innerHTML = `
            <i class="bi bi-check-circle me-2"></i>${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        document.body.appendChild(notification);
        setTimeout(() => notification.remove(), 3000);
    },

    // Display cart contents
    displayCart(containerId) {
        console.log(`📊 Displaying cart in container: ${containerId}`);
        const container = document.getElementById(containerId);
        if (!container) {
            console.error(`❌ Container ${containerId} not found!`);
            return;
        }

        const cart = this.getCart();
        console.log(`🛍️ Cart has ${cart.length} items:`, cart);
        
        if (cart.length === 0) {
            container.innerHTML = `
                <div class="text-center py-5">
                    <i class="bi bi-cart-x display-4 text-muted mb-3"></i>
                    <h4>Your cart is empty</h4>
                    <p class="text-muted">Add some books to get started!</p>
                    <a href="booklisting.html" class="btn bookvault-btn">Browse Books</a>
                </div>
            `;
            return;
        }

        const cartHTML = cart.map(item => `
            <div class="cart-item d-flex align-items-center gap-3 mb-3 p-3 border rounded" data-item-id="${item.id}">
                <img src="${item.imageUrl}" alt="${item.title}" style="width: 60px; height: 80px; object-fit: cover;" class="rounded">
                <div class="flex-grow-1">
                    <h6 class="mb-1">${item.title}</h6>
                    <p class="text-muted mb-1 small">${item.author}</p>
                    <div class="d-flex align-items-center gap-2">
                        <button class="btn btn-sm btn-outline-secondary decrease-qty" data-id="${item.id}" data-current="${item.quantity}">-</button>
                        <span class="mx-2">${item.quantity}</span>
                        <button class="btn btn-sm btn-outline-secondary increase-qty" data-id="${item.id}" data-current="${item.quantity}">+</button>
                    </div>
                </div>
                <div class="text-end">
                    <div class="fw-bold">${Utils.formatCurrency(item.price * item.quantity)}</div>
                    <button class="btn btn-sm btn-outline-danger mt-1 remove-item" data-id="${item.id}">Remove</button>
                </div>
            </div>
        `).join('');

        const total = this.getCartTotal();
        
        container.innerHTML = `
            <div class="cart-items">
                ${cartHTML}
            </div>
            <div class="cart-summary mt-4 p-3 bg-light rounded">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h5 class="mb-0">Total: ${Utils.formatCurrency(total)}</h5>
                </div>
                <div class="d-grid gap-2">
                    <button class="btn bookvault-btn btn-lg checkout-btn">
                        <i class="bi bi-credit-card me-2"></i>Proceed to Checkout
                    </button>
                    <button class="btn btn-outline-secondary clear-cart-btn">Clear Cart</button>
                </div>
            </div>
        `;

        // Add event listeners for cart interactions
        this.attachCartEventListeners(container);
    },

    // Attach event listeners to cart buttons
    attachCartEventListeners(container) {
        console.log('🔗 Attaching cart event listeners...');
        
        // Quantity decrease buttons
        const decreaseBtns = container.querySelectorAll('.decrease-qty');
        console.log(`Found ${decreaseBtns.length} decrease buttons`);
        decreaseBtns.forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                console.log('Decrease button clicked!');
                const itemId = btn.dataset.id;
                console.log('🆔 Item ID from button:', itemId);
                
                // Get current quantity from cart data, not from stale data attribute
                const cart = this.getCart();
                const item = cart.find(item => item.id === itemId);
                const currentQty = item ? item.quantity : 0;
                
                console.log(`Decreasing quantity for item ${itemId} from ${currentQty} to ${currentQty - 1}`);
                this.updateQuantity(itemId, currentQty - 1);
            });
        });

        // Quantity increase buttons
        const increaseBtns = container.querySelectorAll('.increase-qty');
        console.log(`Found ${increaseBtns.length} increase buttons`);
        increaseBtns.forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                console.log('Increase button clicked!');
                const itemId = btn.dataset.id; // Keep as string, don't use parseInt for UUIDs
                console.log('🆔 Item ID from button:', itemId);
                
                // Get current quantity from cart data, not from stale data attribute
                const cart = this.getCart();
                const item = cart.find(item => item.id === itemId);
                const currentQty = item ? item.quantity : 0;
                
                console.log(`Increasing quantity for item ${itemId} from ${currentQty} to ${currentQty + 1}`);
                this.updateQuantity(itemId, currentQty + 1);
            });
        });

        // Remove item buttons
        container.querySelectorAll('.remove-item').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                const itemId = btn.dataset.id; // Keep as string, don't use parseInt for UUIDs
                console.log('🗑️ Remove button clicked for item ID:', itemId);
                this.removeFromCart(itemId);
            });
        });

        // Checkout button
        const checkoutBtn = container.querySelector('.checkout-btn');
        if (checkoutBtn) {
            checkoutBtn.addEventListener('click', (e) => {
                e.preventDefault();
                CheckoutManager.startCheckout();
            });
        }

        // Clear cart button
        const clearBtn = container.querySelector('.clear-cart-btn');
        if (clearBtn) {
            clearBtn.addEventListener('click', (e) => {
                e.preventDefault();
                if (confirm('Are you sure you want to clear your cart?')) {
                    this.clearCart();
                }
            });
        }
    }
};

// Checkout and Order Management
const CheckoutManager = {
    // Start checkout process
    startCheckout() {
        const cart = CartManager.getCart();
        if (cart.length === 0) {
            Utils.showError('Your cart is empty!');
            return;
        }

        if (!AuthManager.isLoggedIn()) {
            if (confirm('You need to be logged in to checkout. Would you like to login now?')) {
                window.location.href = 'login.html';
            }
            return;
        }

        this.showCheckoutModal();
    },

    // Show checkout modal
    showCheckoutModal() {
        const cart = CartManager.getCart();
        const total = CartManager.getCartTotal();
        
        const modal = document.createElement('div');
        modal.className = 'modal fade';
        modal.innerHTML = `
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Checkout</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="checkout-form">
                            <div class="row">
                                <div class="col-md-8">
                                    <h6>Shipping Information</h6>
                                    <div class="row">
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">First Name</label>
                                            <input type="text" class="form-control" name="firstName" required>
                                        </div>
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">Last Name</label>
                                            <input type="text" class="form-control" name="lastName" required>
                                        </div>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Address</label>
                                        <input type="text" class="form-control" name="address" required>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">City</label>
                                            <input type="text" class="form-control" name="city" required>
                                        </div>
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">ZIP Code</label>
                                            <input type="text" class="form-control" name="zipCode" required>
                                        </div>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Country</label>
                                        <select class="form-select" name="country" required>
                                            <option value="United States" selected>United States</option>
                                            <option value="Canada">Canada</option>
                                            <option value="United Kingdom">United Kingdom</option>
                                            <option value="Australia">Australia</option>
                                            <option value="Germany">Germany</option>
                                            <option value="France">France</option>
                                            <option value="Other">Other</option>
                                        </select>
                                    </div>
                                    <h6 class="mt-4">Payment Information</h6>
                                    <div class="mb-3">
                                        <label class="form-label">Payment Method</label>
                                        <select class="form-select" name="paymentMethod" required>
                                            <option value="CREDIT_CARD" selected>Credit Card</option>
                                            <option value="DEBIT_CARD">Debit Card</option>
                                            <option value="PAYPAL">PayPal</option>
                                            <option value="BANK_TRANSFER">Bank Transfer</option>
                                            <option value="CASH_ON_DELIVERY">Cash on Delivery</option>
                                        </select>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Card Number</label>
                                        <input type="text" class="form-control" name="cardNumber" placeholder="1234 5678 9012 3456" required>
                                    </div>
                                    <div class="row">
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">Expiry Date</label>
                                            <input type="text" class="form-control" name="expiryDate" placeholder="MM/YY" required>
                                        </div>
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label">CVV</label>
                                            <input type="text" class="form-control" name="cvv" placeholder="123" required>
                                        </div>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Special Instructions (Optional)</label>
                                        <textarea class="form-control" name="notes" rows="2" placeholder="Any special delivery instructions..."></textarea>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <h6>Order Summary</h6>
                                    <div class="order-summary bg-light p-3 rounded">
                                        ${cart.map(item => `
                                            <div class="d-flex justify-content-between mb-2">
                                                <span class="small">${item.title} (x${item.quantity})</span>
                                                <span class="small">${Utils.formatCurrency(item.price * item.quantity)}</span>
                                            </div>
                                        `).join('')}
                                        <hr>
                                        <div class="d-flex justify-content-between fw-bold">
                                            <span>Total:</span>
                                            <span>${Utils.formatCurrency(total)}</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn bookvault-btn" onclick="CheckoutManager.processOrder()">
                            <i class="bi bi-credit-card me-2"></i>Place Order
                        </button>
                    </div>
                </div>
            </div>
        `;
        
        document.body.appendChild(modal);
        const bsModal = new bootstrap.Modal(modal);
        bsModal.show();
        
        modal.addEventListener('hidden.bs.modal', () => modal.remove());
    },

    // Process order (real backend implementation)
    async processOrder() {
        const form = document.getElementById('checkout-form');
        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        if (!AuthManager.isLoggedIn()) {
            Utils.showError('Please log in to place an order.');
            window.location.href = 'login.html';
            return;
        }

        const formData = new FormData(form);
        const cart = CartManager.getCart();
        
        if (cart.length === 0) {
            Utils.showError('Your cart is empty!');
            return;
        }
        
        // Show loading state
        const button = event.target;
        const originalText = button.innerHTML;
        button.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Processing...';
        button.disabled = true;

        try {
            // Create order data matching backend CreateOrderRequest format
            const orderData = {
                items: cart.map(item => ({
                    bookId: item.id,
                    quantity: item.quantity,
                    unitPrice: item.price
                })),
                shippingAddress: {
                    fullName: `${formData.get('firstName')} ${formData.get('lastName')}`,
                    addressLine1: formData.get('address'),
                    city: formData.get('city'),
                    postalCode: formData.get('zipCode'),
                    country: formData.get('country') || 'United States'
                },
                paymentMethod: formData.get('paymentMethod') || 'CREDIT_CARD',
                notes: formData.get('notes') || ''
            };

            // Submit order to backend
            const response = await APIService.order.create(orderData);

            // Clear cart
            CartManager.clearCart();
            
            // Close modal and show success
            bootstrap.Modal.getInstance(document.querySelector('.modal')).hide();
            this.showOrderConfirmation(response);
            
        } catch (error) {
            console.error('Order processing failed:', error);
            Utils.showError(error.message || 'Order processing failed. Please try again.');
        } finally {
            button.innerHTML = originalText;
            button.disabled = false;
        }
    },

    // Show order confirmation
    showOrderConfirmation(orderData) {
        const orderId = orderData.id || orderData.orderId || 'N/A';
        const modal = document.createElement('div');
        modal.className = 'modal fade';
        modal.innerHTML = `
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-success text-white">
                        <h5 class="modal-title"><i class="bi bi-check-circle me-2"></i>Order Confirmed!</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body text-center">
                        <div class="mb-4">
                            <i class="bi bi-check-circle-fill text-success display-4"></i>
                        </div>
                        <h4>Thank you for your order!</h4>
                        <p class="text-muted">Order ID: <strong>${orderId}</strong></p>
                        <p>You will receive a confirmation email shortly.</p>
                        <div class="alert alert-info">
                            <i class="bi bi-info-circle me-2"></i>
                            Your books will be delivered within 3-5 business days.
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        <a href="user.html" class="btn bookvault-btn">View My Orders</a>
                    </div>
                </div>
            </div>
        `;
        
        document.body.appendChild(modal);
        const bsModal = new bootstrap.Modal(modal);
        bsModal.show();
        
        modal.addEventListener('hidden.bs.modal', () => modal.remove());
    }
};

// Admin Management System
const AdminManager = {
        // Load admin dashboard with real backend data
    async loadAdminDashboard() {
        if (!this.isAdmin()) {
            Utils.showError('Access denied. Admin privileges required.');
            return;
        }
        
        try {
            // Update admin name in the welcome badge
            const currentUser = AuthManager.getCurrentUser();
            const adminNameElement = document.querySelector('#admin-name');
            if (adminNameElement && currentUser) {
                adminNameElement.textContent = currentUser.firstName || currentUser.name || 'Administrator';
            }
            
            // Load dashboard statistics
            const stats = await APIService.admin.getDashboardStats();
            this.displayDashboardStats(stats);
            
            // Load users
            const users = await APIService.admin.getUsers();
            this.displayUsers(users.content || users);
            
            // Load sellers
            const sellers = await APIService.admin.getSellers();
            this.displaySellers(sellers.content || sellers);
            
            // Load books
            const books = await APIService.books.getAll();
            this.displayBooks(books.content || books);
            
            // Load orders if orders tab exists
            const ordersContainer = document.querySelector('#orders-tbody');
            if (ordersContainer) {
                const orders = await APIService.order.admin.getAll();
                this.displayOrders(orders.content || orders);
            }
            
        } catch (error) {
            console.error('Failed to load admin dashboard:', error);
            Utils.showError('Failed to load admin dashboard. Please try again.');
        }
    },

    // Display dashboard statistics
    displayDashboardStats(stats) {
        // Update stats cards if they exist
        const totalUsersCard = document.querySelector('.stats-card .total-users');
        const totalSellersCard = document.querySelector('.stats-card .total-sellers');
        const totalBooksCard = document.querySelector('.stats-card .total-books');
        const totalOrdersCard = document.querySelector('.stats-card .total-orders');
        
        if (totalUsersCard) totalUsersCard.textContent = stats.totalUsers || 0;
        if (totalSellersCard) totalSellersCard.textContent = stats.totalSellers || 0;
        if (totalBooksCard) totalBooksCard.textContent = stats.totalBooks || 0;
        if (totalOrdersCard) totalOrdersCard.textContent = stats.totalOrders || 0;
    },

    // Check if current user is admin
    isAdmin() {
        const userRole = localStorage.getItem('bookvault_user_role');
        return userRole === 'ADMIN';
    },



    // Display users in admin panel
    displayUsers(users) {
        const container = document.querySelector('#users-tbody');
        if (!container) {
            console.error('Users table body not found');
            return;
        }
        console.log('📊 Displaying users:', users);

        container.innerHTML = users.map(user => `
            <tr data-user-id="${user.id}">
                <td>
                    <div class="d-flex align-items-center">
                        <div class="user-avatar me-2">
                            <i class="bi bi-person-circle fs-4 text-muted"></i>
                        </div>
                        <div>
                            <div class="fw-medium">${user.firstName || user.name || 'N/A'} ${user.lastName || ''}</div>
                            <small class="text-muted">ID: ${user.id}</small>
                        </div>
                    </div>
                </td>
                <td>${user.email}</td>
                <td>
                    <span class="badge bg-${user.role === 'ADMIN' ? 'danger' : user.role === 'SELLER' ? 'warning' : 'primary'}">
                        ${user.role || 'USER'}
                    </span>
                </td>
                <td>
                    <small class="text-muted">${user.createdDate || user.joinDate || 'N/A'}</small>
                </td>
                <td>
                    <span class="badge bg-${user.isActive ? 'success' : 'danger'}">
                        ${user.isActive ? 'Active' : 'Suspended'}
                    </span>
                </td>
                <td>
                    <div class="btn-group" role="group">
                        <button class="btn btn-sm btn-${user.isActive ? 'warning' : 'success'}" 
                                onclick="AdminManager.toggleUserStatus('${user.id}', ${user.isActive})"
                                title="${user.isActive ? 'Suspend User' : 'Activate User'}">
                            <i class="bi bi-${user.isActive ? 'pause-circle' : 'check-circle'}"></i>
                        </button>
                        <button class="btn btn-sm btn-info" 
                                onclick="AdminManager.resetUserPassword('${user.id}')"
                                title="Reset Password">
                            <i class="bi bi-key"></i>
                        </button>
                        <button class="btn btn-sm btn-danger" 
                                onclick="AdminManager.deleteUser('${user.id}')"
                                title="Delete User">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    },

    // Display sellers in admin panel
    displaySellers(sellers) {
        const container = document.querySelector('#sellers-tbody');
        if (!container) {
            console.error('Sellers table body not found');
            return;
        }
        console.log('📊 Displaying sellers:', sellers);

        container.innerHTML = sellers.map(seller => `
            <tr data-seller-id="${seller.id}">
                <td>
                    <div class="d-flex align-items-center">
                        <div class="seller-avatar me-2">
                            <i class="bi bi-shop fs-4 text-muted"></i>
                        </div>
                        <div>
                            <div class="fw-medium">${seller.firstName || seller.name || 'N/A'} ${seller.lastName || ''}</div>
                            <small class="text-muted">ID: ${seller.id}</small>
                        </div>
                    </div>
                </td>
                <td>${seller.email}</td>
                <td>
                    <span class="fw-medium text-primary">${seller.storeName || 'N/A'}</span>
                </td>
                <td>
                    <small class="text-muted">${seller.createdDate || seller.joinDate || 'N/A'}</small>
                </td>
                <td>
                    <span class="badge bg-${seller.isActive ? 'success' : 'warning'}">
                        ${seller.isActive ? 'Active' : 'Paused'}
                    </span>
                </td>
                <td>
                    <div class="btn-group" role="group">
                        <button class="btn btn-sm btn-${seller.isActive ? 'warning' : 'success'}" 
                                onclick="AdminManager.toggleSellerStatus('${seller.id}')"
                                title="${seller.isActive ? 'Pause Seller' : 'Activate Seller'}">
                            <i class="bi bi-${seller.isActive ? 'pause-circle' : 'play-circle'}"></i>
                        </button>
                        <button class="btn btn-sm btn-danger" 
                                onclick="AdminManager.deleteSeller('${seller.id}')"
                                title="Delete Seller">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    },

    // Display books in admin panel
    displayBooks(books) {
        const container = document.querySelector('#products-tbody');
        if (!container) {
            console.error('Products table body not found');
            return;
        }
        console.log('📊 Displaying books:', books);

        container.innerHTML = books.map(book => `
            <tr data-book-id="${book.id}">
                <td>
                    <img src="${book.imageUrl || 'asset/img/books/placeholder.jpg'}" 
                         style="width: 44px; height: 60px; object-fit: cover; border-radius: 4px;" 
                         alt="${book.title}"
                         class="shadow-sm">
                </td>
                <td>
                    <div>
                        <div class="fw-medium">${book.title}</div>
                        <small class="text-muted">ID: ${book.id}</small>
                    </div>
                </td>
                <td>
                    <span class="text-muted">${book.author || 'N/A'}</span>
                </td>
                <td>
                    <span class="badge bg-light text-dark">${book.category || book.genre || 'N/A'}</span>
                </td>
                <td>
                    <span class="fw-medium text-success">${Utils.formatCurrency(book.price)}</span>
                </td>
                <td>
                    <span class="badge bg-${book.isActive ? 'success' : 'warning'}">
                        ${book.isActive ? 'Active' : 'Inactive'}
                    </span>
                </td>
                <td>
                    <div class="btn-group" role="group">
                        <button class="btn btn-sm btn-warning" 
                                onclick="AdminManager.editBook('${book.id}')"
                                title="Edit Book">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-sm btn-danger" 
                                onclick="AdminManager.deleteBook('${book.id}')"
                                title="Delete Book">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    },

    // Toggle user status (suspend/activate)
    async toggleUserStatus(userId, currentStatus) {
        try {
            const action = currentStatus ? 'SUSPEND' : 'ACTIVATE';
            await APIService.admin.updateUserStatus(userId, action);
            
            // Reload dashboard data
            this.loadAdminDashboard();
            
            Utils.showSuccess(`User has been ${action.toLowerCase()}d successfully.`);
        } catch (error) {
            console.error('Error updating user status:', error);
            Utils.showError('Failed to update user status.');
        }
    },

    // Reset user password
    async resetUserPassword(userId) {
        if (confirm('Reset password for this user?')) {
            try {
                await APIService.admin.resetUserPassword(userId);
                Utils.showSuccess('Password reset email sent successfully.');
            } catch (error) {
                console.error('Error resetting password:', error);
                Utils.showError('Failed to reset password.');
            }
        }
    },

    // Delete user
    async deleteUser(userId) {
        if (confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
            try {
                await APIService.admin.deleteUser(userId);
                
                // Reload dashboard data
                this.loadAdminDashboard();
                
                Utils.showSuccess('User deleted successfully.');
            } catch (error) {
                console.error('Error deleting user:', error);
                Utils.showError('Failed to delete user.');
            }
        }
    },

    // Toggle seller status
    async toggleSellerStatus(sellerId) {
        try {
            await APIService.admin.toggleSellerStatus(sellerId);
            this.loadAdminDashboard();
            Utils.showSuccess('Seller status updated successfully.');
        } catch (error) {
            console.error('Error updating seller status:', error);
            Utils.showError('Failed to update seller status.');
        }
    },

    // Delete seller
    async deleteSeller(sellerId) {
        if (confirm('Are you sure you want to delete this seller?')) {
            try {
                await APIService.admin.deleteSeller(sellerId);
                this.loadAdminDashboard();
                Utils.showSuccess('Seller deleted successfully.');
            } catch (error) {
                console.error('Error deleting seller:', error);
                Utils.showError('Failed to delete seller.');
            }
        }
    },

    // Edit book
    async editBook(bookId) {
        // For now, show an alert - you can implement a full edit modal later
        Utils.showInfo(`Edit functionality for book ID: ${bookId} will be available in a future update.`);
        
        // TODO: Implement book editing modal
        // This could open a modal with form fields to edit book details
        // and call APIService.books.update(bookId, updatedData)
    },

    // Delete book
    async deleteBook(bookId) {
        if (confirm('Are you sure you want to delete this book?')) {
            try {
                await APIService.admin.deleteBook(bookId);
                this.loadAdminDashboard();
                Utils.showSuccess('Book deleted successfully.');
            } catch (error) {
                console.error('Error deleting book:', error);
                Utils.showError('Failed to delete book.');
            }
        }
    },

    // Display orders in admin panel
    displayOrders(orders) {
        const container = document.querySelector('#orders-tbody');
        if (!container) {
            console.error('Orders table body not found');
            return;
        }
        console.log('📊 Displaying orders:', orders);

        if (!orders || orders.length === 0) {
            container.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center py-4">
                        <i class="bi bi-box display-4 text-muted mb-3"></i>
                        <p class="text-muted">No orders found</p>
                    </td>
                </tr>
            `;
            return;
        }

        container.innerHTML = orders.map(order => {
            const statusColor = this.getOrderStatusColor(order.status);
            const customerName = order.customerFullName || order.shippingDetails?.fullName || 'Unknown Customer';
            const itemCount = order.orderItems?.length || order.itemCount || 0;
            
            return `
                <tr data-order-id="${order.id}">
                    <td>
                        <div>
                            <span class="fw-medium">#${order.id}</span>
                            ${order.orderNumber ? `<br><small class="text-muted">${order.orderNumber}</small>` : ''}
                        </div>
                    </td>
                    <td>
                        <div>
                            <div class="fw-medium">${customerName}</div>
                            ${order.customerEmail || order.userEmail ? `<small class="text-muted">${order.customerEmail || order.userEmail}</small>` : ''}
                        </div>
                    </td>
                    <td>
                        <span class="badge bg-light text-dark">${itemCount} item${itemCount !== 1 ? 's' : ''}</span>
                    </td>
                    <td>
                        <span class="fw-medium text-success">${Utils.formatCurrency(order.totalAmount || order.total || 0)}</span>
                    </td>
                    <td>
                        <small class="text-muted">${new Date(order.createdDate || order.orderDate).toLocaleDateString()}</small>
                    </td>
                    <td>
                        <span class="badge bg-${statusColor}">${order.status || 'PENDING'}</span>
                    </td>
                    <td>
                        <div class="d-flex gap-1 align-items-center">
                            <select class="form-select form-select-sm" style="width: 120px;" 
                                    onchange="AdminManager.updateOrderStatus('${order.id}', this.value)">
                                <option value="PENDING" ${order.status === 'PENDING' ? 'selected' : ''}>Pending</option>
                                <option value="CONFIRMED" ${order.status === 'CONFIRMED' ? 'selected' : ''}>Confirmed</option>
                                <option value="PROCESSING" ${order.status === 'PROCESSING' ? 'selected' : ''}>Processing</option>
                                <option value="SHIPPED" ${order.status === 'SHIPPED' ? 'selected' : ''}>Shipped</option>
                                <option value="OUT_FOR_DELIVERY" ${order.status === 'OUT_FOR_DELIVERY' ? 'selected' : ''}>Out for Delivery</option>
                                <option value="DELIVERED" ${order.status === 'DELIVERED' ? 'selected' : ''}>Delivered</option>
                                <option value="CANCELLED" ${order.status === 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                                <option value="REFUNDED" ${order.status === 'REFUNDED' ? 'selected' : ''}>Refunded</option>
                            </select>
                            <button class="btn btn-sm btn-info" 
                                    onclick="AdminManager.viewOrderDetails('${order.id}')"
                                    title="View Details">
                                <i class="bi bi-eye"></i>
                            </button>
                            <button class="btn btn-sm btn-primary" 
                                    onclick="AdminManager.trackOrder('${order.id}')"
                                    title="Track Order">
                                <i class="bi bi-geo-alt"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        }).join('');
    },

    // Get order status badge color
    getOrderStatusColor(status) {
        switch (status?.toUpperCase()) {
            case 'DELIVERED': return 'success';
            case 'PROCESSING': case 'CONFIRMED': case 'SHIPPED': case 'OUT_FOR_DELIVERY': return 'warning';
            case 'CANCELLED': case 'REFUNDED': return 'danger';
            case 'PENDING': return 'secondary';
            default: return 'secondary';
        }
    },

    // Update order status
    async updateOrderStatus(orderId, newStatus) {
        try {
            await APIService.order.admin.updateStatus(orderId, newStatus);
            Utils.showSuccess(`Order status updated to ${newStatus.replace('_', ' ').toLowerCase()}.`);
            this.loadAdminDashboard();
        } catch (error) {
            console.error('Error updating order status:', error);
            Utils.showError('Failed to update order status.');
        }
    },

    // View order details in admin panel
    async viewOrderDetails(orderId) {
        try {
            const order = await APIService.order.getById(orderId);
            const statusColor = this.getOrderStatusColor(order.status);
            const orderItems = order.orderItems || [];
            
            // Show order details in modal
            const modal = document.createElement('div');
            modal.className = 'modal fade';
            modal.innerHTML = `
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Order #${order.id} - Admin View</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="row mb-3">
                                <div class="col-md-4">
                                    <strong>Customer:</strong> ${order.customerFullName}
                                </div>
                                <div class="col-md-4">
                                    <strong>Order Date:</strong> ${new Date(order.orderDate).toLocaleDateString()}
                                </div>
                                <div class="col-md-4">
                                    <strong>Status:</strong> <span class="badge bg-${statusColor}">${order.status}</span>
                                </div>
                            </div>
                            ${order.trackingNumber ? `
                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <strong>Tracking Number:</strong> ${order.trackingNumber}
                                    </div>
                                    <div class="col-md-6">
                                        <strong>Payment Method:</strong> ${order.paymentMethod}
                                    </div>
                                </div>
                            ` : ''}
                            <h6>Items:</h6>
                            <div class="order-items">
                                ${orderItems.map(item => `
                                    <div class="d-flex align-items-center gap-3 mb-3 p-3 border rounded">
                                        <img src="${item.bookImageUrl || '/asset/img/books/placeholder.jpg'}" style="width: 60px; height: 80px; object-fit: cover;" class="rounded">
                                        <div class="flex-grow-1">
                                            <h6 class="mb-1">${item.bookTitle}</h6>
                                            <p class="text-muted mb-1 small">${item.bookAuthor || 'Unknown Author'}</p>
                                            <div>Quantity: ${item.quantity}</div>
                                            <div class="small text-muted">Unit Price: ${Utils.formatCurrency(item.unitPrice)}</div>
                                        </div>
                                        <div class="text-end">
                                            <div class="fw-bold">${Utils.formatCurrency(item.totalPrice)}</div>
                                        </div>
                                    </div>
                                `).join('')}
                            </div>
                            ${order.shippingAddress ? `
                                <h6 class="mt-4">Shipping Address:</h6>
                                <div class="p-3 border rounded bg-light">
                                    <div>${order.shippingAddress.fullName}</div>
                                    <div>${order.shippingAddress.addressLine1}</div>
                                    ${order.shippingAddress.addressLine2 ? `<div>${order.shippingAddress.addressLine2}</div>` : ''}
                                    <div>${order.shippingAddress.city}, ${order.shippingAddress.postalCode}</div>
                                    <div>${order.shippingAddress.country}</div>
                                </div>
                            ` : ''}
                            <div class="text-end mt-3">
                                <h5>Total: ${Utils.formatCurrency(order.totalAmount)}</h5>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                            <select class="form-select me-2" style="width: auto;" onchange="AdminManager.updateOrderStatus('${order.id}', this.value)">
                                <option value="">Update Status...</option>
                                <option value="CONFIRMED">Confirm Order</option>
                                <option value="PROCESSING">Mark Processing</option>
                                <option value="SHIPPED">Mark Shipped</option>
                                <option value="OUT_FOR_DELIVERY">Out for Delivery</option>
                                <option value="DELIVERED">Mark Delivered</option>
                                <option value="CANCELLED">Cancel Order</option>
                                <option value="REFUNDED">Process Refund</option>
                            </select>
                        </div>
                    </div>
                </div>
            `;
            
            document.body.appendChild(modal);
            const bsModal = new bootstrap.Modal(modal);
            bsModal.show();
            
            modal.addEventListener('hidden.bs.modal', () => modal.remove());
            
        } catch (error) {
            console.error('Error loading order details:', error);
            Utils.showError('Failed to load order details.');
        }
    },

    // Track order
    async trackOrder(orderId) {
        try {
            const tracking = await APIService.order.track(orderId);
            
            const modal = document.createElement('div');
            modal.className = 'modal fade';
            modal.innerHTML = `
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Order Tracking #${orderId}</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="tracking-info">
                                <h6>Current Status: <span class="badge bg-warning">${tracking.currentStatus}</span></h6>
                                ${tracking.trackingNumber ? `<p><strong>Tracking Number:</strong> ${tracking.trackingNumber}</p>` : ''}
                                ${tracking.estimatedDelivery ? `<p><strong>Estimated Delivery:</strong> ${new Date(tracking.estimatedDelivery).toLocaleDateString()}</p>` : ''}
                                
                                <h6 class="mt-3">Tracking History:</h6>
                                <div class="tracking-timeline">
                                    ${tracking.events?.map(event => `
                                        <div class="d-flex mb-2">
                                            <div class="me-3">
                                                <i class="bi bi-circle-fill text-${event.completed ? 'success' : 'muted'}"></i>
                                            </div>
                                            <div>
                                                <div class="fw-bold">${event.status}</div>
                                                <div class="small text-muted">${new Date(event.timestamp).toLocaleString()}</div>
                                                ${event.location ? `<div class="small">${event.location}</div>` : ''}
                                            </div>
                                        </div>
                                    `).join('') || '<p class="text-muted">No tracking events available.</p>'}
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        </div>
                    </div>
                </div>
            `;
            
            document.body.appendChild(modal);
            const bsModal = new bootstrap.Modal(modal);
            bsModal.show();
            
            modal.addEventListener('hidden.bs.modal', () => modal.remove());
            
        } catch (error) {
            console.error('Error loading tracking info:', error);
            Utils.showError('Failed to load tracking information.');
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
            element.innerHTML = '<div class="text-center"><div class="spinner-border text-warning" role="status"><span class="visually-hidden">Loading...</span></div><p class="mt-2">Loading books...</p></div>';
        }
    },

    // Hide loading spinner
    hideLoading: (elementId) => {
        const element = document.getElementById(elementId);
        if (element) {
            element.innerHTML = '';
        }
    },

    // Show error message - IMPROVED VERSION
    showError: (message, options = {}) => {
        const { 
            containerId = null, 
            formId = null, 
            fieldId = null,
            position = 'top-right',
            title = null,
            type = 'error'
        } = options;

        // Handle form-specific errors
        if (formId) {
            const form = document.getElementById(formId);
            if (form) {
                // Remove existing form errors
                const existingErrors = form.querySelectorAll('.auth-error-message');
                existingErrors.forEach(error => error.remove());
                
                // Create form error message
                const errorDiv = document.createElement('div');
                errorDiv.className = 'auth-error-message alert alert-danger alert-dismissible fade show mb-3';
                errorDiv.innerHTML = `
                    <i class="bi bi-exclamation-triangle me-2"></i>
                    ${title ? `<strong>${title}</strong><br>` : ''}
                    ${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                `;
                
                form.insertBefore(errorDiv, form.firstChild);
                
                // Auto-dismiss after 5 seconds
                setTimeout(() => {
                    if (errorDiv.parentNode) {
                        errorDiv.remove();
                    }
                }, 5000);
                return;
            }
        }

        // Handle field-specific errors
        if (fieldId) {
            const field = document.getElementById(fieldId);
            if (field) {
                // Add error styling to field
                field.classList.add('is-invalid');
                
                // Remove existing field error
                const existingError = field.parentNode.querySelector('.invalid-feedback');
                if (existingError) existingError.remove();
                
                // Add error message below field
                const errorDiv = document.createElement('div');
                errorDiv.className = 'invalid-feedback';
                errorDiv.textContent = message;
                field.parentNode.appendChild(errorDiv);
                
                // Clear error styling when user starts typing
                field.addEventListener('input', function clearError() {
                    field.classList.remove('is-invalid');
                    const feedback = field.parentNode.querySelector('.invalid-feedback');
                    if (feedback) feedback.remove();
                    field.removeEventListener('input', clearError);
                }, { once: true });
                return;
            }
        }

        // Handle container-specific errors
        if (containerId) {
            const container = document.getElementById(containerId);
            if (container) {
                container.innerHTML = `
                    <div class="col-12">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <h4 class="alert-heading"><i class="bi bi-exclamation-triangle"></i> ${title || 'Error'}</h4>
                            <p>${message}</p>
                            <hr>
                            <button class="btn btn-warning" onclick="window.location.reload()">Try Again</button>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </div>
                `;
                return;
            }
        }

        // Default: Show floating notification
        const alert = document.createElement('div');
        alert.className = `alert alert-${type === 'error' ? 'danger' : type} alert-dismissible fade show position-fixed`;
        
        let positionStyles = 'z-index: 9999; min-width: 300px; max-width: 500px;';
        switch (position) {
            case 'top-right':
                positionStyles += 'top: 20px; right: 20px;';
                break;
            case 'top-center':
                positionStyles += 'top: 20px; left: 50%; transform: translateX(-50%);';
                break;
            case 'bottom-right':
                positionStyles += 'bottom: 20px; right: 20px;';
                break;
        }
        
        alert.style.cssText = positionStyles;
        alert.innerHTML = `
            <i class="bi bi-${type === 'error' ? 'exclamation-triangle' : 'info-circle'} me-2"></i>
            ${title ? `<strong>${title}</strong><br>` : ''}
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        document.body.appendChild(alert);
        setTimeout(() => {
            if (alert.parentNode) {
                alert.remove();
            }
        }, type === 'error' ? 6000 : 4000);
    },

    // Show success message
    showSuccess: (message, containerId = 'success-container') => {
        const container = document.getElementById(containerId) || document.body;
        
        const alert = document.createElement('div');
        alert.className = 'alert alert-success alert-dismissible fade show position-fixed';
        alert.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
        alert.innerHTML = `
            <i class="bi bi-check-circle me-2"></i>${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        
        document.body.appendChild(alert);
        setTimeout(() => alert.remove(), 4000);
    }
};

// Authentication Manager - Enhanced with Role Management
const AuthManager = {
    // Check if user is logged in
    isLoggedIn() {
        return !!localStorage.getItem('bookvault_auth_token');
    },

    // Get current user data
    getCurrentUser() {
        const userProfile = localStorage.getItem('bookvault_user_profile');
        return userProfile ? JSON.parse(userProfile) : null;
    },

    // Get user role
    getUserRole() {
        return localStorage.getItem('bookvault_user_role') || 'USER';
    },

    // Check if user has admin privileges
    isAdmin() {
        return this.getUserRole() === 'ADMIN';
    },

    // Check if user is a seller
    isSeller() {
        const role = this.getUserRole();
        return role === 'SELLER' || role === 'ADMIN';
    },

    // Login user
    async login(credentials) {
        try {
            const response = await APIService.auth.login(credentials);
            
            if (response.success && response.data.token) {
                // Store authentication data
                localStorage.setItem('bookvault_auth_token', response.data.token);
                localStorage.setItem('bookvault_user_role', response.data.role);
                localStorage.setItem('bookvault_user_profile', JSON.stringify(response.data));
                
                // Update navigation
                this.updateNavigation();
                
                return { success: true, data: response.data };
            }
            
            return { success: false, message: response.message || 'Login failed' };
            
        } catch (error) {
            console.error('Login error:', error);
            return { success: false, message: error.message || 'Login failed' };
        }
    },

    // Logout user
    logout() {
        console.log('🚪 Starting logout process...');
        
        // Store current user info for logging
        const currentUser = this.getCurrentUser();
        console.log('🚪 Logging out user:', currentUser?.email || 'Unknown');
        
        // Clear authentication data
        localStorage.removeItem('bookvault_auth_token');
        localStorage.removeItem('bookvault_user_role'); 
        localStorage.removeItem('bookvault_user_profile');
        console.log('🚪 Authentication data cleared from localStorage');
        
        // Clear cart
        CartManager.clearCart();
        console.log('🚪 Shopping cart cleared');
        
        // Update navigation immediately
        this.updateNavigation();
        console.log('🚪 Navigation updated to guest state');
        
        // Show success message
        console.log('🚪 Logout successful, redirecting to home page...');
        
        // Use a small delay to ensure all operations complete
        setTimeout(() => {
            window.location.href = 'index.html';
        }, 100);
    },

    // Update navigation based on authentication state and user roles
    updateNavigation() {
        const authNav = document.querySelector('.auth-nav');
        const guestNav = document.querySelector('.guest-nav');
        const userNameSpan = document.getElementById('user-name');
        const cartLink = document.getElementById('cart-link');
        
        // Update cart UI
        CartManager.updateCartUI();
        
        if (this.isLoggedIn()) {
            const user = this.getCurrentUser();
            const role = this.getUserRole();
            
            console.log('🔐 Updating navigation for role:', role, 'user:', user);
            
            // Show authenticated navigation
            if (authNav) {
                authNav.classList.remove('d-none');
                authNav.classList.add('d-flex');
            }
            if (guestNav) guestNav.classList.add('d-none');
            
            // Show cart for logged-in users
            if (cartLink) cartLink.classList.remove('d-none');
            
            // Update user name in dropdown
            if (userNameSpan && user) {
                const displayName = user.firstName ? 
                    `${user.firstName} ${user.lastName || ''}`.trim() :
                    user.name || user.email?.split('@')[0] || 'User';
                userNameSpan.textContent = displayName;
            }
            
            // Update role-specific navigation items
            this.updateRoleSpecificNavigation(role);
            
            // Update main navigation links based on role
            this.updateMainNavigationForRole(role);
            
        } else {
            // Show guest navigation
            if (authNav) {
                authNav.classList.add('d-none');
                authNav.classList.remove('d-flex');
            }
            if (guestNav) {
                guestNav.classList.remove('d-none');
                guestNav.classList.add('d-flex');
            }
            
            // Hide cart for guests (they can still access it via direct link)
            if (cartLink) cartLink.classList.add('d-none');
            
            // Hide all role-specific items
            this.hideAllRoleSpecificItems();
        }
    },

    // Update role-specific navigation items in dropdown
    updateRoleSpecificNavigation(role) {
        // Admin navigation items
        const adminNavItems = document.querySelectorAll('.admin-nav');
        adminNavItems.forEach(item => {
            if (role === 'ADMIN') {
                item.classList.remove('d-none');
            } else {
                item.classList.add('d-none');
            }
        });

        // Seller navigation items  
        const sellerNavItems = document.querySelectorAll('.seller-nav');
        sellerNavItems.forEach(item => {
            if (role === 'SELLER' || role === 'ADMIN') {
                item.classList.remove('d-none');
            } else {
                item.classList.add('d-none');
            }
        });

        // User-only navigation items
        const userNavItems = document.querySelectorAll('.user-only-nav');
        userNavItems.forEach(item => {
            if (role === 'USER' || role === 'SELLER' || role === 'ADMIN') {
                item.classList.remove('d-none');
            } else {
                item.classList.add('d-none');
            }
        });
    },

    // Update main navigation based on role (add role-specific main nav items)
    updateMainNavigationForRole(role) {
        // Find main nav container
        const mainNav = document.querySelector('.navbar-nav');
        if (!mainNav) return;

        // Remove existing role-specific main nav items
        const existingRoleItems = mainNav.querySelectorAll('.role-nav-item');
        existingRoleItems.forEach(item => item.remove());

        // Add role-specific main navigation items
        if (role === 'ADMIN') {
            this.addMainNavItem(mainNav, 'admin.html', 'Admin Panel', 'bi-shield-check', 'role-nav-item admin-main-nav');
        }
        
        if (role === 'SELLER' || role === 'ADMIN') {
            this.addMainNavItem(mainNav, 'seller.html', 'Seller Hub', 'bi-shop', 'role-nav-item seller-main-nav');
        }
    },

    // Helper method to add main navigation items
    addMainNavItem(navContainer, href, text, iconClass, extraClasses = '') {
        const listItem = document.createElement('li');
        listItem.className = `nav-item ${extraClasses}`;
        
        const link = document.createElement('a');
        link.className = 'nav-link';
        link.href = href;
        link.innerHTML = `<i class="bi ${iconClass} me-1"></i>${text}`;
        
        listItem.appendChild(link);
        navContainer.appendChild(listItem);
    },

    // Hide all role-specific items (for guests)
    hideAllRoleSpecificItems() {
        const roleSpecificSelectors = ['.admin-nav', '.seller-nav', '.user-only-nav', '.role-nav-item'];
        roleSpecificSelectors.forEach(selector => {
            document.querySelectorAll(selector).forEach(item => {
                item.classList.add('d-none');
            });
        });
    }
};

// API Service - Updated for Real Backend Integration
const APIService = {
    // Generic API call method with service-specific URLs - IMPROVED VERSION
    async makeRequest(endpoint, options = {}, serviceUrl = null) {
        // Determine service URL based on endpoint
        let baseUrl = serviceUrl;
        if (!baseUrl) {
            if (endpoint.includes('/api/auth/') || endpoint.includes('/api/admin/')) {
                baseUrl = CONFIG.AUTH_SERVICE_URL;
                console.log('🔐 Using AUTH service for:', endpoint);
            } else if (endpoint.includes('/api/books')) {
                baseUrl = CONFIG.BOOK_SERVICE_URL;
                console.log('📚 Using BOOK service for:', endpoint);
            } else if (endpoint.includes('/api/orders')) {
                baseUrl = CONFIG.ORDER_SERVICE_URL;
                console.log('📦 Using ORDER service for:', endpoint);
            } else {
                baseUrl = CONFIG.API_BASE_URL;
                console.log('🔧 Using DEFAULT service for:', endpoint);
            }
        }
        
        const url = `${baseUrl}${endpoint}`;
        
        // Set default headers
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };
        
        // Add auth token if available
        const token = localStorage.getItem('bookvault_auth_token');
        if (token) {
            headers.Authorization = `Bearer ${token}`;
        }
        
        const requestOptions = {
            ...options,
            headers
        };
        
        console.log(`🌐 Making ${requestOptions.method || 'GET'} request to:`, url);
        
        try {
            const response = await fetch(url, requestOptions);
            
            // Handle different response statuses
            if (response.status === 401) {
                // Unauthorized - token might be expired
                console.warn('🔒 Authentication failed - clearing tokens');
                localStorage.removeItem('bookvault_auth_token');
                localStorage.removeItem('bookvault_user_role');
                localStorage.removeItem('bookvault_user_profile');
                
                // Only redirect to login if not already on login/register pages
                const currentPage = window.location.pathname;
                if (!currentPage.includes('login') && !currentPage.includes('register') && !currentPage.includes('index')) {
                    window.location.href = 'login.html';
                }
                
                throw new Error('Your session has expired. Please log in again.');
            }
            
            if (response.status === 403) {
                // Forbidden - Enhanced ban handling
                const errorText = await response.text();
                
                if (endpoint.includes('/api/auth/login')) {
                    if (errorText.includes('permanently banned') || errorText.includes('PERMANENT')) {
                        throw new Error('Your account has been permanently banned due to multiple failed login attempts. Please contact the administrator for assistance.');
                    } else if (errorText.includes('temporarily locked') || errorText.includes('15 minutes')) {
                        throw new Error('Your account has been temporarily locked for 15 minutes due to failed login attempts. Please try again later.');
                    } else if (errorText.includes('IP') || errorText.includes('location')) {
                        throw new Error('Too many failed login attempts from this location. Please try again in 30 minutes.');
                    } else {
                        throw new Error('Your account has been temporarily locked due to multiple failed login attempts. Please try again later.');
                    }
                } else if (endpoint.includes('/api/auth/register')) {
                    throw new Error('Registration is currently unavailable. Please try again later.');
                } else {
                    throw new Error('You do not have permission to perform this action.');
                }
            }
            
            if (response.status === 404) {
                throw new Error('The requested resource was not found.');
            }
            
            if (response.status === 429) {
                throw new Error('Too many requests. Please wait a moment before trying again.');
            }
            
            if (response.status >= 500) {
                throw new Error('Server error. Please try again later.');
            }
            
            // Try to parse response as JSON
            let data;
            const contentType = response.headers.get('Content-Type');
            
            if (contentType && contentType.includes('application/json')) {
                try {
                    data = await response.json();
                } catch (parseError) {
                    console.warn('⚠️ Failed to parse JSON response:', parseError);
                    data = {};
                }
            } else {
                // Handle non-JSON responses
                const text = await response.text();
                data = { message: text || 'Unknown response format' };
            }
            
            // Handle unsuccessful responses with data
            if (!response.ok) {
                // Extract error message from various possible formats
                let errorMessage = 'Request failed';
                
                if (data.message) {
                    errorMessage = data.message;
                } else if (data.error) {
                    errorMessage = data.error;
                } else if (data.errors && Array.isArray(data.errors)) {
                    errorMessage = data.errors.join(', ');
                } else if (data.errors && typeof data.errors === 'object') {
                    // Handle validation errors object
                    const errorFields = Object.keys(data.errors);
                    if (errorFields.length > 0) {
                        errorMessage = Object.values(data.errors).flat().join(', ');
                    }
                } else if (typeof data === 'string') {
                    errorMessage = data;
                }
                
                // Add status code for debugging
                errorMessage += ` (${response.status})`;
                
                throw new Error(errorMessage);
            }
            
            console.log('✅ Request successful:', { url, status: response.status, data });
            return data;
            
        } catch (error) {
            // Handle network errors
            if (error.name === 'TypeError' && error.message.includes('fetch')) {
                console.error('🌐 Network error:', error);
                throw new Error('Unable to connect to the server. Please check your internet connection and try again.');
            }
            
            // Handle timeout errors
            if (error.name === 'AbortError') {
                console.error('⏱️ Request timeout:', error);
                throw new Error('The request timed out. Please try again.');
            }
            
            console.error(`❌ API Request failed for ${url}:`, error);
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
    },

    // Admin API calls - Real backend implementation
    admin: {
        getDashboardStats: () => APIService.makeRequest(CONFIG.ENDPOINTS.ADMIN.STATS),
        getUsers: (page = 0, size = 10) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ADMIN.USERS}?page=${page}&size=${size}`),
        getSellers: (page = 0, size = 10) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ADMIN.SELLERS}?page=${page}&size=${size}`),
        getAllUsers: () => APIService.makeRequest(`${CONFIG.ENDPOINTS.ADMIN.USERS}/all`),
        updateUserStatus: (userId, action) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ADMIN.USERS}/${userId}/status`, {
            method: 'PUT',
            body: JSON.stringify({ action })
        }),
        updateUserRole: (userId, role) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ADMIN.USERS}/${userId}/role`, {
            method: 'PUT',
            body: JSON.stringify({ role })
        }),
        resetUserPassword: (userId) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ADMIN.USERS}/${userId}/reset-password`, {
            method: 'POST'
        }),
        deleteUser: (userId) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ADMIN.USERS}/${userId}`, {
            method: 'DELETE'
        }),
        verifyUser: (userId) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ADMIN.USERS}/${userId}/verify`, {
            method: 'POST'
        }),
        updateUser: (userId, userData) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ADMIN.USERS}/${userId}`, {
            method: 'PUT',
            body: JSON.stringify(userData)
        })
    },

    // User API calls - Currently not implemented in backend  
    // TODO: Implement user service endpoints
    user: {
        getProfile: (userId) => APIService.auth.getProfile(userId), // Use auth service for now
        updateProfile: (userId, data) => APIService.auth.updateProfile(userId, data), // Use auth service
        getOrders: (userId) => APIService.order.getByUser(userId), // Use order service (which is mocked)
        getOrder: (userId, orderId) => APIService.order.getById(orderId), // Use order service
        getWishlist: (userId) => {
            // Temporary: Store wishlist in localStorage until backend is implemented
            const wishlist = JSON.parse(localStorage.getItem(`bookvault_wishlist_${userId}`) || '[]');
            return Promise.resolve(wishlist);
        },
        addToWishlist: (userId, bookId) => {
            const wishlist = JSON.parse(localStorage.getItem(`bookvault_wishlist_${userId}`) || '[]');
            if (!wishlist.find(item => item.id === bookId)) {
                // Get book details and add to wishlist
                return APIService.books.getById(bookId).then(book => {
                    wishlist.push(book);
                    localStorage.setItem(`bookvault_wishlist_${userId}`, JSON.stringify(wishlist));
                    return book;
                });
            }
            return Promise.resolve();
        },
        removeFromWishlist: (userId, bookId) => {
            const wishlist = JSON.parse(localStorage.getItem(`bookvault_wishlist_${userId}`) || '[]');
            const filtered = wishlist.filter(item => item.id !== bookId);
            localStorage.setItem(`bookvault_wishlist_${userId}`, JSON.stringify(filtered));
            return Promise.resolve();
        }
    },

    // Order API calls - Real backend implementation
    order: {
        create: (orderData) => APIService.makeRequest(CONFIG.ENDPOINTS.ORDERS.BASE, {
            method: 'POST',
            body: JSON.stringify(orderData)
        }),
        getById: (orderId) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ORDERS.BASE}/${orderId}`),
        getByUser: (userId, page = 0, size = 10) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ORDERS.USER}/${userId}?page=${page}&size=${size}`),
        updateStatus: (orderId, status) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ORDERS.BASE}/${orderId}/status`, {
            method: 'PUT',
            body: JSON.stringify({ status })
        }),
        cancel: (orderId) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ORDERS.BASE}/${orderId}/cancel`, {
            method: 'PUT'
        }),
        track: (orderId) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ORDERS.BASE}/${orderId}/tracking`),
        // Admin order methods
        admin: {
            getAll: (page = 0, size = 10) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ORDERS.ADMIN}?page=${page}&size=${size}`),
            getStats: () => APIService.makeRequest(`${CONFIG.ENDPOINTS.ORDERS.ADMIN}/stats`),
            getByStatus: (status, page = 0, size = 10) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ORDERS.ADMIN}/status/${status}?page=${page}&size=${size}`),
            updateStatus: (orderId, status) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ORDERS.ADMIN}/${orderId}/status`, {
                method: 'PUT',
                body: JSON.stringify({ status })
            })
        }
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
            
            console.log('📚 Book API response:', response);
            
            // Handle response structure from backend API
            const responseData = response.data || response;
            const books = responseData.content || responseData;
            
            console.log(`📚 Found ${books.length} books to display`);
            this.displayBooks(books, containerId);
            
            // Display pagination if available
            const paginationData = responseData.totalPages ? responseData : response;
            if (paginationData.totalPages && paginationData.totalPages > 1) {
                this.displayPagination(paginationData, containerId + '-pagination', category);
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
                    <img src="${book.coverImageUrl || book.imageUrl || '/asset/img/books/placeholder.jpg'}" class="card-img-top" alt="${book.title}" style="height: 300px; object-fit: cover;">
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
                                <a href="book-details.html?id=${book.id}" class="btn btn-primary btn-sm">View Details</a>
                                <button class="btn btn-outline-warning btn-sm" onclick="BookManager.addToWishlist('${book.id}')" title="Add to Wishlist">
                                    <i class="bi bi-heart"></i>
                                </button>
                            </div>
                            <div class="mt-2">
                                ${book.stockQuantity <= 0 ? '<small class="text-danger"><i class="bi bi-x-circle"></i> Out of Stock</small>' : 
                                  book.stockQuantity < 10 ? `<small class="text-warning"><i class="bi bi-exclamation-triangle"></i> Only ${book.stockQuantity} left</small>` : 
                                  '<small class="text-success"><i class="bi bi-check-circle"></i> In Stock</small>'}
                            </div>
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
            const user = AuthManager.getCurrentUser();
            if (!user) {
                Utils.showError('Please log in to add books to your wishlist.');
                return;
            }

            await APIService.user.addToWishlist(user.id, bookId);
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
            const response = await APIService.books.search(query);
            console.log('🔍 Search API response:', response);
            
            // Handle response structure from backend API
            const responseData = response.data || response;
            const books = responseData.content || responseData;
            
            console.log(`🔍 Found ${books.length} books for search: "${query}"`);
            this.displayBooks(books, containerId);
        } catch (error) {
            Utils.showError('Search failed. Please try again.');
            console.error('Error searching books:', error);
        }
    },

    // Load books with filters applied
    async loadBooksWithFilters(containerId = 'books-container', filters = {}) {
        try {
            console.log('🔍 Loading books with filters:', filters);
            
            // Show loading state
            const container = document.getElementById(containerId);
            const loadingElement = document.getElementById('loading-placeholder');
            const errorElement = document.getElementById('error-container');
            
            if (loadingElement) {
                loadingElement.style.display = 'block';
                loadingElement.innerHTML = `
                    <div class="spinner-border text-warning" role="status">
                        <span class="visually-hidden">Filtering books...</span>
                    </div>
                    <p class="mt-2">Filtering books...</p>
                `;
            }
            if (errorElement) errorElement.style.display = 'none';

            // Load all books first (since we don't have backend filtering yet)
            const response = await APIService.books.getAll();
            console.log('🔍 Filter API response:', response);
            
            // Handle response structure from backend API
            const responseData = response.data || response;
            let books = responseData.content || responseData;
            
            if (!Array.isArray(books)) {
                books = [];
            }

            // Apply client-side filtering
            let filteredBooks = books.filter(book => {
                // Genre filter
                if (filters.genres && filters.genres.length > 0) {
                    const bookGenres = book.category || book.genre || '';
                    const hasMatchingGenre = filters.genres.some(genre => 
                        bookGenres.toLowerCase().includes(genre.toLowerCase())
                    );
                    if (!hasMatchingGenre) return false;
                }

                // Author filter
                if (filters.author) {
                    const bookAuthor = book.author || '';
                    if (!bookAuthor.toLowerCase().includes(filters.author.toLowerCase())) {
                        return false;
                    }
                }

                // Price filter
                if (filters.maxPrice !== undefined && filters.maxPrice < 100) {
                    const bookPrice = parseFloat(book.price) || 0;
                    if (bookPrice > filters.maxPrice) return false;
                }

                // Rating filter
                if (filters.minRating > 0) {
                    const bookRating = parseFloat(book.rating) || 0;
                    if (bookRating < filters.minRating) return false;
                }

                return true;
            });

            console.log(`✅ Filtered ${filteredBooks.length} books from ${books.length} total`);

            // Hide loading
            if (loadingElement) loadingElement.style.display = 'none';

            // Display filtered results
            this.displayBooks(filteredBooks, containerId);

            // Show filter results summary
            this.showFilterSummary(filteredBooks.length, books.length, filters);

        } catch (error) {
            console.error('❌ Error loading filtered books:', error);
            
            const loadingElement = document.getElementById('loading-placeholder');
            const errorElement = document.getElementById('error-container');
            
            if (loadingElement) loadingElement.style.display = 'none';
            if (errorElement) {
                errorElement.style.display = 'block';
                errorElement.innerHTML = `
                    <div class="col-12">
                        <div class="alert alert-warning" role="alert">
                            <h4 class="alert-heading"><i class="bi bi-exclamation-triangle"></i> Filter Error</h4>
                            <p>Unable to apply filters. Please try again or clear filters.</p>
                            <button class="btn btn-warning" onclick="PageManager.clearAllFilters()">Clear Filters</button>
                        </div>
                    </div>
                `;
            }
        }
    },

    // Show filter results summary
    showFilterSummary(filteredCount, totalCount, filters) {
        // Remove existing summary
        const existingSummary = document.getElementById('filter-summary');
        if (existingSummary) {
            existingSummary.remove();
        }

        // Create filter summary
        const container = document.getElementById('books-container');
        if (container && (filteredCount < totalCount || this.hasActiveFilters(filters))) {
            const summary = document.createElement('div');
            summary.id = 'filter-summary';
            summary.className = 'col-12 mb-3';
            
            const activeFiltersText = this.getActiveFiltersText(filters);
            
            summary.innerHTML = `
                <div class="alert alert-info d-flex justify-content-between align-items-center">
                    <div>
                        <i class="bi bi-funnel me-2"></i>
                        Showing <strong>${filteredCount}</strong> of <strong>${totalCount}</strong> books
                        ${activeFiltersText ? ` • ${activeFiltersText}` : ''}
                    </div>
                    <button class="btn btn-sm btn-outline-secondary" onclick="PageManager.clearAllFilters()">
                        <i class="bi bi-x-circle me-1"></i>Clear Filters
                    </button>
                </div>
            `;
            
            container.insertBefore(summary, container.firstChild);
        }
    },

    // Check if any filters are active
    hasActiveFilters(filters) {
        return (filters.genres && filters.genres.length > 0) ||
               (filters.author && filters.author.trim()) ||
               (filters.maxPrice < 100) ||
               (filters.minRating > 0);
    },

    // Get text description of active filters
    getActiveFiltersText(filters) {
        const activeFilters = [];
        
        if (filters.genres && filters.genres.length > 0) {
            activeFilters.push(`Genres: ${filters.genres.join(', ')}`);
        }
        
        if (filters.author && filters.author.trim()) {
            activeFilters.push(`Author: ${filters.author}`);
        }
        
        if (filters.maxPrice < 100) {
            activeFilters.push(`Max Price: $${filters.maxPrice}`);
        }
        
        if (filters.minRating > 0) {
            const stars = '★'.repeat(filters.minRating);
            activeFilters.push(`Min Rating: ${stars}`);
        }
        
        return activeFilters.join(' • ');
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

// Page Manager - Enhanced with E-commerce Features
const PageManager = {
    // Initialize the entire page management system
    init() {
        console.log('🚀 Initializing PageManager...');
        
        // Initialize navigation IMMEDIATELY to prevent flickering
        this.initImmediateNavigation();
        
        // Wait for DOM to be fully loaded
        document.addEventListener('DOMContentLoaded', () => {
            console.log('📄 DOM Content Loaded - Page:', this.getCurrentPage());
            
            // Initialize common features for all pages
            this.initCommonFeatures();
            
            // Initialize page-specific features
            const currentPage = this.getCurrentPage();
            switch (currentPage) {
                case 'home':
                    this.initHomePage();
                    break;
                case 'booklisting':
                    this.initBookListingPage();
                    break;
                case 'book-details':
                    this.initBookDetailsPage();
                    break;
                case 'user':
                    this.initUserDashboard();
                    break;
                case 'admin':
                    this.initAdminPage();
                    break;
                case 'login':
                    this.initLoginPage();
                    break;
                case 'register':
                    this.initRegisterPage();
                    break;
                case 'cart':
                    this.initCartPage();
                    break;
                default:
                    console.log('ℹ️ No specific initialization for page:', currentPage);
            }
            
            console.log('✅ PageManager initialization complete');
        });
    },

    // Initialize navigation immediately to prevent auth state flickering
    initImmediateNavigation() {
        // Check if user is already authenticated and update navigation immediately
        if (AuthManager.isLoggedIn()) {
            console.log('🔐 User already logged in - updating navigation immediately');
            
            // Hide guest navigation immediately with smooth transition
            const guestNav = document.querySelector('.guest-nav');
            if (guestNav) {
                guestNav.classList.add('d-none');
                guestNav.classList.remove('d-flex');
            }
            
            // Show authenticated navigation with smooth transition
            const authNav = document.querySelector('.auth-nav');
            if (authNav) {
                authNav.classList.remove('d-none');
                authNav.classList.add('d-flex');
            }
            
            // Update user name if possible
            const user = AuthManager.getCurrentUser();
            const userNameSpan = document.getElementById('user-name');
            if (userNameSpan && user) {
                const displayName = user.firstName ? 
                    `${user.firstName} ${user.lastName || ''}`.trim() :
                    user.name || user.email?.split('@')[0] || 'User';
                userNameSpan.textContent = displayName;
            }
            
            // Show cart link
            const cartLink = document.getElementById('cart-link');
            if (cartLink) {
                cartLink.classList.remove('d-none');
            }
            
            // Handle role-specific navigation
            const role = AuthManager.getUserRole();
            if (role) {
                // Show/hide role-specific navigation items
                const roleSelectors = {
                    'ADMIN': '.admin-nav',
                    'SELLER': '.seller-nav', 
                    'USER': '.user-only-nav'
                };
                
                Object.entries(roleSelectors).forEach(([userRole, selector]) => {
                    const elements = document.querySelectorAll(selector);
                    elements.forEach(element => {
                        if (role === userRole || (userRole === 'SELLER' && role === 'ADMIN') || 
                            (userRole === 'USER' && ['USER', 'SELLER', 'ADMIN'].includes(role))) {
                            element.classList.remove('d-none');
                        } else {
                            element.classList.add('d-none');
                        }
                    });
                });
            }
        } else {
            console.log('🔓 User not logged in - showing guest navigation');
            
            // Show guest navigation with smooth transition
            const guestNav = document.querySelector('.guest-nav');
            if (guestNav) {
                guestNav.classList.remove('d-none');
                guestNav.classList.add('d-flex');
            }
            
            // Hide authenticated navigation with smooth transition
            const authNav = document.querySelector('.auth-nav');
            if (authNav) {
                authNav.classList.add('d-none');
                authNav.classList.remove('d-flex');
            }
            
            // Hide cart for guests
            const cartLink = document.getElementById('cart-link');
            if (cartLink) {
                cartLink.classList.add('d-none');
            }
            
            // Hide all role-specific items
            const roleSpecificSelectors = ['.admin-nav', '.seller-nav', '.user-only-nav', '.role-nav-item'];
            roleSpecificSelectors.forEach(selector => {
                document.querySelectorAll(selector).forEach(item => {
                    item.classList.add('d-none');
                });
            });
        }
        
        // Update cart UI immediately
        CartManager.updateCartUI();
    },

    // Initialize common features for all pages
    initCommonFeatures() {
        // Initialize cart functionality
        CartManager.updateCartUI();
        
        // Update navigation with full authentication state
        AuthManager.updateNavigation();
        
        // Handle page-specific authentication requirements
        this.handleAuthenticationRequirements();
        
        // Initialize search functionality if search elements exist
        this.initSearchFunctionality();
        
        // Initialize any global event listeners
        this.initGlobalEventListeners();
        
        console.log('✅ Common features initialized');
    },
    
    // Handle authentication requirements for different pages
    handleAuthenticationRequirements() {
        const currentPage = this.getCurrentPage();
        const isLoggedIn = AuthManager.isLoggedIn();
        const userRole = AuthManager.getUserRole();
        
        // Pages that require authentication
        const protectedPages = ['user', 'admin', 'seller'];
        
        // Pages that admins only can access
        const adminOnlyPages = ['admin'];
        
        // Pages that sellers and admins can access
        const sellerPages = ['seller'];
        
        // Check if current page requires authentication
        if (protectedPages.includes(currentPage) && !isLoggedIn) {
            console.warn('🔒 Page requires authentication, redirecting to login');
            Utils.showError('Please log in to access this page.', { 
                title: 'Authentication Required',
                position: 'top-center' 
            });
            setTimeout(() => {
                window.location.href = 'login.html';
            }, 2000);
            return;
        }
        
        // Check admin-only access
        if (adminOnlyPages.includes(currentPage) && userRole !== 'ADMIN') {
            console.warn('🚫 Admin access required');
            Utils.showError('You do not have permission to access this page.', { 
                title: 'Access Denied',
                position: 'top-center' 
            });
            setTimeout(() => {
                window.location.href = isLoggedIn ? 'user.html' : 'index.html';
            }, 2000);
            return;
        }
        
        // Check seller access
        if (sellerPages.includes(currentPage) && !['SELLER', 'ADMIN'].includes(userRole)) {
            console.warn('🏪 Seller access required');
            Utils.showError('This page is only available to sellers and administrators.', { 
                title: 'Access Denied',
                position: 'top-center' 
            });
            setTimeout(() => {
                window.location.href = isLoggedIn ? 'user.html' : 'index.html';
            }, 2000);
            return;
        }
        
        // Redirect authenticated users away from login/register pages
        if (['login', 'register'].includes(currentPage) && isLoggedIn) {
            console.log('🔄 User already logged in, redirecting to dashboard');
            setTimeout(() => {
                switch (userRole) {
                    case 'ADMIN':
                        window.location.href = 'admin.html';
                        break;
                    case 'SELLER':
                        window.location.href = 'seller.html';
                        break;
                    default:
                        window.location.href = 'user.html';
                }
            }, 1000);
        }
    },
    
    // Initialize global event listeners
    initGlobalEventListeners() {
        // Handle logout clicks - ENHANCED FOR DROPDOWN COMPATIBILITY
        document.addEventListener('click', (e) => {
            const logoutBtn = e.target.matches('.logout-btn') ? e.target : e.target.closest('.logout-btn');
            if (logoutBtn) {
                e.preventDefault();
                e.stopPropagation(); // Prevent dropdown from interfering
                console.log('🚪 Logout button clicked!');
                
                // Add confirmation dialog for better UX
                if (confirm('Are you sure you want to log out?')) {
                    console.log('🚪 User confirmed logout, proceeding...');
                    AuthManager.logout();
                } else {
                    console.log('🚪 User cancelled logout');
                }
            }
        }, true); // Use capture phase for better dropdown compatibility
        
        // Alternative: Direct logout handler for better compatibility
        const logoutButtons = document.querySelectorAll('.logout-btn');
        console.log(`🔍 Found ${logoutButtons.length} logout buttons on page`);
        
        logoutButtons.forEach((btn, index) => {
            console.log(`🔗 Binding logout handler to button ${index + 1}:`, btn);
            
            // Force visibility check bypass for dropdown items
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                e.stopPropagation();
                console.log('🚪 Direct logout button clicked via direct handler!');
                
                if (confirm('Are you sure you want to log out?')) {
                    console.log('🚪 User confirmed logout via direct handler, proceeding...');
                    AuthManager.logout();
                } else {
                    console.log('🚪 User cancelled logout via direct handler');
                }
            }, true); // Use capture phase
            
            // Also add a mousedown handler as backup
            btn.addEventListener('mousedown', (e) => {
                console.log('🖱️ Mousedown detected on logout button');
            });
        });
        
        // Add keyboard shortcut for logout (Ctrl/Cmd + Shift + L)
        document.addEventListener('keydown', (e) => {
            if ((e.ctrlKey || e.metaKey) && e.shiftKey && e.key === 'L') {
                e.preventDefault();
                console.log('⌨️ Logout keyboard shortcut triggered!');
                if (AuthManager.isLoggedIn() && confirm('Logout with keyboard shortcut?\n\nPress Ctrl+Shift+L to logout from anywhere on the site.')) {
                    AuthManager.logout();
                }
            }
        });
        
        // Handle cart button clicks
        document.addEventListener('click', (e) => {
            if (e.target.matches('.add-to-cart-btn') || e.target.closest('.add-to-cart-btn')) {
                e.preventDefault();
                this.handleAddToCart(e.target.closest('.add-to-cart-btn'));
            }
        });
        
        // Handle wishlist button clicks
        document.addEventListener('click', (e) => {
            if (e.target.matches('.wishlist-btn') || e.target.closest('.wishlist-btn')) {
                e.preventDefault();
                const button = e.target.closest('.wishlist-btn');
                const bookId = button.dataset.bookId;
                if (bookId) {
                    BookManager.addToWishlist(bookId);
                }
            }
        });
        
        // Final fallback: Force fix logout buttons after a delay
        setTimeout(() => {
            this.forceFixLogoutButtons();
        }, 1000);
        
        console.log('🎯 Global event listeners initialized');
        console.log(`🎯 Found ${document.querySelectorAll('.logout-btn').length} logout buttons on page`);
    },
    
    // Force fix logout buttons as final fallback
    forceFixLogoutButtons() {
        const logoutButtons = document.querySelectorAll('.logout-btn');
        console.log(`🔧 Force fixing ${logoutButtons.length} logout buttons...`);
        
        logoutButtons.forEach((logoutBtn, index) => {
            console.log(`🔧 Fixing logout button ${index + 1}`);
            
            // Clone to remove all existing event listeners
            const newLogoutBtn = logoutBtn.cloneNode(true);
            logoutBtn.parentNode.replaceChild(newLogoutBtn, logoutBtn);
            
            // Add reliable click handler
            newLogoutBtn.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                console.log('🚪 FORCE FIXED logout button clicked!');
                
                if (confirm('Are you sure you want to log out?')) {
                    console.log('🚪 User confirmed logout, proceeding...');
                    
                    // Clear auth data
                    localStorage.removeItem('bookvault_auth_token');
                    localStorage.removeItem('bookvault_user_role'); 
                    localStorage.removeItem('bookvault_user_profile');
                    
                    // Clear cart
                    if (window.CartManager) {
                        CartManager.clearCart();
                    }
                    
                    // Redirect
                    window.location.href = 'index.html';
                } else {
                    console.log('🚪 User cancelled logout');
                }
            });
            
            // Also handle mousedown as backup
            newLogoutBtn.addEventListener('mousedown', function() {
                console.log('🖱️ Mousedown detected on fixed logout button');
            });
        });
        
        if (logoutButtons.length > 0) {
            console.log('✅ All logout buttons have been force-fixed!');
        }
    },

    // Initialize home page
    initHomePage() {
        console.log('🏠 Initializing Home Page...');
        
        // Load featured books
        BookManager.loadBooks('featured-books-container', null, 0, 8);
        
        // Initialize search form
        const searchForm = document.querySelector('.bookvault-searchbar');
        if (searchForm) {
            searchForm.addEventListener('submit', (e) => {
                e.preventDefault();
                const query = searchForm.querySelector('input').value.trim();
                if (query) {
                    window.location.href = `booklisting.html?search=${encodeURIComponent(query)}`;
                }
            });
        }
    },

    // Initialize book listing page
    initBookListingPage() {
        console.log('📚 Initializing Book Listing Page...');
        
        // Get URL parameters
        const urlParams = new URLSearchParams(window.location.search);
        const category = urlParams.get('category');
        const searchQuery = urlParams.get('search');
        
        if (searchQuery) {
            // Load search results
            BookManager.searchBooks(searchQuery, 'books-container');
        } else {
            // Load books by category or all books
            BookManager.loadBooks('books-container', category);
        }
        
        // Initialize filters
        this.initFilters();
    },

    // Initialize book details page
    initBookDetailsPage() {
        console.log('📖 Initializing Book Details Page...');
        
        // Get book ID from URL
        const urlParams = new URLSearchParams(window.location.search);
        const bookId = urlParams.get('id');
        
        if (bookId) {
            this.loadBookDetails(bookId);
        } else {
            // Setup buttons for static content
            console.log('No book ID found, setting up static page buttons...');
            this.setupBookActions();
        }
    },

    // Initialize user dashboard
    initUserDashboard() {
        console.log('👤 Initializing User Dashboard...');
        
        if (!AuthManager.isLoggedIn()) {
            window.location.href = 'login.html';
            return;
        }
        
        this.loadUserDashboard();
    },

    // Initialize admin page
    initAdminPage() {
        console.log('⚙️ Initializing Admin Page...');
        
        if (!AuthManager.isAdmin()) {
            Utils.showError('Access denied. Admin privileges required.');
            setTimeout(() => window.location.href = 'index.html', 2000);
            return;
        }
        
        AdminManager.loadAdminDashboard();
    },

    // Initialize login page
    initLoginPage() {
        console.log('🔐 Initializing Login Page...');
        
        // Redirect if already logged in
        if (AuthManager.isLoggedIn()) {
            window.location.href = 'user.html';
            return;
        }
        
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', this.handleLogin);
        }
    },

    // Initialize register page
    initRegisterPage() {
        console.log('📝 Initializing Register Page...');
        
        // Redirect if already logged in
        if (AuthManager.isLoggedIn()) {
            window.location.href = 'user.html';
            return;
        }
        
        const registerForm = document.getElementById('registerForm');
        if (registerForm) {
            registerForm.addEventListener('submit', this.handleRegister);
        }
    },

    // Initialize cart page
    initCartPage() {
        console.log('🛒 Initializing Cart Page...');
        
        // Wait a moment to ensure DOM is ready
        setTimeout(() => {
            const container = document.getElementById('cart-container');
            if (container) {
                console.log('Cart container found, displaying cart...');
                CartManager.displayCart('cart-container');
            } else {
                console.error('Cart container not found!');
            }
        }, 100);
    },

    // Handle login form submission
    async handleLogin(event) {
        event.preventDefault();
        
        const form = event.target;
        const email = form.email.value.trim();
        const password = form.password.value;
        
        // Clear previous errors
        const existingErrors = form.querySelectorAll('.auth-error-message');
        existingErrors.forEach(error => error.remove());
        
        // Validate inputs
        if (!email) {
            Utils.showError('Please enter your email address.', { fieldId: 'loginEmail' });
            return;
        }
        
        if (!password) {
            Utils.showError('Please enter your password.', { fieldId: 'loginPassword' });
            return;
        }
        
        if (!this.isValidEmail(email)) {
            Utils.showError('Please enter a valid email address.', { fieldId: 'loginEmail' });
            return;
        }
        
        // Show loading state
        const submitButton = form.querySelector('button[type="submit"]');
        const originalText = submitButton.textContent;
        submitButton.textContent = 'Signing In...';
        submitButton.disabled = true;
        
        try {
            const result = await AuthManager.login({ email, password });
            
            if (result.success) {
                Utils.showSuccess('Login successful! Redirecting...', { position: 'top-center' });
                
                // Redirect based on user role
                setTimeout(() => {
                    const role = AuthManager.getUserRole();
                    switch (role) {
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
            } else {
                // Handle specific error cases
                this.handleLoginError(result.message || 'Login failed', form);
            }
        } catch (error) {
            console.error('Login error:', error);
            this.handleLoginError(error.message || 'Login failed. Please check your connection and try again.', form);
        } finally {
            submitButton.textContent = originalText;
            submitButton.disabled = false;
        }
    },
    
    // Handle specific login errors - UPDATED FOR NEW RATE LIMITING
    handleLoginError(errorMessage, form) {
        let title = 'Login Failed';
        let message = errorMessage;
        let fieldId = null;
        
        // Enhanced error categorization for new rate limiting
        if (errorMessage.toLowerCase().includes('permanently banned')) {
            title = '🚫 Account Permanently Banned';
            message = 'Your account has been permanently banned due to multiple failed login attempts. Please contact the administrator for assistance.';
        } else if (errorMessage.toLowerCase().includes('temporarily locked') || errorMessage.toLowerCase().includes('15 minutes')) {
            title = '⏱️ Account Temporarily Locked';
            message = 'Your account has been temporarily locked for 15 minutes due to failed login attempts. Please try again later.';
        } else if (errorMessage.toLowerCase().includes('ip') || errorMessage.toLowerCase().includes('location') || errorMessage.toLowerCase().includes('30 minutes')) {
            title = '🌐 IP Address Blocked';
            message = 'Too many failed login attempts from this location. Please try again in 30 minutes.';
        } else if (errorMessage.toLowerCase().includes('warning') && errorMessage.toLowerCase().includes('3 failed attempts')) {
            title = '⚠️ Warning - Account Security';
            message = 'Invalid login credentials. Your account will be locked for 15 minutes after 3 failed attempts, and permanently banned after 5 attempts.';
        } else if (errorMessage.toLowerCase().includes('warning') && errorMessage.toLowerCase().includes('5 failed attempts')) {
            title = '⚠️ Warning - IP Security';
            message = 'Invalid login credentials. This IP address will be blocked for 30 minutes after 5 failed attempts.';
        } else if (errorMessage.toLowerCase().includes('email')) {
            title = 'Invalid Email';
            fieldId = 'loginEmail';
        } else if (errorMessage.toLowerCase().includes('password')) {
            title = 'Incorrect Password';
            fieldId = 'loginPassword';
        } else if (errorMessage.toLowerCase().includes('network') || errorMessage.toLowerCase().includes('connection')) {
            title = 'Connection Error';
            message = 'Unable to connect to the server. Please check your internet connection and try again.';
        }
        
        // Show error in form or as notification based on specificity
        if (fieldId) {
            Utils.showError(message, { fieldId });
        } else {
            Utils.showError(message, { formId: 'loginForm', title });
        }
    },

    // Handle register form submission
    async handleRegister(event) {
        event.preventDefault();
        
        const form = event.target;
        const password = form.password.value;
        const confirmPassword = form.confirmPassword?.value;

        // Clear previous errors
        const existingErrors = form.querySelectorAll('.auth-error-message');
        existingErrors.forEach(error => error.remove());
        
        // Get form data
        const userData = {
            firstName: form.firstName.value.trim(),
            lastName: form.lastName.value.trim(),
            email: form.email.value.trim(),
            password: password,
            phone: form.phone?.value.trim() || '',
            role: form.role ? form.role.value : 'USER'
        };

        // Comprehensive validation
        if (!this.validateRegistrationForm(userData, confirmPassword, form)) {
            return;
        }
        
        // Show loading state
        const submitButton = form.querySelector('button[type="submit"]');
        const originalText = submitButton.textContent;
        submitButton.textContent = 'Creating Account...';
        submitButton.disabled = true;

        try {
            const response = await APIService.auth.register(userData);
            
            if (response.token) {
                // Auto-login after successful registration
                localStorage.setItem('bookvault_auth_token', response.token);
                localStorage.setItem('bookvault_user_role', response.role);
                localStorage.setItem('bookvault_user_profile', JSON.stringify(response));
                
                Utils.showSuccess(`Welcome to BookVault, ${userData.firstName}! Your account has been created successfully.`, 
                    { position: 'top-center' });
                
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
            console.error('Registration error:', error);
            this.handleRegistrationError(error.message || 'Registration failed. Please try again.', form);
        } finally {
            submitButton.textContent = originalText;
            submitButton.disabled = false;
        }
    },
    
    // Validate registration form
    validateRegistrationForm(userData, confirmPassword, form) {
        // First Name validation
        if (!userData.firstName) {
            Utils.showError('Please enter your first name.', { fieldId: 'regFirstName' });
            return false;
        }
        
        if (userData.firstName.length < 2) {
            Utils.showError('First name must be at least 2 characters long.', { fieldId: 'regFirstName' });
            return false;
        }
        
        // Last Name validation
        if (!userData.lastName) {
            Utils.showError('Please enter your last name.', { fieldId: 'regLastName' });
            return false;
        }
        
        if (userData.lastName.length < 2) {
            Utils.showError('Last name must be at least 2 characters long.', { fieldId: 'regLastName' });
            return false;
        }
        
        // Email validation
        if (!userData.email) {
            Utils.showError('Please enter your email address.', { fieldId: 'regEmail' });
            return false;
        }
        
        if (!this.isValidEmail(userData.email)) {
            Utils.showError('Please enter a valid email address.', { fieldId: 'regEmail' });
            return false;
        }
        
        // Password validation
        if (!userData.password) {
            Utils.showError('Please enter a password.', { fieldId: 'regPassword' });
            return false;
        }
        
        if (userData.password.length < 8) {
            Utils.showError('Password must be at least 8 characters long.', { fieldId: 'regPassword' });
            return false;
        }
        
        if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(userData.password)) {
            Utils.showError('Password must contain at least one uppercase letter, one lowercase letter, and one number.', 
                { fieldId: 'regPassword' });
            return false;
        }
        
        // Confirm password validation
        if (confirmPassword && userData.password !== confirmPassword) {
            Utils.showError('Passwords do not match.', { fieldId: 'regConfirm' });
            return false;
        }
        
        // Role validation
        if (!userData.role) {
            Utils.showError('Please select your account type.', { fieldId: 'regRole' });
            return false;
        }
        
        return true;
    },
    
    // Handle specific registration errors
    handleRegistrationError(errorMessage, form) {
        let title = 'Registration Failed';
        let message = errorMessage;
        let fieldId = null;
        
        // Categorize errors for better UX
        if (errorMessage.toLowerCase().includes('email')) {
            if (errorMessage.toLowerCase().includes('already exists') || errorMessage.toLowerCase().includes('taken')) {
                title = 'Email Already Registered';
                message = 'An account with this email already exists. Please use a different email or try logging in.';
                fieldId = 'regEmail';
            } else {
                title = 'Invalid Email';
                fieldId = 'regEmail';
            }
        } else if (errorMessage.toLowerCase().includes('password')) {
            title = 'Password Issue';
            fieldId = 'regPassword';
        } else if (errorMessage.toLowerCase().includes('name')) {
            title = 'Invalid Name';
            if (errorMessage.toLowerCase().includes('first')) {
                fieldId = 'regFirstName';
            } else if (errorMessage.toLowerCase().includes('last')) {
                fieldId = 'regLastName';
            }
        } else if (errorMessage.toLowerCase().includes('network') || errorMessage.toLowerCase().includes('connection')) {
            title = 'Connection Error';
            message = 'Unable to connect to the server. Please check your internet connection and try again.';
        } else if (errorMessage.toLowerCase().includes('validation')) {
            title = 'Validation Error';
        }
        
        // Show error in form or as notification based on specificity
        if (fieldId) {
            Utils.showError(message, { fieldId });
        } else {
            Utils.showError(message, { formId: 'registerForm', title });
        }
    },
    
    // Email validation helper
    isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    },

    // Load user dashboard data
    async loadUserDashboard() {
        const user = AuthManager.getCurrentUser();
        
        if (user) {
            // Update user profile display
            this.displayUserProfile(user);
            
            // Load user orders
            this.loadUserOrders();
            
            // Load user wishlist
            this.loadUserWishlist();
        }
    },

    // Display user profile
    displayUserProfile(user) {
        const profileContainer = document.querySelector('.dashboard-card');
        if (profileContainer) {
            const nameElement = profileContainer.querySelector('.fs-5');
            const emailElement = profileContainer.querySelector('.text-muted');
            
            if (nameElement) {
                nameElement.textContent = `${user.firstName || ''} ${user.lastName || ''}`.trim() || 'User';
            }
            if (emailElement) {
                emailElement.textContent = user.email || '';
            }
        }
    },

    // Load user orders
    async loadUserOrders() {
        try {
            const userId = AuthManager.getCurrentUser()?.id;
            if (!userId) return;
            
            const response = await APIService.order.getByUser(userId);
            const orders = response.content || response;
            const container = document.querySelector('#orders-table-body');
            
            if (container) {
                if (!orders || orders.length === 0) {
                    container.innerHTML = `
                        <tr>
                            <td colspan="6" class="text-center py-4">
                                <i class="bi bi-box display-4 text-muted mb-3"></i>
                                <p class="text-muted">No orders found</p>
                                <a href="booklisting.html" class="btn bookvault-btn">Start Shopping</a>
                            </td>
                        </tr>
                    `;
                } else {
                    container.innerHTML = orders.map(order => {
                        const statusColor = this.getOrderStatusColor(order.status);
                        const firstItem = order.orderItems?.[0] || order.items?.[0];
                        
                        return `
                            <tr>
                                <td><img src="${firstItem?.bookImageUrl || '/asset/img/books/placeholder.jpg'}" 
                                         style="width: 44px; height: 60px; object-fit: cover;" class="rounded" alt=""></td>
                                <td>${firstItem?.bookTitle || 'Order Items'}</td>
                                <td>${new Date(order.orderDate).toLocaleDateString()}</td>
                                <td>${Utils.formatCurrency(order.totalAmount)}</td>
                                <td><span class="badge bg-${statusColor}">${order.status}</span></td>
                                <td>
                                    <div class="d-flex gap-1">
                                        <button class="btn btn-sm btn-outline-primary" onclick="UserManager.viewOrder('${order.id}')" title="View Order Details">
                                            <i class="bi bi-eye"></i>
                                        </button>
                                        <button class="btn btn-sm btn-outline-warning" onclick="UserManager.buyAgain('${order.id}')" title="Buy Again">
                                            <i class="bi bi-arrow-repeat"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        `;
                    }).join('');
                }
            }
        } catch (error) {
            console.error('Error loading orders:', error);
            Utils.showError('Failed to load orders.');
        }
    },
    
    // Get order status badge color
    getOrderStatusColor(status) {
        switch (status?.toUpperCase()) {
            case 'DELIVERED': return 'success';
            case 'PROCESSING': case 'CONFIRMED': case 'SHIPPED': return 'warning';
            case 'CANCELLED': case 'REFUNDED': return 'danger';
            case 'PENDING': return 'secondary';
            default: return 'secondary';
        }
    },

    // View order details
    async viewOrder(orderId) {
        try {
            const userId = AuthManager.getCurrentUser()?.id;
            if (!userId) return;
            
            const order = await APIService.order.getById(orderId);
            const statusColor = this.getOrderStatusColor(order.status);
            const orderItems = order.orderItems || [];
            
            // Show order details in modal
            const modal = document.createElement('div');
            modal.className = 'modal fade';
            modal.innerHTML = `
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Order #${order.id}</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body">
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <strong>Order Date:</strong> ${new Date(order.orderDate).toLocaleDateString()}
                                </div>
                                <div class="col-md-6">
                                    <strong>Status:</strong> <span class="badge bg-${statusColor}">${order.status}</span>
                                </div>
                            </div>
                            ${order.trackingNumber ? `
                                <div class="row mb-3">
                                    <div class="col-md-12">
                                        <strong>Tracking Number:</strong> ${order.trackingNumber}
                                    </div>
                                </div>
                            ` : ''}
                            <h6>Items:</h6>
                            <div class="order-items">
                                ${orderItems.map(item => `
                                    <div class="d-flex align-items-center gap-3 mb-3 p-3 border rounded">
                                        <img src="${item.bookImageUrl || '/asset/img/books/placeholder.jpg'}" style="width: 60px; height: 80px; object-fit: cover;" class="rounded">
                                        <div class="flex-grow-1">
                                            <h6 class="mb-1">${item.bookTitle}</h6>
                                            <p class="text-muted mb-1 small">${item.bookAuthor || 'Unknown Author'}</p>
                                            <div>Quantity: ${item.quantity}</div>
                                            <div class="small text-muted">Unit Price: ${Utils.formatCurrency(item.unitPrice)}</div>
                                        </div>
                                        <div class="text-end">
                                            <div class="fw-bold">${Utils.formatCurrency(item.totalPrice)}</div>
                                        </div>
                                    </div>
                                `).join('')}
                            </div>
                            ${order.shippingAddress ? `
                                <h6 class="mt-4">Shipping Address:</h6>
                                <div class="p-3 border rounded bg-light">
                                    <div>${order.shippingAddress.fullName}</div>
                                    <div>${order.shippingAddress.addressLine1}</div>
                                    ${order.shippingAddress.addressLine2 ? `<div>${order.shippingAddress.addressLine2}</div>` : ''}
                                    <div>${order.shippingAddress.city}, ${order.shippingAddress.postalCode}</div>
                                    <div>${order.shippingAddress.country}</div>
                                </div>
                            ` : ''}
                            <div class="text-end mt-3">
                                <h5>Total: ${Utils.formatCurrency(order.totalAmount)}</h5>
                            </div>
                            ${order.status === 'PENDING' ? `
                                <div class="mt-3">
                                    <button class="btn btn-outline-danger" onclick="UserManager.cancelOrder('${order.id}')">Cancel Order</button>
                                </div>
                            ` : ''}
                        </div>
                    </div>
                </div>
            `;
            
            document.body.appendChild(modal);
            const bsModal = new bootstrap.Modal(modal);
            bsModal.show();
            
            modal.addEventListener('hidden.bs.modal', () => modal.remove());
            
        } catch (error) {
            console.error('Error loading order details:', error);
            Utils.showError('Failed to load order details.');
        }
    },
    
    // Cancel order
    async cancelOrder(orderId) {
        if (!confirm('Are you sure you want to cancel this order?')) return;
        
        try {
            await APIService.order.cancel(orderId);
            Utils.showSuccess('Order cancelled successfully.');
            this.loadUserOrders();
            
            // Close any open modals
            const openModal = document.querySelector('.modal.show');
            if (openModal) {
                bootstrap.Modal.getInstance(openModal).hide();
            }
        } catch (error) {
            console.error('Error cancelling order:', error);
            Utils.showError('Failed to cancel order.');
        }
    },

    // Buy again - Add all items from previous order to cart
    async buyAgain(orderId) {
        try {
            const order = await APIService.order.getById(orderId);
            const orderItems = order.orderItems || [];
            
            if (orderItems.length === 0) {
                Utils.showError('No items found in this order.');
                return;
            }

            let addedCount = 0;
            for (const item of orderItems) {
                // Create book object for cart
                const bookForCart = {
                    id: item.bookId,
                    title: item.bookTitle,
                    author: item.bookAuthor || 'Unknown Author',
                    price: item.unitPrice,
                    imageUrl: item.bookImageUrl || '/asset/img/books/placeholder.jpg'
                };
                
                // Add each item to cart with its original quantity
                CartManager.addToCart(bookForCart, item.quantity);
                addedCount += item.quantity;
            }
            
            Utils.showSuccess(`${addedCount} item(s) added to your cart!`);
            
        } catch (error) {
            console.error('Error processing buy again:', error);
            Utils.showError('Failed to add items to cart.');
        }
    },

    // Load user wishlist
    async loadUserWishlist() {
        try {
            const userId = AuthManager.getCurrentUser()?.id;
            if (!userId) return;
            
            const wishlist = await APIService.user.getWishlist(userId);
            
            const container = document.querySelector('#wishlist .row');
            if (container) {
                if (wishlist.length === 0) {
                    container.innerHTML = `
                        <div class="col-12 text-center py-5">
                            <i class="bi bi-heart display-4 text-muted mb-3"></i>
                            <h5>Your wishlist is empty</h5>
                            <p class="text-muted">Save books you love to your wishlist!</p>
                            <a href="booklisting.html" class="btn bookvault-btn">Browse Books</a>
                        </div>
                    `;
        } else {
                    container.innerHTML = wishlist.map(book => `
                        <div class="col-6 col-md-3">
                            <div class="recommend-card h-100">
                                <img class="recommend-cover" src="${book.imageUrl || '/asset/img/books/placeholder.jpg'}" alt="${book.title}">
                                <div class="recommend-title">${book.title}</div>
                                <div class="recommend-author">${book.author}</div>
                                <button class="btn btn-sm btn-warning rounded-pill mt-2" 
                                        onclick="CartManager.addToCart({id: ${book.id}, title: '${book.title}', author: '${book.author}', price: ${book.price}, imageUrl: '${book.imageUrl}'})">
                                    Add to Cart
                                </button>
                            </div>
                        </div>
                    `).join('');
                }
            }
        } catch (error) {
            console.error('Error loading wishlist:', error);
            Utils.showError('Failed to load wishlist.');
        }
    },

    // Initialize search functionality
    initSearchFunctionality() {
        const searchForms = document.querySelectorAll('.bookvault-searchbar');
        searchForms.forEach(form => {
            form.addEventListener('submit', (e) => {
                e.preventDefault();
                const query = form.querySelector('input').value.trim();
                if (query) {
                    window.location.href = `booklisting.html?search=${encodeURIComponent(query)}`;
                }
            });
        });
    },

    // Initialize filters on listing page
    initFilters() {
        const filterInputs = document.querySelectorAll('.filters-sidebar input, .filters-sidebar select');
        filterInputs.forEach(input => {
            input.addEventListener('change', () => {
                    this.applyFilters();
            });
        });

        // Price range slider
        const priceRange = document.getElementById('priceRange');
        if (priceRange) {
            priceRange.addEventListener('input', (e) => {
                const maxDisplay = document.getElementById('price-max-display');
                if (maxDisplay) {
                    maxDisplay.textContent = `$${e.target.value}`;
                }
            });
        }
    },

    // Apply filters
    applyFilters() {
        const filters = this.getActiveFilters();
        BookManager.loadBooksWithFilters('books-container', filters);
    },

    // Get active filters
    getActiveFilters() {
        const filters = {};
        
        // Genre filters
        const genreCheckboxes = document.querySelectorAll('.filters-sidebar input[type="checkbox"]:checked');
        if (genreCheckboxes.length > 0) {
            filters.genres = Array.from(genreCheckboxes).map(cb => cb.closest('label').textContent.trim());
        }
        
        // Price filter
        const priceRange = document.getElementById('priceRange');
        if (priceRange) {
            filters.maxPrice = priceRange.value;
        }
        
        // Rating filter
        const ratingRadio = document.querySelector('.filters-sidebar input[name="rating"]:checked');
        if (ratingRadio) {
            filters.minRating = ratingRadio.id.replace('r', '');
        }

        return filters;
    },

    // Show cart modal
    showCartModal() {
        const modal = document.createElement('div');
        modal.className = 'modal fade';
        modal.innerHTML = `
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="bi bi-cart me-2"></i>Shopping Cart</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="modal-cart-container"></div>
                    </div>
                </div>
            </div>
        `;
        
        document.body.appendChild(modal);
        const bsModal = new bootstrap.Modal(modal);
        bsModal.show();
        
        // Display cart contents
        CartManager.displayCart('modal-cart-container');
        
        modal.addEventListener('hidden.bs.modal', () => modal.remove());
    },

    // Handle add to cart
    handleAddToCart(button, book = null) {
        console.log('🛒 HandleAddToCart called', { button, book });
        
        // Use provided book data or extract from page
        const bookData = book || this.extractBookDataFromPage();
        console.log('📖 Book data for cart:', bookData);
        
        if (bookData) {
            console.log('✅ Adding book to cart...');
            CartManager.addToCart(bookData);
        } else {
            console.log('❌ No book data found');
            Utils.showError('Unable to add book to cart. Please try again.');
        }
    },

    // Extract book data from current page
    extractBookDataFromPage() {
        // For book details page
        const titleElement = document.querySelector('.book-title-main');
        const authorElement = document.querySelector('.book-author-main');
        const priceElement = document.querySelector('.book-price-main');
        const imageElement = document.querySelector('.book-cover-main');
        
        if (titleElement && authorElement && priceElement) {
            const title = titleElement.textContent.trim();
            const author = authorElement.textContent.trim();
            
            return {
                id: this.generateBookId(title, author), // Use consistent ID based on book content
                title: title,
                author: author,
                price: parseFloat(priceElement.textContent.replace(/[^0-9.]/g, '')),
                imageUrl: imageElement ? imageElement.src : '/asset/img/books/placeholder.jpg'
            };
        }
        
        return null;
    },

    // Generate consistent book ID
    generateBookId(title, author) {
        // Create a simple hash from title and author to ensure consistent IDs
        const text = `${title}-${author}`.toLowerCase().replace(/\s+/g, '-');
        let hash = 0;
        for (let i = 0; i < text.length; i++) {
            const char = text.charCodeAt(i);
            hash = ((hash << 5) - hash) + char;
            hash = hash & hash; // Convert to 32-bit integer
        }
        return Math.abs(hash);
    },

    // Load book details
    async loadBookDetails(bookId) {
        try {
            const book = await APIService.books.getById(bookId);
            this.displayBookDetails(book);
            this.setupBookActions(book);
        } catch (error) {
            console.error('Error loading book details:', error);
            Utils.showError('Failed to load book details.');
            // Setup interactive buttons even if API fails
            this.setupBookActions();
        }
    },

    // Setup book action buttons
    setupBookActions(book = null) {
        // Add to Cart buttons
        const addToCartBtns = document.querySelectorAll('.add-to-cart-btn');
        console.log(`🔘 Found ${addToCartBtns.length} Add to Cart buttons`);
        
        // Remove existing listeners first to prevent duplicates
        addToCartBtns.forEach(btn => {
            // Clone the button to remove all event listeners
            const newBtn = btn.cloneNode(true);
            btn.parentNode.replaceChild(newBtn, btn);
        });
        
        // Re-select buttons after cloning
        const freshAddToCartBtns = document.querySelectorAll('.add-to-cart-btn');
        freshAddToCartBtns.forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                console.log('🔘 Add to Cart button clicked', book);
                this.handleAddToCart(btn, book);
            });
        });

        // Buy Now buttons
        const buyNowBtns = document.querySelectorAll('.buy-now-btn');
        console.log(`🔘 Found ${buyNowBtns.length} Buy Now buttons`);
        
        // Remove existing listeners first to prevent duplicates
        buyNowBtns.forEach(btn => {
            const newBtn = btn.cloneNode(true);
            btn.parentNode.replaceChild(newBtn, btn);
        });
        
        // Re-select buttons after cloning
        const freshBuyNowBtns = document.querySelectorAll('.buy-now-btn');
        freshBuyNowBtns.forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                console.log('🔘 Buy Now button clicked', book);
                this.handleBuyNow(book);
            });
        });
    },

    // Handle buy now
    handleBuyNow(book = null) {
        const bookData = book || this.extractBookDataFromPage();
        if (bookData) {
            CartManager.addToCart(bookData);
            CheckoutManager.startCheckout();
        } else {
            Utils.showError('Unable to process purchase. Please try again.');
        }
    },

    // Display book details
    displayBookDetails(book) {
        const titleElement = document.querySelector('.book-title-main');
        const authorElement = document.querySelector('.book-author-main');
        const priceElement = document.querySelector('.book-price-main');
        const imageElement = document.querySelector('.book-cover-main');
        const descriptionElement = document.querySelector('.book-description');
        
        if (titleElement) titleElement.textContent = book.title;
        if (authorElement) authorElement.textContent = book.author;
        if (priceElement) priceElement.textContent = Utils.formatCurrency(book.price);
        if (imageElement) imageElement.src = book.imageUrl || '/asset/img/books/placeholder.jpg';
        if (descriptionElement) descriptionElement.textContent = book.description;
    },

    // Get current page identifier
    getCurrentPage() {
        const pathname = window.location.pathname;
        const filename = pathname.split('/').pop().toLowerCase();
        
        // Handle different filename formats
        if (!filename || filename === 'index.html') {
            return 'home';
        }
        
        // Remove .html extension if present
        const pageName = filename.replace('.html', '');
        
        // Map specific pages
        const pageMapping = {
            'booklisting': 'booklisting',
            'book-details': 'book-details',
            'user': 'user',
            'admin': 'admin',
            'login': 'login',
            'register': 'register',
            'cart': 'cart',
            'seller': 'seller',
            'about': 'about',
            'contact': 'contact',
            'help': 'help'
        };
        
        return pageMapping[pageName] || pageName;
    }
};

// Global initialization
document.addEventListener('DOMContentLoaded', function() {
    console.log('🔥 BookVault - Starting Application...');
    
    // Initialize the main page manager
    PageManager.init();
    
    console.log('✅ BookVault Application Started Successfully');
});

// Export managers for global access
window.BookVault = {
    PageManager,
    AuthManager,
    CartManager,
    CheckoutManager,
    AdminManager,
    BookManager,
    UserManager,
    Utils
};

// Debug helpers (remove in production)
window.debugCart = {
    addTestBook: () => {
        const testBook = {
            id: 12345,
            title: "Test Book",
            author: "Test Author",
            price: 19.99,
            imageUrl: "asset/img/books/the-great-gatsby.png"
        };
        CartManager.addToCart(testBook);
        console.log("Test book added to cart");
    },
    
    showCart: () => {
        console.log("Current cart:", CartManager.getCart());
    },
    
    clearCart: () => {
        CartManager.clearCart();
        console.log("Cart cleared");
    }
};

// Debug helpers for authentication (remove in production)
window.debugAuth = {
    testLogout: () => {
        console.log('🧪 Testing logout function directly...');
        AuthManager.logout();
    },
    
    checkAuthState: () => {
        console.log('🧪 Current auth state:');
        console.log('- Logged in:', AuthManager.isLoggedIn());
        console.log('- Token:', localStorage.getItem('bookvault_auth_token'));
        console.log('- Role:', AuthManager.getUserRole());
        console.log('- User:', AuthManager.getCurrentUser());
    },
    
    findLogoutButtons: () => {
        const buttons = document.querySelectorAll('.logout-btn');
        console.log(`🧪 Found ${buttons.length} logout buttons:`);
        buttons.forEach((btn, index) => {
            console.log(`- Button ${index + 1}:`, btn);
            console.log(`  - Text:`, btn.textContent);
            console.log(`  - Classes:`, btn.className);
            console.log(`  - Visible:`, btn.offsetParent !== null);
        });
        return buttons;
    },
    
    simulateLogoutClick: () => {
        const buttons = document.querySelectorAll('.logout-btn');
        if (buttons.length > 0) {
            console.log('🧪 Simulating click on first logout button...');
            buttons[0].click();
        } else {
            console.log('🧪 No logout buttons found!');
        }
    },
    
    forceLogout: () => {
        console.log('🧪 Force logout - no confirmation dialog');
        localStorage.removeItem('bookvault_auth_token');
        localStorage.removeItem('bookvault_user_role'); 
        localStorage.removeItem('bookvault_user_profile');
        CartManager.clearCart();
        window.location.href = 'index.html';
    },
    
    showDropdown: () => {
        console.log('🧪 Trying to show dropdown for logout button access...');
        const dropdown = document.querySelector('.dropdown-toggle');
        if (dropdown) {
            dropdown.click();
            setTimeout(() => {
                console.log('🧪 Dropdown should now be visible - try logout button');
                const logoutBtn = document.querySelector('.logout-btn');
                if (logoutBtn && logoutBtn.offsetParent !== null) {
                    console.log('✅ Logout button is now visible!');
                } else {
                    console.log('❌ Logout button still not visible');
                }
            }, 200);
        } else {
            console.log('❌ No dropdown toggle found');
        }
    }
}; 
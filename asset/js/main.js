/**
 * BookVault - Main JavaScript File
 * Handles frontend interactions, API calls, and dynamic content
 */

// Configuration - Updated for Real Backend Integration
const CONFIG = {
    // Get configuration from config.js with fallbacks
    get AUTH_SERVICE_URL() {
        try {
            if (window.BookVaultConfig && window.BookVaultConfig.environment) {
                return window.BookVaultConfig.environment === 'development' 
                    ? window.BookVaultConfig.development.AUTH_SERVICE_URL 
                    : window.BookVaultConfig.production.AUTH_SERVICE_URL;
            }
        } catch (error) {
            console.warn('Config not available, using fallback:', error);
        }
        return 'https://auth-service-production-0848.up.railway.app/api';
    },
    
    get BOOK_SERVICE_URL() {
        try {
            if (window.BookVaultConfig && window.BookVaultConfig.environment) {
                return window.BookVaultConfig.environment === 'development' 
                    ? window.BookVaultConfig.development.BOOK_SERVICE_URL 
                    : window.BookVaultConfig.production.BOOK_SERVICE_URL;
            }
        } catch (error) {
            console.warn('Config not available, using fallback:', error);
        }
        return 'https://book-service-production-4444.up.railway.app/api';
    },
    
    // Orders are now handled by the Book Service
    get ORDER_SERVICE_URL() {
        return this.BOOK_SERVICE_URL;
    },
    
    // Legacy API base URL for compatibility
    get API_BASE_URL() {
        return this.AUTH_SERVICE_URL;
    },
    
    ENDPOINTS: {
        // Real backend endpoints
        AUTH: {
            LOGIN: '/auth/login',
            REGISTER: '/auth/register',
            PROFILE: '/auth/profile',
            VALIDATE: '/auth/validate'
        },
        BOOKS: {
            BASE: '/books',
            SEARCH: '/books/search',
            CATEGORIES: '/books/categories',
            FEATURED: '/books/featured',
            BESTSELLERS: '/books/bestsellers',
            NEW_RELEASES: '/books/new-releases',
            BY_CATEGORY: '/books/category',
            BY_AUTHOR: '/books/author',
            FILTER: '/books/filter'
        },
        ADMIN: {
            USERS: '/auth/admin/users',
            SELLERS: '/auth/admin/sellers',
            DASHBOARD: '/auth/admin/dashboard',
            STATS: '/auth/admin/dashboard/stats'
        },
        ORDERS: {
            BASE: '/orders',
            USER: '/orders/user',
            ADMIN: '/orders/admin'
        },
        SELLER: {
            BOOKS: '/books/seller',
            CREATE_BOOK: '/books',
            UPDATE_BOOK: '/books',
            DELETE_BOOK: '/books',
            ORDERS: '/orders/seller',
            DASHBOARD: '/books/seller/stats'
        }
    }
};

// Shopping Cart Management
const CartManager = {
    // Get cart from localStorage
    getCart() {
        const cart = localStorage.getItem('bookvault_cart');
        const rawCart = cart ? JSON.parse(cart) : [];
        
        // Clean up corrupted cart items
        const cleanCart = rawCart.filter(item => {
            const isValid = item && 
                           item.id && 
                           item.title && 
                           item.author && 
                           item.price != null && 
                           item.price > 0 && 
                           item.quantity > 0;
            
            if (!isValid) {
                console.warn('🧹 Removing corrupted cart item:', item);
            }
            
            return isValid;
        });
        
        // If cart was cleaned up, save the clean version
        if (cleanCart.length !== rawCart.length) {
            console.log(`🧹 Cleaned cart: ${rawCart.length} -> ${cleanCart.length} items`);
            localStorage.setItem('bookvault_cart', JSON.stringify(cleanCart));
        }
        
        return cleanCart;
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
        
        // Validate required book data
        if (!book || !book.id || !book.title || !book.author || book.price == null || book.price <= 0) {
            console.error('❌ Invalid book data provided to addToCart:', book);
            console.error('❌ Validation details:', {
                hasBook: !!book,
                hasId: !!(book?.id),
                hasTitle: !!(book?.title),
                hasAuthor: !!(book?.author),
                hasValidPrice: book?.price != null && book?.price > 0,
                actualPrice: book?.price
            });
            Utils.showError('Unable to add book to cart. Missing book information.');
            return false;
        }
        
        const cart = this.getCart();
        console.log('📦 Current cart before add:', cart);
        
        const existingItem = cart.find(item => String(item.id) === String(book.id));
        
        if (existingItem) {
            console.log(`📝 Found existing item: ${existingItem.title}, current qty: ${existingItem.quantity}, adding: ${quantity}`);
            existingItem.quantity += quantity;
            console.log(`📝 New quantity: ${existingItem.quantity}`);
            this.showCartNotification(`Updated quantity to ${existingItem.quantity}!`);
        } else {
            console.log(`🆕 Adding new item to cart`);
            const newItem = {
                id: String(book.id),
                title: String(book.title).trim(),
                author: String(book.author).trim(),
                price: parseFloat(book.price),
                imageUrl: book.imageUrl || 'asset/img/books/placeholder.jpg',
                quantity: parseInt(quantity),
                addedAt: new Date().toISOString()
            };
            console.log('🆕 New item:', newItem);
            cart.push(newItem);
            this.showCartNotification(`Added "${book.title}" to cart!`);
        }
        
        console.log('📦 Cart after add:', cart);
        this.saveCart(cart);
        this.updateCartUI();
        return true;
    },

    // Remove item from cart
    removeFromCart(bookId) {
        const searchId = String(bookId);
        const cart = this.getCart().filter(item => String(item.id) !== searchId);
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
        console.log(`🔄 UpdateQuantity called: item ${bookId} (type: ${typeof bookId}), new quantity ${quantity}`);
        const cart = this.getCart();
        
        // Ensure bookId is treated as string for consistency
        const searchId = String(bookId);
        
        // Debug cart search
        console.log(`🔍 Looking for item ${searchId} in cart with ${cart.length} items`);
        cart.forEach((item, index) => {
            console.log(`  Cart item ${index}: id=${item.id} (type: ${typeof item.id}), matches: ${String(item.id) === searchId}`);
        });
        
        const item = cart.find(item => String(item.id) === searchId);
        
        if (item) {
            console.log(`📝 Found item: ${item.title}, current quantity: ${item.quantity}`);
            if (quantity <= 0) {
                console.log('🗑️ Quantity <= 0, removing item');
                this.removeFromCart(searchId);
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
            console.log(`❌ Item ${searchId} not found in cart`);
            console.log(`❌ Available item IDs:`, cart.map(item => `${item.id} (${typeof item.id})`));
        }
    },

    // Clear cart
    clearCart() {
        localStorage.removeItem('bookvault_cart');
        this.updateCartUI();
    },

    // Manual cart cleanup method
    cleanupCart() {
        console.log('🧹 Manual cart cleanup initiated...');
        const cart = this.getCart(); // This will automatically clean up
        console.log('✅ Cart cleanup completed');
        
        // Refresh cart display if currently shown
        const cartContainer = document.getElementById('cart-container');
        if (cartContainer) {
            this.displayCart('cart-container');
        }
        
        return cart;
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
        
        // Debug each cart item
        cart.forEach((item, index) => {
            console.log(`📋 Cart item ${index + 1}:`, {
                id: item.id,
                idType: typeof item.id,
                title: item.title,
                quantity: item.quantity
            });
        });
        
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
        
        // Check if listeners are already attached to prevent duplicates
        if (container.hasAttribute('data-cart-listeners-attached')) {
            console.log('🔄 Cart listeners already attached to this container');
            return;
        }
        
        // Mark container as having listeners attached
        container.setAttribute('data-cart-listeners-attached', 'true');
        
        // Use event delegation for better reliability
        container.addEventListener('click', (e) => {
            // Handle decrease quantity buttons
            if (e.target.matches('.decrease-qty') || e.target.closest('.decrease-qty')) {
                e.preventDefault();
                const btn = e.target.closest('.decrease-qty');
                const itemId = btn.dataset.id;
                console.log('🔽 Decrease button clicked for item:', itemId);
                
                // Get current quantity from cart data
                const cart = this.getCart();
                const item = cart.find(item => String(item.id) === String(itemId));
                const currentQty = item ? item.quantity : 0;
                
                // Prevent quantity from going below 1
                if (currentQty <= 1) {
                    console.log('⚠️ Cannot decrease quantity below 1');
                    return;
                }
                
                const newQty = currentQty - 1;
                console.log(`Decreasing quantity for item ${itemId} from ${currentQty} to ${newQty}`);
                this.updateQuantity(itemId, newQty);
                return;
            }
            
            // Handle increase quantity buttons
            if (e.target.matches('.increase-qty') || e.target.closest('.increase-qty')) {
                e.preventDefault();
                const btn = e.target.closest('.increase-qty');
                const itemId = btn.dataset.id;
                console.log('🔼 Increase button clicked for item:', itemId);
                
                // Get current quantity from cart data
                const cart = this.getCart();
                const item = cart.find(item => String(item.id) === String(itemId));
                const currentQty = item ? item.quantity : 0;
                
                const newQty = currentQty + 1;
                console.log(`Increasing quantity for item ${itemId} from ${currentQty} to ${newQty}`);
                this.updateQuantity(itemId, newQty);
                return;
            }
            
            // Handle remove item buttons
            if (e.target.matches('.remove-item') || e.target.closest('.remove-item')) {
                e.preventDefault();
                const btn = e.target.closest('.remove-item');
                const itemId = btn.dataset.id;
                console.log('🗑️ Remove button clicked for item ID:', itemId);
                
                if (confirm('Are you sure you want to remove this item from your cart?')) {
                    this.removeFromCart(itemId);
                }
                return;
            }
            
            // Handle checkout button
            if (e.target.matches('.checkout-btn') || e.target.closest('.checkout-btn')) {
                e.preventDefault();
                console.log('💳 Checkout button clicked');
                CheckoutManager.startCheckout();
                return;
            }
            
            // Handle clear cart button
            if (e.target.matches('.clear-cart-btn') || e.target.closest('.clear-cart-btn')) {
                e.preventDefault();
                console.log('🗑️ Clear cart button clicked');
                if (confirm('Are you sure you want to clear your cart?')) {
                    this.clearCart();
                    // Refresh display
                    this.displayCart(container.id);
                }
                return;
            }
        });
        
        console.log('✅ Event listeners attached via delegation');
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
                                        <label class="form-label">Phone (Optional)</label>
                                        <input type="tel" class="form-control" name="phone" placeholder="+1 (555) 123-4567">
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
        const user = AuthManager.getCurrentUser();
        
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
            // Create order data matching backend CreateOrderRequest format exactly
            const orderData = {
                items: cart.map(item => ({
                    bookId: String(item.id), // Ensure string format
                    bookTitle: item.title,
                    bookAuthor: item.author,
                    bookIsbn: item.isbn || null,
                    bookImageUrl: item.imageUrl,
                    quantity: parseInt(item.quantity),
                    unitPrice: parseFloat(item.price),
                    discountAmount: 0.0
                })),
                shippingAddress: formData.get('address'),
                shippingCity: formData.get('city'),
                shippingState: formData.get('state') || '',
                shippingPostalCode: formData.get('zipCode'),
                shippingCountry: formData.get('country') || 'United States',
                paymentMethod: formData.get('paymentMethod') || 'CREDIT_CARD',
                customerEmail: user.email,
                customerPhone: formData.get('phone') || '',
                customerName: `${formData.get('firstName')} ${formData.get('lastName')}`,
                orderNotes: formData.get('notes') || ''
            };

            console.log('📦 Submitting order data:', orderData);

            // Submit order to backend
            const response = await APIService.order.create(orderData);

            console.log('✅ Order created successfully:', response);

            // Clear cart
            CartManager.clearCart();
            
            // Close modal and show success
            bootstrap.Modal.getInstance(document.querySelector('.modal')).hide();
            this.showOrderConfirmation(response);
            
        } catch (error) {
            console.error('❌ Order processing failed:', error);
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
            try {
                const statsResponse = await APIService.admin.getDashboardStats();
                console.log('📊 Stats response:', statsResponse);
                
                const stats = statsResponse.data || statsResponse;
                this.displayDashboardStats(stats);
            } catch (error) {
                console.error('❌ Error loading dashboard stats:', error);
                this.displayDashboardStats({});
            }
            
            // Load users
            try {
                const usersResponse = await APIService.admin.getUsers();
                console.log('👥 Users response:', usersResponse);
                
                // Handle different response structures
                let users = [];
                if (usersResponse && typeof usersResponse === 'object') {
                    if (Array.isArray(usersResponse)) {
                        users = usersResponse;
                    } else if (usersResponse.content && Array.isArray(usersResponse.content)) {
                        users = usersResponse.content;
                    } else if (usersResponse.data && Array.isArray(usersResponse.data)) {
                        users = usersResponse.data;
                    } else if (usersResponse.data && usersResponse.data.content && Array.isArray(usersResponse.data.content)) {
                        users = usersResponse.data.content;
                    }
                }
                
                console.log('👥 Processed users:', users);
                this.displayUsers(users);
            } catch (error) {
                console.error('❌ Error loading users:', error);
                this.displayUsers([]);
            }
            
            // Load sellers
            try {
                const sellersResponse = await APIService.admin.getSellers();
                console.log('🏪 Sellers response:', sellersResponse);
                
                // Handle different response structures
                let sellers = [];
                if (sellersResponse && typeof sellersResponse === 'object') {
                    if (Array.isArray(sellersResponse)) {
                        sellers = sellersResponse;
                    } else if (sellersResponse.content && Array.isArray(sellersResponse.content)) {
                        sellers = sellersResponse.content;
                    } else if (sellersResponse.data && Array.isArray(sellersResponse.data)) {
                        sellers = sellersResponse.data;
                    } else if (sellersResponse.data && sellersResponse.data.content && Array.isArray(sellersResponse.data.content)) {
                        sellers = sellersResponse.data.content;
                    }
                }
                
                console.log('🏪 Processed sellers:', sellers);
                this.displaySellers(sellers);
            } catch (error) {
                console.error('❌ Error loading sellers:', error);
                this.displaySellers([]);
            }
            
            // Load books
            try {
                const booksResponse = await APIService.books.getAll();
                console.log('📚 Books response:', booksResponse);
                
                // Handle different response structures
                let books = [];
                if (booksResponse && typeof booksResponse === 'object') {
                    if (Array.isArray(booksResponse)) {
                        books = booksResponse;
                    } else if (booksResponse.content && Array.isArray(booksResponse.content)) {
                        books = booksResponse.content;
                    } else if (booksResponse.data && Array.isArray(booksResponse.data)) {
                        books = booksResponse.data;
                    } else if (booksResponse.data && booksResponse.data.content && Array.isArray(booksResponse.data.content)) {
                        books = booksResponse.data.content;
                    }
                }
                
                console.log('📚 Processed books:', books);
                this.displayBooks(books);
            } catch (error) {
                console.error('❌ Error loading books:', error);
                this.displayBooks([]);
            }
            
            // Load orders if orders tab exists
            const ordersContainer = document.querySelector('#orders-tbody');
            if (ordersContainer) {
                try {
                    const ordersResponse = await APIService.order.admin.getAll();
                    console.log('📦 Orders response:', ordersResponse);
                    
                    // Handle different response structures
                    let orders = [];
                    if (ordersResponse && typeof ordersResponse === 'object') {
                        if (Array.isArray(ordersResponse)) {
                            orders = ordersResponse;
                        } else if (ordersResponse.content && Array.isArray(ordersResponse.content)) {
                            orders = ordersResponse.content;
                        } else if (ordersResponse.data && Array.isArray(ordersResponse.data)) {
                            orders = ordersResponse.data;
                        } else if (ordersResponse.data && ordersResponse.data.content && Array.isArray(ordersResponse.data.content)) {
                            orders = ordersResponse.data.content;
                        }
                    }
                    
                    console.log('📦 Processed orders:', orders);
                    this.displayOrders(orders);
                } catch (error) {
                    console.error('❌ Error loading orders:', error);
                    this.displayOrders([]);
                }
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

        // Handle cases where users might not be an array
        if (!Array.isArray(users)) {
            console.warn('Users data is not an array:', users);
            container.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center py-4">
                        <div class="text-muted">No users data available</div>
                    </td>
                </tr>
            `;
            return;
        }

        if (users.length === 0) {
            container.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center py-4">
                        <div class="text-muted">No users found</div>
                    </td>
                </tr>
            `;
            return;
        }

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

        // Handle cases where sellers might not be an array
        if (!Array.isArray(sellers)) {
            console.warn('Sellers data is not an array:', sellers);
            container.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center py-4">
                        <div class="text-muted">No sellers data available</div>
                    </td>
                </tr>
            `;
            return;
        }

        if (sellers.length === 0) {
            container.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center py-4">
                        <div class="text-muted">No sellers found</div>
                    </td>
                </tr>
            `;
            return;
        }

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

        // Handle cases where books might not be an array
        if (!Array.isArray(books)) {
            console.warn('Books data is not an array:', books);
            container.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center py-4">
                        <div class="text-muted">No books data available</div>
                    </td>
                </tr>
            `;
            return;
        }

        if (books.length === 0) {
            container.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center py-4">
                        <div class="text-muted">No books found</div>
                    </td>
                </tr>
            `;
            return;
        }

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

        // Ensure orders is an array
        if (!Array.isArray(orders)) {
            console.warn('Orders is not an array:', orders);
            container.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center py-4">
                        <i class="bi bi-exclamation-triangle display-4 text-muted mb-3"></i>
                        <p class="text-muted">Invalid orders data format</p>
                    </td>
                </tr>
            `;
            return;
        }

        if (orders.length === 0) {
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

// Seller Management - Real backend implementation
const SellerManager = {
    // Load seller dashboard with real backend data
    async loadSellerDashboard() {
        if (!this.isSeller()) {
            Utils.showError('Access denied. Seller privileges required.');
            return;
        }
        
        try {
            // Update seller name in the welcome badge
            const currentUser = AuthManager.getCurrentUser();
            const sellerNameElement = document.querySelector('#seller-name');
            if (sellerNameElement && currentUser) {
                sellerNameElement.textContent = currentUser.firstName || currentUser.name || 'Seller';
            }
            
            // Load seller's books
            const booksResponse = await APIService.seller.getMyBooks();
            const books = booksResponse.data || booksResponse;
            this.displaySellerBooks(books);
            
            // Load seller's orders
            const ordersResponse = await APIService.seller.getMyOrders();
            const orders = ordersResponse.data || ordersResponse;
            this.displaySellerOrders(orders);
            
            // Load seller stats
            const statsResponse = await APIService.seller.getDashboardStats();
            const stats = statsResponse.data || statsResponse;
            this.displaySellerStats(stats);
            
            // Initialize upload form
            this.initSellerUploadForm();
            
        } catch (error) {
            console.error('Failed to load seller dashboard:', error);
            Utils.showError('Failed to load seller dashboard. Please try again.');
        }
    },

    // Check if current user is seller
    isSeller() {
        const userRole = localStorage.getItem('bookvault_user_role');
        return userRole === 'SELLER' || userRole === 'ADMIN';
    },

    // Display seller's books
    displaySellerBooks(books) {
        const container = document.querySelector('#products tbody');
        if (!container) {
            console.error('Products table body not found');
            return;
        }

        if (!Array.isArray(books) || books.length === 0) {
            container.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center py-4">
                        <div class="text-muted">No books found. Upload your first book to get started!</div>
                    </td>
                </tr>
            `;
            return;
        }

        container.innerHTML = books.map(book => `
            <tr data-book-id="${book.id}">
                <td>
                    <img src="${book.coverImageUrl || book.imageUrl || '/asset/img/books/placeholder.jpg'}" 
                         class="rounded" style="width: 46px; height: 60px; object-fit: cover;" alt="${book.title}">
                </td>
                <td>${book.title}</td>
                <td>${book.author}</td>
                <td>${Utils.formatCurrency(book.price)}</td>
                <td>
                    <span class="badge bg-${book.isActive ? 'success' : 'warning'}">
                        ${book.isActive ? 'Active' : 'Paused'}
                    </span>
                </td>
                <td>
                    <div class="btn-group" role="group">
                        <button class="btn btn-sm btn-outline-warning" 
                                onclick="SellerManager.editBook('${book.id}')" 
                                title="Edit Book">
                            <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-sm btn-outline-danger" 
                                onclick="SellerManager.deleteBook('${book.id}')" 
                                title="Delete Book">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
    },

    // Display seller's orders
    displaySellerOrders(orders) {
        const container = document.querySelector('#sold tbody');
        if (!container) {
            console.error('Sold items table body not found');
            return;
        }

        if (!Array.isArray(orders) || orders.length === 0) {
            container.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center py-4">
                        <div class="text-muted">No orders found yet.</div>
                    </td>
                </tr>
            `;
            return;
        }

        container.innerHTML = orders.map(order => `
            <tr>
                <td>
                    <img src="${order.book?.coverImageUrl || order.book?.imageUrl || '/asset/img/books/placeholder.jpg'}" 
                         class="rounded" style="width: 46px; height: 60px;" alt="${order.book?.title || 'Book'}">
                </td>
                <td>${order.book?.title || 'N/A'}</td>
                <td>${order.buyer?.firstName || 'N/A'} ${order.buyer?.lastName || ''}</td>
                <td>${new Date(order.orderDate).toLocaleDateString()}</td>
                <td>${Utils.formatCurrency(order.totalAmount)}</td>
                <td>
                    <span class="badge bg-${this.getOrderStatusColor(order.status)}">
                        ${order.status}
                    </span>
                </td>
            </tr>
        `).join('');
    },

    // Display seller stats
    displaySellerStats(stats) {
        const statProducts = document.getElementById('statProducts');
        const statSold = document.getElementById('statSold');
        const statRevenue = document.getElementById('statRevenue');
        
        if (statProducts) statProducts.textContent = stats.totalBooks || 0;
        if (statSold) statSold.textContent = stats.totalSold || 0;
        if (statRevenue) statRevenue.textContent = Utils.formatCurrency(stats.totalRevenue || 0);
    },

    // Initialize seller upload form
    initSellerUploadForm() {
        const uploadForm = document.querySelector('.upload-book-form');
        if (uploadForm) {
            // Check if event listener is already attached to prevent duplicates
            if (uploadForm.hasAttribute('data-upload-initialized')) {
                console.log('Upload form already initialized, skipping...');
                return;
            }
            
            uploadForm.addEventListener('submit', this.handleBookUpload.bind(this));
            uploadForm.setAttribute('data-upload-initialized', 'true');
            console.log('✅ Upload form initialized');
            
            // Load categories into the dropdown
            this.loadCategoriesIntoDropdown();
        }
    },

    // Load categories into the dropdown
    async loadCategoriesIntoDropdown() {
        try {
            console.log('📚 Loading categories for dropdown...');
            const response = await APIService.books.getCategories();
            const categories = response.data || response;
            
            const categoryDropdown = document.getElementById('bookGenre');
            if (!categoryDropdown) {
                console.error('Category dropdown not found');
                return;
            }
            
            // Clear existing options except the first one
            categoryDropdown.innerHTML = '<option value="">Select a category...</option>';
            
            if (Array.isArray(categories) && categories.length > 0) {
                categories.forEach(category => {
                    if (category.isActive !== false) { // Only show active categories
                        const option = document.createElement('option');
                        option.value = category.id;
                        option.textContent = category.name;
                        categoryDropdown.appendChild(option);
                    }
                });
                console.log(`✅ Loaded ${categories.length} categories into dropdown`);
            } else {
                console.warn('No categories found or invalid response format');
            }
        } catch (error) {
            console.error('❌ Error loading categories:', error);
            // Add fallback options if API fails
            const categoryDropdown = document.getElementById('bookGenre');
            if (categoryDropdown) {
                categoryDropdown.innerHTML = `
                    <option value="">Select a category...</option>
                    <option value="fiction">Fiction</option>
                    <option value="nonfiction">Nonfiction</option>
                    <option value="business">Business</option>
                    <option value="technology">Technology</option>
                    <option value="biography">Biography</option>
                `;
            }
        }
    },

    // Handle book upload
    async handleBookUpload(event) {
        event.preventDefault();
        
        const form = event.target;
        
        // Prevent multiple simultaneous submissions
        if (form.hasAttribute('data-uploading')) {
            console.log('Upload already in progress, ignoring duplicate submission');
            return;
        }
        
        // Mark form as uploading
        form.setAttribute('data-uploading', 'true');
        const submitButton = form.querySelector('button[type="submit"]');
        const originalText = submitButton.textContent;
        submitButton.textContent = 'Uploading...';
        submitButton.disabled = true;
        
        // Debug: Log form elements
        console.log('Form elements:', {
            bookTitle: form.bookTitle?.value,
            bookAuthor: form.bookAuthor?.value,
            bookPrice: form.bookPrice?.value,
            bookDesc: form.bookDesc?.value,
            bookStock: form.bookStock?.value,
            bookCoverImage: form.bookCoverImage?.value,
            bookGenre: form.bookGenre?.value,
            bookGenreText: form.bookGenre?.options[form.bookGenre?.selectedIndex]?.textContent
        });
        
        // Validate required fields first
        if (!form.bookTitle.value.trim() || !form.bookAuthor.value.trim() || 
            !form.bookPrice.value || !form.bookDesc.value.trim() || !form.bookGenre.value) {
            Utils.showError('Please fill in all required fields (Title, Author, Price, Description, Category).');
            return;
        }
        
        if (parseFloat(form.bookPrice.value) <= 0) {
            Utils.showError('Price must be greater than 0.');
            return;
        }
        
        if (parseInt(form.bookStock?.value || 1) <= 0) {
            Utils.showError('Stock quantity must be greater than 0.');
            return;
        }

        try {
            // Create FormData to send file directly
            const formData = new FormData();
            formData.append('title', form.bookTitle.value.trim());
            formData.append('author', form.bookAuthor.value.trim());
            formData.append('price', form.bookPrice.value);
            formData.append('description', form.bookDesc.value.trim());
            formData.append('stockQuantity', form.bookStock?.value || 1);
            
            // Get the selected category ID and name
            const selectedCategoryId = form.bookGenre.value;
            const selectedCategoryOption = form.bookGenre.options[form.bookGenre.selectedIndex];
            const selectedCategoryName = selectedCategoryOption.textContent;
            
            // Send both category ID and name for backend compatibility
            formData.append('categoryIds', selectedCategoryId);
            formData.append('categoryNames', selectedCategoryName);
            
            // Add image file if selected (no base64 conversion needed)
            const imageFile = form.bookCoverImage.files[0];
            if (imageFile) {
                formData.append('coverImage', imageFile);
            }

            // Debug: Log the form data
            console.log('FormData to send:', {
                title: formData.get('title'),
                author: formData.get('author'),
                price: formData.get('price'),
                description: formData.get('description'),
                stockQuantity: formData.get('stockQuantity'),
                categoryIds: formData.get('categoryIds'),
                categoryNames: formData.get('categoryNames'),
                hasImage: !!imageFile
            });

            const response = await APIService.seller.createBookWithFile(formData);
            Utils.showSuccess('Book uploaded successfully!');
            
            // Reset form
            form.reset();
            
            // Clear image preview
            const imagePreview = document.getElementById('imagePreview');
            if (imagePreview) imagePreview.style.display = 'none';
            
            // Reload seller dashboard
            this.loadSellerDashboard();
            
        } catch (error) {
            console.error('Error uploading book:', error);
            Utils.showError('Failed to upload book. Please try again.');
        } finally {
            // Reset form state
            form.removeAttribute('data-uploading');
            submitButton.textContent = originalText;
            submitButton.disabled = false;
        }
    },

    // Edit book
    async editBook(bookId) {
        try {
            const response = await APIService.books.getById(bookId);
            const book = response.data || response;
            
            // Open edit modal with book data
            this.openEditBookModal(book);
            
        } catch (error) {
            console.error('Error loading book for edit:', error);
            Utils.showError('Failed to load book details.');
        }
    },

    // Open edit book modal
    openEditBookModal(book) {
        console.log('Opening edit modal for book:', book);
        
        // Populate the edit form
        document.getElementById('editBookId').value = book.id;
        document.getElementById('editBookTitle').value = book.title;
        document.getElementById('editBookAuthor').value = book.author;
        document.getElementById('editBookPrice').value = book.price;
        document.getElementById('editBookStock').value = book.stockQuantity || 1;
        document.getElementById('editBookDesc').value = book.description;
        
        // Set category if available
        if (book.categories && book.categories.length > 0) {
            document.getElementById('editBookGenre').value = book.categories[0].name;
        }
        
        // Set current cover image
        const currentCover = document.getElementById('currentBookCover');
        if (book.coverImageUrl) {
            currentCover.src = book.coverImageUrl;
            currentCover.style.display = 'block';
        } else {
            currentCover.style.display = 'none';
        }
        
        // Clear any previous edit image preview
        const editImagePreview = document.getElementById('editImagePreview');
        if (editImagePreview) editImagePreview.style.display = 'none';
        
        // Show the modal
        const modal = new bootstrap.Modal(document.getElementById('editBookModal'));
        modal.show();
    },

    // Handle edit book submission
    async handleEditBook(bookId, bookData) {
        // If no parameters provided, get data from form
        if (!bookId || !bookData) {
            bookId = document.getElementById('editBookId').value;
            
            // Check if there's a file upload
            const imageFile = document.getElementById('editBookCoverImage').files[0];
            
            if (imageFile) {
                // Use FormData for file upload
                const formData = new FormData();
                formData.append('title', document.getElementById('editBookTitle').value.trim());
                formData.append('author', document.getElementById('editBookAuthor').value.trim());
                formData.append('price', document.getElementById('editBookPrice').value);
                formData.append('description', document.getElementById('editBookDesc').value.trim());
                formData.append('stockQuantity', document.getElementById('editBookStock').value || 1);
                formData.append('categoryNames', document.getElementById('editBookGenre').value ? document.getElementById('editBookGenre').value.trim() : 'General');
                formData.append('coverImage', imageFile);

                // Validate required fields
                if (!formData.get('title') || !formData.get('author') || !formData.get('price') || !formData.get('description')) {
                    Utils.showError('Please fill in all required fields (Title, Author, Price, Description).');
                    return;
                }

                try {
                    Utils.showLoading('Updating book...');
                    
                    // Use the file upload endpoint
                    let response = await APIService.seller.updateBookWithFile(bookId, formData);
                    
                    if (response.success) {
                        Utils.showSuccess('Book updated successfully!');
                        // Close modal
                        const modal = bootstrap.Modal.getInstance(document.getElementById('editBookModal'));
                        modal.hide();
                        // Reload seller dashboard
                        this.loadSellerDashboard();
                    } else {
                        Utils.showError(response.message || 'Failed to update book');
                    }
                } catch (error) {
                    console.error('Error updating book:', error);
                    Utils.showError('Failed to update book. Please try again.');
                } finally {
                    Utils.hideLoading();
                }
            } else {
                // No file upload, use regular JSON update
                bookData = {
                    title: document.getElementById('editBookTitle').value.trim(),
                    author: document.getElementById('editBookAuthor').value.trim(),
                    price: parseFloat(document.getElementById('editBookPrice').value).toString(),
                    description: document.getElementById('editBookDesc').value.trim(),
                    stockQuantity: parseInt(document.getElementById('editBookStock').value || 1),
                    language: null,
                    pageCount: null,
                    publisher: null
                };

                // Handle uploaded image data - we'll send the file directly, not base64
                bookData.coverImageUrl = null;

                // Validate required fields
                if (!bookData.title || !bookData.author || !bookData.price || !bookData.description) {
                    Utils.showError('Please fill in all required fields (Title, Author, Price, Description).');
                    return;
                }

                try {
                    Utils.showLoading('Updating book...');
                    
                    // Try the API call first
                    let response = await APIService.seller.updateBook(bookId, bookData);
                    
                    if (response.success) {
                        Utils.showSuccess('Book updated successfully!');
                        // Close modal
                        const modal = bootstrap.Modal.getInstance(document.getElementById('editBookModal'));
                        modal.hide();
                        // Reload seller dashboard
                        this.loadSellerDashboard();
                    } else {
                        Utils.showError(response.message || 'Failed to update book');
                    }
                } catch (error) {
                    console.error('Error updating book:', error);
                    Utils.showError('Failed to update book. Please try again.');
                } finally {
                    Utils.hideLoading();
                }
            }
        } else {
            // Parameters provided, use regular update
            try {
                Utils.showLoading('Updating book...');
                
                // Try the API call first
                let response = await APIService.seller.updateBook(bookId, bookData);
                
                if (response.success) {
                    Utils.showSuccess('Book updated successfully!');
                    // Close modal
                    const modal = bootstrap.Modal.getInstance(document.getElementById('editBookModal'));
                    modal.hide();
                    // Reload seller dashboard
                    this.loadSellerDashboard();
                } else {
                    Utils.showError(response.message || 'Failed to update book');
                }
            } catch (error) {
                console.error('Error updating book:', error);
                Utils.showError('Failed to update book. Please try again.');
            } finally {
                Utils.hideLoading();
            }
        }
    },

    // Delete book
    async deleteBook(bookId) {
        if (!confirm('Are you sure you want to delete this book?')) {
            return;
        }

        try {
            await APIService.seller.deleteBook(bookId);
            Utils.showSuccess('Book deleted successfully!');
            
            // Reload seller dashboard
            this.loadSellerDashboard();
            
        } catch (error) {
            console.error('Error deleting book:', error);
            Utils.showError('Failed to delete book.');
        }
    },

    // Get order status color
    getOrderStatusColor(status) {
        switch (status?.toUpperCase()) {
            case 'PENDING': return 'warning';
            case 'CONFIRMED': return 'info';
            case 'SHIPPED': return 'primary';
            case 'DELIVERED': return 'success';
            case 'CANCELLED': return 'danger';
            default: return 'secondary';
        }
    }
};

// Global function for pagination
window.loadBooksPage = function(containerId, category, page) {
    console.log(`🔄 loadBooksPage called: container=${containerId}, category=${category}, page=${page}`);
    
    // Convert 'null' string to null
    const actualCategory = category === 'null' || category === null ? null : category;
    
    if (window.BookManager) {
        console.log('✅ BookManager found, calling loadBooks');
        window.BookManager.loadBooks(containerId, actualCategory, page);
    } else {
        console.error('❌ BookManager not available');
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
    },

    // Show info message
    showInfo: (message, containerId = 'info-container') => {
        const container = document.getElementById(containerId) || document.body;
        
        const alert = document.createElement('div');
        alert.className = 'alert alert-info alert-dismissible fade show position-fixed';
        alert.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
        alert.innerHTML = `
            <i class="bi bi-info-circle me-2"></i>${message}
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
            if (endpoint.includes('/auth')) {
                baseUrl = CONFIG.AUTH_SERVICE_URL;
                console.log('🔐 Using AUTH service for:', endpoint);
            } else if (endpoint.includes('/books') || endpoint.includes('/orders')) {
                baseUrl = CONFIG.BOOK_SERVICE_URL;  // Orders are now handled by book service
                console.log('📚 Using BOOK service for:', endpoint);
            } else if (endpoint.includes('/admin/users') || endpoint.includes('/admin/sellers') || endpoint.includes('/admin/regular-users')) {
                baseUrl = CONFIG.AUTH_SERVICE_URL;  // User admin endpoints go to auth service
                console.log('🔐 Using AUTH service for user admin:', endpoint);
            } else if (endpoint.includes('/admin/orders') || endpoint.includes('/admin/books')) {
                baseUrl = CONFIG.BOOK_SERVICE_URL;  // Order and book admin endpoints go to book service
                console.log('📚 Using BOOK service for order/book admin:', endpoint);
            } else {
                baseUrl = CONFIG.AUTH_SERVICE_URL;  // Default to auth service
                console.log('🔧 Using AUTH service (default) for:', endpoint);
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
        } else {
            console.warn('⚠️ No auth token found for authenticated request');
        }
        
        // For FormData requests, don't set Content-Type - let browser handle it
        if (options.body instanceof FormData) {
            delete headers['Content-Type'];
        }
        
        const requestOptions = {
            ...options,
            headers
        };
        

        

        
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
        }),
        toggleSellerStatus: (sellerId) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ADMIN.SELLERS}/${sellerId}/toggle`, { method: 'PATCH' }),
        deleteSeller: (sellerId) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ADMIN.SELLERS}/${sellerId}`, { method: 'DELETE' })
    },

    // Seller API calls - Real backend implementation
    seller: {
        getMyBooks: () => {
            const currentUser = AuthManager.getCurrentUser();
            const sellerId = currentUser?.id || currentUser?.userId;
            return APIService.makeRequest(`/books/seller/${sellerId}`);
        },
        createBook: (bookData) => {
            // Debug authentication
            const token = localStorage.getItem('bookvault_auth_token');
            const userRole = localStorage.getItem('bookvault_user_role');
            const currentUser = AuthManager.getCurrentUser();
            
            console.log('🔐 Book Upload Debug:', {
                token: token ? 'Present' : 'Missing',
                userRole: userRole,
                currentUser: currentUser,
                bookData: bookData
            });
            
            return APIService.makeRequest(CONFIG.ENDPOINTS.SELLER.CREATE_BOOK, { 
                method: 'POST', 
                body: JSON.stringify(bookData) 
            });
        },
        createBookWithFile: (formData) => {
            // Debug authentication
            const token = localStorage.getItem('bookvault_auth_token');
            const userRole = localStorage.getItem('bookvault_user_role');
            const currentUser = AuthManager.getCurrentUser();
            
            console.log('🔐 Book Upload with File Debug:', {
                token: token ? 'Present' : 'Missing',
                userRole: userRole,
                currentUser: currentUser,
                hasImage: formData.has('coverImage')
            });
            
            return APIService.makeRequest(`${CONFIG.ENDPOINTS.SELLER.CREATE_BOOK}/upload`, { 
                method: 'POST', 
                body: formData
                // Don't set headers - let makeRequest handle Authorization and Content-Type
            });
        },
        updateBook: (bookId, bookData) => {
            console.log('🔍 Debug: Sending book data for update:', JSON.stringify(bookData, null, 2));
            return APIService.makeRequest(`${CONFIG.ENDPOINTS.SELLER.UPDATE_BOOK}/${bookId}`, { 
                method: 'PUT', 
                body: JSON.stringify(bookData) 
            });
        },
        updateBookWithFile: (bookId, formData) => {
            console.log('🔍 Debug: Sending book data with file for update');
            return APIService.makeRequest(`${CONFIG.ENDPOINTS.SELLER.UPDATE_BOOK}/${bookId}/upload`, { 
                method: 'PUT', 
                body: formData
                // Don't set headers - let makeRequest handle Authorization and Content-Type
            });
        },
        deleteBook: (bookId) => APIService.makeRequest(`${CONFIG.ENDPOINTS.SELLER.DELETE_BOOK}/${bookId}`, { method: 'DELETE' }),
        getMyOrders: () => {
            // For now, return empty array since backend doesn't have this endpoint yet
            return Promise.resolve({
                data: []
            });
        },
        getDashboardStats: () => {
            // For now, return mock stats since backend doesn't have this endpoint yet
            return Promise.resolve({
                data: {
                    totalBooks: 0,
                    totalSold: 0,
                    totalRevenue: 0
                }
            });
        }
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
        create: (orderData) => {
            const userId = AuthManager.getCurrentUser()?.userId || AuthManager.getCurrentUser()?.id;
            return APIService.makeRequest(`${CONFIG.ENDPOINTS.ORDERS.BASE}`, {
                method: 'POST',
                headers: { 'X-User-Id': userId },
                body: JSON.stringify(orderData)
            });
        },
        getById: (orderId) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ORDERS.BASE}/${orderId}`),
        getByUser: (userId, page = 0, size = 10) => {
            // Use the correct endpoint that the backend actually provides
            return APIService.makeRequest(`${CONFIG.ENDPOINTS.ORDERS.BASE}/my-orders`, {
                headers: { 'X-User-Id': userId }
            });
        },
        getByUserPaged: (userId, page = 0, size = 10) => {
            // Use the paged version
            return APIService.makeRequest(`${CONFIG.ENDPOINTS.ORDERS.BASE}/my-orders/paged?page=${page}&size=${size}`, {
                headers: { 'X-User-Id': userId }
            });
        },
        updateStatus: (orderId, status) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ORDERS.BASE}/${orderId}/status`, {
            method: 'PUT',
            body: JSON.stringify({ status })
        }),
        cancel: (orderId, reason = "User requested cancellation") => APIService.makeRequest(`${CONFIG.ENDPOINTS.ORDERS.BASE}/${orderId}/cancel`, {
            method: 'PUT',
            body: JSON.stringify({ reason })
        }),
        track: (orderId) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ORDERS.BASE}/${orderId}/tracking`),
        // Admin order methods
        admin: {
            getAll: (page = 0, size = 10) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ORDERS.BASE}/admin/all?page=${page}&size=${size}`),
            getStats: () => APIService.makeRequest(`${CONFIG.ENDPOINTS.ORDERS.BASE}/admin/statistics`),
            getByStatus: (status, page = 0, size = 10) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ORDERS.BASE}/admin/status/${status}?page=${page}&size=${size}`),
            updateStatus: (orderId, status) => APIService.makeRequest(`${CONFIG.ENDPOINTS.ORDERS.BASE}/${orderId}/status`, {
                method: 'PUT',
                body: JSON.stringify({ status })
            })
        }
    }
};

// Book Management - Updated for Real Backend
// Make BookManager globally available
window.BookManager = {
    // Load books on listing page with pagination support
    async loadBooks(containerId = 'books-container', category = null, page = 0, size = 12) {
        console.log(`📚 Loading books: page=${page}, category=${category}`);
        Utils.showLoading(containerId);
        
        try {
            const response = category && category !== 'null' && category !== null
                ? await APIService.books.getByCategory(category, page, size)
                : await APIService.books.getAll(page, size);
            
            console.log('📚 API Response:', response);
            
            // Handle response structure from backend API
            const responseData = response.data || response;
            const books = responseData.content || responseData;
            
            console.log('📚 Processed data:', {
                responseData: responseData,
                books: books,
                booksLength: books ? books.length : 'undefined',
                totalPages: responseData.totalPages,
                currentPage: responseData.page
            });
            
            this.displayBooks(books, containerId);
            
            // Display pagination if available - use responseData which contains the pagination info
            if (responseData.totalPages && responseData.totalPages > 1) {
                this.displayPagination(responseData, containerId + '-pagination', category);
            }
        } catch (error) {
            console.error('❌ Error loading books:', error);
            Utils.showError('Failed to load books. Please try again later.');
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
                                <button class="btn btn-outline-warning btn-sm" onclick="window.BookManager.addToWishlist('${book.id}')" title="Add to Wishlist">
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
        console.log('📄 Display pagination called with:', { response, containerId, category });
        const container = document.getElementById(containerId);
        if (!container) return;

        const { page, totalPages, first, last } = response;
        console.log('📄 Pagination data:', { page, totalPages, first, last });
        
        // Only show pagination if there are multiple pages
        if (totalPages <= 1) {
            console.log('📄 No pagination needed - only one page or less');
            container.innerHTML = '';
            return;
        }
        
        let paginationHTML = '<nav><ul class="pagination justify-content-center">';
        
        // Previous button
        paginationHTML += `<li class="page-item ${first ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="loadBooksPage('books-container', ${category ? `'${category}'` : 'null'}, ${page - 1})">Previous</a>
        </li>`;
        
        // Page numbers
        for (let i = Math.max(0, page - 2); i <= Math.min(totalPages - 1, page + 2); i++) {
            paginationHTML += `<li class="page-item ${i === page ? 'active' : ''}">
                <a class="page-link" href="#" onclick="loadBooksPage('books-container', ${category ? `'${category}'` : 'null'}, ${i})">${i + 1}</a>
            </li>`;
        }
        
        // Next button
        paginationHTML += `<li class="page-item ${last ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="loadBooksPage('books-container', ${category ? `'${category}'` : 'null'}, ${page + 1})">Next</a>
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
            
            // Clear pagination when search is performed (since we're showing all search results)
            const paginationContainer = document.getElementById('books-container-pagination');
            if (paginationContainer) {
                paginationContainer.innerHTML = '';
            }
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
                console.log('🔍 Filtering book:', book.title, 'Author:', book.author, 'Genre:', book.category || book.genre);
                
                // Genre filter
                if (filters.genres && filters.genres.length > 0) {
                    // Handle categories array structure
                    const bookCategories = book.categories || [];
                    const categoryNames = bookCategories.map(cat => cat.name || '').filter(name => name.length > 0);
                    
                    const hasMatchingGenre = filters.genres.some(genre => 
                        categoryNames.some(categoryName => 
                            categoryName.toLowerCase().includes(genre.toLowerCase())
                        )
                    );
                    
                    console.log('🔍 Genre check:', categoryNames, 'vs', filters.genres, 'Match:', hasMatchingGenre);
                    if (!hasMatchingGenre) return false;
                }

                // Author filter
                if (filters.author) {
                    const bookAuthor = book.author || '';
                    const authorMatch = bookAuthor.toLowerCase().includes(filters.author.toLowerCase());
                    console.log('🔍 Author check:', bookAuthor, 'vs', filters.author, 'Match:', authorMatch);
                    if (!authorMatch) return false;
                }

                // Price filter
                if (filters.maxPrice !== undefined && filters.maxPrice < 100) {
                    const bookPrice = parseFloat(book.price) || 0;
                    const priceMatch = bookPrice <= filters.maxPrice;
                    console.log('🔍 Price check:', bookPrice, 'vs', filters.maxPrice, 'Match:', priceMatch);
                    if (!priceMatch) return false;
                }

                // Rating filter
                if (filters.minRating > 0) {
                    const bookRating = parseFloat(book.rating) || 0;
                    const ratingMatch = bookRating >= filters.minRating;
                    console.log('🔍 Rating check:', bookRating, 'vs', filters.minRating, 'Match:', ratingMatch);
                    if (!ratingMatch) return false;
                }

                console.log('✅ Book passed all filters:', book.title);
                return true;
            });

            console.log(`✅ Filtered ${filteredBooks.length} books from ${books.length} total`);

            // Hide loading
            if (loadingElement) loadingElement.style.display = 'none';

            // Display filtered results
            this.displayBooks(filteredBooks, containerId);

            // Clear pagination when filters are applied (since we're showing all filtered results)
            const paginationContainer = document.getElementById('books-container-pagination');
            if (paginationContainer) {
                paginationContainer.innerHTML = '';
            }

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
                    <button class="btn btn-sm btn-outline-secondary" id="clear-filters-btn">
                        <i class="bi bi-x-circle me-1"></i>Clear Filters
                    </button>
                </div>
            `;
            
            // Add event listener to the clear filters button
            const clearButton = summary.querySelector('#clear-filters-btn');
            if (clearButton) {
                clearButton.addEventListener('click', () => {
                    this.clearAllFilters();
                });
            }
            
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
    },

    // Clear all filters and reload books
    clearAllFilters() {
        console.log('🧹 Clearing all filters...');
        
        // Clear all checkboxes
        const checkboxes = document.querySelectorAll('.filters-sidebar input[type="checkbox"]');
        checkboxes.forEach(cb => cb.checked = false);
        
        // Clear radio buttons
        const radioButtons = document.querySelectorAll('.filters-sidebar input[type="radio"]');
        radioButtons.forEach(rb => rb.checked = false);
        
        // Reset price range
        const priceRange = document.getElementById('priceRange');
        if (priceRange) {
            priceRange.value = 100;
            const maxDisplay = document.getElementById('price-max-display');
            if (maxDisplay) {
                maxDisplay.textContent = '$100';
            }
        }
        
        // Clear author input
        const authorInput = document.getElementById('authorFilter');
        if (authorInput) {
            authorInput.value = '';
        }
        
        // Remove filter summary
        const existingSummary = document.getElementById('filter-summary');
        if (existingSummary) {
            existingSummary.remove();
        }
        
        // Clear pagination
        const paginationContainer = document.getElementById('books-container-pagination');
        if (paginationContainer) {
            paginationContainer.innerHTML = '';
        }
        
        // Reload books without filters
        BookManager.loadBooks('books-container');
        
        console.log('✅ All filters cleared and books reloaded');
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
            console.log('🔍 ViewOrder called with ID:', orderId);
            const currentUser = AuthManager.getCurrentUser();
            console.log('👤 Current user object:', currentUser);
            
            // Try different possible ID fields
            const userId = currentUser?.id || currentUser?.userId || currentUser?.sub;
            console.log('🔍 Extracted user ID:', userId);
            
            if (!userId) {
                console.warn('❌ No user ID found in user object');
                return;
            }
            
            console.log('📦 Fetching order details...');
            const response = await APIService.order.getById(orderId);
            const order = response.data || response; // Handle both response formats
            console.log('📦 Order data received:', order);
            
            const statusColor = this.getOrderStatusColor(order.status);
            const orderItems = order.orderItems || [];
            
            console.log('🎨 Creating modal with', orderItems.length, 'items');
            
            // Remove any existing order modal first
            const existingModal = document.getElementById('orderDetailsModal');
            if (existingModal) {
                existingModal.remove();
            }
            
            // Show order details in modal
            const modal = document.createElement('div');
            modal.id = 'orderDetailsModal';
            modal.className = 'modal fade';
            modal.setAttribute('tabindex', '-1');
            modal.innerHTML = `
                <div class="modal-dialog modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h5 class="modal-title">Order Details #${order.orderNumber || order.id.substring(0, 8)}</h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <strong>Order Date:</strong> ${new Date(order.createdAt).toLocaleDateString()}
                                </div>
                                <div class="col-md-6">
                                    <strong>Status:</strong> <span class="badge bg-${statusColor}">${order.status}</span>
                                </div>
                            </div>
                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <strong>Order Number:</strong> ${order.orderNumber || 'N/A'}
                                </div>
                                <div class="col-md-6">
                                    <strong>Payment Method:</strong> ${order.paymentMethodDisplayName || order.paymentMethod}
                                </div>
                            </div>
                            ${order.trackingNumber ? `
                                <div class="row mb-3">
                                    <div class="col-md-12">
                                        <strong>Tracking Number:</strong> ${order.trackingNumber}
                                    </div>
                                </div>
                            ` : ''}
                            
                            <h6 class="mt-4">Order Items:</h6>
                            <div class="order-items">
                                ${orderItems.map(item => `
                                    <div class="d-flex align-items-center gap-3 mb-3 p-3 border rounded">
                                        <img src="${item.bookImageUrl || '/asset/img/books/placeholder.jpg'}" 
                                             style="width: 60px; height: 80px; object-fit: cover;" 
                                             class="rounded" alt="${item.bookTitle}">
                                        <div class="flex-grow-1">
                                            <h6 class="mb-1">${item.bookTitle}</h6>
                                            <p class="text-muted mb-1 small">${item.bookAuthor || 'Unknown Author'}</p>
                                            <div class="small">
                                                <div>Quantity: <strong>${item.quantity}</strong></div>
                                                <div>Unit Price: <strong>$${parseFloat(item.unitPrice).toFixed(2)}</strong></div>
                                            </div>
                                        </div>
                                        <div class="text-end">
                                            <div class="fw-bold text-primary">$${parseFloat(item.totalPrice).toFixed(2)}</div>
                                        </div>
                                    </div>
                                `).join('')}
                            </div>
                            
                            ${order.fullShippingAddress ? `
                                <h6 class="mt-4">Shipping Address:</h6>
                                <div class="p-3 border rounded bg-light">
                                    <div><strong>${order.customerName}</strong></div>
                                    <div>${order.fullShippingAddress}</div>
                                    ${order.customerPhone ? `<div>Phone: ${order.customerPhone}</div>` : ''}
                                </div>
                            ` : ''}
                            
                            <div class="row mt-4">
                                <div class="col-md-6">
                                    ${order.orderNotes ? `
                                        <div>
                                            <strong>Order Notes:</strong>
                                            <p class="text-muted small">${order.orderNotes}</p>
                                        </div>
                                    ` : ''}
                                </div>
                                <div class="col-md-6">
                                    <div class="text-end">
                                        <div>Subtotal: $${parseFloat(order.totalAmount).toFixed(2)}</div>
                                        ${order.shippingCost ? `<div>Shipping: $${parseFloat(order.shippingCost).toFixed(2)}</div>` : ''}
                                        ${order.taxAmount && order.taxAmount > 0 ? `<div>Tax: $${parseFloat(order.taxAmount).toFixed(2)}</div>` : ''}
                                        <hr>
                                        <h5 class="text-primary">Total: $${parseFloat(order.finalAmount || order.totalAmount).toFixed(2)}</h5>
                                    </div>
                                </div>
                            </div>
                            
                            ${order.status === 'PENDING' ? `
                                <div class="mt-4 text-center">
                                    <button class="btn btn-outline-danger" onclick="UserManager.cancelOrder('${order.id}')">
                                        <i class="bi bi-x-circle"></i> Cancel Order
                                    </button>
                                </div>
                            ` : ''}
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                            <button type="button" class="btn btn-primary" onclick="UserManager.buyAgain('${order.id}')">
                                <i class="bi bi-arrow-repeat"></i> Buy Again
                            </button>
                        </div>
                    </div>
                </div>
            `;
            
            document.body.appendChild(modal);
            console.log('📝 Modal added to DOM');
            
            // Try multiple ways to show the modal
            try {
                if (typeof bootstrap !== 'undefined' && bootstrap.Modal) {
                    console.log('🚀 Showing modal with Bootstrap...');
                    const bsModal = new bootstrap.Modal(modal);
                    bsModal.show();
                    
                    modal.addEventListener('hidden.bs.modal', () => {
                        console.log('🗑️ Removing modal from DOM');
                        modal.remove();
                    });
                } else {
                    // Fallback: show modal manually
                    console.log('⚠️ Bootstrap not available, showing modal manually');
                    modal.style.display = 'block';
                    modal.classList.add('show');
                    document.body.classList.add('modal-open');
                    
                    // Add backdrop
                    const backdrop = document.createElement('div');
                    backdrop.className = 'modal-backdrop fade show';
                    document.body.appendChild(backdrop);
                    
                    // Handle close button
                    modal.querySelector('.btn-close').onclick = () => {
                        modal.style.display = 'none';
                        modal.classList.remove('show');
                        document.body.classList.remove('modal-open');
                        backdrop.remove();
                        modal.remove();
                    };
                }
            } catch (modalError) {
                console.error('❌ Error showing modal:', modalError);
                // Fallback to alert if modal fails
                alert(`Order Details:\n\nOrder: ${order.orderNumber}\nStatus: ${order.status}\nTotal: $${order.finalAmount}\nItems: ${orderItems.length}`);
            }
            
        } catch (error) {
            console.error('❌ Error loading order details:', error);
            if (typeof Utils !== 'undefined' && Utils.showError) {
                Utils.showError('Failed to load order details.');
            } else {
                alert('Failed to load order details. Please try again.');
            }
        }
    },
    
    // Cancel order
    async cancelOrder(orderId) {
        if (!confirm('Are you sure you want to cancel this order?')) return;
        
        try {
            await APIService.order.cancel(orderId);
            if (typeof Utils !== 'undefined' && Utils.showSuccess) {
                Utils.showSuccess('Order cancelled successfully.');
            }
            
            // Reload the orders if loadUserOrders function exists
            if (typeof loadUserOrders === 'function') {
                loadUserOrders();
            }
            
            // Close any open modals
            const openModal = document.querySelector('.modal.show');
            if (openModal && typeof bootstrap !== 'undefined') {
                bootstrap.Modal.getInstance(openModal).hide();
            }
        } catch (error) {
            console.error('Error cancelling order:', error);
            if (typeof Utils !== 'undefined' && Utils.showError) {
                Utils.showError('Failed to cancel order.');
            }
        }
    },

    // Buy again - Add all items from previous order to cart
    async buyAgain(orderId) {
        try {
            console.log('🔄 BuyAgain called with order ID:', orderId);
            const response = await APIService.order.getById(orderId);
            const order = response.data || response; // Handle both response formats
            console.log('📦 Order data for buy again:', order);
            
            const orderItems = order.orderItems || [];
            console.log('📦 Found', orderItems.length, 'items in order');
            
            if (orderItems.length === 0) {
                console.warn('❌ No order items found');
                if (typeof Utils !== 'undefined' && Utils.showError) {
                    Utils.showError('No items found in this order.');
                } else {
                    alert('No items found in this order.');
                }
                return;
            }

            let addedCount = 0;
            console.log('🛒 Adding items to cart...');
            
            for (const item of orderItems) {
                console.log('➕ Processing item:', item.bookTitle, 'x', item.quantity);
                
                // Create book object for cart
                const bookForCart = {
                    id: item.bookId,
                    title: item.bookTitle,
                    author: item.bookAuthor || 'Unknown Author',
                    price: parseFloat(item.unitPrice),
                    imageUrl: item.bookImageUrl || '/asset/img/books/placeholder.jpg'
                };
                
                console.log('📚 Book for cart:', bookForCart);
                
                // Add each item to cart with its original quantity
                if (typeof CartManager !== 'undefined' && CartManager.addToCart) {
                    try {
                        CartManager.addToCart(bookForCart, item.quantity);
                        addedCount += item.quantity;
                        console.log('✅ Added to cart:', item.bookTitle, 'x', item.quantity);
                    } catch (cartError) {
                        console.error('❌ Error adding to cart:', cartError);
                    }
                } else {
                    console.warn('⚠️ CartManager not available');
                }
            }
            
            console.log('🎉 Total items added to cart:', addedCount);
            
            if (addedCount > 0) {
                if (typeof Utils !== 'undefined' && Utils.showSuccess) {
                    Utils.showSuccess(`${addedCount} item(s) added to your cart!`);
                } else {
                    alert(`${addedCount} item(s) added to your cart!`);
                }
                
                // Close the modal if it's open
                const openModal = document.querySelector('.modal.show');
                if (openModal && typeof bootstrap !== 'undefined') {
                    const modalInstance = bootstrap.Modal.getInstance(openModal);
                    if (modalInstance) {
                        modalInstance.hide();
                    }
                }
            } else {
                if (typeof Utils !== 'undefined' && Utils.showError) {
                    Utils.showError('No items were added to cart.');
                } else {
                    alert('No items were added to cart.');
                }
            }
            
        } catch (error) {
            console.error('❌ Error processing buy again:', error);
            if (typeof Utils !== 'undefined' && Utils.showError) {
                Utils.showError('Failed to add items to cart.');
            } else {
                alert('Failed to add items to cart.');
            }
        }
    }
};

// Page Manager - Enhanced with E-commerce Features
const PageManager = {
    // Initialize the entire page management system
    init() {
        console.log('🚀 Initializing PageManager...');
        
        // Initialize navigation IMMEDIATELY to prevent flickering
        this.initImmediateNavigation();
        
        // Check if DOM is already loaded
        if (document.readyState === 'loading') {
            // DOM still loading, wait for it
            document.addEventListener('DOMContentLoaded', () => {
                this.performPageInitialization();
            });
        } else {
            // DOM already loaded, initialize immediately
            this.performPageInitialization();
        }
    },

    // Perform the actual page initialization
    performPageInitialization() {
        console.log('📄 DOM Ready - Initializing page:', this.getCurrentPage());
        
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
            case 'seller':
                this.initSellerPage();
                break;
            default:
                console.log('ℹ️ No specific initialization for page:', currentPage);
        }
        
        console.log('✅ PageManager initialization complete');
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
                
                // Update main navigation items based on role
                AuthManager.updateMainNavigationForRole(role);
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
        // ROBUST LOGOUT HANDLING - BOOTSTRAP DROPDOWN COMPATIBLE
        console.log('🎯 Initializing robust logout handlers...');
        
        // Method 1: Global event delegation with immediate handling
        document.addEventListener('click', (e) => {
            // Check if clicked element is logout button or contains logout button
            const logoutBtn = e.target.classList.contains('logout-btn') ? e.target : e.target.closest('.logout-btn');
            if (logoutBtn) {
                console.log('🚪 Logout button clicked (Method 1)!', logoutBtn);
                e.preventDefault();
                e.stopImmediatePropagation();
                e.stopPropagation();
                
                // Immediate logout without confirmation for testing
                this.performLogout();
                return false;
            }
        }, true); // Use capture phase

        // Method 2: Direct event binding with Bootstrap dropdown prevention
        // Simple, non-interfering logout button handler
        const bindLogoutEvents = () => {
            const logoutButtons = document.querySelectorAll('.logout-btn:not([data-logout-bound])');
            
            if (logoutButtons.length > 0) {
                console.log(`🔍 Found ${logoutButtons.length} unbound logout buttons for binding`);
                
                logoutButtons.forEach((btn, index) => {
                    console.log(`🔗 Binding events to logout button ${index + 1}:`, btn);
                    
                    // Simple click handler without cloning (which triggers MutationObserver)
                    btn.addEventListener('click', (e) => {
                        console.log(`🚪 Logout triggered via click!`);
                        e.preventDefault();
                        e.stopImmediatePropagation();
                        e.stopPropagation();
                        
                        // Close dropdown immediately
                        const dropdown = btn.closest('.dropdown');
                        if (dropdown) {
                            const dropdownToggle = dropdown.querySelector('[data-bs-toggle="dropdown"]');
                            if (dropdownToggle && bootstrap && bootstrap.Dropdown) {
                                const dropdownInstance = bootstrap.Dropdown.getInstance(dropdownToggle);
                                if (dropdownInstance) {
                                    dropdownInstance.hide();
                                }
                            }
                        }
                        
                        // Perform logout
                        this.performLogout();
                        return false;
                    }, { capture: true, passive: false });
                    
                    // Mark as bound to avoid re-binding
                    btn.setAttribute('data-logout-bound', 'true');
                    btn.href = 'javascript:void(0)';
                });
                
                console.log(`✅ Successfully bound ${logoutButtons.length} logout buttons`);
            }
        };

        // Initial binding only - no observers or intervals to prevent infinite loops
        setTimeout(bindLogoutEvents, 100);
        
        // Single fallback check after navigation updates
        setTimeout(bindLogoutEvents, 2000);

        // Note: Add-to-cart buttons are handled by specific event listeners in setupBookActions()
        // to ensure proper book data context. Global handler removed to prevent duplication.

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

        console.log('✅ All global event listeners initialized with robust logout handling');
    },

    // Centralized logout performance method
    performLogout() {
        console.log('🚪 Performing logout...');
        
        // Show immediate feedback
        const logoutButtons = document.querySelectorAll('.logout-btn');
        logoutButtons.forEach(btn => {
            btn.textContent = 'Logging out...';
            btn.style.pointerEvents = 'none';
            btn.style.opacity = '0.6';
        });
        
        // Use AuthManager logout method
        if (AuthManager && typeof AuthManager.logout === 'function') {
            console.log('🚪 Using AuthManager.logout()');
            AuthManager.logout();
        } else {
            console.log('🚪 AuthManager not available, performing manual logout');
            this.manualLogout();
        }
    },

    // Manual logout as fallback
    manualLogout() {
        console.log('🚪 Executing manual logout...');
        
        // Clear authentication data
        localStorage.removeItem('bookvault_auth_token');
        localStorage.removeItem('bookvault_user_role'); 
        localStorage.removeItem('bookvault_user_profile');
        console.log('🚪 Authentication data cleared from localStorage');
        
        // Clear cart
        if (window.CartManager) {
            CartManager.clearCart();
            console.log('🚪 Shopping cart cleared');
        }
        
        // Show success message
        alert('Successfully logged out! Redirecting to home page...');
        
        // Redirect to home page
        console.log('🚪 Redirecting to home page...');
        window.location.href = 'index.html';
    },
    
    // Force fix logout buttons as final fallback - SIMPLIFIED VERSION
    forceFixLogoutButtons() {
        const logoutButtons = document.querySelectorAll('.logout-btn');
        console.log(`🔧 Force fixing ${logoutButtons.length} logout buttons...`);
        
        logoutButtons.forEach((logoutBtn, index) => {
            console.log(`🔧 Fixing logout button ${index + 1}`);
            
            // Simply add a direct onclick handler as ultimate fallback
            logoutBtn.onclick = (e) => {
                e.preventDefault();
                e.stopPropagation();
                console.log('🚪 Force fixed onclick handler triggered!');
                this.performLogout();
                return false;
            };
            
            // Mark as force-fixed
            logoutBtn.setAttribute('data-force-fixed', 'true');
        });
        
        if (logoutButtons.length > 0) {
            console.log('✅ All logout buttons have been force-fixed with onclick handlers!');
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
        
        // Ensure CartManager and Utils are available
        if (typeof CartManager === 'undefined') {
            console.error('❌ CartManager not available');
            return;
        }
        
        if (typeof Utils === 'undefined') {
            console.error('❌ Utils not available');
            return;
        }
        
        // Wait for DOM to be fully ready, then initialize cart
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => {
                this.performCartInit();
            });
        } else {
            // DOM already loaded
            this.performCartInit();
        }
    },

    // Initialize seller page
    initSellerPage() {
        console.log('🏪 Initializing Seller Page...');
        SellerManager.loadSellerDashboard();
    },

    // Perform actual cart initialization
    performCartInit() {
        console.log('🚀 Performing cart initialization...');
        
        // Find cart container
        const container = document.getElementById('cart-container');
        if (!container) {
            console.error('❌ Cart container not found!');
            return;
        }
        
        console.log('✅ Cart container found, displaying cart...');
        
        try {
            // Display cart contents
            CartManager.displayCart('cart-container');
            console.log('🎉 Cart displayed successfully!');
        } catch (error) {
            console.error('❌ Error displaying cart:', error);
            // Show fallback error message
            container.innerHTML = `
                <div class="text-center py-5">
                    <i class="bi bi-exclamation-triangle display-4 text-warning mb-3"></i>
                    <h4>Unable to load cart</h4>
                    <p class="text-muted">There was an error loading your cart. Please try refreshing the page.</p>
                    <button class="btn bookvault-btn" onclick="location.reload()">Refresh Page</button>
                </div>
            `;
        }
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
            console.log('📦 Loading user orders...');
            const currentUser = AuthManager.getCurrentUser();
            console.log('👤 Current user:', currentUser);
            
            // Try different possible ID fields
            const userId = currentUser?.id || currentUser?.userId || currentUser?.sub || currentUser?.email;
            console.log('🔍 User ID for orders:', userId);
            
            if (!userId) {
                console.warn('❌ No user ID found, cannot load orders');
                const container = document.querySelector('#orders-table-body');
                if (container) {
                    container.innerHTML = `
                        <tr>
                            <td colspan="6" class="text-center py-4">
                                <i class="bi bi-exclamation-triangle display-4 text-warning mb-3"></i>
                                <p class="text-muted">Unable to load orders - please log in again</p>
                                <a href="login.html" class="btn bookvault-btn">Login</a>
                            </td>
                        </tr>
                    `;
                }
                return;
            }
            
            console.log('🌐 Making API call to get orders for user:', userId);
            const response = await APIService.order.getByUser(userId);
            console.log('📦 Orders API response:', response);
            
            const orders = response.content || response.data || response;
            console.log('📦 Processed orders:', orders);
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
            console.error('❌ Error loading orders:', error);
            const container = document.querySelector('#orders-table-body');
            if (container) {
                // Show different messages based on error type
                if (error.message.includes('network') || error.message.includes('fetch')) {
                    container.innerHTML = `
                        <tr>
                            <td colspan="6" class="text-center py-4">
                                <i class="bi bi-wifi-off display-4 text-muted mb-3"></i>
                                <p class="text-muted">Unable to connect to order service</p>
                                <p class="small text-muted">Error: ${error.message}</p>
                                <button class="btn btn-outline-primary" onclick="UserManager.loadUserOrders()">Retry</button>
                            </td>
                        </tr>
                    `;
                } else if (error.message.includes('404')) {
                    container.innerHTML = `
                        <tr>
                            <td colspan="6" class="text-center py-4">
                                <i class="bi bi-box display-4 text-muted mb-3"></i>
                                <p class="text-muted">No orders found</p>
                                <p class="small text-muted">Order service is not available or you have no orders yet.</p>
                                <a href="booklisting.html" class="btn bookvault-btn">Start Shopping</a>
                            </td>
                        </tr>
                    `;
                } else {
                    container.innerHTML = `
                        <tr>
                            <td colspan="6" class="text-center py-4">
                                <i class="bi bi-exclamation-circle display-4 text-warning mb-3"></i>
                                <p class="text-muted">Failed to load orders</p>
                                <p class="small text-muted">Error: ${error.message}</p>
                                <div class="mt-3">
                                    <button class="btn btn-outline-primary me-2" onclick="UserManager.loadUserOrders()">Retry</button>
                                    <a href="booklisting.html" class="btn btn-outline-secondary">Browse Books</a>
                                </div>
                            </td>
                        </tr>
                    `;
                }
            }
            
            // Also show a toast notification
            if (typeof Utils !== 'undefined' && Utils.showError) {
                Utils.showError('Failed to load purchase history. Please try again.');
            }
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
        console.log('🔧 Initializing filters...');
        
        // Set up event listeners for all filter inputs
        const filterInputs = document.querySelectorAll('.filters-sidebar input, .filters-sidebar select');
        console.log('🔧 Found filter inputs:', filterInputs.length);
        
        filterInputs.forEach(input => {
            console.log('🔧 Setting up listener for:', input.type, input.id || input.name);
            
            // For checkboxes and radio buttons, use 'change' event
            if (input.type === 'checkbox' || input.type === 'radio') {
                input.addEventListener('change', () => {
                    console.log('🔧 Filter changed:', input.type, input.id || input.name);
                    this.applyFilters();
                });
            }
            // For text inputs, use 'input' event for real-time filtering
            else if (input.type === 'text') {
                input.addEventListener('input', () => {
                    console.log('🔧 Text filter changed:', input.id || input.name, input.value);
                    this.applyFilters();
                });
            }
        });

        // Price range slider
        const priceRange = document.getElementById('priceRange');
        if (priceRange) {
            priceRange.addEventListener('input', (e) => {
                const maxDisplay = document.getElementById('price-max-display');
                if (maxDisplay) {
                    maxDisplay.textContent = `$${e.target.value}`;
                }
                console.log('🔧 Price range changed:', e.target.value);
                this.applyFilters();
            });
        }
        
        console.log('✅ Filters initialized');
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
        console.log('🔍 Found checked genre checkboxes:', genreCheckboxes.length);
        if (genreCheckboxes.length > 0) {
            filters.genres = Array.from(genreCheckboxes).map(cb => {
                const label = cb.closest('label');
                const text = label ? label.textContent.trim() : cb.nextElementSibling?.textContent?.trim() || '';
                console.log('🔍 Genre checkbox:', cb.id, 'Text:', text);
                return text;
            }).filter(text => text.length > 0);
            console.log('🔍 Selected genres:', filters.genres);
        }
        
        // Author filter
        const authorInput = document.getElementById('authorFilter');
        if (authorInput && authorInput.value.trim()) {
            filters.author = authorInput.value.trim();
            console.log('🔍 Author filter:', filters.author);
        }
        
        // Price filter
        const priceRange = document.getElementById('priceRange');
        if (priceRange) {
            filters.maxPrice = parseInt(priceRange.value);
            console.log('🔍 Price filter:', filters.maxPrice);
        }
        
        // Rating filter
        const ratingRadio = document.querySelector('.filters-sidebar input[name="rating"]:checked');
        if (ratingRadio) {
            filters.minRating = parseInt(ratingRadio.id.replace('r', ''));
            console.log('🔍 Rating filter:', filters.minRating);
        }

        console.log('🔍 Final filters object:', filters);
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

    // Handle add to cart with duplicate prevention
    handleAddToCart(button, book = null) {
        // Prevent duplicate calls within short timeframe
        const now = Date.now();
        const lastCall = button.dataset.lastAddToCartCall || 0;
        if (now - lastCall < 1000) { // 1 second cooldown
            console.log('🚫 Duplicate add-to-cart call prevented');
            return;
        }
        button.dataset.lastAddToCartCall = now;
        
        console.log('🛒 HandleAddToCart called', { button, book });
        console.log('🛒 CartManager available:', typeof CartManager);
        console.log('🛒 Utils available:', typeof Utils);
        
        let bookData = book;
        
        // Handle API response structure {success: true, data: {...}}
        if (book && book.success && book.data) {
            console.log('📦 Unwrapping API response structure...');
            bookData = book.data;
            console.log('📖 Extracted book data from API response:', bookData);
        } else if (!book) {
            // Fallback to page extraction
            console.log('📖 No book provided, extracting from page...');
            bookData = this.extractBookDataFromPage();
        }
        
        console.log('📖 Final book data for cart:', bookData);
        
        if (bookData && bookData.id && bookData.title && bookData.author && bookData.price != null) {
            console.log('✅ Adding book to cart...');
            try {
                CartManager.addToCart(bookData);
                console.log('🎉 Book successfully added to cart!');
                
                // Show notification
                if (CartManager.showCartNotification) {
                    CartManager.showCartNotification(`Added "${bookData.title}" to cart!`);
                } else {
                    console.log('📢 No CartManager.showCartNotification method found');
                    Utils.showSuccess(`Added "${bookData.title}" to cart!`);
                }
            } catch (error) {
                console.error('❌ Error adding book to cart:', error);
                Utils.showError('Unable to add book to cart. Please try again.');
            }
        } else {
            console.error('❌ Invalid book data for cart:', bookData);
            Utils.showError('Unable to add book to cart. Missing book information.');
        }
    },

    // Extract book data from current page
    extractBookDataFromPage() {
        console.log('📖 Extracting book data from page...');
        
        // First, try to get book ID from URL parameter
        const urlParams = new URLSearchParams(window.location.search);
        const urlBookId = urlParams.get('id');
        console.log('🔗 URL Book ID:', urlBookId);
        
        // Extract elements from page
        const titleElement = document.querySelector('.book-title-main');
        const authorElement = document.querySelector('.book-author-main');
        const priceElement = document.querySelector('.book-price-main');
        const imageElement = document.querySelector('.book-cover-main');
        
        console.log('🔍 Page elements found:');
        console.log('  - Title element:', !!titleElement, titleElement?.textContent?.trim());
        console.log('  - Author element:', !!authorElement, authorElement?.textContent?.trim());
        console.log('  - Price element:', !!priceElement, priceElement?.textContent?.trim());
        console.log('  - Image element:', !!imageElement, imageElement?.src);
        
        if (titleElement && authorElement && priceElement) {
            const title = titleElement.textContent.trim();
            const author = authorElement.textContent.trim();
            const priceText = priceElement.textContent.trim();
            
            // Better price extraction - handle currency symbols
            let price = 0;
            if (priceText) {
                // Remove currency symbols and non-numeric characters except dots
                const cleanPriceText = priceText.replace(/[$£€¥,\s]/g, '');
                price = parseFloat(cleanPriceText);
                // If still NaN, try to extract just numbers
                if (isNaN(price)) {
                    const numberMatch = priceText.match(/\d+\.?\d*/);
                    price = numberMatch ? parseFloat(numberMatch[0]) : 0;
                }
            }
            
            console.log('💰 Price extraction debug:', {
                original: priceText,
                extracted: price,
                isValid: !isNaN(price) && price > 0
            });
            
            // Validate extracted data
            if (!title || !author || isNaN(price) || price <= 0) {
                console.error('❌ Invalid extracted data:', { title, author, price });
                return null;
            }
            
            // Use URL ID if available, otherwise generate from title/author
            const bookId = urlBookId || this.generateBookId(title, author);
            
            const bookData = {
                id: bookId,
                title: title,
                author: author,
                price: price,
                imageUrl: imageElement ? imageElement.src : '/asset/img/books/placeholder.jpg'
            };
            
            console.log('✅ Successfully extracted book data:', bookData);
            return bookData;
        }
        
        console.error('❌ Unable to extract book data from page - missing required elements');
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
            console.log('📚 Loading book details for ID:', bookId);
            const book = await APIService.books.getById(bookId);
            console.log('📚 Book details loaded successfully:', book);
            
            this.displayBookDetails(book);
            this.setupBookActions(book);
        } catch (error) {
            console.error('❌ Error loading book details:', error);
            Utils.showError('Failed to load book details.');
            // Setup interactive buttons even if API fails
            this.setupBookActions();
        }
    },

    // Setup book action buttons with improved event management
    setupBookActions(book = null) {
        console.log('🔧 setupBookActions called with book:', book);
        console.log('🔧 Current DOM state:', document.readyState);
        
        // Wait a moment for DOM to be fully ready
        setTimeout(() => {
            // Add to Cart buttons - improved approach without cloning
            const addToCartBtns = document.querySelectorAll('.add-to-cart-btn:not([data-events-bound])');
            console.log(`🔘 Found ${addToCartBtns.length} unbound Add to Cart buttons`);
            
            addToCartBtns.forEach((btn, index) => {
                console.log(`🔧 Adding event listener to Add to Cart button ${index + 1}`);
                
                // Mark as bound to prevent duplicate binding
                btn.setAttribute('data-events-bound', 'true');
                
                btn.addEventListener('click', (e) => {
                    e.preventDefault();
                    e.stopPropagation(); // Prevent event bubbling
                    console.log('🛒 Add to Cart button clicked!', book);
                    this.handleAddToCart(btn, book);
                });
                
                // Visual feedback
                btn.style.cursor = 'pointer';
                btn.title = 'Add this book to your cart';
            });

            // Buy Now buttons - improved approach without cloning
            const buyNowBtns = document.querySelectorAll('.buy-now-btn:not([data-events-bound])');
            console.log(`🔘 Found ${buyNowBtns.length} unbound Buy Now buttons`);
            
            buyNowBtns.forEach((btn, index) => {
                console.log(`🔧 Adding event listener to Buy Now button ${index + 1}`);
                
                // Mark as bound to prevent duplicate binding
                btn.setAttribute('data-events-bound', 'true');
                
                btn.addEventListener('click', (e) => {
                    e.preventDefault();
                    e.stopPropagation(); // Prevent event bubbling
                    console.log('⚡ Buy Now button clicked!', book);
                    this.handleBuyNow(book);
                });
                
                // Visual feedback
                btn.style.cursor = 'pointer';
                btn.title = 'Buy this book now';
            });
            
            console.log('✅ setupBookActions completed successfully');
        }, 100);
    },

    // Handle buy now
    handleBuyNow(book = null) {
        console.log('⚡ HandleBuyNow called', { book });
        console.log('⚡ CartManager available:', typeof CartManager);
        console.log('⚡ CheckoutManager available:', typeof CheckoutManager);
        
        let bookData = book;
        
        // Handle API response structure {success: true, data: {...}}
        if (book && book.success && book.data) {
            console.log('📦 Unwrapping API response structure for buy now...');
            bookData = book.data;
            console.log('📖 Extracted book data from API response:', bookData);
        } else if (!book) {
            // Fallback to page extraction
            console.log('📖 No book provided, extracting from page...');
            bookData = this.extractBookDataFromPage();
        }
        
        console.log('📖 Final book data for buy now:', bookData);
        
        if (bookData && bookData.id && bookData.title && bookData.author && bookData.price != null) {
            console.log('✅ Adding book to cart and starting checkout...');
            try {
                CartManager.addToCart(bookData);
                console.log('🎉 Book added to cart, starting checkout...');
                CheckoutManager.startCheckout();
                console.log('🛒 Checkout started successfully!');
            } catch (error) {
                console.error('❌ Error processing buy now:', error);
                Utils.showError('Unable to process purchase. Please try again.');
            }
        } else {
            console.error('❌ Invalid book data for buy now:', bookData);
            Utils.showError('Unable to process purchase. Please try again.');
        }
    },

    // Display book details
    displayBookDetails(book) {
        console.log('📄 Displaying book details:', book);
        
        // Handle API response structure
        let bookData = book;
        if (book && book.success && book.data) {
            console.log('📦 Unwrapping API response for display...');
            bookData = book.data;
        }
        
        console.log('📄 Final book data for display:', bookData);
        
        const titleElement = document.querySelector('.book-title-main');
        const authorElement = document.querySelector('.book-author-main');
        const priceElement = document.querySelector('.book-price-main');
        const imageElement = document.querySelector('.book-cover-main');
        const descriptionElement = document.querySelector('.book-description');
        
        if (titleElement && bookData.title) {
            titleElement.textContent = bookData.title;
            console.log('📄 Updated title:', bookData.title);
        }
        if (authorElement && bookData.author) {
            authorElement.textContent = bookData.author;
            console.log('📄 Updated author:', bookData.author);
        }
        if (priceElement && bookData.price != null) {
            priceElement.textContent = Utils.formatCurrency(bookData.price);
            console.log('📄 Updated price:', bookData.price);
        }
        if (imageElement) {
            imageElement.src = bookData.imageUrl || '/asset/img/books/placeholder.jpg';
            console.log('📄 Updated image:', bookData.imageUrl);
        }
        if (descriptionElement && bookData.description) {
            descriptionElement.textContent = bookData.description;
            console.log('📄 Updated description:', bookData.description);
        }
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
    
    // IMMEDIATE LOGOUT FIX - Run before anything else
    console.log('🚨 IMMEDIATE LOGOUT FIX - Starting...');
    immediateLogoutFix();
    
    // Initialize the main page manager
    PageManager.init();
    
    console.log('✅ BookVault Application Started Successfully');
});

// IMMEDIATE LOGOUT FIX FUNCTION
function immediateLogoutFix() {
    console.log('🔧 Applying immediate logout fix...');
    
    // Function to aggressively fix logout buttons
    const aggressiveLogoutFix = () => {
        const logoutButtons = document.querySelectorAll('.logout-btn:not([data-immediate-fixed])');
        console.log(`🎯 IMMEDIATE FIX: Found ${logoutButtons.length} unfixed logout buttons`);
        
        if (logoutButtons.length === 0) {
            console.log('✅ All logout buttons already fixed, skipping...');
            return;
        }
        
        logoutButtons.forEach((btn, index) => {
            console.log(`🔧 IMMEDIATE FIX: Processing button ${index + 1}`, btn);
            
            // Check if already fixed to prevent infinite loop
            if (btn.hasAttribute('data-immediate-fixed')) {
                console.log('⏭️ Button already fixed, skipping...');
                return;
            }
            
            // Mark as fixed FIRST to prevent re-processing
            btn.setAttribute('data-immediate-fixed', 'true');
            
            // Completely override the href WITHOUT cloning (to avoid mutation observer loop)
            btn.removeAttribute('href');
            btn.href = 'javascript:void(0)';
            
            // Set direct onclick handler (most reliable)
            btn.onclick = function(e) {
                console.log('🚪 IMMEDIATE LOGOUT ONCLICK TRIGGERED!');
                e.preventDefault();
                e.stopPropagation();
                e.stopImmediatePropagation();
                
                // Immediate logout
                console.log('🚪 Executing immediate logout...');
                
                // Clear auth data
                localStorage.removeItem('bookvault_auth_token');
                localStorage.removeItem('bookvault_user_role'); 
                localStorage.removeItem('bookvault_user_profile');
                console.log('🚪 Auth data cleared');
                
                // Clear cart
                if (window.CartManager) {
                    CartManager.clearCart();
                    console.log('🚪 Cart cleared');
                }
                
                // Show immediate feedback with auto-dismiss
                showLogoutFeedback();
                
                // Redirect after 2 seconds
                setTimeout(() => {
                    window.location.href = 'index.html';
                }, 2000);
                
                return false;
            };
            
            // Set style
            btn.style.cursor = 'pointer';
            
            console.log(`✅ IMMEDIATE FIX: Button ${index + 1} fixed with onclick handler`);
        });
        
        if (logoutButtons.length > 0) {
            console.log('✅ IMMEDIATE LOGOUT FIX COMPLETED!');
        }
    };
    
    // Apply fix immediately
    aggressiveLogoutFix();
    
    // Apply fix again after a short delay (for dynamic content)
    setTimeout(aggressiveLogoutFix, 500);
    setTimeout(aggressiveLogoutFix, 1000);
    setTimeout(aggressiveLogoutFix, 2000);
    
    // Watch for new logout buttons (but ignore our own changes)
    const observer = new MutationObserver((mutations) => {
        let hasNewUnfixedLogoutButtons = false;
        mutations.forEach((mutation) => {
            if (mutation.type === 'childList') {
                mutation.addedNodes.forEach((node) => {
                    if (node.nodeType === Node.ELEMENT_NODE) {
                        // Only count as new if it's a logout button without our fix
                        if (node.classList && node.classList.contains('logout-btn') && 
                            !node.hasAttribute('data-immediate-fixed')) {
                            hasNewUnfixedLogoutButtons = true;
                        }
                        if (node.querySelectorAll) {
                            const childLogoutButtons = node.querySelectorAll('.logout-btn:not([data-immediate-fixed])');
                            if (childLogoutButtons.length > 0) {
                                hasNewUnfixedLogoutButtons = true;
                            }
                        }
                    }
                });
            }
        });
        
        if (hasNewUnfixedLogoutButtons) {
            console.log('🔄 IMMEDIATE FIX: New unfixed logout buttons detected, applying fix...');
            setTimeout(aggressiveLogoutFix, 100);
        }
    });
    
    observer.observe(document.body, { childList: true, subtree: true });
    
    console.log('🚨 IMMEDIATE LOGOUT FIX INITIALIZED');
}

// Show logout feedback with auto-dismiss
function showLogoutFeedback() {
    // Create toast notification
    const toast = document.createElement('div');
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #28a745;
        color: white;
        padding: 15px 25px;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.3);
        z-index: 10000;
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
        font-size: 14px;
        font-weight: 500;
        display: flex;
        align-items: center;
        gap: 10px;
        opacity: 0;
        transform: translateX(100%);
        transition: all 0.3s ease;
    `;
    
    toast.innerHTML = `
        <i class="bi bi-check-circle" style="font-size: 18px;"></i>
        <span>Logging out... Redirecting in 2 seconds</span>
    `;
    
    document.body.appendChild(toast);
    
    // Animate in
    requestAnimationFrame(() => {
        toast.style.opacity = '1';
        toast.style.transform = 'translateX(0)';
    });
    
    // Auto remove after 2.5 seconds
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 300);
    }, 2500);
}

// Export managers for global access
window.BookVault = {
    PageManager,
    AuthManager,
    CartManager,
    CheckoutManager,
    AdminManager,
    BookManager,
    UserManager,
    SellerManager,
    Utils
};

// Also expose PageManager directly for HTML onclick handlers
window.PageManager = PageManager;

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
        if (PageManager && PageManager.performLogout) {
            PageManager.performLogout();
        } else {
            AuthManager.logout();
        }
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
            console.log(`  - In dropdown:`, btn.closest('.dropdown-menu') !== null);
            console.log(`  - Has onclick:`, btn.onclick !== null);
            console.log(`  - Has data-logout-bound:`, btn.hasAttribute('data-logout-bound'));
            console.log(`  - Has data-force-fixed:`, btn.hasAttribute('data-force-fixed'));
        });
        return buttons;
    },
    
    simulateLogoutClick: () => {
        const buttons = document.querySelectorAll('.logout-btn');
        if (buttons.length > 0) {
            console.log('🧪 Simulating click on first logout button...');
            console.log('🧪 Button details:', buttons[0]);
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
    },
    
    testAllLogoutMethods: () => {
        console.log('🧪 Testing all logout methods...');
        
        // Test Method 1: Direct click simulation
        console.log('🧪 Testing Method 1: Direct click simulation');
        const buttons = document.querySelectorAll('.logout-btn');
        if (buttons.length > 0) {
            buttons[0].click();
        }
        
        setTimeout(() => {
            // Test Method 2: Keyboard shortcut
            console.log('🧪 Testing Method 2: Keyboard shortcut (Ctrl+Shift+L)');
            const event = new KeyboardEvent('keydown', {
                key: 'L',
                ctrlKey: true,
                shiftKey: true,
                bubbles: true
            });
            document.dispatchEvent(event);
        }, 1000);
        
        setTimeout(() => {
            // Test Method 3: Direct function call
            console.log('🧪 Testing Method 3: Direct function call');
            if (PageManager && PageManager.performLogout) {
                PageManager.performLogout();
            }
        }, 2000);
    },
    
    fixLogoutButtons: () => {
        console.log('🧪 Manually fixing logout buttons...');
        if (PageManager && PageManager.forceFixLogoutButtons) {
            PageManager.forceFixLogoutButtons();
        } else {
            console.log('❌ PageManager.forceFixLogoutButtons not available');
        }
    }
};

// GLOBAL IMMEDIATE LOGOUT FUNCTION - Call from console: testLogoutNow()
window.testLogoutNow = function() {
    console.log('🧪 TESTING LOGOUT NOW!');
    
    // Clear auth data immediately
    localStorage.removeItem('bookvault_auth_token');
    localStorage.removeItem('bookvault_user_role'); 
    localStorage.removeItem('bookvault_user_profile');
    console.log('🚪 Auth data cleared');
    
    // Clear cart
    if (window.CartManager) {
        CartManager.clearCart();
        console.log('🚪 Cart cleared');
    }
    
    showLogoutFeedback();
    setTimeout(() => {
        window.location.href = 'index.html';
    }, 2000);
}; 
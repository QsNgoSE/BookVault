package com.bookvault.book.service;

import com.bookvault.book.dto.*;
import com.bookvault.book.model.*;
import com.bookvault.book.repository.*;
import com.bookvault.shared.dto.PagedResponse;
import com.bookvault.shared.exception.BadRequestException;
import com.bookvault.shared.exception.NotFoundException;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * Simple Book Service - Core functionality only
 */
@Service
// @RequiredArgsConstructor
// @Slf4j
@Transactional
public class BookService {
    
    private static final Logger log = LoggerFactory.getLogger(BookService.class);
    
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final OrderItemRepository orderItemRepository;
    
    // Constructor (replacing @RequiredArgsConstructor)
    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository, OrderItemRepository orderItemRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.orderItemRepository = orderItemRepository;
    }
    
    // Get all books (paginated)
    @Transactional(readOnly = true)
    public PagedResponse<BookResponse> getAllBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findByIsActiveTrue(pageable);
        return mapToPagedResponse(books);
    }
    
    // Get book by ID
    @Transactional(readOnly = true)
    public BookResponse getBookById(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + id));
        return mapToResponse(book);
    }
    
    // Get book by ISBN
    @Transactional(readOnly = true)
    public BookResponse getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new NotFoundException("Book not found with ISBN: " + isbn));
        return mapToResponse(book);
    }
    
    // Search books
    @Transactional(readOnly = true)
    public PagedResponse<BookResponse> searchBooks(String query, Pageable pageable) {
        Page<Book> books = bookRepository.searchBooks(query, pageable);
        return mapToPagedResponse(books);
    }
    
    // Get books by category
    @Transactional(readOnly = true)
    public PagedResponse<BookResponse> getBooksByCategory(String categoryName, Pageable pageable) {
        Page<Book> books = bookRepository.findByCategory(categoryName, pageable);
        return mapToPagedResponse(books);
    }
    
    // Get books by author
    @Transactional(readOnly = true)
    public PagedResponse<BookResponse> getBooksByAuthor(String author, Pageable pageable) {
        Page<Book> books = bookRepository.findByAuthorContainingIgnoreCaseAndIsActiveTrue(author, pageable);
        return mapToPagedResponse(books);
    }
    
    // Get featured books
    @Transactional(readOnly = true)
    public PagedResponse<BookResponse> getFeaturedBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findFeaturedBooks(pageable);
        return mapToPagedResponse(books);
    }
    
    // Get top rated books
    @Transactional(readOnly = true)
    public PagedResponse<BookResponse> getTopRatedBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findTopRatedBooks(pageable);
        return mapToPagedResponse(books);
    }
    
    // Get newest books
    @Transactional(readOnly = true)
    public PagedResponse<BookResponse> getNewestBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findNewestBooks(pageable);
        return mapToPagedResponse(books);
    }
    
    // Filter books
    @Transactional(readOnly = true)
    public PagedResponse<BookResponse> filterBooks(String title, String author, String category,
                                                  BigDecimal minPrice, BigDecimal maxPrice, 
                                                  BigDecimal minRating, Pageable pageable) {
        Page<Book> books = bookRepository.findBooksWithFilters(
                title, author, category, minPrice, maxPrice, minRating, pageable);
        return mapToPagedResponse(books);
    }
    
    // Create book
    public BookResponse createBook(BookCreateRequest request) {
        try {
            log.info("Creating book with request: {}", request);
            
            // Get current user ID and set as seller
            UUID currentUserId = getCurrentUserId();
            log.info("Current user ID: {}", currentUserId);
            
            // Create book entity
            Book book = Book.builder()
                    .title(request.getTitle())
                    .author(request.getAuthor())
                    .isbn(request.getIsbn())
                    .description(request.getDescription())
                    .price(request.getPrice())
                    .publishedDate(request.getPublishedDate())
                    .coverImageUrl(request.getCoverImageUrl())
                    .stockQuantity(request.getStockQuantity())
                    .sellerId(currentUserId) // Set current user as seller
                    .language(request.getLanguage())
                    .pageCount(request.getPageCount())
                    .publisher(request.getPublisher())
                    .isActive(true)
                    .reviewCount(0)
                    .build();
            
            log.info("Built book entity: {}", book);
            Book savedBook = bookRepository.save(book);
            log.info("Saved book: {}", savedBook.getId());
            
            // Add categories
            if (request.getCategoryNames() != null && !request.getCategoryNames().isEmpty()) {
                log.info("Processing categories: {}", request.getCategoryNames());
                // Always use a mutable list for bookCategories
                if (savedBook.getBookCategories() == null) {
                    savedBook.setBookCategories(new ArrayList<>());
                }
                for (String categoryName : request.getCategoryNames()) {
                    Category category = categoryRepository.findByNameAndIsActiveTrue(categoryName)
                            .orElseGet(() -> {
                                Category newCategory = Category.builder()
                                        .name(categoryName)
                                        .isActive(true)
                                        .build();
                                return categoryRepository.save(newCategory);
                            });

                    BookCategory bookCategory = BookCategory.builder()
                            .book(savedBook)
                            .category(category)
                            .isPrimary(request.getCategoryNames().indexOf(categoryName) == 0)
                            .build();

                    savedBook.getBookCategories().add(bookCategory);
                }
                savedBook = bookRepository.save(savedBook);
                log.info("Saved book with categories: {}", savedBook.getId());
            }
            
            log.info("Created new book: {} by {}", savedBook.getTitle(), savedBook.getAuthor());
            return mapToResponse(savedBook);
        } catch (Exception e) {
            log.error("Error creating book: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create book: " + e.getMessage(), e);
        }
    }
    
    // Update book
    public BookResponse updateBook(UUID id, BookUpdateRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + id));
        
        // Check if current user is the seller of this book OR is an admin
        UUID currentUserId = getCurrentUserId();
        if (!isCurrentUserAdmin() && !book.getSellerId().equals(currentUserId)) {
            throw new SecurityException("You can only update your own books");
        }
        
        // Update fields
        if (request.getTitle() != null) book.setTitle(request.getTitle());
        if (request.getAuthor() != null) book.setAuthor(request.getAuthor());
        if (request.getDescription() != null) book.setDescription(request.getDescription());
        if (request.getPrice() != null) book.setPrice(request.getPrice());
        if (request.getStockQuantity() != null) book.setStockQuantity(request.getStockQuantity());
        if (request.getCoverImageUrl() != null) book.setCoverImageUrl(request.getCoverImageUrl());
        if (request.getLanguage() != null) book.setLanguage(request.getLanguage());
        if (request.getPageCount() != null) book.setPageCount(request.getPageCount());
        if (request.getPublisher() != null) book.setPublisher(request.getPublisher());
        
        Book savedBook = bookRepository.save(book);
        
        // Update categories if provided
        if (request.getCategoryNames() != null && !request.getCategoryNames().isEmpty()) {
            log.info("Processing category updates: {}", request.getCategoryNames());
            
            // Clear existing categories
            if (savedBook.getBookCategories() != null) {
                savedBook.getBookCategories().clear();
            } else {
                savedBook.setBookCategories(new ArrayList<>());
            }
            
            // Add new categories
            for (String categoryName : request.getCategoryNames()) {
                Category category = categoryRepository.findByNameAndIsActiveTrue(categoryName)
                        .orElseGet(() -> {
                            Category newCategory = Category.builder()
                                    .name(categoryName)
                                    .isActive(true)
                                    .build();
                            return categoryRepository.save(newCategory);
                        });

                BookCategory bookCategory = BookCategory.builder()
                        .book(savedBook)
                        .category(category)
                        .isPrimary(request.getCategoryNames().indexOf(categoryName) == 0)
                        .build();

                savedBook.getBookCategories().add(bookCategory);
            }
            savedBook = bookRepository.save(savedBook);
            log.info("Updated book with categories: {}", savedBook.getId());
        }
        
        log.info("Updated book: {}", savedBook.getId());
        return mapToResponse(savedBook);
    }
    
    // Get current user ID from security context
    private UUID getCurrentUserId() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.info("Principal type: {}, value: {}", principal.getClass().getSimpleName(), principal);
            
            if (principal instanceof UUID) {
                return (UUID) principal;
            } else if (principal instanceof String) {
                return UUID.fromString((String) principal);
            } else {
                log.error("Unexpected principal type: {}", principal.getClass().getName());
                throw new SecurityException("User not authenticated - unexpected principal type: " + principal.getClass().getName());
            }
        } catch (Exception e) {
            log.error("Error getting current user ID: {}", e.getMessage(), e);
            throw new SecurityException("User not authenticated: " + e.getMessage());
        }
    }
    
    // Update stock with validation
    @Transactional
    public void updateBookStock(UUID id, Integer stockQuantity) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Book not found with ID: " + id));
            
            // Check if current user is the seller of this book OR is an admin
            UUID currentUserId = getCurrentUserId();
            if (!isCurrentUserAdmin() && !book.getSellerId().equals(currentUserId)) {
                throw new SecurityException("You can only update stock for your own books");
            }
            
            if (stockQuantity < 0) {
                throw new BadRequestException("Stock quantity cannot be negative");
            }
            
            book.setStockQuantity(stockQuantity);
            bookRepository.save(book);
            log.info("Updated stock for book {}: {} -> {}", id, book.getTitle(), stockQuantity);
        } catch (Exception e) {
            log.error("Failed to update stock for book: {}", id, e);
            throw new BadRequestException("Failed to update book stock: " + e.getMessage());
        }
    }
    
    /**
     * Bulk update stock for multiple books (for admin operations)
     */
    @Transactional
    public void bulkUpdateStock(Map<UUID, Integer> stockUpdates) {
        try {
            for (Map.Entry<UUID, Integer> entry : stockUpdates.entrySet()) {
                UUID bookId = entry.getKey();
                Integer newStock = entry.getValue();
                
                if (newStock < 0) {
                    throw new BadRequestException("Stock quantity cannot be negative for book: " + bookId);
                }
                
                Book book = bookRepository.findById(bookId)
                        .orElseThrow(() -> new NotFoundException("Book not found with ID: " + bookId));
                
                book.setStockQuantity(newStock);
                bookRepository.save(book);
                
                log.info("Updated stock for book {}: {} -> {}", book.getTitle(), bookId, newStock);
            }
        } catch (Exception e) {
            log.error("Failed to bulk update stock", e);
            throw new BadRequestException("Failed to bulk update stock: " + e.getMessage());
        }
    }
    
    // Activate book
    public void activateBook(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + id));
        
        // Check if current user is the seller of this book OR is an admin
        UUID currentUserId = getCurrentUserId();
        if (!isCurrentUserAdmin() && !book.getSellerId().equals(currentUserId)) {
            throw new SecurityException("You can only activate your own books");
        }
        
        book.setIsActive(true);
        bookRepository.save(book);
    }
    
    // Deactivate book
    public void deactivateBook(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + id));
        
        // Check if current user is the seller of this book OR is an admin
        UUID currentUserId = getCurrentUserId();
        if (!isCurrentUserAdmin() && !book.getSellerId().equals(currentUserId)) {
            throw new SecurityException("You can only deactivate your own books");
        }
        
        book.setIsActive(false);
        bookRepository.save(book);
    }
    
    // Delete book
    public void deleteBook(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + id));
        
        // Check if current user is the seller of this book OR is an admin
        UUID currentUserId = getCurrentUserId();
        if (!isCurrentUserAdmin() && !book.getSellerId().equals(currentUserId)) {
            throw new SecurityException("You can only delete your own books");
        }
        
        bookRepository.deleteById(id);
        log.info("Deleted book: {}", id);
    }
    
    // Get books by seller
    @Transactional(readOnly = true)
    public List<BookResponse> getBooksBySeller(UUID sellerId) {
        List<Book> books = bookRepository.findBySellerIdAndIsActiveTrue(sellerId);
        return books.stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    
    // Get all categories
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findActiveCategoriesOrderByName();
        return categories.stream()
                .map(this::mapCategoryToResponse)
                .collect(Collectors.toList());
    }
    
    // Helper methods
    private PagedResponse<BookResponse> mapToPagedResponse(Page<Book> books) {
        List<BookResponse> content = books.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PagedResponse.<BookResponse>builder()
                .content(content)
                .page(books.getNumber())
                .size(books.getSize())
                .totalElements(books.getTotalElements())
                .totalPages(books.getTotalPages())
                .first(books.isFirst())
                .last(books.isLast())
                .numberOfElements(books.getNumberOfElements())
                .build();
    }
    
    private BookResponse mapToResponse(Book book) {
        // Map categories from BookCategory relationships
        List<CategoryResponse> categories = null;
        if (book.getBookCategories() != null && !book.getBookCategories().isEmpty()) {
            categories = book.getBookCategories().stream()
                    .map(bc -> mapCategoryToResponse(bc.getCategory()))
                    .collect(Collectors.toList());
        }
        
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .price(book.getPrice())
                .publishedDate(book.getPublishedDate())
                .coverImageUrl(book.getCoverImageUrl())
                .stockQuantity(book.getStockQuantity())
                .sellerId(book.getSellerId())
                .isActive(book.getIsActive())
                .rating(book.getRating())
                .reviewCount(book.getReviewCount())
                .language(book.getLanguage())
                .pageCount(book.getPageCount())
                .publisher(book.getPublisher())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .categories(categories)
                .inStock(book.isInStock())
                .available(book.isAvailable())
                .build();
    }
    
    private CategoryResponse mapCategoryToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .build();
    }
    
    // ========== SELLER REVENUE ANALYTICS METHODS ==========
    
    /**
     * Get seller revenue analytics
     */
    @Transactional(readOnly = true)
    public SellerRevenueResponse getSellerRevenueAnalytics(UUID sellerId) {
        log.info("Getting revenue analytics for seller: {}", sellerId);
        
        try {
            // Get basic seller stats
            Long totalBooks = bookRepository.countBooksBySeller(sellerId);
            Long activeBooks = bookRepository.countBySellerIdAndIsActiveTrue(sellerId);
            Long lowStockBooks = bookRepository.countBySellerIdAndStockQuantityLessThanEqual(sellerId, 10L);
            
            // Get revenue statistics
            BigDecimal totalRevenue = orderItemRepository.getTotalRevenueBySellerId(sellerId);
            Long totalSoldItems = orderItemRepository.getTotalQuantityBySellerId(sellerId);
            Long orderCount = orderItemRepository.getOrderCountBySellerId(sellerId);
            
            // Handle null values
            totalBooks = totalBooks != null ? totalBooks : 0L;
            activeBooks = activeBooks != null ? activeBooks : 0L;
            lowStockBooks = lowStockBooks != null ? lowStockBooks : 0L;
            totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
            totalSoldItems = totalSoldItems != null ? totalSoldItems : 0L;
            orderCount = orderCount != null ? orderCount : 0L;
            
            // Calculate average order value
            BigDecimal averageOrderValue = BigDecimal.ZERO;
            if (orderCount > 0 && totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
                averageOrderValue = totalRevenue.divide(BigDecimal.valueOf(orderCount), 2, BigDecimal.ROUND_HALF_UP);
            }
            
            // Get time-based revenue (simplified for now)
            BigDecimal monthlyRevenue = BigDecimal.ZERO;
            BigDecimal yearlyRevenue = BigDecimal.ZERO;
            BigDecimal weeklyRevenue = BigDecimal.ZERO;
            
            // Get top performing books
            List<SellerRevenueResponse.BookRevenueItem> topPerformingBooks = getTopPerformingBooks(sellerId);
            
            // Get revenue breakdown
            List<SellerRevenueResponse.RevenuePeriod> revenueBreakdown = getRevenueBreakdown(sellerId);
            
            // Get recent orders
            List<SellerRevenueResponse.SellerOrderItem> recentOrders = getRecentSellerOrders(sellerId);
            
            // Calculate revenue growth (simplified)
            BigDecimal revenueGrowth = BigDecimal.ZERO;
            
            log.info("Seller analytics - Books: {}, Active: {}, Revenue: {}, Sold: {}, Orders: {}", 
                    totalBooks, activeBooks, totalRevenue, totalSoldItems, orderCount);
            
            return SellerRevenueResponse.builder()
                    .sellerId(sellerId)
                    .sellerName("Seller") // TODO: Get from auth service
                    .totalBooks(totalBooks)
                    .totalSoldItems(totalSoldItems)
                    .totalRevenue(totalRevenue)
                    .averageOrderValue(averageOrderValue)
                    .monthlyRevenue(monthlyRevenue)
                    .yearlyRevenue(yearlyRevenue)
                    .weeklyRevenue(weeklyRevenue)
                    .revenueGrowth(revenueGrowth)
                    .orderCount(orderCount)
                    .activeBooks(activeBooks)
                    .lowStockBooks(lowStockBooks)
                    .topPerformingBooks(topPerformingBooks)
                    .revenueBreakdown(revenueBreakdown)
                    .recentOrders(recentOrders)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error getting seller revenue analytics: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get seller revenue analytics: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get seller orders
     */
    @Transactional(readOnly = true)
    public List<SellerRevenueResponse.SellerOrderItem> getSellerOrders(UUID sellerId) {
        log.info("Getting orders for seller: {}", sellerId);
        
        try {
            return getRecentSellerOrders(sellerId);
        } catch (Exception e) {
            log.error("Error getting seller orders: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get seller orders: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get seller dashboard stats
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getSellerDashboardStats(UUID sellerId) {
        log.info("Getting dashboard stats for seller: {}", sellerId);
        
        try {
            Long totalBooks = bookRepository.countBooksBySeller(sellerId);
            Long totalSoldItems = orderItemRepository.getTotalQuantityBySellerId(sellerId);
            BigDecimal totalRevenue = orderItemRepository.getTotalRevenueBySellerId(sellerId);
            
            return Map.of(
                "totalBooks", totalBooks != null ? totalBooks : 0L,
                "totalSold", totalSoldItems != null ? totalSoldItems : 0L,
                "totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO
            );
        } catch (Exception e) {
            log.error("Error getting seller dashboard stats: {}", e.getMessage(), e);
            return Map.of(
                "totalBooks", 0L,
                "totalSold", 0L,
                "totalRevenue", BigDecimal.ZERO
            );
        }
    }
    
    // Helper methods for revenue analytics
    
    private List<SellerRevenueResponse.BookRevenueItem> getTopPerformingBooks(UUID sellerId) {
        try {
            // For now, return empty list to avoid complex queries
            // TODO: Implement when we have more order data
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Error getting top performing books: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private List<SellerRevenueResponse.RevenuePeriod> getRevenueBreakdown(UUID sellerId) {
        try {
            // For now, return empty list to avoid complex queries
            // TODO: Implement when we have more order data
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Error getting revenue breakdown: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private List<SellerRevenueResponse.SellerOrderItem> getRecentSellerOrders(UUID sellerId) {
        try {
            // For now, return empty list to avoid complex queries
            // TODO: Implement when we have more order data
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Error getting recent seller orders: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private BigDecimal calculateRevenueGrowth(UUID sellerId) {
        // Simplified revenue growth calculation
        // TODO: Implement proper month-over-month comparison
        return BigDecimal.ZERO;
    }

    /**
     * Check if current user has admin role
     */
    private boolean isCurrentUserAdmin() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getAuthorities() == null) {
                return false;
            }
            
            return authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        } catch (Exception e) {
            log.error("Error checking admin role: {}", e.getMessage());
            return false;
        }
    }
} 
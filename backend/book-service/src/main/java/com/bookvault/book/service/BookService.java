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

import java.math.BigDecimal;
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
    
    // Constructor (replacing @RequiredArgsConstructor)
    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
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
        
        // Check if current user is the seller of this book
        UUID currentUserId = getCurrentUserId();
        if (!book.getSellerId().equals(currentUserId)) {
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
        book.setIsActive(true);
        bookRepository.save(book);
    }
    
    // Deactivate book
    public void deactivateBook(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + id));
        book.setIsActive(false);
        bookRepository.save(book);
    }
    
    // Delete book
    public void deleteBook(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + id));
        
        // Check if current user is the seller of this book
        UUID currentUserId = getCurrentUserId();
        if (!book.getSellerId().equals(currentUserId)) {
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
} 
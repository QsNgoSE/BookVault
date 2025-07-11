package com.bookvault.book.controller;

import com.bookvault.book.dto.*;
import com.bookvault.book.service.BookService;
import com.bookvault.shared.dto.ApiResponse;
import com.bookvault.shared.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

/**
 * REST Controller for book management
 */
@RestController
@RequestMapping("/api/books")
// @RequiredArgsConstructor
@Tag(name = "Books", description = "Book management API")
public class BookController {
    
    private final BookService bookService;
    
    // Constructor (replacing @RequiredArgsConstructor)
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    
    @GetMapping
    @Operation(summary = "Get all books", description = "Retrieve paginated list of active books")
    public ResponseEntity<ApiResponse<PagedResponse<BookResponse>>> getAllBooks(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "12") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        PagedResponse<BookResponse> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Retrieve book details by ID")
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(
            @Parameter(description = "Book ID") @PathVariable UUID id) {
        
        BookResponse book = bookService.getBookById(id);
        return ResponseEntity.ok(ApiResponse.success(book));
    }
    
    @GetMapping("/isbn/{isbn}")
    @Operation(summary = "Get book by ISBN", description = "Retrieve book details by ISBN")
    public ResponseEntity<ApiResponse<BookResponse>> getBookByIsbn(
            @Parameter(description = "Book ISBN") @PathVariable String isbn) {
        
        BookResponse book = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(ApiResponse.success(book));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search books", description = "Search books by title, author, or description")
    public ResponseEntity<ApiResponse<PagedResponse<BookResponse>>> searchBooks(
            @Parameter(description = "Search query") @RequestParam String q,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "12") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "relevance") String sortBy) {
        
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<BookResponse> books = bookService.searchBooks(q, pageable);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    @GetMapping("/category/{categoryName}")
    @Operation(summary = "Get books by category", description = "Retrieve books by category name")
    public ResponseEntity<ApiResponse<PagedResponse<BookResponse>>> getBooksByCategory(
            @Parameter(description = "Category name") @PathVariable String categoryName,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<BookResponse> books = bookService.getBooksByCategory(categoryName, pageable);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    @GetMapping("/author/{author}")
    @Operation(summary = "Get books by author", description = "Retrieve books by author name")
    public ResponseEntity<ApiResponse<PagedResponse<BookResponse>>> getBooksByAuthor(
            @Parameter(description = "Author name") @PathVariable String author,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<BookResponse> books = bookService.getBooksByAuthor(author, pageable);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    @GetMapping("/featured")
    @Operation(summary = "Get featured books", description = "Retrieve featured books sorted by popularity")
    public ResponseEntity<ApiResponse<PagedResponse<BookResponse>>> getFeaturedBooks(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<BookResponse> books = bookService.getFeaturedBooks(pageable);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    @GetMapping("/bestsellers")
    @Operation(summary = "Get bestselling books", description = "Retrieve top-rated books")
    public ResponseEntity<ApiResponse<PagedResponse<BookResponse>>> getBestsellingBooks(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<BookResponse> books = bookService.getTopRatedBooks(pageable);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    @GetMapping("/new-releases")
    @Operation(summary = "Get new releases", description = "Retrieve newest books")
    public ResponseEntity<ApiResponse<PagedResponse<BookResponse>>> getNewReleases(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<BookResponse> books = bookService.getNewestBooks(pageable);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    @GetMapping("/filter")
    @Operation(summary = "Filter books", description = "Filter books with multiple criteria")
    public ResponseEntity<ApiResponse<PagedResponse<BookResponse>>> filterBooks(
            @Parameter(description = "Title filter") @RequestParam(required = false) String title,
            @Parameter(description = "Author filter") @RequestParam(required = false) String author,
            @Parameter(description = "Category filter") @RequestParam(required = false) String category,
            @Parameter(description = "Minimum price") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Minimum rating") @RequestParam(required = false) BigDecimal minRating,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "12") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<BookResponse> books = bookService.filterBooks(
                title, author, category, minPrice, maxPrice, minRating, pageable);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    @PostMapping
    @Operation(summary = "Create new book", description = "Create a new book listing")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<BookResponse>> createBook(
            @Valid @RequestBody BookCreateRequest request) {
        
        // Validate request
        validateBookCreateRequest(request);
        
        BookResponse book = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(book, "Book created successfully"));
    }
    
    @PostMapping("/upload")
    @Operation(summary = "Create new book with file upload", description = "Create a new book listing with cover image file")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<BookResponse>> createBookWithFile(
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("price") BigDecimal price,
            @RequestParam("description") String description,
            @RequestParam("stockQuantity") Integer stockQuantity,
            @RequestParam("categoryNames") String categoryNames,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {
        
        // Validate required fields
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title is required");
        }
        
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Book author is required");
        }
        
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Book price must be greater than 0");
        }
        
        if (stockQuantity == null || stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity must be 0 or greater");
        }
        
        // Create BookCreateRequest from form data
        BookCreateRequest request = new BookCreateRequest();
        request.setTitle(title.trim());
        request.setAuthor(author.trim());
        request.setPrice(price);
        request.setDescription(description.trim());
        request.setStockQuantity(stockQuantity);
        request.setCategoryNames(List.of(categoryNames));
        
        // Handle cover image
        if (coverImage != null && !coverImage.isEmpty()) {
            // For now, store a placeholder URL instead of base64 to avoid PostgreSQL issues
            // In a production environment, you would upload to a cloud storage service
            String fileName = "book_" + System.currentTimeMillis() + "_" + coverImage.getOriginalFilename();
            request.setCoverImageUrl("/asset/img/books/" + fileName);
            
            // Log the image details for debugging
            System.out.println("Image uploaded: " + fileName + " (" + coverImage.getSize() + " bytes)");
        } else {
            request.setCoverImageUrl("/asset/img/books/placeholder.jpg");
        }
        
        BookResponse book = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(book, "Book created successfully"));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update book", description = "Update existing book details")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @Parameter(description = "Book ID") @PathVariable UUID id,
            @Valid @RequestBody BookUpdateRequest request) {
        
        BookResponse book = bookService.updateBook(id, request);
        return ResponseEntity.ok(ApiResponse.success(book, "Book updated successfully"));
    }
    
    @PutMapping("/{id}/upload")
    @Operation(summary = "Update book with file upload", description = "Update existing book details with cover image file")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<ApiResponse<BookResponse>> updateBookWithFile(
            @Parameter(description = "Book ID") @PathVariable UUID id,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("price") BigDecimal price,
            @RequestParam("description") String description,
            @RequestParam("stockQuantity") Integer stockQuantity,
            @RequestParam("categoryNames") String categoryNames,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {
        
        // Validate required fields
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title is required");
        }
        
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Book author is required");
        }
        
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Book price must be greater than 0");
        }
        
        if (stockQuantity == null || stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity must be 0 or greater");
        }
        
        // Create BookUpdateRequest from form data
        BookUpdateRequest request = new BookUpdateRequest();
        request.setTitle(title.trim());
        request.setAuthor(author.trim());
        request.setPrice(price);
        request.setDescription(description.trim());
        request.setStockQuantity(stockQuantity);
        request.setCategoryNames(List.of(categoryNames));
        
        // Handle cover image
        if (coverImage != null && !coverImage.isEmpty()) {
            // For now, store a placeholder URL instead of base64 to avoid PostgreSQL issues
            // In a production environment, you would upload to a cloud storage service
            String fileName = "book_" + System.currentTimeMillis() + "_" + coverImage.getOriginalFilename();
            request.setCoverImageUrl("/asset/img/books/" + fileName);
            
            // Log the image details for debugging
            System.out.println("Image uploaded for update: " + fileName + " (" + coverImage.getSize() + " bytes)");
        } else {
            // Keep existing cover image URL if no new image provided
            request.setCoverImageUrl(null);
        }
        
        BookResponse book = bookService.updateBook(id, request);
        return ResponseEntity.ok(ApiResponse.success(book, "Book updated successfully"));
    }
    
    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update book stock", description = "Update book stock quantity")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<ApiResponse<String>> updateBookStock(
            @Parameter(description = "Book ID") @PathVariable UUID id,
            @Parameter(description = "New stock quantity") @RequestParam Integer stockQuantity) {
        
        bookService.updateBookStock(id, stockQuantity);
        return ResponseEntity.ok(ApiResponse.success("Stock updated successfully"));
    }
    
    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate book", description = "Activate book listing")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<ApiResponse<String>> activateBook(
            @Parameter(description = "Book ID") @PathVariable UUID id) {
        
        bookService.activateBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book activated successfully"));
    }
    
    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate book", description = "Deactivate book listing")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<ApiResponse<String>> deactivateBook(
            @Parameter(description = "Book ID") @PathVariable UUID id) {
        
        bookService.deactivateBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book deactivated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book", description = "Delete book listing")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SELLER')")
    public ResponseEntity<ApiResponse<String>> deleteBook(
            @Parameter(description = "Book ID") @PathVariable UUID id) {
        
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book deleted successfully"));
    }
    
    @GetMapping("/seller/{sellerId}")
    @Operation(summary = "Get books by seller", description = "Retrieve books by seller ID")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getBooksBySeller(
            @Parameter(description = "Seller ID") @PathVariable UUID sellerId) {
        
        List<BookResponse> books = bookService.getBooksBySeller(sellerId);
        return ResponseEntity.ok(ApiResponse.success(books));
    }
    
    @GetMapping("/categories")
    @Operation(summary = "Get all categories", description = "Retrieve all active book categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = bookService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
    
    // ========== SELLER REVENUE ENDPOINTS ==========
    
    @GetMapping("/seller/revenue")
    @Operation(summary = "Get seller revenue analytics", description = "Get comprehensive revenue analytics for the current seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<SellerRevenueResponse>> getSellerRevenue() {
        try {
            UUID currentUserId = getCurrentUserId();
            SellerRevenueResponse analytics = bookService.getSellerRevenueAnalytics(currentUserId);
            return ResponseEntity.ok(ApiResponse.success(analytics, "Seller revenue analytics retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get seller revenue analytics: " + e.getMessage()));
        }
    }
    
    @GetMapping("/seller/orders")
    @Operation(summary = "Get seller orders", description = "Get recent orders for the current seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<List<SellerRevenueResponse.SellerOrderItem>>> getSellerOrders() {
        try {
            UUID currentUserId = getCurrentUserId();
            List<SellerRevenueResponse.SellerOrderItem> orders = bookService.getSellerOrders(currentUserId);
            return ResponseEntity.ok(ApiResponse.success(orders, "Seller orders retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get seller orders: " + e.getMessage()));
        }
    }
    
    @GetMapping("/seller/stats")
    @Operation(summary = "Get seller dashboard stats", description = "Get basic dashboard statistics for the current seller")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSellerDashboardStats() {
        try {
            UUID currentUserId = getCurrentUserId();
            Map<String, Object> stats = bookService.getSellerDashboardStats(currentUserId);
            return ResponseEntity.ok(ApiResponse.success(stats, "Seller dashboard stats retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to get seller dashboard stats: " + e.getMessage()));
        }
    }
    
    @GetMapping("/proxy-image")
    @Operation(summary = "Proxy image for CORS", description = "Proxy external image URLs to avoid CORS issues")
    public ResponseEntity<Resource> proxyImage(@RequestParam String url) {
        try {
            // Validate URL
            if (url == null || url.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            // Basic URL validation
            URL imageUrl = new URL(url);
            String protocol = imageUrl.getProtocol();
            if (!"http".equals(protocol) && !"https".equals(protocol)) {
                return ResponseEntity.badRequest().build();
            }
            
            // Open connection
            URLConnection connection = imageUrl.openConnection();
            connection.setRequestProperty("User-Agent", "BookVault/1.0");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            
            // Read the image
            try (InputStream inputStream = connection.getInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                
                byte[] imageBytes = outputStream.toByteArray();
                
                // Determine content type
                String contentType = connection.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    // Try to guess from URL
                    String lowerUrl = url.toLowerCase();
                    if (lowerUrl.contains(".jpg") || lowerUrl.contains(".jpeg")) {
                        contentType = "image/jpeg";
                    } else if (lowerUrl.contains(".png")) {
                        contentType = "image/png";
                    } else if (lowerUrl.contains(".gif")) {
                        contentType = "image/gif";
                    } else if (lowerUrl.contains(".webp")) {
                        contentType = "image/webp";
                    } else {
                        contentType = "image/jpeg"; // Default
                    }
                }
                
                // Create response
                ByteArrayResource resource = new ByteArrayResource(imageBytes);
                
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                        .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                        .body(resource);
            }
            
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get current user ID from security context
     */
    private UUID getCurrentUserId() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            
            if (principal instanceof UUID) {
                return (UUID) principal;
            } else if (principal instanceof String) {
                return UUID.fromString((String) principal);
            } else {
                throw new SecurityException("User not authenticated - unexpected principal type: " + principal.getClass().getName());
            }
        } catch (Exception e) {
            throw new SecurityException("User not authenticated: " + e.getMessage());
        }
    }
    
    /**
     * Validate book creation request
     */
    private void validateBookCreateRequest(BookCreateRequest request) {
        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title is required");
        }
        
        if (request.getAuthor() == null || request.getAuthor().trim().isEmpty()) {
            throw new IllegalArgumentException("Book author is required");
        }
        
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Book price must be greater than 0");
        }
        
        if (request.getStockQuantity() == null || request.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity must be 0 or greater");
        }
        
        if (request.getIsbn() != null && !request.getIsbn().trim().isEmpty()) {
            // Basic ISBN validation
            String isbn = request.getIsbn().replaceAll("[^0-9X]", "");
            if (isbn.length() != 10 && isbn.length() != 13) {
                throw new IllegalArgumentException("ISBN must be 10 or 13 digits");
            }
        }
    }

    /**
     * Check if current user has admin role
     */
    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
} 
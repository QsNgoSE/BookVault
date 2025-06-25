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
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<ApiResponse<BookResponse>> createBook(
            @Valid @RequestBody BookCreateRequest request) {
        
        BookResponse book = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(book, "Book created successfully"));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update book", description = "Update existing book details")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @Parameter(description = "Book ID") @PathVariable UUID id,
            @Valid @RequestBody BookUpdateRequest request) {
        
        BookResponse book = bookService.updateBook(id, request);
        return ResponseEntity.ok(ApiResponse.success(book, "Book updated successfully"));
    }
    
    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update book stock", description = "Update book stock quantity")
    public ResponseEntity<ApiResponse<String>> updateBookStock(
            @Parameter(description = "Book ID") @PathVariable UUID id,
            @Parameter(description = "New stock quantity") @RequestParam Integer stockQuantity) {
        
        bookService.updateBookStock(id, stockQuantity);
        return ResponseEntity.ok(ApiResponse.success("Stock updated successfully"));
    }
    
    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate book", description = "Activate book listing")
    public ResponseEntity<ApiResponse<String>> activateBook(
            @Parameter(description = "Book ID") @PathVariable UUID id) {
        
        bookService.activateBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book activated successfully"));
    }
    
    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate book", description = "Deactivate book listing")
    public ResponseEntity<ApiResponse<String>> deactivateBook(
            @Parameter(description = "Book ID") @PathVariable UUID id) {
        
        bookService.deactivateBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book deactivated successfully"));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book", description = "Delete book listing")
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
} 
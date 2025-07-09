package com.bookvault.book.config;

import com.bookvault.book.model.Book;
import com.bookvault.book.model.BookCategory;
import com.bookvault.book.model.Category;
import com.bookvault.book.repository.BookCategoryRepository;
import com.bookvault.book.repository.BookRepository;
import com.bookvault.book.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Data initializer to populate sample books and categories
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final BookCategoryRepository bookCategoryRepository;
    
    public DataInitializer(CategoryRepository categoryRepository, BookRepository bookRepository, 
                          BookCategoryRepository bookCategoryRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
        this.bookCategoryRepository = bookCategoryRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        try {
            log.info("Starting data initialization check...");
            
            // Log environment variables for debugging (without sensitive data)
            log.info("Environment check:");
            log.info("SPRING_PROFILES_ACTIVE: {}", System.getenv("SPRING_PROFILES_ACTIVE"));
            log.info("PGHOST: {}", System.getenv("PGHOST"));
            log.info("PGPORT: {}", System.getenv("PGPORT"));
            log.info("POSTGRES_DB: {}", System.getenv("POSTGRES_DB"));
            log.info("POSTGRES_USER: {}", System.getenv("POSTGRES_USER"));
            log.info("POSTGRES_PASSWORD: {}", System.getenv("POSTGRES_PASSWORD") != null ? "[SET]" : "[NOT SET]");
            
            // Test database connectivity and schema
            log.info("Testing database connectivity and schema...");
            try {
                // Test if tables exist by trying to count records
                long totalCategories = categoryRepository.count();
                long totalBooks = bookRepository.count();
                long totalBookCategories = bookCategoryRepository.count();
                log.info("Database connectivity test successful. Found {} categories, {} books, {} book-category relationships", 
                        totalCategories, totalBooks, totalBookCategories);
            } catch (Exception e) {
                log.error("Database connectivity test failed: {}", e.getMessage(), e);
                throw e;
            }
            
            // Check if categories exist
            long categoryCount = categoryRepository.count();
            log.info("Found {} categories in database", categoryCount);
            
            // Check if books exist
            long bookCount = bookRepository.count();
            log.info("Found {} books in database", bookCount);
            
            // Check if book-category relationships exist
            long bookCategoryCount = bookCategoryRepository.count();
            log.info("Found {} book-category relationships in database", bookCategoryCount);
            
            // Check for inconsistent state (categories exist but no books, or vice versa)
            if (categoryCount > 0 && bookCount == 0) {
                log.warn("Inconsistent state detected: {} categories but no books. Reinitializing data...", categoryCount);
                forceReinitializeData();
            } else if (bookCount > 0 && bookCategoryCount == 0) {
                log.warn("Inconsistent state detected: {} books but no book-category relationships. Reinitializing data...", bookCount);
                forceReinitializeData();
            } else if (categoryCount == 0) {
                log.info("No categories found, initializing sample data...");
                initializeCategories();
                initializeBooksWithCategories();
                log.info("Sample data initialization completed!");
                
                // Verify data was created
                long finalCategoryCount = categoryRepository.count();
                long finalBookCount = bookRepository.count();
                long finalBookCategoryCount = bookCategoryRepository.count();
                log.info("After initialization: {} categories, {} books, {} book-category relationships", 
                        finalCategoryCount, finalBookCount, finalBookCategoryCount);
            } else {
                log.info("Sample data already exists, checking for image updates...");
                updateBookImages();
                
                // Log current data status
                long finalCategoryCount = categoryRepository.count();
                long finalBookCount = bookRepository.count();
                long finalBookCategoryCount = bookCategoryRepository.count();
                log.info("Current data status: {} categories, {} books, {} book-category relationships", 
                        finalCategoryCount, finalBookCount, finalBookCategoryCount);
            }
        } catch (Exception e) {
            log.error("Error during data initialization: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    private void forceReinitializeData() {
        try {
            log.info("Clearing existing data...");
            
            // Clear existing data
            long bookCategoryCount = bookCategoryRepository.count();
            long bookCount = bookRepository.count();
            long categoryCount = categoryRepository.count();
            
            log.info("Clearing {} book-category relationships, {} books, {} categories", 
                    bookCategoryCount, bookCount, categoryCount);
            
            bookCategoryRepository.deleteAll();
            bookRepository.deleteAll();
            categoryRepository.deleteAll();
            
            log.info("Existing data cleared. Reinitializing...");
            
            // Reinitialize data
            initializeCategories();
            initializeBooksWithCategories();
            
            log.info("Data reinitialization completed!");
            
            // Verify data was created
            long finalCategoryCount = categoryRepository.count();
            long finalBookCount = bookRepository.count();
            long finalBookCategoryCount = bookCategoryRepository.count();
            log.info("After reinitialization: {} categories, {} books, {} book-category relationships", 
                    finalCategoryCount, finalBookCount, finalBookCategoryCount);
        } catch (Exception e) {
            log.error("Error during data reinitialization: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    private void initializeCategories() {
        try {
            List<Category> categories = Arrays.asList(
                createCategory("Fiction", "Fictional literature and novels"),
                createCategory("Non-Fiction", "Non-fictional books and biographies"),
                createCategory("Science Fiction", "Science fiction and fantasy novels"),
                createCategory("Mystery", "Mystery and thriller novels"),
                createCategory("Romance", "Romance novels and love stories"),
                createCategory("History", "Historical books and documentaries"),
                createCategory("Biography", "Biographies and memoirs"),
                createCategory("Self-Help", "Self-help and personal development"),
                createCategory("Technology", "Technology and programming books"),
                createCategory("Business", "Business and entrepreneurship books")
            );
            
            List<Category> savedCategories = categoryRepository.saveAll(categories);
            log.info("Successfully created {} categories", savedCategories.size());
            
            // Verify each category was saved
            for (Category category : savedCategories) {
                log.info("Created category: {} (ID: {})", category.getName(), category.getId());
            }
        } catch (Exception e) {
            log.error("Error creating categories: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    private void initializeBooksWithCategories() {
        try {
            List<Category> categories = categoryRepository.findAll();
            log.info("Found {} categories for book creation", categories.size());
            
            if (categories.isEmpty()) {
                log.error("No categories found for book creation!");
                return;
            }
            
            // Create books with proper category relationships
            createBookWithCategory("The Great Gatsby", "F. Scott Fitzgerald", "9780743273565", 
                "A classic American novel set in the Jazz Age", new BigDecimal("12.99"), 
                categories.get(0), UUID.randomUUID());
            
            createBookWithCategory("To Kill a Mockingbird", "Harper Lee", "9780061120084",
                "A gripping tale of racial injustice and childhood innocence", new BigDecimal("13.99"),
                categories.get(0), UUID.randomUUID());
            
            createBookWithCategory("1984", "George Orwell", "9780451524935",
                "A dystopian social science fiction novel", new BigDecimal("14.99"),
                categories.get(2), UUID.randomUUID());
            
            createBookWithCategory("Pride and Prejudice", "Jane Austen", "9780141439518",
                "A romantic novel of manners", new BigDecimal("11.99"),
                categories.get(4), UUID.randomUUID());
            
            createBookWithCategory("The Catcher in the Rye", "J.D. Salinger", "9780316769174",
                "A controversial novel about teenage rebellion", new BigDecimal("13.50"),
                categories.get(0), UUID.randomUUID());
            
            createBookWithCategory("Dune", "Frank Herbert", "9780441172719",
                "A science fiction epic set on the desert planet Arrakis", new BigDecimal("16.99"),
                categories.get(2), UUID.randomUUID());
            
            createBookWithCategory("The Da Vinci Code", "Dan Brown", "9780307474278",
                "A mystery thriller involving art, history, and religion", new BigDecimal("15.99"),
                categories.get(3), UUID.randomUUID());
            
            createBookWithCategory("Steve Jobs", "Walter Isaacson", "9781451648539",
                "The exclusive biography of Apple's co-founder", new BigDecimal("17.99"),
                categories.get(6), UUID.randomUUID());
            
            createBookWithCategory("Clean Code", "Robert C. Martin", "9780132350884",
                "A handbook of agile software craftsmanship", new BigDecimal("42.99"),
                categories.get(8), UUID.randomUUID());
            
            createBookWithCategory("The Lean Startup", "Eric Ries", "9780307887894",
                "How today's entrepreneurs use continuous innovation", new BigDecimal("18.99"),
                categories.get(9), UUID.randomUUID());
            
            log.info("Successfully created 10 sample books with category relationships");
        } catch (Exception e) {
            log.error("Error creating books: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    private void updateBookImages() {
        try {
            List<Book> books = bookRepository.findAll();
            log.info("Found {} books to check for image updates", books.size());
            
            int updatedCount = 0;
            for (Book book : books) {
                if (book.getCoverImageUrl() == null || book.getCoverImageUrl().isEmpty()) {
                    book.setCoverImageUrl("asset/img/books/the-great-gatsby.png");
                    bookRepository.save(book);
                    log.info("Updated cover image for: {}", book.getTitle());
                    updatedCount++;
                }
            }
            log.info("Book image updates completed! Updated {} books", updatedCount);
        } catch (Exception e) {
            log.error("Error updating book images: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    private Category createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setIsActive(true);
        category.setVersion(0L);
        return category;
    }
    
    private void createBookWithCategory(String title, String author, String isbn, String description, 
                                      BigDecimal price, Category category, UUID sellerId) {
        try {
            // Create the book first
            Book book = new Book();
            book.setTitle(title);
            book.setAuthor(author);
            book.setIsbn(isbn);
            book.setDescription(description);
            book.setPrice(price);
            
            // Set varied stock quantities based on book popularity and type
            int stockQuantity = getStockQuantityForBook(title, category.getName());
            book.setStockQuantity(stockQuantity);
            
            book.setSellerId(sellerId);
            book.setIsActive(true);
            book.setRating(BigDecimal.valueOf(3.5 + Math.random() * 1.5)); // Random rating 3.5-5.0
            book.setReviewCount((int) (Math.random() * 100)); // Random review count
            book.setCoverImageUrl("asset/img/books/the-great-gatsby.png");
            book.setVersion(0L);
            
            // Save the book first
            Book savedBook = bookRepository.save(book);
            log.info("Saved book: {} (ID: {}, Stock: {})", savedBook.getTitle(), savedBook.getId(), savedBook.getStockQuantity());
            
            // Create the BookCategory relationship
            BookCategory bookCategory = new BookCategory();
            bookCategory.setBook(savedBook);
            bookCategory.setCategory(category);
            bookCategory.setIsPrimary(true); // Set as primary category
            
            // Save the BookCategory relationship using its repository
            BookCategory savedBookCategory = bookCategoryRepository.save(bookCategory);
            log.info("Saved BookCategory relationship: Book ID: {}, Category ID: {}, Primary: {}", 
                    savedBookCategory.getBook().getId(), savedBookCategory.getCategory().getId(), 
                    savedBookCategory.getIsPrimary());
            
            log.info("Created book '{}' with category '{}' (Book ID: {}, Category ID: {}, Stock: {})", 
                    title, category.getName(), savedBook.getId(), category.getId(), savedBook.getStockQuantity());
        } catch (Exception e) {
            log.error("Error creating book '{}': {}", title, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Determine stock quantity based on book title and category
     * Popular books get higher quantities, niche books get lower quantities
     */
    private int getStockQuantityForBook(String title, String categoryName) {
        // Popular classics and bestsellers get higher stock
        if (title.contains("Great Gatsby") || title.contains("To Kill a Mockingbird") || 
            title.contains("1984") || title.contains("Pride and Prejudice")) {
            return 100 + (int)(Math.random() * 50); // 100-150 units
        }
        
        // Technology and business books get moderate stock
        if (categoryName.equals("Technology") || categoryName.equals("Business")) {
            return 75 + (int)(Math.random() * 25); // 75-100 units
        }
        
        // Science fiction and mystery get good stock
        if (categoryName.equals("Science Fiction") || categoryName.equals("Mystery")) {
            return 60 + (int)(Math.random() * 30); // 60-90 units
        }
        
        // Romance and biography get moderate stock
        if (categoryName.equals("Romance") || categoryName.equals("Biography")) {
            return 50 + (int)(Math.random() * 25); // 50-75 units
        }
        
        // Self-help and history get lower stock
        if (categoryName.equals("Self-Help") || categoryName.equals("History")) {
            return 30 + (int)(Math.random() * 20); // 30-50 units
        }
        
        // Fiction and non-fiction get varied stock
        if (categoryName.equals("Fiction")) {
            return 40 + (int)(Math.random() * 35); // 40-75 units
        }
        
        if (categoryName.equals("Non-Fiction")) {
            return 35 + (int)(Math.random() * 30); // 35-65 units
        }
        
        // Default stock for any other category
        return 25 + (int)(Math.random() * 25); // 25-50 units
    }
} 
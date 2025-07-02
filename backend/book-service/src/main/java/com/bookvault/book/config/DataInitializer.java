package com.bookvault.book.config;

import com.bookvault.book.model.Book;
import com.bookvault.book.model.BookCategory;
import com.bookvault.book.model.Category;
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
    
    public DataInitializer(CategoryRepository categoryRepository, BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            log.info("Initializing sample data...");
            initializeCategories();
            initializeBooksWithCategories();
            log.info("Sample data initialization completed!");
        } else {
            log.info("Sample data already exists, checking for image updates...");
            updateBookImages();
        }
    }
    
    private void initializeCategories() {
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
        
        categoryRepository.saveAll(categories);
        log.info("Created {} categories", categories.size());
    }
    
    private void initializeBooksWithCategories() {
        List<Category> categories = categoryRepository.findAll();
        
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
        
        log.info("Created {} sample books with category relationships", 10);
    }
    
    private void updateBookImages() {
        bookRepository.findAll().forEach(book -> {
            if (book.getCoverImageUrl() == null || book.getCoverImageUrl().isEmpty()) {
                book.setCoverImageUrl("asset/img/books/the-great-gatsby.png");
                bookRepository.save(book);
                log.info("Updated cover image for: {}", book.getTitle());
            }
        });
        log.info("Book image updates completed!");
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
        // Create the book first
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setIsbn(isbn);
        book.setDescription(description);
        book.setPrice(price);
        book.setStockQuantity(50);
        book.setSellerId(sellerId);
        book.setIsActive(true);
        book.setRating(BigDecimal.valueOf(3.5 + Math.random() * 1.5)); // Random rating 3.5-5.0
        book.setReviewCount((int) (Math.random() * 100)); // Random review count
        book.setCoverImageUrl("asset/img/books/the-great-gatsby.png");
        book.setVersion(0L);
        
        // Save the book first
        Book savedBook = bookRepository.save(book);
        
        // Create the BookCategory relationship
        BookCategory bookCategory = new BookCategory();
        bookCategory.setBook(savedBook);
        bookCategory.setCategory(category);
        bookCategory.setIsPrimary(true); // Set as primary category
        
        // Initialize the bookCategories list and add the relationship
        savedBook.setBookCategories(Arrays.asList(bookCategory));
        
        // Save the book again with the category relationship
        bookRepository.save(savedBook);
        
        log.info("Created book '{}' with category '{}'", title, category.getName());
    }
} 
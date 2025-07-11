# Backend Admin Permission Fixes Required

## 🚨 Issue

Admin users are getting **403 Forbidden** errors when trying to:
- Delete books via `/api/books/{id}` (DELETE)
- Edit books via `/api/books/{id}` (PUT)

**Error Message**: "You do not have permission to perform this action."

## 🔍 Root Cause Analysis

The **book-service** endpoints for book management are currently restricted to:
1. **Seller role** - Can only manage their own books
2. **Book ownership verification** - Users can only edit/delete books they created

**Admin users** need **full permissions** to manage ALL books regardless of ownership.

## 🛠️ Required Backend Fixes

### 1. **Update Book Service Authorization**

**File**: `backend/book-service/src/main/java/com/bookvault/book/config/SecurityConfig.java`

**Current Issue**: Book management endpoints only allow seller access with ownership checks.

**Required Changes**:
```java
// Add admin bypass for book management
@PreAuthorize("hasRole('ADMIN') or (hasRole('SELLER') and @bookService.isBookOwner(#bookId, authentication.name))")
```

### 2. **Update Book Controller Methods**

**File**: `backend/book-service/src/main/java/com/bookvault/book/controller/BookController.java`

**Add Admin Checks**:
```java
@DeleteMapping("/{id}")
@PreAuthorize("hasRole('ADMIN') or (hasRole('SELLER') and @bookService.isBookOwner(#id, authentication.name))")
public ResponseEntity<?> deleteBook(@PathVariable String id, Authentication auth) {
    // Admin can delete any book, sellers can only delete their own
    if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
        // Admin deletion - no ownership check
        bookService.deleteBook(id);
    } else {
        // Seller deletion - with ownership check
        bookService.deleteBookBySeller(id, auth.getName());
    }
    return ResponseEntity.ok().build();
}

@PutMapping("/{id}")
@PreAuthorize("hasRole('ADMIN') or (hasRole('SELLER') and @bookService.isBookOwner(#id, authentication.name))")
public ResponseEntity<?> updateBook(@PathVariable String id, @RequestBody BookUpdateRequest request, Authentication auth) {
    // Admin can edit any book, sellers can only edit their own
    if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
        // Admin update - no ownership check
        return ResponseEntity.ok(bookService.updateBook(id, request));
    } else {
        // Seller update - with ownership check
        return ResponseEntity.ok(bookService.updateBookBySeller(id, request, auth.getName()));
    }
}
```

### 3. **Update Book Service Methods**

**File**: `backend/book-service/src/main/java/com/bookvault/book/service/BookService.java`

**Add Admin Methods**:
```java
// Admin can delete any book
public void deleteBook(String bookId) {
    bookRepository.deleteById(bookId);
}

// Admin can update any book
public BookResponse updateBook(String bookId, BookUpdateRequest request) {
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new NotFoundException("Book not found"));
    
    // Update book fields
    book.setTitle(request.getTitle());
    book.setAuthor(request.getAuthor());
    book.setPrice(request.getPrice());
    book.setDescription(request.getDescription());
    book.setStockQuantity(request.getStockQuantity());
    book.setActive(request.isActive());
    
    Book savedBook = bookRepository.save(book);
    return BookMapper.toBookResponse(savedBook);
}

// Seller methods with ownership checks (existing)
public void deleteBookBySeller(String bookId, String sellerEmail) {
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new NotFoundException("Book not found"));
    
    if (!book.getSellerEmail().equals(sellerEmail)) {
        throw new UnauthorizedException("You can only delete your own books");
    }
    
    bookRepository.delete(book);
}
```

### 4. **JWT Token Validation**

**Ensure Admin Role is Properly Set**:
```java
// In JWT processing
if (user.getRole() == UserRole.ADMIN) {
    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
}
```

## 🔧 Quick Fix Implementation

### **Immediate Solution** (Recommended):

**File**: `backend/book-service/src/main/java/com/bookvault/book/controller/BookController.java`

```java
// Add to the beginning of delete and update methods
private boolean isAdmin(Authentication auth) {
    return auth.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
}

@DeleteMapping("/{id}")
public ResponseEntity<?> deleteBook(@PathVariable String id, Authentication auth) {
    if (isAdmin(auth)) {
        // Admin can delete any book
        bookService.deleteById(id);
    } else {
        // Existing seller logic with ownership check
        bookService.deleteBookBySeller(id, auth.getName());
    }
    return ResponseEntity.ok().build();
}

@PutMapping("/{id}")
public ResponseEntity<?> updateBook(@PathVariable String id, @RequestBody BookUpdateRequest request, Authentication auth) {
    if (isAdmin(auth)) {
        // Admin can update any book
        return ResponseEntity.ok(bookService.updateBookByAdmin(id, request));
    } else {
        // Existing seller logic with ownership check
        return ResponseEntity.ok(bookService.updateBookBySeller(id, request, auth.getName()));
    }
}
```

## 🧪 Testing After Fixes

### Test Admin Permissions:
1. **Login as Admin** → Get JWT token
2. **Call DELETE /api/books/{any-book-id}** → Should succeed
3. **Call PUT /api/books/{any-book-id}** → Should succeed

### Test Seller Permissions (ensure not broken):
1. **Login as Seller** → Get JWT token  
2. **Call DELETE /api/books/{own-book-id}** → Should succeed
3. **Call DELETE /api/books/{other-book-id}** → Should fail with 403
4. **Call PUT /api/books/{own-book-id}** → Should succeed
5. **Call PUT /api/books/{other-book-id}** → Should fail with 403

## 📋 Deployment Steps

1. **Update book-service code** with admin permission fixes
2. **Rebuild book-service**: `mvn clean package`
3. **Redeploy to Railway**
4. **Test admin functionality** in frontend
5. **Verify seller functionality** still works

## 🚀 Expected Result

After implementing these fixes:
- ✅ Admin can delete any book from admin panel
- ✅ Admin can edit any book from admin panel
- ✅ Sellers can still only manage their own books
- ✅ Proper error handling and authorization

---

**Priority**: **HIGH** - Admin functionality is currently broken
**Impact**: Admin users cannot perform book management tasks
**Effort**: **Medium** - Requires backend authorization logic updates 
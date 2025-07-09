package com.bookvault.book.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for seller revenue analytics
 */
public class SellerRevenueResponse {
    
    private UUID sellerId;
    private String sellerName;
    
    // Overall statistics
    private Long totalBooks;
    private Long totalSoldItems;
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    
    // Time-based statistics
    private BigDecimal monthlyRevenue;
    private BigDecimal yearlyRevenue;
    private BigDecimal weeklyRevenue;
    
    // Performance metrics
    private BigDecimal revenueGrowth;
    private Long orderCount;
    private Long activeBooks;
    private Long lowStockBooks;
    
    // Top performing books
    private List<BookRevenueItem> topPerformingBooks;
    
    // Revenue breakdown by period
    private List<RevenuePeriod> revenueBreakdown;
    
    // Recent orders
    private List<SellerOrderItem> recentOrders;
    
    // Default constructor
    public SellerRevenueResponse() {}
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private UUID sellerId;
        private String sellerName;
        private Long totalBooks;
        private Long totalSoldItems;
        private BigDecimal totalRevenue;
        private BigDecimal averageOrderValue;
        private BigDecimal monthlyRevenue;
        private BigDecimal yearlyRevenue;
        private BigDecimal weeklyRevenue;
        private BigDecimal revenueGrowth;
        private Long orderCount;
        private Long activeBooks;
        private Long lowStockBooks;
        private List<BookRevenueItem> topPerformingBooks;
        private List<RevenuePeriod> revenueBreakdown;
        private List<SellerOrderItem> recentOrders;
        
        public Builder sellerId(UUID sellerId) {
            this.sellerId = sellerId;
            return this;
        }
        
        public Builder sellerName(String sellerName) {
            this.sellerName = sellerName;
            return this;
        }
        
        public Builder totalBooks(Long totalBooks) {
            this.totalBooks = totalBooks;
            return this;
        }
        
        public Builder totalSoldItems(Long totalSoldItems) {
            this.totalSoldItems = totalSoldItems;
            return this;
        }
        
        public Builder totalRevenue(BigDecimal totalRevenue) {
            this.totalRevenue = totalRevenue;
            return this;
        }
        
        public Builder averageOrderValue(BigDecimal averageOrderValue) {
            this.averageOrderValue = averageOrderValue;
            return this;
        }
        
        public Builder monthlyRevenue(BigDecimal monthlyRevenue) {
            this.monthlyRevenue = monthlyRevenue;
            return this;
        }
        
        public Builder yearlyRevenue(BigDecimal yearlyRevenue) {
            this.yearlyRevenue = yearlyRevenue;
            return this;
        }
        
        public Builder weeklyRevenue(BigDecimal weeklyRevenue) {
            this.weeklyRevenue = weeklyRevenue;
            return this;
        }
        
        public Builder revenueGrowth(BigDecimal revenueGrowth) {
            this.revenueGrowth = revenueGrowth;
            return this;
        }
        
        public Builder orderCount(Long orderCount) {
            this.orderCount = orderCount;
            return this;
        }
        
        public Builder activeBooks(Long activeBooks) {
            this.activeBooks = activeBooks;
            return this;
        }
        
        public Builder lowStockBooks(Long lowStockBooks) {
            this.lowStockBooks = lowStockBooks;
            return this;
        }
        
        public Builder topPerformingBooks(List<BookRevenueItem> topPerformingBooks) {
            this.topPerformingBooks = topPerformingBooks;
            return this;
        }
        
        public Builder revenueBreakdown(List<RevenuePeriod> revenueBreakdown) {
            this.revenueBreakdown = revenueBreakdown;
            return this;
        }
        
        public Builder recentOrders(List<SellerOrderItem> recentOrders) {
            this.recentOrders = recentOrders;
            return this;
        }
        
        public SellerRevenueResponse build() {
            return new SellerRevenueResponse(
                sellerId, sellerName, totalBooks, totalSoldItems, totalRevenue,
                averageOrderValue, monthlyRevenue, yearlyRevenue, weeklyRevenue,
                revenueGrowth, orderCount, activeBooks, lowStockBooks,
                topPerformingBooks, revenueBreakdown, recentOrders
            );
        }
    }
    
    // Constructor
    public SellerRevenueResponse(UUID sellerId, String sellerName, Long totalBooks,
                               Long totalSoldItems, BigDecimal totalRevenue, BigDecimal averageOrderValue,
                               BigDecimal monthlyRevenue, BigDecimal yearlyRevenue, BigDecimal weeklyRevenue,
                               BigDecimal revenueGrowth, Long orderCount, Long activeBooks, Long lowStockBooks,
                               List<BookRevenueItem> topPerformingBooks, List<RevenuePeriod> revenueBreakdown,
                               List<SellerOrderItem> recentOrders) {
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.totalBooks = totalBooks;
        this.totalSoldItems = totalSoldItems;
        this.totalRevenue = totalRevenue;
        this.averageOrderValue = averageOrderValue;
        this.monthlyRevenue = monthlyRevenue;
        this.yearlyRevenue = yearlyRevenue;
        this.weeklyRevenue = weeklyRevenue;
        this.revenueGrowth = revenueGrowth;
        this.orderCount = orderCount;
        this.activeBooks = activeBooks;
        this.lowStockBooks = lowStockBooks;
        this.topPerformingBooks = topPerformingBooks;
        this.revenueBreakdown = revenueBreakdown;
        this.recentOrders = recentOrders;
    }
    
    // Getters and Setters
    public UUID getSellerId() { return sellerId; }
    public void setSellerId(UUID sellerId) { this.sellerId = sellerId; }
    
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    
    public Long getTotalBooks() { return totalBooks; }
    public void setTotalBooks(Long totalBooks) { this.totalBooks = totalBooks; }
    
    public Long getTotalSoldItems() { return totalSoldItems; }
    public void setTotalSoldItems(Long totalSoldItems) { this.totalSoldItems = totalSoldItems; }
    
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    
    public BigDecimal getAverageOrderValue() { return averageOrderValue; }
    public void setAverageOrderValue(BigDecimal averageOrderValue) { this.averageOrderValue = averageOrderValue; }
    
    public BigDecimal getMonthlyRevenue() { return monthlyRevenue; }
    public void setMonthlyRevenue(BigDecimal monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }
    
    public BigDecimal getYearlyRevenue() { return yearlyRevenue; }
    public void setYearlyRevenue(BigDecimal yearlyRevenue) { this.yearlyRevenue = yearlyRevenue; }
    
    public BigDecimal getWeeklyRevenue() { return weeklyRevenue; }
    public void setWeeklyRevenue(BigDecimal weeklyRevenue) { this.weeklyRevenue = weeklyRevenue; }
    
    public BigDecimal getRevenueGrowth() { return revenueGrowth; }
    public void setRevenueGrowth(BigDecimal revenueGrowth) { this.revenueGrowth = revenueGrowth; }
    
    public Long getOrderCount() { return orderCount; }
    public void setOrderCount(Long orderCount) { this.orderCount = orderCount; }
    
    public Long getActiveBooks() { return activeBooks; }
    public void setActiveBooks(Long activeBooks) { this.activeBooks = activeBooks; }
    
    public Long getLowStockBooks() { return lowStockBooks; }
    public void setLowStockBooks(Long lowStockBooks) { this.lowStockBooks = lowStockBooks; }
    
    public List<BookRevenueItem> getTopPerformingBooks() { return topPerformingBooks; }
    public void setTopPerformingBooks(List<BookRevenueItem> topPerformingBooks) { this.topPerformingBooks = topPerformingBooks; }
    
    public List<RevenuePeriod> getRevenueBreakdown() { return revenueBreakdown; }
    public void setRevenueBreakdown(List<RevenuePeriod> revenueBreakdown) { this.revenueBreakdown = revenueBreakdown; }
    
    public List<SellerOrderItem> getRecentOrders() { return recentOrders; }
    public void setRecentOrders(List<SellerOrderItem> recentOrders) { this.recentOrders = recentOrders; }
    
    /**
     * Inner class for book revenue items
     */
    public static class BookRevenueItem {
        private UUID bookId;
        private String bookTitle;
        private String bookAuthor;
        private String coverImageUrl;
        private Long quantitySold;
        private BigDecimal totalRevenue;
        private BigDecimal averageRating;
        
        public BookRevenueItem() {}
        
        public BookRevenueItem(UUID bookId, String bookTitle, String bookAuthor, String coverImageUrl,
                              Long quantitySold, BigDecimal totalRevenue, BigDecimal averageRating) {
            this.bookId = bookId;
            this.bookTitle = bookTitle;
            this.bookAuthor = bookAuthor;
            this.coverImageUrl = coverImageUrl;
            this.quantitySold = quantitySold;
            this.totalRevenue = totalRevenue;
            this.averageRating = averageRating;
        }
        
        // Getters and Setters
        public UUID getBookId() { return bookId; }
        public void setBookId(UUID bookId) { this.bookId = bookId; }
        
        public String getBookTitle() { return bookTitle; }
        public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
        
        public String getBookAuthor() { return bookAuthor; }
        public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }
        
        public String getCoverImageUrl() { return coverImageUrl; }
        public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
        
        public Long getQuantitySold() { return quantitySold; }
        public void setQuantitySold(Long quantitySold) { this.quantitySold = quantitySold; }
        
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
        
        public BigDecimal getAverageRating() { return averageRating; }
        public void setAverageRating(BigDecimal averageRating) { this.averageRating = averageRating; }
    }
    
    /**
     * Inner class for revenue periods
     */
    public static class RevenuePeriod {
        private String period;
        private BigDecimal revenue;
        private Long orderCount;
        private Long itemsSold;
        
        public RevenuePeriod() {}
        
        public RevenuePeriod(String period, BigDecimal revenue, Long orderCount, Long itemsSold) {
            this.period = period;
            this.revenue = revenue;
            this.orderCount = orderCount;
            this.itemsSold = itemsSold;
        }
        
        // Getters and Setters
        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }
        
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        
        public Long getOrderCount() { return orderCount; }
        public void setOrderCount(Long orderCount) { this.orderCount = orderCount; }
        
        public Long getItemsSold() { return itemsSold; }
        public void setItemsSold(Long itemsSold) { this.itemsSold = itemsSold; }
    }
    
    /**
     * Inner class for seller order items
     */
    public static class SellerOrderItem {
        private UUID orderId;
        private String orderNumber;
        private String bookTitle;
        private String bookAuthor;
        private String coverImageUrl;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        private String customerName;
        private String customerEmail;
        private LocalDateTime orderDate;
        private String orderStatus;
        
        public SellerOrderItem() {}
        
        public SellerOrderItem(UUID orderId, String orderNumber, String bookTitle, String bookAuthor,
                              String coverImageUrl, Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice,
                              String customerName, String customerEmail, LocalDateTime orderDate, String orderStatus) {
            this.orderId = orderId;
            this.orderNumber = orderNumber;
            this.bookTitle = bookTitle;
            this.bookAuthor = bookAuthor;
            this.coverImageUrl = coverImageUrl;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = totalPrice;
            this.customerName = customerName;
            this.customerEmail = customerEmail;
            this.orderDate = orderDate;
            this.orderStatus = orderStatus;
        }
        
        // Getters and Setters
        public UUID getOrderId() { return orderId; }
        public void setOrderId(UUID orderId) { this.orderId = orderId; }
        
        public String getOrderNumber() { return orderNumber; }
        public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
        
        public String getBookTitle() { return bookTitle; }
        public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
        
        public String getBookAuthor() { return bookAuthor; }
        public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }
        
        public String getCoverImageUrl() { return coverImageUrl; }
        public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        
        public BigDecimal getTotalPrice() { return totalPrice; }
        public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
        
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        
        public String getCustomerEmail() { return customerEmail; }
        public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
        
        public LocalDateTime getOrderDate() { return orderDate; }
        public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
        
        public String getOrderStatus() { return orderStatus; }
        public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    }
} 
package com.bookvault.shared.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Generic paged response wrapper for API responses
 * @param <T> The type of content in the response
 */
public class PagedResponse<T> {
    
    @JsonProperty("content")
    private List<T> content;
    
    @JsonProperty("page")
    private int page;
    
    @JsonProperty("size")
    private int size;
    
    @JsonProperty("totalElements")
    private long totalElements;
    
    @JsonProperty("totalPages")
    private int totalPages;
    
    @JsonProperty("first")
    private boolean first;
    
    @JsonProperty("last")
    private boolean last;
    
    @JsonProperty("hasNext")
    private boolean hasNext;
    
    @JsonProperty("hasPrevious")
    private boolean hasPrevious;

    // Default constructor
    public PagedResponse() {}

    // Constructor with all fields
    public PagedResponse(List<T> content, int page, int size, long totalElements, int totalPages) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = page == 0;
        this.last = page >= totalPages - 1;
        this.hasNext = page < totalPages - 1;
        this.hasPrevious = page > 0;
    }

    // Getters and Setters
    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
        this.first = page == 0;
        this.hasPrevious = page > 0;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
        this.last = page >= totalPages - 1;
        this.hasNext = page < totalPages - 1;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public boolean isEmpty() {
        return content == null || content.isEmpty();
    }
    
    public int getNumberOfElements() {
        return content != null ? content.size() : 0;
    }

    // Builder pattern
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<T> {
        private List<T> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;
        private boolean hasNext;
        private boolean hasPrevious;
        private int numberOfElements;

        public Builder<T> content(List<T> content) {
            this.content = content;
            this.numberOfElements = content != null ? content.size() : 0;
            return this;
        }

        public Builder<T> page(int page) {
            this.page = page;
            this.first = page == 0;
            this.hasPrevious = page > 0;
            return this;
        }

        public Builder<T> size(int size) {
            this.size = size;
            return this;
        }

        public Builder<T> totalElements(long totalElements) {
            this.totalElements = totalElements;
            return this;
        }

        public Builder<T> totalPages(int totalPages) {
            this.totalPages = totalPages;
            this.last = page >= totalPages - 1;
            this.hasNext = page < totalPages - 1;
            return this;
        }

        public Builder<T> first(boolean first) {
            this.first = first;
            return this;
        }

        public Builder<T> last(boolean last) {
            this.last = last;
            return this;
        }

        public Builder<T> hasNext(boolean hasNext) {
            this.hasNext = hasNext;
            return this;
        }

        public Builder<T> hasPrevious(boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
            return this;
        }

        public Builder<T> numberOfElements(int numberOfElements) {
            this.numberOfElements = numberOfElements;
            return this;
        }

        public PagedResponse<T> build() {
            PagedResponse<T> response = new PagedResponse<>();
            response.content = this.content;
            response.page = this.page;
            response.size = this.size;
            response.totalElements = this.totalElements;
            response.totalPages = this.totalPages;
            response.first = this.first;
            response.last = this.last;
            response.hasNext = this.hasNext;
            response.hasPrevious = this.hasPrevious;
            return response;
        }
    }
} 
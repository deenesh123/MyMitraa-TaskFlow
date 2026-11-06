package com.mymitraa.taskflow.api_mv;

import java.io.Serializable;
import java.util.List;

public class PaginationDataMV implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<?> content;
    private int number;
    private int numberOfElements;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public List<?> getContent() {
        return content;
    }

    public void setContent(List<?> content) {
        this.content = content;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(int numberOfElements) {
        this.numberOfElements = numberOfElements;
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
        if (size > 0) {
            this.totalPages = (int) Math.ceil((double) totalElements / size);
        }
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isFirst() {
        return number == 0;
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
}

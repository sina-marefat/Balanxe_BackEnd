package ir.balanxe.configs.customresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PageableResponse<T> {
    @JsonProperty("current_page")
    private int currentPage;

    @JsonProperty("total_page")
    private int totalPage;

    @JsonProperty("size")
    private int size;

    @JsonProperty("data")
    private T data;

    public PageableResponse() {
    }

    public PageableResponse(int currentPage, int totalPage, int size, T data) {
        this.currentPage = currentPage;
        this.totalPage = totalPage;
        this.size = size;
        this.data = data;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

package com.efhemo.movienano.model;

import java.util.List;

public class ReviewResponse {

    private int id;
    private int page;
    private List<Review> results;

    public ReviewResponse(int id, int page, List<Review> reviewList) {
        this.id = id;
        this.page = page;
        this.results = reviewList;
    }

    public int getId() {
        return id;
    }

    public int getPage() {
        return page;
    }

    public List<Review> getReviewList() {
        return results;
    }
}

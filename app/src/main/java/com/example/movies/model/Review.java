package com.example.movies.model;

public class Review {
    private int movieId;
    private String review, fullName;
    private boolean anonymous;

    public Review(String fullName , int movieId , String review, boolean anonymous){
        this.fullName = fullName;
        this.movieId = movieId;
        this.review = review;
        this.anonymous = anonymous;
    }

    public String getFullName() {
        return fullName;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getReview() {
        return review;
    }

    public boolean isAnonymous() {
        return anonymous;
    }
}

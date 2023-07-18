package com.example.movies.model;

public class Movie {
    private int id;
    private String title , poster , overview, genre, releaseDate, casts;
    private Double rating;

    public Movie(String title , String poster , String overview , Double rating, int id, String genre, String releaseDate, String casts){
        this.title = title;
        this.poster = poster;
        this.overview = overview;
        this.rating = rating;
        this.id = id;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.casts = casts;
    }

    public String getTitle() {
        return title;
    }

    public String getPoster() {
        return poster;
    }

    public String getOverview() {
        return overview;
    }

    public Double getRating() {
        return rating;
    }

    public int getId() {
        return id;
    }

    public String getGenre() {
        return genre;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getCasts() {
        return casts;
    }
}

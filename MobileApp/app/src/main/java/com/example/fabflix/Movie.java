package com.example.fabflix;

public class Movie {
    private String name;
    private short year;
    private short director;
    private String genres;
    private String stars;

    public Movie(String name, short year) {
        this.name = name;
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public short getYear() {
        return year;
    }
}
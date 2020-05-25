package com.example.fabflix;

public class Movie {
    private String id;
    private String name;
    private short year;
    private String director;
    private String[] genres;
    private String[] stars;

    public Movie(String id, String name, short year, String director, String[] genres, String[] stars) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public short getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public String getGenres() {
        String result = "";
        for(int i = 0; i < 3; ++i){
            if(genres[i] != null) {
                if(i!=0)
                result += ", ";
                result += genres[i];
            }
        }
        return result;
    }

    public String getStars() {
        String result = "";
        for(int i = 0; i < 3; ++i){
            if(stars[i] != null) {
                if (i != 0)
                    result += ", ";
                result += stars[i];
            }
        }
        return result;
    }
}
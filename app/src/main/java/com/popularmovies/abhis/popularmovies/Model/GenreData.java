package com.popularmovies.abhis.popularmovies.Model;

public class GenreData {
    int id;
    String name;

    public GenreData() {
    }

    public GenreData(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

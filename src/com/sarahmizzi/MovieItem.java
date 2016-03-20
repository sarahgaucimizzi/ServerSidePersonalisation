package com.sarahmizzi;

/**
 * Created by Sarah on 18-Mar-16.
 */
public class MovieItem extends Movie {
    String id;

    public MovieItem(String title, String year, String id) {
        super(title, year);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

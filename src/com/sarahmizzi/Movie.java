package com.sarahmizzi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Sarah on 18-Mar-16.
 */
public class Movie {
    String title;
    String year;

    @JsonCreator
    public Movie(@JsonProperty("title") String title,
                 @JsonProperty("year") String year) {
        this.title = title;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}

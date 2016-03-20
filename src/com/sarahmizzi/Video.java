package com.sarahmizzi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Sarah on 16-Mar-16.
 */
public class Video {
    String id;
    String title;
    String category;
    String[] tags;

    @JsonCreator
    public Video(@JsonProperty("id") String id,
                 @JsonProperty("title") String title,
                 @JsonProperty("category") String category,
                 @JsonProperty("tags") String[] tags) {

        this.id = id;
        this.title = title;
        this.category = category;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }
}

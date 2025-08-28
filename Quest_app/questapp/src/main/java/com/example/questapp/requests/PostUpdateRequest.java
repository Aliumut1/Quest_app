package com.example.questapp.requests;

import lombok.Data;

@Data

public class PostUpdateRequest {


    String title;
    String text;

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}

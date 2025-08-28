package com.example.questapp.responses;

import com.example.questapp.entities.Comment;

public class CommentResponse {
    Long id;
    Long userId;
    String text;
    String userName;
    Long postId; // EKLENECEK ALAN

    public CommentResponse(Comment entity) {
        this.id = entity.getId();
        this.userId = entity.getUser().getId();
        this.text = entity.getText();
        this.userName = entity.getUser().getUserName();
        this.postId = entity.getPost().getId(); // CONSTRUCTOR'A EKLENECEK SATIR
    }

    // Var olan getter ve setter'lar...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.id = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // YENİ EKLENECEK GETTER VE SETTER
    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }
}
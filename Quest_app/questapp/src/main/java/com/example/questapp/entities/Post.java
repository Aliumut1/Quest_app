package com.example.questapp.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.repository.Temporal;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name="post")
@Data


//http://localhost:8080/posts?userId=1 postmanden böyle çekiyoruz
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)//postu çektiğimde user bana direkt gelmeyecek. lazım olmadığını düşünüyorum. Eager yaptık ve jsonignore kaldırdık
    @JoinColumn(name="user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE) //user silindiğinde post da silinsin

    User user;
    //Long userId;
    String title;
    @Lob
    @Column(columnDefinition="text") //Stringi text olarak algılasın diyeymiş. Yoksa varchar 255 olarak alıyormuş.
    String text;

    LocalDateTime createDate;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }
}

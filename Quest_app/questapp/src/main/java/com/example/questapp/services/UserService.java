package com.example.questapp.services;

import com.example.questapp.entities.Comment;
import com.example.questapp.entities.Like;
import com.example.questapp.entities.User;
import com.example.questapp.repos.CommentRepository;
import com.example.questapp.repos.LikeRepository;
import com.example.questapp.repos.PostRepository;
import com.example.questapp.repos.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    UserRepository userRepository;
    LikeRepository likeRepository;
    CommentRepository commentRepository;
    PostRepository postRepository;

    public UserService(UserRepository userRepository, LikeRepository likeRepository, CommentRepository commentRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveOneUser(User newUser) {
        return userRepository.save(newUser);
    }

    public User getOneUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    // ====> SADECE BU METOT DEĞİŞTİRİLDİ (NİHAİ VE DOĞRU HALİ) <====
    public User updateOneUser(Long userId, User newUser) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()){
            User foundUser = user.get();

            // Sadece frontend'den gelen ve null olmayan alanları güncelle.
            // Bu, mevcut şifrenin veya kullanıcı adının yanlışlıkla 'null' ile ezilmesini ENGELLER.
            if (newUser.getUserName() != null) {
                foundUser.setUserName(newUser.getUserName());
            }
            // Avatar 'int' olduğu için null olamaz, bu yüzden doğrudan set edebiliriz.
            foundUser.setAvatar(newUser.getAvatar());

            // Değiştirilmiş 'foundUser' objesini veritabanına kaydet.
            return userRepository.save(foundUser);
        } else {
            // Güncellenecek kullanıcı bulunamadıysa null dön.
            return null;
        }
    }

    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }

    public User getOneUserByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public List<Object> getUserActivity(Long userId) {
        List<Long> postIds = postRepository.findTopByUserId(userId);
        if(postIds.isEmpty()){
            // Boş liste döndürmek null'dan daha güvenlidir.
            return new ArrayList<>();
        }
        List<Object> comments = commentRepository.findUserCommentsByPostId(postIds);
        List<Object> likes = likeRepository.findUserLikesByPostId(postIds);
        List<Object> result = new ArrayList<>();
        result.addAll(comments);
        result.addAll(likes);
        return result;
    }
}
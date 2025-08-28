package com.example.questapp.services;

import com.example.questapp.entities.Post;
import com.example.questapp.entities.User;
import com.example.questapp.repos.PostRepository;
import com.example.questapp.requests.PostCreateRequest;
import com.example.questapp.requests.PostUpdateRequest;
import com.example.questapp.responses.LikeResponse;
import com.example.questapp.responses.PostResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {
    private PostRepository postRepository;
    private LikeService likeService;
    private UserService userService;

    public PostService(PostRepository postRepository, UserService userService, LikeService likeService) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.likeService = likeService;
    }

    // ... getAllPosts, getOnePostById, createOnePost metodları aynı kalıyor ...
    public List<PostResponse> getAllPosts(Optional<Long> userId) {
        List<Post> list;
        if(userId.isPresent()) {
            list = postRepository.findByUserIdOrderByCreateDateDesc(userId.get());
        } else {
            list = postRepository.findAllByOrderByCreateDateDesc();
        }
        return list.stream().map(p -> {
            List<LikeResponse> likes = likeService.getAllLikesWithParam(Optional.ofNullable(null), Optional.of(p.getId()));
            return new PostResponse(p, likes);}).collect(Collectors.toList());
    }

    public Post getOnePostById(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }

    public PostResponse getOnePostByIdWithLikes(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        List<LikeResponse> likes = likeService.getAllLikesWithParam(Optional.ofNullable(null), Optional.of(postId));
        return new PostResponse(post, likes);
    }

    public Post createOnePost(PostCreateRequest newPostRequest) {
        User user = userService.getOneUserById(newPostRequest.getUserId());
        if(user == null)
            return null;
        Post toSave = new Post();
        toSave.setId(newPostRequest.getId());
        toSave.setText(newPostRequest.getText());
        toSave.setTitle(newPostRequest.getTitle());
        toSave.setUser(user);
        toSave.setCreateDate(LocalDateTime.now());
        return postRepository.save(toSave);
    }

    // ================================================================
    // GÜNCELLENMİŞ VE GÜVENLİ METOT
    // ================================================================
    public Post updateOnePostById(Long postId, PostUpdateRequest updatePost) {
        // 1. Giriş yapmış olan kullanıcının kimliğini al
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.getOneUserByUserName(username);

        // 2. Güncellenmek istenen postu veritabanından bul
        Optional<Post> postOptional = postRepository.findById(postId);

        if (postOptional.isPresent()) {
            Post postToUpdate = postOptional.get();

            // 3. GÜVENLİK KONTROLÜ: Postun sahibi, giriş yapmış kullanıcı mı?
            if (postToUpdate.getUser().getId().equals(currentUser.getId())) {
                // Sahiplik doğruysa, başlık ve metin alanlarını güncelle
                postToUpdate.setTitle(updatePost.getTitle());
                postToUpdate.setText(updatePost.getText());

                // Güncellenmiş postu veritabanına kaydet ve döndür
                return postRepository.save(postToUpdate);
            } else {
                // Başkasının postunu düzenlemeye çalışıyorsa, işlemi engellemek için hata fırlat
                throw new SecurityException("Bu postu düzenleme yetkiniz yok.");
            }
        }

        // Post bulunamazsa null döndür
        return null;
    }

    // deleteOnePostById metodu aynı kalıyor
    public void deleteOnePostById(Long postId) {
        // ... (bir önceki adımdaki güvenli hali)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userService.getOneUserByUserName(username);
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post postToDelete = postOptional.get();
            if (postToDelete.getUser().getId().equals(currentUser.getId())) {
                postRepository.deleteById(postId);
            } else {
                throw new SecurityException("Bu postu silme yetkiniz yok.");
            }
        } else {
            throw new RuntimeException("Post bulunamadı: " + postId);
        }
    }
}
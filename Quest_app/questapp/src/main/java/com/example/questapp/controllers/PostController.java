package com.example.questapp.controllers;

import com.example.questapp.entities.Post;
import com.example.questapp.requests.PostCreateRequest;
import com.example.questapp.requests.PostUpdateRequest;
import com.example.questapp.responses.PostResponse;
import com.example.questapp.services.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.questapp.requests.PostUpdateRequest;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {
    private PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<PostResponse> getAllPosts(@RequestParam Optional<Long> userId){
        return postService.getAllPosts(userId);
    } //postlardan oluşan liste dönecek // Request param demek bize gelen requestin içindeki parametreleri parse et ve sağındaki değişkenin içine at
    //optinal bir parametre gele de bilir gelmeye de bilir

    @PostMapping
    public Post createOnePost(@RequestBody PostCreateRequest newPostRequest){
        return postService.createOnePost(newPostRequest);
    }

    @GetMapping("/{postId}")
    public PostResponse getOnePost(@PathVariable Long postId){
        return postService.getOnePostByIdWithLikes(postId);
    }


    @PutMapping("/{postId}")
    public ResponseEntity<Post> updateOnePost(@PathVariable Long postId, @RequestBody PostUpdateRequest updateRequest) {
        Post updatedPost = postService.updateOnePostById(postId, updateRequest);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{postId}")
    public void deleteOnePost(@PathVariable Long postId){
        postService.deleteOnePostById(postId);
    }







}

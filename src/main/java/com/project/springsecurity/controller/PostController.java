package com.project.springsecurity.controller;

import com.project.springsecurity.controller.dto.CreatePostDto;
import com.project.springsecurity.entities.Post;
import com.project.springsecurity.repository.PostRepository;
import com.project.springsecurity.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostController(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/posts")
    public ResponseEntity<Void> createPost(@RequestBody CreatePostDto dto,
                                           JwtAuthenticationToken token){

        var user = userRepository.findById(UUID.fromString(token.getName()));  //token.getName()  retorna o subject do jwt

        var post = new Post();
        post.setContent(dto.content());
        post.setUser(user.get());

        postRepository.save(post);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long postId,
                                           JwtAuthenticationToken token){
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if(post.getUser().getUserId().equals(UUID.fromString(token.getName()))){
            postRepository.deleteById(postId);
        }else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok().build();
    }
}

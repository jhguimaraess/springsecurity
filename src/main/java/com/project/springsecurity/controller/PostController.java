package com.project.springsecurity.controller;

import com.project.springsecurity.controller.dto.CreatePostDto;
import com.project.springsecurity.controller.dto.FeedDto;
import com.project.springsecurity.controller.dto.FeedItemDto;
import com.project.springsecurity.repository.PostRepository;
import com.project.springsecurity.service.PostService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/posts")
    public ResponseEntity<Void> createPost(@RequestBody CreatePostDto dto, JwtAuthenticationToken token){
        postService.createPost(dto, token);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") Long postId,
                                           JwtAuthenticationToken token){

        try{
            postService.deletePost(postId, token);
            return ResponseEntity.ok().build();
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("feed")
    public ResponseEntity<FeedDto> feed(@RequestParam(value = "page", defaultValue = "0") int page,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize){
        return ResponseEntity.ok(postService.feed(page, pageSize));
    }
}

package com.project.springsecurity.service;

import com.project.springsecurity.controller.dto.CreatePostDto;
import com.project.springsecurity.controller.dto.FeedDto;
import com.project.springsecurity.controller.dto.FeedItemDto;
import com.project.springsecurity.entities.Post;
import com.project.springsecurity.entities.Role;
import com.project.springsecurity.repository.PostRepository;
import com.project.springsecurity.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public void createPost(CreatePostDto dto, JwtAuthenticationToken token){

        var user = userRepository.findById(UUID.fromString(token.getName()));  //token.getName()  retorna o subject do jwt

        var post = new Post();
        post.setContent(dto.content());
        post.setUser(user.get());

        postRepository.save(post);
    }

    public void deletePost(Long postId, JwtAuthenticationToken token){
        var post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var user = userRepository.findById(UUID.fromString(token.getName()));

        var isAdmin = user.get().getRoles()
                .stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if(isAdmin || post.getUser().getUserId().equals(UUID.fromString(token.getName()))){
            postRepository.deleteById(postId);
        }else {
            throw new RuntimeException("Acesso negado");
        }
    }

    public FeedDto feed(int page, int pageSize){
        var posts = postRepository.findAll(
                        PageRequest.of(page, pageSize, Sort.Direction.DESC, "creationTimestamp"))
                .map(post ->
                        new FeedItemDto(
                                post.getPostId(),
                                post.getContent(),
                                post.getUser().getUsername())
                );

        return new FeedDto(posts.getContent(), page, pageSize, posts.getTotalPages(), posts.getTotalElements());
    }
}

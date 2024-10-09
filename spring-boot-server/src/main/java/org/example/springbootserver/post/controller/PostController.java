package org.example.springbootserver.post.controller;

import lombok.RequiredArgsConstructor;
import org.example.springbootserver.onchain.service.NftService;
import org.example.springbootserver.post.dto.PostDTO;
import org.example.springbootserver.post.dto.PostRequestDTO;
import org.example.springbootserver.post.entity.PostEntity;
import org.example.springbootserver.post.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final NftService nftService;

    // Create a new Post
    @PostMapping("/createPost")
    public ResponseEntity<PostDTO> createPost(@RequestBody PostRequestDTO postRequestDTO) {
        PostDTO createdPostDTO = postService.createPost(postRequestDTO);
        return new ResponseEntity<>(createdPostDTO, HttpStatus.CREATED);
    }


    @PostMapping("/postImage")
    public ResponseEntity<String > postImage(@RequestParam("image") MultipartFile file) throws IOException {
        String pingRes = nftService.pingResponse();
        return new ResponseEntity<>(pingRes, HttpStatus.CREATED);
    }
//    @PostMapping("/image")
//    public void postImage(@RequestParam("image") MultipartFile file) throws IOException {
//        String pingRes = nftService.pingResponse();
//        return
//    }

    // 일단 아래는 PostEntity -> PostDTO 아직 안함

    // Get all Posts
    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> posts = postService.getAllPosts();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    // Get a Post by ID
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
        PostDTO postFound = postService.getPostById(id);
        return new ResponseEntity<>(postFound, HttpStatus.OK);
    }

    // Update a Post
    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Long id,
            @RequestBody PostRequestDTO updatedPostRequest) {
        try {
            PostDTO post = postService.updatePost(id, updatedPostRequest);
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Delete a Post
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
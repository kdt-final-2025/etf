package Etf.comment.controller;

import Etf.comment.domain.Comment;
import Etf.comment.dto.CommentRequest2;
import Etf.comment.dto.CommentResponse;
import Etf.comment.serviece.CommentLikeService;
import Etf.comment.serviece.CommentService;
import Etf.comment.serviece.CommentService2;
import Etf.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentRestController2 {

    private final CommentService2 commentService2;
    private final CommentLikeService commentLikeService;


    //CommentLike Or Not
    @PostMapping("/{commentId}/likes/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void toggleLike(
            @PathVariable Long commentId,
            @PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        commentLikeService.toggleLike(commentId, user);
    }

    //When Find Comment Show CommentLike Count
    @GetMapping("/{commentId}/user/{userId}")
    public CommentResponse getComment(
            @PathVariable Long commentId,
            @PathVariable Long userId) {
        Comment comment = commentService2.findById(commentId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        boolean hasLiked = commentLikeService.hasLiked(commentId, user);
        long likeCount = commentLikeService.countLikes(commentId);

        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(comment.getUser().getUsername())
                .hasLiked(hasLiked)
                .likeCount(likeCount)
                .createdAt(comment.getCreatedAt())
                .build();
    }


    //Comment Create
    @PostMapping
    public ResponseEntity<Void> createComment(
            @RequestBody CommentRequest2 request) {
        commentService2.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //Comment Update
    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest2 request) {
        commentService2.update(commentId, request);
        return ResponseEntity.ok().build();
    }

    //Comment Soft Delete
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId) {
        commentService2.delete(commentId);
        return ResponseEntity.noContent().build();
    }


}


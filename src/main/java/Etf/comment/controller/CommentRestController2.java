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
@RequestMapping("/api/v1/comments")
public class CommentRestController2 {

    private final CommentService2 commentService2;
    private final CommentLikeService commentLikeService;

    //사용자 id는 노출되지 않는게 좋다?

    //좋아요 토글
    @PostMapping("/{commentId}/likes/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void toggleLike(
            @PathVariable Long commentId,
            @PathVariable Long userId) {
        commentLikeService.toggleLike(commentId, userId);
    }

    //댓글 조회 (좋아요 상태/개수 포함)
    @GetMapping("/{commentId}/{userId}")
    public CommentResponse getComment(
            @PathVariable Long commentId,
            @PathVariable Long userId) {
        return commentLikeService.getComment(commentId, userId);
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


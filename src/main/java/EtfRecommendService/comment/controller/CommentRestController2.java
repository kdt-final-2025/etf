package EtfRecommendService.comment.controller;

import EtfRecommendService.comment.dto.CommentCreateRequest;
import EtfRecommendService.comment.dto.CommentRequest2;
import EtfRecommendService.comment.dto.CommentResponse2;
import EtfRecommendService.comment.dto.CommentUpdateRequest;
import EtfRecommendService.comment.serviece.CommentLikeService;
import EtfRecommendService.comment.serviece.CommentService2;
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
    public void toggleLike(
            @PathVariable Long commentId,
            @PathVariable Long userId) {
        commentLikeService.toggleLike(commentId, userId);
    }

    //댓글 조회 (좋아요 상태/개수 포함)
    @GetMapping("/{commentId}/{userId}")
    public void getComment(
            @PathVariable Long commentId,
            @PathVariable Long userId) {
        commentService2.getComment(commentId, userId);
    }

    //Comment Create
    @PostMapping
    public void createComment(
            @RequestBody CommentCreateRequest commentCreateRequest) {
        commentService2.create(commentCreateRequest);

    }

    //Comment Update
    @PutMapping("/{commentId}/{userId}")
    public void updateComment(
            @PathVariable Long commentId,
            @PathVariable Long userId,
            @RequestBody CommentUpdateRequest commentUpdateRequest) {
        commentService2.update(commentId, userId, commentUpdateRequest);

    }

    //Comment Soft Delete
    @DeleteMapping("/{commentId}/{userId}")
    public void deleteComment(
            @PathVariable Long commentId,
            @PathVariable Long userId) {
        commentService2.delete(commentId, userId);

    }


}


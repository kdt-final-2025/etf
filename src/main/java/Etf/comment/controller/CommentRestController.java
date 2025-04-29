package Etf.comment.controller;


import Etf.comment.dto.CommentCreateRequest;
import Etf.comment.dto.CommentUpdateRequest;
import Etf.comment.dto.CommentsPageList;
import Etf.comment.serviece.CommentLikeService;
import Etf.comment.serviece.CommentService;
import Etf.loginUtils.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentRestController {

    private final CommentService commentService;
    private final CommentLikeService commentLikeService;

    @GetMapping
    public ResponseEntity<CommentsPageList> readAllComment(@LoginMember String loginId, @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam(name = "etf_id") Long etfId){
        CommentsPageList commentsPageList = commentService.readAll(pageable, etfId);
        return ResponseEntity.status(HttpStatus.OK).body(commentsPageList);
    }

    //좋아요 토글
    @PostMapping("/{commentId}/likes/")
    public void toggleLike(
            @LoginMember String loginId,
            @PathVariable Long commentId) {
        commentLikeService.toggleLike(loginId, commentId);
    }

    //Comment Create
    @PostMapping
    public void createComment(
            @RequestBody CommentCreateRequest commentCreateRequest) {
        commentService.create(commentCreateRequest);

    }

    //Comment Update
    @PutMapping("/{commentId}/")
    public void updateComment(
            @LoginMember String loginId,
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequest commentUpdateRequest) {
        commentService.update(loginId, commentId, commentUpdateRequest);

    }

    //Comment Soft Delete
    @DeleteMapping("/{commentId}/{userId}")
    public void deleteComment(
            @LoginMember String loginId,
            @PathVariable Long commentId) {
        commentService.delete(loginId, commentId);

    }
}

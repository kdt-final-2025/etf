package EtfRecommendService.comment.controller;


import EtfRecommendService.comment.dto.CommentCreateRequest;
import EtfRecommendService.comment.dto.CommentUpdateRequest;
import EtfRecommendService.comment.dto.CommentsPageList;
import EtfRecommendService.comment.dto.ToggleLikeResponse;
import EtfRecommendService.comment.serviece.CommentService;
import EtfRecommendService.loginUtils.LoginMember;
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

    //댓글 생성
    @PostMapping
    public void createComment(
            @LoginMember String loginId,
            @RequestBody CommentCreateRequest commentCreateRequest) {
        commentService.create(loginId, commentCreateRequest);
    }

    //댓글 조회
    @GetMapping
    public ResponseEntity<CommentsPageList> readAllComment(
            @LoginMember String loginId,
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(name = "etf_id") Long etfId) {
        CommentsPageList commentsPageList = commentService.readAll(pageable, etfId);
        return ResponseEntity.status(HttpStatus.OK).body(commentsPageList);
    }

    //댓글 수정
    @PutMapping("/{commentId}")
    public void updateComment(
            @LoginMember String loginId,
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequest commentUpdateRequest) {
        commentService.update(loginId, commentId, commentUpdateRequest);
    }

    //Comment Soft Delete
    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @LoginMember String loginId,
            @PathVariable Long commentId) {
        commentService.delete(loginId, commentId);
    }

    //좋아요 토글
    @PostMapping("/{commentId}/likes")
    public ResponseEntity<ToggleLikeResponse> toggleLike(
            @LoginMember String loginId,
            @PathVariable Long commentId) {
        ToggleLikeResponse response = commentService.toggleLike(loginId, commentId);
        return ResponseEntity.ok(response);
    }


}
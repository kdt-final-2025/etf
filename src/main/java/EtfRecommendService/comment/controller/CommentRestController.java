package EtfRecommendService.comment.controller;


import EtfRecommendService.comment.dto.*;
import EtfRecommendService.comment.serviece.CommentService;
import EtfRecommendService.loginUtils.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentRestController {

    private final CommentService commentService;

    //댓글 생성
    @Secured("USER")
    @PostMapping
    public void createComment(
            @LoginMember String loginId,
            @RequestBody CommentCreateRequest commentCreateRequest) {
        commentService.create(loginId, commentCreateRequest);
    }

    //댓글 조회
    //관리자와 유저 접근 가능
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping
    public ResponseEntity<CommentsPageList> readAllComment(
            @LoginMember String loginId,
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(name = "etf_id") Long etfId) {
        CommentsPageList commentsPageList = commentService.readAll(pageable, etfId);
        return ResponseEntity.status(HttpStatus.OK).body(commentsPageList);
    }

    //댓글 수정
    @Secured("USER")
    @PutMapping("/{commentId}")
    public void updateComment(
            @LoginMember String loginId,
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequest commentUpdateRequest) {
        commentService.update(loginId, commentId, commentUpdateRequest);
    }

    //Comment Soft Delete
    //유저와 어드민 접근가능
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{commentId}")
    public void deleteComment(
            @LoginMember String loginId,
            @PathVariable Long commentId) {
        commentService.delete(loginId, commentId);
    }

    //좋아요 토글
    @Secured("USER")
    @PostMapping("/{commentId}/likes")
    public ResponseEntity<ToggleLikeResponse> toggleLike(
            @LoginMember String loginId,
            @PathVariable Long commentId) {
        ToggleLikeResponse response = commentService.toggleLike(loginId, commentId);
        return ResponseEntity.ok(response);
    }

    //관리자가 신고된 댓글 조회할 때 사용할 API
    //유저별 신고목록 조회와 함께 사용
    //관리자만 접근 가능
    @Secured("ADMIN")
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> readOneComment(@LoginMember String loginId, @PathVariable Long commentId){
        return ResponseEntity.ok(commentService.readOneComment(loginId, commentId));
    }
}
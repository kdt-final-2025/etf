package Etf.comment.controller;


import Etf.comment.dto.CommentResponse;
import Etf.comment.dto.CommentsPageList;
import Etf.comment.serviece.CommentService;
import Etf.loginUtils.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentRestController {

    private final CommentService commentService;

    @GetMapping("/users/comments")
    public ResponseEntity<CommentsPageList> readAllComment(@LoginMember String loginId, @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, @RequestParam(name = "etf_id") Long etfId){
        CommentsPageList commentsPageList = commentService.readAll(loginId, pageable, etfId);
        return ResponseEntity.status(HttpStatus.OK).body(commentsPageList);
    }
}

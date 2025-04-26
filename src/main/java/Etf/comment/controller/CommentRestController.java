package Etf.comment.controller;

import Etf.comment.dto.CommentRequest2;
import Etf.comment.dto.CommentResponse;
import Etf.comment.serviece.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentRestController {

    private final CommentService commentService;

    @PostMapping("/users/comments")
    public ResponseEntity<String> createComment(@RequestBody@Valid CommentRequest2 commentRequest2){
        commentService.create(commentRequest2);
        return ResponseEntity.status(HttpStatus.CREATED).body("Comment Created Successfully");
    }

    @GetMapping("/users/comments")
    public ResponseEntity<List<CommentResponse>> readAllComment(@RequestParam(required = false) int page, @RequestParam(required = false) int size, @RequestParam(required = false) String sort, @RequestParam(name = "etf_id") Long etfId){
        List<CommentResponse> commentResponses = commentService.readAll(page, size, sort, etfId);
        return ResponseEntity.status(HttpStatus.OK).body(commentResponses);
    }

    @PutMapping("/users/commets/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable Long commentId){
        commentService.update(commentId);
        return ResponseEntity.status(HttpStatus.OK).body("Comment Updated successful");
    }

    @DeleteMapping("/users/comments/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long commentId){
        commentService.delete(commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Deleted complete");
    }
}

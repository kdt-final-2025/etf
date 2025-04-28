package Etf.reply.controller;

import Etf.loginUtils.LoginMember;
import Etf.reply.dto.ReplyRequest;
import Etf.reply.service.ReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/replies")
public class ReplyController {
    private final ReplyService replyService;

    @PostMapping
    public ResponseEntity<String> createReply(@LoginMember String loginId, @RequestBody@Valid ReplyRequest rq){
        replyService.create(loginId, rq);
        return ResponseEntity.status(HttpStatus.CREATED).body("Reply Create Successfully");
    }

}

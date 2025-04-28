package Etf.comment.repository;

import Etf.comment.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findAllByEtfId(Long etfId, Pageable pageable);
}

package Etf.comment.repository;

import Etf.comment.domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

//    //좋아요 여부
//    boolean existsByLoginIdAndCommentId(String loginId, Long commentId);
//
//    //좋아요 있음 삭제
//    void deleteByLoginIdAndCommentId(String loginId, Long commentId);


  //   필요하다면 조건 순서를 바꿀 수 있습니다.
     boolean existsByUser_LoginIdAndComment_Id(String loginId, Long commentId);


     void deleteByUser_LoginIdAndComment_Id(String loginId, Long commentId);



}

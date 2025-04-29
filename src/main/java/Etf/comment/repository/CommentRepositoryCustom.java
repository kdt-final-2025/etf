package Etf.comment.repository;

import Etf.comment.domain.Comment;
import Etf.comment.domain.QComment;
import Etf.comment.domain.QCommentLike;
import Etf.comment.repository.qdto.CommentAndLikesCountQDto;
import Etf.comment.repository.qdto.SortedCommentsQDto;
import Etf.etf.QEtf;
import Etf.user.QUser;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private final QComment qComment = QComment.comment;
    private final QCommentLike qCommentLike = QCommentLike.commentLike;
    private final QUser qUser = QUser.user;
    private final QEtf qEtf = QEtf.etf;


    public SortedCommentsQDto findAllByEtfIdOrderByLikes(Pageable pageable, Long etfId) {
        long totalCount = Optional.ofNullable(queryFactory
                .select(qComment.etf.count())
                .from(qComment)
                .where(qComment.etf.id.eq(etfId))
                .fetchOne()).orElse(0L);

        List<CommentAndLikesCountQDto> commentAndLikesCountQDtoList = queryFactory
                .select(Projections.constructor(
                        CommentAndLikesCountQDto.class,
                        qComment,
                        qCommentLike.count()
                ))
                .from(qComment)
                .join(qComment.etf,qEtf).fetchJoin()
                .join(qComment.user,qUser).fetchJoin()
                .leftJoin(qComment.commentLikeList, qCommentLike)
                .where(qComment.etf.id.eq(etfId))
                .groupBy(qComment.id)
                .orderBy(qCommentLike.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();



        return SortedCommentsQDto.builder()
                .commentAndLikesCountQDtoPage(commentAndLikesCountQDtoList)
                .totalCount(totalCount)
                .build();
    }
}

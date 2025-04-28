package Etf.comment.repository;

import Etf.comment.domain.Comment;
import Etf.comment.domain.QComment;
import Etf.comment.domain.QCommentLike;
import Etf.comment.dto.CommentResponse;
import Etf.comment.dto.SortedCommentsQDto;
import Etf.etf.QEtf;
import Etf.user.QUser;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
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

        List<Tuple> tupleList = queryFactory
                .select(qComment, qCommentLike.count())
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

        List<Comment> commentList = tupleList.stream().map(
                        t-> t.get(0, Comment.class)
                )
                .toList();
        List<Long> likesCountList = tupleList.stream().map(
                t->{
                    Long likesCount = t.get(1, Long.class);
                    if(likesCount == null) likesCount = 0L;
                    return likesCount;
                }
        )
                .toList();

        return SortedCommentsQDto.builder()
                .commentList(commentList)
                .likesCountList(likesCountList)
                .totalCount(totalCount)
                .build();
    }
}

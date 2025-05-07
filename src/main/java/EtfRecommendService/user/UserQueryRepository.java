package EtfRecommendService.user;


import EtfRecommendService.comment.domain.QComment;
import EtfRecommendService.reply.domain.QReply;
import EtfRecommendService.user.dto.UserCommentResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QUser user = QUser.user;
    private final QComment comment = QComment.comment;
    private final QReply reply = QReply.reply;

    public UserQueryRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<UserCommentResponse> findUserComment(Long userId, Pageable pageable) {
        return jpaQueryFactory
                .select(Projections.constructor(UserCommentResponse.class,
//                        comment.id,
//                        comment.etf.id,
//                        comment.user.id,
//                        comment.user.nickName,
//                        comment.content,
//                        comment.user.imageUrl,
//                        comment.createdAt
                        reply.id,
                        reply.comment.etf.id,
                        reply.user.id,
                        reply.user.nickName,
                        reply.content,
                        reply.user.imageUrl,
                        reply.createdAt
                        ))
                .from(reply)
                .join(reply.user, user)
                .where(user.id.eq(userId)
                        .and(user.isLikePrivate.eq(false)))
                .orderBy(reply.createdAt.desc()) // 최신순
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

    }


    private long countUserComments(Long userId) {
        Long count = jpaQueryFactory
                .select(comment.count())
                .from(comment)
                .join(comment.user, user)
                .where(user.id.eq(userId)
                        .and(user.isLikePrivate.eq(false)) // 유저가 비공개인지
                        .and(comment.isDeleted.eq(false))) // 댓글이 삭제됐는지
                .fetchOne();
        return count != null ? count : 0L;
    }

    private long countUserReplys(Long userId) {
        Long count = jpaQueryFactory
                .select(reply.count())
                .from(reply)
                .join(reply.user, user)
                .where(user.id.eq(userId)
                        .and(user.isLikePrivate.eq(false)) // 유저가 비공개인지
                        .and(comment.isDeleted.eq(false))) // 댓글이 삭제됐는지
                .fetchOne();
        return count != null ? count : 0L;
    }

    public long totalCount(Long userId) {
        return countUserComments(userId) + countUserReplys(userId);
    }
}

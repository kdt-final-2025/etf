package EtfRecommendService.user;


import EtfRecommendService.comment.domain.QComment;
import EtfRecommendService.etf.domain.QEtf;
import EtfRecommendService.reply.domain.QReply;
import EtfRecommendService.user.dto.getUserCommentsAndReplies;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Repository
public class UserQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QUser user = QUser.user;
    private final QComment comment = QComment.comment;
    private final QReply reply = QReply.reply;
    private final QEtf etf = QEtf.etf;

    public UserQueryRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public List<getUserCommentsAndReplies> commentResponses(Long userId, Pageable pageable) {
        List<getUserCommentsAndReplies> comments = jpaQueryFactory
                .select(Projections.constructor(getUserCommentsAndReplies.class,
                        comment.id,
                        comment.etf.id,
                        comment.user.id,
                        comment.user.nickName,
                        comment.content,
                        comment.user.imageUrl,
                        comment.createdAt))
                .from(comment)
                .join(comment.user, user)
                .join(comment.etf, etf)
                .where(comment.user.id.eq(userId)
                        .and(comment.isDeleted.eq(false)))
                .fetch();

        List<getUserCommentsAndReplies> replies = jpaQueryFactory
                .select(Projections.constructor(getUserCommentsAndReplies.class,
                        reply.id,
                        reply.comment.etf.id,
                        reply.user.id,
                        reply.user.nickName,
                        reply.content,
                        reply.user.imageUrl,
                        reply.createdAt))
                .from(reply)
                .join(reply.user, user)
                .join(reply.comment, comment)
                .where(reply.user.id.eq(userId)
                        .and(reply.isDeleted.eq(false)))
                .fetch();

        List<getUserCommentsAndReplies> mergedList = Stream.concat(
                        comments.stream(),
                        replies.stream())
                .sorted(Comparator.comparing(getUserCommentsAndReplies::createdAt).reversed())
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), mergedList.size());

        if (start < end) {
            return mergedList.subList(start, end);
        } else {
            return Collections.emptyList();
        }
    }


    private long countUserComments(Long userId) {
        Long count = jpaQueryFactory
                .select(comment.count())
                .from(comment)
                .join(comment.user, user)
                .where(user.id.eq(userId)
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
                        .and(comment.isDeleted.eq(false))) // 댓글이 삭제됐는지
                .fetchOne();
        return count != null ? count : 0L;
    }

    public long totalCount(Long userId) {
        return countUserComments(userId) + countUserReplys(userId);
    }

}

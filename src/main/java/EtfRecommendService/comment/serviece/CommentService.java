package EtfRecommendService.comment.serviece;

import EtfRecommendService.comment.repository.CommentRepository;
import EtfRecommendService.etf.EtfRepository;
import EtfRecommendService.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EtfRepository etfRepository;

//    public void create(CommentRequest2 commentRequest2) {
//        User user = userRepository.findById(commentRequest2.parentId()).orElseThrow(()->new NoExistsUserIdException("User ID not found"));
//        Etf etf = etfRepository.findById(commentRequest2.etfId()).orElseThrow(()-> new NoExistsEtfIdException("Etf Id not found"));
//        commentRepository.save(
//                Comment.builder()
//                        .content(commentRequest2.content())
//                        .etf(etf)
//                        .user(user)
//                        .build()
//        );
//    }

//    public List<CommentResponse> readAll(int page, int size, String sort, Long etfId) {
//        return null;
//    }
//
//    public void update(Long commentId) {
//    }
//
//    public void delete(Long commentId) {
//    }
}

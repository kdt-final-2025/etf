package EtfRecommendService;


import EtfRecommendService.etf.Etf;
import EtfRecommendService.etf.EtfRepository;
import EtfRecommendService.user.User;
import EtfRecommendService.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DataSeeder {


    UserRepository userRepository;
    EtfRepository etfRepository;

    public DataSeeder(UserRepository userRepository, EtfRepository etfRepository) {
        this.userRepository = userRepository;
        this.etfRepository = etfRepository;
    }




    @Transactional
    public void initData() {


        User user1 = User.builder()
                .nickName("빼빼로부자")
                .loginId("pepero")
                .build();

        userRepository.save(user1);

        Etf etf1 = Etf.builder()
                .etfName("QQQ빼빼로ETF")
                .build();

        etfRepository.save(etf1);





    }
}

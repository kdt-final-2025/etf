package Etf;


import Etf.etf.Etf;
import Etf.etf.EtfRepository;
import Etf.user.User;
import Etf.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

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

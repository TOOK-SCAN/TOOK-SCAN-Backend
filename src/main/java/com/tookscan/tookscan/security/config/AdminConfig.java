package com.tookscan.tookscan.security.config;

import com.tookscan.tookscan.account.domain.Admin;
import com.tookscan.tookscan.security.domain.mysql.Account;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;
import com.tookscan.tookscan.security.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdminConfig {

    @Value("${admin.id}")
    private String superUserSerialId;

    @Value("${admin.password}")
    private String superUserPassword;

    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner createSuperUser() {
        return args -> {
            Account account = accountRepository.findBySerialIdOrProviderOrElseNull(superUserSerialId, ESecurityProvider.DEFAULT);
            if (account != null) {
                log.info("관리자가 이미 생성되어 있습니다.");
                return;
            }

            account = Admin.builder()
                    .serialId(superUserSerialId)
                    .password(passwordEncoder.encode(superUserPassword))
                    .build();

            accountRepository.save(account);
            log.info("관리자가 생성되었습니다.");
        };
    }
}


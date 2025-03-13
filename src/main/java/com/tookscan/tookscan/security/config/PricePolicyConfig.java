package com.tookscan.tookscan.security.config;

import com.tookscan.tookscan.order.domain.PricePolicy;
import com.tookscan.tookscan.order.repository.PricePolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class PricePolicyConfig {

    private final PricePolicyRepository pricePolicyRepository;

    @Bean
    public ApplicationRunner createPricePolicy() {
        return args -> {
            pricePolicyRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate.now(), LocalDate.now()).ifPresentOrElse(
                    pricePolicy -> {
                        log.info("가격정책이 이미 생성되어 있습니다.");
                    },
                    () -> {
                        PricePolicy pricePolicy = PricePolicy.builder()
                                .defaultPrice(1000)
                                .pricePerPage(10)
                                .deliveryPrice(0)
                                .startDate(LocalDate.now())
                                .build();
                        pricePolicyRepository.save(pricePolicy);
                        log.info("가격 정책이 생성되었습니다.");
                    }
            );
        };
    }
}


package com.tookscan.tookscan.security.config;

import com.tookscan.tookscan.order.domain.PricePolicy;
import com.tookscan.tookscan.order.repository.PricePolicyRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class PricePolicyConfig {

    private final PricePolicyRepository pricePolicyRepository;
    private static final int DEFAULT_PRICE = 1000;
    private static final int DEFAULT_PRICE_PER_PAGE = 10;
    private static final int ADDITIONAL_PRICE_FOR_ONE_DAY_SCAN = 5;
    private static final int ADDITIONAL_PRICE_FOR_OCR = 5;
    private static final int DELIVERY_PRICE = 3000;

    @Bean
    public ApplicationRunner createPricePolicy() {
        return args -> {
            pricePolicyRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate.now(), LocalDate.now()).ifPresentOrElse(
                    pricePolicy -> {
                        log.info("가격정책이 이미 생성되어 있습니다.");
                    },
                    () -> {
                        PricePolicy pricePolicy = PricePolicy.builder()
                                .defaultPrice(DEFAULT_PRICE)
                                .defaultPricePerPage(DEFAULT_PRICE_PER_PAGE)
                                .deliveryPrice(DELIVERY_PRICE)
                                .additionalPriceForOneDayScan(ADDITIONAL_PRICE_FOR_ONE_DAY_SCAN)
                                .additionalPriceForOcr(ADDITIONAL_PRICE_FOR_OCR)
                                .startDate(LocalDate.now())
                                .endDate(LocalDate.now().plusYears(100))
                                .build();
                        pricePolicyRepository.save(pricePolicy);
                        log.info("가격 정책이 생성되었습니다.");
                    }
            );
        };
    }
}


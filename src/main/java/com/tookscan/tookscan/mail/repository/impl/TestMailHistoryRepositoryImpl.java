package com.tookscan.tookscan.mail.repository.impl;

import com.tookscan.tookscan.mail.domain.redis.TestMailHistory;
import com.tookscan.tookscan.mail.repository.TestMailHistoryRepository;
import com.tookscan.tookscan.mail.repository.redis.TestMailHistoryRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TestMailHistoryRepositoryImpl implements TestMailHistoryRepository {

    private final TestMailHistoryRedisRepository testMailHistoryRedisRepository;

    @Override
    public TestMailHistory save(TestMailHistory testMailHistory) {
        return testMailHistoryRedisRepository.save(testMailHistory);
    }

    @Override
    public void deleteById(String value) {
        testMailHistoryRedisRepository.deleteById(value);
    }

    @Override
    public TestMailHistory findByIdOrElseNull(String value) {
        return testMailHistoryRedisRepository.findById(value).orElse(null);
    }
}

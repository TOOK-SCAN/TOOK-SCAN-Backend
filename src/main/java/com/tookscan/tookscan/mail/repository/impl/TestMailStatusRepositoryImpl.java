package com.tookscan.tookscan.mail.repository.impl;

import com.tookscan.tookscan.mail.domain.mysql.TestMailStatus;
import com.tookscan.tookscan.mail.repository.TestMailStatusRepository;
import com.tookscan.tookscan.mail.repository.mysql.TestMailStatusJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class TestMailStatusRepositoryImpl implements TestMailStatusRepository {

    private final TestMailStatusJpaRepository testMailStatusJpaRepository;

    @Override
    public TestMailStatus findByEmailOrElseNull(String email) {
        return testMailStatusJpaRepository.findByEmail(email).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        testMailStatusJpaRepository.deleteById(id);
    }

    @Override
    public TestMailStatus save(TestMailStatus testMailStatus) {
        return testMailStatusJpaRepository.save(testMailStatus);
    }
}

package com.tookscan.tookscan.mail.repository.mysql;

import com.tookscan.tookscan.mail.domain.mysql.TestMailStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TestMailStatusJpaRepository extends JpaRepository<TestMailStatus, Long> {

    Optional<TestMailStatus> findByEmail(String email);
}

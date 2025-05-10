package com.tookscan.tookscan.mail.repository;

import com.tookscan.tookscan.mail.domain.mysql.TestMailStatus;

public interface TestMailStatusRepository {

    TestMailStatus findByEmailOrElseNull(String email);

    TestMailStatus save(TestMailStatus testMailStatus);

    void deleteById(Long id);
}

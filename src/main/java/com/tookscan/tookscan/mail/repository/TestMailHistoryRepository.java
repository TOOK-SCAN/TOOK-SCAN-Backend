package com.tookscan.tookscan.mail.repository;

import com.tookscan.tookscan.mail.domain.redis.TestMailHistory;

public interface TestMailHistoryRepository {

    TestMailHistory save(TestMailHistory testMailHistory);

    void deleteById(String value);

    TestMailHistory findByIdOrElseNull(String value);
}

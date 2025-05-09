package com.tookscan.tookscan.mail.repository.redis;

import com.tookscan.tookscan.mail.domain.redis.TestMailHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestMailHistoryRedisRepository extends CrudRepository<TestMailHistory, String> {
}

package com.tookscan.tookscan.message.repository.mysql;

import com.tookscan.tookscan.message.domain.MessageGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageGroupJpaRepository extends JpaRepository<MessageGroup, Long> {
}

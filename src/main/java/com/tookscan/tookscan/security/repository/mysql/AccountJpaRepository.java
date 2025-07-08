package com.tookscan.tookscan.security.repository.mysql;

import com.tookscan.tookscan.security.domain.mysql.Account;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountJpaRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByIdAndDeletedAtIsNull(UUID accountId);

    Optional<Account> findBySerialIdAndProviderAndDeletedAtIsNull(String serialId, ESecurityProvider provider);

    Optional<Account> findBySerialIdAndDeletedAtIsNull(String serialId);

    Optional<Account> findByPhoneNumberAndDeletedAtIsNull(String phoneNumber);

    Optional<Account> findByPhoneNumberAndSerialIdAndDeletedAtIsNull(String phoneNumber, String serialId);

    Optional<Account> findByPhoneNumberAndSerialIdAndNameAndDeletedAtIsNull(String phoneNumber, String serialId, String name);

    void deleteByIdIn(List<UUID> ids);
}

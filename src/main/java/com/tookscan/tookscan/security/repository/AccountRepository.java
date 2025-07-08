package com.tookscan.tookscan.security.repository;

import com.tookscan.tookscan.security.domain.mysql.Account;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;

import java.util.List;
import java.util.UUID;

public interface AccountRepository {

    Account findByIdAndDeletedAtIsNullOrElseThrow(UUID accountId);

    Account findByIdAndDeletedAtIsNullOrElseNull(UUID accountId);

    void save(Account account);

    void deleteById(UUID accountId);

    void deleteByIdIn(List<UUID> accountIds);

    Account findBySerialIdAndProviderAndDeletedAtIsNullOrElseThrow(String serialId, ESecurityProvider provider);

    Account findBySerialIdAndProviderAndDeletedAtIsNullOrElseNull(String serialId, ESecurityProvider provider);

    Account findByPhoneNumberAndSerialIdAndDeletedAtIsNullOrElseThrow(String phoneNumber, String serialId);

    Account findByPhoneNumberAndSerialIdAndNameAndDeletedAtIsNullOrElseThrow(String phoneNumber, String serialId, String name);

    void existsBySerialIdAndProviderAndDeletedAtIsNullThenThrow(String serialId, ESecurityProvider provider);

    void existsByPhoneNumberAndDeletedAtIsNullThenThrow(String phoneNumber);

    void existsByPhoneNumberAndProvidersAndDeletedAtIsNullThenThrow(String phoneNumber, List<ESecurityProvider> provider);

    boolean existsBySerialIdAndDeletedAtIsNull(String serialId);

    boolean existsByPhoneNumberAndDeletedAtIsNull(String phoneNumber);
}

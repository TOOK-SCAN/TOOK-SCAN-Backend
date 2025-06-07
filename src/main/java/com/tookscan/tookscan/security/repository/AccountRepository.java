package com.tookscan.tookscan.security.repository;

import com.tookscan.tookscan.security.domain.mysql.Account;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;

import java.util.List;
import java.util.UUID;

public interface AccountRepository {

    Account findByIdOrElseThrow(UUID accountId);

    Account findByIdOrElseNull(UUID accountId);

    void save(Account account);

    void deleteById(UUID accountId);

    void deleteByIdIn(List<UUID> accountIds);

    Account findBySerialIdAndProviderOrElseThrow(String serialId, ESecurityProvider provider);

    Account findBySerialIdOrProviderOrElseNull(String serialId, ESecurityProvider provider);

    Account findByPhoneNumberAndSerialIdOrElseThrow(String phoneNumber, String serialId);

    Account findByPhoneNumberAndSerialIdAndNameOrElseThrow(String phoneNumber, String serialId, String name);

    void existsBySerialIdAndProviderThenThrow(String serialId, ESecurityProvider provider);

    void existsByPhoneNumberThenThrow(String phoneNumber);

    void existsByPhoneNumberAndProvidersThenThrow(String phoneNumber, List<ESecurityProvider> provider);

    boolean existsBySerialId(String serialId);

    boolean existsByPhoneNumber(String phoneNumber);
}

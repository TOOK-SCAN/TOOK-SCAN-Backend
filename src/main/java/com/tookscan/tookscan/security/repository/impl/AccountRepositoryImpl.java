package com.tookscan.tookscan.security.repository.impl;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.security.domain.mysql.Account;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;
import com.tookscan.tookscan.security.repository.AccountRepository;
import java.util.List;
import java.util.UUID;

import com.tookscan.tookscan.security.repository.mysql.AccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountJpaRepository accountJpaRepository;

    @Override
    public Account findByIdOrElseThrow(UUID id) {
        return accountJpaRepository.findById(id)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ACCOUNT));
    }

    @Override
    public Account findByIdOrElseNull(UUID id) {
        return accountJpaRepository.findById(id).orElse(null);
    }

    @Override
    public void save(Account account) {
        accountJpaRepository.save(account);
    }

    @Override
    public void deleteById(UUID id) {
        accountJpaRepository.deleteById(id);
    }

    @Override
    public void deleteByIdIn(List<UUID> ids) {
        accountJpaRepository.deleteByIdIn(ids);
    }

    @Override
    public Account findBySerialIdAndProviderOrElseThrow(String serialId, ESecurityProvider provider) {
        return accountJpaRepository.findBySerialIdAndProvider(serialId, provider)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ACCOUNT));
    }

    @Override
    public Account findBySerialIdOrProviderOrElseNull(String serialId, ESecurityProvider provider) {
        return accountJpaRepository.findBySerialIdAndProvider(serialId, provider).orElse(null);
    }

    @Override
    public void existsBySerialIdAndProviderThenThrow(String serialId, ESecurityProvider provider) {
        accountJpaRepository.findBySerialIdAndProvider(serialId, provider)
                .ifPresent(account -> {
                    throw new CommonException(ErrorCode.ALREADY_EXIST_ID);
                });
    }

    @Override
    public void existsByPhoneNumberThenThrow(String phoneNumber) {
        accountJpaRepository.findByPhoneNumber(phoneNumber)
                .ifPresent(account -> {
                    throw new CommonException(ErrorCode.ALREADY_EXIST_PHONE_NUMBER);
                });
    }

    @Override
    public boolean existsBySerialId(String serialId) {
        return accountJpaRepository.findBySerialId(serialId).isPresent();
    }

    @Override
    public Account findByPhoneNumberAndSerialIdOrElseThrow(String phoneNumber, String serialId) {
        return accountJpaRepository.findByPhoneNumberAndSerialId(phoneNumber, serialId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ACCOUNT));
    }
}

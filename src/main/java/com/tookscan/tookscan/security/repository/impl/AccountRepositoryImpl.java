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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    public Account findByPhoneNumberAndSerialIdOrElseThrow(String phoneNumber, String serialId) {
        return accountJpaRepository.findByPhoneNumberAndSerialId(phoneNumber, serialId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ACCOUNT));
    }

    @Override
    public Account findByPhoneNumberAndSerialIdAndNameOrElseThrow(String phoneNumber, String serialId, String name) {
        return accountJpaRepository.findByPhoneNumberAndSerialIdAndName(phoneNumber, serialId, name)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_ACCOUNT));
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
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Account a WHERE a.phoneNumber = :phoneNumber AND a.provider IN :provider")
    public void existsByPhoneNumberAndProvidersThenThrow(@Param("phoneNumber") String phoneNumber, @Param("provider") List<ESecurityProvider> provider) {
        accountJpaRepository.findByPhoneNumber(phoneNumber)
                .ifPresent(account -> {
                    switch (account.getProvider()) {
                        case KAKAO -> throw new CommonException(ErrorCode.KAKAO_SIGN_IN_USE);
                        case GOOGLE -> throw new CommonException(ErrorCode.GOOGLE_SIGN_IN_USE);
                        case NAVER -> throw new CommonException(ErrorCode.NAVER_SIGN_IN_USE);
                        default -> throw new CommonException(ErrorCode.INVALID_ARGUMENT);
                    }
                });
    }

    @Override
    public boolean existsBySerialId(String serialId) {
        return accountJpaRepository.findBySerialId(serialId).isPresent();
    }
}

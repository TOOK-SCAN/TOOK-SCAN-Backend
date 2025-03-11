package com.tookscan.tookscan.security.domain.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.security.domain.mysql.Account;
import com.tookscan.tookscan.security.info.CustomUserPrincipal;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    public void changePassword(Account account, String newPassword) {
        account.updatePassword(newPassword);
    }

    public void checkPassword(Account account, String password) {
        if (!account.checkPassword(password))
            throw new CommonException(ErrorCode.INVALID_ARGUMENT);
    }

    public CustomUserPrincipal createCustomUserPrincipalByAccount(Account account) {
        return CustomUserPrincipal.create(account);
    }

    public Account updatePhoneNumber(Account account, String phoneNumber) {
        account.updatePhoneNumber(phoneNumber);
        return account;
    }
}

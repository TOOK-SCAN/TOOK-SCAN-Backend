package com.tookscan.tookscan.account.domain.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.address.dto.request.AddressRequestDto;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    public User createUser(
            ESecurityProvider provider,
            String serialId,
            String password,
            String name,
            String phoneNumber,
            Boolean marketingAllowed,
            Boolean isReceiveEmail,
            Boolean isReceiveSms
    ) {
        return User.builder()
                .provider(provider)
                .serialId(serialId)
                .password(password)
                .name(name)
                .phoneNumber(phoneNumber)
                .serviceAgreed(LocalDateTime.now())
                .over14Agreed(LocalDateTime.now())
                .marketingAllowed(marketingAllowed ? LocalDateTime.now() : null)
                .isReceiveEmail(isReceiveEmail)
                .isReceiveSms(isReceiveSms)
                .build();
    }

    public boolean isPhoneNumberChanged(
            User user,
            String phoneNumber
    ) {
        return !user.getPhoneNumber().equals(phoneNumber);
    }

    public User updateSelf(
            User user,
            String email,
            String phoneNumber,
            AddressRequestDto addressDto,
            Boolean marketingAllowed,
            Boolean isReceiveEmail,
            Boolean isReceiveSms
    ) {
        boolean currentMarketingAllowed = user.getMarketingAllowed() != null;

        if (currentMarketingAllowed != marketingAllowed) {
            user.updateMarketingAllowed(marketingAllowed ? LocalDateTime.now() : null);
        }

        user.updateEmail(email);
        user.updatePhone(phoneNumber);
        user.updateAddress(addressDto.toEntity());
        user.updateIsReceiveEmail(isReceiveEmail);
        user.updateIsReceiveSms(isReceiveSms);
        return user;
    }

    public User updateByAdmin(
            User user,
            String name,
            String phoneNumber,
            String email,
            AddressRequestDto address,
            String memo
    ) {
        user.updateName(name);
        user.updatePhone(phoneNumber);
        user.updateEmail(email);
        user.updateAddress(address!=null ? address.toEntity() : null);
        user.updateMemo(memo);
        return user;
    }
}

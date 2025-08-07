package com.tookscan.tookscan.account.domain.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.address.dto.request.AddressRequestDto;
import com.tookscan.tookscan.security.domain.type.EGender;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

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
            Boolean isReceiveSms,
            EGender gender,
            LocalDate birth
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
                .gender(gender)
                .birth(birth)
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
            String deliveryRequest,
            Boolean marketingAllowed,
            Boolean isReceiveEmail,
            Boolean isReceiveSms,
            EGender gender,
            LocalDate birth
    ) {
        boolean currentMarketingAllowed = user.getMarketingAllowed() != null;

        if (currentMarketingAllowed != marketingAllowed) {
            user.updateMarketingAllowed(marketingAllowed ? LocalDateTime.now() : null);
        }

        user.updateEmail(email);
        user.updatePhone(phoneNumber);
        user.updateAddress(addressDto != null ? addressDto.toEntity() : null);
        user.updateDeliveryRequest(deliveryRequest);
        user.updateIsReceiveEmail(isReceiveEmail);
        user.updateIsReceiveSms(isReceiveSms);
        user.updateGender(gender);
        user.updateBirth(birth);
        return user;
    }

    public User updateByAdmin(
            User user,
            String name,
            String phoneNumber,
            String email,
            AddressRequestDto address,
            String deliveryRequest,
            String memo,
            EGender gender,
            LocalDate birth
    ) {
        user.updateName(name);
        user.updatePhone(phoneNumber);
        user.updateEmail(email);
        user.updateAddress(address!=null ? address.toEntity() : null);
        user.updateDeliveryRequest(deliveryRequest);
        user.updateMemo(memo);
        user.updateGender(gender);
        user.updateBirth(birth);
        return user;
    }
}

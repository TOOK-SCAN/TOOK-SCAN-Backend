package com.tookscan.tookscan.security.application.service;

import com.tookscan.tookscan.security.domain.mysql.Account;
import com.tookscan.tookscan.security.domain.type.ESecurityProvider;
import com.tookscan.tookscan.security.info.CustomTemporaryUserPrincipal;
import com.tookscan.tookscan.security.info.CustomUserPrincipal;
import com.tookscan.tookscan.security.info.factory.Oauth2UserInfo;
import com.tookscan.tookscan.security.info.factory.Oauth2UserInfoFactory;
import com.tookscan.tookscan.security.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOauth2UserDetailService extends DefaultOAuth2UserService {

    private final AccountRepository accountRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // provider 가져오기
        ESecurityProvider provider = ESecurityProvider.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        // 사용자 정보 가져오기
        Oauth2UserInfo oauth2UserInfo = Oauth2UserInfoFactory.getOauth2UserInfo(provider, super.loadUser(userRequest).getAttributes());

        // 이미 존재하는 사용자인지 확인. 존재하지 않다면 null
        Account account = accountRepository.findBySerialIdAndProviderAndDeletedAtIsNullOrElseNull(oauth2UserInfo.getId(), provider);

        // 최초 가입 유저라면 CustomTemporaryUserPrincipal 반환
        if (account == null) {
            return new CustomTemporaryUserPrincipal(oauth2UserInfo.getId(), provider);
        }

        // 임시 유저가 아니라면 CustomUserPrincipal 반환
        return CustomUserPrincipal.create(account, oauth2UserInfo.getAttributes());
    }
}

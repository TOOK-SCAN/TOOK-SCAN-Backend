package com.tookscan.tookscan.security.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import com.tookscan.tookscan.security.presentation.dto.response.ValidationResponseDto;

@UseCase
public interface ValidatePhoneNumberUseCase {

        /**
        * 전화번호 유효성 검사
        * @param phoneNumber 회원가입시 입력하는 전화번호
        * @return ValidationResponseDto
        */
        ValidationResponseDto execute(String phoneNumber);
}

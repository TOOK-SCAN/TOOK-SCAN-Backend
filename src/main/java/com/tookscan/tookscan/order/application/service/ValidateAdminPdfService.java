package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.utility.PdfWatermarkUtil;
import com.tookscan.tookscan.order.application.usecase.ValidateAdminPdfUseCase;
import com.tookscan.tookscan.order.presentation.dto.response.ValidateAdminPdfResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidateAdminPdfService implements ValidateAdminPdfUseCase {

    @Value("${aes-key}")
    private String aesKeyString;

    @Override
    public ValidateAdminPdfResponseDto execute(MultipartFile file) {
        Map<String, Object> watermarkInfo = PdfWatermarkUtil.extractOneWatermark(
                file,
                aesKeyString.getBytes()
        );

        log.info("Extracted watermark info: {}", watermarkInfo);

        if (watermarkInfo == null || watermarkInfo.isEmpty() || !watermarkInfo.containsKey("name")
                || !watermarkInfo.containsKey("phoneNumber") || !watermarkInfo.containsKey("createdAt")
                || !watermarkInfo.containsKey("orderNumber")) {
            return ValidateAdminPdfResponseDto.of(
                    null,
                    null,
                    null,
                    null
            );
        }

        return ValidateAdminPdfResponseDto.of(
                (String) watermarkInfo.get("name"),
                (String) watermarkInfo.get("phoneNumber"),
                (String) watermarkInfo.get("createdAt"),
                (String) watermarkInfo.get("orderNumber")
        );
    }
}

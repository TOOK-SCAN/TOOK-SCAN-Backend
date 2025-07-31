package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.core.annotation.BusinessLog;
import com.tookscan.tookscan.core.util.LogContext;
import com.tookscan.tookscan.core.utility.PdfWatermarkUtil;
import com.tookscan.tookscan.order.application.usecase.ValidateAdminPdfUseCase;
import com.tookscan.tookscan.order.presentation.dto.response.ValidateAdminPdfResponseDto;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ValidateAdminPdfService implements ValidateAdminPdfUseCase {

    @Value("${aes-key}")
    private String aesKeyString;

    @Override
    @BusinessLog(
        domain = "Order",
        action = "validate pdf watermark",
        userType = "Admin"
    )
    public ValidateAdminPdfResponseDto execute(MultipartFile file) {
        Map<String, Object> watermarkInfo = PdfWatermarkUtil.extractOneWatermark(
                file,
                aesKeyString.getBytes()
        );

        if (watermarkInfo == null || watermarkInfo.isEmpty() || !watermarkInfo.containsKey("name")
                || !watermarkInfo.containsKey("phoneNumber") || !watermarkInfo.containsKey("createdAt")
                || !watermarkInfo.containsKey("orderNumber")) {
            
            LogContext.put("watermark_validation_failed", true);
            
            return ValidateAdminPdfResponseDto.of(
                    null,
                    null,
                    null,
                    null
            );
        }
        
        LogContext.put("order_number", (String) watermarkInfo.get("orderNumber"));

        return ValidateAdminPdfResponseDto.of(
                (String) watermarkInfo.get("name"),
                (String) watermarkInfo.get("phoneNumber"),
                (String) watermarkInfo.get("createdAt"),
                (String) watermarkInfo.get("orderNumber")
        );
    }
}

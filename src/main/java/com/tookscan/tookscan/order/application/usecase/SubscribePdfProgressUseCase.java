package com.tookscan.tookscan.order.application.usecase;

import com.tookscan.tookscan.core.annotation.bean.UseCase;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@UseCase
public interface SubscribePdfProgressUseCase {
    SseEmitter execute(Long pdfId);
}


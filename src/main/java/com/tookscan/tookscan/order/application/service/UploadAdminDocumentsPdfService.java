package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.account.domain.User;
import com.tookscan.tookscan.account.repository.UserRepository;
import com.tookscan.tookscan.core.utility.DateTimeUtil;
import com.tookscan.tookscan.core.utility.PdfWatermarkUtil;
import com.tookscan.tookscan.core.utility.S3Util;
import com.tookscan.tookscan.order.application.usecase.UploadAdminDocumentsPdfUseCase;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Order;
import com.tookscan.tookscan.order.domain.Pdf;
import com.tookscan.tookscan.order.repository.DocumentRepository;
import com.tookscan.tookscan.order.repository.OrderRepository;
import com.tookscan.tookscan.order.repository.PdfRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UploadAdminDocumentsPdfService implements UploadAdminDocumentsPdfUseCase {

    @Value("${aes-key}")
    private String aesKeyString;

    private final DocumentRepository documentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PdfRepository pdfRepository;

    private final S3Util s3Util;

    @Override
    public void execute(Long documentId, MultipartFile file) {
        Document document = documentRepository.findByIdOrElseThrow(documentId);

        Order order = orderRepository.findByIdOrElseThrow(document.getOrder().getId());

        User user = userRepository.findByIdOrElseThrow(order.getUser().getId());

        File watermarkedPdf = PdfWatermarkUtil.embedWatermark(
                file,
                user.getName(),
                user.getPhoneNumber(),
                order.getOrderNumber(),
                DateTimeUtil.convertLocalDateTimeToDartString(order.getCreatedAt()),
                aesKeyString.getBytes()
        );

        String pdfUrl = s3Util.uploadPdf(document, watermarkedPdf);

        Pdf pdf = Pdf.builder()
                .pdfUrl(pdfUrl)
                .pdfCreatedAt(LocalDateTime.now())
                .document(document)
                .build();

        pdfRepository.save(pdf);
    }
}

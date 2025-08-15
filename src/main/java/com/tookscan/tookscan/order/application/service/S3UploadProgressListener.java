package com.tookscan.tookscan.order.application.service;

import com.tookscan.tookscan.order.domain.event.PdfProgressEvent;
import com.tookscan.tookscan.order.domain.type.EPdfUploadStatus;
import java.util.OptionalLong;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import software.amazon.awssdk.transfer.s3.progress.TransferListener;

@Slf4j
@RequiredArgsConstructor
public class S3UploadProgressListener implements TransferListener {

    private final ApplicationEventPublisher eventPublisher;
    private final Long pdfId;

    @Override
    public void bytesTransferred(Context.BytesTransferred context) {
        var snapshot = context.progressSnapshot();
        long transferred = snapshot.transferredBytes();
        OptionalLong totalOpt = snapshot.totalBytes();
        long total = totalOpt.isPresent() ? totalOpt.getAsLong() : -1L;
        double percent = total > 0 ? (transferred * 100.0 / total) : -1.0;
        PdfProgressEvent event = PdfProgressEvent.createCustomEvent(pdfId, "pdf_upload_progress",
                total > 0 ? String.format("%.2f", percent) : String.valueOf(transferred));
        eventPublisher.publishEvent(event);
    }

    @Override
    public void transferComplete(Context.TransferComplete context) {
        PdfProgressEvent event = PdfProgressEvent.createCustomEvent(pdfId, "pdf_upload_status", EPdfUploadStatus.COMPLETED);
        eventPublisher.publishEvent(event);
    }

    @Override
    public void transferFailed(Context.TransferFailed context) {
        PdfProgressEvent event = PdfProgressEvent.createCustomEvent(pdfId, "pdf_upload_status", EPdfUploadStatus.FAILED);
        eventPublisher.publishEvent(event);
    }
}


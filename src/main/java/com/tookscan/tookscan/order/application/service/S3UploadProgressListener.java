package com.tookscan.tookscan.order.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.transfer.s3.progress.TransferListener;
import java.util.OptionalLong;

@Slf4j
@RequiredArgsConstructor
public class S3UploadProgressListener implements TransferListener {

    private final SubscribePdfProgressService progressService;
    private final String storedFileName;
    private final Long pdfId;

    @Override
    public void transferInitiated(Context.TransferInitiated context) {
        progressService.sendEvent(pdfId, "S3_UPLOAD_STARTED", storedFileName);
    }

    @Override
    public void bytesTransferred(Context.BytesTransferred context) {
        var snapshot = context.progressSnapshot();
        long transferred = snapshot.transferredBytes();
        OptionalLong totalOpt = snapshot.totalBytes();
        long total = totalOpt.isPresent() ? totalOpt.getAsLong() : -1L;
        double percent = total > 0 ? (transferred * 100.0 / total) : -1.0;
        progressService.sendEvent(pdfId, "pdf upload progress",
                total > 0 ? String.format("%.2f", percent) : String.valueOf(transferred));
    }

    @Override
    public void transferComplete(Context.TransferComplete context) {
        progressService.sendEvent(pdfId, "pdfUploadStatus", "COMPLETED");
    }

    @Override
    public void transferFailed(Context.TransferFailed context) {
        progressService.sendEvent(pdfId, "pdfUploadStatus", "FAILED");
    }
}


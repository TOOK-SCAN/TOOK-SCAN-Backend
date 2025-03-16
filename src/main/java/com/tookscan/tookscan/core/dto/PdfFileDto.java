package com.tookscan.tookscan.core.dto;

public record PdfFileDto(
        String fileName,
        byte[] content,
        String contentType
) {}

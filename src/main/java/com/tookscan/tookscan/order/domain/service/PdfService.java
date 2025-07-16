package com.tookscan.tookscan.order.domain.service;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.Document;
import com.tookscan.tookscan.order.domain.Pdf;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PdfService {

    public String getPdfUrls(List<Document> documents) {
        if (documents.isEmpty()) {
            throw new CommonException(ErrorCode.NOT_FOUND_DOCUMENT);
        }

        return documents.stream()
                .map(doc -> {
                    List<Pdf> pdfs = doc.getPdfs();
                    String content = doc.getName() + " :<br />";
                    for (Pdf pdf : pdfs) {
                        if (pdf.getPdfUrl() != null) {
                            content += "<a href=\"" + pdf.getPdfUrl() + "\" target=\"_blank\">" +
                                    pdf.getPdfUrl() + "</a> <br />";
                        } else {
                            content += "PDF URL이 없습니다. <br />";
                        }
                    }
                    return content;
                })
                .reduce((doc1, doc2) -> doc1 + "<br /> <br />" + doc2)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_DOCUMENT));
    }
}

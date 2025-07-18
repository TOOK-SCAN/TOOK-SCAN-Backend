package com.tookscan.tookscan.core.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationText;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

@Slf4j
public class PdfWatermarkUtil {
    /**
     * 예: JSON 형태로 유저 정보를 만들고, AES 암호화 + Base64
     */
    public static String generateEncryptedWatermark(String userName, String phone, String orderNumber, String date, byte[] aesKey) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("name", userName);
            obj.put("phoneNumber", phone);
            obj.put("createdAt", date);
            obj.put("orderNumber", orderNumber);

            String raw = obj.toString();

            String enc = AesUtil.encryptAES(raw, aesKey);
            return enc;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * AES + Base64 로 암호화된 워터마크 복호화
     */
    public static String decryptWatermark(String cipherText, byte[] aesKey) {
        try {
            return AesUtil.decryptAES(cipherText, aesKey);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static File embedWatermark(MultipartFile multipartFile, String userName, String phone, String orderNumber, String date, byte[] aesKey) {
        PDDocument doc = null;
        File tempPdfFile = null;

        try {
            // 1. MultipartFile → 임시 파일로 저장
            tempPdfFile = File.createTempFile("origin-", ".pdf");
            multipartFile.transferTo(tempPdfFile);

            // 2. PDFParser로 파싱
            RandomAccessRead rar = new RandomAccessReadBufferedFile(tempPdfFile);
            PDFParser parser = new PDFParser(rar);
            COSDocument cosDoc = parser.parse().getDocument();

            // 3. COSDocument → PDDocument
            doc = new PDDocument(cosDoc, rar);

            // 4. 워터마크 생성 및 삽입
            String encryptedWatermark = generateEncryptedWatermark(userName, phone, orderNumber, date, aesKey);
            insertAnnotations(doc, encryptedWatermark);
            insertMetadataMultiple(doc, encryptedWatermark);

            // 5. 워터마크된 PDF를 새 임시 파일로 저장
            File watermarkedFile = File.createTempFile("watermarked-", ".pdf");
            doc.save(watermarkedFile);
            return watermarkedFile;

        } catch (Exception e) {
            throw new RuntimeException("PDF 워터마크 삽입 실패", e);
        } finally {
            if (doc != null) {
                try {
                    doc.close();
                } catch (IOException ignored) {}
            }

            if (tempPdfFile != null) {
                tempPdfFile.delete(); // 원본 임시파일 정리
            }
        }
    }

    /**
     * 여러 페이지, 여러개의 Annotation 삽입.
     * "Ctrl+F"로 쉽게 찾을 수 있는 접두어 없이,
     * AES+Base64 문자열만 넣음.
     */
    private static void insertAnnotations(PDDocument doc, String encWM) throws IOException {
        int pageCount = doc.getNumberOfPages();
        Random rand = new Random();
        for (int i = 0; i < pageCount; i++) {
            PDPage page = doc.getPage(i);

            // 페이지마다 2~4개의 annotation
            int howMany = 2 + rand.nextInt(3);
            for(int j=0; j<howMany; j++){
                PDAnnotationText ann = new PDAnnotationText();
                ann.setContents(encWM);
                ann.setHidden(true);
                ann.setInvisible(true);

                // 화면 바깥
                float offX = -10000 - j*100;
                float offY = -10000 - (i*100) - (j*50);
                ann.setRectangle(new PDRectangle(offX, offY, 0, 0));

                page.getAnnotations().add(ann);
            }
        }
    }

    /**
     * 메타데이터에 5개 key를 "난수 Key"로 중복 삽입.
     * 예: A1680123456_Ab, B1680123456_Zb, ...
     */
    private static void insertMetadataMultiple(PDDocument doc, String encWM) throws IOException {
        PDMetadata metadata = new PDMetadata(doc);
        COSDictionary cos = metadata.getCOSObject();

        Random rand = new Random();

        for(int i=0; i<5; i++){
            long stamp = System.nanoTime();
            String randomKey = generateRandomKey(stamp, rand);
            cos.setString(COSName.getPDFName(randomKey), encWM);
        }

        doc.getDocumentCatalog().setMetadata(metadata);
    }

    // 난수 key 생성
    private static String generateRandomKey(long stamp, Random rand) {
        StringBuilder sb = new StringBuilder();

        sb.append((char)('A' + rand.nextInt(26)));
        sb.append(stamp);
        sb.append("_");

        sb.append((char)('A'+rand.nextInt(26)));
        sb.append((char)('a'+rand.nextInt(26)));
        return sb.toString();
    }

    public static Map<String, Object> extractOneWatermark(MultipartFile multipartFile, byte[] aesKey) {
        PDDocument doc = null;
        File tempFile = null;

        try {
            // 1. MultipartFile → 임시 파일
            tempFile = File.createTempFile("extract-", ".pdf");
            multipartFile.transferTo(tempFile);

            // 2. COSDocument → PDDocument
            RandomAccessRead rar = new RandomAccessReadBufferedFile(tempFile);
            PDFParser parser = new PDFParser(rar);
            COSDocument cosDoc = parser.parse().getDocument();
            doc = new PDDocument(cosDoc);

            // 3. Annotation 영역 탐색
            for (PDPage page : doc.getPages()) {
                for (PDAnnotation ann : page.getAnnotations()) {
                    if (ann instanceof PDAnnotationText) {
                        String c = ann.getContents();
                        if (c != null && !c.isBlank()) {
                            try {
                                String dec = decryptWatermark(c, aesKey);
                                log.info("워터마크 내용: {}", dec);
                                parseWatermarkJson(dec);
                            } catch (Exception ignored) {}
                        }
                    }
                }
            }

            // 4. Metadata 영역 탐색
            PDMetadata metadata = doc.getDocumentCatalog().getMetadata();
            if (metadata != null) {
                for (COSName name : metadata.getCOSObject().keySet()) {
                    String value = metadata.getCOSObject().getString(name);
                    if (value != null && !value.isBlank()) {
                        try {
                            String dec = decryptWatermark(value, aesKey);
                            log.info("워터마크 내용: {}", dec);
                            return parseWatermarkJson(dec);
                        } catch (Exception ignored) {}
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (doc != null) {
                try {
                    doc.close();
                } catch (IOException ignored) {}
            }

            if (tempFile != null) {
                tempFile.delete(); // 임시 파일 정리
            }
        }

        return null;
    }

    public static Map<String, Object> parseWatermarkJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("워터마크 JSON 파싱 실패", e);
        }
    }
}

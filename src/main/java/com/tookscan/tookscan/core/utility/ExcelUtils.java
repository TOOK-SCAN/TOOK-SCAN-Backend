package com.tookscan.tookscan.core.utility;

import com.tookscan.tookscan.core.exception.error.ErrorCode;
import com.tookscan.tookscan.core.exception.type.CommonException;
import com.tookscan.tookscan.order.domain.Order;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ExcelUtils {

    @Value("${tookscan.delivery.picking-location}")
    private String pickingLocation;

    @Value("${tookscan.delivery.sender-name}")
    private String senderName;

    @Value("${tookscan.delivery.sender-phone-number}")
    private String senderPhoneNumber;

    @Value("${tookscan.delivery.sender-address}")
    private String senderAddress;

    @Value("${tookscan.delivery.pickup-building-entry}")
    private String pickupBuildingEntry;

    @FunctionalInterface
    public interface ExcelRowMapper<T> {
        T mapRow(Row row, int rowNum);
    }

    public <T> List<T> parseExcel(MultipartFile file,
                                         int startRowIndex,
                                         ExcelRowMapper<T> rowMapper) {
        try {
            List<T> result = new ArrayList<>();
            try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);
                int rowCount = sheet.getPhysicalNumberOfRows();

                for (int i = startRowIndex; i < rowCount; i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) {
                        continue;
                    }
                    T obj = rowMapper.mapRow(row, i);
                    result.add(obj);
                }
            }
            return result;
        } catch (IOException e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR, "파일을 읽는 중 오류가 발생했습니다.");
        }

    }

    public String getStringCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        cell.setCellType(org.apache.poi.ss.usermodel.CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    public byte[] writeDeliveries(List<Order> orders) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Deliveries");

            // 헤더 작성
            Row headerRow = sheet.createRow(0);
            String[] headers = {"고객 주문번호", "고객 주문일시", "피킹 위치", "보낸이 이름", "보낸이 전화번호", "보낸이 주소",
                    "픽업지 건물 출입방법", "픽업지 출입 요청사항", "받는이 이름", "받는이 전화번호", "받는이 주소",
                    "받는이 건물 출입방법", "받는이 요청사항", "상품명", "상품 수량", "크기타입", "아이스박스",
                    "셀러명", "판매채널", "상품 카테고리", "상품 깨짐주의", "상품 가액", "상품 코드",
                    "반품 원송장번호", "보낸이 우편번호", "받는이 우편번호"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 데이터 작성
            int rowNum = 1;
            for (Order order : orders) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(order.getOrderNumber());
                row.createCell(1).setCellValue(DateTimeUtil.convertLocalDateTimeToString(order.getCreatedAt()));
                row.createCell(2).setCellValue(pickingLocation);
                row.createCell(3).setCellValue(senderName);
                row.createCell(4).setCellValue(senderPhoneNumber);
                row.createCell(5).setCellValue(senderAddress);
                row.createCell(6).setCellValue(pickupBuildingEntry);
//                row.createCell(7).setCellValue(pickupRequest);
                row.createCell(8).setCellValue(order.getDelivery().getReceiverName());
                row.createCell(9).setCellValue(order.getDelivery().getPhoneNumber());
                row.createCell(10).setCellValue(order.getDelivery().getAddress().getFullAddress());
//                row.createCell(11).setCellValue(order.getReceiverBuildingEntry());
                row.createCell(12).setCellValue(order.getDelivery().getRequest());
                row.createCell(13).setCellValue(order.getDocumentsDescription());
                row.createCell(14).setCellValue(order.getDocuments().size());
//                row.createCell(15).setCellValue(order.getSizeType());
                row.createCell(16).setCellValue("X");
//                row.createCell(17).setCellValue(order.getSellerName());
//                row.createCell(18).setCellValue(order.getSalesChannel());
//                row.createCell(19).setCellValue(order.getProductCategory());
//                row.createCell(20).setCellValue(order.isFragile() ? "Yes" : "No");
//                row.createCell(21).setCellValue(order.getProductValue());
//                row.createCell(22).setCellValue(order.getProductCode());
//                row.createCell(23).setCellValue(order.getReturnTrackingNumber());
//                row.createCell(24).setCellValue(order.getSenderPostalCode());
//                row.createCell(25).setCellValue(order.getReceiverPostalCode());
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new CommonException(ErrorCode.INTERNAL_SERVER_ERROR, "엑셀 파일 생성 중 오류가 발생했습니다." + e);
        }
    }
}
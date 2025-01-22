package com.tookscan.tookscan.core.infrastructure;

import io.hypersistence.tsid.TSID;
import java.net.NetworkInterface;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Enumeration;

public class TsidFactory {

    public TsidFactory() {
    }

    private static TSID.Factory createTSIDFactory() {
        int nodeId = generateNodeId();
        return TSID.Factory.builder()
                .withNodeBits(10)  // NodeBits = 10 -> 2^10 = 1024 노드 지원
                .withNode(nodeId)  // 이 서버(노드)의 고유 ID
                .withClock(Clock.system(ZoneId.of("Asia/Seoul"))) // 나라에 맞는 시스템 시간 使用(사용)
                .build();
    }

    public static TSID.Factory getFactory() {
        return LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final TSID.Factory INSTANCE = createTSIDFactory();
    }

    /**
     * MAC 주소 해시 -> Node ID (0~1023 범위)
     * 예외 발생 시 랜덤 값을 사용.
     */
    private static int generateNodeId() {
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    for (byte macPart : mac) {
                        sb.append(String.format("%02X", macPart));
                    }
                }
            }
            // 10비트 범위 (0 ~ 1023)
            return sb.toString().hashCode() & 0x3FF;
        } catch (Exception e) {
            // 예외 시, 임의의 Node ID
            return (int) (Math.random() * 1024);
        }
    }
}

package org.openpaas.ieda.common.service;

import java.text.DecimalFormat;
import java.text.NumberFormat;

final public class CommonUtils {
    
    public static final double SPACE_KB = 1024;
    public static final double SPACE_MB = 1024 * SPACE_KB;
    public static final double SPACE_GB = 1024 * SPACE_MB;
    public static final double SPACE_TB = 1024 * SPACE_GB;
    
    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : 파일 사이즈 반올림하여 MegaByte로 형식화함
     * @title : formatSizeUnit
     * @return : String
    ***************************************************/
    public static String formatSizeUnit(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        // 밑을 10으로 사용하여 지정된 숫자의 로그를 반환
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        // format 메소드를 사용하여 특정 패턴으로 값을 포맷할 수 있다. (반환 값 String)
        // 소수점 원하는 자릿수까지만 출력,단위 반환
        return new DecimalFormat("#,##0.0").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /***************************************************
     * @project : Paas 플랫폼 설치 자동화
     * @description : File Size 변경
     * @title : bytes2String
     * @return : String
    ***************************************************/
    public static String bytes2String(long sizeInBytes) {
        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(1);

        try {
            if (sizeInBytes < SPACE_KB) {
                return nf.format(sizeInBytes) + " Byte(s)";
            } else if (sizeInBytes < SPACE_MB) {
                return nf.format(sizeInBytes / SPACE_KB) + " KB";
            } else if (sizeInBytes < SPACE_GB) {
                return nf.format(sizeInBytes / SPACE_MB) + " MB";
            } else if (sizeInBytes < SPACE_TB) {
                return nf.format(sizeInBytes / SPACE_GB) + " GB";
            } else {
                return nf.format(sizeInBytes / SPACE_TB) + " TB";
            }
        } catch (NumberFormatException e) {
            return sizeInBytes + " Byte(s)";
        }
    }
}

package com.hanwha.tax.batch;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


@Slf4j
public class Utils {


    /**
     * pattern 형식에 맞게 오늘 일/시 리턴한다.
     * @param pattern
     * @return
     */
    public static String getCurrentDate(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * yyyyMMdd 형식으로 오늘 일자 리턴한다.
     * @return
     */
    public static String getCurrentDate() {
        return getCurrentDate("yyyyMMdd");
    }

    /**
     * yyyy-MM-dd HH:mm:ss 형식으로 오늘 일자 리턴한다.
     * @return
     */
    public static String getCurrentDateTime() {
        return getCurrentDate("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * pattern 형식에 맞게 어제 일/시 리턴한다.
     * @param pattern
     * @return
     */
    public static String getYesterday(String pattern) {
        return LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * yyyyMMdd 형식으로 어제 일자 리턴한다.
     * @return
     */
    public static String getYesterday() {
        return getYesterday("yyyyMMdd");
    }

    public static String deleteAny(String str, String charsToDelete) {
        return org.springframework.util.StringUtils.deleteAny(str, charsToDelete);
    }
    private static Date chkDate(String inputDate, String format) {
        String _date = null;
        _date = inputDate;

        if (_date == null) return null;
        else {
            _date = deleteAny(inputDate, "-");
            _date = deleteAny(_date, "/");
            _date = deleteAny(_date, ".");
            _date = deleteAny(_date, ":");
        }

        SimpleDateFormat formatter = new SimpleDateFormat(format, java.util.Locale.KOREA);
        Date date = null;

        try {
            date = formatter.parse(_date);
        }
        catch ( ParseException e ) {
            return null;
        }

        if ( !formatter.format(date).equals(_date) ) return null;

        return date;
    }

    public static String formatDate(String date, String format) {
        if ( isEmpty(format) ) {
            format = "yyyy-MM-dd";
        }

        if ( isEmpty(date) ) return null;
        if ( "00000000".equals(date) ) return null;
        if ( date.length() < 8 ) return date;

        SimpleDateFormat formatter = new SimpleDateFormat(format);

        String formatString = date;
        try {
            formatString = formatter.format(chkDate(date, "yyyyMMdd"));
        } catch (Exception e) {
            formatString = date;
        }

        return formatString;
    }

    /**
     * 문자열 날짜에서 N일 후 날짜를 리턴한다.
     * @param dateStr
     * @param days
     * @return
     */
    public static String addDays(String dateStr, int days) {
        if (days == 0) {
            return dateStr;
        }

        return LocalDate.parse(dateStr).plusDays(days).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 문자열 날짜에서 N년 후 날짜를 리턴한다.
     * @param dateStr
     * @param years
     * @return
     */
    public static String addYears(String dateStr, int years) {
        if (years == 0) {
            return dateStr;
        }

        return LocalDate.parse(dateStr).plusYears(years).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * 문자열이 null 이거나 빈값 여부를 리턴한다.
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        if (str != null) {
            int len = str.length();
            for (int i = 0; i < len; ++i) {
                if (str.charAt(i) > ' ') {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 문자열을 Camel 표기 형식으로 변환한다.
     * @param str
     * @return
     */
    public static String convertToCamelCase(String str) {
        StringBuilder result = new StringBuilder();
        boolean nextUpper = false;
        String allLower = str.toLowerCase();

        for (int i = 0; i < allLower.length(); i++) {
            char currentChar = allLower.charAt(i);
            if (currentChar == '_') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    currentChar = Character.toUpperCase(currentChar);
                    nextUpper = false;
                }
                result.append(currentChar);
            }
        }
        return result.toString();
    }

    /**
     * 생년월일 검증
     * @param birth
     * @return
     */
    private static boolean validateBirth(String birth) {
        if (isEmpty(birth) || birth.length() != 8) {
            return false;
        }

        return true;
    }

    /**
     * 생년월일로 만나이 계산
     * @param birth
     * @return
     */
    public static int realAge(String birth) {
        if (!validateBirth(birth)) {
            return -1;
        }

        LocalDate now = LocalDate.now();
        LocalDate parsedBirthDate = LocalDate.parse(birth, DateTimeFormatter.ofPattern("yyyyMMdd"));

        int realAge = now.minusYears(parsedBirthDate.getYear()).getYear();
        if (parsedBirthDate.plusYears(realAge).isAfter(now)) {
            realAge = realAge -1;
        }

        return realAge;
    }

    /**
     * 생년월일로 한국 나이 계산
     * @param birth
     * @return
     */
    public static int koreaAge(String birth) {
        if (!validateBirth(birth)) {
            return -1;
        }

        LocalDate now = LocalDate.now();
        LocalDate parsedBirthDate = LocalDate.parse(birth, DateTimeFormatter.ofPattern("yyyyMMdd"));

        return now.minusYears(parsedBirthDate.getYear()).getYear()+1;
    }

    /**
     * 소득세 기준 나이 계산
     * @param birth
     * @return
     */
    public static int taxAge(String birth) {
        if (!validateBirth(birth)) {
            return -1;
        }

        LocalDate now = LocalDate.now();
        LocalDate parsedBirthDate = LocalDate.parse(birth, DateTimeFormatter.ofPattern("yyyyMMdd"));
        int taxAge = now.minusYears(parsedBirthDate.getYear()).getYear();
        if (now.getMonthValue() < 6) taxAge -= taxAge;

        return taxAge;
    }

    /**
     * lpad 함수
     * @param str       원본문자열
     * @param sizeByte  문자열길이
     * @param charPad   padding할 문자
     * @return
     */
    public static String lpadByte(String str, int sizeByte, String charPad) {
        if (isEmpty(charPad)) {
            charPad = " ";
        }

        try {
            int strLen = str.getBytes().length;

            if (strLen < sizeByte) {
                String sResult = "";
                for (int icnt = 0; icnt < (sizeByte - strLen); icnt++) {
                    sResult += charPad;
                }
                return sResult + str;
            } else if (strLen == sizeByte) {
                return str;
            } else {
                byte[] ret = new byte[sizeByte];
                byte[] byteStr = str.getBytes();

                for (int i = 0; i < sizeByte; i++) {
                    ret[i] = byteStr[i];
                }

                return new String(ret);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

//    public static void main(String[] args) {
//        log.info("{}", "CI|TIvDch4batLopWj6GHwACPn87MNemqRB|Y|20220407172217|1|\n".split("\\|").length);
//    }

}

package com.hanwha.tax.batch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


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

    public static void logCallReturned(String op, Object object) {
        if (object == null) log.info("## [RETURN] {}() : null", op);
        else {
            try {
                ObjectMapper mapper = new ObjectMapper();
                log.info("## [RETURN] {}() : {}", op, mapper.writeValueAsString(object));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                log.info("## [RETURN] {}() : {}", op, object.toString());
            }
        }
    }

    public static void logExtCall(String op, Object object) {
        if (object == null) log.info("## [EXT_CALL] {}() : null", op);
        else {
            try {
                ObjectMapper mapper = new ObjectMapper();
                log.info("## [EXT_CALL] {}() : {}", op, mapper.writeValueAsString(object));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                log.info("## [EXT_CALL] {}() : {}", op, object.toString());
            }
        }
    }

    public static void logExtCallReturned(String op, Object object) {
        if (object == null) log.info("## [EXT_RETURN] {}() : null", op);
        else {
            try {
                ObjectMapper mapper = new ObjectMapper();
                log.info("## [EXT_RETURN] {}() : {}", op, mapper.writeValueAsString(object));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                log.info("## [EXT_RETURN] {}() : {}", op, object.toString());
            }
        }
    }

}

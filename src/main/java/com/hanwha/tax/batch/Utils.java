package com.hanwha.tax.batch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Slf4j
public class Utils {

    public static void logCalled(String op, Object object) {
        if (object == null) log.info("## [CALLED] {}() : null", op);
        else {
            try {
                ObjectMapper mapper = new ObjectMapper();
                log.info("## [CALLED] {}() : {}", op, mapper.writeValueAsString(object));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                log.info("## [CALLED] {}() : {}", op, object.toString());
            }
        }
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


    public static String yyyymmddYester() {
        return LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static String yyyymmdd() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

}

package com.hanwha.tax.batch;

import org.springframework.beans.factory.annotation.Value;

public class Constants {
    private String APP_NAME = "TAX";

    @Value("${spring.application.name}")
    public void setAppName(String value) {
        APP_NAME = value;
    }

    public final static String API = "/api";
    public final static String VERSION = "v1";

    public final static String CODE_RET_OK = "OK";
    public final static String CODE_RET_NOK = "NOK";

    // 고객 상태코드 : 정상(00), 탈퇴(02), 휴면(03)
    public final static String CUST_ST_NORMAL = "00";
    public final static String CUST_ST_SIGNOUT = "02";
    public final static String CUST_ST_REST = "03";

    // 고객 등급코드
    public final static String CUST_GR_NOMEM = "00";
    public final static String CUST_GR_ASOC = "01";
    public final static String CUST_GR_REGL = "02";

    // KCB 본인인증 응답코드
    public final static String KCB_RES_B000 = "B000";   // 정상 처리

    // 소득세 flag
    public final static String TAX_FLAG_NONE = "00";
    public final static String TAX_FLAG_SIMBOOK = "10";   // 간편장부
    public final static String TAX_FLAG_COMBOOK = "20";   // 복식부기
    public final static String TAX_FLAG_SIMRATE = "01";   // 단순경비
    public final static String TAX_FLAG_STARATE = "02";   // 기준경비
    public final static String TAX_FLAG_SBSIR = "11";   // 간편장부, 단순경비
    public final static String TAX_FLAG_SBSTR = "12";   // 간편장부, 기준경비
    public final static String TAX_FLAG_CBSIR = "21";   // 복식부기, 단순경비
    public final static String TAX_FLAG_CBSTR = "22";   // 복식부기, 기준경비

    // 쿠콘 마이데이터 전문 파일
    public final static String BANK_FILE = "BANK";              // 은행(원본)
    public final static String CARD_FILE = "CARD";              // 카드(원본)
    public final static String BANK_TRANS_FILE = "BANK_TRANS";  // 은행(수입)
    public final static String CARD_APPR_FILE = "CARD_APPR";    // 카드(경비)
    public final static String REVOKE_FILE = "REVOKE";          // 쿠콘 탈회
    public final static String THIRDPARTY_FILE = "THIRDPARTY";  // 제3자 제공동의

    // 쿠콘 SFTP 파일 경로
    public final static String COOCON_FILE_SFTP_PATH = "/data/";                // sftp 경로
//    public final static String COOCON_FILE_DOWNLOAD_PATH = "D:/dev/hanwha/nas/tax/down/";   // sftp 다운로드 경로
//    public final static String COOCON_FILE_MYDATA_PATH = "D:/dev/hanwha/nas/tax/mydata/";   // 마이데이터 파일 경로
}

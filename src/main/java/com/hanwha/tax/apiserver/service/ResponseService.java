package com.hanwha.tax.apiserver.service;

import com.hanwha.tax.apiserver.Utils;
import com.hanwha.tax.apiserver.model.response.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

@Service
public class ResponseService {

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    // enum으로 API 요청 결과에 대한 code, message를 정의한다.
    public enum Response {
        SUCCESS(0, "성공하였습니다."),
        FAIL(-1, "실패하였습니다.");

        private int code;
        private String msg;
    }

    // 결과를 처리하는 메소드
    public ApiDataResult result(Object data) {
        ApiDataResult result = new ApiDataResult();
        result.setData(data);
        setSuccessResult(result);

        Utils.logCallReturned("success", data);
        return result;
    }
//    // 결과를 처리하는 메소드
//    public <T> ApiDataResult<T> result(T data) {
//        ApiDataResult<T> result = new ApiDataResult<>();
//        result.setData(data);
//        setSuccessResult(result);
//        return result;
//    }

//    // 단일건 결과를 처리하는 메소드
//    public <T> SingleResult<T> getSingleResult(T data) {
//        SingleResult<T> result = new SingleResult<>();
//        result.setData(data);
//        setSuccessResult(result);
//        return result;
//    }
//
//    // 다중건 결과를 처리하는 메소드
//    public <T> ListResult<T> getListResult(List<T> list) {
//        ListResult<T> result = new ListResult<>();
//        result.setList(list);
//        setSuccessResult(result);
//        return result;
//    }

    public ApiDataResult successResult() {
        final ApiDataResult result = new ApiDataResult();
        setSuccessResult(result);
        return result;
    }

    // 실패 결과만 처리하는 메소드
    public CommonResult getFailResult(int code, String msg) {
        final CommonResult result = new CommonResult();
        result.setSuccess(false); // setSuccess : 응답 성공 여부 (true/false)
        result.setCode(code); // setCode : 응답 코드 번호 >= 0 정상, < 0 비정상
        result.setMsg(msg); // setMsg 응답 메시지
        return result;
    }

    public ApiDataResult successResult(String msg) {
        final ApiDataResult result = new ApiDataResult();
        setSuccessResult(result);
        if (Strings.isNotEmpty(msg))
        result.setMessage(msg);
        return result;
    }

    public ApiDataResult failResult() {

        return failResult(Response.FAIL.getCode(), Response.FAIL.getMsg(), null);
    }

    public ApiDataResult failResult(int code, String msg) {
        return failResult(code, msg, null);
    }

    public ApiDataResult failResult(int code, String msg, Exception e) {
        final ApiDataResult result = new ApiDataResult();
        result.setSuccess(false); // setSuccess : 응답 성공 여부 (true/false)
        result.setCode(code); // setCode : 응답 코드 번호 >= 0 정상, < 0 비정상
        result.setMessage(msg); // setMessage 응답 메시지
        if (e != null) {
            result.setDevMsg(e.toString());
        }

        Utils.logCallReturned("fail", msg);
        return result;
    }

    private void setSuccessResult(ApiDataResult result) {
        result.setSuccess(true);
        result.setCode(Response.SUCCESS.getCode());
        result.setMessage(Response.SUCCESS.getMsg());
    }

    private void setFailResult(ApiDataResult result) {
        result.setSuccess(false);
        result.setCode(Response.FAIL.getCode());
        result.setMessage(Response.FAIL.getMsg());
    }
}
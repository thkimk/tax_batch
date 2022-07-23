package com.hanwha.tax.apiserver.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ApiDataResult {

    @ApiModelProperty(value = "응답 성공여부 : true/false", position = 1)
    private boolean success;

    @ApiModelProperty(value = "응답 코드 번호 : >= 0 정상, < 0 비정상", position = 2)
    private int code;

    @ApiModelProperty(value = "응답 메시지", position = 3)
    private String message;

    //TODO 로그 확인용 ID. 추가해야함.
    @ApiModelProperty(value = "Log Id", hidden = true)
    private String trId;

    @ApiModelProperty(value = "응답 시간", position = 4)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    private Date timestamp = new Date();

    @ApiModelProperty(value = "Error 메시지", hidden = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String devMsg;

    @ApiModelProperty(value = "응답 DATA", position = 5)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;
}
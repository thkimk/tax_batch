package com.hanwha.tax.apiserver.advice.exception;

public class InvalidInputValueException extends RuntimeException {

    public InvalidInputValueException(String msg, Throwable t){
        super(msg, t);
    }

    public InvalidInputValueException(String msg){
        super(msg);
    }

    public InvalidInputValueException(){
        super();
    }
}

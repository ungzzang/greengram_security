package com.green.greengram.common.exception;

public interface ErrorCode {
    //나를 상속받은 ENUM은 String message 멤버필드를 꼭 가져야 한다.
    String getMessage();
}

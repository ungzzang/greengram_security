package com.green.greengram.common.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import io.jsonwebtoken.security.SignatureException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice // AOP (Aspect Orientation Programming, 관점 지향 프로그래밍)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    //(추가 메소드) 우리가 커스텀한 에러가 발생되었을 경우 캐치
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleException(CustomException e) {
        return handleExceptionInternal(e.getErrorCode());
    }


    //Validation 예외가 발생되었을 경우 캐치(내가 지정한대로 안넘어왔을때 잡아냄)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex
                                                                                      , HttpHeaders headers
                                                                                      , HttpStatusCode statusCode
                                                                                      , WebRequest request) {
        return handleExceptionInternal(CommonErrorCode.INVALID_PARAMETER, ex);
    }

    // 밑에랑 합쳤음.
    /*@ExceptionHandler(SignatureException.class) //토큰이 오염 되었을 때
    public ResponseEntity<Object> handleSignatureException() {
        return handleExceptionInternal(UserErrorCode.UNAUTHENTICATED);
    }*/

    @ExceptionHandler({MalformedJwtException.class, SignatureException.class}) //토큰 값이 유효하지 않을 때, 토큰이 오염 되었을 때
    public ResponseEntity<Object> handleMalformedJwtException() {
        return handleExceptionInternal(UserErrorCode.INVALID_TOKEN);
    }

    @ExceptionHandler(ExpiredJwtException.class) //토큰이 만료가 되었을 때
    public ResponseEntity<Object> handleExpiredJwtException() {
        return handleExceptionInternal(UserErrorCode.EXPIRED_TOKEN);
    }


    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) { //위에서 이거 호출할꺼다.
        return handleExceptionInternal(errorCode, null); //밑에 handleExceptionInternal 호출
    }

    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, BindException e) {//BindException얘가 MethodArgumentNotValidException얘 부모
        return ResponseEntity.status(errorCode.getHttpStatus())
                             .body(makeErrorResponse(errorCode, e));
    }


    private MyErrorResponse makeErrorResponse(ErrorCode errorCode, BindException e) {
        return MyErrorResponse.builder() //이미 앞전(MyErrorResponse)에 <String>으로 지정해서 따로 타입지정 안함.
                .resultMessage(errorCode.getMessage())
                .resultData(errorCode.name())
                .valids(e == null ? null : getValidationError(e))
                .build();
    }


    private List<MyErrorResponse.ValidationError> getValidationError(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        //List<FieldError> fieldErrors = e.getFieldErrors();

        List<MyErrorResponse.ValidationError> errors = new ArrayList<>(fieldErrors.size()); //errors - 여기에 필드에러들 담을꺼다.
        for(FieldError fieldError : fieldErrors) {
            errors.add(MyErrorResponse.ValidationError.of(fieldError));
        }

        return errors;
    }
}

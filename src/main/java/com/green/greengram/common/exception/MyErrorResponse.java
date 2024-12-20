package com.green.greengram.common.exception;

import com.green.greengram.common.model.ResultResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.validation.FieldError;

import java.util.List;

@Getter
@SuperBuilder //부모가 가진 빌더 자식도 쓰려고
public class MyErrorResponse extends ResultResponse<String> {
    //Validation 에러메시지 전달
    private final List<ValidationError> valids; //밑에서 리턴된값이 여기 담김


    //Validation 에러가 발생시, 해당 에러의 메세지
    //어떤 필드였고, 에러 메세지를 묶음으로 담을 객체를 만들때 사용
    @Getter
    @Builder
    public static class ValidationError{
        private final String field;
        private final String message;

        // of라는 static 메소드
        public static ValidationError of(final FieldError fieldError) {
            return ValidationError.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();
        }
    }
}

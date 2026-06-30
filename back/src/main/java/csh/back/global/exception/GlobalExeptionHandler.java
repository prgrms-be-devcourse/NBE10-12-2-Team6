package csh.back.global.exception;

import csh.back.domain.trip.group.exception.NotFoundException;
import csh.back.global.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExeptionHandler {

//    @ExceptionHandler(여기는 우리가 만든 에러클래스 하면될듯.class) 이 에러가 반환되면 여기서 돌아가는거
//    public ResponseEntity<ResponseData<Void>> handleProductNotFound(
//            ProductNotFoundException e
//    ) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body(new ResponseData<>("404-1", e.getMessage(), null));
//    }
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ErrorResponse> handleGroupNotFound(
			NotFoundException e
	) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(new ErrorResponse(404, e.getMessage()));  // 따옴표 제거 + new 추가
	}
}

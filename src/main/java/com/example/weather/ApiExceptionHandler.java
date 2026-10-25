package com.example.weather;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.*;
import java.util.Map;
@RestControllerAdvice
public class ApiExceptionHandler {
  private ResponseEntity<Map<String,Object>> err(HttpStatus s,String m){ return ResponseEntity.status(s).body(Map.of("status",s.value(),"error",m)); }
  @ExceptionHandler({ConstraintViolationException.class, org.springframework.web.method.annotation.HandlerMethodValidationException.class, MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
  public ResponseEntity<Map<String,Object>> bad(Exception e){ return err(HttpStatus.BAD_REQUEST,"Invalid request: "+e.getMessage()); }
  @ExceptionHandler(UpstreamException.class)
  public ResponseEntity<Map<String,Object>> up(UpstreamException e){ return err(HttpStatus.BAD_GATEWAY,"Weather data source unavailable, please try again later."); }
}

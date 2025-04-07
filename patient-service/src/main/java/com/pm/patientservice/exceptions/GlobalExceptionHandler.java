package com.pm.patientservice.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HashMap<String,String>> handleValidationException(MethodArgumentNotValidException ex){
        HashMap<String,String> errors= new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error-> errors.put(error.getField(),error.getDefaultMessage()));
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<HashMap<String,String>> hadnleEmailAlreadyExistException(EmailAlreadyExistException ex){
        HashMap<String,String> errors=new HashMap<>();
//        log.warn("Email already exists {}",ex.getMessage());
        errors.put("message","Email already exist");
        return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity<HashMap<String, String>> handlePatientNotFoundException(PatientNotFoundException ex){
        HashMap<String, String> error=new HashMap<>();
        error.put("message", ex.getMessage());
//        log.info(String.valueOf(error));

        return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
    }
}

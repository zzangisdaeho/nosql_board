package com.example.nosql.community.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> handleVersionCheckException(OptimisticLockingFailureException ex, HttpServletRequest request) {
        log.error("OptimisticLockingFailureException occur : ", ex);
        return new ResponseEntity<>("해당 정보가 다른 유저에 의해 이미 변경되었습니다. 리프레시 후 재시도 해 주세요.", HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler
    public ResponseEntity<Object> handleVersionCheckException(IllegalStateException ex, HttpServletRequest request) {
        log.error("IllegalStateException occur : ", ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

    }
}

package com.fancydsp.data.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class InnerErrorController {
    Logger logger = LoggerFactory.getLogger(getClass());


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse errorResponse(Exception e) {
        logger.error(e.getMessage(),e.getClass());
        return new ErrorResponse(e.getMessage(),HttpStatus.BAD_REQUEST.value());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse processUnauthenticatedException(NativeWebRequest request, Exception e) {
        logger.error(e.getMessage(),e.getClass());
        return new ErrorResponse("unauthorized",HttpStatus.UNAUTHORIZED.value());
    }


    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ErrorResponse defaultErrorHandler(HttpServletRequest req, HttpServletResponse response, Exception e) throws Exception {
        logger.error(e.getMessage(), e.getClass());
        return new ErrorResponse(e.getMessage(), response.getStatus());
    }


    public static class ErrorResponse {
        private String message;
        private int code;

        public ErrorResponse(String message, int code) {
            this.message = message;
            this.code = code;
        }

        public String getMessage() {
            return message;
        }
        public int getCode(){
            return code;
        }
    }
}

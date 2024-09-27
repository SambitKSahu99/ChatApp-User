package com.elixr.ChatApp_UserManagement.exceptionhandling;

import com.elixr.ChatApp_UserManagement.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<Response> handleUserException(UserException userException){
        List<String> errors = new ArrayList<>();
        errors.add(userException.getMessage());
        return new ResponseEntity<>(new Response(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Response> handleUserNotFoundException(UserNotFoundException userNotFoundException){
        List<String> errors = new ArrayList<>();
        errors.add(userNotFoundException.getMessage());
        return new ResponseEntity<>(new Response(errors),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNameConflictException.class)
    public ResponseEntity<Response> handleUsernameConflict(UserNameConflictException userNameConflictException){
        List<String> errors = new ArrayList<>();
        errors.add(userNameConflictException.getMessage());
        return new ResponseEntity<>(new Response(errors),HttpStatus.CONFLICT);
    }

}

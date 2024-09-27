package com.elixr.ChatApp_UserManagement.exceptionhandling;

public class UserNameConflictException extends Exception{

    public UserNameConflictException(String message){
        super(message);
    }
}

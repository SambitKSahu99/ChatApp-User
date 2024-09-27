package com.elixr.ChatApp_UserManagement.exceptionhandling;

public class UserNotFoundException extends Exception{

    public UserNotFoundException(String exceptionMessage){
        super(exceptionMessage);
    }
}

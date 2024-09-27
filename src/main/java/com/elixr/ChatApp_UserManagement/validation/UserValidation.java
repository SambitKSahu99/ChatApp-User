package com.elixr.ChatApp_UserManagement.validation;

import com.elixr.ChatApp_UserManagement.contants.MessagesConstants;
import com.elixr.ChatApp_UserManagement.contants.UserConstants;
import com.elixr.ChatApp_UserManagement.exceptionhandling.UserException;
import org.springframework.stereotype.Component;

@Component
public class UserValidation {

    public String isValidUserName(Object userName) throws UserException {
        if((!(userName instanceof String)) || (userName.equals(UserConstants.EMPTY_STRING))) {
            throw new UserException(MessagesConstants.USER_NAME_NOT_VALID);
        }
        return userName.toString();
    }

    public String isValidPassword(Object password) throws UserException {
        if((!(password instanceof String)) || (password.equals(UserConstants.EMPTY_STRING))) {
            throw new UserException(MessagesConstants.PASSWORD_NOT_VALID);
        }
        return password.toString();
    }
}

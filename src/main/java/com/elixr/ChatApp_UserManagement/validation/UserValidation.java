package com.elixr.ChatApp_UserManagement.validation;

import com.elixr.ChatApp_UserManagement.contants.MessagesConstants;
import com.elixr.ChatApp_UserManagement.dto.UserDetailsDto;
import com.elixr.ChatApp_UserManagement.exceptionhandling.UserException;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class UserValidation {

    public void isValidDto(UserDetailsDto userDetailsDto) throws UserException {
        if(ObjectUtils.isEmpty(userDetailsDto.getUserName())){
            throw new UserException(MessagesConstants.USER_NAME_NOT_VALID);
        }
        if(ObjectUtils.isEmpty(userDetailsDto.getPassword())){
            throw new UserException(MessagesConstants.PASSWORD_NOT_VALID);
        }
        if(ObjectUtils.isEmpty(userDetailsDto.getFirstName())){
            throw new UserException(MessagesConstants.FIRST_NOT_VALID);
        }
        if(ObjectUtils.isEmpty(userDetailsDto.getLastName())){
            throw new UserException(MessagesConstants.LAST_NOT_VALID);
        }
    }
}

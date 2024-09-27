package com.elixr.ChatApp_UserManagement.util;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordUtil {

    public String hashPassword(String rawPassword){
        return BCrypt.hashpw(rawPassword,BCrypt.gensalt());
    }

    public boolean verifyPassword(String rawPassword,String hashPassword){
        return BCrypt.checkpw(rawPassword,hashPassword);
    }
}

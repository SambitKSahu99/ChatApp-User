package com.elixr.ChatApp_UserManagement.model;

import com.elixr.ChatApp_UserManagement.contants.UserConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = UserConstants.USER_COLLECTION_NAME)
public class UserDetailsModel {

    @Id
    private UUID id;
    private String userName;
    private String firstName;
    private String lastName;
    private String password;
}

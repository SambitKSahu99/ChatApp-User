package com.elixr.ChatApp_UserManagement.repository;

import com.elixr.ChatApp_UserManagement.model.UserDetailsModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserManagementRepository extends MongoRepository<UserDetailsModel, UUID> {
    boolean existsByUserName(String userName);
    Optional<UserDetailsModel> findByUserName(String userName);
}

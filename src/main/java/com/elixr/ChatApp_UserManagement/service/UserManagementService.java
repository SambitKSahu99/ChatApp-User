package com.elixr.ChatApp_UserManagement.service;

import com.elixr.ChatApp_UserManagement.contants.LogInfoConstants;
import com.elixr.ChatApp_UserManagement.contants.MessagesConstants;
import com.elixr.ChatApp_UserManagement.contants.UserConstants;
import com.elixr.ChatApp_UserManagement.dto.UserDetailsDto;
import com.elixr.ChatApp_UserManagement.exceptionhandling.UserException;
import com.elixr.ChatApp_UserManagement.exceptionhandling.UserNameConflictException;
import com.elixr.ChatApp_UserManagement.exceptionhandling.UserNotFoundException;
import com.elixr.ChatApp_UserManagement.filter.JwtFilter;
import com.elixr.ChatApp_UserManagement.model.UserDetailsModel;
import com.elixr.ChatApp_UserManagement.repository.UserManagementRepository;
import com.elixr.ChatApp_UserManagement.util.PasswordUtil;
import com.elixr.ChatApp_UserManagement.validation.UserValidation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserManagementService {

    private final UserManagementRepository userManagementRepository;
    private final PasswordUtil passwordUtil;
    private final UserValidation userValidation;
    private final JwtFilter jwtFilter;
    @Value(UserConstants.MESSAGE_SERVICE_URL_VALUE)
    private String messageServiceBaseUrl;

    public UserManagementService(UserManagementRepository userManagementRepository, PasswordUtil passwordUtil, UserValidation userValidation, JwtFilter jwtFilter) {
        this.userManagementRepository = userManagementRepository;
        this.passwordUtil = passwordUtil;
        this.userValidation = userValidation;
        this.jwtFilter = jwtFilter;
    }

    public String saveUser(UserDetailsDto userDetailsDto) throws UserException, UserNameConflictException {
        userValidation.isValidDto(userDetailsDto);
        if (userManagementRepository
                .existsByUserName(userDetailsDto.getUserName())) {
            log.info(LogInfoConstants.DB_CALL_FOR_CHECKING_USERNAME);
            throw new UserNameConflictException(MessagesConstants.USER_NAME_ALREADY_TAKEN);
        }
        UserDetailsModel userDetailsModel = UserDetailsModel.builder()
                .id(UUID.randomUUID())
                .firstName(userDetailsDto.getFirstName())
                .lastName(userDetailsDto.getLastName())
                .userName(userDetailsDto.getUserName())
                .password(passwordUtil.hashPassword(userDetailsDto.getPassword()))
                .build();
        userManagementRepository.save(userDetailsModel);
        log.info(LogInfoConstants.SAVING_USER_INFO,userDetailsModel.getUserName());
        return userDetailsModel.getUserName();
    }

    public boolean verifyUser(String userName){
        Optional<UserDetailsModel> user = userManagementRepository.findByUserName(userName);
        log.info(LogInfoConstants.DB_CALL_FOR_CHECKING_USERNAME);
        return user.isPresent();
    }

    public UserDetailsDto getCurrentUserDetails(String currentUserName) throws UserException {
        log.info(LogInfoConstants.RETRIEVING_CURRENT_USER,currentUserName);
        Optional<UserDetailsModel> currentUser = userManagementRepository.findByUserName(currentUserName);
        if(currentUser.isEmpty()){
            log.warn(LogInfoConstants.NO_USER_FOUND);
            throw new UserException(MessagesConstants.USER_NOT_FOUND);
        }
        UserDetailsModel user = currentUser.get();
        return UserDetailsDto.builder()
                .userName(user.getUserName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public List<String> getAllUsers() throws UserNotFoundException {
        List<UserDetailsModel> allUsers = userManagementRepository.findAll();
        log.info(LogInfoConstants.GETTING_ALL_USERS_LIST);
        String currentUser = jwtFilter.getCurrentUser();
        if (allUsers.isEmpty()) {
            throw new UserNotFoundException(MessagesConstants.NO_USERS_FOUND);
        }
        return allUsers.stream()
                .map(UserDetailsModel::getUserName)
                .filter(name -> !name.equals(currentUser))
                .collect(Collectors.toList());
    }

    public UserDetailsDto updateUser(UserDetailsDto userDetailsDto) {
        String currentUserName = jwtFilter.getCurrentUser();
        Optional<UserDetailsModel> currentUserOptional = userManagementRepository.findByUserName(currentUserName);
        UserDetailsModel currentUser = null;
        if(currentUserOptional.isPresent()){
             currentUser = currentUserOptional.get();
        }
        UserDetailsModel user = userManagementRepository.save(UserDetailsModel.builder()
                .id(currentUser.getId())
                .userName(currentUser.getUserName())
                .firstName(userDetailsDto.getFirstName())
                .lastName(userDetailsDto.getLastName())
                        .password(currentUser.getPassword())
                .build());
        log.info(LogInfoConstants.CALLING_DB_TO_UPDATE,user.getUserName());
        return UserDetailsDto.builder()
                .userName(user.getUserName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    public void deleteUser() throws UserException {
        String currentUser = jwtFilter.getCurrentUser();
        Optional<UserDetailsModel> user = userManagementRepository.findByUserName(currentUser);
        log.info(LogInfoConstants.DB_CALL_FOR_CHECKING_USERNAME+" for deleting");
        user.ifPresent(userDetailsModel -> userManagementRepository.deleteById(userDetailsModel.getId()));
        if(user.isEmpty()){
            throw new UserException(MessagesConstants.ERROR_OCCURRED);
        }
    }
}

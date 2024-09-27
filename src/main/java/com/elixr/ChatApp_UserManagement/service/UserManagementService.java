package com.elixr.ChatApp_UserManagement.service;

import com.elixr.ChatApp_UserManagement.contants.MessagesConstants;
import com.elixr.ChatApp_UserManagement.contants.UrlConstants;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserManagementService {

    private final UserManagementRepository userManagementRepository;
    private final PasswordUtil passwordUtil;
    private final UserValidation userValidation;
    private final JwtFilter jwtFilter;
    private final WebClient webClient;

    public UserManagementService(UserManagementRepository userManagementRepository, PasswordUtil passwordUtil, UserValidation userValidation, JwtFilter jwtFilter, WebClient.Builder webClient) {
        this.userManagementRepository = userManagementRepository;
        this.passwordUtil = passwordUtil;
        this.userValidation = userValidation;
        this.jwtFilter = jwtFilter;
        this.webClient = webClient.baseUrl(UrlConstants.MESSAGE_SERVICE_URL).build();
    }

    public String saveUser(UserDetailsDto userDetailsDto) throws UserException, UserNameConflictException {
        String userName = userValidation.isValidUserName(userDetailsDto.getUserName());
        String password = userValidation.isValidPassword(userDetailsDto.getPassword());
        if (userManagementRepository
                .existsByUserName(userName)) {
            throw new UserNameConflictException(MessagesConstants.USER_NAME_ALREADY_TAKEN);
        }
        UserDetailsModel userDetailsModel = UserDetailsModel.builder()
                .id(UUID.randomUUID())
                .userName(userName)
                .password(passwordUtil.hashPassword(password))
                .build();
        userManagementRepository.save(userDetailsModel);
        return userDetailsModel.getUserName();
    }

    public boolean verifyUser(String userName){
        Optional<UserDetailsModel> user = userManagementRepository.findByUserName(userName);
        return user.isPresent();
    }

    public List<String> getAllUsers() throws UserNotFoundException {
        List<UserDetailsModel> allUsers = userManagementRepository.findAll();
        String currentUser = jwtFilter.getUserName();
        if (allUsers.isEmpty()) {
            throw new UserNotFoundException(MessagesConstants.NO_USERS_FOUND);
        }
        return allUsers.stream()
                .map(UserDetailsModel::getUserName) // Extract the userName from each UserDetailsModel
                .filter(name -> !name.equals(currentUser)) // Filter out the userName you want to exclude
                .collect(Collectors.toList());
    }

    public UserDetailsDto updateUser(UserDetailsDto userDetailsDto) throws UserNameConflictException {
        String currentUser = jwtFilter.getUserName();
        UserDetailsModel user = UserDetailsModel.builder()
                .id(userManagementRepository.findByUserName(currentUser).get().getId())
                .userName(userDetailsDto.getUserName())
                .password(passwordUtil.hashPassword(userDetailsDto.getPassword()))
                .build();
        updateMessages(currentUser,userDetailsDto.getUserName());
        UserDetailsModel userDetailsModel = userManagementRepository.save(user);
        return UserDetailsDto.builder()
                .userName(userDetailsModel.getUserName())
                .build();

    }

    public void deleteUser() throws UserException {
        String currentUser = jwtFilter.getUserName();
        Optional<UserDetailsModel> user = userManagementRepository.findByUserName(currentUser);
        user.ifPresent(userDetailsModel -> userManagementRepository.deleteById(userDetailsModel.getId()));
        if(user.isEmpty()){
            throw new UserException(MessagesConstants.ERROR_OCCURRED);
        }
    }

    private void updateMessages(String oldName,String newName){
        String token = jwtFilter.getToken();
        webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path(UrlConstants.MESSAGE_ENDPOINT)
                        .queryParam(UserConstants.OLD_NAME, oldName)
                        .queryParam(UserConstants.NEW_NAME, newName)
                        .build())
                .header(UserConstants.AUTHORIZATION_HEADER,UserConstants.BEARER+token)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}

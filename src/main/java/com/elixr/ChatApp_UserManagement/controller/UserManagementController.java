package com.elixr.ChatApp_UserManagement.controller;

import com.elixr.ChatApp_UserManagement.contants.MessagesConstants;
import com.elixr.ChatApp_UserManagement.contants.UrlConstants;
import com.elixr.ChatApp_UserManagement.contants.UserConstants;
import com.elixr.ChatApp_UserManagement.dto.UserDetailsDto;
import com.elixr.ChatApp_UserManagement.exceptionhandling.UserException;
import com.elixr.ChatApp_UserManagement.exceptionhandling.UserNameConflictException;
import com.elixr.ChatApp_UserManagement.exceptionhandling.UserNotFoundException;
import com.elixr.ChatApp_UserManagement.response.Response;
import com.elixr.ChatApp_UserManagement.service.UserManagementService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(UserConstants.ALLOWED_HEADERS)
public class UserManagementController {

    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @PostMapping(UrlConstants.REGISTER_USER_API_ENDPOINT)
    public ResponseEntity<Response> saveUser(@RequestBody UserDetailsDto userDetailsDto)
            throws UserException, UserNameConflictException {
        String response = userManagementService.saveUser(userDetailsDto);
        String message = response + MessagesConstants.USER_ADDED_MESSAGE;
        return new  ResponseEntity<>(new Response(message),HttpStatus.OK);
    }

    @PostMapping(UrlConstants.VERIFY_USER)
    public ResponseEntity<Boolean> verifyUser(@RequestBody String userName){
        return new ResponseEntity<>(userManagementService.verifyUser(userName),HttpStatus.OK);
    }

    @GetMapping(UrlConstants.USER_END_POINT)
    public ResponseEntity<List<String>> getAllUsers()
            throws UserNotFoundException{
        List<String> response = userManagementService.getAllUsers();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping(UrlConstants.UPDATE_USER_END_POINT)
    public ResponseEntity<Response> updateUser(@RequestBody UserDetailsDto userDetailsDto) throws UserNameConflictException {
        return new ResponseEntity<>(new Response(userManagementService.updateUser(userDetailsDto).getUserName()),HttpStatus.OK);
    }

    @DeleteMapping(UrlConstants.USER_END_POINT)
    public ResponseEntity<Response> deleteUser(HttpServletRequest request, HttpServletResponse response) throws UserException {
        userManagementService.deleteUser();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null){
            new SecurityContextLogoutHandler().logout(request,response,authentication);
        }
        SecurityContextHolder.clearContext();
        return new ResponseEntity<>(new Response(MessagesConstants.DELETED_SUCCESSFUL),HttpStatus.OK);
    }
}

package com.keycloak.userservice.controller;

import com.keycloak.userservice.dto.UserRequestDto;
import com.keycloak.userservice.dto.UserResponseDto;
import com.keycloak.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Path;
import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;


    // api to get user by user id
    @GetMapping("/getUserById/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable String userId){
        return userService.getUserById(userId);
    }

    // api to get user by username
    @GetMapping("/getUserByUsername/{username}")
    public ResponseEntity<UserResponseDto> getUserByUsername(@PathVariable String username){
        return userService.getUserByUsername(username);
    }

    // api to get user by email id
    @GetMapping("/getUserByEmail/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email){
        return userService.getUserByEmail(email);
    }

    // api to add user to keycloak db
    @PostMapping("/addUser")
//    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<UserResponseDto> addUser(@RequestBody UserRequestDto userRequestDto){
        return userService.addUser(userRequestDto);
    }

    // To fetch all users by role
    @GetMapping("/getByRole/{role}")
    public List<UserResponseDto> getAllUsers(@PathVariable String role){
        return userService.getAllUsersByRole(role);
    }

    // To delete user by userId
    @PostMapping("/deleteUserById/{userId}")
    public void deleteUserById(@PathVariable String userId){
        userService.deleteUserById(userId);
    }

    // test api
    @GetMapping("/get")
    public String name(){
        return "String returned...";
    }


}
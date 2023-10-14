package com.tsyhanok.restapi.controller;

import com.tsyhanok.restapi.entity.User;
import com.tsyhanok.restapi.response.UserResponse;
import com.tsyhanok.restapi.service.UserService;
import com.tsyhanok.restapi.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.format.annotation.DateTimeFormat;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserValidator validator;

    @PostMapping("/create")
    public ResponseEntity<UserResponse> createUser(@RequestBody User user) {
        ResponseEntity<UserResponse> validationResponse = validator.validateUser(user);
        return (validationResponse != null) ? validationResponse :
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(new UserResponse("User created successfully: ", userService.create(user).getId()));
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserResponse> updateAllFields(@PathVariable UUID userId,
                                                        @RequestBody User user) {
        ResponseEntity<UserResponse> validationResponse = validator.validateUser(user);
        return (validationResponse != null) ? validationResponse :
                userService.updateAllFields(userId, user) != null ?
                        ResponseEntity.ok(new UserResponse("User updated successfully: ", userId)) :
                        ResponseEntity.notFound().build();
    }

    @PutMapping("/update/fields/{userId}")
    public ResponseEntity<UserResponse> updateSpecificFields(@PathVariable UUID userId,
                                                             @RequestBody Map<String, String> fieldsToUpdate) {

        boolean  isEmailValid = validator.isEmailValid(fieldsToUpdate.get("email"));
        if(isEmailValid)


        ResponseEntity<UserResponse> validationResponse = validator.validateUserCustomFields(userId, fieldsToUpdate);
        return (validationResponse != null) ? validationResponse :
                userService.updateSpecificFields(userId, fieldsToUpdate) != null ?
                        ResponseEntity.ok(new UserResponse("User updated successfully: ", userId)) :
                        ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<UserResponse> delete(@PathVariable UUID userId) {
        userService.delete(userId);
        UserResponse response = new UserResponse("User deleted successfully: ", userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/find-by-birth-date-range")
    public ResponseEntity<List<User>> findUsersByBirthDateRange(
            @RequestParam("fromDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @RequestParam("toDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {
        List<User> users = userService.findUsersByBirthDateRange(fromDate, toDate);
        return users.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(users);
    }
}
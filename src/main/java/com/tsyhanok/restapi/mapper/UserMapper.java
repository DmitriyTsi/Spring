package com.tsyhanok.restapi.mapper;

import com.tsyhanok.restapi.dto.UserDto;
import com.tsyhanok.restapi.entity.User;
import org.springframework.stereotype.Component;

import java.util.Map;


public interface UserMapper {

    UserDto mapToUserDto(User user);
    User mapToUser(UserDto dto);
    User applyAllFieldsToUser(User current, User update);
    User applySpecificFieldsToUser(User current, Map<String, String> fieldsToUpdate);
}

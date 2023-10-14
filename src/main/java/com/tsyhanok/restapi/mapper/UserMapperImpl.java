package com.tsyhanok.restapi.mapper;

import com.tsyhanok.restapi.dto.UserDto;
import com.tsyhanok.restapi.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto mapToUserDto(User user) {
        if (user == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setBirthDate(user.getBirthDate());
        dto.setEmail(user.getEmail());
        dto.setAddress(user.getAddress());
        dto.setPhoneNumber(user.getPhoneNumber());
        return dto;
    }

    @Override
    public User mapToUser(UserDto dto) {
        if(dto == null){
            return  null;
        }
            User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setBirthDate(dto.getBirthDate());
        user.setEmail(dto.getEmail());
        user.setAddress(dto.getAddress());
        user.setPhoneNumber(dto.getPhoneNumber());
        return user;
    }

    public User applyAllFieldsToUser(User current, User update) {
        if (current == null || update == null) {
            return null;
        }
        if (update.getName() != null) {
            current.setName(update.getName());
        }
        if (update.getEmail() != null) {
            current.setEmail(update.getEmail());
        }
        if (update.getSurname() != null) {
            current.setSurname(update.getSurname());
        }
        if (update.getAddress() != null) {
            current.setAddress(update.getAddress());
        }
        if (update.getBirthDate() != null) {
            current.setBirthDate(update.getBirthDate());
        }
        if (update.getPhoneNumber() != null) {
            current.setPhoneNumber(update.getPhoneNumber());
        }
        return current;
    }

    @Override
    public User applySpecificFieldsToUser(User current, Map<String, String> fieldsToUpdate) {
        System.out.println("--Mapper Start--");
        if (current == null || fieldsToUpdate == null) {
            System.out.println("currentUser: " + current);
            System.out.println("fieldsToUpdate: " + fieldsToUpdate);
            return null;
        }

        User user = new User(); // Создание нового объекта User
        user.setId(current.getId());
        System.out.println("-------");
        System.out.println("USER MAPPER - user: " + user.toString());

        for (Map.Entry<String, String> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            // Проверка и установка значений полей
            if (fieldName != null && fieldValue != null) {
                switch (fieldName) {
                    case "name":
                        user.setName(fieldValue);
                        break;

                    case "email":
                        user.setEmail(fieldValue);
                        break;

                    case "surname":
                        user.setSurname(fieldValue);
                        break;

                    case "birth_date":
                        try {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                            LocalDate birthDate = LocalDate.parse(fieldValue, formatter);
                            user.setBirthDate(birthDate);
                        } catch (DateTimeParseException e) {
                            // Обработка ошибки парсинга даты
                        }
                        break;

                    case "address":
                        user.setAddress(fieldValue);
                        break;

                    case "phone_number":
                        user.setPhoneNumber(fieldValue);
                        break;
                }
            }
        }
        System.out.println("-------");
        System.out.println("USER MAPPER - user: " + user.toString());
        return user;
    }


}

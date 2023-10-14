package com.tsyhanok.restapi.service;

import com.tsyhanok.restapi.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public interface UserService {

    // 2.1 Создать пользователя. Позволяет регистрировать пользователей старше [18] лет.
    User create(User user);
    // 2.3 Обновить все пользовательские поля
    User updateAllFields(UUID userId, User update);
    // 2.2 Обновить одно/несколько пользовательских полей
    User updateSpecificFields(UUID userId, Map<String, String> fieldsToUpdate);
    void delete (UUID userId);
    User findById(UUID userId);
    List<User> findUsersByBirthDateRange(LocalDate fromDate, LocalDate toDate);
}






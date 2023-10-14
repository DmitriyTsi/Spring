package com.tsyhanok.restapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tsyhanok.restapi.entity.User;
import com.tsyhanok.restapi.response.UserResponse;
import com.tsyhanok.restapi.service.UserService;
import com.tsyhanok.restapi.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class UserControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserValidator validator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();


    }

    @Test
    public void testCreateUser_Success() {
        // Создаем тестового пользователя
        User user = new User();
        user.setId(java.util.UUID.randomUUID());
        user.setName("John");
        user.setSurname("Doe");
        user.setBirthDate(LocalDate.now().minusYears(25));
        user.setEmail("test@example.com");
        user.setAddress("123 Main St");
        user.setPhoneNumber("555-1234");

        // Устанавливаем моки для валидатора и сервиса
        when(validator.validateUser(user)).thenReturn(null);
        when(userService.create(user)).thenReturn(user);

        // Вызываем метод контроллера
        ResponseEntity<UserResponse> response = userController.createUser(user);

        // Проверяем, что HTTP статус - CREATED
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Проверяем, что в ответе верное сообщение
        UserResponse responseBody = response.getBody();
        assertEquals("User created successfully: ", responseBody.getMessage());

        // Проверяем, что в ответе правильный ID пользователя
        assertEquals(user.getId(), responseBody.getUserId());

        // Проверяем, что сервис был вызван один раз для создания пользователя
        verify(userService, times(1)).create(user);
    }

    @Test
    public void testCreateUser_ValidationFailed() {
        // Создаем тестового пользователя
        User user = new User();
        user.setId(java.util.UUID.randomUUID());
        user.setName("John");
        user.setSurname("Doe");
        user.setBirthDate(LocalDate.now().minusYears(17)); // Возраст менее 18 лет

        // Устанавливаем мок для валидатора, который вернет ошибку валидации
        when(validator.validateUser(user)).thenReturn(
                ResponseEntity.badRequest().body(new UserResponse("User validation failed: age < 18", user.getId()))
        );

        // Вызываем метод контроллера
        ResponseEntity<UserResponse> response = userController.createUser(user);

        // Проверяем, что HTTP статус - BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Проверяем, что в ответе верное сообщение
        UserResponse responseBody = response.getBody();
        assertEquals("User validation failed: age < 18", responseBody.getMessage());

        // Проверяем, что ID пользователя в ответе соответствует ожидаемому
        assertEquals(user.getId(), responseBody.getUserId());

        // Проверяем, что сервис не был вызван для создания пользователя
        verify(userService, never()).create(user);
    }


    // Вспомогательный метод для сериализации объекта в JSON
    private String asJsonString(Object obj) throws Exception {
        return new ObjectMapper().writeValueAsString(obj);
    }


    @Test
    public void testupdateAllFields_Success() {
        User userToUpdate = new User();
        UUID userId = java.util.UUID.randomUUID();
        userToUpdate.setId(userId);
        userToUpdate.setEmail("test@example.com");
        userToUpdate.setName("John");
        userToUpdate.setSurname("Doe");
        userToUpdate.setBirthDate(LocalDate.parse("2024-10-01", DateTimeFormatter.ISO_LOCAL_DATE));
        userToUpdate.setPhoneNumber("123-123-45");
        userToUpdate.setAddress("st Markony 113");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("Updated John");
        updatedUser.setEmail("updated@example.com");

        when(validator.validateUser(userToUpdate)).thenReturn(null);
        when(userService.updateAllFields(userId, userToUpdate)).thenReturn(updatedUser);

        ResponseEntity<UserResponse> response = userController.updateAllFields(userId, userToUpdate);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("User updated successfully: ", responseBody.getMessage());
        assertEquals(userId, responseBody.getUserId());

        verify(validator, times(1)).validateUser(userToUpdate);
        verify(userService, times(1)).updateAllFields(userId, userToUpdate);
    }

    @Test
    public void testUpdateSpecificFields_Success() {
        // Создаем тестового пользователя
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setName("John");
        user.setSurname("Doe");
        user.setBirthDate(LocalDate.now().minusYears(25));
        user.setEmail("test@example.com");
        user.setAddress("123 Main St");
        user.setPhoneNumber("555-1234");

        // Устанавливаем моки для сервиса и валидатора
        when(userService.findById(userId)).thenReturn(user);
        when(validator.validateUser(user)).thenReturn(null);
        when(userService.updateSpecificFields(userId, new HashMap<>())).thenReturn(user);

        // Вызываем метод контроллера
        ResponseEntity<UserResponse> response = userController.updateSpecificFields(userId, new HashMap<>());

        // Проверяем, что HTTP статус - OK
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Проверяем, что в ответе верное сообщение
        UserResponse responseBody = response.getBody();
        assertEquals("User updated successfully: ", responseBody.getMessage());

        // Проверяем, что ID пользователя в ответе соответствует ожидаемому
        assertEquals(userId, responseBody.getUserId());

        // Проверяем, что сервис был вызван для обновления полей пользователя
        verify(userService, times(1)).updateSpecificFields(userId, new HashMap<>());
    }

    @Test
    public void testUpdateSpecificFields_UserNotFound() {
        // Генерируем случайный UUID, который не связан с существующим пользователем
        UUID userId = UUID.randomUUID();

        // Устанавливаем мок для сервиса, который вернет null, чтобы симулировать отсутствие пользователя
        when(userService.findById(userId)).thenReturn(null);

        // Вызываем метод контроллера
        ResponseEntity<UserResponse> response = userController.updateSpecificFields(userId, new HashMap<>());

        // Проверяем, что HTTP статус - NOT_FOUND
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Проверяем, что сервис не был вызван для обновления полей пользователя
        verify(userService, never()).updateSpecificFields(userId, new HashMap<>());
    }

    @Test
    public void testUpdateSpecificFields_ValidationFailed() {
        // Создаем тестового пользователя
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setName("John");
        user.setSurname("Doe");
        user.setBirthDate(LocalDate.now().minusYears(17)); // Возраст менее 18 лет

        // Устанавливаем моки для сервиса и валидатора
        when(userService.findById(userId)).thenReturn(user);
        when(validator.validateUser(user)).thenReturn(
                ResponseEntity.badRequest().body(new UserResponse("User validation failed: age < 18", user.getId()))
        );

        // Вызываем метод контроллера
        ResponseEntity<UserResponse> response = userController.updateSpecificFields(userId, new HashMap<>());

        // Проверяем, что HTTP статус - BAD_REQUEST
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // Проверяем, что в ответе верное сообщение
        UserResponse responseBody = response.getBody();
        assertEquals("User validation failed: age < 18", responseBody.getMessage());

        // Проверяем, что сервис не был вызван для обновления полей пользователя
        verify(userService, never()).updateSpecificFields(userId, new HashMap<>());
    }


    @Test
    public void testDelete_Success() {
        // Генерируем случайный UUID пользователя
        UUID userId = UUID.randomUUID();

        // Устанавливаем мок для сервиса
        doNothing().when(userService).delete(userId);

        // Вызываем метод контроллера
        ResponseEntity<UserResponse> response = userController.delete(userId);

        // Проверяем, что HTTP статус - OK
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Проверяем, что в ответе верное сообщение
        UserResponse responseBody = response.getBody();
        assertEquals("User deleted successfully: ", responseBody.getMessage());

        // Проверяем, что сервис был вызван для удаления пользователя
        verify(userService, times(1)).delete(userId);
    }


    @Test
    void testFindUsersByBirthDateRange() {
        // Создаем тестовые данные
        LocalDate fromDate = LocalDate.of(2001, 1, 1);
        LocalDate toDate = LocalDate.of(2002, 12, 31);
        List<User> users = new ArrayList<>();
        User user1 = new User();
        UUID userId1 = java.util.UUID.randomUUID();
        user1.setId(userId1);
        user1.setEmail("test@example.com");
        user1.setName("John");
        user1.setSurname("Doe");
        user1.setBirthDate(LocalDate.parse("2000-10-01", DateTimeFormatter.ISO_LOCAL_DATE));
        user1.setPhoneNumber("123-123-45");
        user1.setAddress("st Markony 113");

        User user2 = new User();
        UUID userId2 = java.util.UUID.randomUUID();
        user2.setId(userId2);
        user2.setEmail("test@example.com");
        user2.setName("John");
        user2.setSurname("Doe");
        user2.setBirthDate(LocalDate.parse("2003-10-01", DateTimeFormatter.ISO_LOCAL_DATE));
        user2.setPhoneNumber("123-123-45");
        user2.setAddress("st Markony 113");

        users.add(user1);
        users.add(user2);

        // Мокируем сервис
        when(userService.findUsersByBirthDateRange(fromDate, toDate)).thenReturn(users);

        // Вызываем метод контроллера
        ResponseEntity<List<User>> responseEntity = userController.findUsersByBirthDateRange(fromDate, toDate);

        // Проверяем, что ответ имеет ожидаемый HTTP статус
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        // Проверяем, что ответ содержит список пользователей
        assertEquals(users, responseEntity.getBody());

        // Проверяем, что сервис был вызван один раз с ожидаемыми аргументами
        verify(userService, times(1)).findUsersByBirthDateRange(fromDate, toDate);
    }
}



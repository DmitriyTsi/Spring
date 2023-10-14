package com.tsyhanok.restapi.service;

import com.tsyhanok.restapi.entity.User;
import com.tsyhanok.restapi.mapper.UserMapper;
import com.tsyhanok.restapi.mapper.UserMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import com.tsyhanok.restapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        // Нет необходимости инициализировать MockitoAnnotations в JUnit 5
    }

    @Test
    public void testUpdateAllFields() {
        User currentUser = new User();
        UUID userId = UUID.randomUUID();
        currentUser.setId(userId);
        currentUser.setEmail("test@example.com");
        currentUser.setName("John");
        currentUser.setSurname("Doe");
        currentUser.setBirthDate(LocalDate.parse("01.10.1995", DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        currentUser.setPhoneNumber("123-123-45");
        currentUser.setAddress("st Markony 113");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setEmail("billy@example.com");
        updatedUser.setName("Billy");
        updatedUser.setSurname("Trump");
        updatedUser.setBirthDate(LocalDate.parse("11.10.2005", DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        updatedUser.setPhoneNumber("555-555-55");
        updatedUser.setAddress("st Markony 258");


        // Мокируйте вызовы репозитория и маппера
        when(userRepository.findById(userId)).thenReturn(Optional.of(currentUser));
        when(userMapper.applyAllFieldsToUser(currentUser, updatedUser)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        // Вызовите метод, который вы хотите протестировать
        User resultUser = userService.updateAllFields(userId, updatedUser);

        // Проверьте, что метод вернул ожидаемого пользователя
        assertNotNull(resultUser);
        assertEquals(userId, resultUser.getId());
        assertEquals("Billy", resultUser.getName());
        assertEquals("billy@example.com", resultUser.getEmail());

        // Проверьте, что вызовы методов репозитория и маппера были выполнены
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(updatedUser);
        verify(userMapper, times(1)).applyAllFieldsToUser(currentUser, updatedUser);
    }

    @Test
    void createUser() {
        User userToCreate = new User();
        userToCreate.setId(UUID.randomUUID());
        userToCreate.setEmail("test@example.com");
        userToCreate.setName("John");
        userToCreate.setSurname("Doe");
        userToCreate.setBirthDate(LocalDate.parse("01.10.2020",
                DateTimeFormatter.ofPattern("dd.MM.yyyy")));


        when(userRepository.save(userToCreate)).thenReturn(userToCreate);
        User createdUser = userService.create(userToCreate);

        assertNotNull(createdUser);
        assertEquals(userToCreate, createdUser);
        verify(userRepository, times(1)).save(userToCreate);
    }

    @Test
    public void testUpdateSpecificFields_Success() {
        UUID userId = UUID.randomUUID();
        Map<String, String> fieldsToUpdate = new HashMap<>();
        fieldsToUpdate.put("name", "NewName");
        fieldsToUpdate.put("surname", "NewSurname");

        User originalUser = new User();
        originalUser.setId(userId);
        originalUser.setName("OldName");
        originalUser.setSurname("OldSurname");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("NewName");
        updatedUser.setSurname("NewSurname");

        when(userRepository.findById(userId)).thenReturn(Optional.of(originalUser));
        when(userMapper.applySpecificFieldsToUser(originalUser, fieldsToUpdate)).thenReturn(updatedUser);
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        User result = userService.updateSpecificFields(userId, fieldsToUpdate);

        assertEquals(updatedUser, result);

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).applySpecificFieldsToUser(originalUser, fieldsToUpdate);
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    public void testUpdateSpecificFields_UserNotFound() {
        UUID userId = UUID.randomUUID();
        Map<String, String> fieldsToUpdate = new HashMap<>();
        fieldsToUpdate.put("name", "NewName");
        fieldsToUpdate.put("surname", "NewSurname");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                userService.updateSpecificFields(userId, fieldsToUpdate));

        assertEquals("java.nio.file.attribute.UserPrincipalNotFoundException", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, never()).applySpecificFieldsToUser(any(), any());
        verify(userRepository, never()).save(any());
    }



    @Test
    void testFindUsersByBirthDateRange() {
        List<User> userList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            User user = new User();
            user.setId(UUID.randomUUID());
            user.setName("Name" + i);
            user.setSurname("Surname" + i);
            // Генерируем рандомную дату рождения в пределах 20 лет назад от текущей даты
            LocalDate birthDate = LocalDate.now().minusYears(20).
                    minusDays(random.nextInt(365 * 20));
            user.setBirthDate(birthDate);
            user.setEmail("user" + i + "@example.com");
            user.setAddress("Address" + i);
            user.setPhoneNumber("123-456-" + i);
            userList.add(user);
        }
        LocalDate fromDate = LocalDate.parse("01.10.1995",
                DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        LocalDate toDate = LocalDate.parse("01.10.2000",
                DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        when(userRepository.findAll()).thenReturn(userList);

        List<User> resultUserList = userService.findUsersByBirthDateRange(fromDate, toDate);
        for (User user : resultUserList) {
            // Проверяем, что результат не равен null
            assertNotNull(resultUserList);

            // Проверяем, что все пользователи в результате находятся в заданном диапазоне дат
            for (User user1 : resultUserList) {
                assertTrue(user.getBirthDate().isEqual(fromDate) || user.getBirthDate().isAfter(fromDate));
                assertTrue(user.getBirthDate().isBefore(toDate));
            }
        }
    }
}

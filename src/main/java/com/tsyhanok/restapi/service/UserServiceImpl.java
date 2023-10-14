package com.tsyhanok.restapi.service;

import com.tsyhanok.restapi.entity.User;
import com.tsyhanok.restapi.mapper.UserMapper;
import com.tsyhanok.restapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    UserRepository repository;
    UserMapper mapper;

    public UserServiceImpl(UserRepository repository, UserMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public User create(User user) {
        try {
            return repository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user: " + user, e);
        }
    }

    @Override
    public User updateAllFields(UUID id, User update) {
        User currentUser = findById(id);
        final User userUpdated = mapper.applyAllFieldsToUser(currentUser, update);
        return repository.save(userUpdated);
    }

    @Override
    public User updateSpecificFields(UUID userId, Map<String, String> fieldsToUpdate) {
        User user = findById(userId);
        User userUpdated = mapper.applySpecificFieldsToUser(user, fieldsToUpdate);
        return repository.save(userUpdated);
    }

    @Override
    public void delete(UUID userId) {
        repository.delete(findById(userId));
    }

    @Override
    public List<User> findUsersByBirthDateRange(LocalDate fromDate, LocalDate toDate) {
        Iterable<User> userList = repository.findAll();
        List<User> result = new ArrayList<>();

        for (User user : userList) {
            LocalDate userBirthday = user.getBirthDate();
            // Проверяем, что дата рождения пользователя находится в указанном диапазоне
            if ((userBirthday.isEqual(fromDate) || userBirthday.isAfter(fromDate))
                    && (userBirthday.isEqual(toDate) || userBirthday.isBefore(toDate))) {
                result.add(user);
            }
        }
        return result;
    }

    @Override
    public User findById(UUID userId) {
        try {
            return repository.findById(userId)
                    .orElseThrow(() ->
                            new UserPrincipalNotFoundException("User not found with ID: " + userId));
        } catch (UserPrincipalNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

//Требования:
//1. Он имеет следующие поля:
//1.1. Электронная почта (обязательно). Добавить проверку по шаблону электронной почты
//1.2. Имя (обязательно)
//1.3. Фамилия (обязательно)
//1.4. Дата рождения (обязательно). Значение должно быть раньше текущей даты.
//1,5. Адрес (необязательно)
//1.6. Телефонный номер (не обязательно)
//
//2. Имеет следующий функционал:
//2.1. Создать пользователя. Позволяет регистрировать пользователей старше [18] лет.
//Значение [18] следует взять из файла свойств.

//
//2.2. Обновить одно/несколько пользовательских полей
//2.3. Обновить все пользовательские поля
//2.4. Удалить пользователя
//2.5. Поиск пользователей по диапазону дат рождения. Добавьте проверку, которая проверяет,
//что «От» меньше «Кому». Должен вернуть список объектов


//3. Код покрывается модульными тестами с использованием Spring.
//4. В коде есть обработка ошибок для REST.
//5. Ответы API предоставляются в формате JSON.
//6. Использование базы данных не обязательно. Уровень сохранения данных не требуется.
//7. Любая версия Spring Boot. Java-версия на ваш выбор
//8. Для создания проекта вы можете использовать утилиту Spring Initializer: Spring Initializr.
//
//Пожалуйста, обрати внимание:
//мы оцениваем только те задания, в которых выполнены все требования
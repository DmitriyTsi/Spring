package com.tsyhanok.restapi.validator;

import com.tsyhanok.restapi.entity.User;
import com.tsyhanok.restapi.response.UserResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Getter
@Component
public class UserValidator {

    private final Environment environment;
    private final Integer minAge;

    @Autowired
    public UserValidator(Environment environment) {
        this.environment = environment;
        minAge = Integer.valueOf(Objects.requireNonNull(environment.getProperty("app.minAge")));
    }

     public ResponseEntity<UserResponse> validateUser(User user) {
        boolean ageValid = isAgeValid(user.getBirthDate());
        boolean mailValid = isEmailValid(user.getEmail());
        if (!ageValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UserResponse("User validation failed: age < " +
                            minAge, user.getId()));
        }
        if (!mailValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new UserResponse("User validation failed: email not valid " + user.getEmail(),
                            user.getId()));
        }
        return null; // Если данные прошли валидацию, возвращаем null
    }

    public ResponseEntity<UserResponse> validateUserCustomFields (UUID userId, Map<String, String> fields) {


        for (String value : fields.keySet()) {
            if(value == "email") {

            }
            if(value == "birthDate") {

            }

        }


        LocalDate birthDate = LocalDate.parse(fields.get("birthDate"));
        boolean ageValid = isAgeValid(birthDate);
            boolean mailValid = isEmailValid(fields.get("email"));
            if (!ageValid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new UserResponse("User validation failed: age < " +
                                minAge, userId));
            }
            if (!mailValid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new UserResponse("User validation failed: email not valid " + fields.get("email"),
                                userId));
            }
            return null; // Если данные прошли валидацию, возвращаем null
        }


    public boolean isEmailValid(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }
    public boolean isAgeValid(LocalDate birthDate) {
        LocalDate currentDate = LocalDate.now();
        int userAge = currentDate.getYear() - birthDate.getYear();
        if(userAge < 1){
            return false;}

        if (birthDate.getMonthValue() > currentDate.getMonthValue() ||
                (birthDate.getMonthValue() == currentDate.getMonthValue() &&
                        birthDate.getDayOfMonth() > currentDate.getDayOfMonth())) {
            userAge--;
        }
        return userAge >= minAge;
    }

}

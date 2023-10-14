package com.tsyhanok.restapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    private UUID id;
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
    private String address;
    private String phoneNumber;
}

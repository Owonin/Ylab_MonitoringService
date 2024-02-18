package com.example.out.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {
    private Integer userId;
    private String username;
    private String password;
    private Set<String> roles;
}

package com.diplomado.ms_auth.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDto {
    private String message;
    private String token;
    private String username;
    private String role;
}
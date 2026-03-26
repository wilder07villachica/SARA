package com.diplomado.ms_auth.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionStatusDTO {
    private String usuario;
    private boolean activa;
    private Long idSession;
}
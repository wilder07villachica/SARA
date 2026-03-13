package com.diplomado.ms_backend_api.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertaMasivaDTO {
    private boolean activa;
    private int casos;
    private int ventanaHoras;
    private String criterio;
}

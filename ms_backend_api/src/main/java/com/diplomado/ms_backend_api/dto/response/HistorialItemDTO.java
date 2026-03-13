package com.diplomado.ms_backend_api.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistorialItemDTO {
    private LocalDateTime fechaHora;
    private String prioridad;
    private String recomendacion;
    private String resultado;
}

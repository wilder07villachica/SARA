package com.diplomado.ms_backend_api.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrioridadDTO {
    private String nivel;   // P1/P2/P3
    private String accion;  // Escalar / Revisar
    private String motivo;
}

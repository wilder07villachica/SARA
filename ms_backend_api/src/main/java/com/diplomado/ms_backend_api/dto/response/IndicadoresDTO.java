package com.diplomado.ms_backend_api.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IndicadoresDTO {
    private String redVoz;   // OK / NO_REGISTRADO / NO_DISPONIBLE
    private String redDatos; // OK / NO_REGISTRADO / NO_DISPONIBLE
    private String volte;    // (futuro)
}

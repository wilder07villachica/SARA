package com.diplomado.ms_backend_api.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecomendacionDTO {
    private String titulo;
    private String detalle;
}

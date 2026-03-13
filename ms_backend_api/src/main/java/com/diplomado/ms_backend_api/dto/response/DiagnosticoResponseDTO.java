package com.diplomado.ms_backend_api.dto.response;

import com.diplomado.ms_backend_api.dto.LineaDashboardDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiagnosticoResponseDTO {
    private LineaDashboardDTO linea;
    private IndicadoresDTO indicadores;
    private PrioridadDTO prioridad;
    private RecomendacionDTO recomendacion;
    private AlertaMasivaDTO alertaMasiva;
    private List<HistorialItemDTO> historial72h;
}

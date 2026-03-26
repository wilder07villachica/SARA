package com.diplomado.ms_backend_api.service.diagnostico;

import com.diplomado.ms_backend_api.dto.LineaDashboardDTO;
import com.diplomado.ms_backend_api.dto.response.*;
import com.diplomado.ms_backend_api.models.ConsultaAuditoria;
import com.diplomado.ms_backend_api.repository.ConsultaAuditoriaRepository;
import com.diplomado.ms_backend_api.service.ConsultaService;
import com.diplomado.ms_backend_api.service.reglas.ReglasDiagnosticoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiagnosticoService {

    private final ConsultaService consultaService;
    private final ReglasDiagnosticoService reglas;
    private final ConsultaAuditoriaRepository auditoriaRepo;
    private final SessionValidator sessionValidator;

    public DiagnosticoResponseDTO diagnosticar(String numero, String usuario) {

        sessionValidator.requireActiveSession(usuario);

        LineaDashboardDTO linea = consultaService.consultar(numero);

        IndicadoresDTO indicadores = reglas.calcularIndicadores(linea);
        PrioridadDTO prioridad = reglas.calcularPrioridad(indicadores);
        RecomendacionDTO recomendacion = reglas.generarRecomendacion(indicadores);

        String criterioMasivo = criterio(indicadores);
        int ventanaHoras = 3;

        long casosPrevios = auditoriaRepo.countByCriterioMasivoAndFechaHoraAfter(
                criterioMasivo, LocalDateTime.now().minusHours(ventanaHoras)
        );

        long casos = casosPrevios + 1;

        boolean alerta = !"SIN_CRITERIO".equals(criterioMasivo) && casos >= 3;

        AlertaMasivaDTO alertaMasiva = AlertaMasivaDTO.builder()
                .activa(alerta)
                .casos((int) casos)
                .ventanaHoras(ventanaHoras)
                .criterio(criterioMasivo)
                .build();

        List<HistorialItemDTO> historial = auditoriaRepo
                .findTop10ByNumeroAndFechaHoraAfterOrderByFechaHoraDesc(
                        numero, LocalDateTime.now().minusHours(72)
                )
                .stream()
                .map(a -> HistorialItemDTO.builder()
                        .fechaHora(a.getFechaHora())
                        .prioridad(a.getPrioridad())
                        .recomendacion(a.getRecomendacion())
                        .resultado(a.getResultado())
                        .build())
                .toList();

        auditoriaRepo.save(ConsultaAuditoria.builder()
                .numero(numero)
                .usuario(usuario)
                .fechaHora(LocalDateTime.now())
                .prioridad(prioridad.getNivel())
                .recomendacion(recomendacion.getTitulo())
                .alertaMasiva(alerta)
                .criterioMasivo(criterioMasivo)
                .resultado("OK")
                .build());

        return DiagnosticoResponseDTO.builder()
                .linea(linea)
                .indicadores(indicadores)
                .prioridad(prioridad)
                .recomendacion(recomendacion)
                .alertaMasiva(alertaMasiva)
                .historial72h(historial)
                .build();
    }

    private String criterio(IndicadoresDTO ind) {
        if ("NO_REGISTRADO".equals(ind.getRedVoz()) && "NO_REGISTRADO".equals(ind.getRedDatos())) {
            return "VOZ_Y_DATOS_NO_REGISTRADO";
        }
        if ("NO_REGISTRADO".equals(ind.getRedVoz())) return "VOZ_NO_REGISTRADO";
        if ("NO_REGISTRADO".equals(ind.getRedDatos())) return "DATOS_NO_REGISTRADO";
        return "SIN_CRITERIO";
    }
}
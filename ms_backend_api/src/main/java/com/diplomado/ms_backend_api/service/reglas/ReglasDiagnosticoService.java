package com.diplomado.ms_backend_api.service.reglas;

import com.diplomado.ms_backend_api.dto.LineaDashboardDTO;
import com.diplomado.ms_backend_api.dto.response.IndicadoresDTO;
import com.diplomado.ms_backend_api.dto.response.PrioridadDTO;
import com.diplomado.ms_backend_api.dto.response.RecomendacionDTO;
import org.springframework.stereotype.Service;

@Service
public class ReglasDiagnosticoService {

    public IndicadoresDTO calcularIndicadores(LineaDashboardDTO d) {
        String voz = normalize(d.getVozEstado());
        String datosEstado = normalize(d.getDatosEstadoRed());

        String indVoz = vozEstadoToIndicador(voz);
        String indDatos = estadoDatosToIndicador(datosEstado);

        return IndicadoresDTO.builder()
                .redVoz(indVoz)
                .redDatos(indDatos)
                .volte("NO_DISPONIBLE")
                .build();
    }

    public PrioridadDTO calcularPrioridad(IndicadoresDTO ind) {
        if ("NO_REGISTRADO".equals(ind.getRedVoz()) && "NO_REGISTRADO".equals(ind.getRedDatos())) {
            return PrioridadDTO.builder()
                    .nivel("P2")
                    .accion("Escalar a segundo nivel")
                    .motivo("No registrado en red de voz y red de datos")
                    .build();
        }

        if ("NO_REGISTRADO".equals(ind.getRedVoz()) || "NO_REGISTRADO".equals(ind.getRedDatos())) {
            return PrioridadDTO.builder()
                    .nivel("P1")
                    .accion("Revisar en primer nivel")
                    .motivo("Falla parcial de registro")
                    .build();
        }

        return PrioridadDTO.builder()
                .nivel("P3")
                .accion("Atención estándar")
                .motivo("Indicadores en estado OK")
                .build();
    }

    public RecomendacionDTO generarRecomendacion(IndicadoresDTO ind) {
        if ("NO_REGISTRADO".equals(ind.getRedVoz()) && "NO_REGISTRADO".equals(ind.getRedDatos())) {
            return RecomendacionDTO.builder()
                    .titulo("Escalar: sin registro en red")
                    .detalle("No registra en voz ni datos. Sugerir reinicio SIM/equipo y escalar a segundo nivel.")
                    .build();
        }
        if ("NO_REGISTRADO".equals(ind.getRedDatos()) && "OK".equals(ind.getRedVoz())) {
            return RecomendacionDTO.builder()
                    .titulo("Revisión de datos/APN")
                    .detalle("Voz OK, datos no registrado. Verificar aprovisionamiento de datos, APN y PCRF.")
                    .build();
        }
        if ("NO_REGISTRADO".equals(ind.getRedVoz()) && "OK".equals(ind.getRedDatos())) {
            return RecomendacionDTO.builder()
                    .titulo("Revisión de voz/registro")
                    .detalle("Datos OK, voz no registrado. Verificar registro/cobertura y parámetros de voz.")
                    .build();
        }
        return RecomendacionDTO.builder()
                .titulo("Servicio estable")
                .detalle("No se detectan anomalías críticas. Continuar diagnóstico según guías operativas.")
                .build();
    }

    // Helpers
    private String normalize(String s) {
        if (s == null) return "NO_DISPONIBLE";
        String v = s.trim().toUpperCase();
        return v.isEmpty() ? "NO_DISPONIBLE" : v;
    }

    private String vozEstadoToIndicador(String vozEstado) {
        if (vozEstado.equals("NO_DISPONIBLE")) return "NO_DISPONIBLE";
        if (vozEstado.contains("NO REGISTRADO") || vozEstado.contains("NO_REGISTRADO") || vozEstado.contains("NO")) return "NO_REGISTRADO";
        return "OK";
    }

    private String estadoDatosToIndicador(String estadoDatos) {
        if (estadoDatos.equals("NO_DISPONIBLE")) return "NO_DISPONIBLE";
        if (estadoDatos.contains("NO REGISTRADO") || estadoDatos.contains("NO_REGISTRADO") || estadoDatos.contains("NO")) return "NO_REGISTRADO";
        if (estadoDatos.contains("REGISTRADO")) return "OK";
        // fallback
        return "NO_DISPONIBLE";
    }
}

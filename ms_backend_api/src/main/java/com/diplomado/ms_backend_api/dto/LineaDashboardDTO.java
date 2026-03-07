package com.diplomado.ms_backend_api.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LineaDashboardDTO {
    private String numeroTelefono;

    private String imei;
    private String marca;
    private String modelo;
    private String estadoImei;
    private String condicionImei;

    private String perfilSuscriptor;
    private String tecnologiaActiva;
    private String planComercial;

    private String vozEstado;
    private String vozVlr;
    private String vozPaisOperador;
    private String vozRedRegistrada;
    private String vozDisponibilidad;
    private String vozUltimaCelda;
    private String vozAccesible;
    private LocalDateTime vozFechaRegistro;

    private String datosUltimoNe;
    private String datosPaisOperador;
    private String datosUltimaRed;
    private String datosUltimaCelda;
    private LocalDateTime datosUltimoRegistro;
    private String apnUso;
    private String ipApn;
}

package com.diplomado.ms_backend_api.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "consulta_auditoria")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsultaAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numero;
    private String usuario;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    private String prioridad;
    private String recomendacion;

    @Column(name = "alerta_masiva")
    private boolean alertaMasiva;

    @Column(name = "criterio_masivo")
    private String criterioMasivo;

    private String resultado; // OK / PARCIAL / ERROR
}

package com.diplomado.ms_backend_api.projections;

import java.time.LocalDateTime;

public interface LineaDashboardProjection {
    String getNumeroTelefono();

    // Equipo (BSCS/PCRF)
    String getImei();
    String getMarca();
    String getModelo();
    String getEstadoImei();
    String getCondicionImei();

    // Línea
    String getPerfilSuscriptor();
    String getTecnologiaActiva();
    String getPlanComercial();

    // Voz (HSS)
    String getVozEstado();
    String getVozVlr();
    String getVozPaisOperador();
    String getVozRedRegistrada();
    String getVozDisponibilidad();
    String getVozUltimaCelda();
    String getVozAccesible();
    LocalDateTime getVozFechaRegistro();

    // ✅ Datos (RedViva)
    String getDatosEstadoRed();  // <-- NUEVO (redviva_estadoRedDatos)
    String getDatosUltimoNe();
    String getDatosPaisOperador();
    String getDatosUltimaRed();
    String getDatosUltimaCelda();
    LocalDateTime getDatosUltimoRegistro();
    String getApnUso();
    String getIpApn();
}

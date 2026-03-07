package com.diplomado.ms_backend_api.repository;

import com.diplomado.ms_backend_api.models.LineaRow;
import com.diplomado.ms_backend_api.projections.LineaDashboardProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LineaRepository extends JpaRepository<LineaRow, String> {

    @Query(value = """
        SELECT
          numeroTelefono AS numeroTelefono,

          -- Equipo
          bscs_imei AS imei,
          bscs_marca AS marca,
          bscs_modelo AS modelo,
          pcrf_estadoIMEI AS estadoImei,
          pcrf_condicionIMEI AS condicionImei,

          -- Línea
          bscs_perfilPlanComercial AS perfilSuscriptor,
          pcrf_tecnologiaActiva AS tecnologiaActiva,
          pcrf_planComercial AS planComercial,

          -- Voz (HSS)
          hss_estadoRedVoz AS vozEstado,
          hss_vlrRegistrada AS vozVlr,
          hss_paisOperador AS vozPaisOperador,
          hss_redRegistrada AS vozRedRegistrada,
          hss_disponibilidad AS vozDisponibilidad,
          hss_ultimaCelda AS vozUltimaCelda,
          hss_accesible AS vozAccesible,
          hss_fechaRegistro AS vozFechaRegistro,

          -- Datos (RedViva)
          redviva_ultimoNERegistrado AS datosUltimoNe,
          redviva_paisOperadorDatos AS datosPaisOperador,
          redviva_ultimaRedRegistrada AS datosUltimaRed,
          redviva_ultimaCeldaDatos AS datosUltimaCelda,
          redviva_ultimoRegistroConexion AS datosUltimoRegistro,
          redviva_apnUso AS apnUso,
          redviva_ipApn AS ipApn

        FROM linea_diagnostico_consolidado
        WHERE numeroTelefono = :numero
        """, nativeQuery = true)
    Optional<LineaDashboardProjection> consultarDashboard(@Param("numero") String numero);
}

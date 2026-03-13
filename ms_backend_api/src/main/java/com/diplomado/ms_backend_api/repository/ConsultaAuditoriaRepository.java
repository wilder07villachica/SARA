package com.diplomado.ms_backend_api.repository;

import com.diplomado.ms_backend_api.models.ConsultaAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaAuditoriaRepository extends JpaRepository<ConsultaAuditoria, Long> {
    List<ConsultaAuditoria> findTop10ByNumeroAndFechaHoraAfterOrderByFechaHoraDesc(String numero, LocalDateTime after);
    long countByCriterioMasivoAndFechaHoraAfter(String criterioMasivo, LocalDateTime after);
}

package com.diplomado.ms_auth.repository;

import com.diplomado.ms_auth.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    boolean existsByUsuarioLdapAndEstado(String usuario, String estado);
    Optional<Session> findTopByUsuarioLdapAndEstadoOrderByFechaInicioDesc(String usuario, String estado);
}

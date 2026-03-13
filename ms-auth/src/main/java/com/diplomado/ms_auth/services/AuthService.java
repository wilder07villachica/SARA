package com.diplomado.ms_auth.services;

import com.diplomado.ms_auth.dto.SessionStatusDTO;
import com.diplomado.ms_auth.models.Session;
import com.diplomado.ms_auth.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LdapService ldapService;
    private final SessionRepository sessionRepository;

    @Transactional
    public String login(String username, String password, String ip) {
        if (sessionRepository.existsByUsuarioLdapAndEstado(username, "ACTIVA")) {
            throw new RuntimeException("Ya existe una sesión activa para este usuario");
        }

        if (!ldapService.authenticate(username, password)) {
            throw new RuntimeException("Credenciales inválidas");
        }

        Session session = new Session();
        session.setUsuarioLdap(username);
        session.setFechaInicio(LocalDateTime.now());
        session.setEstado("ACTIVA");
        session.setIpOrigen(ip);

        sessionRepository.save(session);
        return "Login exitoso";
    }

    @Transactional
    public String logout(String username) {
        Session session = sessionRepository
                .findTopByUsuarioLdapAndEstadoOrderByFechaInicioDesc(username, "ACTIVA")
                .orElseThrow(() -> new RuntimeException("No hay sesión activa"));

        session.setFechaFin(LocalDateTime.now());
        session.setEstado("CERRADA");
        sessionRepository.save(session);

        return "Logout exitoso";
    }

    public SessionStatusDTO sessionStatus(String username) {
        var opt = sessionRepository.findTopByUsuarioLdapAndEstadoOrderByFechaInicioDesc(username, "ACTIVA");
        return SessionStatusDTO.builder()
                .usuario(username)
                .activa(opt.isPresent())
                .idSession(opt.map(Session::getIdSession).orElse(null))
                .build();
    }
}

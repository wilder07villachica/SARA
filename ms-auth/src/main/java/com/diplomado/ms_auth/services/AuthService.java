package com.diplomado.ms_auth.services;

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
        // 1) Si ya existe una sesión activa, la cerramos (re-login)
        sessionRepository.findByUsuarioLdapAndEstado(username, "ACTIVA")
                .ifPresent(s -> {
                    s.setFechaFin(LocalDateTime.now());
                    s.setEstado("CERRADA");
                    sessionRepository.save(s);
                });

        // Validar LDAP
        boolean valid = ldapService.authenticate(username, password);
        if (!valid) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // Crear sesión
        Session session = new Session();
        session.setUsuarioLdap(username);
        session.setFechaInicio(LocalDateTime.now());
        session.setEstado("ACTIVA");
        session.setIpOrigen(ip);

        sessionRepository.save(session);

        return "Login exitoso";
    }

    public String logout(String username) {
        Session session = sessionRepository
                .findByUsuarioLdapAndEstado(username, "ACTIVA")
                .orElseThrow(() -> new RuntimeException("No hay sesión activa"));

        session.setFechaFin(LocalDateTime.now());
        session.setEstado("CERRADA");

        sessionRepository.save(session);

        return "Logout exitoso";
    }
}

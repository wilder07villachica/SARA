package com.diplomado.ms_backend_api.service.diagnostico;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
@RequiredArgsConstructor
public class SessionValidator {

    private final RestClient rest;

    @Value("${msauth.base-url}")
    private String msAuthBaseUrl;

    public void requireActiveSession(String username) {
        try {
            SessionStatusResponse r = rest.get()
                    .uri(msAuthBaseUrl + "/auth/session?username={u}", username)
                    .retrieve()
                    .body(SessionStatusResponse.class);

            if (r == null || !r.activa) {
                throw new RuntimeException("Sesión no activa. Vuelva a autenticarse.");
            }
        } catch (RestClientException e) {
            throw new RuntimeException("No se pudo validar la sesión con ms-auth. Intente nuevamente.", e);
        }
    }

    public static class SessionStatusResponse {
        public String usuario;
        public boolean activa;
        public Long idSession;
    }
}
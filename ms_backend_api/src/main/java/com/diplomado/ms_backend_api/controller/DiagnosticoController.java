package com.diplomado.ms_backend_api.controller;

import com.diplomado.ms_backend_api.dto.response.DiagnosticoResponseDTO;
import com.diplomado.ms_backend_api.security.JwtService;
import com.diplomado.ms_backend_api.service.diagnostico.DiagnosticoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DiagnosticoController {

    private final DiagnosticoService diagnosticoService;
    private final JwtService jwtService;

    @PostMapping("/diagnostico")
    public DiagnosticoResponseDTO diagnostico(@RequestParam String numero,
                                              HttpServletRequest request) {

        if (!numero.matches("^\\d{9,15}$")) {
            throw new RuntimeException("Formato inválido (9-15 dígitos)");
        }

        String authHeader = request.getHeader("Authorization");
        String token = jwtService.resolveToken(authHeader);

        if (!jwtService.isTokenValid(token)) {
            throw new RuntimeException("Token inválido o expirado");
        }

        String usuario = jwtService.extractUsername(token);

        return diagnosticoService.diagnosticar(numero, usuario);
    }
}
package com.diplomado.ms_backend_api.controller;

import com.diplomado.ms_backend_api.dto.response.DiagnosticoResponseDTO;
import com.diplomado.ms_backend_api.service.diagnostico.DiagnosticoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DiagnosticoController {

    private final DiagnosticoService diagnosticoService;

    @PostMapping("/diagnostico")
    public DiagnosticoResponseDTO diagnostico(@RequestParam String numero,
                                              @RequestParam String usuario) {

        if (!numero.matches("^\\d{9,15}$")) {
            throw new RuntimeException("Formato inválido (9-15 dígitos)");
        }
        if (usuario == null || usuario.isBlank()) {
            throw new RuntimeException("Usuario requerido");
        }

        return diagnosticoService.diagnosticar(numero, usuario);
    }
}

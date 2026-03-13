package com.diplomado.ms_backend_api.controller;

import com.diplomado.ms_backend_api.dto.LineaDashboardDTO;
import com.diplomado.ms_backend_api.service.ConsultaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ConsultaController {

    private final ConsultaService service;

    @PostMapping("/consultar")
    public LineaDashboardDTO consultar(@RequestParam String numero) {
        if (!numero.matches("^\\d{9,15}$")) {
            throw new RuntimeException("Formato inválido (9-15 dígitos)");
        }
        return service.consultar(numero);
    }
}

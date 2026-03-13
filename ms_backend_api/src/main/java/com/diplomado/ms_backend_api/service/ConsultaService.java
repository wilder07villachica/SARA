package com.diplomado.ms_backend_api.service;

import com.diplomado.ms_backend_api.dto.LineaDashboardDTO;
import com.diplomado.ms_backend_api.repository.LineaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final LineaRepository repo;

    public LineaDashboardDTO consultar(String numero) {
        var p = repo.consultarDashboard(numero)
                .orElseThrow(() -> new RuntimeException("Número no encontrado en simulación"));

        return LineaDashboardDTO.builder()
                .numeroTelefono(p.getNumeroTelefono())

                .imei(p.getImei())
                .marca(p.getMarca())
                .modelo(p.getModelo())
                .estadoImei(p.getEstadoImei())
                .condicionImei(p.getCondicionImei())

                .perfilSuscriptor(p.getPerfilSuscriptor())
                .tecnologiaActiva(p.getTecnologiaActiva())
                .planComercial(p.getPlanComercial())

                .vozEstado(p.getVozEstado())
                .vozVlr(p.getVozVlr())
                .vozPaisOperador(p.getVozPaisOperador())
                .vozRedRegistrada(p.getVozRedRegistrada())
                .vozDisponibilidad(p.getVozDisponibilidad())
                .vozUltimaCelda(p.getVozUltimaCelda())
                .vozAccesible(p.getVozAccesible())
                .vozFechaRegistro(p.getVozFechaRegistro())

                .datosEstadoRed(p.getDatosEstadoRed())

                .datosUltimoNe(p.getDatosUltimoNe())
                .datosPaisOperador(p.getDatosPaisOperador())
                .datosUltimaRed(p.getDatosUltimaRed())
                .datosUltimaCelda(p.getDatosUltimaCelda())
                .datosUltimoRegistro(p.getDatosUltimoRegistro())
                .apnUso(p.getApnUso())
                .ipApn(p.getIpApn())
                .build();
    }
}

DROP TABLE IF EXISTS linea_diagnostico_consolidado;

CREATE TABLE linea_diagnostico_consolidado (
    numeroTelefono VARCHAR(15) PRIMARY KEY,

    -- BSCS
    bscs_perfilPlanComercial VARCHAR(100),
    bscs_imei VARCHAR(20),
    bscs_marca VARCHAR(50),
    bscs_modelo VARCHAR(50),
    bscs_serieOriginal VARCHAR(50),

    -- HSS
    hss_estadoRedVoz VARCHAR(50),
    hss_vlrRegistrada VARCHAR(50),
    hss_paisOperador VARCHAR(50),
    hss_redRegistrada VARCHAR(20),
    hss_disponibilidad VARCHAR(100),
    hss_ultimaCelda VARCHAR(100),
    hss_accesible VARCHAR(50),
    hss_fechaRegistro TIMESTAMP,
    hss_rnc VARCHAR(50),

    -- REDVIVA DATOS
    redviva_estadoRedDatos VARCHAR(100),
    redviva_ultimoNERegistrado VARCHAR(100),
    redviva_paisOperadorDatos VARCHAR(50),
    redviva_ultimaRedRegistrada VARCHAR(20),
    redviva_ultimaCeldaDatos VARCHAR(150),
    redviva_ultimoRegistroConexion TIMESTAMP,
    redviva_apnUso VARCHAR(100),
    redviva_ipApn VARCHAR(50),

    -- VOLTE
    redviva_condicionVolte VARCHAR(50),
    redviva_anclajeVolte VARCHAR(50),
    redviva_enrutamientoVolte VARCHAR(50),
    redviva_volte VARCHAR(50),
    redviva_roamingVolte4G VARCHAR(50),

    -- PCRF
    pcrf_estadoIMEI VARCHAR(50),
    pcrf_condicionIMEI VARCHAR(50),
    pcrf_tecnologiaActiva VARCHAR(20),
    pcrf_planComercial VARCHAR(100),
    pcrf_estadoServicios VARCHAR(50),
    pcrf_vozWifi VARCHAR(50),
    pcrf_servicioDatos VARCHAR(100),
    pcrf_serviciosBasicos VARCHAR(50),
    pcrf_saldo VARCHAR(50),
    pcrf_serviciosSuplementarios VARCHAR(50),
    pcrf_serviciosAdicionales VARCHAR(50),
    pcrf_apnAprovisionados VARCHAR(50),
    pcrf_portabilidad VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS consulta_auditoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(20) NOT NULL,
    usuario VARCHAR(80) NOT NULL,
    fecha_hora TIMESTAMP NOT NULL,
    prioridad VARCHAR(10),
    recomendacion VARCHAR(255),
    alerta_masiva BOOLEAN DEFAULT FALSE,
    criterio_masivo VARCHAR(60),
    resultado VARCHAR(20)
    );

CREATE INDEX IF NOT EXISTS idx_aud_num_fecha
    ON consulta_auditoria(numero, fecha_hora);

CREATE INDEX IF NOT EXISTS idx_aud_criterio_fecha
    ON consulta_auditoria(criterio_masivo, fecha_hora);
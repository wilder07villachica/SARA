const el = (id) => document.getElementById(id);

function getSession() {
  return {
    username: localStorage.getItem("ms_auth_username") || "",
    active: localStorage.getItem("ms_auth_active") === "true",
    token: localStorage.getItem("ms_auth_token") || "",
    role: localStorage.getItem("ms_auth_role") || ""
  };
}

function setStatus(msg) {
  el("dashStatus").textContent = msg;
}

function requireSessionOrRedirect() {
  const { username, active } = getSession();
  if (!active || !username) {
    window.location.href = "index.html";
    return;
  }
  el("userLine").textContent = `Usuario: ${username}`;
}

function setText(id, value) {
  const node = el(id);
  const v = (value === null || value === undefined || value === "") ? "—" : String(value);
  node.textContent = v;
  node.title = v;
}

async function logoutBackend(token) {
  const res = await fetch(`${AUTH_BASE}/auth/logout`, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${token}`
    }
  });

  const text = await res.text();
  if (!res.ok) throw new Error(text || `HTTP ${res.status}`);
  return text;
}

el("btnCerrar").addEventListener("click", async () => {
  const { token } = getSession();
  setStatus("Cerrando sesión...");

  try {
    await logoutBackend(token);
  } catch (e) {
    // no bloquea limpieza local
  }

  localStorage.removeItem("ms_auth_active");
  localStorage.removeItem("ms_auth_username");
  localStorage.removeItem("ms_auth_token");
  localStorage.removeItem("ms_auth_role");

  window.location.href = "index.html";
});

async function consultarDiagnostico(numero, token) {
  const url = `${API_BASE}/api/diagnostico?numero=${encodeURIComponent(numero)}`;

  const res = await fetch(url, {
    method: "POST",
    headers: {
      "Accept": "application/json",
      "Authorization": `Bearer ${token}`
    }
  });

  if (!res.ok) {
    const txt = await res.text();
    throw new Error(txt || `HTTP ${res.status}`);
  }
  return res.json();
}

el("btnBuscar").addEventListener("click", async () => {
  const msisdn = el("msisdn").value.trim();
  if (!msisdn) return setStatus("Ingresa un número para buscar.");

  const { token } = getSession();

  setStatus("Consultando diagnóstico...");

  try {
    const r = await consultarDiagnostico(msisdn, token);
    const d = r.linea;

    setText("eq_num", d.numeroTelefono);
    setText("ln_num", d.numeroTelefono);

    setText("eq_imei", d.imei);
    setText("eq_estado_imei", d.estadoImei);
    setText("eq_marca", d.marca);
    setText("eq_modelo", d.modelo);
    setText("eq_condicion", d.condicionImei);

    setText("eq_imsi", "—");
    setText("ln_imsi", "—");

    setText("ln_perfil", d.perfilSuscriptor);
    setText("ln_tech", d.tecnologiaActiva);
    setText("ln_plan", d.planComercial);

    setText("voz_estado", d.vozEstado);
    setText("voz_vrl", d.vozVlr);
    setText("voz_pais", d.vozPaisOperador);
    setText("voz_red", d.vozRedRegistrada);
    setText("voz_disp", d.vozDisponibilidad);
    setText("voz_celda", d.vozUltimaCelda);
    setText("voz_access", d.vozAccesible);

    setText("dat_estado", d.datosEstadoRed);
    setText("dat_ne", d.datosUltimoNe);
    setText("dat_pais", d.datosPaisOperador);
    setText("dat_red", d.datosUltimaRed);
    setText("dat_celda", d.datosUltimaCelda);
    setText("dat_ult", d.datosUltimoRegistro);

    setStatus(`Diagnóstico OK: ${msisdn}`);
  } catch (e) {
    setStatus(`Error: ${e.message}`);
  }
});

requireSessionOrRedirect();
setStatus("Listo. Ingresa un número y presiona Buscar.");
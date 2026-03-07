const AUTH_BASE = "http://localhost:8080"; // ms_auth
const API_BASE  = "http://localhost:8081"; // ms_backend_api
const el = (id) => document.getElementById(id);

function getSession() {
  const username = localStorage.getItem("ms_auth_username") || "";
  const active = localStorage.getItem("ms_auth_active") === "true";
  return { username, active };
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

/** Set text AND tooltip (title) so you can see full value on hover */
function setText(id, value) {
  const node = el(id);
  const v = (value === null || value === undefined || value === "") ? "—" : String(value);
  node.textContent = v;
  node.title = v; // tooltip
}

async function logoutBackend(username) {
  const body = new URLSearchParams({ username }).toString();

  const res = await fetch(`${AUTH_BASE}/auth/logout`, {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
    body,
  });

  const text = await res.text();
  if (!res.ok) throw new Error(text || `HTTP ${res.status}`);
  return text;
}

el("btnCerrar").addEventListener("click", async () => {
  const username = localStorage.getItem("ms_auth_username") || "";
  setStatus("Cerrando sesión...");

  try {
    await logoutBackend(username);
  } catch (e) {
    // no bloquea la limpieza local
  }

  localStorage.removeItem("ms_auth_active");
  window.location.href = "index.html";
});

// Consumo de búsqueda
async function consultarNumero(numero) {
  const res = await fetch(`${API_BASE}/api/consultar?numero=${encodeURIComponent(numero)}`, {
    method: "POST",
    headers: { "Accept": "application/json" }
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

  setStatus("Consultando...");

  try {
    const d = await consultarNumero(msisdn);

    // Número
    setText("eq_num", d.numeroTelefono);
    setText("ln_num", d.numeroTelefono);

    // Equipo
    setText("eq_imei", d.imei);
    setText("eq_estado_imei", d.estadoImei);
    setText("eq_marca", d.marca);
    setText("eq_modelo", d.modelo);
    setText("eq_condicion", d.condicionImei);

    // IMSI (tu tabla no lo tiene todavía)
    setText("eq_imsi", "—");
    setText("ln_imsi", "—");

    // Línea
    setText("ln_perfil", d.perfilSuscriptor);
    setText("ln_tech", d.tecnologiaActiva);
    setText("ln_plan", d.planComercial);

    // Voz
    setText("voz_estado", d.vozEstado);
    setText("voz_vrl", d.vozVlr);
    setText("voz_pais", d.vozPaisOperador);
    setText("voz_red", d.vozRedRegistrada);
    setText("voz_disp", d.vozDisponibilidad);
    setText("voz_celda", d.vozUltimaCelda);
    setText("voz_access", d.vozAccesible);

    // Datos
    setText("dat_ne", d.datosUltimoNe);
    setText("dat_pais", d.datosPaisOperador);
    setText("dat_red", d.datosUltimaRed);
    setText("dat_celda", d.datosUltimaCelda);
    setText("dat_ult", d.datosUltimoRegistro);

    setStatus(`Consulta OK: ${msisdn}`);
  } catch (e) {
    setStatus(`Error: ${e.message}`);
  }
});

requireSessionOrRedirect();
setStatus("Listo. Ingresa un número y presiona Buscar.");
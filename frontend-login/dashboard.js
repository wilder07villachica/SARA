// frontend-login/dashboard.js
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
  if (!node) return;
  const v = (value === null || value === undefined || value === "") ? "—" : String(value);
  node.textContent = v;
  node.title = v;
}

/* ===== Modal Recomendación ===== */
function openRecoModal(title, detail) {
  el("recoTitle").textContent = title || "Recomendación";
  el("recoDetail").textContent = detail || "—";
  el("modalReco").classList.remove("hidden");
}
function closeRecoModal() {
  el("modalReco").classList.add("hidden");
}
el("btnCloseReco").addEventListener("click", closeRecoModal);
el("btnOkReco").addEventListener("click", closeRecoModal);
el("modalReco").addEventListener("click", (e) => {
  if (e.target.id === "modalReco") closeRecoModal();
});

/* ===== Toasts ===== */
function showToast(kind, title, msg, ms = 4500) {
  const host = el("toastHost");
  const div = document.createElement("div");
  div.className = `toast ${kind}`;
  div.innerHTML = `<p class="t-title">${title}</p><p class="t-msg">${msg}</p>`;
  host.appendChild(div);

  setTimeout(() => {
    div.style.opacity = "0";
    div.style.transform = "translateY(-6px)";
    div.style.transition = "all .25s ease";
    setTimeout(() => div.remove(), 260);
  }, ms);
}

/* ===== Historial ===== */
function fmtDate(iso) {
  if (!iso) return "—";
  const d = new Date(iso);
  if (isNaN(d.getTime())) return String(iso);
  return d.toLocaleString();
}
function badgeForPriority(p) {
  const val = (p || "").toUpperCase();
  const cls = val === "P1" ? "p1" : val === "P2" ? "p2" : val === "P3" ? "p3" : "na";
  return `<span class="badge ${cls}">${val || "—"}</span>`;
}
function renderHistorial(items) {
  const body = el("histBody");
  if (!body) return;

  if (!items || items.length === 0) {
    body.innerHTML = `<tr><td colspan="4" class="muted">Sin historial</td></tr>`;
    return;
  }
  body.innerHTML = items.map(it => `
    <tr>
      <td>${fmtDate(it.fechaHora)}</td>
      <td>${badgeForPriority(it.prioridad)}</td>
      <td title="${(it.recomendacion || "—").replaceAll('"', "'")}">${it.recomendacion || "—"}</td>
      <td>${it.resultado || "—"}</td>
    </tr>
  `).join("");
}

/* ===== Indicadores (dots de color) ===== */
function setDot(dotId, state) {
  const dot = el(dotId);
  if (!dot) return;

  dot.classList.remove("ok", "warn", "off", "na");

  // state: OK / NO_REGISTRADO / NO_DISPONIBLE
  if (state === "OK") dot.classList.add("ok");
  else if (state === "NO_REGISTRADO") dot.classList.add("off");
  else if (state === "NO_DISPONIBLE") dot.classList.add("na");
  else dot.classList.add("na");
}

function setApnDot(apnValue) {
  // APN: si hay APN -> OK (verde), si no hay -> warn (amarillo)
  const has = !!(apnValue && String(apnValue).trim() && apnValue !== "—");
  const dot = el("indApnDot");
  if (!dot) return;

  dot.classList.remove("ok", "warn", "off", "na");
  dot.classList.add(has ? "ok" : "warn");
}

/* ===== Logout ===== */
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

/* ===== Endpoint consolidado ===== */
async function consultarDiagnostico(numero, usuario) {
  const url = `${API_BASE}/api/diagnostico?numero=${encodeURIComponent(numero)}&usuario=${encodeURIComponent(usuario)}`;

  const res = await fetch(url, {
    method: "POST",
    headers: { "Accept": "application/json" }
  });

  if (!res.ok) {
    const txt = await res.text();
    throw new Error(txt || `HTTP ${res.status}`);
  }
  return res.json();
}

/* ===== Buscar ===== */
el("btnBuscar").addEventListener("click", async () => {
  const msisdn = el("msisdn").value.trim();
  if (!msisdn) return setStatus("Ingresa un número para buscar.");

  const { username } = getSession();
  setStatus("Consultando diagnóstico...");

  try {
    const r = await consultarDiagnostico(msisdn, username);
    const d = r.linea;

    // Número
    setText("eq_num", d.numeroTelefono);
    setText("ln_num", d.numeroTelefono);

    // Equipo
    setText("eq_imei", d.imei);
    setText("eq_estado_imei", d.estadoImei);
    setText("eq_marca", d.marca);
    setText("eq_modelo", d.modelo);
    setText("eq_condicion", d.condicionImei);

    // IMSI (no está en tu tabla)
    setText("eq_imsi", "—");
    setText("ln_imsi", "—");

    // Línea
    setText("ln_perfil", d.perfilSuscriptor);
    setText("ln_tech", d.tecnologiaActiva);
    setText("ln_plan", d.planComercial);

    // Voz (texto)
    setText("voz_estado", d.vozEstado);
    setText("voz_vrl", d.vozVlr);
    setText("voz_pais", d.vozPaisOperador);
    setText("voz_red", d.vozRedRegistrada);
    setText("voz_disp", d.vozDisponibilidad);
    setText("voz_celda", d.vozUltimaCelda);
    setText("voz_access", d.vozAccesible);

    // Datos (texto)
    setText("dat_ne", d.datosUltimoNe);
    setText("dat_pais", d.datosPaisOperador);
    setText("dat_red", d.datosUltimaRed);
    setText("dat_celda", d.datosUltimaCelda);
    setText("dat_ult", d.datosUltimoRegistro);

    // ✅ NUEVO: Estado red datos y APN en uso (si existen los IDs en HTML)
    setText("dat_estado", d.datosEstadoRed);
    setText("dat_apn", d.apnUso);

    // Historial panel derecho
    renderHistorial(r.historial72h);

    // ✅ NUEVO: dots de color desde indicadores
    setDot("indVozDot", r.indicadores?.redVoz);
    setDot("indDatosDot", r.indicadores?.redDatos);
    setApnDot(d.apnUso);

    // Toast prioridad
    if (r.prioridad?.nivel) {
      const p = r.prioridad.nivel;
      const kind = p === "P2" ? "danger" : p === "P1" ? "warn" : "info";
      showToast(kind, `Prioridad ${p}`, `${r.prioridad.accion}. ${r.prioridad.motivo || ""}`.trim());
    }

    // Popup alerta masiva
    if (r.alertaMasiva?.activa) {
      showToast(
        "danger",
        "Posible falla masiva",
        `${r.alertaMasiva.casos} casos en las últimas ${r.alertaMasiva.ventanaHoras}h (${r.alertaMasiva.criterio}).`,
        6500
      );
    }

    // Modal recomendación
    if (r.recomendacion?.titulo || r.recomendacion?.detalle) {
      openRecoModal(r.recomendacion.titulo, r.recomendacion.detalle);
    }

    setStatus(`Diagnóstico OK: ${msisdn}`);
  } catch (e) {
    setStatus(`Error: ${e.message}`);
  }
});

requireSessionOrRedirect();
setStatus("Listo. Ingresa un número y presiona Buscar.");
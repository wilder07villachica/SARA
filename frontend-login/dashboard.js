const el = (id) => document.getElementById(id);

function getSession() {
  return {
    username: localStorage.getItem("ms_auth_username") || "",
    active: localStorage.getItem("ms_auth_active") === "true",
    token: localStorage.getItem("ms_auth_token") || "",
    role: localStorage.getItem("ms_auth_role") || ""
  };
}

function requireSessionOrRedirect() {
  const { username, active, role } = getSession();
  if (!active || !username) {
    window.location.href = "index.html";
    return;
  }
  el("userLine").textContent = role ? `${username} · ${role}` : username;
}

function setText(id, value) {
  const node = el(id);
  if (!node) return;
  const v = (value === null || value === undefined || value === "") ? "—" : String(value);
  node.textContent = v;
  node.title = v;
}

/* ===== Modal recomendación ===== */
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
function showToast(kind, title, msg, ms = 4200) {
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

/* ===== Indicadores por color ===== */
function setDot(dotId, state) {
  const dot = el(dotId);
  if (!dot) return;

  dot.classList.remove("ok", "warn", "off", "na");

  if (state === "OK") dot.classList.add("ok");
  else if (state === "NO_REGISTRADO") dot.classList.add("off");
  else if (state === "NO_DISPONIBLE") dot.classList.add("na");
  else dot.classList.add("na");
}

function setApnDot(apnValue) {
  const has = !!(apnValue && String(apnValue).trim() && apnValue !== "—");
  const dot = el("indApnDot");
  if (!dot) return;

  dot.classList.remove("ok", "warn", "off", "na");
  dot.classList.add(has ? "ok" : "warn");
}

function setSimpleDot(dotId, value) {
  const dot = el(dotId);
  if (!dot) return;

  dot.classList.remove("ok", "warn", "off", "na");

  if (!value || value === "—") {
    dot.classList.add("na");
    return;
  }

  const v = String(value).toUpperCase();

  if (v.includes("OK") || v.includes("ACTIVO") || v.includes("CONFIGURADO") || v.includes("NATIVO")) {
    dot.classList.add("ok");
  } else if (v.includes("NO") || v.includes("BLOQUEADO") || v.includes("INACTIVO") || v.includes("RESTRINGIDO")) {
    dot.classList.add("off");
  } else {
    dot.classList.add("warn");
  }
}

/* ===== Logout ===== */
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

/* ===== Endpoint consolidado ===== */
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

/* ===== Buscar ===== */
el("btnBuscar").addEventListener("click", async () => {
  const msisdn = el("msisdn").value.trim();
  if (!msisdn) {
    showToast("warn", "Número requerido", "Ingrese un número para realizar la consulta.");
    return;
  }

  const { token } = getSession();

  try {
    const r = await consultarDiagnostico(msisdn, token);
    const d = r.linea || {};

    /* Equipo */
    setText("eq_num", d.numeroTelefono);
    setText("eq_imsi", "—");
    setText("eq_imei", d.imei);
    setText("eq_estado_imei", d.estadoImei);
    setText("eq_marca", d.marca);
    setText("eq_modelo", d.modelo);
    setText("eq_condicion", d.condicionImei);

    /* Aprovisionamiento */
    setText("ln_num", d.numeroTelefono);
    setText("ln_imsi", "—");
    setText("ln_perfil", d.perfilSuscriptor);
    setText("ln_tech", d.tecnologiaActiva);
    setText("ln_plan", d.planComercial);

    /* Campos opcionales simplificados */
    setText("ln_serie", "—");
    setText("ln_portabilidad", "—");
    setText("ln_volte_cond", "—");

    setSimpleDot("lnSerieDot", "OK");
    setSimpleDot("lnPortaDot", "—");
    setSimpleDot("lnVolteDot", "—");

    /* Voz */
    setText("voz_estado", d.vozEstado);
    setText("voz_vrl", d.vozVlr);
    setText("voz_pais", d.vozPaisOperador);
    setText("voz_red", d.vozRedRegistrada);
    setText("voz_disp", d.vozDisponibilidad);
    setText("voz_celda", d.vozUltimaCelda);
    setText("voz_access", d.vozAccesible);

    /* Datos */
    setText("dat_estado", d.datosEstadoRed);
    setText("dat_ne", d.datosUltimoNe);
    setText("dat_pais", d.datosPaisOperador);
    setText("dat_red", d.datosUltimaRed);
    setText("dat_celda", d.datosUltimaCelda);
    setText("dat_ult", d.datosUltimoRegistro);
    setText("dat_apn", d.apnUso);

    /* Historial */
    renderHistorial(r.historial72h);

    /* Dots */
    setDot("indVozDot", r.indicadores?.redVoz);
    setDot("indDatosDot", r.indicadores?.redDatos);
    setApnDot(d.apnUso);

    /* Toast prioridad */
    if (r.prioridad?.nivel) {
      const p = r.prioridad.nivel;
      const kind = p === "P2" ? "danger" : p === "P1" ? "warn" : "info";
      showToast(kind, `Prioridad ${p}`, `${r.prioridad.accion}. ${r.prioridad.motivo || ""}`.trim());
    }

    /* Alerta masiva */
    if (r.alertaMasiva?.activa) {
      showToast(
        "danger",
        "Posible falla masiva",
        `${r.alertaMasiva.casos} casos en las últimas ${r.alertaMasiva.ventanaHoras} horas.`,
        6200
      );
    }

    /* Recomendación */
    if (r.recomendacion?.titulo || r.recomendacion?.detalle) {
      openRecoModal(r.recomendacion.titulo, r.recomendacion.detalle);
    }

  } catch (e) {
    showToast("danger", "Consulta no completada", "No se pudo obtener el diagnóstico. Inténtelo nuevamente.", 5200);
  }
});

requireSessionOrRedirect();
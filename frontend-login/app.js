localStorage.setItem("ms_auth_active", "false");

const el = (id) => document.getElementById(id);

const loginForm = el("loginForm");
const usernameInput = el("username");
const passwordInput = el("password");

const btnLogin = el("btnLogin");
const btnLogout = el("btnLogout");
const btnAutofill = el("btnAutofill");

const statusBox = el("status");
const apiBaseText = el("apiBaseText");
if (apiBaseText) apiBaseText.textContent = AUTH_BASE;

function setStatus(msg) {
  statusBox.textContent = msg;
}

function saveSession(username, active, token, role) {
  localStorage.setItem("ms_auth_username", username || "");
  localStorage.setItem("ms_auth_active", active ? "true" : "false");
  localStorage.setItem("ms_auth_token", token || "");
  localStorage.setItem("ms_auth_role", role || "");
}

function restoreUI() {
  const u = localStorage.getItem("ms_auth_username") || "";
  if (u) usernameInput.value = u;

  btnLogin.disabled = false;
  usernameInput.disabled = false;
  passwordInput.disabled = false;
  if (btnLogout) btnLogout.disabled = true;

  setStatus("Ingresa tus credenciales.");
}

async function postLogin(url, dataObj) {
  const body = new URLSearchParams(dataObj).toString();

  const res = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
    body,
  });

  const data = await res.json().catch(() => null);
  if (!res.ok) throw new Error(data?.message || `HTTP ${res.status}`);
  return data;
}

loginForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  const username = usernameInput.value.trim();
  const password = passwordInput.value;

  if (!username || !password) {
    setStatus("Completa usuario y contraseña.");
    return;
  }

  setStatus("Autenticando...");
  btnLogin.disabled = true;

  try {
    const data = await postLogin(`${AUTH_BASE}/auth/login`, { username, password });

    saveSession(data.username, true, data.token, data.role);
    setStatus(data.message || "Login exitoso");
    window.location.href = "dashboard.html";
  } catch (err) {
    saveSession(username, false, "", "");
    btnLogin.disabled = false;
    setStatus(err.message || "Error en login");
  }
});

btnAutofill?.addEventListener("click", () => {
  usernameInput.value = "admin.callcenter";
  passwordInput.value = "Admin2026*";
  setStatus("Credenciales demo cargadas.");
});

restoreUI();
// al cargar login, limpia bandera de sesión activa del navegador
localStorage.setItem("ms_auth_active", "false");

const API_BASE = "http://localhost:8080"; // puerto/host backend

const el = (id) => document.getElementById(id);

const loginForm = el("loginForm");
const usernameInput = el("username");
const passwordInput = el("password");

const btnLogin = el("btnLogin");
const btnLogout = el("btnLogout");
const btnAutofill = el("btnAutofill");

const statusBox = el("status");
const apiBaseText = el("apiBaseText");
if (apiBaseText) apiBaseText.textContent = API_BASE;

function setStatus(msg) {
  statusBox.textContent = msg;
}

function setLoggedInUI(isLoggedIn) {
  btnLogout.disabled = !isLoggedIn;
  btnLogin.disabled = isLoggedIn;
  usernameInput.disabled = isLoggedIn;
  passwordInput.disabled = isLoggedIn;
}

async function postForm(url, dataObj) {
  const body = new URLSearchParams(dataObj).toString();

  const res = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
    body,
  });

  const text = await res.text();
  if (!res.ok) throw new Error(text || `HTTP ${res.status}`);
  return text;
}

function saveSession(username, active) {
  localStorage.setItem("ms_auth_username", username || "");
  localStorage.setItem("ms_auth_active", active ? "true" : "false");
}

function loadSession() {
  const u = localStorage.getItem("ms_auth_username") || "";
  const active = localStorage.getItem("ms_auth_active") === "true";
  return { u, active };
}

function restoreUI() {
  const u = localStorage.getItem("ms_auth_username") || "";
  if (u) usernameInput.value = u;

  // En la pantalla de login SIEMPRE permitir intentar iniciar sesión
  btnLogin.disabled = false;
  usernameInput.disabled = false;
  passwordInput.disabled = false;

  // opcional: no usar logout en esta pantalla
  if (btnLogout) btnLogout.disabled = true;

  setStatus("Ingresa tus credenciales.");
}

// LOGIN -> redirige a dashboard.html
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
    const msg = await postForm(`${API_BASE}/auth/login`, { username, password });
    saveSession(username, true);
    setStatus(msg || "Login exitoso");

    // Redirección
    window.location.href = "dashboard.html";
  } catch (err) {
    saveSession(username, false);
    setLoggedInUI(false);
    btnLogin.disabled = false;
    setStatus(err.message || "Error en login");
  }
});

// LOGOUT (en login)
btnLogout?.addEventListener("click", async () => {
  const username = usernameInput.value.trim();
  if (!username) return;

  setStatus("Cerrando sesión...");
  btnLogout.disabled = true;

  try {
    const msg = await postForm(`${API_BASE}/auth/logout`, { username });
    saveSession(username, false);
    setLoggedInUI(false);
    passwordInput.value = "";
    setStatus(msg || "Logout exitoso");
  } catch (err) {
    btnLogout.disabled = false;
    setStatus(err.message || "Error en logout");
  }
});

btnAutofill?.addEventListener("click", () => {
  usernameInput.value = "testuser";
  passwordInput.value = "test123";
  setStatus("Credenciales de prueba cargadas.");
});

restoreUI();
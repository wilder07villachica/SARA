localStorage.setItem("ms_auth_active", "false");

const el = (id) => document.getElementById(id);

const loginForm = el("loginForm");
const usernameInput = el("username");
const passwordInput = el("password");
const btnLogin = el("btnLogin");

const feedbackModal = el("feedbackModal");
const modalTitle = el("modalTitle");
const modalMessage = el("modalMessage");
const modalBtn = el("modalBtn");
const modalIcon = el("modalIcon");

function saveSession(username, active, token, role) {
  localStorage.setItem("ms_auth_username", username || "");
  localStorage.setItem("ms_auth_active", active ? "true" : "false");
  localStorage.setItem("ms_auth_token", token || "");
  localStorage.setItem("ms_auth_role", role || "");
}

function restoreUI() {
  btnLogin.disabled = false;
  usernameInput.disabled = false;
  passwordInput.disabled = false;
}

async function postLogin(url, dataObj) {
  const body = new URLSearchParams(dataObj).toString();

  const res = await fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
    body,
  });

  const data = await res.json().catch(() => null);

  if (!res.ok) {
    throw new Error(data?.message || "No se pudo iniciar sesión");
  }

  return data;
}

function openModal({ title, message, success }) {
  modalTitle.textContent = title;
  modalMessage.textContent = message;
  modalIcon.textContent = success ? "✓" : "!";
  modalIcon.className = success ? "modal-icon success" : "modal-icon error";
  feedbackModal.classList.remove("hidden");
}

function closeModal() {
  feedbackModal.classList.add("hidden");
}

modalBtn.addEventListener("click", () => {
  closeModal();
});

feedbackModal.addEventListener("click", (e) => {
  if (e.target.id === "feedbackModal") {
    closeModal();
  }
});

loginForm.addEventListener("submit", async (e) => {
  e.preventDefault();

  const username = usernameInput.value.trim();
  const password = passwordInput.value;

  if (!username || !password) {
    openModal({
      title: "Campos incompletos",
      message: "Ingrese su usuario y contraseña para continuar.",
      success: false
    });
    return;
  }

  btnLogin.disabled = true;

  try {
    const data = await postLogin(`${AUTH_BASE}/auth/login`, { username, password });

    saveSession(data.username, true, data.token, data.role);

    openModal({
      title: "Registro exitoso",
      message: "Inicio de sesión correcto. Redirigiendo a la pantalla principal...",
      success: true
    });

    setTimeout(() => {
      window.location.href = "dashboard.html";
    }, 1400);

  } catch (err) {
    saveSession(username, false, "", "");

    openModal({
      title: "Inicio no completado",
      message: "No se pudo iniciar sesión. Verifique sus credenciales e inténtelo nuevamente.",
      success: false
    });

    btnLogin.disabled = false;
  }
});

restoreUI();
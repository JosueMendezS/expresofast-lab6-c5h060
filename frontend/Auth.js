
const API_BASE_URL = 'http://localhost:8080';

const CLAVE_TOKEN = 'jwt_token';

function obtenerToken() {
    return sessionStorage.getItem(CLAVE_TOKEN);
}

// Decodifica el "payload" (segunda parte) de un JWT en base64url.
// No valida la firma: solo se usa para leer datos en el cliente (usuario y roles).
function decodificarJWT(token) {
    try {
        const payloadBase64 = token.split('.')[1];
        const payloadJson = atob(payloadBase64.replace(/-/g, '+').replace(/_/g, '/'));
        return JSON.parse(payloadJson);
    } catch (error) {
        return null;
    }
}

function obtenerUsername() {
    const token = obtenerToken();
    if (!token) return '';
    const payload = decodificarJWT(token);
    return (payload && payload.sub) || '';
}

function obtenerRoles() {
    const token = obtenerToken();
    if (!token) return [];
    const payload = decodificarJWT(token);
    return (payload && payload.roles) || [];
}

function tieneRol(rol) {
    return obtenerRoles().includes(rol);
}

function esAdmin() {
    return tieneRol('ROLE_ADMIN');
}

function esOperador() {
    return tieneRol('ROLE_OPERADOR');
}

function esConductor() {
    return tieneRol('ROLE_CONDUCTOR');
}

function guardarSesion(authResponseDTO) {
    sessionStorage.setItem(CLAVE_TOKEN, authResponseDTO.token);
}

function limpiarSesion() {
    sessionStorage.removeItem(CLAVE_TOKEN);
}

function cerrarSesion() {
    limpiarSesion();
    window.location.href = 'index.html';
}

function exigirSesion() {
    if (!obtenerToken()) {
        window.location.href = 'index.html';
    }
}

async function fetchWithAuth(url, options = {}) {
    const headers = {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${obtenerToken()}`,
        ...(options.headers || {}),
    };

    const respuesta = await fetch(url, { ...options, headers });

    if (respuesta.status === 401 || respuesta.status === 403) {
        limpiarSesion();
        window.location.href = 'index.html';
        throw new Error('Sesión expirada o sin permisos. Redirigiendo al login…');
    }

    if (!respuesta.ok) {
        const cuerpo = await respuesta.json().catch(() => null);
        let mensaje = (cuerpo && cuerpo.message) || `El servidor respondió con estado ${respuesta.status}`;

        if (cuerpo && Array.isArray(cuerpo.errores) && cuerpo.errores.length > 0) {
            const detalle = cuerpo.errores.map((e) => `${e.campo}: ${e.mensaje}`).join(' · ');
            mensaje = `${mensaje} (${detalle})`;
        }

        throw new Error(mensaje);
    }

    return respuesta;
}

exigirSesion();

let paginaActual = 0;

const inputBusqueda = document.getElementById('input-busqueda');
const selectEstado = document.getElementById('select-estado');
const selectTamano = document.getElementById('select-tamano');
const tbodyEnvios = document.getElementById('tbody-envios');
const textoPagina = document.getElementById('texto-pagina');
const btnPrimera = document.getElementById('btn-primera');
const btnAnterior = document.getElementById('btn-anterior');
const btnSiguiente = document.getElementById('btn-siguiente');
const btnUltima = document.getElementById('btn-ultima');

async function cargarPagina() {
    const size = selectTamano.value;
    const busqueda = inputBusqueda.value.trim();
    const estado = selectEstado.value;

    const params = new URLSearchParams({ page: paginaActual, size });
    if (busqueda) params.append('busqueda', busqueda);
    if (estado) params.append('estado', estado);

    try {
        const respuesta = await fetchWithAuth(`${API_BASE_URL}/api/v1/envios?${params.toString()}`);
        const data = await respuesta.json();
        renderizarTabla(data.content);
        actualizarPaginador(data);
    } catch (error) {
        alert(error.message);
    }
}

function renderizarTabla(envios) {
    tbodyEnvios.innerHTML = '';
    envios.forEach((envio) => {
        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td>${envio.codigoRastreo}</td>
            <td>${envio.direccionDestino}</td>
            <td>${envio.montoFlete}</td>
            <td>${envio.estado}</td>
        `;
        tbodyEnvios.appendChild(fila);
    });
}

function actualizarPaginador(data) {

    const paginaMostrada = data.number + 1;
    const totalPaginas = data.totalPages === 0 ? 1 : data.totalPages;
    textoPagina.textContent = `Página ${paginaMostrada} de ${totalPaginas} (Total: ${data.totalElements} envíos)`;

    btnPrimera.disabled = data.first;
    btnAnterior.disabled = data.first;
    btnSiguiente.disabled = data.last;
    btnUltima.disabled = data.last;

    paginaActual = data.number;
}

async function consultarViaStoredProcedure() {
    const estado = selectEstado.value;
    if (!estado) {
        alert('Seleccione un estado para consultar el Stored Procedure.');
        return;
    }

    try {
        const respuesta = await fetchWithAuth(`${API_BASE_URL}/api/v1/envios/procedimiento/${estado}`);
        const envios = await respuesta.json();
        renderizarTabla(envios);

        textoPagina.textContent = `Resultado vía Stored Procedure (Total: ${envios.length} envíos, sin paginación)`;
        btnPrimera.disabled = true;
        btnAnterior.disabled = true;
        btnSiguiente.disabled = true;
        btnUltima.disabled = true;
    } catch (error) {
        alert(error.message);
    }
}

document.getElementById('form-filtros').addEventListener('submit', (evento) => {
    evento.preventDefault();
    paginaActual = 0;
    cargarPagina();
});

document.getElementById('btn-sp').addEventListener('click', consultarViaStoredProcedure);

btnPrimera.addEventListener('click', () => { paginaActual = 0; cargarPagina(); });
btnAnterior.addEventListener('click', () => { paginaActual -= 1; cargarPagina(); });
btnSiguiente.addEventListener('click', () => { paginaActual += 1; cargarPagina(); });
btnUltima.addEventListener('click', async () => {
    const params = new URLSearchParams({ page: 0, size: selectTamano.value });
    const respuesta = await fetchWithAuth(`${API_BASE_URL}/api/v1/envios?${params.toString()}`);
    const data = await respuesta.json();
    paginaActual = data.totalPages - 1;
    cargarPagina();
});

cargarPagina();
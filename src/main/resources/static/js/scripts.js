let filtroEstadoActual = '';

function filtrarTabla() {
    const texto = document.getElementById('buscador').value;
    const estado = document.getElementById('filtroEstado').value;

    filtrarPorEstadoServidor(estado, 0, texto);
}

function filtrarDesdeSelect(estado) {
    const texto = document.getElementById('buscador').value;
    filtrarPorEstadoServidor(estado, 0, texto);
}

function filtrarPorEstadoServidor(estado, pagina, buscar = '') {
	fetch(`/ingresos/lista/json?page=${pagina}&estado=${estado}&buscar=${encodeURIComponent(buscar)}`)
	.then(res => res.json())
	.then(data => {
        const tbody = document.querySelector('#tablaIngresos tbody');
        tbody.innerHTML = '';

        const offset = pagina * 10;

        if (!data.ingresos || data.ingresos.length === 0) {
            tbody.innerHTML = `<tr>
                <td colspan="11" class="text-center text-muted py-4">
                    No hay registros con ese estado
                </td></tr>`;
            actualizarPaginacion(pagina, 0, 0, 0);
            actualizarBotonesPaginacion(0, 0);
            return;
        }

        data.ingresos.forEach((i, idx) => {
            const estadoBadge = i.estado === 1
                ? '<span class="badge bg-success">Activo</span>'
                : '<span class="badge bg-secondary">Suspendido</span>';

            const btnEstado = i.estado === 1
                ? `<a href="javascript:void(0)"
                      data-url="/ingresos/estado/${i.idIngreso}"
                      data-estado="${i.estado}"
                      class="btn btn-danger btn-sm"
                      onclick="confirmarEstado(this.dataset.url, this.dataset.estado)">
                      <i class="bi bi-pause-circle"></i></a>`
                : `<a href="javascript:void(0)"
                      data-url="/ingresos/estado/${i.idIngreso}"
                      data-estado="${i.estado}"
                      class="btn btn-success btn-sm"
                      onclick="confirmarEstado(this.dataset.url, this.dataset.estado)">
                      <i class="bi bi-play-circle"></i></a>`;

            const proveedor = i.proveedor ? i.proveedor.nombre : '-';
            const factura   = i.nroFactura || '-';
            const fecha     = i.fecha
                ? new Date(i.fecha).toLocaleDateString('es-PE') : '-';

            tbody.innerHTML += `
            <tr class="${i.estado === 2 ? 'table-secondary' : ''}">
                <td>${offset + idx + 1}</td>
                <td>
                    <span class="fw-semibold">${i.producto.descripcion}</span>
                    <br/><small class="text-muted">${i.producto.idProducto}</small>
                </td>
                <td class="d-none d-xl-table-cell">${proveedor}</td>
                <td><span class="badge bg-dark">${i.sede.nombre}</span></td>
                <td><span class="badge bg-success">${i.cantidad}</span></td>
                <td class="d-none d-lg-table-cell">S/ ${i.costoUnitario}</td>
                <td class="fw-semibold text-success" style="white-space:nowrap">
                    S/ ${i.total}</td>
                <td class="d-none d-xl-table-cell">${factura}</td>
                <td>${fecha}</td>
                <td>${estadoBadge}</td>
                <td>
                    <div class="acciones-btn">
                        <button class="btn btn-warning btn-sm"
                            data-bs-toggle="modal"
                            data-bs-target="#modalIngreso"
                            data-id="${i.idIngreso}"
                            data-producto="${i.producto.idProducto}"
                            data-proveedor="${i.proveedor ? i.proveedor.idProveedor : 0}"
                            data-sede="${i.sede.idSede}"
                            data-cantidad="${i.cantidad}"
                            data-costo="${i.costoUnitario}"
                            data-factura="${factura}"
                            data-estado="${i.estado}"
                            onclick="editarIngreso(this)">
                            <i class="bi bi-pencil"></i>
                        </button>
                        ${btnEstado}
                    </div>
                </td>
            </tr>`;
        });

        actualizarPaginacion(pagina, data.totalPages,
            data.totalElements, data.ingresos.length);
        actualizarBotonesPaginacionFiltro(
            pagina, data.totalPages, estado);
    });
}

function actualizarBotonesPaginacionFiltro(paginaActual, totalPaginas, estado) {
    const nav = document.querySelector('.pagination');
    if (!nav) return;

    let html = '';

    html += `<li class="page-item ${paginaActual === 0 ? 'disabled' : ''}">
        <a class="page-link" href="javascript:void(0)"
           onclick="${paginaActual > 0 ?
               `filtrarPorEstadoServidor('${estado}', ${paginaActual - 1})` : ''}">
            <i class="bi bi-chevron-left"></i>
        </a>
    </li>`;

    for (let i = 0; i < totalPaginas; i++) {
        html += `<li class="page-item ${i === paginaActual ? 'active' : ''}">
            <a class="page-link" href="javascript:void(0)"
               onclick="filtrarPorEstadoServidor('${estado}', ${i})">${i + 1}</a>
        </li>`;
    }

    html += `<li class="page-item ${paginaActual === totalPaginas - 1 ? 'disabled' : ''}">
        <a class="page-link" href="javascript:void(0)"
           onclick="${paginaActual < totalPaginas - 1 ?
               `filtrarPorEstadoServidor('${estado}', ${paginaActual + 1})` : ''}">
            <i class="bi bi-chevron-right"></i>
        </a>
    </li>`;

    nav.innerHTML = html;
}


function limpiarModal(tituloId, campos) {
    if (tituloId) {
        const titulo = document.getElementById(tituloId);
        if (titulo) titulo.innerHTML = titulo.dataset.default || titulo.innerHTML;
    }
    if (campos) {
        campos.forEach(id => {
            const el = document.getElementById(id);
            if (el) el.value = '';
        });
    }
}


function limpiarModalIngreso() {
    document.getElementById('tituloModal').innerHTML =
        '<i class="bi bi-arrow-down-circle me-2"></i>Nuevo Ingreso';
    ['idIngreso','selectProducto','selectProveedor',
     'selectSede','inputCantidad','inputCosto',
     'inputFactura','inputObservacion'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });
}


function editarIngreso(btn) {
    const d = btn.dataset;
    document.getElementById('tituloModal').innerHTML =
        '<i class="bi bi-pencil me-2"></i>Editar Ingreso #' + d.id;
    document.getElementById('idIngreso').value        = d.id;
	document.getElementById('inputEstado').value      = d.estado;
    document.getElementById('selectProducto').value   = d.producto;
    document.getElementById('selectProveedor').value  = d.proveedor;
    document.getElementById('selectSede').value       = d.sede;
    document.getElementById('inputCantidad').value    = d.cantidad;
    document.getElementById('inputCosto').value       = d.costo;
    document.getElementById('inputFactura').value     = d.factura || '';
}


function guardarIngreso(event) {
    event.preventDefault();
    const form = document.getElementById('formIngreso');
    const formData = new FormData(form);
    const esNuevo = !document.getElementById('idIngreso').value;

    fetch('/ingresos/guardar/ajax', {
        method: 'POST',
        body: new URLSearchParams(formData)
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            bootstrap.Modal.getInstance(
                document.getElementById('modalIngreso')).hide();
            Swal.fire({
                icon: 'success',
                title: '¡Guardado!',
                text: data.mensaje,
                timer: 2000,
                showConfirmButton: false
            }).then(() => {
                if (esNuevo) {
                    fetch('/ingresos/lista/json?page=0')
                    .then(r => r.json())
                    .then(d => {
                        const ultimaPagina = d.totalPages - 1;
                        history.pushState({}, '',
                            '/ingresos?page=' + ultimaPagina);
                        recargarTabla();
                    });
                } else {
                    recargarTabla();
                }
            });
        } else {
            Swal.fire({ icon: 'error', title: 'Error', text: data.mensaje });
        }
    })
    .catch(() => {
        Swal.fire({ icon: 'error', title: 'Error', text: 'Error de conexión' });
    });
}


function confirmarEstado(url, estadoActual) {
    const esActivo = estadoActual == 1;
    const idIngreso = url.split('/').filter(x => !isNaN(x) && x !== '').pop();

    Swal.fire({
        title: esActivo ? '¿Suspender registro?' : '¿Activar registro?',
        text: esActivo
            ? 'El registro pasará a estado Suspendido'
            : 'El registro volverá a estado Activo',
        icon: esActivo ? 'warning' : 'question',
        showCancelButton: true,
        confirmButtonColor: esActivo ? '#dc3545' : '#198754',
        cancelButtonColor: '#6c757d',
        confirmButtonText: esActivo ? 'Sí, suspender' : 'Sí, activar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            fetch('/ingresos/estado/ajax/' + idIngreso, { method: 'POST' })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    Swal.fire({
                        icon: 'success',
                        title: 'Actualizado',
                        text: data.mensaje,
                        timer: 1500,
                        showConfirmButton: false
                    }).then(() => recargarTabla());
                }
            });
        }
    });
}


function recargarTabla() {
    const urlParams = new URLSearchParams(window.location.search);
    const paginaActual = parseInt(urlParams.get('page') || '0');

    fetch('/ingresos/lista/json?page=' + paginaActual)
    .then(res => res.json())
    .then(data => {
        const tbody = document.querySelector('#tablaIngresos tbody');
        tbody.innerHTML = '';

        if (!data.ingresos || data.ingresos.length === 0) {
            tbody.innerHTML = `<tr>
                <td colspan="11" class="text-center text-muted py-4">
                    No hay ingresos registrados
                </td></tr>`;
            actualizarPaginacion(data.currentPage, data.totalPages, data.totalElements);
            return;
        }

        const offset = paginaActual * 10;
        data.ingresos.forEach((i, idx) => {
            const estadoBadge = i.estado === 1
                ? '<span class="badge bg-success">Activo</span>'
                : '<span class="badge bg-secondary">Suspendido</span>';

            const btnEstado = i.estado === 1
                ? `<a href="javascript:void(0)"
                      data-url="/ingresos/estado/${i.idIngreso}"
                      data-estado="${i.estado}"
                      class="btn btn-danger btn-sm"
                      onclick="confirmarEstado(this.dataset.url, this.dataset.estado)">
                      <i class="bi bi-pause-circle"></i></a>`
                : `<a href="javascript:void(0)"
                      data-url="/ingresos/estado/${i.idIngreso}"
                      data-estado="${i.estado}"
                      class="btn btn-success btn-sm"
                      onclick="confirmarEstado(this.dataset.url, this.dataset.estado)">
                      <i class="bi bi-play-circle"></i></a>`;

            const proveedor = i.proveedor ? i.proveedor.nombre : '-';
            const factura   = i.nroFactura || '-';
            const fecha     = i.fecha
                ? new Date(i.fecha).toLocaleDateString('es-PE') : '-';

            tbody.innerHTML += `
            <tr class="${i.estado === 2 ? 'table-secondary' : ''}">
                <td>${offset + idx + 1}</td>
                <td>
                    <span class="fw-semibold">${i.producto.descripcion}</span>
                    <br/><small class="text-muted">${i.producto.idProducto}</small>
                </td>
                <td class="d-none d-xl-table-cell">${proveedor}</td>
                <td><span class="badge bg-dark">${i.sede.nombre}</span></td>
                <td><span class="badge bg-success">${i.cantidad}</span></td>
                <td class="d-none d-lg-table-cell">S/ ${i.costoUnitario}</td>
                <td class="fw-semibold text-success" style="white-space:nowrap">
                    S/ ${i.total}</td>
                <td class="d-none d-xl-table-cell">${factura}</td>
                <td>${fecha}</td>
                <td>${estadoBadge}</td>
                <td>
                    <div class="acciones-btn">
                        <button class="btn btn-warning btn-sm"
                            data-bs-toggle="modal"
                            data-bs-target="#modalIngreso"
                            data-id="${i.idIngreso}"
                            data-producto="${i.producto.idProducto}"
                            data-proveedor="${i.proveedor ? i.proveedor.idProveedor : 0}"
                            data-sede="${i.sede.idSede}"
                            data-cantidad="${i.cantidad}"
                            data-costo="${i.costoUnitario}"
                            data-factura="${factura}"
                            data-estado="${i.estado}"
                            onclick="editarIngreso(this)">
                            <i class="bi bi-pencil"></i>
                        </button>
                        ${btnEstado}
                    </div>
                </td>
            </tr>`;
        });

        actualizarPaginacion(
            data.currentPage,
            data.totalPages,
            data.totalElements,
            data.ingresos.length
        );
		
		actualizarBotonesPaginacion(
		    data.currentPage,
		    data.totalPages
		);
    });
}

function actualizarPaginacion(paginaActual, totalPaginas, totalElements, mostrando) {
    const textoMostrando = document.querySelector('.card-footer .text-muted');
    if (textoMostrando) {
        textoMostrando.innerHTML =
            `Mostrando <strong>${mostrando || 0}</strong> de
             <strong>${totalElements}</strong> registros`;
    }

    const textoPagina = document.querySelector('.col-md-auto .text-muted');
    if (textoPagina) {
        textoPagina.innerHTML =
            `Página <strong>${paginaActual + 1}</strong> de
             <strong>${totalPaginas}</strong> —
             Total: <strong>${totalElements}</strong> registros`;
    }
}




function mostrarExito(mensaje) {
    Swal.fire({
        icon: 'success',
        title: '¡Guardado!',
        text: mensaje || 'Registro guardado correctamente',
        timer: 2000,
        showConfirmButton: false
    });
}



let _sidebarAnimando = false;

function toggleSidebar() {
    if (_sidebarAnimando) return;

    const sidebar = document.getElementById('sidebar');
    const main    = document.getElementById('mainContent');
    const iconBot = document.getElementById('iconToggleBottom');

    _sidebarAnimando = true;
    sidebar.classList.add('animating');
    main.classList.add('animating');
    sidebar.classList.toggle('collapsed');
    main.classList.toggle('expanded');

    const estaColapsado = sidebar.classList.contains('collapsed');
    if (iconBot) iconBot.className = estaColapsado
        ? 'bi bi-chevron-right fs-5'
        : 'bi bi-chevron-left fs-5';

    localStorage.setItem('sidebarCollapsed', estaColapsado);

    setTimeout(() => {
        sidebar.classList.remove('animating');
        main.classList.remove('animating');
        _sidebarAnimando = false;
    }, 320);
}

document.addEventListener('DOMContentLoaded', () => {
    document.documentElement.classList.remove('sidebar-pre-collapsed');

    if (localStorage.getItem('sidebarCollapsed') === 'true') {
        const sidebar = document.getElementById('sidebar');
        const main    = document.getElementById('mainContent');
        const iconBot = document.getElementById('iconToggleBottom');
        sidebar?.classList.add('collapsed');
        main?.classList.add('expanded');
        if (iconBot) iconBot.className = 'bi bi-chevron-right fs-5';
    }
});


function navegarAjax(url, pushState = true) {
    fetch(url, {
        headers: { 'X-Requested-With': 'XMLHttpRequest' }
    })
    .then(res => res.text())
    .then(html => {
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, 'text/html');
        const nuevoContenido = doc.querySelector('section');

        if (nuevoContenido) {
            document.getElementById('contenidoPrincipal').innerHTML =
                nuevoContenido.innerHTML;
            const titulo = doc.querySelector('title');
            if (titulo) {
                document.querySelector('.topbar h5').textContent =
                    titulo.textContent;
            }
            actualizarSidebarActivo(url);
            if (pushState) {
                history.pushState({ url }, '', url);
            }
            ejecutarScripts(document.getElementById('contenidoPrincipal'));
        }
    })
    .catch(() => {
        window.location.href = url;
    });
}

function actualizarSidebarActivo(url) {
    document.querySelectorAll('.sidebar .nav-link').forEach(link => {
        if (link.classList.contains('brand-link')) return;
        
        link.classList.remove('active');
        const href = link.getAttribute('href');
        if (href && url.startsWith(href) && href !== '/') {
            link.classList.add('active');
        } else if (href === '/' && url === '/') {
            link.classList.add('active');
        }
    });
}

function ejecutarScripts(contenedor) {
    contenedor.querySelectorAll('script').forEach(script => {
        const nuevoScript = document.createElement('script');
        nuevoScript.textContent = script.textContent;
        document.body.appendChild(nuevoScript);
    });
}

window.addEventListener('popstate', (e) => {
    if (e.state?.url) {
        navegarAjax(e.state.url, false);
    }
});

function irPagina(pagina) {
    const url = '/ingresos?page=' + pagina;
    history.pushState({ url }, '', url);
    
    fetch('/ingresos/lista/json?page=' + pagina)
    .then(res => res.json())
    .then(data => {
        const tbody = document.querySelector('#tablaIngresos tbody');
        tbody.innerHTML = '';
        
        const offset = pagina * 10;
        
        if (!data.ingresos || data.ingresos.length === 0) {
            tbody.innerHTML = `<tr>
                <td colspan="11" class="text-center text-muted py-4">
                    No hay ingresos registrados
                </td></tr>`;
            actualizarPaginacion(pagina, data.totalPages, data.totalElements, 0);
            return;
        }

        data.ingresos.forEach((i, idx) => {
            const estadoBadge = i.estado === 1
                ? '<span class="badge bg-success">Activo</span>'
                : '<span class="badge bg-secondary">Suspendido</span>';

            const btnEstado = i.estado === 1
                ? `<a href="javascript:void(0)"
                      data-url="/ingresos/estado/${i.idIngreso}"
                      data-estado="${i.estado}"
                      class="btn btn-danger btn-sm"
                      onclick="confirmarEstado(this.dataset.url, this.dataset.estado)">
                      <i class="bi bi-pause-circle"></i></a>`
                : `<a href="javascript:void(0)"
                      data-url="/ingresos/estado/${i.idIngreso}"
                      data-estado="${i.estado}"
                      class="btn btn-success btn-sm"
                      onclick="confirmarEstado(this.dataset.url, this.dataset.estado)">
                      <i class="bi bi-play-circle"></i></a>`;

            const proveedor = i.proveedor ? i.proveedor.nombre : '-';
            const factura   = i.nroFactura || '-';
            const fecha     = i.fecha
                ? new Date(i.fecha).toLocaleDateString('es-PE') : '-';

            tbody.innerHTML += `
            <tr class="${i.estado === 2 ? 'table-secondary' : ''}">
                <td>${offset + idx + 1}</td>
                <td>
                    <span class="fw-semibold">${i.producto.descripcion}</span>
                    <br/><small class="text-muted">${i.producto.idProducto}</small>
                </td>
                <td class="d-none d-xl-table-cell">${proveedor}</td>
                <td><span class="badge bg-dark">${i.sede.nombre}</span></td>
                <td><span class="badge bg-success">${i.cantidad}</span></td>
                <td class="d-none d-lg-table-cell">S/ ${i.costoUnitario}</td>
                <td class="fw-semibold text-success" style="white-space:nowrap">
                    S/ ${i.total}</td>
                <td class="d-none d-xl-table-cell">${factura}</td>
                <td>${fecha}</td>
                <td>${estadoBadge}</td>
                <td>
                    <div class="acciones-btn">
                        <button class="btn btn-warning btn-sm"
                            data-bs-toggle="modal"
                            data-bs-target="#modalIngreso"
                            data-id="${i.idIngreso}"
                            data-producto="${i.producto.idProducto}"
                            data-proveedor="${i.proveedor ? i.proveedor.idProveedor : 0}"
                            data-sede="${i.sede.idSede}"
                            data-cantidad="${i.cantidad}"
                            data-costo="${i.costoUnitario}"
                            data-factura="${factura}"
                            data-estado="${i.estado}"
                            onclick="editarIngreso(this)">
                            <i class="bi bi-pencil"></i>
                        </button>
                        ${btnEstado}
                    </div>
                </td>
            </tr>`;
        });

        actualizarPaginacion(pagina, data.totalPages,
            data.totalElements, data.ingresos.length);
        actualizarBotonesPaginacion(pagina, data.totalPages);
    });
}

function actualizarBotonesPaginacion(paginaActual, totalPaginas) {
    const nav = document.querySelector('.pagination');
    if (!nav) return;

    let html = '';

    html += `<li class="page-item ${paginaActual === 0 ? 'disabled' : ''}">
        <a class="page-link" href="javascript:void(0)"
           onclick="${paginaActual > 0 ? `irPagina(${paginaActual - 1})` : ''}">
            <i class="bi bi-chevron-left"></i>
        </a>
    </li>`;

    for (let i = 0; i < totalPaginas; i++) {
        html += `<li class="page-item ${i === paginaActual ? 'active' : ''}">
            <a class="page-link" href="javascript:void(0)"
               onclick="irPagina(${i})">${i + 1}</a>
        </li>`;
    }

    html += `<li class="page-item ${paginaActual === totalPaginas - 1 ? 'disabled' : ''}">
        <a class="page-link" href="javascript:void(0)"
           onclick="${paginaActual < totalPaginas - 1 ? `irPagina(${paginaActual + 1})` : ''}">
            <i class="bi bi-chevron-right"></i>
        </a>
    </li>`;

    nav.innerHTML = html;
}
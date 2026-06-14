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

//DASHBOARD 

function irPaginaDashboard(pagina) {
    navegarAjax('/?page=' + pagina, true);
}

//PRODUCTOS JS

function filtrarTablaProductos() {
    const texto  = document.getElementById('buscador').value;
    const estado = document.getElementById('filtroEstado').value;
    filtrarProductosServidor(estado, 0, texto);
}

function filtrarDesdeSelectProductos(estado) {
    const texto = document.getElementById('buscador').value;
    filtrarProductosServidor(estado, 0, texto);
}

function filtrarProductosServidor(estado, pagina, buscar = '') {
    fetch(`/productos/lista/json?page=${pagina}&estado=${estado}&buscar=${encodeURIComponent(buscar)}`)
    .then(res => res.json())
    .then(data => {
        const tbody = document.querySelector('#tablaProductos tbody');
        tbody.innerHTML = '';

        if (!data.productos || data.productos.length === 0) {
            tbody.innerHTML = `<tr>
                <td colspan="9" class="text-center text-muted py-4">
                    No hay productos registrados
                </td></tr>`;
            actualizarPaginacionProductos(pagina, 0, 0, 0);
            return;
        }

        const offset = pagina * 10;
        data.productos.forEach((p, idx) => {
            const estadoBadge = p.estado === 1
                ? '<span class="badge bg-success">Activo</span>'
                : '<span class="badge bg-secondary">Suspendido</span>';

            const btnEstado = p.estado === 1
                ? `<a href="javascript:void(0)"
                      data-url="/productos/estado/${p.idProducto}"
                      data-estado="${p.estado}"
                      class="btn btn-danger btn-sm"
                      onclick="confirmarEstadoProducto(this.dataset.url, this.dataset.estado)">
                      <i class="bi bi-pause-circle"></i></a>`
                : `<a href="javascript:void(0)"
                      data-url="/productos/estado/${p.idProducto}"
                      data-estado="${p.estado}"
                      class="btn btn-success btn-sm"
                      onclick="confirmarEstadoProducto(this.dataset.url, this.dataset.estado)">
                      <i class="bi bi-play-circle"></i></a>`;
            let stockClass = 'badge bg-success';
            if (p.stockTotal === 0)      stockClass = 'badge bg-danger';
            else if (p.stockTotal < 10)  stockClass = 'badge bg-warning text-dark';

            const tipo = p.tipo ? p.tipo.nombre : '-';
            const ean  = p.eanInt || '-';

            tbody.innerHTML += `
            <tr class="${p.estado === 2 ? 'table-secondary' : ''}">
                <td><span class="fw-semibold text-primary">${p.idProducto}</span></td>
                <td>${p.descripcion}</td>
                <td><span class="badge bg-secondary">${tipo}</span></td>
                <td class="d-none d-lg-table-cell text-muted">${ean}</td>
                <td>S/ ${p.costoUnitario ?? '0.00'}</td>
                <td class="d-none d-lg-table-cell">S/ ${p.precioVenta ?? '0.00'}</td>
                <td><span class="${stockClass}">${p.stockTotal}</span></td>
                <td>${estadoBadge}</td>
                <td>
                    <div class="acciones-btn">
                        <button class="btn btn-warning btn-sm"
                            data-bs-toggle="modal"
                            data-bs-target="#modalProducto"
                            data-id="${p.idProducto}"
                            data-ean="${ean}"
                            data-descripcion="${p.descripcion}"
                            data-tipo="${p.tipo ? p.tipo.idTipo : ''}"
                            data-costo="${p.costoUnitario ?? ''}"
                            data-venta="${p.precioVenta ?? ''}"
                            data-stock="${p.stockTotal}"
                            data-estado="${p.estado}"
                            onclick="editarProducto(this)">
                            <i class="bi bi-pencil"></i>
                        </button>
                        ${btnEstado}
                    </div>
                </td>
            </tr>`;
        });

        actualizarPaginacionProductos(pagina, data.totalPages,
            data.totalElements, data.productos.length);
        actualizarBotonesProductos(pagina, data.totalPages, estado, buscar);
    });
}

function limpiarModalProducto() {
    document.getElementById('tituloModalProducto').innerHTML =
        '<i class="bi bi-box-seam me-2"></i>Nuevo Producto';
    ['inputIdProducto','inputEan','inputDescripcion','selectTipo',
     'inputCostoProducto','inputVenta','inputEstadoProducto'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });
    const stock = document.getElementById('inputStock');
    if (stock) stock.value = '0';
    const idInput = document.getElementById('inputIdProducto');
    if (idInput) idInput.disabled = false;
}

function editarProducto(btn) {
    const d = btn.dataset;
    document.getElementById('tituloModalProducto').innerHTML =
        '<i class="bi bi-pencil me-2"></i>Editar Producto ' + d.id;
    document.getElementById('inputIdProducto').value      = d.id;
    document.getElementById('inputIdProducto').disabled   = true; 
    document.getElementById('inputEan').value             = d.ean !== '-' ? d.ean : '';
    document.getElementById('inputDescripcion').value     = d.descripcion;
    document.getElementById('selectTipo').value           = d.tipo;
    document.getElementById('inputCostoProducto').value   = d.costo;
    document.getElementById('inputVenta').value           = d.venta;
    document.getElementById('inputStock').value           = d.stock;
    document.getElementById('inputEstadoProducto').value  = d.estado;
}

function guardarProducto(event) {
    event.preventDefault();
    const form     = document.getElementById('formProducto');
    const formData = new FormData(form);
    const idInput = document.getElementById('inputIdProducto');
    if (idInput.disabled) {
        formData.set('idProducto', idInput.value);
    }

    fetch('/productos/guardar/ajax', {
        method: 'POST',
        body: new URLSearchParams(formData)
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            bootstrap.Modal.getInstance(
                document.getElementById('modalProducto')).hide();
            Swal.fire({
                icon: 'success', title: '¡Guardado!',
                text: data.mensaje, timer: 2000,
                showConfirmButton: false
            }).then(() => recargarTablaProductos());
        } else {
            Swal.fire({ icon: 'error', title: 'Error', text: data.mensaje });
        }
    })
    .catch(() => {
        Swal.fire({ icon: 'error', title: 'Error', text: 'Error de conexión' });
    });
}

function confirmarEstadoProducto(url, estadoActual) {
    const esActivo = estadoActual == 1;
    const id = url.split('/').pop();

    Swal.fire({
        title: esActivo ? '¿Suspender producto?' : '¿Activar producto?',
        text: esActivo
            ? 'El producto no aparecerá en nuevos ingresos'
            : 'El producto volverá a estar disponible',
        icon: esActivo ? 'warning' : 'question',
        showCancelButton: true,
        confirmButtonColor: esActivo ? '#dc3545' : '#198754',
        cancelButtonColor: '#6c757d',
        confirmButtonText: esActivo ? 'Sí, suspender' : 'Sí, activar',
        cancelButtonText: 'Cancelar'
    }).then(result => {
        if (result.isConfirmed) {
            fetch('/productos/estado/ajax/' + id, { method: 'POST' })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    Swal.fire({
                        icon: 'success', title: 'Actualizado',
                        text: data.mensaje, timer: 1500,
                        showConfirmButton: false
                    }).then(() => recargarTablaProductos());
                }
            });
        }
    });
}

function recargarTablaProductos() {
    const urlParams    = new URLSearchParams(window.location.search);
    const paginaActual = parseInt(urlParams.get('page') || '0');
    irPaginaProductos(paginaActual);
}

function irPaginaProductos(pagina) {
    history.pushState({}, '', '/productos?page=' + pagina);
    filtrarProductosServidor('', pagina, '');
}

function actualizarPaginacionProductos(paginaActual, totalPaginas,
                                        totalElements, mostrando) {
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

function actualizarBotonesProductos(paginaActual, totalPaginas,
                                     estado = '', buscar = '') {
    const nav = document.querySelector('.pagination');
    if (!nav) return;

    let html = `<li class="page-item ${paginaActual === 0 ? 'disabled' : ''}">
        <a class="page-link" href="javascript:void(0)"
           onclick="${paginaActual > 0
               ? `filtrarProductosServidor('${estado}', ${paginaActual - 1}, '${buscar}')`
               : ''}">
            <i class="bi bi-chevron-left"></i>
        </a></li>`;

    for (let i = 0; i < totalPaginas; i++) {
        html += `<li class="page-item ${i === paginaActual ? 'active' : ''}">
            <a class="page-link" href="javascript:void(0)"
               onclick="filtrarProductosServidor('${estado}', ${i}, '${buscar}')">
               ${i + 1}</a></li>`;
    }

    html += `<li class="page-item ${paginaActual === totalPaginas - 1 ? 'disabled' : ''}">
        <a class="page-link" href="javascript:void(0)"
           onclick="${paginaActual < totalPaginas - 1
               ? `filtrarProductosServidor('${estado}', ${paginaActual + 1}, '${buscar}')`
               : ''}">
            <i class="bi bi-chevron-right"></i>
        </a></li>`;

    nav.innerHTML = html;
}

// trabajadores
function filtrarTablaTrabajadores() {
    const texto = document.getElementById('buscador').value;
    const estado = document.getElementById('filtroEstado').value;

    filtrarTrabajadoresServidor(estado, 0, texto);
}

function filtrarDesdeSelectTrabajadores(estado) {
    const texto = document.getElementById('buscador').value;

    filtrarTrabajadoresServidor(estado, 0, texto);
}

function filtrarTrabajadoresServidor(
    estado,
    pagina,
    buscar = ''
) {

    fetch(
        `/trabajadores/lista/json?page=${pagina}&estado=${estado}&buscar=${encodeURIComponent(buscar)}`
    )
    .then(res => res.json())
    .then(data => {
        const tbody =
            document.querySelector('#tablaTrabajadores tbody');
        tbody.innerHTML = '';

        if (
            !data.trabajadores ||
            data.trabajadores.length === 0
        ) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="8"
                        class="text-center text-muted py-4">
                        No hay trabajadores registrados
                    </td>
                </tr>`;

            return;
        }

        data.trabajadores.forEach((t, idx) => {

            const estadoBadge =
                t.activoCesado === 'ACTIVO'
                ? '<span class="badge bg-success">ACTIVO</span>'
                : '<span class="badge bg-secondary">CESADO</span>';

            const btnEstado =
                t.activoCesado === 'ACTIVO'
                ? `<a href="javascript:void(0)"
                        data-id="${t.idTrabajador}"
                        data-estado="${t.activoCesado}"
                        class="btn btn-danger btn-sm"
                        onclick="confirmarEstadoTrabajador(
                            this.dataset.id,
                            this.dataset.estado)">
                        <i class="bi bi-pause-circle"></i>
                   </a>`

                : `<a href="javascript:void(0)"
                        data-id="${t.idTrabajador}"
                        data-estado="${t.activoCesado}"
                        class="btn btn-success btn-sm"
                        onclick="confirmarEstadoTrabajador(
                            this.dataset.id,
                            this.dataset.estado)">
                        <i class="bi bi-play-circle"></i>
                   </a>`;

            tbody.innerHTML += `
                <tr>

                    <td>${t.idTrabajador}</td>

                    <td>
                        <span class="fw-semibold">
                            ${t.nombreCompleto}
                        </span>
                        <br>
                        <small class="text-muted">
                            ${t.documentoIdentidad}
                        </small>
                    </td>

                    <td>${t.documentoIdentidad}</td>

                    <td>${t.puesto ?? ''}</td>

                    <td>${t.cliente ?? ''}</td>

                    <td>
                        <span class="badge bg-dark">
                            ${t.sede
                                ? t.sede.nombre
                                : ''}
                        </span>
                    </td>

                    <td>
                        ${estadoBadge}
                    </td>

                    <td>
                        <div class="acciones-btn">

                            <button
                                class="btn btn-warning btn-sm"
                                data-bs-toggle="modal"
                                data-bs-target="#modalTrabajador">

                                <i class="bi bi-pencil"></i>

                            </button>

                            ${btnEstado}

                        </div>
                    </td>

                </tr>`;
        });
		
		actualizarPaginacionTrabajadores(
		    pagina,
		    data.totalPages,
		    data.totalElements,
		    data.trabajadores.length
		);

		actualizarBotonesTrabajadores(
		    pagina,
		    data.totalPages,
		    estado,
		    buscar
		);

    });
}

function confirmarEstadoTrabajador(
    id,
    estadoActual
) {

    const esActivo =
        estadoActual === 'ACTIVO';

    Swal.fire({

        title: esActivo
            ? '¿Cesar trabajador?'
            : '¿Activar trabajador?',

        text: esActivo
            ? 'El trabajador pasará a estado CESADO'
            : 'El trabajador volverá a estado ACTIVO',

        icon: esActivo
            ? 'warning'
            : 'question',

        showCancelButton: true,

        confirmButtonColor:
            esActivo
                ? '#dc3545'
                : '#198754',

        cancelButtonColor: '#6c757d',

        confirmButtonText:
            esActivo
                ? 'Sí, cesar'
                : 'Sí, activar',

        cancelButtonText: 'Cancelar'

    })

    .then((result) => {

        if (result.isConfirmed) {

            fetch(
                '/trabajadores/estado/ajax/' + id,
                {
                    method: 'POST'
                }
            )

            .then(res => res.json())

            .then(data => {

                if (data.success) {

                    Swal.fire({

                        icon: 'success',

                        title: 'Actualizado',

                        text: data.mensaje,

                        timer: 1500,

                        showConfirmButton: false

                    })

                    .then(() => {

                        const estado =
                            document.getElementById(
                                'filtroEstado'
                            ).value;

                        const buscar =
                            document.getElementById(
                                'buscador'
                            ).value;

                        filtrarTrabajadoresServidor(
                            estado,
                            0,
                            buscar
                        );
                    });
                }
            });
        }
    });
}

function limpiarModalTrabajador() {

    document.getElementById('tituloModalTrabajador').innerHTML =
        '<i class="bi bi-people-fill me-2"></i>Nuevo Trabajador';

    [
        'idTrabajador',
        'inputNombre',
        'inputDni',
        'inputPuesto',
        'inputCliente',
        'selectSedeTrabajador',
        'inputFechaIngreso'
    ].forEach(id => {

        const el = document.getElementById(id);

        if (el) el.value = '';
    });
}
function editarTrabajador(btn) {

    const d = btn.dataset;

    document.getElementById('tituloModalTrabajador').innerHTML =
        '<i class="bi bi-pencil me-2"></i>Editar Trabajador #' + d.id;

    document.getElementById('idTrabajador').value = d.id;
    document.getElementById('inputNombre').value = d.nombre;
    document.getElementById('inputDni').value = d.dni;
    document.getElementById('inputPuesto').value = d.puesto || '';
    document.getElementById('inputCliente').value = d.cliente || '';
    document.getElementById('selectSedeTrabajador').value = d.sede;
    document.getElementById('inputFechaIngreso').value = d.fecha || '';
}


function irPaginaTrabajadores(pagina) {

    history.pushState(
        {},
        '',
        '/trabajadores?page=' + pagina
    );

    const estado =
        document.getElementById('filtroEstado').value;

    const buscar =
        document.getElementById('buscador').value;

    filtrarTrabajadoresServidor(
        estado,
        pagina,
        buscar
    );
}
function actualizarPaginacionTrabajadores(
    paginaActual,
    totalPaginas,
    totalElements,
    mostrando
) {

    const textoMostrando =
        document.querySelector('.card-footer .text-muted');

    if (textoMostrando) {

        textoMostrando.innerHTML =
            `Mostrando <strong>${mostrando || 0}</strong>
             de <strong>${totalElements}</strong> registros`;
    }

    const textoPagina =
        document.querySelector('.col-md-auto .text-muted');

    if (textoPagina) {

        textoPagina.innerHTML =
            `Página <strong>${paginaActual + 1}</strong>
             de <strong>${totalPaginas}</strong>
             — Total:
             <strong>${totalElements}</strong> registros`;
    }
}

function actualizarBotonesTrabajadores(
    paginaActual,
    totalPaginas,
    estado = '',
    buscar = ''
) {

    const nav = document.querySelector('.pagination');

    if (!nav) return;

    let html = '';

    html += `
    <li class="page-item ${paginaActual === 0 ? 'disabled' : ''}">
        <a class="page-link"
           href="javascript:void(0)"
           onclick="${
               paginaActual > 0
               ? `irPaginaTrabajadores(${paginaActual - 1})`
               : ''
           }">
            <i class="bi bi-chevron-left"></i>
        </a>
    </li>`;

    for (let i = 0; i < totalPaginas; i++) {

        html += `
        <li class="page-item ${i === paginaActual ? 'active' : ''}">
            <a class="page-link"
               href="javascript:void(0)"
               onclick="irPaginaTrabajadores(${i})">

                ${i + 1}

            </a>
        </li>`;
    }

    html += `
    <li class="page-item ${
        paginaActual === totalPaginas - 1
        ? 'disabled'
        : ''
    }">

        <a class="page-link"
           href="javascript:void(0)"
           onclick="${
               paginaActual < totalPaginas - 1
               ? `irPaginaTrabajadores(${paginaActual + 1})`
               : ''
           }">

            <i class="bi bi-chevron-right"></i>

        </a>

    </li>`;

    nav.innerHTML = html;
}

function guardarTrabajador(event) {

    event.preventDefault();

    const form = document.getElementById('formTrabajador');

    const formData = new FormData(form);

    const esNuevo =
        !document.getElementById('idTrabajador').value;

    fetch('/trabajadores/guardar/ajax', {

        method: 'POST',

        body: new URLSearchParams(formData)

    })

    .then(res => res.json())

    .then(data => {

        if (data.success) {

            bootstrap.Modal.getInstance(
                document.getElementById('modalTrabajador')
            ).hide();

            Swal.fire({

                icon: 'success',

                title: '¡Guardado!',

                text: data.mensaje,

                timer: 2000,

                showConfirmButton: false

            })

            .then(() => {

                const estado =
                    document.getElementById(
                        'filtroEstado'
                    ).value;

                const buscar =
                    document.getElementById(
                        'buscador'
                    ).value;

                filtrarTrabajadoresServidor(
                    estado,
                    0,
                    buscar
                );
            });

        } else {

            Swal.fire({

                icon: 'error',

                title: 'Error',

                text: data.mensaje

            });
        }

    })

    .catch(() => {

        Swal.fire({

            icon: 'error',

            title: 'Error',

            text: 'Error de conexión'

        });

    });
}
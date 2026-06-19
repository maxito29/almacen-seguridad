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
    renderPaginacion(
        document.querySelector('.pagination'),
        paginaActual, totalPaginas,
        `(p) => filtrarPorEstadoServidor('${estado}', p)`
    );
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

function actualizarBotonesPaginacion(paginaActual, totalPaginas) {
    renderPaginacion(
        document.querySelector('.pagination'),
        paginaActual, totalPaginas, 'irPagina'
    );
}


//PRODUCTOS

function actualizarBotonesProductos(paginaActual, totalPaginas) {
    renderPaginacion(
        document.querySelector('.pagination'),
        paginaActual, totalPaginas, 'irPaginaProductos'
    );
}

function actualizarPaginacionProductos(paginaActual, totalPaginas, totalElements, mostrando) {
    const textoMostrando = document.getElementById('textoMostrando');
    if (textoMostrando)
        textoMostrando.innerHTML =
            `Mostrando <strong>${mostrando || 0}</strong> de <strong>${totalElements}</strong> registros`;

    const textoPagina = document.getElementById('textoPaginaFiltro');
    if (textoPagina)
        textoPagina.innerHTML =
            `Página <strong>${paginaActual + 1}</strong> de <strong>${totalPaginas}</strong> — Total: <strong>${totalElements}</strong> registros`;

    const textoPaginaBottom = document.getElementById('textoPaginaBottom');
    if (textoPaginaBottom)
        textoPaginaBottom.innerHTML =
            `Página <strong>${paginaActual + 1}</strong> de <strong>${totalPaginas}</strong>`;
}

function actualizarBotonesProductos(paginaActual, totalPaginas) {
    renderPaginacion(
        document.querySelector('.pagination'),
        paginaActual, totalPaginas, 'irPaginaProductos'
    );
}

//PROVEEDORES

function actualizarPaginacionProveedores(paginaActual, totalPaginas, totalElements, mostrando) {
    const textoMostrando = document.getElementById('textoMostrando');
    if (textoMostrando)
        textoMostrando.innerHTML =
            `Mostrando <strong>${mostrando || 0}</strong> de <strong>${totalElements}</strong> registros`;

    const textoPagina = document.getElementById('textoPaginaFiltro');
    if (textoPagina)
        textoPagina.innerHTML =
            `Página <strong>${paginaActual + 1}</strong> de <strong>${totalPaginas}</strong> — Total: <strong>${totalElements}</strong> registros`;

    const textoPaginaBottom = document.getElementById('textoPaginaBottom');
    if (textoPaginaBottom)
        textoPaginaBottom.innerHTML =
            `Página <strong>${paginaActual + 1}</strong> de <strong>${totalPaginas}</strong>`;
}

function actualizarBotonesProveedores(paginaActual, totalPaginas) {
    renderPaginacion(
        document.querySelector('.pagination'),
        paginaActual, totalPaginas, 'irPaginaProveedores'
    );
}

//TRABAJADORES

function actualizarPaginacionTrabajadores(paginaActual, totalPaginas, totalElements, mostrando) {
    const textoMostrando = document.getElementById('textoMostrando');
    if (textoMostrando)
        textoMostrando.innerHTML =
            `Mostrando <strong>${mostrando || 0}</strong> de <strong>${totalElements}</strong> registros`;

    const textoPagina = document.getElementById('textoPaginaFiltro');
    if (textoPagina)
        textoPagina.innerHTML =
            `Página <strong>${paginaActual + 1}</strong> de <strong>${totalPaginas}</strong> — Total: <strong>${totalElements}</strong> registros`;

    const textoPaginaBottom = document.getElementById('textoPaginaBottom');
    if (textoPaginaBottom)
        textoPaginaBottom.innerHTML =
            `Página <strong>${paginaActual + 1}</strong> de <strong>${totalPaginas}</strong>`;
}

function actualizarBotonesTrabajadores(paginaActual, totalPaginas) {
    renderPaginacion(
        document.querySelector('.pagination'),
        paginaActual, totalPaginas, 'irPaginaTrabajadores'
    );
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

    if (document.getElementById('chartData')) {
        cargarScript('/js/dashboard.js');
		
		}
		const divBienvenida = document.getElementById('divBienvenida');
		if (divBienvenida) {
		    const nombre = divBienvenida.dataset.nombre || 'Usuario';
		    
		    history.replaceState({}, '', '/');
		    
		    Swal.fire({
		        icon: 'success',
		        title: '¡Bienvenido!',
		        text: nombre,
		        timer: 2500,
		        showConfirmButton: false
		    });
		}
});

function navegarAjax(url, pushState = true) {
    NProgress.start(); 
    
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
        NProgress.done(); 
    })
    .catch(() => {
        NProgress.done(); 
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
    ['graficoDona', 'graficoBarras', 'graficoTop5'].forEach(id => {
        const el = document.getElementById(id);
        if (el && el._apexcharts) {
            el._apexcharts.destroy();
            delete el._apexcharts;
        }
    });
    contenedor.querySelectorAll('script').forEach(script => {
        if (script.src) return; 
        const nuevoScript = document.createElement('script');
        nuevoScript.textContent = script.textContent;
        document.body.appendChild(nuevoScript);
    });

    if (document.getElementById('chartData')) {
        cargarScript('/js/dashboard.js');
    }
}

function cargarScript(src) {
    const existente = document.querySelector(`script[src="${src}"]`);
    if (existente) existente.remove(); 
    const script = document.createElement('script');
    script.src = src;
    document.body.appendChild(script);
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

		actualizarBotonesPaginacion(pagina, data.totalPages);
		        actualizarPaginacion(          
		            pagina,
		            data.totalPages,
		            data.totalElements,
		            data.ingresos.length
		        );
    });
}



//DASHBOARD 

function irPaginaDashboard(pagina) {
    fetch(`/dashboard/stock/json?page=${pagina}`)
    .then(res => res.json())
    .then(data => {
        const tbody = document.querySelector('.table tbody');
        tbody.innerHTML = '';

        if (!data.productos || data.productos.length === 0) {
            tbody.innerHTML = `<tr>
                <td colspan="6" class="text-center text-muted py-4">
                    No hay productos registrados
                </td></tr>`;
        } else {
            data.productos.forEach(p => {
                let rowClass = '';
                if (p.stockTotal === 0) rowClass = 'table-danger';
                else if (p.stockTotal <= 3) rowClass = 'table-warning';

                let estadoBadge = '';
                if (p.stockTotal > 3)
                    estadoBadge = '<span class="badge bg-success">Con Stock</span>';
                else if (p.stockTotal > 0)
                    estadoBadge = '<span class="badge bg-warning text-dark">Stock Bajo</span>';
                else
                    estadoBadge = '<span class="badge bg-danger">Sin Stock</span>';

                const stockClass = p.stockTotal === 0 ? 'text-danger' : 'text-success';
                const tipo = p.tipo ? p.tipo.nombre : '-';

                tbody.innerHTML += `
                <tr class="${rowClass}">
                    <td>${p.idProducto}</td>
                    <td>${p.descripcion}</td>
                    <td><span class="badge bg-secondary">${tipo}</span></td>
                    <td>S/ ${p.costoUnitario}</td>
                    <td class="fw-bold ${stockClass}">${p.stockTotal}</td>
                    <td>${estadoBadge}</td>
                </tr>`;
            });
        }

        const textoMostrando = document.getElementById('textoMostrando');
        if (textoMostrando)
            textoMostrando.innerHTML =
                `Mostrando <strong>${data.productos?.length || 0}</strong> de <strong>${data.totalElements}</strong> productos`;

        const textoPaginaBottom = document.getElementById('textoPaginaBottom');
        if (textoPaginaBottom)
            textoPaginaBottom.innerHTML =
                `Página <strong>${pagina + 1}</strong> de <strong>${data.totalPages}</strong>`;

        renderPaginacion(
            document.querySelector('.pagination'),
            pagina, data.totalPages, 'irPaginaDashboard'
        );

        history.pushState({}, '', '/?page=' + pagina);
    })
    .catch(() => {
        console.error('Error al cargar página del dashboard');
    });
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

function filtrarTrabajadoresServidor(estado, pagina, buscar = '') {
    fetch(`/trabajadores/lista/json?page=${pagina}&estado=${estado}&buscar=${encodeURIComponent(buscar)}`)
    .then(res => res.json())
    .then(data => {
        const tbody = document.querySelector('#tablaTrabajadores tbody');
        tbody.innerHTML = '';

        if (!data.trabajadores || data.trabajadores.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="8" class="text-center text-muted py-4">
                        No hay trabajadores registrados
                    </td>
                </tr>`;
            return;
        }

        data.trabajadores.forEach((t, idx) => {
            const estadoBadge = t.activoCesado === 'ACTIVO'
                ? '<span class="badge bg-success">ACTIVO</span>'
                : '<span class="badge bg-secondary">CESADO</span>';

            const btnEstado = t.activoCesado === 'ACTIVO'
                ? `<a href="javascript:void(0)"
                        data-id="${t.idTrabajador}"
                        data-estado="${t.activoCesado}"
                        class="btn btn-danger btn-sm"
                        onclick="confirmarEstadoTrabajador(this.dataset.id, this.dataset.estado)">
                        <i class="bi bi-pause-circle"></i>
                   </a>`
                : `<a href="javascript:void(0)"
                        data-id="${t.idTrabajador}"
                        data-estado="${t.activoCesado}"
                        class="btn btn-success btn-sm"
                        onclick="confirmarEstadoTrabajador(this.dataset.id, this.dataset.estado)">
                        <i class="bi bi-play-circle"></i>
                   </a>`;

            tbody.innerHTML += `
                <tr>
                    <td>${t.idTrabajador}</td>
                    <td>
                        <span class="fw-semibold">${t.nombreCompleto}</span>
                        <br>
                        <small class="text-muted">${t.documentoIdentidad}</small>
                    </td>
                    <td>${t.documentoIdentidad}</td>
                    <td>${t.puesto ?? ''}</td>
                    <td>${t.cliente ?? ''}</td>
                    <td>
                        <span class="badge bg-dark">${t.sede ? t.sede.nombre : ''}</span>
                    </td>
                    <td>${estadoBadge}</td>
                    <td>
                        <div class="acciones-btn">
						<button class="btn btn-warning btn-sm"
						    data-bs-toggle="modal"
						    data-bs-target="#modalTrabajador"
						    data-id="${t.idTrabajador}"
						    data-nombre="${t.nombreCompleto}"
						    data-dni="${t.documentoIdentidad}"
						    data-puesto="${t.puesto || ''}"
						    data-cliente="${t.cliente || ''}"
						    data-sede="${t.sede ? t.sede.idSede : ''}"
						    data-fecha="${t.fechaIngreso ? t.fechaIngreso.split('T')[0] : ''}"
						    onclick="editarTrabajador(this)">
						    <i class="bi bi-pencil"></i>
						</button>
                            ${btnEstado}
                        </div>
                    </td>
                </tr>`;
        });

        actualizarPaginacionTrabajadores(pagina, data.totalPages,
            data.totalElements, data.trabajadores.length);
        actualizarBotonesTrabajadores(pagina, data.totalPages, estado, buscar);
    });
}

function confirmarEstadoTrabajador(id, estadoActual) {
    const esActivo = estadoActual === 'ACTIVO';

    Swal.fire({
        title: esActivo ? '¿Cesar trabajador?' : '¿Activar trabajador?',
        text: esActivo
            ? 'El trabajador pasará a estado CESADO'
            : 'El trabajador volverá a estado ACTIVO',
        icon: esActivo ? 'warning' : 'question',
        showCancelButton: true,
        confirmButtonColor: esActivo ? '#dc3545' : '#198754',
        cancelButtonColor: '#6c757d',
        confirmButtonText: esActivo ? 'Sí, cesar' : 'Sí, activar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            fetch('/trabajadores/estado/ajax/' + id, { method: 'POST' })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    Swal.fire({
                        icon: 'success',
                        title: 'Actualizado',
                        text: data.mensaje,
                        timer: 1500,
                        showConfirmButton: false
                    }).then(() => {
                        const estado = document.getElementById('filtroEstado').value;
                        const buscar = document.getElementById('buscador').value;
                        filtrarTrabajadoresServidor(estado, 0, buscar);
                    });
                }
            });
        }
    });
}

function limpiarModalTrabajador() {
    document.getElementById('tituloModalTrabajador').innerHTML =
        '<i class="bi bi-people-fill me-2"></i>Nuevo Trabajador';

    ['idTrabajador','inputNombre','inputDni','inputPuesto',
     'inputCliente','selectSedeTrabajador','inputFechaIngreso'
    ].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });
}

function editarTrabajador(btn) {
    const d = btn.dataset;
	document.getElementById('inputActivoCesado').value = d.estado || 'ACTIVO';
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
    history.pushState({}, '', '/trabajadores?page=' + pagina);
    const estado = document.getElementById('filtroEstado').value;
    const buscar = document.getElementById('buscador').value;
    filtrarTrabajadoresServidor(estado, pagina, buscar);
}


//PROVEEDORES

function filtrarTablaProveedores() {
    const texto  = document.getElementById('buscador').value;
    const estado = document.getElementById('filtroEstado').value;
    filtrarProveedoresServidor(estado, 0, texto);
}

function filtrarDesdeSelectProveedores(estado) {
    const texto = document.getElementById('buscador').value;
    filtrarProveedoresServidor(estado, 0, texto);
}

function filtrarProveedoresServidor(estado, pagina, buscar = '') {
    fetch(`/proveedores/lista/json?page=${pagina}&estado=${estado}` +
          `&buscar=${encodeURIComponent(buscar)}`)
    .then(res => res.json())
    .then(data => {
        const tbody = document.querySelector('#tablaProveedores tbody');
        tbody.innerHTML = '';

        if (!data.proveedores || data.proveedores.length === 0) {
            tbody.innerHTML = `<tr>
                <td colspan="8" class="text-center text-muted py-4">
                    No hay proveedores registrados
                </td></tr>`;
            actualizarPaginacionProveedores(pagina, 0, 0, 0);
            return;
        }

        data.proveedores.forEach(p => {
            const estadoBadge = p.estado === 1
                ? '<span class="badge bg-success">Activo</span>'
                : '<span class="badge bg-secondary">Suspendido</span>';

            const btnEstado = p.estado === 1
                ? `<a href="javascript:void(0)"
                      data-url="/proveedores/estado/${p.idProveedor}"
                      data-estado="${p.estado}"
                      class="btn btn-danger btn-sm"
                      onclick="confirmarEstadoProveedor(this.dataset.url, this.dataset.estado)">
                      <i class="bi bi-pause-circle"></i></a>`
                : `<a href="javascript:void(0)"
                      data-url="/proveedores/estado/${p.idProveedor}"
                      data-estado="${p.estado}"
                      class="btn btn-success btn-sm"
                      onclick="confirmarEstadoProveedor(this.dataset.url, this.dataset.estado)">
                      <i class="bi bi-play-circle"></i></a>`;

            const sunatBadge = p.estadoSunat === 'ACTIVO'
                ? `<span class="badge bg-success">${p.estadoSunat}</span>`
                : p.estadoSunat
                    ? `<span class="badge bg-danger">${p.estadoSunat}</span>`
                    : '-';

            const tipoBadge = p.tipo
                ? `<span class="badge bg-secondary">${p.tipo}</span>`
                : '-';

            tbody.innerHTML += `
            <tr class="${p.estado === 2 ? 'table-secondary' : ''}">
                <td>${p.idProveedor}</td>
                <td><span class="fw-semibold text-primary">${p.ruc || '-'}</span></td>
                <td>${p.nombre}</td>
                <td class="d-none d-xl-table-cell text-muted">${p.direccion || '-'}</td>
                <td>${tipoBadge}</td>
                <td>${sunatBadge}</td>
                <td>${estadoBadge}</td>
                <td>
                    <div class="acciones-btn">
                        <button class="btn btn-warning btn-sm"
                            data-bs-toggle="modal"
                            data-bs-target="#modalProveedor"
                            data-id="${p.idProveedor}"
                            data-ruc="${p.ruc || ''}"
                            data-nombre="${p.nombre}"
                            data-direccion="${p.direccion || ''}"
                            data-telefono="${p.telefono || ''}"
                            data-tipo="${p.tipo || ''}"
                            data-estadosunat="${p.estadoSunat || ''}"
                            data-estado="${p.estado}"
                            onclick="editarProveedor(this)">
                            <i class="bi bi-pencil"></i>
                        </button>	
                        ${btnEstado}
                    </div>
                </td>
            </tr>`;
        });

        actualizarPaginacionProveedores(pagina, data.totalPages,
            data.totalElements, data.proveedores.length);
        actualizarBotonesProveedores(pagina, data.totalPages, estado, buscar);
    });
}

function limpiarModalProveedor() {
    document.getElementById('tituloModalProveedor').innerHTML =
        '<i class="bi bi-truck me-2"></i>Nuevo Proveedor';
    ['idProveedor','inputRucProveedor','inputNombreProveedor',
     'inputDireccionProveedor','inputTelefono','inputTipoProveedor',
     'inputEstadoSunat','inputEstadoProveedor'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });
}

function editarProveedor(btn) {
    const d = btn.dataset;
    document.getElementById('tituloModalProveedor').innerHTML =
        '<i class="bi bi-pencil me-2"></i>Editar Proveedor #' + d.id;
    document.getElementById('idProveedor').value             = d.id;
    document.getElementById('inputRucProveedor').value       = d.ruc;
    document.getElementById('inputNombreProveedor').value    = d.nombre;
    document.getElementById('inputDireccionProveedor').value = d.direccion;
    document.getElementById('inputTelefono').value           = d.telefono;
    document.getElementById('inputTipoProveedor').value      = d.tipo;
    document.getElementById('inputEstadoSunat').value        = d.estadosunat;
    document.getElementById('inputEstadoProveedor').value    = d.estado;
}

function consultarRucProveedor() {
    const ruc = document.getElementById('inputRucProveedor').value.trim();

    if (!ruc || ruc.length !== 11) {
        Swal.fire({
            icon: 'warning',
            title: 'RUC inválido',
            text: 'El RUC debe tener exactamente 11 dígitos',
            timer: 2000,
            showConfirmButton: false
        });
        return;
    }

    const btn = document.getElementById('btnConsultarRuc');
    btn.innerHTML = '<i class="bi bi-hourglass-split me-1"></i>Consultando...';
    btn.disabled = true;

    fetch('/api/ruc/' + ruc)
    .then(res => res.json())
    .then(data => {
        btn.innerHTML = '<i class="bi bi-search me-1"></i>Consultar SUNAT';
        btn.disabled = false;

        if (data.error) {
            Swal.fire({
                icon: 'error',
                title: 'No encontrado',
                text: 'No se encontró información para ese RUC',
                timer: 2500,
                showConfirmButton: false
            });
            return;
        }

        const nombre = document.getElementById('inputNombreProveedor');
        const dir    = document.getElementById('inputDireccionProveedor');
        const tipo   = document.getElementById('inputTipoProveedor');
        const estado = document.getElementById('inputEstadoSunat');

        nombre.value = data.razonSocial || data.nombre || '';
        dir.value    = data.direccion   || '';
        tipo.value   = data.tipoContribuyente || '';
        estado.value = data.estado      || '';
        [nombre, dir, tipo, estado].forEach(el => {
            el.style.backgroundColor = '#d1fae5';
            setTimeout(() => el.style.backgroundColor = '', 1500);
        });

        Swal.fire({
            icon: 'success',
            title: 'Datos encontrados',
            text: nombre.value,
            timer: 2000,
            showConfirmButton: false
        });
    })
    .catch(() => {
        btn.innerHTML = '<i class="bi bi-search me-1"></i>Consultar SUNAT';
        btn.disabled = false;
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'No se pudo conectar con la API',
            timer: 2000,
            showConfirmButton: false
        });
    });
}

function guardarProveedor(event) {
    event.preventDefault();
    const formData = new FormData(document.getElementById('formProveedor'));

    fetch('/proveedores/guardar/ajax', {
        method: 'POST',
        body: new URLSearchParams(formData)
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            bootstrap.Modal.getInstance(
                document.getElementById('modalProveedor')).hide();
            Swal.fire({
                icon: 'success', title: '¡Guardado!',
                text: data.mensaje, timer: 2000,
                showConfirmButton: false
            }).then(() => recargarTablaProveedores());
        } else {
            Swal.fire({ icon: 'error', title: 'Error', text: data.mensaje });
        }
    });
}

function confirmarEstadoProveedor(url, estadoActual) {
    const esActivo = estadoActual == 1;
    const id = url.split('/').pop();

    Swal.fire({
        title: esActivo ? '¿Suspender proveedor?' : '¿Activar proveedor?',
        icon: esActivo ? 'warning' : 'question',
        showCancelButton: true,
        confirmButtonColor: esActivo ? '#dc3545' : '#198754',
        cancelButtonColor: '#6c757d',
        confirmButtonText: esActivo ? 'Sí, suspender' : 'Sí, activar',
        cancelButtonText: 'Cancelar'
    }).then(result => {
        if (result.isConfirmed) {
            fetch('/proveedores/estado/ajax/' + id, { method: 'POST' })
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    Swal.fire({
                        icon: 'success', title: 'Actualizado',
                        text: data.mensaje, timer: 1500,
                        showConfirmButton: false
                    }).then(() => recargarTablaProveedores());
                }
            });
        }
    });
}

function recargarTablaProveedores() {
    const urlParams    = new URLSearchParams(window.location.search);
    const paginaActual = parseInt(urlParams.get('page') || '0');
    irPaginaProveedores(paginaActual);
}

function irPaginaProveedores(pagina) {
    history.pushState({}, '', '/proveedores?page=' + pagina);
    filtrarProveedoresServidor('', pagina, '');
}

function guardarTrabajador(event) {
    event.preventDefault();

    const form = document.getElementById('formTrabajador');
    
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const formData = new FormData(form);
    const esNuevo = !document.getElementById('idTrabajador').value;

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
            }).then(() => {
                const estado = document.getElementById('filtroEstado').value;
                const buscar = document.getElementById('buscador').value;
                filtrarTrabajadoresServidor(estado, 0, buscar);
            });
        } else {
            Swal.fire({ icon: 'error', title: 'Error', text: data.mensaje });
        }
    })
    .catch(() => {
        Swal.fire({ icon: 'error', title: 'Error', text: 'Error de conexión' });
    });
}

function autocompletarRucProveedor(select) {
    const opcionSeleccionada = select.options[select.selectedIndex];
    const ruc = opcionSeleccionada?.dataset?.ruc || '';
    const inputFactura = document.getElementById('inputFactura');
    if (!inputFactura) return;

    const valorActual = inputFactura.value;
    const eraAutocompletado = /^\d{11}-/.test(valorActual);

    if (ruc && (!valorActual || eraAutocompletado)) {
        inputFactura.value = ruc;
        inputFactura.focus();
    } else if (!ruc) {
        if (eraAutocompletado) inputFactura.value = '';
    }
}


//CHAT IA 

function toggleChatIA() {
    const panel = document.getElementById('panelChatIA');
    panel.classList.toggle('activo');

    if (panel.classList.contains('activo')) {
        document.getElementById('chatInput').focus();
    }
}

function enviarSugerencia(texto) {
    document.getElementById('chatInput').value = texto;
    enviarMensajeChat();
}

function enviarMensajeChat() {
    const input = document.getElementById('chatInput');
    const pregunta = input.value.trim();

    if (!pregunta) return;

    const mensajes = document.getElementById('chatMensajes');
    const divUsuario = document.createElement('div');
    divUsuario.className = 'chat-mensaje chat-mensaje-usuario';
    divUsuario.textContent = pregunta;
    mensajes.appendChild(divUsuario);

    input.value = '';
    const sugerencias = document.getElementById('chatSugerencias');
    if (sugerencias) sugerencias.style.display = 'none';
    const divLoading = document.createElement('div');
    divLoading.className = 'chat-mensaje chat-mensaje-loading';
    divLoading.id = 'chatLoading';
    divLoading.innerHTML = `<span class="chat-loading-dots">
        <span>●</span><span>●</span><span>●</span></span>`;
    mensajes.appendChild(divLoading);

    mensajes.scrollTop = mensajes.scrollHeight;

    const btnEnviar = document.getElementById('btnEnviarChat');
    input.disabled = true;
    btnEnviar.disabled = true;

    fetch('/api/chat/preguntar', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pregunta: pregunta })
    })
    .then(res => res.json())
    .then(data => {
        document.getElementById('chatLoading')?.remove();

        const divBot = document.createElement('div');
        divBot.className = 'chat-mensaje chat-mensaje-bot';
        divBot.textContent = data.respuesta || data.error
            || 'No pude procesar tu solicitud.';
        mensajes.appendChild(divBot);

        mensajes.scrollTop = mensajes.scrollHeight;
        input.disabled = false;
        btnEnviar.disabled = false;
        input.focus();
    })
    .catch(() => {
        document.getElementById('chatLoading')?.remove();

        const divError = document.createElement('div');
        divError.className = 'chat-mensaje chat-mensaje-bot';
        divError.textContent =
            'Error de conexión. Intenta nuevamente.';
        mensajes.appendChild(divError);

        mensajes.scrollTop = mensajes.scrollHeight;
        input.disabled = false;
        btnEnviar.disabled = false;
    });
}

function consultarDniTrabajador() {
    const dni = document.getElementById('inputDni').value.trim();

    if (!dni || dni.length !== 8) {
        Swal.fire({
            icon: 'warning',
            title: 'DNI inválido',
            text: 'El DNI debe tener exactamente 8 dígitos',
            timer: 2000,
            showConfirmButton: false
        });
        return;
    }

    const btn = document.getElementById('btnConsultarDni');
    btn.innerHTML = '<i class="bi bi-hourglass-split me-1"></i>...';
    btn.disabled = true;

    fetch('/api/dni/' + dni)
    .then(res => res.json())
    .then(data => {
        btn.innerHTML = '<i class="bi bi-search me-1"></i>Consultar';
        btn.disabled = false;

        const nombreCompleto = [
            data.nombres          || '',
            data.apellidoPaterno  || '',
            data.apellidoMaterno  || ''
        ].filter(Boolean).join(' ').trim();

        const campoNombre = document.getElementById('inputNombre');

        if (data.error || !nombreCompleto) {
            campoNombre.value = '';
            campoNombre.focus();

            Swal.fire({
                icon: 'info',
                title: 'Persona no encontrada',
                text: 'No se encontraron datos para este DNI. ' +
                      'Por favor escribe el nombre manualmente.',
                confirmButtonColor: '#1a1a2e'
            });
            return;
        }

        campoNombre.value = nombreCompleto;
        campoNombre.style.backgroundColor = '#d1fae5';
        setTimeout(() => campoNombre.style.backgroundColor = '', 1500);

        Swal.fire({
            icon: 'success',
            title: 'DNI encontrado',
            text: nombreCompleto,
            timer: 2000,
            showConfirmButton: false
        });
    })
    .catch(() => {
        btn.innerHTML = '<i class="bi bi-search me-1"></i>Consultar';
        btn.disabled = false;
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'No se pudo conectar con la API',
            timer: 2000,
            showConfirmButton: false
        });
    });
}

function renderPaginacion(nav, paginaActual, totalPaginas, fnPagina) {
    if (!nav) return;

    const inicio = Math.max(0, paginaActual - 2);
    const fin    = Math.min(totalPaginas - 1, paginaActual + 2);

    let html = `
    <li class="page-item ${paginaActual === 0 ? 'disabled' : ''}">
        <a class="page-link-circular" href="javascript:void(0)"
           onclick="${paginaActual > 0 ? `${fnPagina}(0)` : ''}">«</a>
    </li>
    <li class="page-item ${paginaActual === 0 ? 'disabled' : ''}">
        <a class="page-link-circular" href="javascript:void(0)"
           onclick="${paginaActual > 0 ? `${fnPagina}(${paginaActual - 1})` : ''}">‹</a>
    </li>`;

    for (let i = inicio; i <= fin; i++) {
        html += `
    <li class="page-item">
        <a class="page-link-circular ${i === paginaActual ? 'active' : ''}"
           href="javascript:void(0)"
           onclick="${fnPagina}(${i})">${i + 1}</a>
    </li>`;
    }

    html += `
    <li class="page-item ${paginaActual === totalPaginas - 1 ? 'disabled' : ''}">
        <a class="page-link-circular" href="javascript:void(0)"
           onclick="${paginaActual < totalPaginas - 1 ? `${fnPagina}(${paginaActual + 1})` : ''}">›</a>
    </li>
    <li class="page-item ${paginaActual === totalPaginas - 1 ? 'disabled' : ''}">
        <a class="page-link-circular" href="javascript:void(0)"
           onclick="${paginaActual < totalPaginas - 1 ? `${fnPagina}(${totalPaginas - 1})` : ''}">»</a>
    </li>`;

    nav.innerHTML = html;
}

function actualizarPaginacion(paginaActual, totalPaginas, totalElements, mostrando) {
    const textoMostrando = document.getElementById('textoMostrando');
    if (textoMostrando)
        textoMostrando.innerHTML =
            `Mostrando <strong>${mostrando}</strong> de <strong>${totalElements}</strong> registros`;

    const textoPaginaFiltro = document.getElementById('textoPaginaFiltro');
    if (textoPaginaFiltro)
        textoPaginaFiltro.innerHTML =
            `Página <strong>${paginaActual + 1}</strong> de <strong>${totalPaginas}</strong> — Total: <strong>${totalElements}</strong> registros`;

    const textoPaginaBottom = document.getElementById('textoPaginaBottom');
    if (textoPaginaBottom)
        textoPaginaBottom.innerHTML =
            `Página <strong>${paginaActual + 1}</strong> de <strong>${totalPaginas}</strong>`;
}


// KARDEX

function cargarKardex(pagina) {
    const idProducto = document.getElementById('filtroProducto')?.value || '';
    const idSede = document.getElementById('filtroSede')?.value || '';
    const tipoMov = document.getElementById('filtroTipoMov')?.value || '';
    const texto = document.getElementById('filtroTexto')?.value || '';
    const page = pagina || 0;

    let url = `/kardex/lista/json?page=${page}&size=10&sortBy=fecha&sortDir=desc`;

    if (idProducto && idProducto.trim() !== '') {
        url += `&idProducto=${encodeURIComponent(idProducto.trim())}`;
    }
    if (idSede && idSede.trim() !== '') {
        url += `&idSede=${encodeURIComponent(idSede.trim())}`;
    }
    if (tipoMov && tipoMov.trim() !== '') {
        url += `&tipoMov=${encodeURIComponent(tipoMov.trim())}`;
    }
    if (texto && texto.trim() !== '') {
        url += `&texto=${encodeURIComponent(texto.trim())}`;
    }

    console.log('📡 URL:', url);

    fetch(url)
        .then(res => {
            if (!res.ok) {
                throw new Error(`HTTP error! status: ${res.status}`);
            }
            return res.json();
        })
        .then(data => {
            console.log('Datos recibidos:', data);
            renderizarTablaKardex(data);
            renderizarPaginacionKardex(data, page);
        })
        .catch(error => {
            console.error('Error:', error);
            const tbody = document.getElementById('tablaKardexBody');
            if (tbody) {
                tbody.innerHTML = `
                    <tr>
                        <td colspan="9" class="text-center text-danger py-4">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            Error al cargar los datos: ${error.message}
                        </td>
                    </tr>`;
            }
            const totalRegistros = document.getElementById('totalRegistros');
            if (totalRegistros) totalRegistros.textContent = '0 registros';
        });
}

function renderizarTablaKardex(data) {
    const content = data.content || [];
    const tbody = document.getElementById('tablaKardexBody');

    if (!tbody) return;

    if (content.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="9" class="text-center text-muted py-4">
                    <i class="fas fa-inbox fa-2x d-block mb-2"></i>
                    No hay movimientos registrados
                </td>
            </tr>`;
        const totalRegistros = document.getElementById('totalRegistros');
        if (totalRegistros) totalRegistros.textContent = '0 registros';
        return;
    }

    let html = '';
    content.forEach(item => {
        const fecha = new Date(item.fecha);
        const fechaStr = formatFechaKardex(fecha);

        let badgeTipo = '';
        if (item.tipoMov === 'E' || item.tipoMov === 'e') {
            badgeTipo = `<span class="badge-entrada"><i class="fas fa-arrow-down me-1"></i>E</span>`;
        } else {
            badgeTipo = `<span class="badge-salida"><i class="fas fa-arrow-up me-1"></i>S</span>`;
        }

        html += `
            <tr>
                <td>${fechaStr}</td>
                <td>
                    <span class="fw-semibold">${item.producto?.descripcion || 'N/A'}</span>
                    <div class="codigo-producto">${item.producto?.idProducto || ''}</div>
                </td>
                <td><span class="badge-sede">${item.sede?.nombre || 'N/A'}</span></td>
                <td>${badgeTipo}</td>
                <td class="text-end">${item.cantidad || 0}</td>
                <td class="text-end">S/ ${formatNumberKardex(item.costoUnit)}</td>
                <td class="text-end">S/ ${formatNumberKardex(item.total)}</td>
                <td class="text-end saldo-negrita">${item.saldoCant || 0}</td>
                <td><span class="badge bg-light text-dark">${item.referencia || '-'}</span></td>
            </tr>`;
    });

    tbody.innerHTML = html;
    
    const totalRegistros = document.getElementById('totalRegistros');
    if (totalRegistros) {
        totalRegistros.textContent = `${data.totalElements || 0} registros`;
    }
}

function renderizarPaginacionKardex(data, paginaActual) {
    const totalPaginas = data.totalPages || 0;
    const currentPage = data.number || 0;
    const totalElements = data.totalElements || 0;
    const size = data.size || 10;

    const desde = totalElements === 0 ? 0 : (currentPage * size) + 1;
    const hasta = Math.min((currentPage + 1) * size, totalElements);
    
    const desdeEl = document.getElementById('desde');
    const hastaEl = document.getElementById('hasta');
    const totalEl = document.getElementById('total');
    const paginaActualText = document.getElementById('paginaActualText');
    const totalPaginasText = document.getElementById('totalPaginasText');
    const textoMostrando = document.getElementById('textoMostrando');

    if (desdeEl) desdeEl.textContent = desde;
    if (hastaEl) hastaEl.textContent = hasta;
    if (totalEl) totalEl.textContent = totalElements;
    if (paginaActualText) paginaActualText.textContent = currentPage + 1;
    if (totalPaginasText) totalPaginasText.textContent = totalPaginas || 1;
    
    if (textoMostrando) {
        const mostrando = data.content?.length || 0;
        textoMostrando.innerHTML = 
            `Mostrando <strong>${mostrando}</strong> de <strong>${totalElements}</strong> registros`;
    }

    const nav = document.getElementById('paginacion');
    if (!nav) return;

    const inicio = Math.max(0, currentPage - 2);
    const fin = Math.min(totalPaginas - 1, currentPage + 2);

    let html = `
        <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
            <a class="page-link-circular" href="javascript:void(0)" 
               onclick="${currentPage > 0 ? `cargarKardex(0)` : ''}">
                <i class="fas fa-angle-double-left"></i>
            </a>
        </li>
        <li class="page-item ${currentPage === 0 ? 'disabled' : ''}">
            <a class="page-link-circular" href="javascript:void(0)" 
               onclick="${currentPage > 0 ? `cargarKardex(${currentPage - 1})` : ''}">
                <i class="fas fa-angle-left"></i>
            </a>
        </li>`;

    for (let i = inicio; i <= fin; i++) {
        html += `
            <li class="page-item">
                <a class="page-link-circular ${i === currentPage ? 'active' : ''}" 
                   href="javascript:void(0)" onclick="cargarKardex(${i})">
                    ${i + 1}
                </a>
            </li>`;
    }

    html += `
        <li class="page-item ${currentPage >= totalPaginas - 1 ? 'disabled' : ''}">
            <a class="page-link-circular" href="javascript:void(0)" 
               onclick="${currentPage < totalPaginas - 1 ? `cargarKardex(${currentPage + 1})` : ''}">
                <i class="fas fa-angle-right"></i>
            </a>
        </li>
        <li class="page-item ${currentPage >= totalPaginas - 1 ? 'disabled' : ''}">
            <a class="page-link-circular" href="javascript:void(0)" 
               onclick="${currentPage < totalPaginas - 1 ? `cargarKardex(${totalPaginas - 1})` : ''}">
                <i class="fas fa-angle-double-right"></i>
            </a>
        </li>`;

    nav.innerHTML = html;
}

function formatFechaKardex(fecha) {
    const dd = String(fecha.getDate()).padStart(2, '0');
    const mm = String(fecha.getMonth() + 1).padStart(2, '0');
    const yyyy = fecha.getFullYear();
    const hh = String(fecha.getHours()).padStart(2, '0');
    const min = String(fecha.getMinutes()).padStart(2, '0');
    return `${dd}/${mm}/${yyyy} ${hh}:${min}`;
}

function formatNumberKardex(num) {
    if (num === undefined || num === null) return '0.00';
    return Number(num).toFixed(2);
}





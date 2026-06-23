let stompClient = null;
let chatContactoActualId = null;
let chatGrupalCargado = false;
let lastDiaGrupal = null;
let lastDiaDirecto = null;

function conectarChat() {
    const socket = new SockJS('/ws-chat');
    stompClient = new StompJs.Client({
        webSocketFactory: () => socket,
        onConnect: () => {
            stompClient.subscribe('/topic/chat.grupal', (msg) => {
                pintarMensajeGrupal(JSON.parse(msg.body));
            });
            stompClient.subscribe('/user/queue/chat.directo', (msg) => {
                manejarMensajeDirectoEntrante(JSON.parse(msg.body));
            });
        }
    });
    stompClient.activate();
}

document.addEventListener('DOMContentLoaded', () => {
    conectarChat();
    actualizarContadorChat();
});

function toggleChatPanel() {
    const panel = document.getElementById('chat-panel');
    const visible = panel.style.display === 'block';
    panel.style.display = visible ? 'none' : 'block';
    if (!visible && !chatGrupalCargado) {
        cargarHistorialGrupal();
        chatGrupalCargado = true;
    }
}

function cambiarTabChat(tab) {
    const tabGrupal  = document.getElementById('tabGrupal');
    const tabDirecto = document.getElementById('tabDirecto');

    tabGrupal.style.color  = tab === 'grupal'  ? '#1a1a2e' : '#aaa';
    tabDirecto.style.color = tab === 'directo' ? '#1a1a2e' : '#aaa';
    tabGrupal.style.borderBottomColor  = tab === 'grupal'  ? '#f0a500' : 'transparent';
    tabDirecto.style.borderBottomColor = tab === 'directo' ? '#f0a500' : 'transparent';

    document.getElementById('chatVistaGrupal').style.display    = tab === 'grupal'  ? 'block' : 'none';
    document.getElementById('chatVistaContactos').style.display = tab === 'directo' ? 'block' : 'none';
    document.getElementById('chatVistaDirecto').style.display   = 'none';

    if (tab === 'directo') cargarContactos();
}

function cargarHistorialGrupal() {
    fetch('/api/chat/grupal')
    .then(res => res.json())
    .then(lista => {
        document.getElementById('chat-mensajes-grupal').innerHTML = '';
        lastDiaGrupal = null;
        lista.forEach(pintarMensajeGrupal);
    });
}

function pintarMensajeGrupal(m) {
    const cont = document.getElementById('chat-mensajes-grupal');

    const claveDia = claveDiaChat(m.fecha);
    if (lastDiaGrupal !== claveDia) {
        lastDiaGrupal = claveDia;
        const sep = document.createElement('div');
        sep.style.cssText = 'text-align:center; margin:12px 0;';
        sep.innerHTML = `<span style="background:#f0f0f0; color:#888; font-size:0.7rem;
                                padding:3px 10px; border-radius:10px;">
                                ${formatDiaSeparadorChat(m.fecha)}</span>`;
        cont.appendChild(sep);
    }

    const esMio = m.idEmisor === window.idUsuarioActual;
    const div = document.createElement('div');
    div.style.marginBottom = '10px';
    div.style.textAlign = esMio ? 'right' : 'left';
    div.innerHTML = `
        <div style="font-size:0.7rem; color:#999;">${esMio ? 'Tú' : m.nombreEmisor}</div>
        <div style="display:inline-block; background:${esMio ? '#f0a500' : '#eee'};
                    color:${esMio ? '#fff' : '#333'}; padding:6px 10px;
                    border-radius:10px; max-width:80%; font-size:0.85rem;">
            ${m.contenido}
        </div>
        <div style="font-size:0.65rem; color:#bbb; margin-top:2px;">${formatHoraChat(m.fecha)}</div>`;
    cont.appendChild(div);
    cont.scrollTop = cont.scrollHeight;
}

function enviarMensajeGrupal() {
    const input = document.getElementById('chatInputGrupal');
    const texto = input.value.trim();
    if (!texto || !stompClient?.connected) return;
    stompClient.publish({
        destination: '/app/chat.grupal',
        body: JSON.stringify({ contenido: texto })
    });
    input.value = '';
}

function cargarContactos() {
    fetch('/api/chat/contactos')
    .then(res => res.json())
    .then(lista => {
        const cont = document.getElementById('chatVistaContactos');
        cont.innerHTML = '';
        lista.forEach(c => {
            const div = document.createElement('div');
            div.style.cssText = 'padding:10px 14px; border-bottom:1px solid #f0f0f0; cursor:pointer; display:flex; justify-content:space-between; align-items:center;';
            div.onclick = () => abrirConversacion(c.idUsuario, c.nombre);
            div.innerHTML = `
                <div>
                    <div style="font-weight:600; font-size:0.85rem;">${c.nombre}</div>
                    <div style="font-size:0.72rem; color:#999;">${c.rol}${c.sede ? ' · ' + c.sede : ''}</div>
                </div>
                ${c.noLeidos > 0 ? `<span class="badge bg-danger rounded-pill">${c.noLeidos}</span>` : ''}
            `;
            cont.appendChild(div);
        });
    });
}

function abrirConversacion(idUsuario, nombre) {
    chatContactoActualId = idUsuario;
    document.getElementById('chatDirectoNombre').textContent = nombre;
    document.getElementById('chatVistaContactos').style.display = 'none';
    document.getElementById('chatVistaDirecto').style.display = 'block';

    fetch('/api/chat/directo/' + idUsuario)
    .then(res => res.json())
    .then(lista => {
        const cont = document.getElementById('chat-mensajes-directo');
        cont.innerHTML = '';
        lastDiaDirecto = null;
        lista.forEach(pintarMensajeDirecto);
        actualizarContadorChat();
    });
}

function volverAContactos() {
    chatContactoActualId = null;
    document.getElementById('chatVistaDirecto').style.display = 'none';
    document.getElementById('chatVistaContactos').style.display = 'block';
    cargarContactos();
}

function pintarMensajeDirecto(m) {
    const cont = document.getElementById('chat-mensajes-directo');

    const claveDia = claveDiaChat(m.fecha);
    if (lastDiaDirecto !== claveDia) {
        lastDiaDirecto = claveDia;
        const sep = document.createElement('div');
        sep.style.cssText = 'text-align:center; margin:12px 0;';
        sep.innerHTML = `<span style="background:#f0f0f0; color:#888; font-size:0.7rem;
                                padding:3px 10px; border-radius:10px;">
                                ${formatDiaSeparadorChat(m.fecha)}</span>`;
        cont.appendChild(sep);
    }

    const esMio = m.idEmisor === window.idUsuarioActual;
    const div = document.createElement('div');
    div.style.marginBottom = '10px';
    div.style.textAlign = esMio ? 'right' : 'left';
    div.innerHTML = `
        <div style="display:inline-block; background:${esMio ? '#f0a500' : '#eee'};
                    color:${esMio ? '#fff' : '#333'}; padding:6px 10px;
                    border-radius:10px; max-width:80%; font-size:0.85rem;">
            ${m.contenido}
        </div>
        <div style="font-size:0.65rem; color:#bbb; margin-top:2px;">${formatHoraChat(m.fecha)}</div>`;
    cont.appendChild(div);
    cont.scrollTop = cont.scrollHeight;
}

function enviarMensajeDirecto() {
    const input = document.getElementById('chatInputDirecto');
    const texto = input.value.trim();
    if (!texto || !chatContactoActualId || !stompClient?.connected) return;
    stompClient.publish({
        destination: '/app/chat.directo',
        body: JSON.stringify({ contenido: texto, idDestinatario: String(chatContactoActualId) })
    });
    input.value = '';
}

function manejarMensajeDirectoEntrante(m) {
    const otroId = m.idEmisor === window.idUsuarioActual ? m.idDestinatario : m.idEmisor;
    if (chatContactoActualId === otroId) {
        pintarMensajeDirecto(m);
    } else if (m.idEmisor !== window.idUsuarioActual) {
        actualizarContadorChat();
    }
}

function actualizarContadorChat() {
    fetch('/api/chat/contactos')
    .then(res => res.json())
    .then(lista => {
        const total = lista.reduce((acc, c) => acc + (c.noLeidos || 0), 0);
        const badge = document.getElementById('chat-badge');
        document.getElementById('chat-count').textContent = total;
        badge.style.display = total > 0 ? 'block' : 'none';
    });
}

function formatHoraChat(fechaStr) {
    const f = new Date(fechaStr);
    return f.toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' });
}

function formatDiaSeparadorChat(fechaStr) {
    const f = new Date(fechaStr);
    const hoy = new Date();
    const ayer = new Date();
    ayer.setDate(hoy.getDate() - 1);

    const mismoDia = (a, b) =>
        a.getFullYear() === b.getFullYear() &&
        a.getMonth() === b.getMonth() &&
        a.getDate() === b.getDate();

    if (mismoDia(f, hoy)) return 'Hoy';
    if (mismoDia(f, ayer)) return 'Ayer';

    let texto = f.toLocaleDateString('es-PE', { weekday: 'long', day: 'numeric', month: 'long' });
    return texto.charAt(0).toUpperCase() + texto.slice(1);
}

function claveDiaChat(fechaStr) {
    const f = new Date(fechaStr);
    return `${f.getFullYear()}-${f.getMonth()}-${f.getDate()}`;
}
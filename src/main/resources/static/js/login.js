function togglePassword() {
    const input = document.getElementById('inputPassword');
    const icon  = document.getElementById('iconTogglePassword');
    const seVaAMostrar = input.type === 'password';

    input.type = seVaAMostrar ? 'text' : 'password';
    icon.classList.toggle('bi-eye-slash', !seVaAMostrar);
    icon.classList.toggle('bi-eye', seVaAMostrar);
    icon.setAttribute('aria-label',
        seVaAMostrar ? 'Ocultar contraseña' : 'Mostrar contraseña');
}
package com.seguridad.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@Component
public class AccesoSedeHelper {

    public CustomUserDetails usuarioActual(Authentication auth) {
        return (CustomUserDetails) auth.getPrincipal();
    }

    public String rolActual(Authentication auth) {
        return usuarioActual(auth).getUsuario().getRol();
    }

    public boolean esAdmin(Authentication auth) {
        return "ADMIN".equals(rolActual(auth));
    }

    public Integer idSedeRestriccion(Authentication auth) {
        if (esAdmin(auth)) {
            return null;
        }
        var usuario = usuarioActual(auth).getUsuario();
        return usuario.getSede() != null ? usuario.getSede().getIdSede() : null;
    }
}
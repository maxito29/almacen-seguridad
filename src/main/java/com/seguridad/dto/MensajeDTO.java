package com.seguridad.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MensajeDTO {
    private Long idMensaje;
    private Integer idEmisor;
    private String nombreEmisor;
    private Integer idDestinatario;
    private String contenido;
    private LocalDateTime fecha;
}
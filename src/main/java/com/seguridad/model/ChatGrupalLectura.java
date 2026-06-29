package com.seguridad.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tb_chat_grupal_lectura")
public class ChatGrupalLectura {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "ultima_lectura")
    private LocalDateTime ultimaLectura;
}
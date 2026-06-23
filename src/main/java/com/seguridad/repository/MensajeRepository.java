package com.seguridad.repository;

import com.seguridad.model.Mensaje;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    @Query("SELECT m FROM Mensaje m WHERE m.destinatario IS NULL ORDER BY m.fecha DESC")
    List<Mensaje> findUltimosGrupales(Pageable pageable);

    @Query("SELECT m FROM Mensaje m WHERE " +
           "(m.emisor.idUsuario = :u1 AND m.destinatario.idUsuario = :u2) OR " +
           "(m.emisor.idUsuario = :u2 AND m.destinatario.idUsuario = :u1) " +
           "ORDER BY m.fecha ASC")
    List<Mensaje> findConversacion(@Param("u1") Integer u1, @Param("u2") Integer u2);

    @Query("SELECT COUNT(m) FROM Mensaje m WHERE m.destinatario.idUsuario = :idDestinatario " +
           "AND m.emisor.idUsuario = :idEmisor AND m.leido = false")
    long contarNoLeidosDe(@Param("idDestinatario") Integer idDestinatario,
                          @Param("idEmisor") Integer idEmisor);

    @Modifying
    @Query("UPDATE Mensaje m SET m.leido = true WHERE m.destinatario.idUsuario = :idDestinatario " +
           "AND m.emisor.idUsuario = :idEmisor AND m.leido = false")
    void marcarConversacionLeida(@Param("idDestinatario") Integer idDestinatario,
                                  @Param("idEmisor") Integer idEmisor);
}
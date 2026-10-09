package com.devdesk.Gestion_Incidencias.core.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BitacoraRepository extends JpaRepository<Bitacora, Long> {
    List<Bitacora> findByTicketIdOrderByFechaCreacionDesc(Long ticketId);
}
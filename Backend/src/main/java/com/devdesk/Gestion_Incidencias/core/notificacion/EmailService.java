package com.devdesk.Gestion_Incidencias.core.notificacion;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    /**
     * Envía un correo electrónico en texto plano de forma asíncrona.
     * Al usar @Async, el hilo principal no se bloquea esperando al servidor SMTP.
     */
    @Async
    public void enviarCorreo(String destinatario, String asunto, String mensaje) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom(remitente);
            email.setTo(destinatario);
            email.setSubject(asunto);
            email.setText(mensaje);

            mailSender.send(email);
            log.info("Correo enviado exitosamente a: {}", destinatario);
            
        } catch (MailException e) {
            log.error("Error al enviar el correo a {}: {}", destinatario, e.getMessage());
            // No lanzamos la excepción para no interrumpir el flujo de negocio (ej. crear un ticket)
            // si el servidor de correo llega a fallar temporalmente.
        }
    }
}
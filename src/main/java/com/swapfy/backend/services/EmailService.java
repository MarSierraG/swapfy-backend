package com.swapfy.backend.services;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetCode(String to, String code) {
        log.info("[EmailService] Preparando email para: {} con código: {}", to, code);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Código de recuperación - Swapfy");
            message.setText("Hola!\n\nTu código de recuperación es: " + code +
                    "\n\nSi no solicitaste este código, puedes ignorar este mensaje.");
            // opcional pero recomendable:
            // message.setFrom("swapfy.noreply@gmail.com");

            log.info("[EmailService] Enviando email a {}", to);
            mailSender.send(message);
            S log.info("[EmailService] Email enviado correctamente a {}", to);
        } catch (Exception e) {
            log.error("[EmailService] ERROR enviando email a {}: {}", to, e.getMessage(), e);
            e.printStackTrace();
            // re-lanzamos para que AuthService pueda decidir qué hacer
            throw new RuntimeException("Error enviando email", e);
        }
    }
}

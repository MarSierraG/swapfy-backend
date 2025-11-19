package com.swapfy.backend.services;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetCode(String to, String code) {
        System.out.println("[EmailService] Preparando email para: " + to + " con código: " + code);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Código de recuperación - Swapfy");
            message.setText("Hola!\n\nTu código de recuperación es: " + code +
                    "\n\nSi no solicitaste este código, puedes ignorar este mensaje.");
            // opcional pero recomendable:
            // message.setFrom("swapfy.noreply@gmail.com");

            System.out.println("[EmailService] Enviando email...");
            mailSender.send(message);
            System.out.println("[EmailService] Email enviado correctamente a: " + to);
        } catch (Exception e) {
            System.out.println("[EmailService] ERROR enviando email a " + to + ": " + e.getMessage());
            e.printStackTrace();
            // re-lanzamos para que AuthService pueda decidir qué hacer
            throw new RuntimeException("Error enviando email", e);
        }
    }
}

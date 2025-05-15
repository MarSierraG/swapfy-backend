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
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(" Código de recuperación - Swapfy");
        message.setText("Hola!\n\nTu código de recuperación es: " + code + "\n\nSi no solicitaste este código, puedes ignorar este mensaje.");
        mailSender.send(message);
    }
}

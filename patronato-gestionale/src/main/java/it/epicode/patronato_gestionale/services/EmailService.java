package it.epicode.patronato_gestionale.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            System.out.println("Email inviata con successo a: " + to);
        } catch (Exception e) {
            System.err.println("Errore durante l'invio dell'email: " + e.getMessage());
            throw new RuntimeException("Errore nell'invio della mail", e);
        }
    }
}
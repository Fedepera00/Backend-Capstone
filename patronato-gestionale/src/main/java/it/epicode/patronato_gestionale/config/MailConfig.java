package it.epicode.patronato_gestionale.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Debug per verificare le variabili d'ambiente
        System.out.println("MAIL_USERNAME: " + System.getenv("MAIL_USERNAME"));
        System.out.println("MAIL_PASSWORD: " + System.getenv("MAIL_PASSWORD"));

        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(System.getenv("MAIL_USERNAME")); // Usa la variabile d'ambiente
        mailSender.setPassword(System.getenv("MAIL_PASSWORD")); // Usa la variabile d'ambiente

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.debug", "true");

        // Debug per verificare le proprietà configurate
        System.out.println("JavaMailSender configurato con successo.");
        return mailSender;
    }
}
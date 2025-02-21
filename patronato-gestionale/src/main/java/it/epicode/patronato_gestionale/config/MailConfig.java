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

        // RIMOSSI i System.out.println() con credenziali
        // Se serve il debug in locale, tienili commentati o loggali con un livello appropriato
        // System.out.println("MAIL_USERNAME: + System.getenv("MAIL_USERNAME"));
        // System.out.println("MAIL_PASSWORD: + System.getenv("MAIL_PASSWORD"));

        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(System.getenv("MAIL_USERNAME"));
        mailSender.setPassword(System.getenv("MAIL_PASSWORD"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // Disabilita il debug di JavaMail
        props.put("mail.debug", "false");

        // System.out.println("JavaMailSender configurato con successo."); // Se vuoi, rimuovi o commenta

        return mailSender;
    }
}
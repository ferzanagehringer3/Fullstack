package ch.fullstack.dalzana.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailService(JavaMailSender mailSender,
                        @Value("${spring.mail.username}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void sendNotification(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            if (fromAddress != null && !fromAddress.isBlank()) {
                message.setFrom(fromAddress);
            }
            
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't fail the entire operation
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
        }
    }

    public void sendTeamMessageNotification(String recipientEmail, String recipientName, 
                                           String teamName, String senderName, String messageContent) {
        String subject = "Neue Nachricht in Team: " + teamName;
        String body = String.format(
            "Hallo %s,\n\n" +
            "%s hat eine neue Nachricht im Team '%s' gesendet:\n\n" +
            "\"%s\"\n\n" +
            "Öffne die DalZana App um zu antworten.\n\n" +
            "Viele Grüsse,\n" +
            "Dein DalZana Team",
            recipientName, senderName, teamName, messageContent
        );
        
        sendNotification(recipientEmail, subject, body);
    }

    public void sendHtmlNotification(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            if (fromAddress != null && !fromAddress.isBlank()) {
                helper.setFrom(fromAddress);
            }
            
            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Failed to send HTML email to " + to + ": " + e.getMessage());
        }
    }
}

package ch.fullstack.dalzana.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import java.io.IOException;

@Service
public class EmailService {

    private final String sendGridApiKey;
    private final String fromAddress;
    private final boolean sendGridEnabled;

    public EmailService(@Value("${sendgrid.api.key:}") String sendGridApiKey,
                        @Value("${spring.mail.username}") String fromAddress) {
        this.sendGridApiKey = sendGridApiKey;
        this.fromAddress = fromAddress;
        this.sendGridEnabled = sendGridApiKey != null && !sendGridApiKey.isBlank();
        
        if (!sendGridEnabled) {
            System.out.println("WARNING: SendGrid API Key nicht konfiguriert. E-Mails werden nicht versendet.");
        }
    }

    public void sendNotification(String to, String subject, String text) {
        if (!sendGridEnabled) {
            System.out.println("E-Mail wird nicht versendet (SendGrid nicht konfiguriert): " + to + " - " + subject);
            return;
        }
        
        try {
            Email from = new Email(fromAddress);
            Email toEmail = new Email(to);
            Content content = new Content("text/plain", text);
            Mail mail = new Mail(from, subject, toEmail, content);

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                System.out.println("E-Mail erfolgreich versendet an: " + to);
            } else {
                System.err.println("SendGrid Fehler (" + response.getStatusCode() + "): " + response.getBody());
            }
        } catch (IOException e) {
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
        if (!sendGridEnabled) {
            System.out.println("HTML E-Mail wird nicht versendet (SendGrid nicht konfiguriert): " + to + " - " + subject);
            return;
        }
        
        try {
            Email from = new Email(fromAddress);
            Email toEmail = new Email(to);
            Content content = new Content("text/html", htmlBody);
            Mail mail = new Mail(from, subject, toEmail, content);

            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            
            Response response = sg.api(request);
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                System.out.println("HTML E-Mail erfolgreich versendet an: " + to);
            } else {
                System.err.println("SendGrid HTML Fehler (" + response.getStatusCode() + "): " + response.getBody());
            }
        } catch (IOException e) {
            System.err.println("Failed to send HTML email to " + to + ": " + e.getMessage());
        }
    }
}

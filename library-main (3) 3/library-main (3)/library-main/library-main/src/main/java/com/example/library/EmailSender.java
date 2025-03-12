package com.example.library;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {
    public static void main(String[] args) {
        // Recipient's email ID
        String recipient = "parth.belsare01@gmail.com";

        // Sender's email ID
        String sender = "parth.22110808@viit.ac.in";

        // SMTP server information (Gmail's SMTP server)
        String host = "smtp.gmail.com";

        // SMTP port (Gmail uses port 587 for TLS)
        final String username = "parth.22110808@viit.ac.in";  // Gmail username
        final String password = "";         // Gmail password or App Password if 2FA is enabled

        // Set properties for the mail session
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); // Enable TLS encryption
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587"); // Port for TLS

        // Create session with authentication
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Create MimeMessage object
            MimeMessage message = new MimeMessage(session);

            // Set sender
            message.setFrom(new InternetAddress(sender));

            // Set recipient
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));

            // Set subject and body
            message.setSubject("Test Email");
            message.setText("Hello! This is a test email sent using Java.");

            // Send the email
            Transport.send(message);
            System.out.println("Email successfully sent!");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}

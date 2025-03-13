package com.example.library;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

@Service
class EmailReminderService {

    private final MongoCollection<Document> collection;
    private final MongoClient mongoClient;
    private final String senderEmail;
    private final String senderPassword;

    public EmailReminderService() {
        try {
            // Try to find .env file in project root directory first
            File envFile = new File(".env");
            String envPath = envFile.exists() ? "." :
                    "C:\\Users\\dks\\IdeaProjects\\digitalLibraryProject\\library-main (3) 3\\library-main (3)\\library-main\\library-main";

            System.out.println("Looking for .env file in: " + envPath);

            // Load environment variables from .env
            Dotenv dotenv = Dotenv.configure()
                    .directory(envPath)
                    .filename(".env")
                    .load();

            this.senderEmail = dotenv.get("LIBRARY_EMAIL");
            this.senderPassword = dotenv.get("LIBRARY_PASSWORD");

            System.out.println("Email credentials loaded: " +
                    (senderEmail != null ? senderEmail : "NULL") +
                    ", Password loaded: " + (senderPassword != null && !senderPassword.isEmpty()));

            // MongoDB connection setup
            String mongoUri = dotenv.get("MONGO_URI", "mongodb://localhost:27017");
            System.out.println("Connecting to MongoDB: " + mongoUri);

            this.mongoClient = MongoClients.create(mongoUri);
            MongoDatabase database = mongoClient.getDatabase("digitallibrary");
            this.collection = database.getCollection("borrowed_books");

            // Check if collection exists and has documents
            long docCount = collection.countDocuments();
            System.out.println("Connected to MongoDB. Collection 'borrowed_books' contains " + docCount + " documents.");

        } catch (Exception e) {
            System.err.println("Error during initialization:");
            e.printStackTrace();
            throw e; // Rethrow to prevent service from starting with invalid configuration
        }
    }

    // Runs every day at 9 AM
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendReminders() {
        try {
            LocalDate today = LocalDate.now();
            System.out.println("Starting email reminder check for: " + today);

            // Test SMTP connection first
            testSmtpConnection();

            // Email properties for Gmail
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.timeout", "5000");
            properties.put("mail.smtp.connectiontimeout", "5000");
            properties.put("mail.debug", "true"); // Enable debug mode

            // Create email session
            Session session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            AtomicInteger totalBooksFound = new AtomicInteger(0);

            // Check for books due in 1, 2, and 3 days
            for (int daysBefore = 1; daysBefore <= 3; daysBefore++) {
                LocalDate reminderDate = today.plusDays(daysBefore);

                // Get timestamp range for the entire day
                long startOfDayTimestamp = reminderDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long endOfDayTimestamp = reminderDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

                System.out.println("Checking due books for: " + reminderDate +
                        " (timestamp range: " + startOfDayTimestamp + " to " + endOfDayTimestamp + ")");

                // Query MongoDB using date comparison
                FindIterable<Document> documents = collection.find(
                        Filters.and(
                                Filters.gte("returnDate", new java.util.Date(startOfDayTimestamp)),
                                Filters.lt("returnDate", new java.util.Date(endOfDayTimestamp))
                        )
                );

                int booksForThisDate = 0;

                for (Document doc : documents) {
                    booksForThisDate++;
                    totalBooksFound.incrementAndGet();

                    System.out.println("Found document: " + doc.toJson());

                    String recipientEmail = doc.getString("username");
                    String bookTitle = doc.getString("title");
                    java.util.Date returnDate = doc.getDate("returnDate");

                    if (recipientEmail != null && bookTitle != null && returnDate != null) {
                        if (!recipientEmail.contains("@")) {
                            System.out.println("Warning: username field '" + recipientEmail + "' doesn't look like an email address");
                            continue;
                        }

                        LocalDate dueDate = returnDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        sendEmail(session, recipientEmail, bookTitle, dueDate);
                    } else {
                        System.out.println("Skipping invalid document: " + doc.toJson());
                    }
                }

                System.out.println("Found " + booksForThisDate + " books due on " + reminderDate);
            }

            System.out.println("Email reminder process completed. Total books found: " + totalBooksFound.get());

        } catch (Exception e) {
            System.err.println("Error during email reminder process:");
            e.printStackTrace();
        }
    }

    private void testSmtpConnection() {
        try {
            System.out.println("Testing SMTP connection...");

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.timeout", "5000");
            props.put("mail.smtp.connectiontimeout", "5000");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            Transport transport = session.getTransport("smtp");
            transport.connect("smtp.gmail.com", senderEmail, senderPassword);
            transport.close();

            System.out.println("SMTP connection test successful");
        } catch (Exception e) {
            System.err.println("SMTP connection test failed:");
            e.printStackTrace();
        }
    }

    private void sendEmail(Session session, String recipientEmail, String bookTitle, LocalDate dueDate) {
        try {
            System.out.println("Attempting to send email to: " + recipientEmail);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            message.setSubject("Library Return Reminder - " + bookTitle);

            String emailBody = String.format(
                    "Dear user,\n\nYour book '%s' is due for return on %s.\n" +
                            "Please return it on time to avoid late fees.\n\nThank you!\nLibrary Team",
                    bookTitle, dueDate);

            message.setText(emailBody);

            Transport.send(message);
            System.out.println("✓ Reminder successfully sent to: " + recipientEmail);

        } catch (MessagingException e) {
            System.err.println("Failed to send email to: " + recipientEmail);
            e.printStackTrace();
        }
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("MongoDB connection closed.");
        }
    }

    public void triggerReminder() {
        System.out.println("Manual trigger of email reminders");
        sendReminders(); // Call the scheduled method manually
    }
}

public class TestEmailReminder {
    public static void main(String[] args) {
        try {
            System.out.println("Starting email reminder test...");
            EmailReminderService service = new EmailReminderService();
            service.triggerReminder(); // Trigger manually
            service.close(); // Clean up MongoDB connection
            System.out.println("Email reminder test completed.");
        } catch (Exception e) {
            System.err.println("Email reminder test failed with exception:");
            e.printStackTrace();
        }
    }
}
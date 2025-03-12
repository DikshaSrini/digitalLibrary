//package com.example.library;
//
//import com.mongodb.client.*;
//import com.mongodb.client.model.Filters;
//import io.github.cdimascio.dotenv.Dotenv;
//import org.bson.Document;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import javax.mail.*;
//import javax.mail.internet.*;
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.util.Properties;
//
//@Service
//public class EmailReminderService {
//
//    private final MongoCollection<Document> collection;
//    private final MongoClient mongoClient;
//    private final String senderEmail;
//    private final String senderPassword;
//
//    public EmailReminderService() {
//        // Load environment variables from .env
//        Dotenv dotenv = Dotenv.configure()
//                .directory("C:\\Users\\dks\\Downloads\\library-main (3)\\library-main\\library-main")
//                .filename(".env")
//                .load();
//
//        this.senderEmail = dotenv.get("LIBRARY_EMAIL");
//        this.senderPassword = dotenv.get("LIBRARY_PASSWORD");
//
//        // MongoDB connection setup
//        String mongoUri = dotenv.get("MONGO_URI", "mongodb://localhost:27017");
//        this.mongoClient = MongoClients.create(mongoUri);
//        MongoDatabase database = mongoClient.getDatabase("digitallibrary");
//        this.collection = database.getCollection("borrowed_books");
//    }
//
//    // Runs every day at 9 AM
//    @Scheduled(cron = "0 0 9 * * ?")
//    public void sendReminders() {
//        LocalDate today = LocalDate.now();
//        System.out.println("Starting email reminder check for: " + today);
//
//        // Email properties for Gmail
//        Properties properties = new Properties();
//        properties.put("mail.smtp.auth", "true");
//        properties.put("mail.smtp.starttls.enable", "true");
//        properties.put("mail.smtp.host", "smtp.gmail.com");
//        properties.put("mail.smtp.port", "587");
//
//        // Create email session
//        Session session = Session.getInstance(properties, new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(senderEmail, senderPassword);
//            }
//        });
//
//        // Check for books due in 1, 2, and 3 days
//        for (int daysBefore = 1; daysBefore <= 3; daysBefore++) {
//            LocalDate reminderDate = today.plusDays(daysBefore);
//
//            // Get timestamp range for the entire day (UTC)
//            long startOfDayTimestamp = reminderDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli();
//            long endOfDayTimestamp = startOfDayTimestamp + 86399999; // 23:59:59.999
//
//            System.out.println("Checking due books for: " + reminderDate);
//
//            // Query MongoDB using timestamp range
//            FindIterable<Document> documents = collection.find(
//                    Filters.and(
//                            Filters.gte("returnDate", startOfDayTimestamp),
//                            Filters.lte("returnDate", endOfDayTimestamp)
//                    )
//            );
//
//            boolean foundBooks = false;
//
//            for (Document doc : documents) {
//                foundBooks = true;
//                String recipientEmail = doc.getString("username");
//                String bookTitle = doc.getString("title");
//
//                if (recipientEmail != null && bookTitle != null) {
//                    sendEmail(session, recipientEmail, bookTitle, reminderDate);
//                } else {
//                    System.out.println("Skipping invalid document: " + doc.toJson());
//                }
//            }
//
//            if (!foundBooks) {
//                System.out.println("No books found for: " + reminderDate);
//            }
//        }
//    }
//
//    private void sendEmail(Session session, String recipientEmail, String bookTitle, LocalDate dueDate) {
//        try {
//            MimeMessage message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(senderEmail));
//            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
//            message.setSubject("Library Return Reminder - " + bookTitle);
//
//            String emailBody = String.format(
//                    "Dear user,\n\nYour book '%s' is due for return on %s.\n" +
//                            "Please return it on time to avoid late fees.\n\nThank you!\nLibrary Team",
//                    bookTitle, dueDate);
//
//            message.setText(emailBody);
//
//            Transport.send(message);
//            System.out.println("Reminder sent to: " + recipientEmail);
//
//        } catch (MessagingException e) {
//            System.err.println("Failed to send email to: " + recipientEmail);
//            e.printStackTrace();
//        }
//    }
//
//    public void close() {
//        mongoClient.close();
//        System.out.println("MongoDB connection closed.");
//    }
//}
//
///*package com.example.library;
//
//import com.mongodb.client.*;
//import com.mongodb.client.model.Filters;
//import io.github.cdimascio.dotenv.Dotenv;
//import org.bson.Document;
//import org.springframework.stereotype.Service;
//
//import javax.mail.*;
//import javax.mail.internet.*;
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.util.Properties;
//
//@Service
//public class EmailReminderService {
//
//    private final MongoCollection<Document> collection;
//    private final MongoClient mongoClient;
//    private final String senderEmail;
//    private final String senderPassword;
//
//    public EmailReminderService() {
//        // Load environment variables from .env
//        Dotenv dotenv = Dotenv.configure()
//                .directory("C:\\Users\\pabl\\Downloads\\library-main (3) (1)\\library-main (3)\\library-main\\library-main")
//                .filename(".env")
//                .load();
//        this.senderEmail = dotenv.get("LIBRARY_EMAIL");
//        this.senderPassword = dotenv.get("LIBRARY_PASSWORD");
//
//        // MongoDB connection setup
//        String mongoUri = dotenv.get("MONGO_URI", "mongodb://localhost:27017");
//        this.mongoClient = MongoClients.create(mongoUri);
//        MongoDatabase database = mongoClient.getDatabase("digitallibrary");
//        this.collection = database.getCollection("borrowed_books");
//    }
//
//    public void sendReminders() {
//        LocalDate today = LocalDate.now();
//        System.out.println("Starting email reminder check for: " + today);
//
//        // Email properties for Gmail
//        Properties properties = new Properties();
//        properties.put("mail.smtp.auth", "true");
//        properties.put("mail.smtp.starttls.enable", "true");
//        properties.put("mail.smtp.host", "smtp.gmail.com");
//        properties.put("mail.smtp.port", "587");
//
//        // Create email session
//        Session session = Session.getInstance(properties, new Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(senderEmail, senderPassword);
//            }
//        });
//
//        // Check for books due in 1, 2, and 3 days
//        for (int daysBefore = 1; daysBefore <= 3; daysBefore++) {
//            LocalDate reminderDate = today.plusDays(daysBefore);
//
//            // Get timestamp range for the entire day (UTC)
//            long startOfDayTimestamp = reminderDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli();
//            long endOfDayTimestamp = startOfDayTimestamp + 86399999; // 23:59:59.999
//
//            System.out.println("Checking due books for: " + reminderDate);
//            System.out.println("Timestamp range (UTC): " + startOfDayTimestamp + " to " + endOfDayTimestamp);
//            // Query MongoDB using timestamp range
//            FindIterable<Document> documents = collection.find(
//                    Filters.and(
//                            Filters.gte("returnDate", startOfDayTimestamp),
//                            Filters.lte("returnDate", endOfDayTimestamp) // Include exact match
//                    )
//            );
//
//            boolean foundBooks = false;
//
//            for (Document doc : documents) {
//                foundBooks = true;
//                String recipientEmail = doc.getString("username");
//                String bookTitle = doc.getString("title");
//
//                System.out.println("Book found: " + bookTitle + " for user: " + recipientEmail);
//                System.out.println("Document: " + doc.toJson());
//
//                if (recipientEmail != null && bookTitle != null) {
//                    sendEmail(session, recipientEmail, bookTitle, reminderDate);
//                } else {
//                    System.out.println("Skipping invalid document: " + doc.toJson());
//                }
//            }
//
//            if (!foundBooks) {
//                System.out.println("No books found for: " + reminderDate);
//            }
//        }
//    }
//
//    private void sendEmail(Session session, String recipientEmail, String bookTitle, LocalDate dueDate) {
//        try {
//            MimeMessage message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(senderEmail));
//            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
//            message.setSubject("Library Return Reminder - " + bookTitle);
//
//            String emailBody = String.format(
//                    "Dear user,\n\nYour book '%s' is due for return on %s.\n" +
//                            "Please return it on time to avoid late fees.\n\nThank you!\nLibrary Team",
//                    bookTitle, dueDate);
//
//            message.setText(emailBody);
//
//            Transport.send(message);
//            System.out.println("Reminder sent to: " + recipientEmail);
//
//        } catch (MessagingException e) {
//            System.err.println("Failed to send email to: " + recipientEmail);
//            e.printStackTrace();
//        }
//    }
//
//    public void close() {
//        mongoClient.close();
//        System.out.println("MongoDB connection closed.");
//    }
//
//    public static void main(String[] args) {
//        EmailReminderService service = new EmailReminderService();
//        service.sendReminders();
//        service.close();
//    }
//}
//*/

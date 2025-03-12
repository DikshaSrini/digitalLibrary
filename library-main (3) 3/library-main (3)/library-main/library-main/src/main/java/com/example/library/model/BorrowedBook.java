package com.example.library.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "borrowed_books")
public class BorrowedBook {
    @Id
    private String id;
    private String username;
    private String bookId;
    private String title;
    private String genre;
    private LocalDate issueDate;
    private LocalDate returnDate;

    public BorrowedBook() {}

    public BorrowedBook(String username, String bookId, String title, String genre, LocalDate issueDate, LocalDate returnDate) {
        this.username = username;
        this.bookId = bookId;
        this.title = title;
        this.genre = genre;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getReturnDate() { return returnDate; }

    public void setId(String id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    public void setTitle(String title) { this.title = title; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
}
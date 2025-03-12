package com.example.library.repository;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

// BorrowRecord.java
@Document(collection = "borrow_records")
public class BorrowRecord {
    @Id
    private String id;
    private String username;
    private String bookId;
    private String title;  // Denormalized for convenience
    private String genre;  // Denormalized for convenience
    private LocalDate issueDate;
    private LocalDate returnDate;
    private boolean returned;
    // Constructors, getters, setters
}

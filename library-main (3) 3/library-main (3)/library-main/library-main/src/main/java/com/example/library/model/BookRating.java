package com.example.library.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "book_ratings")
@CompoundIndex(name = "username_bookId_idx", def = "{'username': 1, 'bookId': 1}", unique = true)
public class BookRating {
    @Id
    private String id;
    private String username;
    private String bookId;
    private int rating;
    private String comment;
    private LocalDate ratedDate;

    public BookRating() {}

    public BookRating(String username, String bookId, int rating, String comment, LocalDate ratedDate) {
        this.username = username;
        this.bookId = bookId;
        this.rating = rating;
        this.comment = comment;
        this.ratedDate = ratedDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getRatedDate() {
        return ratedDate;
    }

    public void setRatedDate(LocalDate ratedDate) {
        this.ratedDate = ratedDate;
    }
}
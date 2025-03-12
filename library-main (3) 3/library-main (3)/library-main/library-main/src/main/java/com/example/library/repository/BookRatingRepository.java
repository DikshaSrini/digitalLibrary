package com.example.library.repository;

import com.example.library.model.BookRating;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface BookRatingRepository extends MongoRepository<BookRating, String> {
    List<BookRating> findByBookId(String bookId);
    int countByBookId(String bookId);
    BookRating findByUsernameAndBookId(String username, String bookId);
}
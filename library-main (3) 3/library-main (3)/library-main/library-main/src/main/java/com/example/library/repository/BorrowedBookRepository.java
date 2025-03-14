package com.example.library.repository;

import com.example.library.model.BorrowedBook;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface BorrowedBookRepository extends MongoRepository<BorrowedBook, String> {
    List<BorrowedBook> findByUsername(String username);
    boolean existsByUsernameAndBookId(String username, String bookId);
    void deleteByUsernameAndBookId(String username, String bookId);
    BorrowedBook findByUsernameAndBookId(String username, String bookId);

    // Add this custom query method directly in the interface
    @Query(value = "{ 'username': ?0 }", fields = "{ 'genre': 1, '_id': 0 }")
    List<BorrowedBook> findGenresByUsername(String username);
}
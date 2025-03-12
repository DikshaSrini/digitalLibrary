package com.example.library.repository;

import com.example.library.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Set;

public interface BookRepository extends MongoRepository<Book, String> {
    @Query("{'$or':[ {'title': {$regex: ?0, $options: 'i'}}, {'author': {$regex: ?0, $options: 'i'}}, {'genre': {$regex: ?0, $options: 'i'}}, {'pub_year': {$regex: ?0, $options: 'i'}} ]}")
    List<Book> searchBooks(String keyword);
    List<Book> findByGenreIn(Set<String> genres);

    @Query("{'$or':[ " +
            "{'title': {$regex: '^' + ?0, $options: 'i'}}, " +
            "{'author': {$regex: '^' + ?0, $options: 'i'}}, " +
            "{'genre': {$regex: '^' + ?0, $options: 'i'}}, " +
            "{'pub_year': {$regex: '^' + ?0, $options: 'i'}} " +
            "]}")
    List<Book> findSuggestions(String prefix);

}
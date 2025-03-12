package com.example.library.service;

import com.example.library.model.Book;
import com.example.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowedBookService borrowedBookService;

    // Find all books and include ratings
    public List<Book> findAll() {
        List<Book> books = bookRepository.findAll();

        // Populate ratings for each book
        for (Book book : books) {
            book.setAverageRating(borrowedBookService.getAverageRatingForBook(book.getId()));
            book.setRatingCount(borrowedBookService.getRatingCountForBook(book.getId()));
        }

        return books;
    }

    public Book findById(String id) {
        return bookRepository.findById(id).map(book -> {
            book.setAverageRating(borrowedBookService.getAverageRatingForBook(book.getId()));
            book.setRatingCount(borrowedBookService.getRatingCountForBook(book.getId()));
            return book;
        }).orElse(null);
    }

    public void save(Book book) {
        bookRepository.save(book);
    }

    // Search books and include ratings
    public List<Book> searchBooks(String query) {
        List<Book> books = bookRepository.searchBooks(query);

        // Populate ratings
        for (Book book : books) {
            book.setAverageRating(borrowedBookService.getAverageRatingForBook(book.getId()));
            book.setRatingCount(borrowedBookService.getRatingCountForBook(book.getId()));
        }

        return books;
    }

    public void deleteById(String id) {
        bookRepository.deleteById(id);
    }

    public List<String> findAllGenres() {
        return bookRepository.findAll().stream()
                .map(Book::getGenre)
                .distinct()
                .collect(Collectors.toList());
    }

    // Find books by genre and include ratings
    public List<Book> findByGenreIn(Set<String> genres) {
        List<Book> books = bookRepository.findByGenreIn(genres);

        // Populate ratings
        for (Book book : books) {
            book.setAverageRating(borrowedBookService.getAverageRatingForBook(book.getId()));
            book.setRatingCount(borrowedBookService.getRatingCountForBook(book.getId()));
        }

        return books;
    }

    public List<String> getAutocompleteSuggestions(String prefix, String field) {
        List<Book> suggestions = bookRepository.findSuggestions(prefix);

        if (field == null || field.isEmpty() || field.equalsIgnoreCase("title")) {
            return suggestions.stream()
                    .map(Book::getTitle)
                    .distinct()
                    .limit(10)
                    .collect(Collectors.toList());
        } else if (field.equalsIgnoreCase("author")) {
            return suggestions.stream()
                    .map(Book::getAuthor)
                    .distinct()
                    .limit(10)
                    .collect(Collectors.toList());
        } else if (field.equalsIgnoreCase("genre")) {
            return suggestions.stream()
                    .map(Book::getGenre)
                    .distinct()
                    .limit(10)
                    .collect(Collectors.toList());
        } else if (field.equalsIgnoreCase("year")) {
            return suggestions.stream()
                    .map(book -> String.valueOf(book.getPublicationYear()))
                    .distinct()
                    .limit(10)
                    .collect(Collectors.toList());
        }

        // Default to title if field is not recognized
        return suggestions.stream()
                .map(Book::getTitle)
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }

    public Map<String, List<String>> getAllAutocompleteSuggestions(String prefix) {
        List<Book> suggestions = bookRepository.findSuggestions(prefix);
        Map<String, List<String>> allSuggestions = new HashMap<>();

        allSuggestions.put("title", suggestions.stream()
                .map(Book::getTitle)
                .distinct()
                .limit(5)
                .collect(Collectors.toList()));

        allSuggestions.put("author", suggestions.stream()
                .map(Book::getAuthor)
                .distinct()
                .limit(5)
                .collect(Collectors.toList()));

        allSuggestions.put("genre", suggestions.stream()
                .map(Book::getGenre)
                .distinct()
                .limit(5)
                .collect(Collectors.toList()));

        allSuggestions.put("year", suggestions.stream()
                .map(book -> String.valueOf(book.getPublicationYear()))
                .distinct()
                .limit(5)
                .collect(Collectors.toList()));

        return allSuggestions;
    }
}

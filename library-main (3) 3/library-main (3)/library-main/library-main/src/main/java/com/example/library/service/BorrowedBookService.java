package com.example.library.service;

import com.example.library.model.BookRating;
import com.example.library.model.BorrowHistory;
import com.example.library.model.BorrowedBook;
import com.example.library.repository.BookRatingRepository;
import com.example.library.repository.BorrowHistoryRepository;
import com.example.library.repository.BorrowedBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BorrowedBookService {

    @Autowired
    private BookRatingRepository bookRatingRepository;

    @Autowired
    private BorrowedBookRepository borrowedBookRepository;

    @Autowired
    private BorrowHistoryRepository borrowHistoryRepository;

    public void save(BorrowedBook borrowedBook) {
        borrowedBookRepository.save(borrowedBook);
    }

    public void returnBook(String username, String bookId) {
        BorrowedBook borrowedBook = borrowedBookRepository.findByUsernameAndBookId(username, bookId);

        if (borrowedBook != null) {
            // Create history record
            BorrowHistory history = new BorrowHistory(
                    borrowedBook.getUsername(),
                    borrowedBook.getBookId(),
                    borrowedBook.getTitle(),
                    borrowedBook.getGenre(),
                    borrowedBook.getIssueDate(),
                    LocalDate.now() // Set return date to today
            );

            // Save to history
            borrowHistoryRepository.save(history);

            // Delete from borrowed
            borrowedBookRepository.deleteByUsernameAndBookId(username, bookId);
        }
    }

    public void saveBorrowHistory(BorrowHistory borrowHistory) {
        borrowHistoryRepository.save(borrowHistory);
    }

    public List<BorrowedBook> findByUsername(String username) {
        return borrowedBookRepository.findByUsername(username);
    }

    public List<BorrowHistory> findBorrowHistoryByUsername(String username) {
        return borrowHistoryRepository.findByUsername(username);
    }

    public boolean existsByUsernameAndBookId(String username, String bookId) {
        return borrowedBookRepository.existsByUsernameAndBookId(username, bookId);
    }

    public Set<String> findBorrowedGenresByUsername(String username) {
        List<BorrowedBook> books = borrowedBookRepository.findByUsername(username);
        return books.stream()
                .map(BorrowedBook::getGenre)
                .collect(Collectors.toSet());
    }

    public BorrowedBook findByUsernameAndBookId(String username, String bookId) {
        return borrowedBookRepository.findByUsernameAndBookId(username, bookId);
    }

    public Set<String> findUserInterestGenres(String username) {
        // Get genres from currently borrowed books
        Set<String> currentBorrowedGenres = findBorrowedGenresByUsername(username);

        // Get genres from borrowing history
        List<BorrowHistory> history = borrowHistoryRepository.findByUsername(username);
        Set<String> historyGenres = history.stream()
                .map(BorrowHistory::getGenre)
                .collect(Collectors.toSet());

        // Combine both sets
        currentBorrowedGenres.addAll(historyGenres);

        return currentBorrowedGenres;
    }

    public void saveRating(BookRating rating) {
        // Check if user already rated this book
        BookRating existingRating = bookRatingRepository.findByUsernameAndBookId(
                rating.getUsername(), rating.getBookId());

        if (existingRating != null) {
            // Update existing rating
            existingRating.setRating(rating.getRating());
            existingRating.setComment(rating.getComment());
            existingRating.setRatedDate(LocalDate.now());
            bookRatingRepository.save(existingRating);
        } else {
            // Save new rating
            bookRatingRepository.save(rating);
        }
    }

    public double getAverageRatingForBook(String bookId) {
        List<BookRating> ratings = bookRatingRepository.findByBookId(bookId);
        if (ratings.isEmpty()) {
            return 0.0;
        }

        double sum = ratings.stream().mapToInt(BookRating::getRating).sum();
        return Math.round((sum / ratings.size()) * 10.0) / 10.0; // Round to 1 decimal place
    }

    public int getRatingCountForBook(String bookId) {
        return bookRatingRepository.countByBookId(bookId);
    }
}
package com.example.library.controller;

import com.example.library.model.Book;
import com.example.library.model.BookRating;
import com.example.library.model.BorrowHistory;
import com.example.library.model.BorrowedBook;
import com.example.library.service.BookService;
import com.example.library.service.BorrowedBookService;
import com.example.library.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/user")
@Tag(name = "User Controller")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private BorrowedBookService borrowedBookService;

    @GetMapping("/dashboard")
    @Operation(summary = "User dashboard")
    public String userDashboard(@RequestParam(value = "query", required = false) String query,
                                Model model, Principal principal) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = principal.getName();

        // Fetch books (filtered by search query if applicable)
        List<Book> books = (query != null && !query.isEmpty()) ? bookService.searchBooks(query) : bookService.findAll();

        // Populate average ratings for each book
        books.forEach(book -> {
            double avgRating = borrowedBookService.getAverageRatingForBook(book.getId());
            int ratingCount = borrowedBookService.getRatingCountForBook(book.getId());
            book.setAverageRating(avgRating);
            book.setRatingCount(ratingCount);
        });

        model.addAttribute("username", username);
        model.addAttribute("roles", auth.getAuthorities());
        model.addAttribute("books", books);
        model.addAttribute("query", query);
        model.addAttribute("genres", bookService.findAllGenres());

        List<BorrowedBook> borrowedBooks = borrowedBookService.findByUsername(username);
        model.addAttribute("borrowedBooks", borrowedBooks);

        // Fetch genres of books the user has borrowed
        Set<String> borrowedGenres = borrowedBookService.findUserInterestGenres(username);
        List<Book> suggestedBooks = bookService.findByGenreIn(borrowedGenres);
        model.addAttribute("suggestedBooks", suggestedBooks);

        // Fetch user's borrowed history
        List<BorrowHistory> borrowHistory = borrowedBookService.findBorrowHistoryByUsername(username);
        model.addAttribute("borrowHistory", borrowHistory);

        return "user/dashboard";
    }

    @PostMapping("/borrow-book")
    @Operation(summary = "Borrowing of book")
    public String borrowBook(@RequestParam String bookId, Principal principal, RedirectAttributes redirectAttributes) {
        Book book = bookService.findById(bookId);
        String username = principal.getName();

        if (book != null && book.getQuantity() > 0) {
            if (borrowedBookService.existsByUsernameAndBookId(username, bookId)) {
                redirectAttributes.addFlashAttribute("error", "You have already borrowed this book.");
                return "redirect:/user/dashboard";
            }

            book.setQuantity(book.getQuantity() - 1);
            bookService.save(book);

            BorrowedBook borrowedBook = new BorrowedBook(username, bookId, book.getTitle(),
                    book.getGenre(), LocalDate.now(), LocalDate.now().plusDays(14));
            borrowedBookService.save(borrowedBook);

            redirectAttributes.addFlashAttribute("success", "Book borrowed successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Book is out of stock.");
        }
        return "redirect:/user/dashboard";
    }

    @PostMapping("/return-book")
    @Operation(summary = "Returning of book")
    public String returnBook(@RequestParam String bookId, Principal principal, RedirectAttributes redirectAttributes) {
        Book book = bookService.findById(bookId);
        String username = principal.getName();

        if (book != null) {
            book.setQuantity(book.getQuantity() + 1);
            bookService.save(book);

            borrowedBookService.returnBook(username, bookId);

            redirectAttributes.addFlashAttribute("showRatingModal", true);
            redirectAttributes.addFlashAttribute("ratedBookId", bookId);
            redirectAttributes.addFlashAttribute("ratedBookTitle", book.getTitle());
            redirectAttributes.addFlashAttribute("success", "Book returned successfully. Thank you for rating!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Book return failed.");
        }
        return "redirect:/user/dashboard";
    }

    @PostMapping("/submit-rating")
    @Operation(summary = "Rating of book")
    public String submitRating(@RequestParam String bookId,
                               @RequestParam int rating,
                               @RequestParam(required = false) String comment,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        String username = principal.getName();


        logger.info("Received Rating: {}", rating);

        // Create and save the rating
        BookRating bookRating = new BookRating(username, bookId, rating, comment, LocalDate.now());
        borrowedBookService.saveRating(bookRating);

        redirectAttributes.addFlashAttribute("success", "Thank you for your rating!");
        return "redirect:/user/dashboard";
    }

    @GetMapping("/autocomplete")
    @ResponseBody
    public List<String> autocomplete(@RequestParam String term,
                                     @RequestParam(required = false) String field) {
        return bookService.getAutocompleteSuggestions(term, field);
    }

    @GetMapping("/autocomplete-all")
    @ResponseBody
    public Map<String, List<String>> autocompleteAll(@RequestParam String term) {
        Map<String, List<String>> results = bookService.getAllAutocompleteSuggestions(term);
        results.values().stream().mapToInt(List::size).sum();
        return results;
    }
}
package com.example.library.controller;

import com.example.library.model.Book;
import com.example.library.service.BookService;
import com.example.library.service.BorrowedBookService;
import com.example.library.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private BookService bookService;

    @Mock
    private UserService userService;

    @Mock
    private BorrowedBookService borrowedBookService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private Principal principal;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUserDashboard() {
        when(principal.getName()).thenReturn("testUser");
        List<Book> books = Arrays.asList(
                new Book("1", "Spring Boot", "John Doe", "Technology", 2021, 5),
                new Book("2", "Java Programming", "Jane Doe", "Programming", 2020, 3)
        );
        when(bookService.findAll()).thenReturn(books);

        String viewName = userController.userDashboard(null, model, principal);
        assertEquals("user/dashboard", viewName);
        verify(model, times(1)).addAttribute("username", "testUser");
        verify(model, times(1)).addAttribute("books", books);
    }

    @Test
    void testBorrowBook() {
        when(principal.getName()).thenReturn("testUser");
        Book book = new Book("1", "Spring Boot", "John Doe", "Technology", 2021, 5);
        when(bookService.findById("1")).thenReturn(book);

        String result = userController.borrowBook("1", principal, redirectAttributes);

        assertEquals("redirect:/user/dashboard", result);
        verify(bookService, times(1)).save(book);
        verify(borrowedBookService, times(1)).save(any());
    }

    @Test
    void testReturnBook() {
        when(principal.getName()).thenReturn("testUser");
        Book book = new Book("1", "Spring Boot", "John Doe", "Technology", 2021, 5);
        when(bookService.findById("1")).thenReturn(book);

        String result = userController.returnBook("1", principal, redirectAttributes);

        assertEquals("redirect:/user/dashboard", result);
        verify(bookService, times(1)).save(book);
        verify(borrowedBookService, times(1)).returnBook("testUser", "1");
    }
}

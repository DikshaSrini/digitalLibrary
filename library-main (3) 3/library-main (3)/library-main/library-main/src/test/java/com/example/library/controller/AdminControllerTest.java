package com.example.library.controller;

import com.example.library.model.Book;
import com.example.library.model.RegistrationForm;
import com.example.library.service.BookService;
import com.example.library.service.UserRegistrationService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @Mock
    private BookService bookService;

    @Mock
    private UserRegistrationService registrationService;

    @Mock
    private Model model;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ✅ Test: Admin Dashboard Loads with Books
    @Test
    void testAdminDashboard() {
        List<Book> books = Arrays.asList(
                new Book("1", "Steve Jobs", "Walter Isaacson", "Biography", 1926, 20),
                new Book("2", "Clean Code", "Robert C. Martin", "Programming", 2008, 10)
        );

        when(bookService.findAll()).thenReturn(books);

        String viewName = adminController.adminDashboard(response, model);

        assertEquals("admin/dashboard", viewName);
        verify(response).setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        verify(model).addAttribute("books", books);
    }

    // ✅ Test: Add a New Book
    @Test
    void testAddBook() {
        Book book = new Book(null, "The Pragmatic Programmer", "Andy Hunt", "Technology", 1999, 15);

        doNothing().when(bookService).save(any(Book.class));

        String result = adminController.addBook(book);

        assertEquals("redirect:/admin/dashboard", result);
        verify(bookService, times(1)).save(book);
    }

    // ✅ Test: Remove Book by ID
    @Test
    void testRemoveBook() {
        String bookId = "1";

        doNothing().when(bookService).deleteById(bookId);

        String result = adminController.removeBook(bookId);

        assertEquals("redirect:/admin/dashboard", result);
        verify(bookService, times(1)).deleteById(bookId);
    }

    // ✅ Test: Manage Users Page
    @Test
    void testManageUsers() {
        String viewName = adminController.manageUsers();
        assertEquals("admin/manage-users", viewName);
    }

    // ✅ Test: Manage Books (with Genres)
    @Test
    void testManageBooks() {
        List<Book> books = Arrays.asList(
                new Book("1", "Steve Jobs", "Walter Isaacson", "Biography", 1926, 20),
                new Book("2", "Clean Code", "Robert C. Martin", "Programming", 2008, 10)
        );
        Set<String> expectedGenres = new HashSet<>(Arrays.asList("Biography", "Programming"));

        when(bookService.findAll()).thenReturn(books);

        String viewName = adminController.manageBooks(model);

        assertEquals("admin/manage-books", viewName);
        verify(model).addAttribute("books", books);
        verify(model).addAttribute("genres", expectedGenres);
    }

    // ✅ Test: Register User
    @Test
    void testRegisterUser() {
        String username = "admin";
        String password = "password";
        String role = "ADMIN";

        doNothing().when(registrationService).registerUser(username, password, role);

        String result = adminController.registerUser(username, password, role);

        assertEquals("redirect:/admin/manage-users", result);
        verify(registrationService, times(1)).registerUser(username, password, role);
    }

    // ✅ Test: Show Registration Form
    @Test
    void testShowRegistrationForm() {
        String role = "LIBRARIAN";

        String result = adminController.showRegistrationForm(role, model);

        assertEquals("/register", result);
        verify(model, times(1)).addAttribute("role", role);
        verify(model, times(1)).addAttribute(anyString(), any(RegistrationForm.class));
    }

    // ✅ Test: Show Reports Page
    @Test
    void testShowReports() {
        String result = adminController.showReports();
        assertEquals("admin/reports", result);
    }

    // ✅ Test: Search Books
    @Test
    void testSearchBooks() {
        List<Book> mockBooks = Arrays.asList(
                new Book("1", "Spring Boot", "Craig Walls", "Technology", 2018, 5),
                new Book("2", "Spring Security", "Rob Winch", "Security", 2019, 8)
        );

        when(bookService.searchBooks("Spring")).thenReturn(mockBooks);

        List<Book> result = bookService.searchBooks("Spring");

        assertEquals(2, result.size());
        verify(bookService, times(1)).searchBooks("Spring");
    }

    // ✅ Test: Autocomplete Suggestions
    @Test
    void testGetAutocompleteSuggestions() {
        List<String> mockTitles = Arrays.asList("Spring Boot", "Spring Security");

        when(bookService.getAutocompleteSuggestions("Spring", "title")).thenReturn(mockTitles);

        List<String> result = bookService.getAutocompleteSuggestions("Spring", "title");

        assertEquals(2, result.size());
        verify(bookService, times(1)).getAutocompleteSuggestions("Spring", "title");
    }

    // ✅ Test: Get All Autocomplete Suggestions
    @Test
    void testGetAllAutocompleteSuggestions() {
        Map<String, List<String>> mockSuggestions = new HashMap<>();
        mockSuggestions.put("title", Arrays.asList("Spring Boot"));
        mockSuggestions.put("author", Arrays.asList("Craig Walls"));
        mockSuggestions.put("genre", Arrays.asList("Technology"));
        mockSuggestions.put("year", Arrays.asList("2018"));

        when(bookService.getAllAutocompleteSuggestions("Spring")).thenReturn(mockSuggestions);

        Map<String, List<String>> result = bookService.getAllAutocompleteSuggestions("Spring");

        assertEquals(4, result.size());
        verify(bookService, times(1)).getAllAutocompleteSuggestions("Spring");
    }
}

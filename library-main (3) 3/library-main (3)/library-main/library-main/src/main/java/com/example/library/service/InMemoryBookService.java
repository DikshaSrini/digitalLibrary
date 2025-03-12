//package com.example.library.service;
//
//import com.example.library.model.Book;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class InMemoryBookService {
//    private final List<Book> books = new ArrayList<>();
//
//    public InMemoryBookService() {
//        // Add sample books for testing
//        books.add(new Book("1", "Spring Boot in Action", "Craig Walls", "Technology", 2021, true));
//        books.add(new Book("2", "Effective Java", "Joshua Bloch", "Programming", 2018, true));
//        books.add(new Book("3", "Clean Code", "Robert C. Martin", "Software Engineering", 2008, true));
//    }
//
//    public List<Book> findAll() {
//        return books;
//    }
//
//    public Book findById(String id) {
//        return books.stream().filter(book -> book.getId().equals(id)).findFirst().orElse(null);
//    }
//
//    public void save(Book book) {
//        books.add(book);
//    }
//
//    public void remove(String id) {
//        books.removeIf(book -> book.getId().equals(id));
//    }
//}

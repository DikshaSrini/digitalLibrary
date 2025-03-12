package com.example.library.repository;

import com.example.library.model.BorrowHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BorrowHistoryRepository extends MongoRepository<BorrowHistory, String> {
    List<BorrowHistory> findByUsername(String username);
}
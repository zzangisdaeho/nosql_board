package com.example.nosql.community.repository;

import com.example.nosql.community.document.Board;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BoardRepository extends MongoRepository<Board, String> {
}

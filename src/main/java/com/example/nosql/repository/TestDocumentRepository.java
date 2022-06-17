package com.example.nosql.repository;

import com.example.nosql.document.TestDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TestDocumentRepository extends MongoRepository<TestDocument, Long> {

    TestDocument findByBoardCreateRightSeq(String seq);
}

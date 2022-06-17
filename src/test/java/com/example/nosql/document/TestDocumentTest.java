package com.example.nosql.document;

import com.example.nosql.repository.TestDocumentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TestDocumentTest {

    @Autowired
    TestDocumentRepository testDocumentRepository;

    @Test
    void write(){

        TestDocument testDocument = new TestDocument(3L, new TestDocument.BoardCreateRight(true, false));

        testDocumentRepository.save(testDocument);
    }

    @Test
    void read(){
        TestDocument byBoardCreateRightSeq = testDocumentRepository.findByBoardCreateRightSeq("91a8e86c-a66e-46fd-ac55-b65569fd5a4a");
        System.out.println("byBoardCreateRightSeq = " + byBoardCreateRightSeq);
    }
}
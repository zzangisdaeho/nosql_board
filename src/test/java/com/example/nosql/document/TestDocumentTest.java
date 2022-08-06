package com.example.nosql.document;

import com.example.nosql.community.document.PersonalBoardConfig;
import com.example.nosql.community.repository.PersonalBoardConfigRepository;
import com.example.nosql.repository.TestDocumentRepository;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class TestDocumentTest {

    @Autowired
    TestDocumentRepository testDocumentRepository;

    @Autowired
    PersonalBoardConfigRepository personalBoardConfigRepository;

    @Autowired
    ModelMapper modelMapper;

    @Test
    void write(){

        TestDocument testDocument = new TestDocument(1L, new TestDocument.BoardCreateRight(true, false));

        testDocumentRepository.save(testDocument);
    }

    @Test
    void read(){
        TestDocument testDocument = testDocumentRepository.findByBoardCreateRightSeq("f7d32765-12c1-4064-9b24-c4eef1d8b53c");
        TestDto testDto = new TestDto(5L, new TestDto.BoardCreateRight(false, false));
        System.out.println("testDocument = " + testDocument);

        modelMapper.map(testDto, testDocument);

        testDocumentRepository.save(testDocument);
    }

    @Test
    void save(){
        PersonalBoardConfig personalBoardConfig = new PersonalBoardConfig(1L, List.of("abc", "def"));
        personalBoardConfigRepository.save(personalBoardConfig);
    }

    @Test
    void load(){
        PersonalBoardConfig a1 = personalBoardConfigRepository.findByPersonalOrderListContains("abc").get();
        PersonalBoardConfig a2 = personalBoardConfigRepository.findByPersonalOrderListContains("abc").get();

        boolean equals = a1.equals(a2);
        System.out.println("equals = " + equals);
    }
}
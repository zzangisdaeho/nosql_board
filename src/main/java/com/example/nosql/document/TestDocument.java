package com.example.nosql.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Document(collection = "test")
@AllArgsConstructor
@NoArgsConstructor
public class TestDocument {

    @Id
    private Long companySeq;
    private BoardCreateRight boardCreateRight;

    @Data
    @NoArgsConstructor
    static class BoardCreateRight {

        private String seq;
        private boolean entireMember;
        private boolean manager;

        public BoardCreateRight(boolean entireMember, boolean manager) {
            this.seq = UUID.randomUUID().toString();
            this.entireMember = entireMember;
            this.manager = manager;
        }
    }
}

package com.example.nosql.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestDto {

    private Long id;
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

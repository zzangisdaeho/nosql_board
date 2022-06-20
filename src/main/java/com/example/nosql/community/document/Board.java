package com.example.nosql.community.document;

import com.example.nosql.audit.MongoAuditMetadata;
import com.example.nosql.community.dto.GoogleFileCreateResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Document(collection = "boards")
@AllArgsConstructor
@NoArgsConstructor
public class Board extends MongoAuditMetadata {

    private String boardId;
    private boolean topFixed;
    private String title;
    private String content;
    private List<GoogleFileCreateResponse> attachFileList = new ArrayList<>();
    private List<BoardComment> comments = new ArrayList<>();
    private List<Long> notificationList = new ArrayList<>();

    public static class BoardComment {

    }
}

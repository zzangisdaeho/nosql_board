package com.example.nosql.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PostUpdateRequestDto {

    private String postId;
    private String boardId;
    private boolean topFixed;
    private String title;
    private String content;
//    private List<Board.FileInfo> files = new ArrayList<>();
    private List<Long> notificationList = new ArrayList<>();
}

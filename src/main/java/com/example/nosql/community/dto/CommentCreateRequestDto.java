package com.example.nosql.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CommentCreateRequestDto {

    private String postId;
    private String boardId;
    private String content;
    private List<Long> notificationList = new ArrayList<>();
}

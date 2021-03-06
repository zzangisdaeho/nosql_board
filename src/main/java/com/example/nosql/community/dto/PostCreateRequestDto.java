package com.example.nosql.community.dto;

import com.example.nosql.community.document.Posts;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PostCreateRequestDto {

    private String boardId;
    private boolean topFixed;
    private String title;
    private String content;
    private List<Posts.FileInfo> files = new ArrayList<>();
    private List<Long> notificationList = new ArrayList<>();
}

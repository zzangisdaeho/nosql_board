package com.example.nosql.community.dto;

import lombok.Data;

@Data
public class MemberBoardUpdateDto {

    private String boardId;
    private String boardName;
    private boolean entireAccess;
    private boolean use;

}

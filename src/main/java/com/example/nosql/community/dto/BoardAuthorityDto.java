package com.example.nosql.community.dto;

import lombok.Data;

import java.util.List;

@Data
public class BoardAuthorityDto {

    private String boardId;

    private List<Long> authoritySeqList;
}

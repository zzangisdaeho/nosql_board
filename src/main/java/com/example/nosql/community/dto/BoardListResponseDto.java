package com.example.nosql.community.dto;

import com.example.nosql.community.document.BoardPermission;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BoardListResponseDto {

    private List<BoardPermission.MemberBoard> memberBoardList;
}

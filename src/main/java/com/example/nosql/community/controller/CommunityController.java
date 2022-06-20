package com.example.nosql.community.controller;

import com.example.nosql.community.document.BoardPermission;
import com.example.nosql.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommunityController {

    private CommunityService communityService;

    @GetMapping("/community/{companySeq}/{memberSeq}")
    public Map<String, List<? extends BoardPermission.MemberBoard>> findMyBoardList(@PathVariable Long companySeq, @PathVariable Long memberSeq){

        return communityService.findMyBoardList(companySeq, memberSeq);
    }





    //todo : 맴버 시퀀스 기준으로 접근권한 있는지 반환.
    //todo : 드라이브에 폴더 추가 후 drivefolderUuid 세팅
    //todo : 게시글 document 제작. 게시글 crud, 댓글 crud, 게시판 접근시 게시판 폴더에 권한 있는지 확인.
}

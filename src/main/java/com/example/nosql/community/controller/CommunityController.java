package com.example.nosql.community.controller;

import com.example.nosql.community.document.BoardPermission;
import com.example.nosql.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @GetMapping("/community/{companySeq}")
    public BoardPermission boardPermission(@PathVariable Long companySeq){
        return communityService.boardPermission(companySeq);
    }

    @PutMapping("/community/{companySeq}/right")
    public BoardPermission.BoardCreateRight updateRight(@PathVariable Long companySeq, @RequestBody BoardPermission.BoardCreateRight boardCreateRight){
        return communityService.updateBoardCreateRight(companySeq, boardCreateRight);
    }

    @PutMapping("/community/{companySeq}/common/order")
    public List<BoardPermission.CommonBoard> updateOrder(@PathVariable Long companySeq, @RequestParam String id, @RequestParam String id2){
        return communityService.updateCommonBoardOrder(companySeq, id, id2);
    }

    @PostMapping("/community/{companySeq}/common")
    public List<BoardPermission.CommonBoard> createCommonBoard(@PathVariable Long companySeq){
        return communityService.createCommonBoard(companySeq);
    }

    @PostMapping("/community/{companySeq}/member")
    public List<BoardPermission.MemberBoard> createMemberBoard(@PathVariable Long companySeq){
        return communityService.createMemberBoard(companySeq);
    }

    //todo : 맴버, 공통 게시판 설정 변경(개별 게시판단위).  부서 접근 권한, 조직원 접근 권한 추가.  맴버 시퀀스 기준으로 접근권한 있는지 반환. 게시판 삭제 기능.
    //todo : 드라이브에 폴더 추가 후 drivefolderUuid 세팅
    //todo : 게시글 document 제작. 게시글 crud, 댓글 crud, 게시판 접근시 게시판 폴더에 권한 있는지 확인.

}

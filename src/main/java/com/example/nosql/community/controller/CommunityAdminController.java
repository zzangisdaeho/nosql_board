package com.example.nosql.community.controller;

import com.example.nosql.community.document.BoardPermission;
import com.example.nosql.community.dto.BoardAuthorityDto;
import com.example.nosql.community.dto.CommonBoardUpdateDto;
import com.example.nosql.community.dto.MemberBoardUpdateDto;
import com.example.nosql.community.service.CommunityAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
//todo API접근권한 게시판 관리자권한만 가능하도록 @PreAuthorize 설정 필요
public class CommunityAdminController {

    private final CommunityAdminService communityService;

    @GetMapping("/community/admin/{companySeq}")
    public BoardPermission boardPermission(@PathVariable Long companySeq){
        return communityService.getBoardPermission(companySeq);
    }

    @PutMapping("/community/admin/{companySeq}/right")
    public BoardPermission.BoardCreateRight updateRight(@PathVariable Long companySeq, @RequestBody BoardPermission.BoardCreateRight boardCreateRight){
        return communityService.updateBoardCreateRight(companySeq, boardCreateRight);
    }

    @PutMapping("/community/admin/{companySeq}/common/order")
    public List<BoardPermission.CommonBoard> updateOrder(@PathVariable Long companySeq, @RequestParam String id, @RequestParam String id2){
        return communityService.updateCommonBoardOrder(companySeq, id, id2);
    }

    @PostMapping("/community/admin/{companySeq}/common")
    public List<BoardPermission.CommonBoard> createCommonBoard(@PathVariable Long companySeq){
        return communityService.createCommonBoard(companySeq);
    }

    @PostMapping("/community/admin/{companySeq}/member")
    public List<BoardPermission.MemberBoard> createMemberBoard(@PathVariable Long companySeq){
        return communityService.createMemberBoard(companySeq);
    }

    @PutMapping("/community/admin/{companySeq}/common")
    public List<BoardPermission.CommonBoard> updateCommonBoard(@PathVariable Long companySeq, @RequestBody CommonBoardUpdateDto commonBoardUpdateDto){
        return communityService.updateCommonBoard(companySeq, commonBoardUpdateDto);
    }

    @PutMapping("/community/admin/{companySeq}/member")
    public List<BoardPermission.MemberBoard> updateMemberBoard(@PathVariable Long companySeq, @RequestBody MemberBoardUpdateDto memberBoardUpdateDto){
        return communityService.updateMemberBoard(companySeq, memberBoardUpdateDto);
    }

    @PutMapping("/community/admin/{companySeq}/common/authority/team")
    public BoardPermission.AccessRight insertCommonBoardTeamAuthority(@PathVariable Long companySeq, @RequestBody BoardAuthorityDto authorityDto){
        return communityService.insertTeamAuthority(companySeq, authorityDto);
    }

    @PutMapping("/community/admin/{companySeq}/common/authority/member")
    public BoardPermission.AccessRight insertCommonBoardMemberAuthority(@PathVariable Long companySeq, @RequestBody BoardAuthorityDto authorityDto){
        return communityService.insertMemberAuthority(companySeq, authorityDto);
    }

    @PutMapping("/community/admin/{companySeq}/member/authority/team")
    public BoardPermission.AccessRight insertMemberBoardTeamAuthority(@PathVariable Long companySeq, @RequestBody BoardAuthorityDto authorityDto){
        return communityService.insertTeamAuthority(companySeq, authorityDto);
    }

    @PutMapping("/community/admin/{companySeq}/member/authority/member")
    public BoardPermission.AccessRight insertMemberBoardMemberAuthority(@PathVariable Long companySeq, @RequestBody BoardAuthorityDto authorityDto){
        return communityService.insertMemberAuthority(companySeq, authorityDto);
    }

    @DeleteMapping("/community/admin/{companySeq}/common")
    public void deleteCommonBoard(@PathVariable Long companySeq, List<String> boardIdList){
        communityService.deleteBoard(companySeq, boardIdList);
    }

    @DeleteMapping("/community/admin/{companySeq}/member")
    public void deleteMemberBoard(@PathVariable Long companySeq, @RequestParam("boardIdList") ArrayList<String> boardIdList){
        communityService.deleteBoard(companySeq, boardIdList);
    }




}

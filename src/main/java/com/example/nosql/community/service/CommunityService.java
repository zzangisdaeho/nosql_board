package com.example.nosql.community.service;

import com.example.nosql.community.document.BoardPermission;
import com.example.nosql.community.dto.BoardAuthorityDto;
import com.example.nosql.community.dto.CommonBoardUpdateDto;
import com.example.nosql.community.dto.MemberBoardUpdateDto;
import com.example.nosql.community.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;

    public BoardPermission getBoardPermission(Long companySeq){
        return communityRepository.getBoardPermission(companySeq);
    }

    private BoardPermission updateBoardPermission(BoardPermission boardPermission) {
        return communityRepository.updateBoardPermission(boardPermission);
    }

    public BoardPermission.BoardCreateRight updateBoardCreateRight(Long companySeq, BoardPermission.BoardCreateRight boardCreateRight){
        BoardPermission boardPermission = getBoardPermission(companySeq);
        boardPermission.getBoardCreateRight().changeRight(boardCreateRight.isEntireMember(), boardCreateRight.isManager());

        return updateBoardPermission(boardPermission).getBoardCreateRight();
    }


    public List<BoardPermission.CommonBoard> updateCommonBoardOrder(Long companySeq, String id1, String id2){
        BoardPermission boardPermission = getBoardPermission(companySeq);
        boardPermission.orderingCommonBoardBoard(id1, id2);

        return updateBoardPermission(boardPermission).getCommonBoards();
    }

    public List<BoardPermission.CommonBoard> createCommonBoard(Long companySeq) {
        BoardPermission boardPermission = getBoardPermission(companySeq);
        boardPermission.createCommonBoard("임시 공통 게시판", true, true, 1000L, true);
        //todo Drive 추가

        return updateBoardPermission(boardPermission).getCommonBoards();
    }

    public List<BoardPermission.MemberBoard> createMemberBoard(Long companySeq) {
        BoardPermission boardPermission = getBoardPermission(companySeq);
        boardPermission.createMemberBoard("임시 맴버 게시판", true, true, 1000L);

        return updateBoardPermission(boardPermission).getMemberBoards();
    }

    public List<BoardPermission.CommonBoard> updateCommonBoard(Long companySeq, CommonBoardUpdateDto commonBoardUpdateDto) {
        BoardPermission boardPermission = getBoardPermission(companySeq);

        BoardPermission.MemberBoard findBoard = boardPermission.findBoard(commonBoardUpdateDto.getBoardId());
        BoardPermission.CommonBoard commonBoard = (BoardPermission.CommonBoard) findBoard;

        commonBoard.updateBoardInfo(commonBoardUpdateDto.getBoardName(), commonBoardUpdateDto.isEntireAccess(), commonBoardUpdateDto.isUse(), commonBoardUpdateDto.isNecessary());

        BoardPermission boardPermissionSaved = updateBoardPermission(boardPermission);

        return boardPermissionSaved.getCommonBoards();

    }

    public List<BoardPermission.MemberBoard> updateMemberBoard(Long companySeq, MemberBoardUpdateDto memberBoardUpdateDto) {
        BoardPermission boardPermission = getBoardPermission(companySeq);

        BoardPermission.MemberBoard memberBoard = boardPermission.findBoard(memberBoardUpdateDto.getBoardId());

        memberBoard.updateBoardInfo(memberBoardUpdateDto.getBoardName(), memberBoardUpdateDto.isEntireAccess(), memberBoardUpdateDto.isUse());

        BoardPermission boardPermissionSaved = updateBoardPermission(boardPermission);

        return boardPermissionSaved.getMemberBoards();
    }

    public BoardPermission.AccessRight insertTeamAuthority(Long companySeq, BoardAuthorityDto authorityDto) {
        BoardPermission boardPermission = getBoardPermission(companySeq);

        BoardPermission.MemberBoard board = boardPermission.findBoard(authorityDto.getBoardId());

        board.getAccessRight().addTeamAuthority(authorityDto.getAuthoritySeqList());

        updateBoardPermission(boardPermission);

        return board.getAccessRight();


    }

    public BoardPermission.AccessRight insertMemberAuthority(Long companySeq, BoardAuthorityDto authorityDto) {
        BoardPermission boardPermission = getBoardPermission(companySeq);

        BoardPermission.MemberBoard board = boardPermission.findBoard(authorityDto.getBoardId());

        board.getAccessRight().addMemberAuthority(authorityDto.getAuthoritySeqList());

        updateBoardPermission(boardPermission);

        return board.getAccessRight();
    }

    public void deleteBoard(Long companySeq, List<String> boardIdList) {
        BoardPermission boardPermission = getBoardPermission(companySeq);
        boardPermission.deleteBoard(boardIdList);

        //todo 게시판에서 글도 삭제해줘야함

        updateBoardPermission(boardPermission);

    }
}

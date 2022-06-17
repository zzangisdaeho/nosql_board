package com.example.nosql.community.service;

import com.example.nosql.community.document.BoardPermission;
import com.example.nosql.community.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;

    public BoardPermission boardPermission(Long companySeq){
        return communityRepository.getBoardPermission(companySeq);
    }

    public BoardPermission.BoardCreateRight updateBoardCreateRight(Long companySeq, BoardPermission.BoardCreateRight boardCreateRight){
        BoardPermission boardPermission = communityRepository.getBoardPermission(companySeq);
        boardPermission.getBoardCreateRight().changeRight(boardCreateRight.isEntireMember(), boardCreateRight.isManager());

        return communityRepository.updateBoardPermission(boardPermission).getBoardCreateRight();
    }

    public List<BoardPermission.CommonBoard> updateCommonBoardOrder(Long companySeq, String id1, String id2){
        BoardPermission boardPermission = communityRepository.getBoardPermission(companySeq);
        boardPermission.orderingCommonBoardBoard(id1, id2);

        return communityRepository.updateBoardPermission(boardPermission).getCommonBoards();
    }

    public List<BoardPermission.CommonBoard> createCommonBoard(Long companySeq) {
        BoardPermission boardPermission = communityRepository.getBoardPermission(companySeq);
        boardPermission.createCommonBoard("임시 공통 게시판", true, true, 1000L, true);
        // Drive 추가

//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        return communityRepository.updateBoardPermission(boardPermission).getCommonBoards();
    }

    public List<BoardPermission.MemberBoard> createMemberBoard(Long companySeq) {
        BoardPermission boardPermission = communityRepository.getBoardPermission(companySeq);
        boardPermission.createMemberBoard("임시 맴버 게시판", true, true, 1000L);

        return communityRepository.updateBoardPermission(boardPermission).getMemberBoards();
    }
}

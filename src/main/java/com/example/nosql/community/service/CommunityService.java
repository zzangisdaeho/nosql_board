package com.example.nosql.community.service;

import com.example.nosql.community.document.Board;
import com.example.nosql.community.document.BoardPermission;
import com.example.nosql.community.document.PersonalBoardConfig;
import com.example.nosql.community.dto.CommentUpdateRequestDto;
import com.example.nosql.community.dto.PostCreateRequestDto;
import com.example.nosql.community.dto.PostUpdateRequestDto;
import com.example.nosql.community.dto.CommentCreateRequestDto;
import com.example.nosql.community.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;

    private final ModelMapper modelMapper;


    public Map<String, List<? extends BoardPermission.MemberBoard>> findMyBoardList(Long companySeq, Long memberSeq) {

        BoardPermission boardPermission = communityRepository.getBoardPermission(companySeq);

        PersonalBoardConfig personalBoardConfig = communityRepository.getPersonalBoardConfig(memberSeq);

        checkPersonalConfigContainsDeletedBoards(boardPermission, personalBoardConfig);
        checkPersonalConfigContainsAllBoards(boardPermission, personalBoardConfig);
        savePersonalConfigIfChanged(memberSeq, personalBoardConfig);

        //todo memberSeqList 받아서 넣기
        Map<String, List<? extends BoardPermission.MemberBoard>> myBoardList = boardPermission.findMyBoardList(memberSeq, List.of(1L));
        List<? extends BoardPermission.MemberBoard> memberBoards = myBoardList.get(BoardPermission.MemberBoard.class.getName());

        ArrayList<BoardPermission.MemberBoard> myMemberBoards = getMyPersonalOrderMemberBoard(personalBoardConfig, memberBoards);

        myBoardList.put(BoardPermission.MemberBoard.class.getName(), myMemberBoards);

        return myBoardList;
    }

    //리턴할 게시판 리스트를 personal설정값에 맞게 순서 변경
    private ArrayList<BoardPermission.MemberBoard> getMyPersonalOrderMemberBoard(PersonalBoardConfig personalBoardConfig, List<? extends BoardPermission.MemberBoard> memberBoards){
        ArrayList<BoardPermission.MemberBoard> myMemberBoards = new ArrayList<>();
        personalBoardConfig.getPersonalOrderList().forEach(personalOrder -> {
            BoardPermission.MemberBoard findMemberBoard = memberBoards.stream()
                    .filter(memberBoard -> memberBoard.getBoardId().equals(personalOrder)).findFirst()
                    .orElseThrow(() -> new IllegalStateException("이건 내부로직 오류야 ㅅㅂ 망했다"));
            myMemberBoards.add(findMemberBoard);
        });
        return myMemberBoards;
    }

    private void savePersonalConfigIfChanged(Long memberSeq, PersonalBoardConfig personalBoardConfig) {
        //변한 개인 설정 값이 있으면 업데이트
        if(!communityRepository.getPersonalBoardConfig(memberSeq).equals(personalBoardConfig)){
            communityRepository.updatePersonalBoardConfig(personalBoardConfig);
        }
    }

    private void checkPersonalConfigContainsAllBoards(BoardPermission boardPermission, PersonalBoardConfig personalBoardConfig) {
        //존재하는 게시판 중 나의 설정값에 없는 게시판이 있는지 확인. 있으면 하단에 추가
        boardPermission.getMemberBoards().forEach(memberBoard -> {
            boolean exist = personalBoardConfig.checkExistBoardId(memberBoard.getBoardId());
            if(!exist) personalBoardConfig.addBoardId(memberBoard.getBoardId());
        });
    }

    private void checkPersonalConfigContainsDeletedBoards(BoardPermission boardPermission, PersonalBoardConfig personalBoardConfig) {
        //나의 설정에 있는 게시판 중 존재하지 않는 게시판 있나 확인. 있으면 삭제
        personalBoardConfig.getPersonalOrderList().forEach(personalOrder -> {
            boolean exist = boardPermission.checkExistBoardInMemberBoard(personalOrder);
            if(!exist) personalBoardConfig.deleteBoardId(personalOrder);
        });
    }

    public ArrayList<BoardPermission.MemberBoard> getPersonalConfig(Long companySeq, Long memberSeq){
        BoardPermission boardPermission = communityRepository.getBoardPermission(companySeq);

        PersonalBoardConfig personalBoardConfig = communityRepository.getPersonalBoardConfig(memberSeq);

        checkPersonalConfigContainsDeletedBoards(boardPermission, personalBoardConfig);
        checkPersonalConfigContainsAllBoards(boardPermission, personalBoardConfig);
        savePersonalConfigIfChanged(memberSeq, personalBoardConfig);

        //todo memberSeqList 받아서 넣기
        Map<String, List<? extends BoardPermission.MemberBoard>> myBoardList = boardPermission.findMyBoardList(memberSeq, List.of(1L));
        List<? extends BoardPermission.MemberBoard> memberBoards = myBoardList.get(BoardPermission.MemberBoard.class.getName());

        return getMyPersonalOrderMemberBoard(personalBoardConfig, memberBoards);
    }

    public Board writeBoard(PostCreateRequestDto boardCreateRequestDto) {

        Board board = modelMapper.map(boardCreateRequestDto, Board.class);
        //todo driveFolder create and move files and set driveFolder



        return communityRepository.saveBoard(board);
    }

    public Board updateBoard(PostUpdateRequestDto boardUpdateRequestDto){
        Board board = communityRepository.getBoard(boardUpdateRequestDto.getPostId());

        modelMapper.map(board, boardUpdateRequestDto);

        return communityRepository.saveBoard(board);
    }

    public void deleteBoard(String boardId) {
        communityRepository.deleteBoard(boardId);
    }

    public Board.BoardComment addComment(CommentCreateRequestDto commentCreateRequestDto){
        Board board = communityRepository.getBoard(commentCreateRequestDto.getPostId());
        Board.BoardComment comment = modelMapper.map(commentCreateRequestDto, Board.BoardComment.class);
        board.addComment(comment);

        communityRepository.updateBoard(board);

        return comment;
    }


    public Board.BoardComment updateComment(CommentUpdateRequestDto commentUpdateRequestDto) {
        Board board = communityRepository.getBoard(commentUpdateRequestDto.getBoardId());
        Board.BoardComment comment = board.getComment(commentUpdateRequestDto.getCommentId());
        modelMapper.map(board, comment);

        communityRepository.updateBoard(board);

        return comment;
    }

    public void deleteComment(String postId, String commentId) {
        Board board = communityRepository.getBoard(postId);
        board.removeComment(board.getComment(commentId));
    }
}

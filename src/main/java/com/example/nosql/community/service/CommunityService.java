package com.example.nosql.community.service;

import com.example.nosql.community.document.Posts;
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

    //변한 개인 설정 값이 있으면 업데이트
    private void savePersonalConfigIfChanged(Long memberSeq, PersonalBoardConfig personalBoardConfig) {
        if(!communityRepository.getPersonalBoardConfig(memberSeq).equals(personalBoardConfig)){
            communityRepository.updatePersonalBoardConfig(personalBoardConfig);
        }
    }

    //존재하는 게시판 중 나의 설정값에 없는 게시판이 있는지 확인. 있으면 하단에 추가
    private void checkPersonalConfigContainsAllBoards(BoardPermission boardPermission, PersonalBoardConfig personalBoardConfig) {
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

    public Posts writePost(PostCreateRequestDto boardCreateRequestDto) {

        Posts board = modelMapper.map(boardCreateRequestDto, Posts.class);
        //todo driveFolder create and move files and set driveFolder



        return communityRepository.savePost(board);
    }

    public Posts updatePost(PostUpdateRequestDto boardUpdateRequestDto){
        Posts board = communityRepository.getPost(boardUpdateRequestDto.getPostId());

        modelMapper.map(board, boardUpdateRequestDto);

        return communityRepository.savePost(board);
    }

    public void deletePost(String boardId) {
        communityRepository.deletePost(boardId);
    }

    public Posts.Comments addComment(CommentCreateRequestDto commentCreateRequestDto){
        Posts board = communityRepository.getPost(commentCreateRequestDto.getPostId());
        Posts.Comments comment = modelMapper.map(commentCreateRequestDto, Posts.Comments.class);
        board.addComment(comment);

        communityRepository.updatePost(board);

        return comment;
    }


    public Posts.Comments updateComment(CommentUpdateRequestDto commentUpdateRequestDto) {
        Posts board = communityRepository.getPost(commentUpdateRequestDto.getBoardId());
        Posts.Comments comment = board.getComment(commentUpdateRequestDto.getCommentId());
        modelMapper.map(board, comment);

        communityRepository.updatePost(board);

        return comment;
    }

    public void deleteComment(String postId, String commentId) {
        Posts board = communityRepository.getPost(postId);
        board.removeComment(board.getComment(commentId));

        communityRepository.updatePost(board);
    }
}

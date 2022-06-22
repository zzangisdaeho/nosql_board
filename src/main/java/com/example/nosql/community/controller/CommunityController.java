package com.example.nosql.community.controller;

import com.example.nosql.community.document.Board;
import com.example.nosql.community.document.BoardPermission;
import com.example.nosql.community.dto.CommentCreateRequestDto;
import com.example.nosql.community.dto.CommentUpdateRequestDto;
import com.example.nosql.community.dto.PostCreateRequestDto;
import com.example.nosql.community.dto.PostUpdateRequestDto;
import com.example.nosql.community.repository.BoardPermissionRepository;
import com.example.nosql.community.repository.BoardRepository;
import com.example.nosql.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final BoardPermissionRepository boardPermissionRepository;
    private final BoardRepository boardRepository;

    @GetMapping("/community/{companySeq}/{memberSeq}")
    public Map<String, List<? extends BoardPermission.MemberBoard>> findMyBoardList(@PathVariable Long companySeq, @PathVariable Long memberSeq){

        return communityService.findMyBoardList(companySeq, memberSeq);
    }

    @PostMapping("/community/{companySeq}/posts")
    public Board writeBoard(@PathVariable Long companySeq, @RequestBody PostCreateRequestDto postCreateRequestDto){
        checkAuthorizationOfBoard(companySeq, postCreateRequestDto.getBoardId());
        return communityService.writeBoard(postCreateRequestDto);
    }

    @PutMapping("/community/{companySeq}/posts")
    public Board updateBoard(@PathVariable Long companySeq, @RequestBody PostUpdateRequestDto postUpdateRequestDto){
        checkAuthorizationOfBoard(companySeq, postUpdateRequestDto.getBoardId());
        checkAuthorizationOfPostWriter(postUpdateRequestDto.getPostId(), 1L);
        return communityService.updateBoard(postUpdateRequestDto);
    }

    @DeleteMapping("/community/{companySeq}/posts")
    public void deleteBoard(@PathVariable Long companySeq, @RequestParam String boardId){
        checkAuthorizationOfBoard(companySeq, boardId);
        checkAuthorizationOfPostWriter(boardId, 1L);
        communityService.deleteBoard(boardId);
    }

    @PostMapping("/community/{companySeq}/posts/comment")
    public Board.BoardComment writeComment(@PathVariable Long companySeq, @RequestBody CommentCreateRequestDto commentCreateRequestDto){
        checkAuthorizationOfBoard(companySeq, commentCreateRequestDto.getBoardId());

        return communityService.addComment(commentCreateRequestDto);
    }

    @PutMapping("/community/{companySeq}/posts/comment")
    public Board.BoardComment updateComment(@PathVariable Long companySeq, @RequestBody CommentUpdateRequestDto commentUpdateRequestDto){
        checkAuthorizationOfBoard(companySeq, commentUpdateRequestDto.getBoardId());
        checkAuthorizationOfCommentWriter(commentUpdateRequestDto.getPostId(), commentUpdateRequestDto.getCommentId(), 1L);
        return communityService.updateComment(commentUpdateRequestDto);
    }

    @DeleteMapping("/community/{companySeq}/posts/comment")
    public void deleteComment(@PathVariable Long companySeq, @RequestParam String boardId, @RequestParam String postId, @RequestParam String commentId){
        checkAuthorizationOfBoard(companySeq, boardId);
        checkAuthorizationOfCommentWriter(postId, commentId, 1L);
        communityService.deleteComment(postId, commentId);
    }

    private boolean checkAuthorizationOfBoard(Long companySeq, String boardId) {
        BoardPermission boardPermission = boardPermissionRepository.findById(companySeq).get();
        BoardPermission.MemberBoard board = boardPermission.findBoard(boardId);
        boolean authorization = board.authorization(List.of(1L), 1L);

        if(!authorization) throw new IllegalStateException("넌 이쪽 게시판으로 못지나간다");

        return true;
    }

    private boolean isPostWriter(String postId, Long memberSeq){
        Board board = boardRepository.findById(postId).orElseThrow(() -> new IllegalStateException("존재하지 않는 게시물입니다"));
        return board.isWriter(memberSeq);
    }

    private void checkAuthorizationOfPostWriter(String postId, Long memberSeq){
        if(!isPostWriter(postId, memberSeq)) throw new IllegalStateException("넌 이쪽 게시글을 수정할 자격이 없다");
    }

    private boolean isCommentWriter(String postId, String commentId, Long memberSeq){
        Board board = boardRepository.findById(postId).orElseThrow(() -> new IllegalStateException("존재하지 않는 게시물입니다"));
        Board.BoardComment comment = board.getComment(commentId);
        return comment.isWriter(memberSeq);
    }

    private void checkAuthorizationOfCommentWriter(String postId, String commentId, Long memberSeq){
        if(!isCommentWriter(postId, commentId, memberSeq)) throw new IllegalStateException("넌 이쪽 댓글을 수정할 자격이 없다");
    }




    //todo : 맴버 시퀀스 기준으로 접근권한 있는지 반환.
    //todo : 드라이브에 폴더 추가 후 drivefolderUuid 세팅
}

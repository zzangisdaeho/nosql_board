package com.example.nosql.community.controller;

import com.example.nosql.community.document.Posts;
import com.example.nosql.community.document.BoardPermission;
import com.example.nosql.community.dto.CommentCreateRequestDto;
import com.example.nosql.community.dto.CommentUpdateRequestDto;
import com.example.nosql.community.dto.PostCreateRequestDto;
import com.example.nosql.community.dto.PostUpdateRequestDto;
import com.example.nosql.community.repository.BoardPermissionRepository;
import com.example.nosql.community.repository.PostRepository;
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
    private final PostRepository boardRepository;

    @GetMapping("/community/{companySeq}/{memberSeq}")
    public Map<String, List<? extends BoardPermission.MemberBoard>> findMyBoardList(@PathVariable Long companySeq, @PathVariable Long memberSeq){

        return communityService.findMyBoardList(companySeq, memberSeq);
    }

    @PostMapping("/community/{companySeq}/posts")
    public Posts writeBoard(@PathVariable Long companySeq, @RequestBody PostCreateRequestDto postCreateRequestDto){
        checkAuthorizationOfBoard(companySeq, postCreateRequestDto.getBoardId());
        return communityService.writePost(postCreateRequestDto);
    }

    @PutMapping("/community/{companySeq}/posts")
    public Posts updateBoard(@PathVariable Long companySeq, @RequestBody PostUpdateRequestDto postUpdateRequestDto){
        checkAuthorizationOfBoard(companySeq, postUpdateRequestDto.getBoardId());
        checkAuthorizationOfPostWriter(postUpdateRequestDto.getPostId(), 1L);
        return communityService.updatePost(postUpdateRequestDto);
    }

    @DeleteMapping("/community/{companySeq}/posts")
    public void deleteBoard(@PathVariable Long companySeq, @RequestParam String boardId){
        checkAuthorizationOfBoard(companySeq, boardId);
        checkAuthorizationOfPostWriter(boardId, 1L);
        communityService.deletePost(boardId);
    }

    @PostMapping("/community/{companySeq}/posts/comment")
    public Posts.Comments writeComment(@PathVariable Long companySeq, @RequestBody CommentCreateRequestDto commentCreateRequestDto){
        checkAuthorizationOfBoard(companySeq, commentCreateRequestDto.getBoardId());

        return communityService.addComment(commentCreateRequestDto);
    }

    @PutMapping("/community/{companySeq}/posts/comment")
    public Posts.Comments updateComment(@PathVariable Long companySeq, @RequestBody CommentUpdateRequestDto commentUpdateRequestDto){
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
        Posts board = boardRepository.findById(postId).orElseThrow(() -> new IllegalStateException("존재하지 않는 게시물입니다"));
        return board.isWriter(memberSeq);
    }

    private void checkAuthorizationOfPostWriter(String postId, Long memberSeq){
        if(!isPostWriter(postId, memberSeq)) throw new IllegalStateException("넌 이쪽 게시글을 수정할 자격이 없다");
    }

    private boolean isCommentWriter(String postId, String commentId, Long memberSeq){
        Posts board = boardRepository.findById(postId).orElseThrow(() -> new IllegalStateException("존재하지 않는 게시물입니다"));
        Posts.Comments comment = board.getComment(commentId);
        return comment.isWriter(memberSeq);
    }

    private void checkAuthorizationOfCommentWriter(String postId, String commentId, Long memberSeq){
        if(!isCommentWriter(postId, commentId, memberSeq)) throw new IllegalStateException("넌 이쪽 댓글을 수정할 자격이 없다");
    }

    //todo : 드라이브에 폴더 추가 후 drivefolderUuid 세팅
}

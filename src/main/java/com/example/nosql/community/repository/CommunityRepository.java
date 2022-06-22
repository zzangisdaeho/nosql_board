package com.example.nosql.community.repository;

import com.example.nosql.community.document.Posts;
import com.example.nosql.community.document.BoardPermission;
import com.example.nosql.community.document.PersonalBoardConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CommunityRepository {

    private final BoardPermissionRepository boardPermissionRepository;
    private final PersonalBoardConfigRepository personalBoardConfigRepository;
    private final PostRepository postRepository;
    private final MongoTemplate template;

    public BoardPermission getBoardPermission(Long companySeq) {
        return boardPermissionRepository.findById(companySeq)
                .orElseGet(() -> {
                            BoardPermission.CommonBoard defaultCommonBoard = new BoardPermission.CommonBoard("공통 게시판", true, new BoardPermission.AccessRight(Set.of(1L, 2L), Set.of(11L, 12L)), true, 1000L, true);
                            defaultCommonBoard.defaultCommonFolder();
                            BoardPermission boardPermission = new BoardPermission(companySeq, new BoardPermission.BoardCreateRight(true, false), List.of(defaultCommonBoard), new ArrayList<>());
                            return boardPermissionRepository.save(boardPermission);
                        }
                );
    }

    public BoardPermission updateBoardPermission(BoardPermission boardPermission) {
        return boardPermissionRepository.save(boardPermission);
    }

    public PersonalBoardConfig getPersonalBoardConfig(Long memberSeq) {
        return personalBoardConfigRepository.findById(memberSeq)
                .orElseGet(() -> {
                    PersonalBoardConfig personalBoardConfig = new PersonalBoardConfig(memberSeq, Collections.emptyList());
                    return personalBoardConfigRepository.save(personalBoardConfig);
                });
    }

    public PersonalBoardConfig updatePersonalBoardConfig(PersonalBoardConfig personalBoardConfig) {
        return personalBoardConfigRepository.save(personalBoardConfig);
    }

    public Posts savePost(Posts post){
        return postRepository.save(post);
    }

    public Posts updatePost(Posts post){
        return postRepository.save(post);
    }

    public void deletePost(String postId){
        postRepository.deleteById(postId);
    }

    public Posts getPost(String postId){
        return postRepository.findById(postId).orElseThrow(() -> new IllegalStateException("존재하지 않는 게시물입니다"));
    }
}

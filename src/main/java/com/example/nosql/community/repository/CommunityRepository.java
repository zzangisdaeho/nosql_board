package com.example.nosql.community.repository;

import com.example.nosql.community.document.BoardPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CommunityRepository {

    private final BoardPermissionRepository boardPermissionRepository;
    private final MongoTemplate template;

    public BoardPermission getBoardPermission(Long companySeq) {
        return boardPermissionRepository.findById(companySeq)
                .orElseGet(() -> {
                            BoardPermission.CommonBoard defaultCommonBoard = new BoardPermission.CommonBoard("공통 게시판", true, new BoardPermission.AccessRight(Set.of(1L, 2L), Set.of(11L, 12L)), true, 1000L,true);
                            defaultCommonBoard.defaultCommonFolder();
                            BoardPermission boardPermission = new BoardPermission(companySeq, new BoardPermission.BoardCreateRight(true, false), List.of(defaultCommonBoard), new ArrayList<>());
                            return boardPermissionRepository.save(boardPermission);
                        }
                );
    }

    public BoardPermission updateBoardPermission(BoardPermission boardPermission){
        return boardPermissionRepository.save(boardPermission);
    }
}

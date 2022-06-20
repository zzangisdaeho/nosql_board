package com.example.nosql.community.service;

import com.example.nosql.community.document.BoardPermission;
import com.example.nosql.community.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;


    public Map<String, List<? extends BoardPermission.MemberBoard>> findMyBoardList(Long companySeq, Long memberSeq) {

        BoardPermission boardPermission = communityRepository.getBoardPermission(companySeq);

        return boardPermission.findMyBoardList(memberSeq, List.of(1L));
    }


}

package com.example.nosql.community.document;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.List;

/**
 * 개인별 커스터마이징 데이터
 */
@Getter
@Document(collection = "personalBoardConfigs")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PersonalBoardConfig {

    @Id
    private Long memberSeq;

    private List<String> personalOrderList;

    public void orderingPersonalBoard(String switchId1, String switchId2){

        int firstIndex = this.personalOrderList.indexOf(switchId1);
        int secondIndex = this.personalOrderList.indexOf(switchId2);

        if(firstIndex < 0 || secondIndex < 0){
            throw new IllegalStateException("순서를 바꾸려는 게시판이 존재하지 않습니다.");
        }

        Collections.swap(this.personalOrderList, firstIndex, secondIndex);
    }

    public void deleteBoardId(String boardId){
        this.personalOrderList.removeIf(personalOrder -> personalOrderList.equals(boardId));
    }

    public boolean checkExistBoardId(String boardId){
        return this.personalOrderList.contains(boardId);
    }

    public boolean addBoardId(String boardId){
        return this.personalOrderList.add(boardId);
    }

}

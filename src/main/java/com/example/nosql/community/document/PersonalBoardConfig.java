package com.example.nosql.community.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "personalBoardConfigs")
@AllArgsConstructor
@NoArgsConstructor
public class PersonalBoardConfig {

    @Id
    private Long memberSeq;


}

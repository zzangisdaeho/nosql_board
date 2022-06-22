package com.example.nosql.community.repository;

import com.example.nosql.community.document.PersonalBoardConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PersonalBoardConfigRepository extends MongoRepository<PersonalBoardConfig, Long> {

    Optional<PersonalBoardConfig> findByPersonalOrderListContains(String boardId);
}

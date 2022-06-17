package com.example.nosql.community.repository;

import com.example.nosql.community.document.BoardPermission;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BoardPermissionRepository extends MongoRepository<BoardPermission, Object> {
}

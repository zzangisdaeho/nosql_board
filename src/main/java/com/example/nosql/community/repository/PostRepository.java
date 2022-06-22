package com.example.nosql.community.repository;

import com.example.nosql.community.document.Posts;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PostRepository extends MongoRepository<Posts, String> {
}

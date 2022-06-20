package com.example.nosql.community.document;

import com.example.nosql.audit.MongoAuditMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document(collection = "boards")
@AllArgsConstructor
@NoArgsConstructor
public class Board extends MongoAuditMetadata {


}

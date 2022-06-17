package com.example.nosql.audit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class MongoAuditMetadata {

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @CreatedBy
    private String createdByUser;

    @LastModifiedBy
    private String modifiedByUser;

    @Version
    private Integer version;
}

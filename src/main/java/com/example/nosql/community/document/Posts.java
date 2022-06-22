package com.example.nosql.community.document;

import com.example.nosql.audit.MongoAuditMetadata;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Document(collection = "posts")
@AllArgsConstructor
@NoArgsConstructor
public class Posts extends MongoAuditMetadata {

    @Id
    private String postId;
    private String boardId;
    private boolean topFixed;
    private String title;
    private String content;
    private String googleFolderUuid;
    private List<FileInfo> files = new ArrayList<>();
    private List<Comments> comments = new ArrayList<>();

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Comments {

        private String commentId = UUID.randomUUID().toString();
        private String content;
        private Long createdBy;
        private LocalDateTime createdDate = LocalDateTime.now();

        public boolean isWriter(Long memberSeq){
            return this.getCreatedBy().equals(memberSeq);
        }
    }

    @Data
    public static class FileInfo{
        private String fileUuid;

        private String url;

        private String icon;

        private String fileName;

        private String googleFileId;

        @Builder
        public FileInfo(String fileUuid, String url, String icon, String fileName, String googleFileId) {
            this.fileUuid = fileUuid;
            this.url = url;
            this.icon = icon;
            this.fileName = fileName;
            this.googleFileId = googleFileId;
        }
    }

    public boolean isWriter(Long memberSeq){
        return this.getCreatedBy().equals(memberSeq);
    }

    public Comments addComment(Comments comment){
        this.comments.add(comment);
        return comment;
    }

    public Comments getComment(String commentId){
        return this.comments.stream()
                .filter(comment -> comment.getCommentId().equals(commentId)).findFirst()
                .orElseThrow(() -> new IllegalStateException("해당 코멘트는 존재하지 않습니다"));
    }

    public void removeComment(Comments comment){
        this.comments.removeIf(savedComment -> savedComment.equals(comment));
    }
}

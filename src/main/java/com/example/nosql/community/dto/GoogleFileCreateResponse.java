package com.example.nosql.community.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@Getter
@Setter
@ToString
@SuperBuilder
@AllArgsConstructor
public class GoogleFileCreateResponse {

    private String folderUuid;

    @Builder.Default
    private List<FileInfo> files = new ArrayList<>();

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
}

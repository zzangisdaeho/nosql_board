package com.example.nosql.community.document;

import com.example.nosql.audit.AuditConfig;
import com.example.nosql.audit.MongoAuditMetadata;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;


@Getter
@Document(collection = "boardPermissions")
@AllArgsConstructor
@NoArgsConstructor
public class BoardPermission extends MongoAuditMetadata {

    @Id
    private Long companySeq;
    private BoardCreateRight boardCreateRight;
    private List<CommonBoard> commonBoards;
    private List<MemberBoard> memberBoards;
//    @Version
//    private int version;


    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BoardCreateRight {
        private boolean entireMember = false;
        private boolean manager = false;

        public void changeRight(boolean entireMember, boolean manager){
            this.entireMember = entireMember;
            this.manager = manager;
        }
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor
    public static class CommonBoard extends MemberBoard {
        private boolean necessary;

        public CommonBoard(String boardName, boolean entireAccess, AccessRight accessRight, boolean use, Long createUser, boolean necessary) {
            super(boardName, entireAccess, accessRight, use, createUser);
            this.necessary = necessary;
        }

        public void updateBoardInfo(String boardName, boolean entireAccess, boolean use, boolean necessary){
            super.updateBoardInfo(boardName, entireAccess, use);
            this.necessary = necessary;
        }

    }

    @Getter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class MemberBoard {
        private String boardId = UUID.randomUUID().toString();
        private String boardName;
        private boolean entireAccess;
        private AccessRight accessRight;
        private boolean use;
        private String googleDriveFolderUuid;
        private Long createUser;
        private LocalDateTime createDate;
        private boolean isMandatory;

        public MemberBoard(String boardName, boolean entireAccess, AccessRight accessRight, boolean use, Long createUser) {
            this.boardName = boardName;
            this.entireAccess = entireAccess;
            this.accessRight = accessRight;
            this.use = use;
            this.createUser = createUser;
            this.createDate = LocalDateTime.now();
        }

        // 기본 공용 폴더 생성시에만 사용!! 함부로 쓰지마세요. 진짜 화낼꺼임
        public void defaultCommonFolder(){
            this.isMandatory = true;
        }

        // memberSeq가 게시판 맴버에 등록되어 있는지 체크
        public boolean isMemberInList(Long memberSeq) {
            if (use) {
                if (entireAccess) {
                    return true;
                } else {
                    return this.accessRight.getMemberList().stream().anyMatch(aLong -> aLong.equals(memberSeq));
                }
            } else {
                return false;
            }
        }

        // teamSeq가 게시판 팀에 등록되어 있는지 체크
        public boolean isTeamInList(List<Long> teamSeqList) {
            if (use) {
                if (entireAccess) {
                    return true;
                } else {
                    return this.accessRight.getTeamList().stream().anyMatch(teamSeqList::contains);
                }
            } else {
                return false;
            }
        }

        //자신이 해당 게시판에 접근할 수 있는지 체크
        public boolean authorization(List<Long> teamSeqList, Long memberSeq){
            return isTeamInList(teamSeqList) || isMemberInList(memberSeq);
        }

        public void updateBoardInfo(String boardName, boolean entireAccess, boolean use){
            this.boardName = boardName;
            this.entireAccess = entireAccess;
            this.use = use;
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccessRight {
        private Set<Long> teamList = new HashSet<>();
        private Set<Long> memberList = new HashSet<>();

        public void addTeamAuthority(List<Long> teamList){
            this.teamList.clear();
            this.teamList.addAll(teamList);
        }

        public void addMemberAuthority(List<Long> memberList){
            this.memberList.clear();
            this.memberList.addAll(memberList);
        }
    }

    // 게시판 id로 어떤 게시판인지 찾아옴
    public MemberBoard findBoard(String boardId) {
        Optional<? extends MemberBoard> result;
        if (this.commonBoards.stream().anyMatch(isBoardOfId(boardId))) {
            result = commonBoards.stream().filter(isBoardOfId(boardId)).findFirst();
        } else {
            result = memberBoards.stream().filter(isBoardOfId(boardId)).findFirst();
        }

        if (result.isEmpty()) {
            throw new IllegalStateException("존재하지 않는 게시판 번호입니다.");
        } else {
            MemberBoard memberBoard = result.get();
            if (!memberBoard.use) {
                throw new IllegalStateException("사용하지 않는 게시판 입니다.");
            } else {
                return memberBoard;
            }
        }
    }

    // 자신이 속한 팀과 맴버시퀀스를 통해 자기가 접근 가능한 게시판 리스트를 가져옴
    public Map<String, List<? extends MemberBoard>> findMyBoardList(Long memberSeq, List<Long> teamSeqList) {
        Map<String, List<? extends MemberBoard>> result = new HashMap<>();

        List<CommonBoard> commonBoardList = this.commonBoards.stream()
                .filter(commonBoard -> commonBoard.isMemberInList(memberSeq) || commonBoard.isTeamInList(teamSeqList))
                .toList();
        result.put(CommonBoard.class.getName(), commonBoardList);

        List<MemberBoard> memberBoardList = this.memberBoards.stream()
                .filter(memberBoard -> memberBoard.isMemberInList(memberSeq) || memberBoard.isTeamInList(teamSeqList))
                .toList();
        result.put(MemberBoard.class.getName(), memberBoardList);

        return result;
    }

//    public void updateCommonBoardInfo(List<BoardPermission.CommonBoard> commonBoardList){
//        //param으로 들어온 board의 id와 동일한 게시판들
//        List<CommonBoard> alreadyExistCommonBoards = commonBoardList.stream().filter(commonBoard -> this.commonBoards.stream().anyMatch(eachBoard -> eachBoard.getBoardId().equals(commonBoard.getBoardId()))).toList();
//        //param으로 들어온 board의 id에 없는 기존 board들
//        List<CommonBoard> removeTargetCommonBoards = this.commonBoards.stream().filter(commonBoard -> commonBoardList.stream().noneMatch(eachBoard -> eachBoard.getBoardId().equals(commonBoard.getBoardId()))).toList();
//
//        //이미 존재하는 게시판은 업데이트
//        alreadyExistCommonBoards.forEach(commonBoard -> commonBoard.updateBoardInfo(commonBoard.getBoardName(), commonBoard.isEntireAccess(), commonBoard.getAccessRight(), commonBoard.isUse(), commonBoard.isNecessary()));
//
//        this.commonBoards.removeAll(removeTargetCommonBoards); // 남은건 추가시켜야함
//
//    }

    public List<CommonBoard> createCommonBoard(String boardName, boolean entireAccess, boolean use, Long createUser, boolean necessary){
        CommonBoard newCommonBoard = new CommonBoard(boardName, entireAccess, new AccessRight(), use, createUser, necessary);

        this.commonBoards.add(newCommonBoard);

        return this.commonBoards;
    }

    public List<MemberBoard> createMemberBoard(String boardName, boolean entireAccess, boolean use, Long createUser){
        MemberBoard newMemberBoard = new MemberBoard(boardName, entireAccess, new AccessRight(), use, createUser);

        this.memberBoards.add(newMemberBoard);

        return this.memberBoards;
    }

    public void orderingCommonBoardBoard(String switchId1, String switchId2){
        CommonBoard first = this.commonBoards.stream().filter(isBoardOfId(switchId1)).findFirst().orElseThrow(() -> new IllegalStateException("순서를 바꾸려는 게시판이 존재하지 않습니다."));
        CommonBoard second = this.commonBoards.stream().filter(isBoardOfId(switchId2)).findFirst().orElseThrow(() -> new IllegalStateException("순서를 바꾸려는 게시판이 존재하지 않습니다."));

        int firstIndex = this.commonBoards.indexOf(first);
        int secondIndex = this.commonBoards.indexOf(second);

        Collections.swap(this.commonBoards, firstIndex, secondIndex);
    }

    private Predicate<MemberBoard> isBoardOfId(String targetId) {
        return board -> board.getBoardId().equals(targetId);
    }

    public void deleteBoard(List<String> boardIdList){
        //기본 폴더 지우려는지 먼저 검사
        if (boardIdList.stream().anyMatch(boardId -> findBoard(boardId).isMandatory)) throw new IllegalStateException("기본 공용 폴더는 삭제 불가능합니다");

        this.memberBoards.removeIf(memberBoard -> boardIdList.contains(memberBoard.getBoardId()));
        this.commonBoards.removeIf(commonBoard -> boardIdList.contains(commonBoard.getBoardId()));
    }

}

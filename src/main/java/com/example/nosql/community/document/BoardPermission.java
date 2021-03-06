package com.example.nosql.community.document;

import com.example.nosql.audit.MongoAuditMetadata;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
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

        // ?????? ?????? ?????? ??????????????? ??????!! ????????? ???????????????. ?????? ????????????
        public void defaultCommonFolder(){
            this.isMandatory = true;
        }

        // memberSeq??? ????????? ????????? ???????????? ????????? ??????
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

        // teamSeq??? ????????? ?????? ???????????? ????????? ??????
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

        //????????? ?????? ???????????? ????????? ??? ????????? ??????
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

    // ????????? id??? ?????? ??????????????? ?????????
    public MemberBoard findBoard(String boardId) {
        Optional<? extends MemberBoard> result;
        if (this.commonBoards.stream().anyMatch(isBoardOfId(boardId))) {
            result = commonBoards.stream().filter(isBoardOfId(boardId)).findFirst();
        } else {
            result = memberBoards.stream().filter(isBoardOfId(boardId)).findFirst();
        }

        if (result.isEmpty()) {
            throw new IllegalStateException("???????????? ?????? ????????? ???????????????.");
        } else {
            MemberBoard memberBoard = result.get();
            if (!memberBoard.use) {
                throw new IllegalStateException("???????????? ?????? ????????? ?????????.");
            } else {
                return memberBoard;
            }
        }
    }

    // ????????? ?????? ?????? ?????????????????? ?????? ????????? ?????? ????????? ????????? ???????????? ?????????
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
//        //param?????? ????????? board??? id??? ????????? ????????????
//        List<CommonBoard> alreadyExistCommonBoards = commonBoardList.stream().filter(commonBoard -> this.commonBoards.stream().anyMatch(eachBoard -> eachBoard.getBoardId().equals(commonBoard.getBoardId()))).toList();
//        //param?????? ????????? board??? id??? ?????? ?????? board???
//        List<CommonBoard> removeTargetCommonBoards = this.commonBoards.stream().filter(commonBoard -> commonBoardList.stream().noneMatch(eachBoard -> eachBoard.getBoardId().equals(commonBoard.getBoardId()))).toList();
//
//        //?????? ???????????? ???????????? ????????????
//        alreadyExistCommonBoards.forEach(commonBoard -> commonBoard.updateBoardInfo(commonBoard.getBoardName(), commonBoard.isEntireAccess(), commonBoard.getAccessRight(), commonBoard.isUse(), commonBoard.isNecessary()));
//
//        this.commonBoards.removeAll(removeTargetCommonBoards); // ????????? ??????????????????
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
        CommonBoard first = this.commonBoards.stream().filter(isBoardOfId(switchId1)).findFirst().orElseThrow(() -> new IllegalStateException("????????? ???????????? ???????????? ???????????? ????????????."));
        CommonBoard second = this.commonBoards.stream().filter(isBoardOfId(switchId2)).findFirst().orElseThrow(() -> new IllegalStateException("????????? ???????????? ???????????? ???????????? ????????????."));

        int firstIndex = this.commonBoards.indexOf(first);
        int secondIndex = this.commonBoards.indexOf(second);

        Collections.swap(this.commonBoards, firstIndex, secondIndex);
    }

    private Predicate<MemberBoard> isBoardOfId(String targetId) {
        return board -> board.getBoardId().equals(targetId);
    }

    public void deleteBoard(List<String> boardIdList){
        //?????? ?????? ??????????????? ?????? ??????
        if (boardIdList.stream().anyMatch(boardId -> findBoard(boardId).isMandatory)) throw new IllegalStateException("?????? ?????? ????????? ?????? ??????????????????");

        this.memberBoards.removeIf(memberBoard -> boardIdList.contains(memberBoard.getBoardId()));
        this.commonBoards.removeIf(commonBoard -> boardIdList.contains(commonBoard.getBoardId()));
    }

    public boolean checkExistBoardInMemberBoard(String boardId){
        return this.memberBoards.stream().anyMatch(isBoardOfId(boardId));
    }

}

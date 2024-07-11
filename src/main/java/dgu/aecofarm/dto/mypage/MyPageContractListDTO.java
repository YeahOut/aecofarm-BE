package dgu.aecofarm.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyPageContractListDTO {
    private List<LendingItem> lendingItems;
    private List<BorrowingItem> borrowingItems;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LendingItem {
        private Long contractId;
        private String itemName;
        private int price;
        private String itemPlace;
        private int time;
        private int contractTime;
        private List<String> itemHash;
        private boolean likeStatus;
        private boolean donateStatus;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BorrowingItem {
        private Long contractId;
        private String itemName;
        private String itemImage;
        private int price;
        private String itemPlace;
        private int time;
        private int contractTime;
        private List<String> itemHash;
        private boolean likeStatus;
        private boolean donateStatus;
    }
}

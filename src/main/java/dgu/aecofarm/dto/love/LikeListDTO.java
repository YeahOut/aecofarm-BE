package dgu.aecofarm.dto.love;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeListDTO {
    private List<LendingItem> lendingItems;
    private List<BorrowingItem> borrowingItems;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LendingItem {
        private Long contractId;
        private String itemName;
        private int price;
        private int time;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BorrowingItem {
        private Long contractId;
        private String itemName;
        private String itemImage;
        private int price;
        private int time;
    }
}

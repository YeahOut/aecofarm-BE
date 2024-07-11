package dgu.aecofarm.dto.member;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchResponseDTO {
    private List<SearchItemDTO> lendItems;
    private List<SearchItemDTO> borrowItems;

    @Data
    @Builder
    public static class SearchItemDTO {
        private Long contractId;
        private String itemName;
        private String itemImage;
        private int price;
        private String itemPlace;
        private int time; // 대여 가능 시간
        private int contractTime; // 거래 가능 시간
        private List<String> itemHash;
        private boolean likeStatus; // 좋아요 여부
        private boolean donateStatus; // 기부하기 여부
    }
}
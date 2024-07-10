package dgu.aecofarm.dto.borrow;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LendListResponseDTO {
    private Long contractId;
    private Long itemId;
    private String itemName;
    private int price;
    private String itemPlace;
    private int time; // 대여 가능 시간
    private int contractTime; // 거래 가능 시간
    private List<String> itemHash;
    private boolean likeStatus; // 좋아요 여부
    private boolean donateStatus; // 기부하기 여부
}

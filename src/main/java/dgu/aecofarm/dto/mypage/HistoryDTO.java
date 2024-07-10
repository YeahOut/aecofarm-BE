package dgu.aecofarm.dto.mypage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HistoryDTO {
    private Long contractId;
    private String itemName;
    private String itemImage;
    private int time;
    private int price;
    private Boolean likeStatus;
}

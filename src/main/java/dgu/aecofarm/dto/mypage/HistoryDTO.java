package dgu.aecofarm.dto.mypage;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class HistoryDTO {
    private Long contractId;
    private String itemName;
    private String itemImage;
    private int time;
    private int price;
    private Boolean likeStatus;
}

package dgu.aecofarm.dto.contract;

import dgu.aecofarm.entity.Category;
import dgu.aecofarm.entity.Status;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ContractDetailResponseDTO {
    private Boolean owner;
    private String userName;
    private String itemName;
    private String userImage;
    private Integer price;
    private String itemImage;
    private String itemContents;
    private String itemPlace;
    private List<String> itemHash;
    private Integer time;
    private Integer contractTime;
    private Boolean likeStatus;
    private String kakao;
    private LocalDateTime createdAt;
}

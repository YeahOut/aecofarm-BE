package dgu.aecofarm.dto.contract;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetPayResponseDTO {
    private String itemName;
    private String image;
    private int price;
    private int myPoint;
    private String itemPlace;
    private int time;
    private int contractTime;
    private List<String> itemHash;
}

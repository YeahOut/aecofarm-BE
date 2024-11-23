package dgu.aecofarm.dto.contract;

import lombok.Data;

import java.util.List;

@Data
public class CreateContractRequestDTO {
    private String category;
    private String itemName;
    private String kakao;
    private List<String> itemHash;
    private int time;
    private int contractTime;
    private int price;
    private String itemPlace;
    private String itemContents;
}
package dgu.aecofarm.dto.contract;

import lombok.Data;

@Data
public class CreateContractRequestDTO {
    private String category;
    private String itemName;
    private String kakao;
    private String itemImage;
    private String[] itemHash;
    private int time;
    private int contractTime;
    private int price;
    private String itemPlace;
    private String itemContents;
}
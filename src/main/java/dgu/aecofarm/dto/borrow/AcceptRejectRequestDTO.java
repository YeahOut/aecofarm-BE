package dgu.aecofarm.dto.borrow;

import lombok.Data;

@Data
public class AcceptRejectRequestDTO {
    private boolean success; // 승인은 true, 거절은 false
    private Long contractId;
}

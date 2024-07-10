package dgu.aecofarm.dto.alarm;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AlarmStatusResponseDTO {
    private String status;
    private String memberStatus;
    private String userName;
    private Long contractId;
    private String itemName;
    private String image;
    private LocalDateTime time;
}
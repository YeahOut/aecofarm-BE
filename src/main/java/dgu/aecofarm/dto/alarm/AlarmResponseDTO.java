package dgu.aecofarm.dto.alarm;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AlarmResponseDTO {
    private List<AlarmStatusResponseDTO> lending;
    private List<AlarmStatusResponseDTO> borrowing;
}

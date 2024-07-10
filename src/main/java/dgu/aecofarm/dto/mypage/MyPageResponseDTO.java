package dgu.aecofarm.dto.mypage;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
@Builder
public class MyPageResponseDTO {
    private ProfileDTO profile;
    private List<HistoryDTO> history;

}

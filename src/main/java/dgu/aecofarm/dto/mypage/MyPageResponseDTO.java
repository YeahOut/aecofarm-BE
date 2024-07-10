package dgu.aecofarm.dto.mypage;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class MyPageResponseDTO {
    private int code;
    private String message;
    private ProfileDTO profile;
    private List<HistoryDTO> history;

}

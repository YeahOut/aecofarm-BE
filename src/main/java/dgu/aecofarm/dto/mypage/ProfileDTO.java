package dgu.aecofarm.dto.mypage;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class ProfileDTO {
    private String userName;
    private String email;
    private String image;
    private int point;
}

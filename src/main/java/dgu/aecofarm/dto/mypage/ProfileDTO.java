package dgu.aecofarm.dto.mypage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDTO {
    private String userName;
    private String email;
    private String image;
    private int point;
}

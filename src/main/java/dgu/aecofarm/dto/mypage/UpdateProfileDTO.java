package dgu.aecofarm.dto.mypage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileDTO {
    private String userName;
    private String email;
    private String image;
}

package dgu.aecofarm.dto.member;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignupRequestDTO {
    private String email;
    private String userName;
    private String password;
    private String phone;
    private int schoolNum;
    private String imageUrl;
}

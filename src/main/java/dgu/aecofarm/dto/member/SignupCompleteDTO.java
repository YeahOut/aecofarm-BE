package dgu.aecofarm.dto.member;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignupCompleteDTO {
    private SignupRequestDTO signupRequestDTO;
    private String authCode;
    private String expectedCode;
}

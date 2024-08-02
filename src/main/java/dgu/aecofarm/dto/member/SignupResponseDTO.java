package dgu.aecofarm.dto.member;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignupResponseDTO {
    private SignupRequestDTO signupRequestDTO;
    private String expectedCode;
}
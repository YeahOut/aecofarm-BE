package dgu.aecofarm.dto.member;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {
    private Long memberId;
    private String userName;
}

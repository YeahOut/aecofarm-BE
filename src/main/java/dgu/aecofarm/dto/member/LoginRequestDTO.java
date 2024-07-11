package dgu.aecofarm.dto.member;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}

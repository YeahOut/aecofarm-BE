package dgu.aecofarm.dto.member;

import lombok.Data;

@Data
public class SignupRequestDTO {
    private String email;
    private String userName;
    private String password;
    private String phone;
    private int schoolNum;
}

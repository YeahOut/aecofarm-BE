package dgu.aecofarm.dto.member;

import lombok.Data;

@Data
public class FindPasswordRequestDTO {
    private String email;
    private String userName;
    private int schoolNum;
    private String password;
}

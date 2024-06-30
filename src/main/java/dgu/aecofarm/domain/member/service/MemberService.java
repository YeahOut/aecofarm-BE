package dgu.aecofarm.domain.member.service;

import dgu.aecofarm.dto.member.JwtInfoResponseDTO;
import dgu.aecofarm.dto.member.LoginRequestDTO;
import dgu.aecofarm.dto.member.LoginResponseDTO;
import dgu.aecofarm.dto.member.SignupRequestDTO;
import dgu.aecofarm.entity.Member;

import java.util.Optional;

public interface MemberService {
    String signup(SignupRequestDTO registerRequestDTO);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    Optional<Member> getLoginUserInfoByMemberId(String memberId);

    Optional<JwtInfoResponseDTO> getLoginUserInfoByUserid(String name);
}

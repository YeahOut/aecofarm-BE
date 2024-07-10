package dgu.aecofarm.domain.member.service;

import dgu.aecofarm.dto.member.*;
import dgu.aecofarm.entity.Member;

import java.util.Optional;

public interface MemberService {
    String signup(SignupRequestDTO registerRequestDTO);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    Optional<Member> getLoginUserInfoByMemberId(String memberId);

    Optional<JwtInfoResponseDTO> getLoginUserInfoByUserid(String name);

    String findPassword(FindPasswordRequestDTO findPasswordDTO);

    String signout(String memberId);

    RecommendResponseDTO getRecommand(String memberId);

    SearchResponseDTO searchItems(SearchRequestDTO searchRequestDTO, String memberId);
}

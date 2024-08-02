package dgu.aecofarm.domain.member.service;

import dgu.aecofarm.dto.member.*;
import dgu.aecofarm.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface MemberService {
    SignupResponseDTO initiateSignup(SignupRequestDTO signupRequestDTO, String imageUrl);

    String completeSignup(SignupRequestDTO signupRequestDTO, String authCode, String expectedCode, String imageUrl);

    String uploadFile(MultipartFile file);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    Optional<Member> getLoginUserInfoByMemberId(String memberId);

    Optional<JwtInfoResponseDTO> getLoginUserInfoByUserid(String name);

    String findPassword(FindPasswordRequestDTO findPasswordDTO);

    String signout(String memberId);

    RecommendResponseDTO getRecommand(String memberId);

    SearchResponseDTO searchItems(SearchRequestDTO searchRequestDTO, String memberId);
}

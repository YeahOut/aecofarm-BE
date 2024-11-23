package dgu.aecofarm.domain.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dgu.aecofarm.domain.member.service.MemberService;
import dgu.aecofarm.dto.email.EmailResponseDto;
import dgu.aecofarm.dto.member.*;
import dgu.aecofarm.entity.Member;
import dgu.aecofarm.entity.Response;
import dgu.aecofarm.repository.ItemRepository;
import dgu.aecofarm.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public Response<?> signup(@RequestPart("signupData") SignupRequestDTO signupRequestDTO,
                              @RequestPart("file") MultipartFile file) {
        try {
            String imageUrl = memberService.uploadFile(file);
            SignupResponseDTO signupResponseDTO = memberService.initiateSignup(signupRequestDTO, imageUrl);
            signupResponseDTO.getSignupRequestDTO().setImageUrl(imageUrl);
            return Response.success(signupResponseDTO);
        } catch (Exception e) {
            return Response.failure(e);
        }
    }
    @PostMapping("/signup/complete")
    public Response<?> completeSignup(@RequestBody SignupCompleteDTO signupCompleteDTO) {
        try {
            return Response.success(memberService.completeSignup(signupCompleteDTO.getSignupRequestDTO(),
                    signupCompleteDTO.getAuthCode(),
                    signupCompleteDTO.getExpectedCode(),
                    signupCompleteDTO.getSignupRequestDTO().getImageUrl()));
        } catch (Exception e) {
            return Response.failure(e);
        }
    }
    @PostMapping("/login")
    public Response<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            LoginResponseDTO loginResponseDTO = memberService.login(loginRequestDTO);

            if (loginResponseDTO == null) {
                return Response.failure("아이디 또는 비밀번호가 불일치합니다.");
            }

            long expireTimeMs = 1000 * 60 * 60;     // Token 유효 시간 = 60분

            String jwtToken = JwtTokenUtil.createToken(loginResponseDTO.getMemberId().toString(), expireTimeMs);

            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("token", jwtToken);

            return Response.success(tokenMap);
        } catch (Exception e) {
            return Response.failure(e);
        }
    }

    @GetMapping("/jwtInfo")
    @ResponseBody
    public Response<?> userInfo(Authentication auth) {
        Optional<JwtInfoResponseDTO> jwtInfoResponseDTO = memberService.getLoginUserInfoByUserid(auth.getName());

        if (jwtInfoResponseDTO.isPresent()) {
            return Response.success(jwtInfoResponseDTO.get());
        }
        return Response.failure("사용자가 없습니다.");
    }

    @PostMapping("/logout")
    public Response<?> logout() {
        // 클라이언트 측에서 토큰 삭제
        return Response.success("로그아웃 되었습니다.");
    }

    @PostMapping("/update/pw")
    public Response<?> findPassword(@RequestBody FindPasswordRequestDTO findPasswordDTO) {
        try {
            return Response.success(memberService.findPassword(findPasswordDTO));
        } catch (Exception e) {
            return Response.failure(e);
        }
    }

    @DeleteMapping("/signout")
    public Response<?> signout(Authentication auth) {
        try {
            return Response.success(memberService.signout(auth.getName()));
        } catch (Exception e) {
            return Response.failure(e);
        }
    }

    @GetMapping("/recommand")
    public Response<?> getRecommand(Authentication auth) {
        try {
            RecommendResponseDTO recommendResponseDTO = memberService.getRecommand(auth.getName());
            return Response.success(recommendResponseDTO);
        } catch (Exception e) {
            return Response.failure(e);
        }
    }

    @PostMapping("/search")
    public Response<SearchResponseDTO> searchItems(@RequestBody SearchRequestDTO searchRequestDTO, Authentication auth) {
        try {
            return Response.success(memberService.searchItems(searchRequestDTO, auth.getName()));
        } catch (Exception e) {
            return Response.failure(e);
        }
    }
}

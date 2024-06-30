package dgu.aecofarm.domain.member.controller;

import dgu.aecofarm.domain.member.service.MemberService;
import dgu.aecofarm.dto.member.JwtInfoResponseDTO;
import dgu.aecofarm.dto.member.LoginRequestDTO;
import dgu.aecofarm.dto.member.LoginResponseDTO;
import dgu.aecofarm.dto.member.SignupRequestDTO;
import dgu.aecofarm.entity.Response;
import dgu.aecofarm.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public Response<?> signup(@RequestBody SignupRequestDTO signupRequestDTO) {
        try {
            return Response.success(memberService.signup(signupRequestDTO));
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
}

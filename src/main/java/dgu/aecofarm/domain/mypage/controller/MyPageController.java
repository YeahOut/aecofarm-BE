package dgu.aecofarm.domain.mypage.controller;

import dgu.aecofarm.domain.mypage.service.MyPageService;
import dgu.aecofarm.dto.mypage.MyPageResponseDTO;
import dgu.aecofarm.dto.mypage.UpdateProfileDTO;
import dgu.aecofarm.entity.Response;
import dgu.aecofarm.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/get")
    public Response<?> getMyPage(@RequestHeader("Authorization") String authorization) {
        Long memberId = extractMemberIdFromToken(authorization);
        MyPageResponseDTO response = myPageService.getMyPage(memberId);
        return Response.success(response);
    }

    @PatchMapping("/update")
    public Response<?> updateProfile(@RequestHeader("Authorization") String authorization, @RequestBody UpdateProfileDTO updateProfileDTO) {
        Long memberId = extractMemberIdFromToken(authorization);
        myPageService.updateProfile(memberId, updateProfileDTO);
        return Response.success("마이페이지 수정이 완료되었습니다.");
    }

    private Long extractMemberIdFromToken(String token) {
        // "Bearer " 부분을 제거하고 토큰만 추출
        String jwtToken = token.substring(7);
        // JwtTokenUtil을 사용하여 memberId 추출
        String memberId = JwtTokenUtil.getLoginId(jwtToken);
        return Long.valueOf(memberId); // 추출한 memberId를 Long 타입으로 변환
    }
}

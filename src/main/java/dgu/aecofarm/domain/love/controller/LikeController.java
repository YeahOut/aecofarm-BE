package dgu.aecofarm.domain.love.controller;

import dgu.aecofarm.dto.like.AddLikeDTO;
import dgu.aecofarm.entity.Response;
import dgu.aecofarm.domain.love.service.LikeService;
import dgu.aecofarm.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class LikeController {

    private final LikeService likeService;

    @PatchMapping("/add")
    public Response<?> addLike(@RequestHeader("Authorization") String authorization, @RequestBody AddLikeDTO addLikeDTO) {
        Long memberId = extractMemberIdFromToken(authorization);
        likeService.addLike(memberId, addLikeDTO);
        return Response.success("좋아요 추가에 성공하였습니다.");
    }

    @PatchMapping("/delete")
    public Response<?> deleteLike(@RequestHeader("Authorization") String authorization, @RequestBody AddLikeDTO addLikeDTO) {
        Long memberId = extractMemberIdFromToken(authorization);
        likeService.deleteLike(memberId, addLikeDTO);
        return Response.success("좋아요 삭제에 성공하였습니다.");
    }

    private Long extractMemberIdFromToken(String token) {
        String jwtToken = token.substring(7);  // "Bearer " 부분을 제거하고 토큰만 추출
        String memberId = JwtTokenUtil.getLoginId(jwtToken);
        return Long.valueOf(memberId);  // 추출한 memberId를 Long 타입으로 변환
    }
}

package dgu.aecofarm.domain.love.controller;


import dgu.aecofarm.dto.love.LikeListDTO;
import dgu.aecofarm.entity.Response;
import dgu.aecofarm.domain.love.service.LikeService;
import dgu.aecofarm.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/add/{contractId}")
    public Response<?> addLike(@PathVariable("contractId") Long contractId, @RequestHeader("Authorization") String authorization) {
        Long memberId = extractMemberIdFromToken(authorization);
        likeService.addLike(memberId, contractId);
        return Response.success("좋아요 추가에 성공하였습니다.");
    }

    @DeleteMapping("/delete/{contractId}")
    public Response<?> deleteLike(@PathVariable("contractId") Long contractId, @RequestHeader("Authorization") String authorization) {
        Long memberId = extractMemberIdFromToken(authorization);
        likeService.deleteLike(memberId, contractId);
        return Response.success("좋아요 삭제에 성공하였습니다.");
    }

    @GetMapping("/list")
    public Response<LikeListDTO> getLikesList(@RequestHeader("Authorization") String authorization) {
        Long memberId = extractMemberIdFromToken(authorization);
        LikeListDTO likeList = likeService.getLikesList(memberId);
        return Response.success(likeList);
    }

    private Long extractMemberIdFromToken(String token) {
        String jwtToken = token.substring(7);  // "Bearer " 부분을 제거하고 토큰만 추출
        String memberId = JwtTokenUtil.getLoginId(jwtToken);
        return Long.valueOf(memberId);  // 추출한 memberId를 Long 타입으로 변환
    }
}

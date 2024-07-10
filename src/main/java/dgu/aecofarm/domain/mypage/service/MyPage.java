package dgu.aecofarm.domain.mypage.service;
import dgu.aecofarm.dto.mypage.MyPageResponseDTO;
public interface MyPage {
    MyPageResponseDTO getMyPage(Long memberId);
}

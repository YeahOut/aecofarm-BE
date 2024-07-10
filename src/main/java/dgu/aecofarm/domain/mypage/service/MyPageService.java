package dgu.aecofarm.domain.mypage.service;

import dgu.aecofarm.dto.mypage.MyPageResponseDTO;

public interface MyPageService {
    MyPageResponseDTO getMyPage(Long memberId);
}

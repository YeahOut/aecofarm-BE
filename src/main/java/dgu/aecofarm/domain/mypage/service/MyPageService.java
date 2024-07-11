package dgu.aecofarm.domain.mypage.service;

import dgu.aecofarm.dto.mypage.*;

public interface MyPageService {
    MyPageResponseDTO getMyPage(Long memberId);
    void updateProfile(Long memberId, UpdateProfileDTO updateProfileDTO);
    MyPageContractListDTO getMyPageContracts(Long memberId);
}

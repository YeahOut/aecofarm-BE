package dgu.aecofarm.domain.mypage.service;

import dgu.aecofarm.dto.mypage.*;
import org.springframework.web.multipart.MultipartFile;

public interface MyPageService {
    MyPageResponseDTO getMyPage(Long memberId);
    void updateProfile(Long memberId, UpdateProfileDTO updateProfileDTO, MultipartFile file);
    MyPageContractListDTO getMyPageContracts(Long memberId);
}

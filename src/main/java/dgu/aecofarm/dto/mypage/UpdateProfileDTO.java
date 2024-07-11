package dgu.aecofarm.dto.mypage;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UpdateProfileDTO {
    private String userName;
    private String email;
    private MultipartFile file; // 새로운 프로필 사진 파일
    private String existingImageUrl; // 기존 프로필 사진 URL
}

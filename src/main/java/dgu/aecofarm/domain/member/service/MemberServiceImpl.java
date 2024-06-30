package dgu.aecofarm.domain.member.service;

import dgu.aecofarm.dto.member.JwtInfoResponseDTO;
import dgu.aecofarm.dto.member.LoginRequestDTO;
import dgu.aecofarm.dto.member.LoginResponseDTO;
import dgu.aecofarm.dto.member.SignupRequestDTO;
import dgu.aecofarm.entity.Member;
import dgu.aecofarm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public String signup(SignupRequestDTO signupRequestDTO) {
        String email = signupRequestDTO.getEmail();
        String userName = signupRequestDTO.getUserName();
        String password = toSHA256(signupRequestDTO.getPassword());
        String phone = signupRequestDTO.getPhone();
        int schoolNum = signupRequestDTO.getSchoolNum();
        String image = signupRequestDTO.getImage();

        Member checkDuplicate = memberRepository.findMemberByEmail(email);
        if (checkDuplicate != null) {
            throw new IllegalArgumentException("중복된 이메일입니다.");
        }

        Member member = Member.builder()
                .email(email)
                .userName(userName)
                .password(password)
                .phone(phone)
                .schoolNum(schoolNum)
                .image(image)
                .build();

        memberRepository.save(member);
        return "회원가입 성공";
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        Member member = memberRepository.findMemberByEmail(loginRequestDTO.getEmail());
        if (member == null) {
            throw new IllegalArgumentException("유효한 이메일이 아닙니다.");
        }

        String encode_password = toSHA256(loginRequestDTO.getPassword());

        if (encode_password.equals(member.getPassword())) {
            LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                    .memberId(member.getMemberId())
                    .userName(member.getUserName())
                    .build();
            return loginResponseDTO;
        }
        throw new IllegalArgumentException("비밀번호가 틀립니다.");
    }

    // Jwt Token에서 추출한 loginId로 Member 찾아오기
    public Optional<Member> getLoginUserInfoByMemberId(String memberId) {
        return memberRepository.findByMemberId(Long.valueOf(memberId));
    }

    @Override
    public Optional<JwtInfoResponseDTO> getLoginUserInfoByUserid(String memberId) {
        return memberRepository.findByMemberId(Long.valueOf(memberId)).map(member ->
                JwtInfoResponseDTO.builder()
                        .memberId(member.getMemberId())
                        .userName(member.getUserName())
                        .build());
    }

    private String toSHA256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}

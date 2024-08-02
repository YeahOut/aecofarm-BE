package dgu.aecofarm.domain.email.service;

import dgu.aecofarm.entity.Member;
import dgu.aecofarm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final MemberRepository memberRepository;

    // 임시 비밀번호를 설정하는 메서드
    public void setTempPassword(String email, String tempPassword) {
        Optional<Member> optionalMember = memberRepository.findMemberByEmail(email);
        if (!optionalMember.isPresent()) {
            throw new IllegalArgumentException("유효한 이메일이 아닙니다.");
        }
        Member member = optionalMember.get();
        member.updatePassword(tempPassword);
        memberRepository.save(member);
    }

    // 사용자 정보 조회 메서드
    public Optional<Member> getMemberByEmail(String email) {
        return memberRepository.findMemberByEmail(email);
    }

    // 사용자 비밀번호 재설정 메서드
    public void resetPassword(String email, String newPassword) {
        Optional<Member> optionalMember = memberRepository.findMemberByEmail(email);
        if (!optionalMember.isPresent()) {
            throw new IllegalArgumentException("유효한 이메일이 아닙니다.");
        }
        Member member = optionalMember.get();
        member.updatePassword(newPassword);
        memberRepository.save(member);
    }
}
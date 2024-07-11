package dgu.aecofarm.repository;

import dgu.aecofarm.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findMemberByEmail(String email);

    Optional<Member> findByMemberId(Long memberId);
}

package dgu.aecofarm.repository;

import dgu.aecofarm.entity.Item;
import dgu.aecofarm.entity.Love;
import dgu.aecofarm.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoveRepository extends JpaRepository<Love, Long> {
    boolean existsByItemAndMember(Item item, Member member);
    Optional<Love> findByMemberAndItem(Member member, Item item);
    List<Love> findByItem(Item item);

    List<Love> findByMember(Member member);

}

package dgu.aecofarm.repository;

import dgu.aecofarm.entity.Item;
import dgu.aecofarm.entity.Love;
import dgu.aecofarm.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoveRepository extends JpaRepository<Love, Long> {
    boolean existsByItemAndMember(Item item, Member member);
}

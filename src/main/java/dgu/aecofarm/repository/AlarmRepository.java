package dgu.aecofarm.repository;

import dgu.aecofarm.entity.Alarm;
import dgu.aecofarm.entity.Contract;
import dgu.aecofarm.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Optional<Alarm> findByContract(Contract contract);

    List<Alarm> findByLendMemberOrBorrowMember(Member lendMember, Member borrowMember);
}

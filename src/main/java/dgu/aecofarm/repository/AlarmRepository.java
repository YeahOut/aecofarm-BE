package dgu.aecofarm.repository;

import dgu.aecofarm.entity.Alarm;
import dgu.aecofarm.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Optional<Alarm> findByContract(Contract contract);
}

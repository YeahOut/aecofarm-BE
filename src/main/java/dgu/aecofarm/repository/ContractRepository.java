package dgu.aecofarm.repository;

import dgu.aecofarm.entity.Category;
import dgu.aecofarm.entity.Contract;
import dgu.aecofarm.entity.Member;
import dgu.aecofarm.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByCategoryAndStatus(Category category, Status status);

    List<Contract> findByCategoryAndStatusAndItemItemNameContaining(Category category, Status status, String keyword);
    List<Contract> findByMember(Member member);
}

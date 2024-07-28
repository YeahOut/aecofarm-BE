package dgu.aecofarm.repository;

import dgu.aecofarm.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByCategoryAndStatus(Category category, Status status);

    List<Contract> findByCategoryAndStatusAndItemItemNameContaining(Category category, Status status, String keyword);
    List<Contract> findByCategoryAndLendMember(Category category,Member member);
    List<Contract> findByCategoryAndBorrowMember(Category category,Member member);

    Optional<Contract> findByItem(Item item);
}

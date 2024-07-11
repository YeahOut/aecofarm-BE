package dgu.aecofarm.domain.lend.service;

import dgu.aecofarm.dto.borrow.BorrowListResponseDTO;
import dgu.aecofarm.dto.borrow.LendListResponseDTO;
import dgu.aecofarm.dto.borrow.SortType;

import java.util.List;

public interface LendService {
    String requestLend(Long contractId, String memberId);

    List<LendListResponseDTO> getLendList(String memberId, SortType sortType);
}

package dgu.aecofarm.domain.borrow.service;

import dgu.aecofarm.dto.borrow.AcceptRejectRequestDTO;
import dgu.aecofarm.dto.borrow.BorrowListResponseDTO;
import dgu.aecofarm.dto.borrow.SortType;

import java.util.List;

public interface BorrowService {
    String requestBorrow(Long contractId, String memeberId);
    String requestAcceptReject(AcceptRejectRequestDTO acceptRejectRequestDTO, String memberId);

    List<BorrowListResponseDTO> getBorrowList(String memberId, SortType sortType);
}

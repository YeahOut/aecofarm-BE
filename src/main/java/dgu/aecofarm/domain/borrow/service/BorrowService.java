package dgu.aecofarm.domain.borrow.service;

import dgu.aecofarm.dto.borrow.AcceptRejectRequestDTO;

public interface BorrowService {
    String requestBorrow(Long contractId, String memeberId);
    String requestAcceptReject(AcceptRejectRequestDTO acceptRejectRequestDTO, String memberId);
}

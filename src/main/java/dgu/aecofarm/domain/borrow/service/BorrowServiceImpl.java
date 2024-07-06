package dgu.aecofarm.domain.borrow.service;

import dgu.aecofarm.dto.contract.CreateContractRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    @Override
    public String requestBorrow(Long contractId, String memeberId) {

        return null;
    }
}

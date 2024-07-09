package dgu.aecofarm.domain.contract.service;

import dgu.aecofarm.dto.contract.*;

public interface ContractService {
    String createContract(CreateContractRequestDTO postRequestDTO, String memeberId);

    String  updateContract(Long contractId, CreateContractRequestDTO postRequestDTO, String memeberId);

    String deleteContract(Long contractId, String memberId);

    ContractDetailResponseDTO getContractDetail(Long contractId, String memberId);

    GetPayResponseDTO getPayDetails(Long contractId, String memberId);

    String payForContract(PayRequestDTO payRequestDTO, String memberId);

    GetReserveResponseDTO getReserveDetails(Long contractId);
}

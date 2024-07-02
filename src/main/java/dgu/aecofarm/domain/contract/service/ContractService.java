package dgu.aecofarm.domain.contract.service;

import dgu.aecofarm.dto.contract.CreateContractRequestDTO;

public interface ContractService {
    String createContract(CreateContractRequestDTO postRequestDTO, String memeberId);

    String  updateContract(Long contractId, CreateContractRequestDTO postRequestDTO, String memeberId);

    String deleteContract(Long contractId, String memberId);
}

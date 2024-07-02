package dgu.aecofarm.domain.contract.service;

import dgu.aecofarm.dto.contract.CreateContractRequestDTO;

public interface ContractService {
    String createContract(CreateContractRequestDTO postRequestDTO, String memeberId);
}

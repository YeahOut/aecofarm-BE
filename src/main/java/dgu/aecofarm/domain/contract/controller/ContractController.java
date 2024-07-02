package dgu.aecofarm.domain.contract.controller;

import dgu.aecofarm.domain.contract.service.ContractService;
import dgu.aecofarm.dto.contract.CreateContractRequestDTO;
import dgu.aecofarm.entity.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contract")
public class ContractController {

    private final ContractService contractService;

    @PostMapping("/post")
    public Response<?> createContract(@RequestBody CreateContractRequestDTO postRequestDTO, Authentication auth) {
        try {
            return Response.success(contractService.createContract(postRequestDTO, auth.getName()));
        } catch (Exception e) {
            return Response.failure(e);
        }
    }


}

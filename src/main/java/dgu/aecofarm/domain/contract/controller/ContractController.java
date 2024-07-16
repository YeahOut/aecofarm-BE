package dgu.aecofarm.domain.contract.controller;

import dgu.aecofarm.domain.contract.service.ContractService;
import dgu.aecofarm.domain.member.service.MemberService;
import dgu.aecofarm.dto.contract.ContractDetailResponseDTO;
import dgu.aecofarm.dto.contract.CreateContractRequestDTO;
import dgu.aecofarm.dto.contract.PayRequestDTO;
import dgu.aecofarm.dto.member.SignupRequestDTO;
import dgu.aecofarm.entity.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contract")
public class ContractController {

    private final ContractService contractService;
    private final MemberService memberService;

    @PostMapping("/post")
    public Response<?> createContract(@RequestPart("createContract") CreateContractRequestDTO createContractRequestDTO,
                                      @RequestPart("file") MultipartFile file, Authentication auth) {
        try {
            String imageUrl = memberService.uploadFile(file);
            return Response.success(contractService.createContract(imageUrl, createContractRequestDTO, auth.getName()));
        } catch (Exception e) {
            return Response.failure(e);
        }
    }

    @PutMapping("/update/{contractId}")
    public Response<?> updateContract(@PathVariable("contractId") Long contractId, @RequestPart("updateContract") CreateContractRequestDTO postRequestDTO,
                                      @RequestPart("file") MultipartFile file, Authentication auth) {
        try {
            String imageUrl = memberService.uploadFile(file);
            return Response.success(contractService.updateContract(imageUrl, contractId, postRequestDTO, auth.getName()));
        } catch (Exception e) {
            return Response.failure(e);
        }
    }

    @DeleteMapping("/delete/{contractId}")
    public Response<?> deleteContract(@PathVariable("contractId") Long contractId, Authentication auth) {
        try {
            return Response.success(contractService.deleteContract(contractId, auth.getName()));
        } catch (Exception e) {
            return Response.failure(e);
        }
    }

    @GetMapping("/detail/{contractId}")
    public Response<?> getContractDetail(@PathVariable("contractId") Long contractId, Authentication auth) {
        try {
            ContractDetailResponseDTO contractDetail = contractService.getContractDetail(contractId, auth.getName());
            return Response.success(contractDetail);
        } catch (Exception e) {
            return Response.failure(e.getMessage());
        }
    }

    @GetMapping("/get/pay/{contractId}")
    public Response<?> getPayDetails(@PathVariable("contractId") Long contractId, Authentication auth) {
        return Response.success(contractService.getPayDetails(contractId, auth.getName()));
    }

    @PostMapping("/pay")
    public Response<?> payForContract(@RequestBody PayRequestDTO payRequestDTO, Authentication auth) {
        try {
            return Response.success(contractService.payForContract(payRequestDTO, auth.getName()));
        } catch (Exception e) {
            return Response.failure(e);
        }
    }

    @GetMapping("/get/reserve/{contractId}")
    public Response<?> getReserveDetails(@PathVariable("contractId") Long contractId) {
        try {
            return Response.success(contractService.getReserveDetails(contractId));
        } catch (Exception e) {
            return Response.failure(e.getMessage());
        }
    }
}

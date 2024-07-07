package dgu.aecofarm.domain.lend.controller;

import dgu.aecofarm.domain.lend.service.LendService;
import dgu.aecofarm.entity.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lend")
public class LendController {

    private final LendService lendService;

    @PatchMapping("/reqeust/{contractId}")
    public Response<?> requestLend(@PathVariable("contractId") Long contractId, Authentication auth) {
        try {
            return Response.success(lendService.requestLend(contractId, auth.getName()));
        } catch (Exception e) {
            return Response.failure(e);
        }
    }
}

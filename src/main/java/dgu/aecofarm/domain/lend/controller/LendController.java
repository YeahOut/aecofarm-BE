package dgu.aecofarm.domain.lend.controller;

import dgu.aecofarm.domain.lend.service.LendService;
import dgu.aecofarm.dto.borrow.SortType;
import dgu.aecofarm.entity.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/list")
    public Response<?> getLendList(@RequestParam(value = "sortType", required = false, defaultValue = "NEWEST") SortType sortType, Authentication auth) {
        try {
            return Response.success(lendService.getLendList(auth.getName(), sortType));
        } catch (Exception e) {
            return Response.failure(e);
        }
    }
}

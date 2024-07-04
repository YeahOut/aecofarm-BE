package dgu.aecofarm.domain.borrow.controller;

import dgu.aecofarm.domain.borrow.service.BorrowService;
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
@RequestMapping("/borrow")
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping("/reqeust")
    public Response<?> requestBorrow() {
        try {
            return Response.success();
        } catch (Exception e) {
            return Response.failure(e);
        }
    }


}

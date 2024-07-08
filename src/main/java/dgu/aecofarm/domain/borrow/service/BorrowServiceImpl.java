package dgu.aecofarm.domain.borrow.service;

import dgu.aecofarm.dto.contract.CreateContractRequestDTO;
import dgu.aecofarm.entity.*;
import dgu.aecofarm.exception.InvalidUserIdException;
import dgu.aecofarm.repository.AlarmRepository;
import dgu.aecofarm.repository.ContractRepository;
import dgu.aecofarm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final ContractRepository contractRepository;
    private final MemberRepository memberRepository;
    private final AlarmRepository alarmRepository;

    @Transactional
    public String requestBorrow(Long contractId, String memberId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("유효한 계약 ID가 아닙니다."));

        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        if (contract.getBorrowMember() != null) {
            throw new IllegalArgumentException("이미 대여 요청된 계약입니다.");
        }

        contract.updateBorrowMember(member);
        contract.updateStatus(Status.REQUESTED);
        contractRepository.save(contract);

        Alarm alarm = Alarm.builder()
                .contract(contract)
                .category(Category.BORROW)
                .borrowMember(member)
                .lendMember(contract.getLendMember())
                .alarmStatus(alarmStatus.REQUEST)
                .time(LocalDateTime.now())
                .build();

        alarmRepository.save(alarm);

        return "대여 요청에 성공하였습니다.";
    }
}

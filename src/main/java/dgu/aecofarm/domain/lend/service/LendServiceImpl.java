package dgu.aecofarm.domain.lend.service;

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
public class LendServiceImpl implements LendService {

    private final ContractRepository contractRepository;
    private final MemberRepository memberRepository;
    private final AlarmRepository alarmRepository;

    @Transactional
    public String requestLend(Long contractId, String memberId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("유효한 계약 ID가 아닙니다."));

        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        if (contract.getLendMember() != null) {
            throw new IllegalArgumentException("이미 빌려주기 요청된 계약입니다.");
        }

        contract.updateLendMember(member);
        contract.updateStatus(Status.BEFOREPAY);
        contractRepository.save(contract);

        Alarm alarm = Alarm.builder()
                .contract(contract)
                .category(Category.LEND)
                .borrowMember(contract.getBorrowMember())
                .lendMember(member)
                .alarmStatus(alarmStatus.REQUEST)
                .time(LocalDateTime.now())
                .build();

        alarmRepository.save(alarm);

        return "빌려주기 요청에 성공하였습니다.";
    }
}

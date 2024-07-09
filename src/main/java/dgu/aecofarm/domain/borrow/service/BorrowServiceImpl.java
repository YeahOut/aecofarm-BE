package dgu.aecofarm.domain.borrow.service;

import dgu.aecofarm.dto.borrow.AcceptRejectRequestDTO;
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
                .alarmStatus(AlarmStatus.REQUEST)
                .time(LocalDateTime.now())
                .build();

        alarmRepository.save(alarm);

        return "대여 요청에 성공하였습니다.";
    }

    @Transactional
    public String requestAcceptReject(AcceptRejectRequestDTO acceptRejectRequestDTO, String memberId) {
        Contract contract = contractRepository.findById(acceptRejectRequestDTO.getContractId())
                .orElseThrow(() -> new IllegalArgumentException("유효한 계약 ID가 아닙니다."));

        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        if (!contract.getLendMember().equals(member)) {
            throw new IllegalArgumentException("승인 또는 거절 권한이 없습니다.");
        }

        if (contract.getStatus() != Status.REQUESTED) {
            throw new IllegalArgumentException("대여 요청 상태가 아닙니다.");
        }

        Alarm alarm = alarmRepository.findByContract(contract)
                .orElseThrow(() -> new IllegalArgumentException("유효한 알람 ID가 아닙니다."));

        LocalDateTime now = LocalDateTime.now();

        if (acceptRejectRequestDTO.isSuccess()) {
            contract.updateStatus(Status.BEFOREPAY);
            contract.updateSuccessTime(now); // successTime을 현재 시간으로 설정
            alarm.updateStatus(AlarmStatus.ACCEPT);
            alarm.updateTime(now); // 알람의 시간을 현재 시간으로 설정
        } else {
            contract.updateStatus(Status.NONE);
            contract.updateBorrowMember(null);
            alarm.updateStatus(AlarmStatus.REJECT);
            alarm.updateTime(now); // 알람의 시간을 현재 시간으로 설정
        }

        contractRepository.save(contract);
        alarmRepository.save(alarm);

        return acceptRejectRequestDTO.isSuccess() ? "대여 요청을 승인하였습니다." : "대여 요청을 거절하였습니다.";
    }
}

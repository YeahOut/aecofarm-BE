package dgu.aecofarm.domain.alarm.service;

import dgu.aecofarm.dto.alarm.AlarmResponseDTO;
import dgu.aecofarm.dto.alarm.AlarmStatusResponseDTO;
import dgu.aecofarm.entity.Alarm;
import dgu.aecofarm.entity.Category;
import dgu.aecofarm.entity.Member;
import dgu.aecofarm.exception.InvalidUserIdException;
import dgu.aecofarm.repository.AlarmRepository;
import dgu.aecofarm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AlarmServiceImpl implements AlarmService {

    private final AlarmRepository alarmRepository;
    private final MemberRepository memberRepository;

    public AlarmResponseDTO getAlarmStatus(String memberId) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        List<Alarm> alarms = alarmRepository.findByLendMemberOrBorrowMember(member, member);

        List<AlarmStatusResponseDTO> lending = alarms.stream()
                .filter(alarm -> alarm.getContract().getCategory() == Category.LEND)
                .map(alarm -> toAlarmStatusResponseDTO(alarm, member))
                .collect(Collectors.toList());

        List<AlarmStatusResponseDTO> borrowing = alarms.stream()
                .filter(alarm -> alarm.getContract().getCategory() == Category.BORROW)
                .map(alarm -> toAlarmStatusResponseDTO(alarm, member))
                .collect(Collectors.toList());

        return AlarmResponseDTO.builder()
                .lending(lending)
                .borrowing(borrowing)
                .build();
    }

    private AlarmStatusResponseDTO toAlarmStatusResponseDTO(Alarm alarm, Member member) {
        String status = alarm.getAlarmStatus().name();
        String memberStatus;

        if (alarm.getContract().getCategory() == Category.LEND) {
            memberStatus = alarm.getLendMember().equals(member) ? "LEND" : "BORROW";
        } else {
            memberStatus = alarm.getBorrowMember().equals(member) ? "BORROW" : "LEND";
        }

        String userName = memberStatus.equals("LEND") ? alarm.getBorrowMember().getUserName() : alarm.getLendMember().getUserName();

        return AlarmStatusResponseDTO.builder()
                .status(status)
                .memberStatus(memberStatus)
                .userName(userName)
                .contractId(alarm.getContract().getContractId())
                .itemName(alarm.getContract().getItem().getItemName())
                .image(alarm.getContract().getItem().getItemImage())
                .time(alarm.getTime())
                .build();
    }
}
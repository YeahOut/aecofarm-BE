package dgu.aecofarm.domain.borrow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dgu.aecofarm.dto.borrow.AcceptRejectRequestDTO;
import dgu.aecofarm.dto.borrow.BorrowListResponseDTO;
import dgu.aecofarm.dto.borrow.SortType;
import dgu.aecofarm.entity.*;
import dgu.aecofarm.exception.InvalidUserIdException;
import dgu.aecofarm.repository.AlarmRepository;
import dgu.aecofarm.repository.ContractRepository;
import dgu.aecofarm.repository.LoveRepository;
import dgu.aecofarm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final ContractRepository contractRepository;
    private final MemberRepository memberRepository;
    private final AlarmRepository alarmRepository;
    private final LoveRepository loveRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public String requestBorrow(Long contractId, String memberId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("유효한 계약 ID가 아닙니다."));

        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        if (contract.getCategory() != Category.BORROW) {
            throw new IllegalArgumentException("대여 가능한 물품이 아닙니다.");
        }

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
        } else {
            contract.updateStatus(Status.NONE);
            contract.updateBorrowMember(null);
            alarm.updateStatus(AlarmStatus.REJECT);
        }

        alarm.updateTime(now); // 알람의 시간을 현재 시간으로 설정
        contractRepository.save(contract);
        alarmRepository.save(alarm);

        return acceptRejectRequestDTO.isSuccess() ? "대여 요청을 승인하였습니다." : "대여 요청을 거절하였습니다.";
    }

    @Transactional
    public List<BorrowListResponseDTO> getBorrowList(String memberId, SortType sortType) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        List<Contract> contracts = contractRepository.findByCategoryAndStatus(Category.BORROW, Status.NONE);

        List<BorrowListResponseDTO> resultList = contracts.stream().map(contract -> {
            Item item = contract.getItem();
            List<String> itemHashList;
            try {
                itemHashList = objectMapper.readValue(item.getItemHash(), List.class);
            } catch (IOException e) {
                throw new RuntimeException("아이템 해시를 리스트로 변환하는데 실패했습니다.", e);
            }
            boolean likeStatus = loveRepository.existsByItemAndMember(item, member);
            boolean donateStatus = item.getPrice() == 0;

            return BorrowListResponseDTO.builder()
                    .contractId(contract.getContractId())
                    .itemId(item.getItemId())
                    .itemName(item.getItemName())
                    .itemImage(item.getItemImage())
                    .price(item.getPrice())
                    .itemPlace(item.getItemPlace())
                    .time(item.getTime())
                    .contractTime(item.getContractTime())
                    .itemHash(itemHashList)
                    .likeStatus(likeStatus)
                    .donateStatus(donateStatus)
                    .build();
        }).collect(Collectors.toList());

        // 정렬 기준에 따라 정렬
        if (sortType == SortType.NEWEST) {
            resultList.sort(Comparator.comparing(BorrowListResponseDTO::getContractId).reversed());
        } else if (sortType == SortType.PRICE_ASC) {
            resultList.sort(Comparator.comparing(BorrowListResponseDTO::getPrice));
        } else if (sortType == SortType.PRICE_DESC) {
            resultList.sort(Comparator.comparing(BorrowListResponseDTO::getPrice).reversed());
        } else if (sortType == SortType.DISTANCE) {
            resultList.sort(Comparator.comparing(BorrowListResponseDTO::getContractTime));
        }

        return resultList;
    }
}
package dgu.aecofarm.domain.lend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dgu.aecofarm.dto.borrow.BorrowListResponseDTO;
import dgu.aecofarm.dto.borrow.LendListResponseDTO;
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
@Transactional
public class LendServiceImpl implements LendService {

    private final ContractRepository contractRepository;
    private final MemberRepository memberRepository;
    private final AlarmRepository alarmRepository;
    private final LoveRepository loveRepository;
    private final ObjectMapper objectMapper;

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
                .alarmStatus(AlarmStatus.REQUEST)
                .time(LocalDateTime.now())
                .build();

        alarmRepository.save(alarm);

        return "빌려주기 요청에 성공하였습니다.";
    }

    public List<LendListResponseDTO> getLendList(String memberId, SortType sortType) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        List<Contract> contracts = contractRepository.findByCategoryAndStatus(Category.LEND, Status.NONE);

        List<LendListResponseDTO> resultList = contracts.stream().map(contract -> {
            Item item = contract.getItem();
            List<String> itemHashList;
            try {
                itemHashList = objectMapper.readValue(item.getItemHash(), List.class);
            } catch (IOException e) {
                throw new RuntimeException("아이템 해시를 리스트로 변환하는데 실패했습니다.", e);
            }
            boolean likeStatus = loveRepository.existsByItemAndMember(item, member);
            boolean donateStatus = item.getPrice() == 0;

            return LendListResponseDTO.builder()
                    .contractId(contract.getContractId())
                    .itemId(item.getItemId())
                    .itemName(item.getItemName())
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
            resultList.sort(Comparator.comparing(LendListResponseDTO::getContractId).reversed());
        } else if (sortType == SortType.PRICE_ASC) {
            resultList.sort(Comparator.comparing(LendListResponseDTO::getPrice));
        } else if (sortType == SortType.PRICE_DESC) {
            resultList.sort(Comparator.comparing(LendListResponseDTO::getPrice).reversed());
        } else if (sortType == SortType.DISTANCE) {
            resultList.sort(Comparator.comparing(LendListResponseDTO::getContractTime));
        }

        return resultList;
    }
}

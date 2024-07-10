package dgu.aecofarm.domain.mypage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dgu.aecofarm.dto.mypage.*;
import dgu.aecofarm.entity.*;
import dgu.aecofarm.exception.InvalidUserIdException;
import dgu.aecofarm.repository.ContractRepository;
import dgu.aecofarm.repository.LoveRepository;
import dgu.aecofarm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

    private final MemberRepository memberRepository;
    private final ContractRepository contractRepository;
    private final LoveRepository loveRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public MyPageResponseDTO getMyPage(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        ProfileDTO profile = ProfileDTO.builder()
                .userName(member.getUserName())
                .email(member.getEmail())
                .image(member.getImage())
                .point(member.getPoint())
                .build();

        List<Object> rawRecentList;
        List<Long> recentContractIds;
        try {
            rawRecentList = member.getRecent() == null ? List.of() : objectMapper.readValue(member.getRecent(), List.class);
            recentContractIds = rawRecentList.stream()
                    .map(id -> {
                        if (id instanceof Integer) {
                            return ((Integer) id).longValue();
                        } else if (id instanceof Long) {
                            return (Long) id;
                        } else {
                            throw new IllegalArgumentException("유효하지 않은 ID 타입입니다.");
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("최근 본 물품을 로드하는데 실패했습니다.", e);
        }

        // 최근 본 물품을 역순으로 가져오기
        List<Contract> recentContracts = new ArrayList<>();
        for (int i = recentContractIds.size() - 1; i >= 0; i--) {
            Long contractId = recentContractIds.get(i);
            contractRepository.findById(contractId).ifPresent(recentContracts::add);
        }

        List<HistoryDTO> historyList = recentContracts.stream().map(contract -> {
            boolean likeStatus = loveRepository.existsByItemAndMember(contract.getItem(), member);

            return HistoryDTO.builder()
                    .contractId(contract.getContractId())
                    .itemName(contract.getItem().getItemName())
                    .itemImage(contract.getItem().getItemImage())
                    .time(contract.getItem().getTime())
                    .price(contract.getItem().getPrice())
                    .likeStatus(likeStatus)
                    .build();
        }).collect(Collectors.toList());

        return MyPageResponseDTO.builder()
                .profile(profile)
                .history(historyList)
                .build();
    }
}

package dgu.aecofarm.domain.mypage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dgu.aecofarm.dto.mypage.*;
import dgu.aecofarm.entity.*;
import dgu.aecofarm.exception.InvalidUserIdException;
import dgu.aecofarm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

    private final MemberRepository memberRepository;
    private final ContractRepository contractRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public MyPageResponseDTO getMyPage(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        ProfileDTO profile = new ProfileDTO();
        profile.setUserName(member.getUserName());
        profile.setEmail(member.getEmail());
        profile.setImage(member.getImage());
        profile.setPoint(member.getPoint());

        List<Contract> contracts = contractRepository.findByLendMemberOrBorrowMember(member, member);
        List<HistoryDTO> historyList = contracts.stream().map(contract -> {
            HistoryDTO history = new HistoryDTO();
            history.setContractId(contract.getContractId());
            history.setItemName(contract.getItem().getItemName());
            history.setItemImage(contract.getItem().getItemImage());
            history.setTime(contract.getSuccessTime() != null ? contract.getSuccessTime().getHour() : 0);
            history.setPrice(contract.getItem().getPrice());
            // Like status 로직 추가 필요
            history.setLikeStatus(false);
            return history;
        }).collect(Collectors.toList());

        MyPageResponseDTO response = new MyPageResponseDTO();
        response.setCode(200);
        response.setMessage("SUCCESS");
        response.setProfile(profile);
        response.setHistory(historyList);

        return response;
    }
}

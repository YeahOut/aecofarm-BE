package dgu.aecofarm.domain.contract.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dgu.aecofarm.dto.contract.CreateContractRequestDTO;
import dgu.aecofarm.entity.*;
import dgu.aecofarm.exception.InvalidUserIdException;
import dgu.aecofarm.repository.ContractRepository;
import dgu.aecofarm.repository.ItemRepository;
import dgu.aecofarm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public String createContract(CreateContractRequestDTO createContractRequestDTO, String memberId) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        String itemHashJson;
        try {
            itemHashJson = objectMapper.writeValueAsString(createContractRequestDTO.getItemHash());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("아이템 해시를 JSON으로 변환하는데 실패했습니다.", e);
        }

        Item item = Item.builder()
                .itemName(createContractRequestDTO.getItemName())
                .price(createContractRequestDTO.getPrice())
                .itemImage(createContractRequestDTO.getItemImage())
                .itemContents(createContractRequestDTO.getItemContents())
                .itemPlace(createContractRequestDTO.getItemPlace())
                .itemHash(itemHashJson)
                .time(createContractRequestDTO.getTime())
                .contractTime(createContractRequestDTO.getContractTime())
                .kakao(createContractRequestDTO.getKakao())
                .click(0) // 기본값 설정
                .createdAt(LocalDateTime.now())
                .build();

        itemRepository.save(item);

        Contract contract = Contract.builder()
                .lendMember(createContractRequestDTO.getCategory().equals("LEND") ? member : null)
                .borrowMember(createContractRequestDTO.getCategory().equals("BORROW") ? member : null)
                .item(item)
                .category(Category.valueOf(createContractRequestDTO.getCategory()))
                .status(Status.NONE)
                .askTime(LocalDateTime.now())
                .build();

        contractRepository.save(contract);
        return "게시글 등록에 성공하였습니다.";
    }
}

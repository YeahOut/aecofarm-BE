package dgu.aecofarm.domain.contract.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dgu.aecofarm.dto.contract.*;
import dgu.aecofarm.entity.*;
import dgu.aecofarm.exception.InvalidUserIdException;
import dgu.aecofarm.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final AlarmRepository alarmRepository;
    private final LoveRepository loveRepository;
    private final ObjectMapper objectMapper;

    public String createContract(String imageUrl, CreateContractRequestDTO createContractRequestDTO, String memberId) {
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
                .itemImage(imageUrl)
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
                .lendMember(createContractRequestDTO.getCategory().equals("BORROW") ? member : null)
                .borrowMember(createContractRequestDTO.getCategory().equals("LEND") ? member : null)
                .item(item)
                .category(Category.valueOf(createContractRequestDTO.getCategory()))
                .status(Status.NONE)
                .askTime(LocalDateTime.now())
                .build();

        contractRepository.save(contract);
        return "게시글 등록에 성공하였습니다.";
    }

    public String updateContract(String imageUrl, Long contractId, CreateContractRequestDTO createContractRequestDTO, String memberId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("유효한 계약 ID가 아닙니다."));

        String itemHashJson;
        try {
            itemHashJson = objectMapper.writeValueAsString(createContractRequestDTO.getItemHash());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("아이템 해시를 JSON으로 변환하는데 실패했습니다.", e);
        }

        Item item = contract.getItem();
        item.updateItemName(createContractRequestDTO.getItemName());
        item.updatePrice(createContractRequestDTO.getPrice());
        item.updateItemImage(imageUrl);
        item.updateItemContents(createContractRequestDTO.getItemContents());
        item.updateItemPlace(createContractRequestDTO.getItemPlace());
        item.updateItemHash(itemHashJson);
        item.updateTime(createContractRequestDTO.getTime());
        item.updateContractTime(createContractRequestDTO.getContractTime());
        item.updateKakao(createContractRequestDTO.getKakao());
        itemRepository.save(item);

        contract.updateItem(item);
        contract.updateCategory(Category.valueOf(createContractRequestDTO.getCategory()));
        contractRepository.save(contract);

        return "게시글 수정에 성공하였습니다.";
    }

    public String deleteContract(Long contractId, String memberId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("유효한 계약 ID가 아닙니다."));

        // 아이템 삭제
        Item item = contract.getItem();
        contractRepository.delete(contract); // 먼저 계약 삭제
        itemRepository.delete(item); // 그 다음 아이템 삭제
        return "게시글 삭제에 성공하였습니다.";
    }

    public ContractDetailResponseDTO getContractDetail(Long contractId, String memberId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("없는 게시글 입니다."));

        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        Item item = contract.getItem();

        String nickname;
        String userImage;
        if (contract.getCategory() == Category.BORROW) {
            nickname = contract.getLendMember().getUserName();
            userImage = contract.getLendMember().getImage();
        } else {
            nickname = contract.getBorrowMember().getUserName();
            userImage = contract.getBorrowMember().getImage();
        }

        boolean likeStatus = loveRepository.existsByItemAndMember(item, member);

        // 수정, 삭제 권한 체크
        boolean hasPermission = false;
        if (contract.getCategory() == Category.BORROW && contract.getLendMember() != null && contract.getLendMember().equals(member)) {
            hasPermission = true;
        } else if (contract.getCategory() == Category.LEND && contract.getBorrowMember() != null && contract.getBorrowMember().equals(member)) {
            hasPermission = true;
        }

        List<String> itemHashList;
        try {
            itemHashList = objectMapper.readValue(item.getItemHash(), List.class);
        } catch (IOException e) {
            throw new RuntimeException("아이템 해시를 리스트로 변환하는데 실패했습니다.", e);
        }

        // 조회할 때마다 클릭 횟수를 증가시킴
        item.updateClickCount();
        itemRepository.save(item);

        // 최근 본 물품에 추가
        try {
            if (contract.getCategory() == Category.BORROW) {
                // Integer -> Long 으로 변환
                List<Integer> rawRecentList = member.getRecent() == null ? new ArrayList<>() : objectMapper.readValue(member.getRecent(), List.class);
                List<Long> recentList = new ArrayList<>();
                for (Object id : rawRecentList) {
                    if (id instanceof Integer) {
                        recentList.add(((Integer) id).longValue());
                    } else if (id instanceof Long) {
                        recentList.add((Long) id);
                    }
                }

                // 중복된 물품이 있는 경우 제거
                int index = recentList.indexOf(contractId);
                if (index != -1) {
                    recentList.remove(index);
                    System.out.println(index);
                }

                // 맨 마지막에 추가
                recentList.add(contractId);
                member.updateRecent(objectMapper.writeValueAsString(recentList));
                memberRepository.save(member);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("최근 본 물품 업데이트 중 오류 발생", e);
        }

        return ContractDetailResponseDTO.builder()
                .owner(hasPermission)
                .userName(nickname)
                .userImage(userImage)
                .itemName(item.getItemName())
                .price(item.getPrice())
                .itemImage(item.getItemImage())
                .itemContents(item.getItemContents())
                .itemPlace(item.getItemPlace())
                .itemHash(itemHashList)
                .time(item.getTime())
                .likeStatus(likeStatus)
                .contractTime(item.getContractTime())
                .kakao(item.getKakao())
                .createdAt(item.getCreatedAt())
                .build();
    }

    public GetPayResponseDTO getPayDetails(Long contractId, String memberId) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("유효한 계약 ID가 아닙니다."));

        // status가 BEFOREPAY가 아닐 경우
        if (contract.getStatus() != Status.BEFOREPAY) {
            throw new IllegalArgumentException("결제 가능한 상태가 아닙니다.");
        }

        // 사용자가 해당 계약에 대한 권한이 있는지 확인
        if ((contract.getCategory() == Category.BORROW && !contract.getBorrowMember().equals(member)) ||
                (contract.getCategory() == Category.LEND && !contract.getBorrowMember().equals(member))) {
            throw new IllegalArgumentException("해당 contract에 대한 권한이 없습니다.");
        }

        Item item = contract.getItem();

        List<String> itemHashList;
        try {
            itemHashList = objectMapper.readValue(item.getItemHash(), List.class);
        } catch (IOException e) {
            throw new RuntimeException("아이템 해시를 리스트로 변환하는데 실패했습니다.", e);
        }

        return GetPayResponseDTO.builder()
                .itemName(item.getItemName())
                .image(item.getItemImage())
                .price(item.getPrice())
                .myPoint(member.getPoint())
                .itemPlace(item.getItemPlace())
                .time(item.getTime())
                .contractTime(item.getContractTime())
                .itemHash(itemHashList)
                .build();
    }

    public String payForContract(PayRequestDTO payRequestDTO, String memberId) {
        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        Contract contract = contractRepository.findById(payRequestDTO.getContractId())
                .orElseThrow(() -> new IllegalArgumentException("유효한 계약 ID가 아닙니다."));

        // status가 BEFOREPAY가 아닐 경우
        if (contract.getStatus() != Status.BEFOREPAY) {
            throw new IllegalArgumentException("결제 가능한 상태가 아닙니다.");
        }

        Member lendMember = contract.getLendMember();
        Member borrowMember = contract.getBorrowMember();

        // 사용자가 해당 계약에 대한 권한이 있는지 확인
        if ((contract.getCategory() == Category.BORROW && !borrowMember.equals(member)) ||
                (contract.getCategory() == Category.LEND && !borrowMember.equals(member))) {
            throw new IllegalArgumentException("해당 contract에 대한 권한이 없습니다.");
        }

        // 포인트 차감 및 적립
        int point = payRequestDTO.getPoint();
        if (borrowMember.getPoint() < point) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        lendMember.updatePoint(lendMember.getPoint() + point);
        borrowMember.updatePoint(borrowMember.getPoint() - point);

        memberRepository.save(lendMember);
        memberRepository.save(borrowMember);

        // contract 상태 업데이트 및 alarm 상태 업데이트
        contract.updateStatus(Status.COMPLETED);
        contract.updateSuccessTime(LocalDateTime.now());
        contractRepository.save(contract);

        Alarm alarm = alarmRepository.findByContract(contract)
                .orElseThrow(() -> new IllegalArgumentException("유효한 알람 ID가 아닙니다."));

        LocalDateTime now = LocalDateTime.now();

        alarm.updateStatus(AlarmStatus.COMPLETE);
        alarm.updateTime(now); // 알람의 시간을 현재 시간으로 설정
        alarmRepository.save(alarm);

        return "결제가 완료되었습니다.";
    }

    public GetReserveResponseDTO getReserveDetails(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("유효한 계약 ID가 아닙니다."));

        // contract 상태가 NONE인지 확인
        if (contract.getStatus() != Status.NONE) {
            throw new IllegalArgumentException("예약 가능한 상태가 아닙니다.");
        }

        Item item = contract.getItem();

        List<String> itemHashList;
        try {
            itemHashList = objectMapper.readValue(item.getItemHash(), List.class);
        } catch (IOException e) {
            throw new RuntimeException("아이템 해시를 리스트로 변환하는데 실패했습니다.", e);
        }

        return GetReserveResponseDTO.builder()
                .itemName(item.getItemName())
                .image(item.getItemImage())
                .price(item.getPrice())
                .itemPlace(item.getItemPlace())
                .time(item.getTime())
                .contractTime(item.getContractTime())
                .itemHash(itemHashList)
                .build();
    }
}
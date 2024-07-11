package dgu.aecofarm.domain.love.service;

import dgu.aecofarm.dto.love.LikeListDTO;
import dgu.aecofarm.entity.*;
import dgu.aecofarm.exception.InvalidUserIdException;
import dgu.aecofarm.repository.ContractRepository;
import dgu.aecofarm.repository.ItemRepository;
import dgu.aecofarm.repository.LoveRepository;
import dgu.aecofarm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final LoveRepository loveRepository;
    private final ContractRepository contractRepository;

    @Override
    @Transactional
    public void addLike(Long memberId, Long contractId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("유효한 계약 ID가 아닙니다."));

        if (contract.getStatus() != Status.NONE) {
            throw new IllegalArgumentException("좋아요를 누를 수 없는 상태입니다.");
        }

        Item item = contract.getItem();

        Optional<Love> existingLove = loveRepository.findByMemberAndItem(member, item);

        if (!existingLove.isPresent()) {
            Love love = Love.builder()
                    .member(member)
                    .contract(contract)
                    .item(item)
                    .build();
            loveRepository.save(love);
        }
    }

    @Override
    @Transactional
    public void deleteLike(Long memberId, Long contractId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("유효한 계약 ID가 아닙니다."));

        if (contract.getStatus() != Status.NONE) {
            throw new IllegalArgumentException("좋아요를 취소할 수 없는 상태입니다.");
        }

        Item item = contract.getItem();

        Optional<Love> existingLove = loveRepository.findByMemberAndItem(member, item);

        if (existingLove.isPresent()) {
            loveRepository.delete(existingLove.get());
        } else {
            throw new IllegalArgumentException("좋아요가 존재하지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public LikeListDTO getLikesList(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        List<Love> loves = loveRepository.findByMember(member);

        List<LikeListDTO.LendingItem> lendingItems = loves.stream()
                .filter(love -> love.getContract().getCategory() == Category.LEND)
                .filter(love -> love.getContract().getStatus() == Status.NONE)
                .map(love -> new LikeListDTO.LendingItem(
                        love.getContract().getContractId(),
                        love.getItem().getItemName(),
                        love.getItem().getPrice(),
                        love.getItem().getTime()))
                .collect(Collectors.toList());
        Collections.reverse(lendingItems);

        List<LikeListDTO.BorrowingItem> borrowingItems = loves.stream()
                .filter(love -> love.getContract().getCategory() == Category.BORROW)
                .filter(love -> love.getContract().getStatus() == Status.NONE)
                .map(love -> new LikeListDTO.BorrowingItem(
                        love.getContract().getContractId(),
                        love.getItem().getItemName(),
                        love.getItem().getItemImage(),
                        love.getItem().getPrice(),
                        love.getItem().getTime()))
                .collect(Collectors.toList());
        Collections.reverse(borrowingItems);

        return new LikeListDTO(lendingItems, borrowingItems);
    }
}
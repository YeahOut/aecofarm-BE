package dgu.aecofarm.domain.love.service;

import dgu.aecofarm.dto.love.AddLikeDTO;
import dgu.aecofarm.dto.love.LikeListDTO;
import dgu.aecofarm.entity.Item;
import dgu.aecofarm.entity.Love;
import dgu.aecofarm.entity.Member;
import dgu.aecofarm.exception.InvalidUserIdException;
import dgu.aecofarm.repository.ItemRepository;
import dgu.aecofarm.repository.LoveRepository;
import dgu.aecofarm.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final LoveRepository loveRepository;

    @Override
    @Transactional
    public void addLike(Long memberId, AddLikeDTO addLikeDTO) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        Item item = itemRepository.findById(addLikeDTO.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("유효한 물품 ID가 아닙니다."));

        Optional<Love> existingLove = loveRepository.findByMemberAndItem(member, item);

        if (!existingLove.isPresent()) {
            Love love = Love.builder()
                    .member(member)
                    .item(item)
                    .build();
            loveRepository.save(love);
        }
    }

    @Override
    @Transactional
    public void deleteLike(Long memberId, AddLikeDTO addLikeDTO) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        Item item = itemRepository.findById(addLikeDTO.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("유효한 물품 ID가 아닙니다."));

        Optional<Love> existingLove = loveRepository.findByMemberAndItem(member, item);

        existingLove.ifPresent(loveRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public LikeListDTO getLikesList(Long memberId, Long itemId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidUserIdException("유효한 사용자 ID가 아닙니다."));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("유효한 물품 ID가 아닙니다."));

        List<Love> loves = loveRepository.findByItem(item);

        List<LikeListDTO.LendingItem> lendingItems = loves.stream()
                .map(love -> new LikeListDTO.LendingItem(
                        love.getLikeId(),
                        love.getItem().getItemName(),
                        love.getItem().getPrice(),
                        love.getItem().getTime()))
                .collect(Collectors.toList());

        List<LikeListDTO.BorrowingItem> borrowingItems = loves.stream()
                .map(love -> new LikeListDTO.BorrowingItem(
                        love.getLikeId(),
                        love.getItem().getItemName(),
                        love.getItem().getItemImage(),
                        love.getItem().getPrice(),
                        love.getItem().getTime()))
                .collect(Collectors.toList());

        return new LikeListDTO(lendingItems, borrowingItems);
    }



}

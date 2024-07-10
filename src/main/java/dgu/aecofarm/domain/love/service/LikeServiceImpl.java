package dgu.aecofarm.domain.love.service;

import dgu.aecofarm.dto.like.AddLikeDTO;
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

        if (loveRepository.existsByMemberAndItem(member, item)) {
            throw new IllegalArgumentException("이미 좋아요를 누른 물품입니다.");
        }

        Love love = Love.builder()
                .member(member)
                .item(item)
                .build();

        loveRepository.save(love);
    }
}

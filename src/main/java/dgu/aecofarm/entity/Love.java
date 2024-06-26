package dgu.aecofarm.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "love")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)public class Love {

    // 좋아요 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    // 회원 아이디
    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Member member;

    // 물품 아이디
    @ManyToOne
    @JoinColumn(name = "itemId", nullable = false)
    private Item item;
}

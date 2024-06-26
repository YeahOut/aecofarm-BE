package dgu.aecofarm.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Table(name = "contract")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contract {

    // 체결 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractId;

    // 빌려주는 사람
    @ManyToOne
    @JoinColumn(name = "lendId", nullable = false)
    private Member lendMember;

    // 빌리는 사람
    @ManyToOne
    @JoinColumn(name = "borrowId", nullable = false)
    private Member borrowMember;

    // 물품 아이디
    @ManyToOne
    @JoinColumn(name = "itemId", nullable = false)
    private Item item;

    // 카테고리 (빌려주기, 빌리기 게시판)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    // 요청 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    // 대여 완료 시간
    private LocalDateTime successTime;

    // 대여 요청 시간
    private LocalDateTime askTime;

    @OneToMany(mappedBy = "contract")
    private List<Alarm> alarms;
}

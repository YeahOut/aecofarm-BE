package dgu.aecofarm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter
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
    @JoinColumn(name = "lendId")
    private Member lendMember;

    // 빌리는 사람
    @ManyToOne
    @JoinColumn(name = "borrowId")
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

    // 수정 메서드
    public void updateItem(Item item) {
        this.item = item;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }

    public void updateBorrowMember(Member member) {
        this.borrowMember = member;
    }

    public void updateLendMember(Member member) {
        this.lendMember = member;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }

    public void updateSuccessTime(LocalDateTime now) {
        this.successTime = now;
    }
}

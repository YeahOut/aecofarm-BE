package dgu.aecofarm.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "alarm")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Alarm {

    // 알림 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alarmId;

    // 체결 아이디
    @ManyToOne
    @JoinColumn(name = "contractId", nullable = false)
    private Contract contract;

    // 알림 시간
    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private alarmStatus alarmStatus;

    @Column(nullable = false)
    private Category category;

    // 빌려주는 사람
    @ManyToOne
    @JoinColumn(name = "lendId", nullable = false)
    private Member lendMember;

    // 빌리는 사람
    @ManyToOne
    @JoinColumn(name = "borrowId", nullable = false)
    private Member borrowMember;
}

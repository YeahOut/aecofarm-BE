package dgu.aecofarm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@Table(name = "member")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    // 회원 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    // 회원 이름
    @Column(nullable = false)
    private String userName;

    // 이메일
    @Column(nullable = false)
    private String email;

    // 비밀번호
    @Column(nullable = false)
    private String password;

    // 전화번호
    @Column(nullable = false)
    private String phone;

    // 학번
    @Column(nullable = false)
    private Integer schoolNum;

    // 사진
    private String image;

    // 포인트
    @Column(nullable = false)
    private int point;

    // 빌려준 횟수
    private int lendCount;

    // 빌린 횟수
    private int borrowCount;

    // 최근 본 물품
    private String recent;

    @OneToMany(mappedBy = "member")
    private List<Love> likes;

    @OneToMany(mappedBy = "lendMember", cascade = CascadeType.REMOVE)
    private List<Contract> lendContracts;

    @OneToMany(mappedBy = "borrowMember", cascade = CascadeType.REMOVE)
    private List<Contract> borrowContracts;

    @OneToMany(mappedBy = "lendMember", cascade = CascadeType.REMOVE)
    private List<Alarm> lendAlarms;

    @OneToMany(mappedBy = "borrowMember", cascade = CascadeType.REMOVE)
    private List<Alarm> borrowAlarms;

    // 비밀번호 변경
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    // 최근 본 물품 업데이트
    public void updateRecent(String recent) {
        this.recent = recent;
    }

    // 포인트 적립 or 차감
    public void updatePoint(int newPoint) {
        this.point = newPoint;
    }

    public void updateProfile(String userName, String email, String image) {
        this.userName = userName;
        this.email = email;
        this.image = image;
    }
}

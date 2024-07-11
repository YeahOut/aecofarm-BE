package dgu.aecofarm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter
@Table(name = "item")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item {

    // 물품 아이디
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;

    // 상품명
    @Column(nullable = false)
    private String itemName;

    // 물품 가격
    @Column(nullable = false)
    private Integer price;

    // 물품 사진
    private String itemImage;

    // 물품 설명
    private String itemContents;

    // 물품 거래 장소
    private String itemPlace;

    // 물품 해시태그
    private String itemHash;

    // 대여 가능 시간
    private Integer time;

    // 거래 가능 시간
    private Integer contractTime;

    // 오픈채팅방 링크
    private String kakao;

    // 물품 조회 횟수
    @Column(nullable = false)
    private Integer click;

    // 글 작성 시간
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "item")
    private List<Love> likes;

    @OneToMany(mappedBy = "item")
    private List<Contract> contracts;

    // 수정 메서드
    public void updateItemName(String itemName) {
        this.itemName = itemName;
    }

    public void updatePrice(Integer price) {
        this.price = price;
    }

    public void updateItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public void updateItemContents(String itemContents) {
        this.itemContents = itemContents;
    }

    public void updateItemPlace(String itemPlace) {
        this.itemPlace = itemPlace;
    }

    public void updateItemHash(String itemHash) {
        this.itemHash = itemHash;
    }

    public void updateTime(Integer time) {
        this.time = time;
    }

    public void updateContractTime(Integer contractTime) {
        this.contractTime = contractTime;
    }

    public void updateKakao(String kakao) {
        this.kakao = kakao;
    }

    public void updateClickCount() {
        this.click += 1;
    }
}

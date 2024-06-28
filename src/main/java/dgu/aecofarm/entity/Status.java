package dgu.aecofarm.entity;

public enum Status {
    NONE,       // 아무 요청이 없는 상태
    REQUESTED,  // 요청이 된 상태

    BOFOREPAY, // 결제 전 상태

    COMPLETED   // 대여 완료된 상태
}

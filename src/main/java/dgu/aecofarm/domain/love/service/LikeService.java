package dgu.aecofarm.domain.love.service;


import dgu.aecofarm.dto.love.LikeListDTO;

public interface LikeService {
    void addLike(Long memberId, Long contractId);
    void deleteLike(Long memberId, Long contractId);
    LikeListDTO getLikesList(Long memberId);
}

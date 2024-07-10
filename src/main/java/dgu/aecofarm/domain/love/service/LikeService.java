package dgu.aecofarm.domain.love.service;

import dgu.aecofarm.dto.like.AddLikeDTO;
import dgu.aecofarm.dto.like.LikeListDTO;

public interface LikeService {
    void addLike(Long memberId, AddLikeDTO addLikeDTO);
    void deleteLike(Long memberId, AddLikeDTO addLikeDTO);
    LikeListDTO getLikesList(Long memberId, Long itemId);
}

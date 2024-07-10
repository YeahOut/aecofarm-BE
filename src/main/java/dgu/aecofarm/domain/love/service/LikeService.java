package dgu.aecofarm.domain.love.service;

import dgu.aecofarm.dto.like.AddLikeDTO;

public interface LikeService {
    void addLike(Long memberId, AddLikeDTO addLikeDTO);
}

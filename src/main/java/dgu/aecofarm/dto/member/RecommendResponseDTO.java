package dgu.aecofarm.dto.member;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RecommendResponseDTO {
    private List<String> recommendedKeywords;
    private List<String> hotSearchRankings;
}

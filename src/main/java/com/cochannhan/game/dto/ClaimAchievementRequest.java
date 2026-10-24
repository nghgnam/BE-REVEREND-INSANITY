package com.cochannhan.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimAchievementRequest {
    private int rewardStones;
    private int rewardPoints;
    private String achievementId;
}

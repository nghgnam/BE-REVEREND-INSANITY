package com.cochannhan.game.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GambleRockResponse {
    @JsonProperty("success")
    private boolean isSuccess;
    private String outcomeType; // "RARE_GU", "STONES", "RUBBLE"
    private String rewardName;
    private String message;
    private int remainingStones;
    private Long characterGuId;
    private String guCode;
    private int sellValue;
    private boolean isMutated;
}

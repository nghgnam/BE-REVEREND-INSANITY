package com.cochannhan.game.dto;

import com.cochannhan.game.model.Aptitude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AwakenRequest {
    private String name;
    private String customScenario; // null for random, or "PHUONG_NGUYEN_REBIRTH"
}

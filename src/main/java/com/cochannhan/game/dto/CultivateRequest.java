package com.cochannhan.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CultivateRequest {
    private int stonesUsed; // optional primeval stones burned to accelerate breakthrough
}

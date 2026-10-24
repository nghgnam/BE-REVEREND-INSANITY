package com.cochannhan.game.dto;

import com.cochannhan.game.model.CombatDistance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombatActionRequest {
    private String actionType; // "USE_GU", "ABSORB_STONE", "MOVE_CLOSER", "MOVE_AWAY", "FLEE"
    private Long characterGuId;
}

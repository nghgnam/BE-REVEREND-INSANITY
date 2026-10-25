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
public class CombatStateDto {
    private String combatId;
    private int playerHp;
    private int playerMaxHp;
    private int playerEssence;
    private int playerMaxEssence;
    private int playerAp;
    private int playerMaxAp;

    private String enemyName;
    private String enemyTitle;
    private int enemyHp;
    private int enemyMaxHp;
    private int enemyEssence;
    private List<String> enemyKnownGus;
    private int enemyUnknownGuCount;

    private CombatDistance distance;
    private int turnNumber;
    private boolean isPlayerTurn;
    private boolean isFinished;
    private boolean isPlayerWon;
    private int playerAtkBuffTurns;
    private int playerAtkBuffPercent;
    private int damageImmunityHits;
    private int evadeHits;
    private String rewardMessage;
    private List<String> combatLogs;
}

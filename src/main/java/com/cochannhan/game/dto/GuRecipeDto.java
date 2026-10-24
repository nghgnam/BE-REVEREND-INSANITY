package com.cochannhan.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuRecipeDto {
    private Long id;
    private String recipeName;

    private String primaryGuCode;
    private String primaryGuName;
    private String primaryGuIcon;
    private int primaryGuRank;

    private String secondaryGuCode;
    private String secondaryGuName;
    private String secondaryGuIcon;
    private int secondaryGuRank;

    private String materialName;

    private String resultGuCode;
    private String resultGuName;
    private String resultGuIcon;
    private int resultGuRank;
    private String resultGuPath;

    private int stoneCost;
    private int successRate;
    private int backlashDamage;
    private String environmentCondition;
    private boolean isUnlocked;
    private String description;
}

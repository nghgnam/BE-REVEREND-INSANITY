package com.cochannhan.game.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gu_recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuRecipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipeName;
    private String primaryGuCode;
    private String secondaryGuCode;
    private String materialName;
    private String resultGuCode;
    private int stoneCost;
    private int successRate; // in percent (e.g. 70)
    private int backlashDamage;
    private String environmentCondition;
    private boolean isUnlocked;
}

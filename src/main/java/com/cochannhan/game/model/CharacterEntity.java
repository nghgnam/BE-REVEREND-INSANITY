package com.cochannhan.game.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "characters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Aptitude aptitude;

    private int aptitudePercentage;

    @Enumerated(EnumType.STRING)
    private CultivationRank rank;

    @Enumerated(EnumType.STRING)
    private SubStage subStage;

    private int breakThroughExp;

    private int maxEssence;
    private int currentEssence;

    private int maxHp;
    private int currentHp;

    private int maxAp;

    private int currentLifespanYears;
    private int maxLifespanYears;

    private int primevalStones;
    private int contributionPoints;

    @Enumerated(EnumType.STRING)
    private Alignment alignment;

    private int daoMarksLuc;
    private int daoMarksViem;
    private int daoMarksHuyet;

    private int springAutumnCicadaStress;
    private int rebirthCount;
}

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

    @Builder.Default
    private Integer permanentBonusAtk = 0;

    @Builder.Default
    private Integer permanentBonusDef = 0;

    @Builder.Default
    private Integer elementalResistPercent = 0;

    @Builder.Default
    private Integer talentPoints = 2;

    @Builder.Default
    @Column(length = 1000)
    private String unlockedTalents = "";

    private int springAutumnCicadaStress;
    private int rebirthCount;

    public int getPermanentBonusAtk() {
        return permanentBonusAtk != null ? permanentBonusAtk : 0;
    }

    public int getPermanentBonusDef() {
        return permanentBonusDef != null ? permanentBonusDef : 0;
    }

    public int getElementalResistPercent() {
        return elementalResistPercent != null ? elementalResistPercent : 0;
    }

    public int getTalentPoints() {
        return talentPoints != null ? talentPoints : 2;
    }

    public String getUnlockedTalents() {
        return unlockedTalents != null ? unlockedTalents : "";
    }

    public boolean hasTalent(String talentCode) {
        if (unlockedTalents == null || unlockedTalents.isEmpty() || talentCode == null) {
            return false;
        }
        for (String t : unlockedTalents.split(",")) {
            if (t.trim().equalsIgnoreCase(talentCode.trim())) {
                return true;
            }
        }
        return false;
    }
}

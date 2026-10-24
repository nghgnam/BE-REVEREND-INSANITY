package com.cochannhan.game.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "character_gus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterGu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long characterId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gu_definition_id", nullable = false)
    private GuDefinition guDefinition;

    private int currentHungerHours;
    private boolean isEquipped;
    private Integer slotIndex;
    private boolean isHibernating;
    private boolean isStarving;
    private boolean isMutated;
    private int mutationBonus; // e.g. +30% power
    private String mutationPassive; // e.g. "Xuyên giáp 15%", "Giảm 20% Chân Nguyên tiêu hao"

    public void feed(int hours) {
        this.currentHungerHours = Math.min(this.guDefinition.getMaxHungerHours(), this.currentHungerHours + hours);
        this.isStarving = false;
    }

    public void decayHunger(int hours) {
        int decay = isHibernating ? Math.max(1, (int)(hours * 0.2)) : hours;
        this.currentHungerHours = Math.max(0, this.currentHungerHours - decay);
        if (this.currentHungerHours == 0) {
            this.isStarving = true;
        }
    }
}

package com.cochannhan.game.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gu_definitions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GuDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    private int rank;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, columnDefinition = "varchar(50)")
    private GuType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "optimal_distance", length = 50, columnDefinition = "varchar(50)")
    private CombatDistance optimalDistance;

    private int primevalCostPercent;
    private int apCost;
    private int baseDamage;
    private int baseDefense;
    private int baseHeal;

    @Builder.Default
    private boolean consumable = false;
    @Builder.Default
    private int bonusHp = 0;
    @Builder.Default
    private int bonusAtk = 0;
    @Builder.Default
    private int bonusDef = 0;

    private String foodType;
    private int maxHungerHours;

    private String pathName; // Lưu phái: Lực Đạo, Biến Hóa Đạo, Viêm Đạo, Thủy Đạo, Lôi Đạo, Phong Đạo, Trí Đạo, Vận Đạo, Thời Gian Đạo, Không Gian Đạo, Nô Đạo, Huyết Đạo, Cốt Đạo, Thâu Đạo, Trinh Sát, Trị Liệu, Phòng Ngự, Đặc Biệt
    private String slotCategory; // CONG_KICH, PHONG_NGU, THAN_PHAP, PHU_TRO, DAC_BIET, TIEN_CO_NGAM

    @Column(length = 1000)
    private String description;

    private String icon;
}

package com.cochannhan.game.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game_state")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameStateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long characterId;

    private int currentDay;

    @Enumerated(EnumType.STRING)
    private DayPeriod currentPeriod;

    private int wolfTideDaysRemaining;
    private int xuanThuThienPressureDays;

    private String location;
    private int clanSuspicion;

    private String marketEvent;

    private int lastStipendClaimedDay;

    @Column(length = 2000)
    private String lastEventLog;
}

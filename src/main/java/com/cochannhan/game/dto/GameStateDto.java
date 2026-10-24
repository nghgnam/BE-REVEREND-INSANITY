package com.cochannhan.game.dto;

import com.cochannhan.game.model.CharacterEntity;
import com.cochannhan.game.model.CharacterGu;
import com.cochannhan.game.model.GameStateEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameStateDto {
    private CharacterEntity character;
    private GameStateEntity gameState;
    private List<CharacterGu> equippedGus;
    private List<CharacterGu> allGus;
    private String announcement;
}

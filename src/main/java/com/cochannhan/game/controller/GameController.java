package com.cochannhan.game.controller;

import com.cochannhan.game.dto.AwakenRequest;
import com.cochannhan.game.dto.ClaimAchievementRequest;
import com.cochannhan.game.dto.CultivateRequest;
import com.cochannhan.game.dto.GameStateDto;
import com.cochannhan.game.model.CharacterEntity;
import com.cochannhan.game.model.CharacterGu;
import com.cochannhan.game.model.GameStateEntity;
import com.cochannhan.game.repository.CharacterGuRepository;
import com.cochannhan.game.repository.CharacterRepository;
import com.cochannhan.game.repository.GameStateRepository;
import com.cochannhan.game.service.AwakeningService;
import com.cochannhan.game.service.ClanQuestService;
import com.cochannhan.game.service.CultivationService;
import com.cochannhan.game.service.RebirthService;
import com.cochannhan.game.service.TimeEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GameController {

    private final AwakeningService awakeningService;
    private final TimeEngineService timeEngineService;
    private final CultivationService cultivationService;
    private final RebirthService rebirthService;
    private final ClanQuestService clanQuestService;
    private final CharacterRepository characterRepository;
    private final CharacterGuRepository characterGuRepository;
    private final GameStateRepository gameStateRepository;

    @PostMapping("/awaken")
    public ResponseEntity<GameStateDto> awaken(@RequestBody(required = false) AwakenRequest request) {
        if (request == null) {
            request = new AwakenRequest();
        }
        return ResponseEntity.ok(awakeningService.awakenCharacter(request));
    }

    @GetMapping("/state/{characterId}")
    public ResponseEntity<GameStateDto> getGameState(@PathVariable Long characterId) {
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân vật không tồn tại"));

        GameStateEntity state = gameStateRepository.findByCharacterId(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Trạng thái game không tồn tại"));

        List<CharacterGu> gus = characterGuRepository.findByCharacterId(characterId);

        return ResponseEntity.ok(GameStateDto.builder()
                .character(character)
                .gameState(state)
                .equippedGus(gus.stream().filter(CharacterGu::isEquipped).toList())
                .allGus(gus)
                .announcement(state.getLastEventLog())
                .build());
    }

    @PostMapping("/next-period/{characterId}")
    public ResponseEntity<GameStateDto> nextPeriod(@PathVariable Long characterId) {
        return ResponseEntity.ok(timeEngineService.advancePeriod(characterId));
    }

    @PostMapping("/mission/{characterId}/{missionType}")
    public ResponseEntity<GameStateDto> executeMission(
            @PathVariable Long characterId,
            @PathVariable String missionType) {
        return ResponseEntity.ok(clanQuestService.executeMission(characterId, missionType));
    }

    @PostMapping("/cultivate/{characterId}")
    public ResponseEntity<GameStateDto> cultivate(
            @PathVariable Long characterId,
            @RequestBody(required = false) CultivateRequest request) {
        if (request == null) {
            request = new CultivateRequest(0);
        }
        return ResponseEntity.ok(cultivationService.cultivate(characterId, request));
    }

    @PostMapping("/rebirth/{characterId}")
    public ResponseEntity<GameStateDto> rebirth(@PathVariable Long characterId) {
        return ResponseEntity.ok(rebirthService.triggerRebirth(characterId));
    }

    @PostMapping("/claim-stipend/{characterId}")
    public ResponseEntity<GameStateDto> claimStipend(@PathVariable Long characterId) {
        return ResponseEntity.ok(clanQuestService.claimDailyStipend(characterId));
    }

    @PostMapping("/claim-achievement/{characterId}")
    public ResponseEntity<GameStateDto> claimAchievement(
            @PathVariable Long characterId,
            @RequestBody ClaimAchievementRequest request) {
        int stones = request != null ? request.getRewardStones() : 0;
        int points = request != null ? request.getRewardPoints() : 0;
        String id = request != null && request.getAchievementId() != null ? request.getAchievementId() : "ACH";
        return ResponseEntity.ok(clanQuestService.claimAchievement(characterId, stones, points, id));
    }
}

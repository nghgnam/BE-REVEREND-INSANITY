package com.cochannhan.game.controller;

import com.cochannhan.game.dto.CombatActionRequest;
import com.cochannhan.game.dto.CombatStateDto;
import com.cochannhan.game.service.CombatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/combat")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CombatController {

    private final CombatService combatService;

    @PostMapping("/start/{characterId}")
    public ResponseEntity<CombatStateDto> startCombat(
            @PathVariable Long characterId,
            @RequestParam(defaultValue = "DIEN_LANG") String enemyType) {
        return ResponseEntity.ok(combatService.startCombat(characterId, enemyType));
    }

    @PostMapping("/action/{characterId}")
    public ResponseEntity<CombatStateDto> executeAction(
            @PathVariable Long characterId,
            @RequestBody CombatActionRequest request) {
        return ResponseEntity.ok(combatService.executeAction(characterId, request));
    }

    @GetMapping("/state/{characterId}")
    public ResponseEntity<CombatStateDto> getCombatState(@PathVariable Long characterId) {
        return ResponseEntity.ok(combatService.getCombatState(characterId));
    }
}

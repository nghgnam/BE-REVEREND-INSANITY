package com.cochannhan.game.controller;

import com.cochannhan.game.dto.GameStateDto;
import com.cochannhan.game.dto.GuRecipeDto;
import com.cochannhan.game.dto.RefineGuRequest;
import com.cochannhan.game.service.GuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gu")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GuController {

    private final GuService guService;

    @PostMapping("/feed/{characterId}/{characterGuId}")
    public ResponseEntity<GameStateDto> feedGu(
            @PathVariable Long characterId,
            @PathVariable Long characterGuId) {
        return ResponseEntity.ok(guService.feedGu(characterId, characterGuId));
    }

    @PostMapping("/equip/{characterId}/{characterGuId}")
    public ResponseEntity<GameStateDto> toggleEquip(
            @PathVariable Long characterId,
            @PathVariable Long characterGuId) {
        return ResponseEntity.ok(guService.toggleEquip(characterId, characterGuId));
    }

    @PostMapping("/use/{characterId}/{characterGuId}")
    public ResponseEntity<GameStateDto> useGu(
            @PathVariable Long characterId,
            @PathVariable Long characterGuId) {
        return ResponseEntity.ok(guService.useGu(characterId, characterGuId));
    }

    @PostMapping("/hibernate/{characterId}/{characterGuId}")
    public ResponseEntity<GameStateDto> toggleHibernate(
            @PathVariable Long characterId,
            @PathVariable Long characterGuId) {
        return ResponseEntity.ok(guService.toggleHibernate(characterId, characterGuId));
    }

    @PostMapping("/refine/{characterId}")
    public ResponseEntity<GameStateDto> refineGu(
            @PathVariable Long characterId,
            @RequestBody RefineGuRequest request) {
        return ResponseEntity.ok(guService.refineGu(characterId, request));
    }

    @GetMapping("/recipes")
    public ResponseEntity<List<GuRecipeDto>> getAllRecipes() {
        return ResponseEntity.ok(guService.getAllRecipes());
    }
}

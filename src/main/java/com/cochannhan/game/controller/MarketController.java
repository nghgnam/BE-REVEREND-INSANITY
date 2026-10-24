package com.cochannhan.game.controller;

import com.cochannhan.game.dto.GambleRockResponse;
import com.cochannhan.game.dto.GameStateDto;
import com.cochannhan.game.dto.MarketItemDto;
import com.cochannhan.game.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/market")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MarketController {

    private final MarketService marketService;

    @PostMapping("/gamble-rock/{characterId}")
    public ResponseEntity<GambleRockResponse> gambleRock(@PathVariable Long characterId) {
        return ResponseEntity.ok(marketService.gambleRock(characterId));
    }

    @PostMapping("/sell-gu/{characterId}/{characterGuId}")
    public ResponseEntity<GameStateDto> sellGu(
            @PathVariable Long characterId,
            @PathVariable Long characterGuId) {
        return ResponseEntity.ok(marketService.sellGu(characterId, characterGuId));
    }

    @PostMapping("/buy-gu/{characterId}/{guCode}")
    public ResponseEntity<GameStateDto> buyGu(
            @PathVariable Long characterId,
            @PathVariable String guCode) {
        return ResponseEntity.ok(marketService.buyGu(characterId, guCode));
    }

    @PostMapping("/exchange-clan-gu/{characterId}/{guCode}")
    public ResponseEntity<GameStateDto> exchangeClanGu(
            @PathVariable Long characterId,
            @PathVariable String guCode) {
        return ResponseEntity.ok(marketService.exchangeClanGu(characterId, guCode));
    }

    @GetMapping("/shop-items")
    public ResponseEntity<List<MarketItemDto>> getShopItems() {
        return ResponseEntity.ok(marketService.getShopItems());
    }

    @GetMapping("/clan-vault-items")
    public ResponseEntity<List<MarketItemDto>> getClanVaultItems() {
        return ResponseEntity.ok(marketService.getClanVaultItems());
    }
}

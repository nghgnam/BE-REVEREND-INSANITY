package com.cochannhan.game.service;

import com.cochannhan.game.dto.GameStateDto;
import com.cochannhan.game.model.*;
import com.cochannhan.game.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RebirthService {

    private final CharacterRepository characterRepository;
    private final CharacterGuRepository characterGuRepository;
    private final GuDefinitionRepository guDefinitionRepository;
    private final GameStateRepository gameStateRepository;

    @Transactional
    public GameStateDto triggerRebirth(Long characterId) {
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân vật không tồn tại"));

        GameStateEntity state = gameStateRepository.findByCharacterId(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy trạng thái game"));

        // Verify character possesses Xuân Thu Thiền
        List<CharacterGu> oldGus = characterGuRepository.findByCharacterId(characterId);
        boolean hasXuanThuThien = oldGus.stream()
                .anyMatch(cg -> "XUAN_THU_THIEN".equals(cg.getGuDefinition().getCode()));
        if (!hasXuanThuThien) {
            throw new IllegalStateException("Nhân vật không sở hữu Xuân Thu Thiền, không thể kích hoạt tự bạo trùng sinh!");
        }

        // Increase rebirth count
        character.setRebirthCount(character.getRebirthCount() + 1);
        character.setSpringAutumnCicadaStress(25);

        // Reset cultivation back to Rank 1 So Giai
        character.setRank(CultivationRank.NHAT_CHUYEN);
        character.setSubStage(SubStage.SO_GIAI);
        character.setBreakThroughExp(0);
        int baseEssence = (int) Math.round(CultivationRank.NHAT_CHUYEN.getBaseEssencePool() * (character.getAptitudePercentage() / 100.0));
        character.setMaxEssence(baseEssence);
        character.setCurrentEssence(baseEssence);
        character.setMaxHp(100);
        character.setCurrentHp(100);
        character.setPrimevalStones(20); // Small bonus for memory of past life

        // Retain Xuân Thu Thiền + Starter Nguyệt Quang Cổ, clear other common gus
        characterGuRepository.deleteAll(oldGus);

        GuDefinition nguyetQuang = guDefinitionRepository.findByCode("NGUYET_QUANG_CO").orElseThrow();
        GuDefinition xuanThuThien = guDefinitionRepository.findByCode("XUAN_THU_THIEN").orElseThrow();

        CharacterGu g1 = CharacterGu.builder()
                .characterId(characterId)
                .guDefinition(nguyetQuang)
                .currentHungerHours(nguyetQuang.getMaxHungerHours())
                .isEquipped(true)
                .slotIndex(0)
                .isHibernating(false)
                .isStarving(false)
                .build();

        CharacterGu g2 = CharacterGu.builder()
                .characterId(characterId)
                .guDefinition(xuanThuThien)
                .currentHungerHours(xuanThuThien.getMaxHungerHours())
                .isEquipped(false)
                .slotIndex(null)
                .isHibernating(false)
                .isStarving(false)
                .build();

        List<CharacterGu> newGus = characterGuRepository.saveAll(List.of(g1, g2));

        // Reset GameState to Day 1
        state.setCurrentDay(1);
        state.setCurrentPeriod(DayPeriod.SANG);
        state.setWolfTideDaysRemaining(45);
        state.setXuanThuThienPressureDays(120);
        state.setLocation("Học Đường Gia Tộc");

        String rebirthMsg = String.format("⏳ [XUÂN THU THIỀN NGHỊCH LƯU QUANG ÂM] Tự bạo trùng sinh thành công kiếp thứ %d! " +
                "Ký ức 500 năm thức tỉnh! Cổ Nguyệt Phương Nguyên mở mắt trở lại ngày đầu Khai Khiếu!", character.getRebirthCount());
        state.setLastEventLog(rebirthMsg);

        characterRepository.save(character);
        gameStateRepository.save(state);

        return GameStateDto.builder()
                .character(character)
                .gameState(state)
                .equippedGus(newGus.stream().filter(CharacterGu::isEquipped).toList())
                .allGus(newGus)
                .announcement(rebirthMsg)
                .build();
    }
}

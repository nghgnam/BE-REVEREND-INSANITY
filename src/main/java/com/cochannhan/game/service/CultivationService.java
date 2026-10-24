package com.cochannhan.game.service;

import com.cochannhan.game.dto.CultivateRequest;
import com.cochannhan.game.dto.GameStateDto;
import com.cochannhan.game.model.*;
import com.cochannhan.game.repository.CharacterGuRepository;
import com.cochannhan.game.repository.CharacterRepository;
import com.cochannhan.game.repository.GameStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CultivationService {

    private final CharacterRepository characterRepository;
    private final CharacterGuRepository characterGuRepository;
    private final GameStateRepository gameStateRepository;

    @Transactional
    public GameStateDto cultivate(Long characterId, CultivateRequest request) {
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân vật không tồn tại"));

        GameStateEntity state = gameStateRepository.findByCharacterId(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy trạng thái game"));

        int essenceCost = (int)(character.getMaxEssence() * 0.3);
        if (character.getCurrentEssence() < essenceCost) {
            throw new IllegalStateException("Chân nguyên không đủ để cọ rửa vách Không Khiếu (cần ít nhất 30% chân nguyên).");
        }

        int stonesToUse = Math.max(0, request.getStonesUsed());
        if (stonesToUse > character.getPrimevalStones()) {
            throw new IllegalStateException("Không đủ Nguyên Thạch trong túi!");
        }

        // Spend resources
        character.setCurrentEssence(character.getCurrentEssence() - essenceCost);
        character.setPrimevalStones(character.getPrimevalStones() - stonesToUse);

        // Check for equipped Tửu Trùng (Wine Gu) - boosts aperture wall cleansing
        boolean hasEquippedTuuTrung = characterGuRepository.findByCharacterId(characterId).stream()
                .anyMatch(g -> g.isEquipped() && (
                        g.getGuDefinition().getCode().contains("TUU_TRUNG") || 
                        g.getGuDefinition().getName().contains("Tửu Trùng")
                ));
        int tuuTrungBonus = hasEquippedTuuTrung ? 35 : 0;
        String wineNotice = hasEquippedTuuTrung ? " 🍶 [Tửu Trùng tinh luyện chân nguyên +35 điểm!]" : "";

        // Experience gained: base + bonus from stones + bonus from wine gu
        int expGained = 25 + (stonesToUse * 15) + tuuTrungBonus;
        int currentExp = character.getBreakThroughExp() + expGained;
        int expRequired = character.getSubStage().getBreakThroughExpRequired();

        String message;
        if (currentExp >= expRequired) {
            // Check breakthrough
            SubStage currentStage = character.getSubStage();
            if (currentStage == SubStage.DINH_PHONG) {
                // Advance Rank
                CultivationRank nextRank = getNextRank(character.getRank());
                if (nextRank != null) {
                    character.setRank(nextRank);
                    character.setSubStage(SubStage.SO_GIAI);
                    character.setBreakThroughExp(0);
                    int newMaxEssence = (int) Math.round(nextRank.getBaseEssencePool() * (character.getAptitudePercentage() / 100.0));
                    character.setMaxEssence(newMaxEssence);
                    character.setCurrentEssence(newMaxEssence);
                    character.setMaxHp(character.getMaxHp() + 80);
                    character.setCurrentHp(character.getMaxHp());
                    message = String.format("🎉 ĐỘT PHÁ THÀNH CÔNG! Vách không khiếu nứt toác, Chân nguyên chuyển hóa thành %s! Cảnh giới đạt %s Sơ Giai!%s",
                            nextRank.getEssenceType(), nextRank.getRankName(), wineNotice);
                } else {
                    character.setBreakThroughExp(expRequired);
                    message = "Đã đạt đỉnh phong tuyệt đối của phàm nhân!" + wineNotice;
                }
            } else {
                // Advance SubStage
                SubStage nextSubStage = getNextSubStage(currentStage);
                character.setSubStage(nextSubStage);
                character.setBreakThroughExp(0);
                character.setMaxHp(character.getMaxHp() + 25);
                character.setCurrentHp(character.getMaxHp());
                message = String.format("✨ Đột phá thành công tiểu cảnh giới! Đạt %s %s!%s",
                        character.getRank().getRankName(), nextSubStage.getStageName(), wineNotice);
            }
        } else {
            character.setBreakThroughExp(currentExp);
            message = String.format("Dùng chân nguyên cọ rửa vách khiếu thành công (+%d điểm tiến độ%s, %d/%d).",
                    expGained, wineNotice, currentExp, expRequired);
        }

        characterRepository.save(character);
        state.setLastEventLog(message);
        gameStateRepository.save(state);

        var gus = characterGuRepository.findByCharacterId(characterId);

        return GameStateDto.builder()
                .character(character)
                .gameState(state)
                .equippedGus(gus.stream().filter(CharacterGu::isEquipped).toList())
                .allGus(gus)
                .announcement(message)
                .build();
    }

    private SubStage getNextSubStage(SubStage current) {
        return switch (current) {
            case SO_GIAI -> SubStage.TRUNG_GIAI;
            case TRUNG_GIAI -> SubStage.CAO_GIAI;
            case CAO_GIAI -> SubStage.DINH_PHONG;
            case DINH_PHONG -> SubStage.DINH_PHONG;
        };
    }

    private CultivationRank getNextRank(CultivationRank current) {
        return switch (current) {
            case NHAT_CHUYEN -> CultivationRank.NHI_CHUYEN;
            case NHI_CHUYEN -> CultivationRank.TAM_CHUYEN;
            case TAM_CHUYEN -> CultivationRank.TU_CHUYEN;
            case TU_CHUYEN -> CultivationRank.NGU_CHUYEN;
            case NGU_CHUYEN -> null;
        };
    }
}

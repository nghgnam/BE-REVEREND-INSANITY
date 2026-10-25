package com.cochannhan.game.service;

import com.cochannhan.game.dto.AwakenRequest;
import com.cochannhan.game.dto.GameStateDto;
import com.cochannhan.game.model.*;
import com.cochannhan.game.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwakeningService {

    private final CharacterRepository characterRepository;
    private final GuDefinitionRepository guDefinitionRepository;
    private final CharacterGuRepository characterGuRepository;
    private final GameStateRepository gameStateRepository;
    private final Random random = new Random();

    @Transactional
    public GameStateDto awakenCharacter(AwakenRequest request) {
        String name = (request.getName() == null || request.getName().trim().isEmpty())
                ? "Cổ Nguyệt Phương Nguyên" : request.getName().trim();

        Aptitude aptitude;
        int percentage;
        int starterStones;
        int starterTalentPoints = 2;
        String extremePhysiqueTitle = "";

        String[] tenExtremePhysiques = {
            "Cổ Nguyệt Âm Hoang Thể",
            "Bắc Minh Băng Phách Thể",
            "Đại Lực Chân Vũ Thể",
            "Thái Nhật Dương Mãng Thể",
            "Vạn Thọ Vô Cương Thể",
            "Tuyệt Không Cương Trụ Thể",
            "Hỗn Nguyên Nhất Khí Thể",
            "Vũ Trụ Tinh Thần Thể",
            "Tiêu Dao Trí Tâm Thể",
            "Đại Đạo Vô Tình Thể"
        };

        if ("PHUONG_NGUYEN_REBIRTH".equalsIgnoreCase(request.getCustomScenario())) {
            aptitude = Aptitude.BINH_DANG;
            percentage = 44; // Cổ Nguyệt Phương Nguyên 44%
            starterStones = 30; // Phương Nguyên trùng sinh có vốn tích lũy ban đầu
        } else {
            // Random gacha rolling distribution: 1% Thập Tuyệt Thể (100%), 9% Giáp (80-99%), 25% Ất (60-79%), 45% Bính (40-59%), 20% Đinh (20-39%)
            int roll = random.nextInt(100);
            if (roll < 1) { // 1% Cơ duyên nghịch thiên
                aptitude = Aptitude.THAP_TUYET_THE;
                percentage = 100; // 100% Thập Tuyệt Thể!
                starterStones = 100;
                starterTalentPoints = 4;
                extremePhysiqueTitle = " [" + tenExtremePhysiques[random.nextInt(tenExtremePhysiques.length)] + "]";
            } else if (roll < 10) { // 9% Giáp Đẳng
                aptitude = Aptitude.GIAP_DANG;
                percentage = 80 + random.nextInt(20); // 80 - 99%
                starterStones = 60;
                starterTalentPoints = 3;
            } else if (roll < 35) { // 25% Ất Đẳng
                aptitude = Aptitude.AT_DANG;
                percentage = 60 + random.nextInt(20); // 60 - 79%
                starterStones = 40;
            } else if (roll < 80) { // 45% Bính Đẳng
                aptitude = Aptitude.BINH_DANG;
                percentage = 40 + random.nextInt(20); // 40 - 59%
                starterStones = 25;
            } else { // 20% Đinh Đẳng
                aptitude = Aptitude.DINH_DANG;
                percentage = 20 + random.nextInt(20); // 20 - 39%
                starterStones = 15;
            }
        }

        CultivationRank rank = CultivationRank.NHAT_CHUYEN;
        SubStage subStage = SubStage.SO_GIAI;
        int maxEssence = (int) Math.round(rank.getBaseEssencePool() * (percentage / 100.0));

        CharacterEntity character = CharacterEntity.builder()
                .name(name)
                .aptitude(aptitude)
                .aptitudePercentage(percentage)
                .rank(rank)
                .subStage(subStage)
                .breakThroughExp(0)
                .maxEssence(maxEssence)
                .currentEssence(maxEssence)
                .maxHp(100)
                .currentHp(100)
                .maxAp(4)
                .currentLifespanYears(100)
                .maxLifespanYears(100)
                .primevalStones(starterStones)
                .contributionPoints(0)
                .alignment(Alignment.CHINH_DAO)
                .daoMarksLuc(0)
                .daoMarksViem(0)
                .daoMarksHuyet(0)
                .permanentBonusAtk(aptitude == Aptitude.THAP_TUYET_THE ? 30 : 0)
                .permanentBonusDef(aptitude == Aptitude.THAP_TUYET_THE ? 20 : 0)
                .elementalResistPercent(aptitude == Aptitude.THAP_TUYET_THE ? 15 : 0)
                .talentPoints(starterTalentPoints)
                .unlockedTalents("")
                .springAutumnCicadaStress("PHUONG_NGUYEN_REBIRTH".equalsIgnoreCase(request.getCustomScenario()) ? 25 : (aptitude == Aptitude.THAP_TUYET_THE ? 15 : 0))
                .rebirthCount("PHUONG_NGUYEN_REBIRTH".equalsIgnoreCase(request.getCustomScenario()) ? 1 : 0)
                .build();

        character = characterRepository.save(character);

        // Starter Gus
        List<CharacterGu> starterGus = new ArrayList<>();
        GuDefinition nguyetQuang = guDefinitionRepository.findByCode("NGUYET_QUANG_CO")
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy Nguyệt Quang Cổ"));

        CharacterGu nguyetQuangChar = CharacterGu.builder()
                .characterId(character.getId())
                .guDefinition(nguyetQuang)
                .currentHungerHours(nguyetQuang.getMaxHungerHours())
                .isEquipped(true)
                .slotIndex(0)
                .isHibernating(false)
                .isStarving(false)
                .build();
        starterGus.add(characterGuRepository.save(nguyetQuangChar));

        if (aptitude == Aptitude.THAP_TUYET_THE) {
            final Long savedCharId = character.getId();
            guDefinitionRepository.findByCode("TU_TRUNG").ifPresent(tuTrung -> {
                CharacterGu tuTrungChar = CharacterGu.builder()
                        .characterId(savedCharId)
                        .guDefinition(tuTrung)
                        .currentHungerHours(tuTrung.getMaxHungerHours())
                        .isEquipped(true)
                        .slotIndex(4)
                        .isHibernating(false)
                        .isStarving(false)
                        .build();
                starterGus.add(characterGuRepository.save(tuTrungChar));
            });
        }

        if ("PHUONG_NGUYEN_REBIRTH".equalsIgnoreCase(request.getCustomScenario())) {
            GuDefinition xuanThuThien = guDefinitionRepository.findByCode("XUAN_THU_THIEN")
                    .orElseThrow(() -> new IllegalStateException("Không tìm thấy Xuân Thu Thiền"));
            CharacterGu xuanThuChar = CharacterGu.builder()
                    .characterId(character.getId())
                    .guDefinition(xuanThuThien)
                    .currentHungerHours(xuanThuThien.getMaxHungerHours())
                    .isEquipped(true)
                    .slotIndex(99)
                    .isHibernating(false)
                    .isStarving(false)
                    .build();
            starterGus.add(characterGuRepository.save(xuanThuChar));
        }

        // Initialize Game State
        GameStateEntity gameState = GameStateEntity.builder()
                .characterId(character.getId())
                .currentDay(1)
                .currentPeriod(DayPeriod.SANG)
                .wolfTideDaysRemaining(45)
                .xuanThuThienPressureDays(120)
                .location("Học Đường Gia Tộc")
                .clanSuspicion(10)
                .marketEvent("Gia tộc phát trợ cấp sơ giai: Mỗi đệ tử nhận 3 viên Nguyên Thạch.")
                .lastEventLog(String.format("Khai Khiếu Đại Điển hoàn tất! Đạt tư chất %s%s (%d%%). Không Khiếu mở ra biển Thanh Đồng Chân Nguyên rực rỡ.",
                        aptitude.getDisplayName(), extremePhysiqueTitle, percentage))
                .build();

        gameState = gameStateRepository.save(gameState);

        return GameStateDto.builder()
                .character(character)
                .gameState(gameState)
                .equippedGus(starterGus.stream().filter(CharacterGu::isEquipped).toList())
                .allGus(starterGus)
                .announcement(String.format("🌟 Chúc mừng %s đã khai khiếu thành công! Tư chất: %s%s (%d%%).",
                        character.getName(), aptitude.getDisplayName(), extremePhysiqueTitle, percentage))
                .build();
    }
}

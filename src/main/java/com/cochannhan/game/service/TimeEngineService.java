package com.cochannhan.game.service;

import com.cochannhan.game.dto.GameStateDto;
import com.cochannhan.game.model.*;
import com.cochannhan.game.repository.CharacterGuRepository;
import com.cochannhan.game.repository.CharacterRepository;
import com.cochannhan.game.repository.GameStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeEngineService {

    private final CharacterRepository characterRepository;
    private final CharacterGuRepository characterGuRepository;
    private final GameStateRepository gameStateRepository;
    private final Random random = new Random();

    @Transactional
    public GameStateDto advancePeriod(Long characterId) {
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân vật không tồn tại"));

        GameStateEntity state = gameStateRepository.findByCharacterId(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy trạng thái game"));

        DayPeriod oldPeriod = state.getCurrentPeriod();
        DayPeriod nextPeriod = oldPeriod.next();
        state.setCurrentPeriod(nextPeriod);

        boolean isNewDay = (nextPeriod == DayPeriod.SANG);
        if (isNewDay) {
            state.setCurrentDay(state.getCurrentDay() + 1);
            if (state.getWolfTideDaysRemaining() > 0) {
                state.setWolfTideDaysRemaining(state.getWolfTideDaysRemaining() - 1);
            }
            if (state.getXuanThuThienPressureDays() > 0) {
                state.setXuanThuThienPressureDays(state.getXuanThuThienPressureDays() - 1);
            }
        }

        // Hunger decay for all Gus (6 hours per period, or 4 hours if Keo Kiệt Cực Hạn talent is unlocked)
        int decayHours = character.hasTalent("KEO_KIET_CUC_HAN") ? 4 : 6;
        List<CharacterGu> gus = characterGuRepository.findByCharacterId(characterId);
        StringBuilder hungerReport = new StringBuilder();
        int starvingCount = 0;

        for (CharacterGu gu : gus) {
            // Spring Autumn Cicada doesn't starve
            if (!"XUAN_THU_THIEN".equals(gu.getGuDefinition().getCode())) {
                gu.decayHunger(decayHours);
                if (gu.isStarving()) {
                    starvingCount++;
                    hungerReport.append(String.format("[%s đang đói cồn cào! Giảm 50%% uy lực!] ", gu.getGuDefinition().getName()));
                }
            }
        }
        characterGuRepository.saveAll(gus);

        // Regenerate Essence & HP per period based on Aptitude
        int recoveryRate = character.getAptitude().getRecoveryRatePerTurn();
        int restoredEssence = Math.max(15, (int) Math.round(character.getMaxEssence() * (recoveryRate / 100.0)));
        character.setCurrentEssence(Math.min(character.getMaxEssence(), character.getCurrentEssence() + restoredEssence));
        character.setCurrentHp(Math.min(character.getMaxHp(), character.getCurrentHp() + 25));
        characterRepository.save(character);

        // Dynamic world & market events
        String eventLog;
        if (isNewDay) {
            String[] dailyEvents = {
                "Thương Đội từ xa vừa tiến vào chân núi Cổ Nguyệt! Nhiều Cổ trùng và đá quý lạ xuất hiện.",
                "Trưởng lão Học Đường tuần tra, bắt quả tang đệ tử đánh nhau tranh đoạt phụ cấp.",
                "Rừng Thanh Mao xáo động, trinh sát phát hiện dấu vết bầy Sói Điện đang di chuyển.",
                "Dược Đường mở đợt thu mua Nguyệt Lan Hoa với giá hời để chế thuốc trị thương."
            };
            state.setMarketEvent(dailyEvents[random.nextInt(dailyEvents.length)]);
            eventLog = String.format("Qua ngày thứ %d. %s %s Chân nguyên tự động hồi phục %d điểm.",
                    state.getCurrentDay(), state.getMarketEvent(), hungerReport, restoredEssence);
        } else {
            eventLog = String.format("Chuyển sang %s. Không Khiếu hồi phục %d Chân nguyên. %s",
                    nextPeriod.getTitle(), restoredEssence, hungerReport);
        }

        state.setLastEventLog(eventLog);
        gameStateRepository.save(state);

        return GameStateDto.builder()
                .character(character)
                .gameState(state)
                .equippedGus(gus.stream().filter(CharacterGu::isEquipped).toList())
                .allGus(gus)
                .announcement(eventLog)
                .build();
    }
}

package com.cochannhan.game.service;

import com.cochannhan.game.dto.GameStateDto;
import com.cochannhan.game.model.CharacterEntity;
import com.cochannhan.game.model.CharacterGu;
import com.cochannhan.game.model.GameStateEntity;
import com.cochannhan.game.repository.CharacterGuRepository;
import com.cochannhan.game.repository.CharacterRepository;
import com.cochannhan.game.repository.GameStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClanQuestService {

    private final CharacterRepository characterRepository;
    private final CharacterGuRepository characterGuRepository;
    private final GameStateRepository gameStateRepository;
    private final TimeEngineService timeEngineService;

    @Transactional
    public GameStateDto executeMission(Long characterId, String missionType) {
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân vật không tồn tại"));

        GameStateEntity state = gameStateRepository.findByCharacterId(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy trạng thái game"));

        String resultMsg;

        switch (missionType.toUpperCase()) {
            case "TUAN_SON": {
                // Tuần tra chân núi Thanh Mao
                int stoneReward = 6;
                int contribReward = 15;
                character.setPrimevalStones(character.getPrimevalStones() + stoneReward);
                character.setContributionPoints(character.getContributionPoints() + contribReward);
                character = characterRepository.saveAndFlush(character);

                // Qua 1 canh giờ
                timeEngineService.advancePeriod(characterId);
                state = gameStateRepository.findByCharacterId(characterId).orElse(state);
                character = characterRepository.findById(characterId).orElse(character);

                resultMsg = String.format("🎯 Hoàn thành nhiệm vụ Tuần Sơn! Được Trưởng Lão Học Đường thưởng +%d Nguyên Thạch và +%d Điểm Cống Hiến. (Thời gian trôi 1 Canh Giờ)",
                        stoneReward, contribReward);
                break;
            }
            case "THU_THAP": {
                // Thu thập Nguyệt Lan Hoa vách núi
                int stoneReward = 6;
                int contribReward = 12;
                character.setPrimevalStones(character.getPrimevalStones() + stoneReward);
                character.setContributionPoints(character.getContributionPoints() + contribReward);
                character = characterRepository.saveAndFlush(character);

                // Cho Nguyệt Quang Cổ ăn hồi phục 100% độ no
                List<CharacterGu> gus = characterGuRepository.findByCharacterId(characterId);
                boolean fed = false;
                for (CharacterGu gu : gus) {
                    if ("NGUYET_QUANG_CO".equals(gu.getGuDefinition().getCode())) {
                        gu.feed(gu.getGuDefinition().getMaxHungerHours());
                        characterGuRepository.save(gu);
                        fed = true;
                    }
                }

                timeEngineService.advancePeriod(characterId);
                state = gameStateRepository.findByCharacterId(characterId).orElse(state);
                character = characterRepository.findById(characterId).orElse(character);

                resultMsg = String.format("🌿 Thu hái cánh hoa Nguyệt Lan thành công! Nhận +%d Nguyên Thạch, +%d Cống Hiến%s. (Thời gian trôi 1 Canh Giờ)",
                        stoneReward, contribReward, fed ? " và Nguyệt Quang Cổ đã được cho ăn no!" : "");
                break;
            }
            case "SAN_SOI": {
                // Tiêu diệt Sói Hoang thám thính
                if (character.getCurrentHp() <= 20) {
                    throw new IllegalStateException("Khí huyết quá suy nhược (dưới 20 HP), không thể đi săn Sói! Hãy tĩnh dưỡng trước.");
                }
                int stoneReward = 20;
                int contribReward = 35;
                character.setCurrentHp(Math.max(1, character.getCurrentHp() - 15));
                character.setPrimevalStones(character.getPrimevalStones() + stoneReward);
                character.setContributionPoints(character.getContributionPoints() + contribReward);
                character = characterRepository.saveAndFlush(character);

                timeEngineService.advancePeriod(characterId);
                state = gameStateRepository.findByCharacterId(characterId).orElse(state);
                character = characterRepository.findById(characterId).orElse(character);

                resultMsg = String.format("🐺 Tiêu diệt bầy Sói Dò Đường! Tiêu hao 15 HP, gia tộc khen thưởng +%d Nguyên Thạch và +%d Điểm Cống Hiến!",
                        stoneReward, contribReward);
                break;
            }
            case "TRAN_LOT": {
                // Chặn cổng học đường trấn lột đồng môn
                int stoneReward = 8;
                character.setPrimevalStones(character.getPrimevalStones() + stoneReward);
                character = characterRepository.saveAndFlush(character);

                timeEngineService.advancePeriod(characterId);
                state = gameStateRepository.findByCharacterId(characterId).orElse(state);
                character = characterRepository.findById(characterId).orElse(character);

                resultMsg = String.format("👊 Phương Nguyên dựa vào kinh nghiệm Ma Tôn 500 năm, chặn cổng học đường trấn lột thành công +%d Nguyên Thạch từ đám đệ tử yếu ớt!",
                        stoneReward);
                break;
            }
            case "TINH_TOA": {
                // Tĩnh tọa bế quan hồi phục toàn bộ
                character.setCurrentEssence(character.getMaxEssence());
                character.setCurrentHp(Math.min(character.getMaxHp(), character.getCurrentHp() + 50));
                characterRepository.save(character);

                timeEngineService.advancePeriod(characterId);
                state = gameStateRepository.findByCharacterId(characterId).orElse(state);
                character = characterRepository.findById(characterId).orElse(character);

                resultMsg = "🛏️ Tĩnh tọa bế quan 1 Canh Giờ. Không Khiếu hồi phục 100% Chân Nguyên và +50 Khí Huyết (HP)!";
                break;
            }
            case "ABSORB_STONE_INSTANT": {
                // Đốt 1 Nguyên Thạch ngoài đời hồi tức thì (không qua canh giờ)
                if (character.getPrimevalStones() < 1) {
                    throw new IllegalStateException("Trong túi không còn viên Nguyên Thạch nào để hấp thụ!");
                }
                character.setPrimevalStones(character.getPrimevalStones() - 1);
                character.setCurrentEssence(Math.min(character.getMaxEssence(), character.getCurrentEssence() + 35));
                character.setCurrentHp(Math.min(character.getMaxHp(), character.getCurrentHp() + 30));
                characterRepository.save(character);

                resultMsg = "💎 Đã bóp vỡ 1 viên Nguyên Thạch! Hấp thụ Chân Nguyên tinh thuần (+35 Chân Nguyên, +30 HP).";
                break;
            }
            case "HO_TONG": {
                // Hộ tống đoàn lạc đà của Thương Đội
                if (character.getCurrentHp() <= 20) {
                    throw new IllegalStateException("Khí huyết quá suy nhược (dưới 20 HP), không thể đi hộ tống! Hãy tĩnh dưỡng trước.");
                }
                int stoneReward = 18;
                int contribReward = 30;
                character.setCurrentHp(Math.max(1, character.getCurrentHp() - 15));
                character.setPrimevalStones(character.getPrimevalStones() + stoneReward);
                character.setContributionPoints(character.getContributionPoints() + contribReward);
                character = characterRepository.saveAndFlush(character);

                timeEngineService.advancePeriod(characterId);
                state = gameStateRepository.findByCharacterId(characterId).orElse(state);
                character = characterRepository.findById(characterId).orElse(character);

                resultMsg = String.format("🐪 Hộ tống lạc đà Thương Đội qua hẻm núi an toàn! Tiêu hao 15 HP, thương nhân tạ ơn +%d Nguyên Thạch và +%d Điểm Cống Hiến!",
                        stoneReward, contribReward);
                break;
            }
            case "DAO_KHOANG": {
                // Khai thác Thạch Anh dưới sông ngầm
                int stoneReward = 14;
                int contribReward = 20;
                character.setPrimevalStones(character.getPrimevalStones() + stoneReward);
                character.setContributionPoints(character.getContributionPoints() + contribReward);
                // Dòng nước ngầm hồi phục chút chân nguyên
                character.setCurrentEssence(Math.min(character.getMaxEssence(), character.getCurrentEssence() + 20));
                character = characterRepository.saveAndFlush(character);

                timeEngineService.advancePeriod(characterId);
                state = gameStateRepository.findByCharacterId(characterId).orElse(state);
                character = characterRepository.findById(characterId).orElse(character);

                resultMsg = String.format("⛏️ Đập vỡ các khối tinh thể Thạch Anh dưới đáy sông ngầm! Thu được +%d Nguyên Thạch, +%d Cống Hiến và hồi phục 20 Chân Nguyên!",
                        stoneReward, contribReward);
                break;
            }
            case "HOA_CUU_SU": {
                // Dò tìm bí cảnh Hoa Cửu Sư
                if (character.getCurrentHp() <= 25) {
                    throw new IllegalStateException("Khí huyết không đủ (cần trên 25 HP) để thám hiểm hang động ma đạo!");
                }
                int stoneReward = 28;
                int contribReward = 15;
                character.setCurrentHp(Math.max(1, character.getCurrentHp() - 20));
                character.setPrimevalStones(character.getPrimevalStones() + stoneReward);
                character.setContributionPoints(character.getContributionPoints() + contribReward);
                character = characterRepository.saveAndFlush(character);

                timeEngineService.advancePeriod(characterId);
                state = gameStateRepository.findByCharacterId(characterId).orElse(state);
                character = characterRepository.findById(characterId).orElse(character);

                resultMsg = String.format("🏺 Vượt qua bẫy đá tìm thấy dấu tích bí mật của Hoa Cửu Sư! Tiêu hao 20 HP, đoạt lấy +%d Nguyên Thạch và +%d Điểm Cống Hiến!",
                        stoneReward, contribReward);
                break;
            }
            case "TRO_LUYEN": {
                // Trợ lửa Bách Luyện Các
                int stoneReward = 15;
                int contribReward = 28;
                character.setPrimevalStones(character.getPrimevalStones() + stoneReward);
                character.setContributionPoints(character.getContributionPoints() + contribReward);
                character = characterRepository.saveAndFlush(character);

                timeEngineService.advancePeriod(characterId);
                state = gameStateRepository.findByCharacterId(characterId).orElse(state);
                character = characterRepository.findById(characterId).orElse(character);

                resultMsg = String.format("🔥 Vận chân khí trợ lửa lò luyện Cổ cho Gia Lão thành công! Nhận bổng lộc +%d Nguyên Thạch và +%d Điểm Cống Hiến!",
                        stoneReward, contribReward);
                break;
            }
            case "GIAN_TE": {
                // Truy bắt gian tế
                if (character.getCurrentHp() <= 35) {
                    throw new IllegalStateException("Khí huyết quá yếu (cần trên 35 HP) để truy bắt gian tế nguy hiểm!");
                }
                int stoneReward = 35;
                int contribReward = 60;
                character.setCurrentHp(Math.max(1, character.getCurrentHp() - 30));
                character.setPrimevalStones(character.getPrimevalStones() + stoneReward);
                character.setContributionPoints(character.getContributionPoints() + contribReward);
                character = characterRepository.saveAndFlush(character);

                timeEngineService.advancePeriod(characterId);
                state = gameStateRepository.findByCharacterId(characterId).orElse(state);
                character = characterRepository.findById(characterId).orElse(character);

                resultMsg = String.format("🥷 Bắt sống thám tử Bạch Gia mang theo bản đồ phòng thủ! Tiêu hao 30 HP, Tộc Trưởng trọng thưởng +%d Nguyên Thạch và +%d Điểm Cống Hiến!",
                        stoneReward, contribReward);
                break;
            }
            case "CHI_DIEM": {
                // Chỉ điểm quyền cước học đường
                int stoneReward = 10;
                int contribReward = 22;
                character.setPrimevalStones(character.getPrimevalStones() + stoneReward);
                character.setContributionPoints(character.getContributionPoints() + contribReward);
                character = characterRepository.saveAndFlush(character);

                timeEngineService.advancePeriod(characterId);
                state = gameStateRepository.findByCharacterId(characterId).orElse(state);
                character = characterRepository.findById(characterId).orElse(character);

                resultMsg = String.format("🥋 Chỉ điểm quyền cước cho các thiếu niên học đường! Gia Lão khen ngợi, ban thưởng +%d Nguyên Thạch và +%d Cống Hiến!",
                        stoneReward, contribReward);
                break;
            }
            default:
                throw new IllegalArgumentException("Loại nhiệm vụ không hợp lệ: " + missionType);
        }

        state.setLastEventLog(resultMsg);
        gameStateRepository.save(state);

        List<CharacterGu> gus = characterGuRepository.findByCharacterId(characterId);

        return GameStateDto.builder()
                .character(character)
                .gameState(state)
                .equippedGus(gus.stream().filter(CharacterGu::isEquipped).toList())
                .allGus(gus)
                .announcement(resultMsg)
                .build();
    }

    @Transactional
    public GameStateDto claimDailyStipend(Long characterId) {
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân vật không tồn tại"));

        GameStateEntity state = gameStateRepository.findByCharacterId(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy trạng thái game"));

        if (state.getLastStipendClaimedDay() >= state.getCurrentDay()) {
            throw new IllegalStateException("Ngươi đã nhận bổng lộc ngày thứ " + state.getCurrentDay() + " rồi! Hãy qua canh giờ sang ngày mới để nhận tiếp.");
        }

        int rank = character.getRank() != null ? character.getRank().getRankNumber() : 1;
        int stipendStones;
        int stipendPoints;
        String rankTitle;

        switch (rank) {
            case 2:
                stipendStones = 12;
                stipendPoints = 15;
                rankTitle = "Thập Trưởng (Nhị Chuyển)";
                break;
            case 3:
                stipendStones = 25;
                stipendPoints = 30;
                rankTitle = "Gia Lão (Tam Chuyển)";
                break;
            case 4:
                stipendStones = 50;
                stipendPoints = 60;
                rankTitle = "Phó Tộc Trưởng (Tứ Chuyển)";
                break;
            case 5:
                stipendStones = 100;
                stipendPoints = 120;
                rankTitle = "Tộc Trưởng (Ngũ Chuyển)";
                break;
            default:
                stipendStones = 5;
                stipendPoints = 5;
                rankTitle = "Học Đường Đệ Tử (Nhất Chuyển)";
                break;
        }

        character.setPrimevalStones(character.getPrimevalStones() + stipendStones);
        character.setContributionPoints(character.getContributionPoints() + stipendPoints);
        character = characterRepository.saveAndFlush(character);

        state.setLastStipendClaimedDay(state.getCurrentDay());
        String msg = String.format("🎁 Nhận bổng lộc gia tộc ngày %d [%s] thành công: +%d Nguyên Thạch, +%d Điểm Cống Hiến!",
                state.getCurrentDay(), rankTitle, stipendStones, stipendPoints);
        state.setLastEventLog(msg);
        gameStateRepository.saveAndFlush(state);

        List<CharacterGu> gus = characterGuRepository.findByCharacterId(characterId);

        return GameStateDto.builder()
                .character(character)
                .gameState(state)
                .equippedGus(gus.stream().filter(CharacterGu::isEquipped).toList())
                .allGus(gus)
                .announcement(msg)
                .build();
    }

    @Transactional
    public GameStateDto claimAchievement(Long characterId, int rewardStones, int rewardPoints, String achievementId) {
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân vật không tồn tại"));

        GameStateEntity state = gameStateRepository.findByCharacterId(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy trạng thái game"));

        if (rewardStones > 0) {
            character.setPrimevalStones(character.getPrimevalStones() + rewardStones);
        }
        if (rewardPoints > 0) {
            character.setContributionPoints(character.getContributionPoints() + rewardPoints);
        }
        character = characterRepository.saveAndFlush(character);

        String msg = String.format("🏆 Đã nhận thưởng thành tựu [%s]: +%d Nguyên Thạch, +%d Cống Hiến!",
                achievementId, rewardStones, rewardPoints);
        state.setLastEventLog(msg);
        gameStateRepository.saveAndFlush(state);

        List<CharacterGu> gus = characterGuRepository.findByCharacterId(characterId);

        return GameStateDto.builder()
                .character(character)
                .gameState(state)
                .equippedGus(gus.stream().filter(CharacterGu::isEquipped).toList())
                .allGus(gus)
                .announcement(msg)
                .build();
    }
}

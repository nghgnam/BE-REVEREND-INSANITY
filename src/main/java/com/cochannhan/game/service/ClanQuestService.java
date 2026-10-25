package com.cochannhan.game.service;

import com.cochannhan.game.dto.GameStateDto;
import com.cochannhan.game.model.CharacterEntity;
import com.cochannhan.game.model.CharacterGu;
import com.cochannhan.game.model.GameStateEntity;
import com.cochannhan.game.model.GuDefinition;
import com.cochannhan.game.repository.CharacterGuRepository;
import com.cochannhan.game.repository.CharacterRepository;
import com.cochannhan.game.repository.GameStateRepository;
import com.cochannhan.game.repository.GuDefinitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClanQuestService {

    private final CharacterRepository characterRepository;
    private final CharacterGuRepository characterGuRepository;
    private final GameStateRepository gameStateRepository;
    private final GuDefinitionRepository guDefinitionRepository;
    private final TimeEngineService timeEngineService;
    private final Random random = new Random();

    @Transactional
    public GameStateDto executeMission(Long characterId, String missionType) {
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân vật không tồn tại"));

        GameStateEntity state = gameStateRepository.findByCharacterId(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy trạng thái game"));

        String type = missionType != null ? missionType.toUpperCase().trim() : "";

        // Validate HP before advancing time
        if ("SAN_SOI".equals(type) && character.getCurrentHp() < 20) {
            throw new IllegalStateException("Khí huyết quá suy nhược (dưới 20 HP), không thể đi săn Sói! Hãy tĩnh dưỡng trước.");
        }
        if ("HO_TONG".equals(type) && character.getCurrentHp() < 20) {
            throw new IllegalStateException("Khí huyết quá suy nhược (dưới 20 HP), không thể đi hộ tống! Hãy tĩnh dưỡng trước.");
        }
        if ("HOA_CUU_SU".equals(type) && character.getCurrentHp() < 25) {
            throw new IllegalStateException("Khí huyết không đủ (cần ít nhất 25 HP) để thám hiểm hang động ma đạo!");
        }
        if ("GIAN_TE".equals(type) && character.getCurrentHp() < 35) {
            throw new IllegalStateException("Khí huyết quá yếu (cần ít nhất 35 HP) để truy bắt gian tế nguy hiểm!");
        }
        if ("ABSORB_STONE_INSTANT".equals(type) && character.getPrimevalStones() < 1) {
            throw new IllegalStateException("Trong túi không còn viên Nguyên Thạch nào để hấp thụ!");
        }

        // Advance 1 period first for time-consuming missions so its save never overwrites mission rewards
        if (!"ABSORB_STONE_INSTANT".equals(type)) {
            timeEngineService.advancePeriod(characterId);
            character = characterRepository.findById(characterId).orElseThrow();
            state = gameStateRepository.findByCharacterId(characterId).orElseThrow();
        }

        int oldStones = character.getPrimevalStones();
        int oldContrib = character.getContributionPoints();
        String resultMsg;

        switch (type) {
            case "TUAN_SON": {
                int stoneReward = 120;
                int contribReward = 300;
                character.setPrimevalStones(oldStones + stoneReward);
                character.setContributionPoints(oldContrib + contribReward);
                String bonusGu = (random.nextInt(100) < 35) ? awardGuToCharacter(characterId, "HAC_THI_CO") : null;
                resultMsg = String.format("🎯 Hoàn thành Tuần Sơn! Nguyên Thạch: %d ➔ %d (+%d💎), Cống Hiến: %d ➔ %d (+%d📜)%s.",
                        oldStones, character.getPrimevalStones(), stoneReward,
                        oldContrib, character.getContributionPoints(), contribReward,
                        bonusGu != null ? ", Thu phục thêm [" + bonusGu + "]" : "");
                break;
            }
            case "THU_THAP":
            case "HAI_THUOC": {
                int stoneReward = 120;
                int contribReward = 240;
                character.setPrimevalStones(oldStones + stoneReward);
                character.setContributionPoints(oldContrib + contribReward);

                List<CharacterGu> gus = characterGuRepository.findByCharacterId(characterId);
                for (CharacterGu gu : gus) {
                    gu.feed(gu.getGuDefinition().getMaxHungerHours());
                    characterGuRepository.save(gu);
                }
                String bonusGu = (random.nextInt(100) < 40) ? awardGuToCharacter(characterId, "SINH_CO_DIEP") : null;

                resultMsg = String.format("🌿 Thu hái Nguyệt Lan & Dược Thảo thành công! Nguyên Thạch: %d ➔ %d (+%d💎), Cống Hiến: %d ➔ %d (+%d📜), toàn bộ Cổ trùng đã no 100%%%s!",
                        oldStones, character.getPrimevalStones(), stoneReward,
                        oldContrib, character.getContributionPoints(), contribReward,
                        bonusGu != null ? " & Nhận [" + bonusGu + "]" : "");
                break;
            }
            case "SAN_SOI": {
                int stoneReward = 400;
                int contribReward = 700;
                character.setCurrentHp(Math.max(1, character.getCurrentHp() - 15));
                character.setPrimevalStones(oldStones + stoneReward);
                character.setContributionPoints(oldContrib + contribReward);
                String bonusGu = awardGuToCharacter(characterId, random.nextBoolean() ? "LANG_BIEN_CO" : "THIEM_DIEN_CO");

                resultMsg = String.format("🐺 Tiêu diệt bầy Sói Dò Đường! Nguyên Thạch: %d ➔ %d (+%d💎), Cống Hiến: %d ➔ %d (+%d📜), Chiến lợi phẩm: [%s]!",
                        oldStones, character.getPrimevalStones(), stoneReward,
                        oldContrib, character.getContributionPoints(), contribReward,
                        bonusGu);
                break;
            }
            case "TRAN_LOT": {
                int stoneReward = 160;
                character.setPrimevalStones(oldStones + stoneReward);
                resultMsg = String.format("👊 Chặn cổng Học Đường trấn lột thành công! Nguyên Thạch: %d ➔ %d (+%d💎)!",
                        oldStones, character.getPrimevalStones(), stoneReward);
                break;
            }
            case "TINH_TOA": {
                character.setCurrentEssence(character.getMaxEssence());
                character.setCurrentHp(character.getMaxHp());
                resultMsg = "🛏️ Tĩnh tọa bế quan 1 Canh Giờ. Không Khiếu hồi phục 100% Chân Nguyên và 100% Khí Huyết (HP)!";
                break;
            }
            case "ABSORB_STONE_INSTANT": {
                character.setPrimevalStones(oldStones - 1);
                character.setCurrentEssence(Math.min(character.getMaxEssence(), character.getCurrentEssence() + 35));
                character.setCurrentHp(Math.min(character.getMaxHp(), character.getCurrentHp() + 30));
                resultMsg = String.format("💎 Đã hấp thụ 1 viên Nguyên Thạch (Còn %d💎)! Hồi phục +35 Chân Nguyên, +30 HP.",
                        character.getPrimevalStones());
                break;
            }
            case "HO_TONG": {
                int stoneReward = 360;
                int contribReward = 600;
                character.setCurrentHp(Math.max(1, character.getCurrentHp() - 15));
                character.setPrimevalStones(oldStones + stoneReward);
                character.setContributionPoints(oldContrib + contribReward);
                String bonusGu = (random.nextInt(100) < 50) ? awardGuToCharacter(characterId, "THIET_BI_CO") : null;

                resultMsg = String.format("🐪 Hộ tống Thương Đội an toàn! Nguyên Thạch: %d ➔ %d (+%d💎), Cống Hiến: %d ➔ %d (+%d📜)%s!",
                        oldStones, character.getPrimevalStones(), stoneReward,
                        oldContrib, character.getContributionPoints(), contribReward,
                        bonusGu != null ? ", Thương nhân tặng [" + bonusGu + "]" : "");
                break;
            }
            case "DAO_KHOANG": {
                int stoneReward = 280;
                int contribReward = 400;
                character.setPrimevalStones(oldStones + stoneReward);
                character.setContributionPoints(oldContrib + contribReward);
                character.setCurrentEssence(character.getMaxEssence());
                String bonusGu = (random.nextInt(100) < 45) ? awardGuToCharacter(characterId, "BACH_NGOC_CO") : null;

                resultMsg = String.format("⛏️ Khai thác Thạch Anh sông ngầm! Nguyên Thạch: %d ➔ %d (+%d💎), Cống Hiến: %d ➔ %d (+%d📜), Hồi đầy Chân Nguyên%s!",
                        oldStones, character.getPrimevalStones(), stoneReward,
                        oldContrib, character.getContributionPoints(), contribReward,
                        bonusGu != null ? " & Đào được [" + bonusGu + "]" : "");
                break;
            }
            case "HOA_CUU_SU": {
                int stoneReward = 560;
                int contribReward = 300;
                character.setCurrentHp(Math.max(1, character.getCurrentHp() - 20));
                character.setPrimevalStones(oldStones + stoneReward);
                character.setContributionPoints(oldContrib + contribReward);
                String bonusGu = awardGuToCharacter(characterId, random.nextBoolean() ? "BACH_THI_CO" : "THANH_DONG_XA_LOI_CO");

                resultMsg = String.format("🏺 Thám hiểm mật động Hoa Cửu Sư! Nguyên Thạch: %d ➔ %d (+%d💎), Cống Hiến: %d ➔ %d (+%d📜), Đoạt được [%s]!",
                        oldStones, character.getPrimevalStones(), stoneReward,
                        oldContrib, character.getContributionPoints(), contribReward,
                        bonusGu);
                break;
            }
            case "TRO_LUYEN": {
                int stoneReward = 300;
                int contribReward = 560;
                character.setPrimevalStones(oldStones + stoneReward);
                character.setContributionPoints(oldContrib + contribReward);
                String bonusGu = (random.nextInt(100) < 45) ? awardGuToCharacter(characterId, "HOA_CAU_CO") : null;

                resultMsg = String.format("🔥 Trợ lửa Bách Luyện Các thành công! Nguyên Thạch: %d ➔ %d (+%d💎), Cống Hiến: %d ➔ %d (+%d📜)%s!",
                        oldStones, character.getPrimevalStones(), stoneReward,
                        oldContrib, character.getContributionPoints(), contribReward,
                        bonusGu != null ? " & Nhận [" + bonusGu + "]" : "");
                break;
            }
            case "GIAN_TE": {
                int stoneReward = 700;
                int contribReward = 1200;
                character.setCurrentHp(Math.max(1, character.getCurrentHp() - 25));
                character.setPrimevalStones(oldStones + stoneReward);
                character.setContributionPoints(oldContrib + contribReward);
                String bonusGu = awardGuToCharacter(characterId, random.nextBoolean() ? "XICH_THIET_XA_LOI_CO" : "THIEN_BONG_CO");

                resultMsg = String.format("🥷 Bắt sống gian tế ngoại tộc! Nguyên Thạch: %d ➔ %d (+%d💎), Cống Hiến: %d ➔ %d (+%d📜), Chiến lợi phẩm: [%s]!",
                        oldStones, character.getPrimevalStones(), stoneReward,
                        oldContrib, character.getContributionPoints(), contribReward,
                        bonusGu);
                break;
            }
            case "CHI_DIEM": {
                int stoneReward = 200;
                int contribReward = 440;
                character.setPrimevalStones(oldStones + stoneReward);
                character.setContributionPoints(oldContrib + contribReward);

                resultMsg = String.format("🥋 Chỉ điểm quyền cước Học Đường! Nguyên Thạch: %d ➔ %d (+%d💎), Cống Hiến: %d ➔ %d (+%d📜)!",
                        oldStones, character.getPrimevalStones(), stoneReward,
                        oldContrib, character.getContributionPoints(), contribReward);
                break;
            }
            case "SAN_TUU_TRUNG": {
                // SỰ KIỆN KÉO DÀI: Săn Tửu Trùng & Cổ Trùng Tửu Đạo Chuyển Cao (x20 phần thưởng!)
                int stoneReward = 800 + random.nextInt(401); // 800 - 1200 Nguyên Thạch
                int contribReward = 500;
                character.setPrimevalStones(oldStones + stoneReward);
                character.setContributionPoints(oldContrib + contribReward);

                String[] wineGuPool = {
                    "TU_TRUNG", "TU_VI_TUU_TRUNG", "THAT_HUONG_TUU_TRUNG",
                    "CUU_CHUYEN_TIEN_TUU_TRUNG", "TUY_TIEN_NGUYET_CO",
                    "CUONG_TUU_BA_VUONG_CO", "BACH_QUA_TIEN_TUU_CO",
                    "XICH_THIET_XA_LOI_CO", "BACH_NGAN_XA_LOI_CO"
                };
                String gu1 = awardGuToCharacter(characterId, wineGuPool[random.nextInt(wineGuPool.length)]);
                String gu2 = awardGuToCharacter(characterId, wineGuPool[random.nextInt(wineGuPool.length)]);

                resultMsg = String.format("🍶 [SỰ KIỆN SĂN TỬU TRÙNG] Nguyên Thạch: %d ➔ %d (+%d💎), Cống Hiến: %d ➔ %d (+%d📜) & Bắt được 2 Linh Trùng: [%s], [%s]!",
                        oldStones, character.getPrimevalStones(), stoneReward,
                        oldContrib, character.getContributionPoints(), contribReward,
                        gu1, gu2);
                break;
            }
            case "TRUYEN_THUA_HOA_TUU": {
                // SỰ KIỆN KÉO DÀI: Thám Hiểm Truyền Thừa Hoa Tửu Hành Giả (x20 phần thưởng: 2,600 - 4,000 Đá!)
                int stoneReward = 2600 + random.nextInt(1401); // 2,600 - 4,000 Nguyên Thạch!
                int contribReward = 900;
                character.setCurrentHp(Math.max(1, character.getCurrentHp() - 15));
                character.setPrimevalStones(oldStones + stoneReward);
                character.setContributionPoints(oldContrib + contribReward);

                String[] hoaTuuCorePool = {
                    "TU_TRUNG", "TU_VI_TUU_TRUNG", "THAT_HUONG_TUU_TRUNG",
                    "CUU_CHUYEN_TIEN_TUU_TRUNG", "BACH_QUA_TIEN_TUU_CO",
                    "XICH_THIET_XA_LOI_CO", "BACH_NGAN_XA_LOI_CO",
                    "HOANG_KIM_XA_LOI_CO", "TU_TINH_XA_LOI_CO", "THO_CO"
                };
                String[] hoaTuuTreasurePool = {
                    "BACH_NHAN_LUC_CO", "THIEN_NHAN_LUC_CO", "VAN_NHAN_LUC_CO",
                    "TUONG_LUC_CO", "LONG_LAN_CO", "KIM_CUONG_BAT_HOAI_CO",
                    "TU_TINH_CO", "TINH_HA_KIEM_TRAN_CO", "VAN_KIEM_QUY_TONG_CO",
                    "DAI_MONG_THIEN_THU_CO", "TU_VI_DE_TINH_CO",
                    "TU_LA_CHIEN_THE_CO", "PHUONG_HOANG_NIET_BAN_CO",
                    "LONG_HUYET_DOAN_THE_CO", "THIEN_NGUYEN_BAO_LIEN", "KIM_CHUNG_TRAO_CO"
                };

                String gu1 = awardGuToCharacter(characterId, hoaTuuCorePool[random.nextInt(hoaTuuCorePool.length)]);
                String gu2 = awardGuToCharacter(characterId, hoaTuuTreasurePool[random.nextInt(hoaTuuTreasurePool.length)]);
                String gu3 = awardGuToCharacter(characterId, hoaTuuCorePool[random.nextInt(hoaTuuCorePool.length)]);

                resultMsg = String.format("🍷 [CHIẾN DỊCH TRUYỀN THỪA HOA TỬU HÀNH GIẢ] Đại thu hoạch x20! Nguyên Thạch: %d ➔ %d (+%d💎), Cống Hiến: %d ➔ %d (+%d📜), Đoạt 3 Cổ Trùng Quý: [%s], [%s], [%s]!",
                        oldStones, character.getPrimevalStones(), stoneReward,
                        oldContrib, character.getContributionPoints(), contribReward,
                        gu1, gu2, gu3);
                break;
            }
            case "BI_CANH_THIEN_TIEN": {
                // SỰ KIỆN KÉO DÀI: Thám Hiểm Động Thiên Cổ Tiên (x20 phần thưởng: 2,000 - 3,000 Đá!)
                int stoneReward = 2000 + random.nextInt(1001);
                int contribReward = 800;
                character.setPrimevalStones(oldStones + stoneReward);
                character.setContributionPoints(oldContrib + contribReward);

                String[] highRankPool = {
                    "VAN_KIEM_QUY_TONG_CO", "TRAM_THIEN_BAT_KIEM_CO", "DAI_MONG_THIEN_THU_CO",
                    "TU_VI_DE_TINH_CO", "THAI_AM_THAN_NGUYET_CO", "TU_LA_CHIEN_THE_CO",
                    "TU_TIEU_THAN_LOI_CO", "THAI_DUONG_KIM_O_CO", "THAI_CO_CU_NHAN_CO",
                    "THANH_LONG_THIEN_UY_BIEN_CO", "NGHICH_THIEN_CAI_MENH_CO",
                    "VAN_PHAP_BAT_XAM_CO", "THOI_KHONG_DAO_LUU_CO", "DOC_CO_KIEM_TIEN_CO",
                    "MONG_DIEP_TIEN_CO", "BAN_CO_KHAI_THIEN_CO", "HONG_MONG_TU_KHI_CO"
                };
                String gu1 = awardGuToCharacter(characterId, highRankPool[random.nextInt(highRankPool.length)]);
                String gu2 = awardGuToCharacter(characterId, highRankPool[random.nextInt(highRankPool.length)]);

                resultMsg = String.format("🌌 [BÍ CẢNH ĐỘNG THIÊN CỔ TIÊN] Nguyên Thạch: %d ➔ %d (+%d💎), Cống Hiến: %d ➔ %d (+%d📜) & Thu phục 2 Tiên Duyên: [%s], [%s]!",
                        oldStones, character.getPrimevalStones(), stoneReward,
                        oldContrib, character.getContributionPoints(), contribReward,
                        gu1, gu2);
                break;
            }
            default:
                throw new IllegalArgumentException("Loại nhiệm vụ không hợp lệ: " + missionType);
        }

        character = characterRepository.saveAndFlush(character);
        characterGuRepository.flush();
        state.setLastEventLog(resultMsg);
        state = gameStateRepository.saveAndFlush(state);

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

        int oldStones = character.getPrimevalStones();
        int oldPoints = character.getContributionPoints();

        if (rewardStones > 0) {
            character.setPrimevalStones(oldStones + rewardStones);
        }
        if (rewardPoints > 0) {
            character.setContributionPoints(oldPoints + rewardPoints);
        }

        String titlePart = achievementId != null ? achievementId : "Phần Thưởng";
        StringBuilder awardedGus = new StringBuilder();
        StringBuilder extraBuffs = new StringBuilder();

        if (achievementId != null && achievementId.contains("|")) {
            String[] segments = achievementId.split("\\|");
            titlePart = segments[0];
            for (int i = 1; i < segments.length; i++) {
                String seg = segments[i];
                if (seg.startsWith("GU:")) {
                    String guPart = seg.substring(3);
                    for (String guCode : guPart.split(",")) {
                        if (!guCode.isBlank()) {
                            String guName = awardGuToCharacter(characterId, guCode.trim());
                            if (awardedGus.length() > 0) awardedGus.append(", ");
                            awardedGus.append("[").append(guName).append("]");
                        }
                    }
                    characterGuRepository.flush();
                } else if (seg.startsWith("SUSP:")) {
                    int susp = Integer.parseInt(seg.substring(5).trim());
                    state.setClanSuspicion(Math.min(100, state.getClanSuspicion() + susp));
                    extraBuffs.append(" (⚠️ +").append(susp).append(" Nghi Ngờ)");
                } else if (seg.startsWith("DAOLUC:")) {
                    int dao = Integer.parseInt(seg.substring(7).trim());
                    character.setDaoMarksLuc(character.getDaoMarksLuc() + dao);
                    character.setPermanentBonusAtk(character.getPermanentBonusAtk() + 35);
                    extraBuffs.append(" (💪 +").append(dao).append(" Đạo Ngân Lực Đạo & +35 ATK Vĩnh Viễn)");
                } else if (seg.startsWith("WISDOM:")) {
                    int wis = Integer.parseInt(seg.substring(7).trim());
                    character.setElementalResistPercent(character.getElementalResistPercent() + wis);
                    character.setPermanentBonusDef(character.getPermanentBonusDef() + 25);
                    extraBuffs.append(" (🧠 +").append(wis).append("% Tốc Độ Suy Tính & Giảm Hao Chân Nguyên)");
                } else if (seg.startsWith("HPCOST:")) {
                    int hpCost = Integer.parseInt(seg.substring(7).trim());
                    character.setCurrentHp(Math.max(1, character.getCurrentHp() - hpCost));
                }
            }
        }

        character = characterRepository.saveAndFlush(character);

        String msg = String.format("🏆 Đã nhận thưởng [%s]: Nguyên Thạch %d ➔ %d (+%d💎), Cống Hiến %d ➔ %d (+%d📜)%s%s!",
                titlePart, oldStones, character.getPrimevalStones(), rewardStones,
                oldPoints, character.getContributionPoints(), rewardPoints,
                awardedGus.length() > 0 ? " & Cổ Trùng: " + awardedGus : "",
                extraBuffs);
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

    private String awardGuToCharacter(Long characterId, String guCode) {
        GuDefinition def = guDefinitionRepository.findByCode(guCode)
                .orElseGet(() -> guDefinitionRepository.findByCode("TU_TRUNG").orElseThrow());
        boolean isMutated = random.nextInt(100) < 12;
        CharacterGu newGu = CharacterGu.builder()
                .characterId(characterId)
                .guDefinition(def)
                .currentHungerHours(def.getMaxHungerHours())
                .isEquipped(false)
                .isHibernating(false)
                .isStarving(false)
                .isMutated(isMutated)
                .mutationBonus(isMutated ? 30 : 0)
                .build();
        characterGuRepository.save(newGu);
        return (isMutated ? "[DỊ BIẾN] " : "") + def.getName() + " (Chuyển " + def.getRank() + ")";
    }
}

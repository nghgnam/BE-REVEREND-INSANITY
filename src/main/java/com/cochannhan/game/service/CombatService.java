package com.cochannhan.game.service;

import com.cochannhan.game.dto.CombatActionRequest;
import com.cochannhan.game.dto.CombatStateDto;
import com.cochannhan.game.model.*;
import com.cochannhan.game.repository.CharacterGuRepository;
import com.cochannhan.game.repository.CharacterRepository;
import com.cochannhan.game.repository.GameStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class CombatService {

    private final CharacterRepository characterRepository;
    private final CharacterGuRepository characterGuRepository;
    private final GameStateRepository gameStateRepository;
    private final Random random = new Random();

    // In-memory active combat sessions
    private final Map<Long, CombatStateDto> activeSessions = new ConcurrentHashMap<>();

    @Transactional
    public CombatStateDto startCombat(Long characterId, String enemyType) {
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân vật không tồn tại"));

        String enemyName;
        String enemyTitle;
        int enemyMaxHp;
        int enemyEssence;
        List<String> knownGus = new ArrayList<>();
        int unknownGus;

        if ("DIEN_LANG".equalsIgnoreCase(enemyType) || "DIEN_LANG_W1".equalsIgnoreCase(enemyType)) {
            enemyName = "Điện Lang Đột Biến (Đợt 1)";
            enemyTitle = "Tiền Trạm Rừng Thanh Mao";
            enemyMaxHp = 120;
            enemyEssence = 80;
            knownGus.add("Lôi Trảo Sát");
            unknownGus = 1;
        } else if ("DIEN_LANG_W2".equalsIgnoreCase(enemyType)) {
            enemyName = "Hào Điện Lang (Đợt 2)";
            enemyTitle = "Cuồng Triều Xung Kích • Tinh Anh";
            enemyMaxHp = 220;
            enemyEssence = 140;
            knownGus.add("Lôi Giáp Cổ");
            unknownGus = 2;
        } else if ("DIEN_LANG_W3".equalsIgnoreCase(enemyType)) {
            enemyName = "Cuồng Điện Lang (Đợt 3)";
            enemyTitle = "Bách Thú Vương Thống Lĩnh";
            enemyMaxHp = 360;
            enemyEssence = 220;
            knownGus.add("Cuồng Lôi Trảo");
            unknownGus = 2;
        } else if ("DIEN_LANG_W4".equalsIgnoreCase(enemyType)) {
            enemyName = "Lôi Dực Điện Lang (Đợt 4)";
            enemyTitle = "Thiên Thú Vương Giáng Lâm • Dị Chủng";
            enemyMaxHp = 550;
            enemyEssence = 320;
            knownGus.add("Thiên Lôi Sí");
            unknownGus = 3;
        } else if ("DIEN_LANG_W5".equalsIgnoreCase(enemyType)) {
            enemyName = "Bạch Ngân Lang Vương (Đợt 5)";
            enemyTitle = "Vạn Thú Vương Chúa Tể • Đỉnh Cấp";
            enemyMaxHp = 850;
            enemyEssence = 450;
            knownGus.add("Ngân Lôi Diệt Tuyệt");
            unknownGus = 3;
        } else if ("CO_SU_MA_DAO".equalsIgnoreCase(enemyType)) {
            enemyName = "Cổ Sư Ma Đạo Bị Thương";
            enemyTitle = "Nhị Chuyển Tà Tu Trốn Chạy";
            enemyMaxHp = 220;
            enemyEssence = 180;
            knownGus.add("Huyết Thương Cổ");
            unknownGus = 2;
        } else {
            enemyName = "Đồng Môn Học Đường";
            enemyTitle = "Đệ Tử Cổ Nguyệt Gia";
            enemyMaxHp = 80;
            enemyEssence = 60;
            knownGus.add("Nguyệt Quang Cổ");
            unknownGus = 1;
        }

        List<CharacterGu> equippedGus = characterGuRepository.findByCharacterId(characterId).stream()
                .filter(CharacterGu::isEquipped)
                .toList();
        int bonusHp = equippedGus.stream().mapToInt(g -> g.getGuDefinition().getBonusHp()).sum();
        int playerMaxHp = character.getMaxHp() + bonusHp;

        CombatStateDto state = CombatStateDto.builder()
                .combatId(UUID.randomUUID().toString())
                .playerHp(Math.min(playerMaxHp, character.getCurrentHp() + bonusHp))
                .playerMaxHp(playerMaxHp)
                .playerEssence(character.getCurrentEssence())
                .playerMaxEssence(character.getMaxEssence())
                .playerAp(character.getMaxAp())
                .playerMaxAp(character.getMaxAp())
                .enemyName(enemyName)
                .enemyTitle(enemyTitle)
                .enemyHp(enemyMaxHp)
                .enemyMaxHp(enemyMaxHp)
                .enemyEssence(enemyEssence)
                .enemyKnownGus(knownGus)
                .enemyUnknownGuCount(unknownGus)
                .distance(CombatDistance.TAM_XA)
                .turnNumber(1)
                .isPlayerTurn(true)
                .isFinished(false)
                .isPlayerWon(false)
                .combatLogs(new ArrayList<>(List.of(
                        String.format("Chiến đấu khai mở! Chạm trán %s ở cự ly Tầm Xa.", enemyName)
                )))
                .build();

        activeSessions.put(characterId, state);
        return state;
    }

    @Transactional
    public CombatStateDto executeAction(Long characterId, CombatActionRequest request) {
        CombatStateDto combat = activeSessions.get(characterId);
        if (combat == null || combat.isFinished()) {
            throw new IllegalStateException("Không có trận chiến nào đang diễn ra!");
        }

        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân vật không tồn tại"));

        String action = request.getActionType();
        List<String> logs = combat.getCombatLogs();

        switch (action) {
            case "USE_GU" -> {
                CharacterGu gu = characterGuRepository.findById(request.getCharacterGuId())
                        .orElseThrow(() -> new IllegalArgumentException("Cổ trùng không tìm thấy"));

                GuDefinition def = gu.getGuDefinition();
                int essenceCost = (int) Math.round(combat.getPlayerMaxEssence() * (def.getPrimevalCostPercent() / 100.0));

                if (combat.getPlayerAp() < def.getApCost()) {
                    logs.add("⚠️ Không đủ Tâm Thần (AP) để xuất chiêu!");
                    return combat;
                }
                if (combat.getPlayerEssence() < essenceCost) {
                    logs.add("⚠️ Chân nguyên cạn kiệt! Không thể kích hoạt " + def.getName());
                    return combat;
                }

                combat.setPlayerAp(combat.getPlayerAp() - def.getApCost());
                combat.setPlayerEssence(combat.getPlayerEssence() - essenceCost);

                // Calculate damage & effects
                double distanceMultiplier = (combat.getDistance() == def.getOptimalDistance()) ? 1.3 : 0.8;
                double hungerMultiplier = gu.isStarving() ? 0.5 : 1.0;

                if (def.getType() == GuType.CONG_KICH) {
                    int bonusAtk = characterGuRepository.findByCharacterId(characterId).stream()
                            .filter(CharacterGu::isEquipped)
                            .mapToInt(g -> g.getGuDefinition().getBonusAtk())
                            .sum();
                    int dmg = (int) Math.round(def.getBaseDamage() * distanceMultiplier * hungerMultiplier) + bonusAtk;
                    if (gu.isMutated()) {
                        dmg = (int) Math.round(dmg * (1.0 + (gu.getMutationBonus() / 100.0)));
                    }
                    combat.setEnemyHp(Math.max(0, combat.getEnemyHp() - dmg));
                    String bonusAtkText = bonusAtk > 0 ? String.format(" (+%d uy lực từ Cổ trang bị)", bonusAtk) : "";
                    logs.add(String.format("🗡️ Bạn thi triển [%s]! Gây %d sát thương lên %s!%s",
                            def.getName(), dmg, combat.getEnemyName(), bonusAtkText));
                } else if (def.getType() == GuType.PHONG_NGU) {
                    int shield = (int) Math.round(def.getBaseDefense() * hungerMultiplier);
                    combat.setPlayerHp(Math.min(combat.getPlayerMaxHp(), combat.getPlayerHp() + shield / 2));
                    logs.add(String.format("🛡️ Kích hoạt [%s]! Phòng ngự bạch quang bao bọc thân thể (+%d phòng ngự)!",
                            def.getName(), shield));
                } else if (def.getType() == GuType.TRI_LIEU) {
                    int heal = (int) Math.round(def.getBaseHeal() * hungerMultiplier);
                    combat.setPlayerHp(Math.min(combat.getPlayerMaxHp(), combat.getPlayerHp() + heal));
                    logs.add(String.format("🍃 Dùng [%s]! Vết thương liền sẹo, hồi phục %d HP!", def.getName(), heal));
                } else if (def.getType() == GuType.TRINH_SAT) {
                    if (combat.getEnemyUnknownGuCount() > 0) {
                        combat.setEnemyUnknownGuCount(combat.getEnemyUnknownGuCount() - 1);
                        combat.getEnemyKnownGus().add("Cốt Hỏa Cổ");
                        logs.add(String.format("👂 Dùng [%s] nhìn thấu sơ hở! Phát hiện kẻ địch đang giấu Cốt Hỏa Cổ!", def.getName()));
                    } else {
                        logs.add(String.format("👂 Dùng [%s] dò xét toàn trường!", def.getName()));
                    }
                } else if (def.getType() == GuType.THAN_PHAP) {
                    combat.setDistance(CombatDistance.TAM_TRUNG);
                    logs.add(String.format("🌪️ Kích hoạt [%s] lướt nhanh chuyển đổi cự ly chiến trường!", def.getName()));
                }
            }
            case "ABSORB_STONE" -> {
                // "Đốt Nguyên Thạch giữa trận"
                if (character.getPrimevalStones() < 3) {
                    logs.add("⚠️ Không đủ 3 viên Nguyên Thạch trong túi để hấp thụ!");
                    return combat;
                }
                if (combat.getPlayerAp() < 1) {
                    logs.add("⚠️ Cần ít nhất 1 AP để bóp nát Nguyên Thạch!");
                    return combat;
                }
                combat.setPlayerAp(combat.getPlayerAp() - 1);
                character.setPrimevalStones(character.getPrimevalStones() - 3);
                int restoreEssence = (int) (combat.getPlayerMaxEssence() * 0.35);
                combat.setPlayerEssence(Math.min(combat.getPlayerMaxEssence(), combat.getPlayerEssence() + restoreEssence));
                logs.add(String.format("💎 [DÙNG TIỀN ĐÈ NGƯỜI] Bóp nát 3 viên Nguyên Thạch! Chân nguyên hồi ngay %d điểm!", restoreEssence));
            }
            case "MOVE_CLOSER" -> {
                if (combat.getPlayerAp() < 1) return combat;
                combat.setPlayerAp(combat.getPlayerAp() - 1);
                if (combat.getDistance() == CombatDistance.TAM_XA) {
                    combat.setDistance(CombatDistance.TAM_TRUNG);
                    logs.add("🏃 Bạn áp sát về cự ly Tầm Trung!");
                } else if (combat.getDistance() == CombatDistance.TAM_TRUNG) {
                    combat.setDistance(CombatDistance.CAN_CHIEN);
                    logs.add("🏃 Bạn lao vào cự ly Cận Chiến giáp lá cà!");
                }
            }
            case "MOVE_AWAY" -> {
                if (combat.getPlayerAp() < 1) return combat;
                combat.setPlayerAp(combat.getPlayerAp() - 1);
                if (combat.getDistance() == CombatDistance.CAN_CHIEN) {
                    combat.setDistance(CombatDistance.TAM_TRUNG);
                    logs.add("💨 Bạn lùi nhanh về cự ly Tầm Trung!");
                } else if (combat.getDistance() == CombatDistance.TAM_TRUNG) {
                    combat.setDistance(CombatDistance.TAM_XA);
                    logs.add("💨 Bạn kéo giãn khoảng cách ra Tầm Xa thả diều!");
                }
            }
            case "END_COMBAT_TURN" -> {
                // Enemy Turn Execution
                executeEnemyTurn(characterId, combat, logs);
                // Reset player AP and naturally restore essence
                combat.setTurnNumber(combat.getTurnNumber() + 1);
                combat.setPlayerAp(combat.getPlayerMaxAp());
                int turnRegen = (int) (combat.getPlayerMaxEssence() * (character.getAptitude().getRecoveryRatePerTurn() / 100.0));
                combat.setPlayerEssence(Math.min(combat.getPlayerMaxEssence(), combat.getPlayerEssence() + turnRegen));
                logs.add(String.format("--- Lượt %d: Chân nguyên tự hồi phục %d điểm ---", combat.getTurnNumber(), turnRegen));
            }
        }

        // Check Victory / Defeat
        if (combat.getEnemyHp() <= 0) {
            combat.setFinished(true);
            combat.setPlayerWon(true);
            int stoneReward;
            int contriReward;
            String bonusLoot = "";
            
            if (combat.getEnemyName().contains("Đợt 5") || combat.getEnemyName().contains("Bạch Ngân")) {
                stoneReward = 200 + random.nextInt(100);
                contriReward = 120;
                bonusLoot = " [Bạch Ngân Lang Nha, Tinh Hạch Vạn Thú]";
            } else if (combat.getEnemyName().contains("Đợt 4") || combat.getEnemyName().contains("Lôi Dực")) {
                stoneReward = 110 + random.nextInt(40);
                contriReward = 65;
                bonusLoot = " [Lôi Dực Linh Vũ, Lôi Thạch Cực Phẩm]";
            } else if (combat.getEnemyName().contains("Đợt 3") || combat.getEnemyName().contains("Cuồng Điện")) {
                stoneReward = 75 + random.nextInt(25);
                contriReward = 45;
                bonusLoot = " [Cuồng Lôi Lang Bì, Huyết Tinh Thạch]";
            } else if (combat.getEnemyName().contains("Đợt 2") || combat.getEnemyName().contains("Hào Điện")) {
                stoneReward = 50 + random.nextInt(25);
                contriReward = 30;
                bonusLoot = " [Điện Hào Thiết Giáp]";
            } else if (combat.getEnemyName().contains("Điện Lang")) {
                stoneReward = 35 + random.nextInt(15);
                contriReward = 20;
                bonusLoot = " [Điện Lang Nha]";
            } else {
                stoneReward = 15 + random.nextInt(20);
                contriReward = 10;
            }

            character.setPrimevalStones(character.getPrimevalStones() + stoneReward);
            character.setContributionPoints(character.getContributionPoints() + contriReward);
            combat.setRewardMessage(String.format("🏆 ĐẠI THẮNG! Trảm sát %s! Thu được %d Nguyên Thạch, %d Điểm Cống Hiến%s!",
                    combat.getEnemyName(), stoneReward, contriReward, bonusLoot));
            logs.add(combat.getRewardMessage());
        } else if (combat.getPlayerHp() <= 0) {
            combat.setFinished(true);
            combat.setPlayerWon(false);
            logs.add("💀 BẠN ĐÃ TỬ TRẬN! Sinh mệnh cạn kiệt!");
        }

        // Save character HP and essence back
        character.setCurrentHp(combat.getPlayerHp());
        character.setCurrentEssence(combat.getPlayerEssence());
        characterRepository.save(character);

        return combat;
    }

    private void executeEnemyTurn(Long characterId, CombatStateDto combat, List<String> logs) {
        if (combat.getEnemyHp() <= 0) return;

        int baseDmg = 20;
        if (combat.getEnemyName().contains("Đợt 5") || combat.getEnemyName().contains("Bạch Ngân")) {
            baseDmg = 55;
        } else if (combat.getEnemyName().contains("Đợt 4") || combat.getEnemyName().contains("Lôi Dực")) {
            baseDmg = 42;
        } else if (combat.getEnemyName().contains("Đợt 3") || combat.getEnemyName().contains("Cuồng Điện")) {
            baseDmg = 32;
        } else if (combat.getEnemyName().contains("Đợt 2") || combat.getEnemyName().contains("Hào Điện")) {
            baseDmg = 25;
        }

        int bonusDef = characterGuRepository.findByCharacterId(characterId).stream()
                .filter(CharacterGu::isEquipped)
                .mapToInt(g -> g.getGuDefinition().getBonusDef())
                .sum();

        int enemyDmg = Math.max(1, (baseDmg + random.nextInt(20)) - bonusDef);
        combat.setPlayerHp(Math.max(0, combat.getPlayerHp() - enemyDmg));
        String defNotice = bonusDef > 0 ? String.format(" (Cổ phòng ngự giảm %d sát thương)", bonusDef) : "";
        logs.add(String.format("💥 %s phản công dữ dội! Gây %d sát thương lên bạn!%s",
                combat.getEnemyName(), enemyDmg, defNotice));
    }

    public CombatStateDto getCombatState(Long characterId) {
        return activeSessions.get(characterId);
    }
}

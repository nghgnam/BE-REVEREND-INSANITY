package com.cochannhan.game.service;

import com.cochannhan.game.dto.GameStateDto;
import com.cochannhan.game.dto.GuRecipeDto;
import com.cochannhan.game.dto.RefineGuRequest;
import com.cochannhan.game.model.*;
import com.cochannhan.game.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuService {

    private final CharacterRepository characterRepository;
    private final CharacterGuRepository characterGuRepository;
    private final GuDefinitionRepository guDefinitionRepository;
    private final GuRecipeRepository guRecipeRepository;
    private final GameStateRepository gameStateRepository;
    private final Random random = new Random();

    @Transactional
    public GameStateDto feedGu(Long characterId, Long characterGuId) {
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân vật không tồn tại"));

        CharacterGu gu = characterGuRepository.findById(characterGuId)
                .orElseThrow(() -> new IllegalArgumentException("Cổ trùng không tồn tại"));

        int feedingCost = 5; // 5 Nguyên thạch để mua thức ăn đặc thù
        if (character.getPrimevalStones() < feedingCost) {
            throw new IllegalStateException("Không đủ Nguyên Thạch để mua thức ăn cho " + gu.getGuDefinition().getName());
        }

        character.setPrimevalStones(character.getPrimevalStones() - feedingCost);
        gu.feed(gu.getGuDefinition().getMaxHungerHours());
        characterGuRepository.save(gu);
        characterRepository.save(character);

        String msg = String.format("Đã cho %s ăn no (%s)! Độ đói hồi phục 100%%.",
                gu.getGuDefinition().getName(), gu.getGuDefinition().getFoodType());

        return getUpdatedGameState(characterId, msg);
    }

    @Transactional
    public GameStateDto toggleEquip(Long characterId, Long characterGuId) {
        CharacterGu gu = characterGuRepository.findById(characterGuId)
                .orElseThrow(() -> new IllegalArgumentException("Cổ trùng không tồn tại"));

        if (!gu.getCharacterId().equals(characterId)) {
            throw new IllegalArgumentException("Cổ trùng này không thuộc về bạn");
        }

        List<CharacterGu> allPlayerGus = characterGuRepository.findByCharacterId(characterId);
        List<CharacterGu> equipped = allPlayerGus.stream().filter(CharacterGu::isEquipped).toList();

        if (gu.isEquipped()) {
            gu.setEquipped(false);
            gu.setSlotIndex(null);
            characterGuRepository.save(gu);
            String msg = "Đã tháo " + gu.getGuDefinition().getName() + " về kho Không Khiếu.";
            return getUpdatedGameState(characterId, msg);
        }

        // Xuan Thu Thien: Slot ngầm vĩnh cửu
        if ("XUAN_THU_THIEN".equals(gu.getGuDefinition().getCode()) || "TIEN_CO_NGAM".equals(gu.getGuDefinition().getSlotCategory())) {
            gu.setEquipped(true);
            gu.setSlotIndex(99);
            characterGuRepository.save(gu);
            return getUpdatedGameState(characterId, "Xuân Thu Thiền đã thức tỉnh ở tầng sâu Không Khiếu, bảo hộ linh hồn!");
        }

        // 6 CATEGORIZED LOADOUT SLOTS:
        // Slot 0: Ô Công Kích 1
        // Slot 1: Ô Công Kích 2
        // Slot 2: Ô Phòng Ngự
        // Slot 3: Ô Thân Pháp / Di Chuyển
        // Slot 4: Ô Trinh Sát / Phụ Trợ
        // Slot 5: Ô Cổ Đặc Biệt / Tiêu Hao
        int targetSlot = determineTargetSlot(gu, equipped);

        // Unequip whatever currently occupies targetSlot
        allPlayerGus.stream()
                .filter(g -> g.isEquipped() && Objects.equals(g.getSlotIndex(), targetSlot))
                .forEach(g -> {
                    g.setEquipped(false);
                    g.setSlotIndex(null);
                    characterGuRepository.save(g);
                });

        gu.setEquipped(true);
        gu.setSlotIndex(targetSlot);
        characterGuRepository.save(gu);

        String slotName = getSlotName(targetSlot);
        String msg = String.format("Đã nạp %s vào [%s] Không Khiếu!", gu.getGuDefinition().getName(), slotName);
        return getUpdatedGameState(characterId, msg);
    }

    private int determineTargetSlot(CharacterGu gu, List<CharacterGu> equipped) {
        String category = gu.getGuDefinition().getSlotCategory();
        if (category == null || category.isEmpty()) {
            GuType type = gu.getGuDefinition().getType();
            if (type == GuType.CONG_KICH) category = "CONG_KICH";
            else if (type == GuType.PHONG_NGU) category = "PHONG_NGU";
            else if (type == GuType.THAN_PHAP) category = "THAN_PHAP";
            else if (type == GuType.DAC_BIET) category = "DAC_BIET";
            else category = "PHU_TRO";
        }

        switch (category) {
            case "CONG_KICH":
                boolean slot0Occupied = equipped.stream().anyMatch(g -> Objects.equals(g.getSlotIndex(), 0));
                boolean slot1Occupied = equipped.stream().anyMatch(g -> Objects.equals(g.getSlotIndex(), 1));
                if (!slot0Occupied) return 0;
                if (!slot1Occupied) return 1;
                return 0; // replace slot 0
            case "PHONG_NGU":
                return 2;
            case "THAN_PHAP":
                return 3;
            case "PHU_TRO":
            case "TRINH_SAT":
            case "TRI_LIEU":
                return 4;
            case "DAC_BIET":
                return 5;
            default:
                for (int i = 0; i < 6; i++) {
                    final int idx = i;
                    if (equipped.stream().noneMatch(g -> Objects.equals(g.getSlotIndex(), idx))) {
                        return idx;
                    }
                }
                return 0;
        }
    }

    private String getSlotName(int slotIndex) {
        switch (slotIndex) {
            case 0: return "Ô Công Kích 1";
            case 1: return "Ô Công Kích 2";
            case 2: return "Ô Phòng Ngự";
            case 3: return "Ô Thân Pháp";
            case 4: return "Ô Phụ Trợ / Trinh Sát";
            case 5: return "Ô Cổ Đặc Biệt";
            default: return "Ô Tiên Cổ Ngầm";
        }
    }

    @Transactional
    public GameStateDto toggleHibernate(Long characterId, Long characterGuId) {
        CharacterGu gu = characterGuRepository.findById(characterGuId)
                .orElseThrow(() -> new IllegalArgumentException("Cổ trùng không tồn tại"));

        if (gu.isEquipped()) {
            throw new IllegalStateException("Cổ trùng đang trang bị chiến đấu không thể phong ấn ngủ đông!");
        }

        gu.setHibernating(!gu.isHibernating());
        characterGuRepository.save(gu);

        String msg = gu.isHibernating() ? "Đã phong ấn " + gu.getGuDefinition().getName() + " ngủ đông (giảm 80% tốc độ đói)."
                : "Đã đánh thức " + gu.getGuDefinition().getName() + " dậy hoạt động.";

        return getUpdatedGameState(characterId, msg);
    }

    @Transactional
    public GameStateDto refineGu(Long characterId, RefineGuRequest request) {
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân vật không tồn tại"));

        List<CharacterGu> playerGus = characterGuRepository.findByCharacterId(characterId);
        CharacterGu primaryCharGu = playerGus.stream()
                .filter(g -> g.getGuDefinition().getCode().equals(request.getPrimaryGuCode()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Bạn không sở hữu Cổ trùng chủ thể: " + request.getPrimaryGuCode()));

        CharacterGu secondaryCharGu = null;
        if (request.getSecondaryGuCode() != null && !request.getSecondaryGuCode().isEmpty()) {
            secondaryCharGu = playerGus.stream()
                    .filter(g -> !g.getId().equals(primaryCharGu.getId()) && g.getGuDefinition().getCode().equals(request.getSecondaryGuCode()))
                    .findFirst()
                    .orElse(null);
        }

        // Check if there is an existing registered recipe
        Optional<GuRecipe> recipeOpt = guRecipeRepository.findByPrimaryGuCodeAndSecondaryGuCode(
                request.getPrimaryGuCode(), request.getSecondaryGuCode());
        if (recipeOpt.isEmpty() && request.getSecondaryGuCode() != null) {
            recipeOpt = guRecipeRepository.findByPrimaryGuCodeAndSecondaryGuCode(
                    request.getSecondaryGuCode(), request.getPrimaryGuCode());
        }

        int stoneCost = 35;
        int successRate = 65;
        int backlashDamage = 30;
        GuDefinition resultDef = null;
        boolean isHiddenRecipeDiscovered = false;

        GuDefinition primaryDef = primaryCharGu.getGuDefinition();
        GuDefinition secondaryDef = secondaryCharGu != null ? secondaryCharGu.getGuDefinition() : null;

        if (recipeOpt.isPresent()) {
            GuRecipe recipe = recipeOpt.get();
            stoneCost = recipe.getStoneCost();
            backlashDamage = recipe.getBacklashDamage();
            resultDef = guDefinitionRepository.findByCode(recipe.getResultGuCode()).orElse(null);
        } else {
            // EXPERIMENTATION (THỬ NGHIỆM LÒ CỔ & BÍ PHƯƠNG ẨN)
            int targetRank = Math.min(6, Math.max(primaryDef.getRank(), secondaryDef != null ? secondaryDef.getRank() : 1) + 1);
            final int tRank = targetRank;
            final String tPath = primaryDef.getPathName() != null ? primaryDef.getPathName() : "";

            resultDef = guDefinitionRepository.findAll().stream()
                    .filter(g -> g.getRank() == tRank && g.getPathName() != null && g.getPathName().equalsIgnoreCase(tPath))
                    .findAny()
                    .orElseGet(() -> guDefinitionRepository.findAll().stream()
                            .filter(g -> g.getRank() == tRank)
                            .findAny()
                            .orElse(primaryDef));

            isHiddenRecipeDiscovered = true;
        }

        // RULE 2: TỶ LỆ THÀNH CÔNG (SUCCESS RATE CALCULATION)
        // BaseSuccessRate = RecipeQualityPercent (70% Bí phương chính ngạch, 30% Bí phương tự mò)
        // + (PlayerRefinementLevel * 2%)
        // + PathBonus (Cùng lưu phái +10%, Trái lưu phái khắc chế -40%)
        // + LuckStatBonus (Tích lũy từ Vận Đạo)
        int recipeQuality = recipeOpt.isPresent() ? 70 : 30;
        int playerRefinementLevel = character.getRank().ordinal() + 1;
        int refinementSkillBonus = playerRefinementLevel * 2;

        String p1 = primaryDef.getPathName() != null ? primaryDef.getPathName() : "";
        String p2 = secondaryDef != null && secondaryDef.getPathName() != null ? secondaryDef.getPathName() : p1;

        boolean isSamePath = !p1.isEmpty() && p1.equalsIgnoreCase(p2);
        boolean isConflict = (p1.contains("Thủy") && p2.contains("Viêm")) || (p1.contains("Viêm") && p2.contains("Thủy"));

        int pathBonus = 0;
        if (isSamePath) {
            pathBonus = 10;
        } else if (isConflict) {
            pathBonus = -40;
            backlashDamage = 80;
        }

        int luckStatBonus = (character.getDaoMarksLuc() / 15) + (character.getRebirthCount() * 2);
        successRate = Math.min(95, Math.max(5, recipeQuality + refinementSkillBonus + pathBonus + luckStatBonus));

        if (character.getPrimevalStones() < stoneCost) {
            throw new IllegalStateException("Không đủ " + stoneCost + " Nguyên Thạch để đốt lò luyện cổ!");
        }
        character.setPrimevalStones(character.getPrimevalStones() - stoneCost);

        int roll = random.nextInt(100);
        String outcomeMessage;

        if (roll < successRate && resultDef != null) {
            // SUCCESS
            characterGuRepository.delete(primaryCharGu);
            if (secondaryCharGu != null) {
                characterGuRepository.delete(secondaryCharGu);
            }

            // RULE 4: ĐỘT BIẾN LƯỢNG BIẾN (MUTATION CHANCE: 2%)
            boolean isMutated = random.nextInt(100) < 2;
            int mutationBonus = isMutated ? 30 : 0;
            String mutationPassive = null;
            if (isMutated) {
                String[] passives = {
                    "Xuyên giáp 15%",
                    "Giảm 20% Chân Nguyên tiêu hao",
                    "Tăng chu kỳ nhịn đói gấp đôi",
                    "Bạo kích sát thương 25%",
                    "Phản chấn 20% sát thương"
                };
                mutationPassive = passives[random.nextInt(passives.length)];
            }

            CharacterGu newGu = CharacterGu.builder()
                    .characterId(characterId)
                    .guDefinition(resultDef)
                    .currentHungerHours(resultDef.getMaxHungerHours())
                    .isEquipped(false)
                    .isHibernating(false)
                    .isStarving(false)
                    .isMutated(isMutated)
                    .mutationBonus(mutationBonus)
                    .mutationPassive(mutationPassive)
                    .build();
            characterGuRepository.save(newGu);

            if (isMutated) {
                outcomeMessage = String.format("🌟 THIÊN BIẾN VẠN HÓA! Kích hoạt Đột Biến Cổ Trùng (2%%)! Ra lò [DỊ BIẾN] %s (+%d%% uy lực, Thuộc tính ẩn: %s) phẩm chất vượt cấp!",
                        resultDef.getName(), mutationBonus, mutationPassive);
            } else {
                outcomeMessage = String.format("🔥 HỢP LUYỆN ĐẠI THÀNH CÔNG! %s Hào quang rực rỡ, xuất lò %s (%s - Chuyển %d) uy lực bất phàm!",
                        isHiddenRecipeDiscovered ? "✨ Khám phá Bí Phương Ẩn mới!" : "",
                        resultDef.getName(), resultDef.getPathName() != null ? resultDef.getPathName() : (resultDef.getType() != null ? resultDef.getType().getLabel() : "Cổ Trùng"), resultDef.getRank());
            }

            if (isHiddenRecipeDiscovered && secondaryDef != null) {
                GuRecipe newRecipe = GuRecipe.builder()
                        .recipeName("Bí Phương " + resultDef.getName())
                        .primaryGuCode(primaryDef.getCode())
                        .secondaryGuCode(secondaryDef.getCode())
                        .materialName(request.getMaterialName() != null ? request.getMaterialName() : "Hỗn Hợp Tinh Hoa")
                        .environmentCondition(request.getEnvironmentCondition() != null ? request.getEnvironmentCondition() : "Bình thường")
                        .resultGuCode(resultDef.getCode())
                        .stoneCost(stoneCost)
                        .successRate(successRate)
                        .backlashDamage(backlashDamage)
                        .isUnlocked(true)
                        .build();
                guRecipeRepository.save(newRecipe);
            }
        } else {
            // RULE 3: HẬU QUẢ THẤT BẠI (FAILURE PENALTY)
            int failSeverity = random.nextInt(100);
            if (failSeverity < 60) {
                // 60% Tỉ lệ: Mất toàn bộ SubMaterials. Cổ chủ thể giảm 50% Độ No (Hunger).
                if (secondaryCharGu != null) {
                    characterGuRepository.delete(secondaryCharGu);
                }
                primaryCharGu.setCurrentHungerHours(Math.max(1, primaryCharGu.getCurrentHungerHours() / 2));
                characterGuRepository.save(primaryCharGu);
                outcomeMessage = String.format("⚠️ LUYỆN CỔ THẤT BẠI (Mất Phụ Liệu)! Toàn bộ nguyên liệu phụ trợ tan biến. Cổ chủ thể %s bảo toàn tính mạng nhưng bị tiêu hao 50%% Độ No!",
                        primaryDef.getName());
            } else if (failSeverity < 90) {
                // 30% Tỉ lệ: Mất SubMaterials + Tiêu hủy Cổ chủ thể (Mất xác).
                characterGuRepository.delete(primaryCharGu);
                if (secondaryCharGu != null) {
                    characterGuRepository.delete(secondaryCharGu);
                }
                outcomeMessage = String.format("💀 LUYỆN CỔ THẤT BẠI (Mất Xác)! Lực lượng nghịch chuyển bạo phát, Cổ chủ thể %s cùng toàn bộ nguyên liệu phụ trợ hoàn toàn tiêu hủy!",
                        primaryDef.getName());
            } else {
                // 10% Tỉ lệ (NỔ LÒ): Mất tất cả + Trừ 30% HP Nhân vật + Giảm 20% Chân Nguyên tối đa trong 3 ngày
                characterGuRepository.delete(primaryCharGu);
                if (secondaryCharGu != null) {
                    characterGuRepository.delete(secondaryCharGu);
                }
                int hpLoss = Math.max(20, (int) Math.round(character.getMaxHp() * 0.3));
                character.setCurrentHp(Math.max(1, character.getCurrentHp() - hpLoss));
                character.setCurrentEssence((int) (character.getCurrentEssence() * 0.8));
                outcomeMessage = String.format("💥 ĐẠI HỌA NỔ LÒ (Thương Hại Không Khiếu)! Lò luyện phát nổ kinh hoàng gây phản phệ trừ 30%% Khí Huyết (-%d HP) và vách Không Khiếu chấn thương giảm 20%% Chân Nguyên!",
                        hpLoss);
            }
        }

        characterRepository.save(character);
        return getUpdatedGameState(characterId, outcomeMessage);
    }

    @Transactional
    public GameStateDto useGu(Long characterId, Long characterGuId) {
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân vật không tồn tại"));

        CharacterGu gu = characterGuRepository.findById(characterGuId)
                .orElseThrow(() -> new IllegalArgumentException("Cổ trùng không tồn tại"));

        if (!gu.getCharacterId().equals(characterId)) {
            throw new IllegalArgumentException("Cổ trùng này không thuộc về bạn");
        }

        GuDefinition def = gu.getGuDefinition();
        if (!def.isConsumable() && def.getType() != GuType.TIEU_THU && !"DAC_BIET".equalsIgnoreCase(def.getSlotCategory())) {
            throw new IllegalStateException(def.getName() + " là Cổ trùng trang bị! Hãy nạp vào các ô chiến đấu Không Khiếu để thôi động.");
        }

        String msg;
        String code = def.getCode();

        // 1. XÁ LỢI CỔ TỪ 1-5 CHUYỂN
        if ("THANH_DONG_XA_LOI_CO".equals(code) || (code.contains("XA_LOI") && def.getRank() == 1)) {
            if (character.getRank() != CultivationRank.NHAT_CHUYEN) {
                throw new IllegalStateException("Thanh Đồng Xá Lợi Cổ chỉ có tác dụng đối với Cổ sư Nhất Chuyển! Tu vi của bạn không phù hợp.");
            }
            msg = advanceSubStageDirectly(character, "Thanh Đồng Xá Lợi Cổ");
            characterGuRepository.delete(gu);
        } else if ("XICH_THIET_XA_LOI_CO".equals(code) || (code.contains("XA_LOI") && def.getRank() == 2)) {
            if (character.getRank() != CultivationRank.NHI_CHUYEN) {
                throw new IllegalStateException("Xích Thiết Xá Lợi Cổ chỉ có tác dụng đối với Cổ sư Nhị Chuyển! Tu vi của bạn không phù hợp.");
            }
            msg = advanceSubStageDirectly(character, "Xích Thiết Xá Lợi Cổ");
            characterGuRepository.delete(gu);
        } else if ("BACH_NGAN_XA_LOI_CO".equals(code) || (code.contains("XA_LOI") && def.getRank() == 3)) {
            if (character.getRank() != CultivationRank.TAM_CHUYEN) {
                throw new IllegalStateException("Bạch Ngân Xá Lợi Cổ chỉ có tác dụng đối với Cổ sư Tam Chuyển! Tu vi của bạn không phù hợp.");
            }
            msg = advanceSubStageDirectly(character, "Bạch Ngân Xá Lợi Cổ");
            characterGuRepository.delete(gu);
        } else if ("HOANG_KIM_XA_LOI_CO".equals(code) || (code.contains("XA_LOI") && def.getRank() == 4)) {
            if (character.getRank() != CultivationRank.TU_CHUYEN) {
                throw new IllegalStateException("Hoàng Kim Xá Lợi Cổ chỉ có tác dụng đối với Cổ sư Tứ Chuyển! Tu vi của bạn không phù hợp.");
            }
            msg = advanceSubStageDirectly(character, "Hoàng Kim Xá Lợi Cổ");
            characterGuRepository.delete(gu);
        } else if ("TU_TINH_XA_LOI_CO".equals(code) || (code.contains("XA_LOI") && def.getRank() == 5)) {
            if (character.getRank() != CultivationRank.NGU_CHUYEN) {
                throw new IllegalStateException("Tử Tinh Xá Lợi Cổ chỉ có tác dụng đối với Cổ sư Ngũ Chuyển! Tu vi của bạn không phù hợp.");
            }
            msg = advanceSubStageDirectly(character, "Tử Tinh Xá Lợi Cổ");
            characterGuRepository.delete(gu);
        } else if ("THO_CO".equals(code) || code.contains("THO_CO")) {
            // Thọ Cổ: Tăng vĩnh viễn tuổi thọ (không bị phản phệ)
            character.setMaxLifespanYears(character.getMaxLifespanYears() + 20);
            character.setCurrentLifespanYears(character.getCurrentLifespanYears() + 20);
            characterGuRepository.delete(gu);
            msg = "⏳ THỌ NGUYÊN BẤT DIỆT! Hấp thụ Thọ Cổ ngưng tụ sinh mệnh tinh hoa, vĩnh viễn gia tăng 20 năm tuổi thọ!";
        } else if ("SINH_CO_DIEP".equals(code)) {
            character.setCurrentHp(character.getMaxHp());
            characterGuRepository.delete(gu);
            msg = "🍃 Sinh Cơ Diệp dán vào lồng ngực, hồi phục trọn vẹn 100% Khí Huyết (HP)!";
        } else if ("HOI_XUAN_CO".equals(code)) {
            character.setCurrentHp(character.getMaxHp());
            character.setCurrentEssence(character.getMaxEssence());
            characterGuRepository.delete(gu);
            msg = "🌸 Sinh cơ mùa xuân cuộn trào! Toàn bộ Khí Huyết và Chân Nguyên được hồi phục đỉnh phong!";
        } else if ("HAC_THI_CO".equals(code) || "BACH_THI_CO".equals(code)) {
            character.setMaxHp(character.getMaxHp() + 30);
            character.setCurrentHp(character.getCurrentHp() + 30);
            character.setDaoMarksLuc(character.getDaoMarksLuc() + 10);
            characterGuRepository.delete(gu);
            msg = String.format("🐗 Dùng %s cọ rửa gân cốt! Vĩnh viễn tăng 30 Khí Huyết và 10 Đạo Ngân Lực Đạo!", def.getName());
        } else if ("LOI_TUONG_CO".equals(code)) {
            characterGuRepository.delete(gu);
            msg = "⚡ Lôi Tương bộc phát! Phóng thích sóng lôi đình chấn nhiếp toàn trường!";
        } else {
            characterGuRepository.delete(gu);
            msg = String.format("✨ Đã tiêu thụ %s! Dược lực tinh hoa ngấm sâu vào Không Khiếu.", def.getName());
        }

        characterRepository.save(character);
        return getUpdatedGameState(characterId, msg);
    }

    private String advanceSubStageDirectly(CharacterEntity character, String guName) {
        SubStage currentStage = character.getSubStage();
        if (currentStage == SubStage.DINH_PHONG) {
            return String.format("⚠️ Bạn đã đạt %s Đỉnh Phong! %s không thể giúp phá vỡ vách ngăn cảnh giới lớn, hãy dùng Chân Nguyên cọ rửa vách khiếu!",
                    character.getRank().getRankName(), guName);
        }

        SubStage nextStage = switch (currentStage) {
            case SO_GIAI -> SubStage.TRUNG_GIAI;
            case TRUNG_GIAI -> SubStage.CAO_GIAI;
            case CAO_GIAI -> SubStage.DINH_PHONG;
            default -> SubStage.DINH_PHONG;
        };

        character.setSubStage(nextStage);
        character.setBreakThroughExp(0);
        character.setMaxHp(character.getMaxHp() + 35);
        character.setCurrentHp(character.getMaxHp());
        character.setCurrentEssence(character.getMaxEssence());

        return String.format("🌟 THẦN DIỆU VÔ CÙNG! Tiêu thụ [%s], sóng chân nguyên dâng trào trực tiếp ép tăng một bậc cảnh giới! Đạt %s %s!",
                guName, character.getRank().getRankName(), nextStage.getStageName());
    }

    private GameStateDto getUpdatedGameState(Long characterId, String announcement) {
        CharacterEntity character = characterRepository.findById(characterId).orElseThrow();
        GameStateEntity state = gameStateRepository.findByCharacterId(characterId).orElseThrow();
        state.setLastEventLog(announcement);
        gameStateRepository.save(state);
        List<CharacterGu> allGus = characterGuRepository.findByCharacterId(characterId);

        return GameStateDto.builder()
                .character(character)
                .gameState(state)
                .equippedGus(allGus.stream().filter(CharacterGu::isEquipped).toList())
                .allGus(allGus)
                .announcement(announcement)
                .build();
    }

    public List<GuRecipeDto> getAllRecipes() {
        List<GuRecipe> recipes = guRecipeRepository.findAll();
        List<GuRecipeDto> dtos = new ArrayList<>();

        for (GuRecipe r : recipes) {
            GuDefinition primary = guDefinitionRepository.findByCode(r.getPrimaryGuCode()).orElse(null);
            GuDefinition secondary = r.getSecondaryGuCode() != null 
                    ? guDefinitionRepository.findByCode(r.getSecondaryGuCode()).orElse(null) : null;
            GuDefinition result = r.getResultGuCode() != null 
                    ? guDefinitionRepository.findByCode(r.getResultGuCode()).orElse(null) : null;

            dtos.add(GuRecipeDto.builder()
                    .id(r.getId())
                    .recipeName(r.getRecipeName())
                    .primaryGuCode(r.getPrimaryGuCode())
                    .primaryGuName(primary != null ? primary.getName() : r.getPrimaryGuCode())
                    .primaryGuIcon(primary != null && primary.getIcon() != null ? primary.getIcon() : "🐛")
                    .primaryGuRank(primary != null ? primary.getRank() : 1)
                    .secondaryGuCode(r.getSecondaryGuCode())
                    .secondaryGuName(secondary != null ? secondary.getName() : (r.getSecondaryGuCode() != null ? r.getSecondaryGuCode() : "Không"))
                    .secondaryGuIcon(secondary != null && secondary.getIcon() != null ? secondary.getIcon() : "🐛")
                    .secondaryGuRank(secondary != null ? secondary.getRank() : 1)
                    .materialName(r.getMaterialName() != null ? r.getMaterialName() : "Ngọc Thạch Phấn")
                    .resultGuCode(r.getResultGuCode())
                    .resultGuName(result != null ? result.getName() : r.getResultGuCode())
                    .resultGuIcon(result != null && result.getIcon() != null ? result.getIcon() : "✨")
                    .resultGuRank(result != null ? result.getRank() : 2)
                    .resultGuPath(result != null && result.getPathName() != null ? result.getPathName() : "Cổ Đạo")
                    .stoneCost(r.getStoneCost())
                    .successRate(r.getSuccessRate())
                    .backlashDamage(r.getBacklashDamage())
                    .environmentCondition(r.getEnvironmentCondition() != null ? r.getEnvironmentCondition() : "Bình thường")
                    .isUnlocked(r.isUnlocked())
                    .description(result != null && result.getDescription() != null ? result.getDescription() : "")
                    .build());
        }

        dtos.sort(Comparator.comparingInt(GuRecipeDto::getResultGuRank).thenComparing(GuRecipeDto::getSuccessRate).reversed());
        return dtos;
    }
}

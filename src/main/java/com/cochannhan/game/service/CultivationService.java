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

        int stonesToUse = (request != null) ? Math.max(0, request.getStonesUsed()) : 0;
        if (stonesToUse > character.getPrimevalStones()) {
            throw new IllegalStateException("Không đủ Nguyên Thạch trong túi!");
        }

        int baseEssenceCost = (int) (character.getMaxEssence() * 0.3);
        int essenceCost = baseEssenceCost;
        if (stonesToUse >= 2) {
            // Dùng từ 2 Nguyên Thạch trở lên: hoàn toàn rút chân nguyên từ đá, không hao chân nguyên trong khiếu!
            essenceCost = 0;
        } else if (stonesToUse == 1) {
            // Dùng 1 Nguyên Thạch: giảm 50% lượng chân nguyên tiêu hao
            essenceCost = Math.max(0, baseEssenceCost / 2);
        }

        // Nếu không có Nguyên Thạch trợ lực và Chân Nguyên dưới 30% thì báo lỗi
        if (character.getCurrentEssence() < essenceCost && stonesToUse <= 0) {
            throw new IllegalStateException("Chân nguyên không đủ để cọ rửa vách Không Khiếu (cần ít nhất 30% chân nguyên hoặc dùng ít nhất 1 viên Nguyên Thạch trợ lực).");
        }

        // Spend resources
        character.setCurrentEssence(Math.max(0, character.getCurrentEssence() - essenceCost));
        character.setPrimevalStones(Math.max(0, character.getPrimevalStones() - stonesToUse));

        // Check for equipped Tửu Trùng (Wine Gu) - boosts aperture wall cleansing by rank
        int tuuTrungBonus = 0;
        String highestTuuName = "";
        for (CharacterGu g : characterGuRepository.findByCharacterId(characterId)) {
            if (!g.isEquipped()) continue;
            String c = g.getGuDefinition().getCode();
            String n = g.getGuDefinition().getName();
            if ("BACH_QUA_TIEN_TUU_CO".equals(c) && tuuTrungBonus < 160) {
                tuuTrungBonus = 160;
                highestTuuName = n;
            } else if ("CUU_CHUYEN_TIEN_TUU_TRUNG".equals(c) && tuuTrungBonus < 110) {
                tuuTrungBonus = 110;
                highestTuuName = n;
            } else if ("THAT_HUONG_TUU_TRUNG".equals(c) && tuuTrungBonus < 75) {
                tuuTrungBonus = 75;
                highestTuuName = n;
            } else if ("TU_VI_TUU_TRUNG".equals(c) && tuuTrungBonus < 50) {
                tuuTrungBonus = 50;
                highestTuuName = n;
            } else if ((c.contains("TU_TRUNG") || c.contains("TUU_TRUNG") || n.contains("Tửu Trùng")) && tuuTrungBonus < 35) {
                tuuTrungBonus = 35;
                highestTuuName = n;
            }
        }
        String wineNotice = tuuTrungBonus > 0 ? String.format(" 🍶 [%s tinh luyện chân nguyên +%d điểm!]", highestTuuName, tuuTrungBonus) : "";

        // Experience gained: base + bonus from stones + bonus from wine gu
        int stoneExpRate = 15;
        if (character.getRank() == CultivationRank.NHAT_CHUYEN) {
            stoneExpRate = 30; // Nhất Chuyển: Nguyên thạch cực kỳ hiệu quả (+30 exp / đá)
        } else if (character.getRank() == CultivationRank.NHI_CHUYEN) {
            stoneExpRate = 22; // Nhị Chuyển: +22 exp / đá
        }
        int expGained = 25 + (stonesToUse * stoneExpRate) + tuuTrungBonus;
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
                    character.setTalentPoints(character.getTalentPoints() + 1);
                    message = String.format("🎉 ĐỘT PHÁ THÀNH CÔNG! Vách không khiếu nứt toác, Chân nguyên chuyển hóa thành %s! Cảnh giới đạt %s Sơ Giai (+1 Điểm Thiên Phú)!%s",
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

    @Transactional
    public GameStateDto unlockTalent(Long characterId, String talentCode) {
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân vật không tồn tại"));
        GameStateEntity state = gameStateRepository.findByCharacterId(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy trạng thái game"));

        if (character.hasTalent(talentCode)) {
            throw new IllegalStateException("Bạn đã lĩnh ngộ Thiên Phú này rồi!");
        }

        String talentName = switch (talentCode) {
            case "LUYEN_DAN_SU" -> "Luyện Đan Sư / Luyện Cổ Cuồng Nhân (+15% tỷ lệ luyện cổ, giảm 20% tổn thất)";
            case "BAN_TAY_VANG" -> "Bàn Tay Vàng (Tăng gấp đôi 4% tỷ lệ Cổ Trùng Biến Dị)";
            case "NGHICH_CANH_PHA_LO" -> "Nghịch Cảnh Phá Lô (Hoàn lại 50% Nguyên Thạch khi luyện cổ thất bại)";
            case "LUC_DAO_CHUYEN_GIA" -> "Lực Đạo Chuyên Gia (-30% Chân Nguyên Lực Đạo, +25% sát thương cận chiến)";
            case "NGUYEN_TO_LINH_THE" -> "Viêm/Thủy Đạo Linh Thể (+30% uy lực Nguyên Tố, giảm 50% sát thương nhận vào)";
            case "DA_NGUYEN_TO" -> "Đa Nguyên Tố (Giảm 50% hình phạt xung khắc lưu phái khi luyện cổ & chiến đấu)";
            case "THUONG_GIA_GIAO_HOP" -> "Thương Gia Giảo Hoạt (-15% giá mua, +15% giá bán tại Cổ Thị)";
            case "DAU_CO_TRUC_LOI" -> "Đầu Cơ Trục Lợi (+10% biên độ lợi nhuận khi có Sự Kiện Biến Động Giá)";
            case "KEO_KIET_CUC_HAN" -> "Keo Kiệt Cực Hạn (Giảm 25% lượng thức ăn tiêu hao định kỳ của toàn bộ Cổ trùng)";
            default -> throw new IllegalArgumentException("Thiên phú không hợp lệ: " + talentCode);
        };

        String costText;
        if (character.getTalentPoints() > 0) {
            character.setTalentPoints(character.getTalentPoints() - 1);
            costText = "1 Điểm Thiên Phú";
        } else if (character.getContributionPoints() >= 40) {
            character.setContributionPoints(character.getContributionPoints() - 40);
            costText = "40 Điểm Cống Hiến";
        } else if (character.getPrimevalStones() >= 60) {
            character.setPrimevalStones(character.getPrimevalStones() - 60);
            costText = "60 Nguyên Thạch";
        } else {
            throw new IllegalStateException("Cần 1 Điểm Thiên Phú (hoặc 40 Điểm Cống Hiến / 60 Nguyên Thạch) để khai mở Thiên Phú mới!");
        }

        String current = character.getUnlockedTalents() == null ? "" : character.getUnlockedTalents().trim();
        character.setUnlockedTalents(current.isEmpty() ? talentCode : current + "," + talentCode);
        characterRepository.save(character);

        String message = String.format("🌟 LĨNH NGỘ THIÊN PHÚ! Đã tiêu hao %s khai mở [%s]!", costText, talentName);
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

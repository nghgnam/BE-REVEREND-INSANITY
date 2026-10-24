package com.cochannhan.game.service;

import com.cochannhan.game.dto.GambleRockResponse;
import com.cochannhan.game.dto.GameStateDto;
import com.cochannhan.game.dto.MarketItemDto;
import com.cochannhan.game.model.*;
import com.cochannhan.game.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarketService {

    private final CharacterRepository characterRepository;
    private final CharacterGuRepository characterGuRepository;
    private final GuDefinitionRepository guDefinitionRepository;
    private final GameStateRepository gameStateRepository;
    private final Random random = new Random();

    // Map of Clan Vault Gu Code -> Contribution points required
    private static final Map<String, Integer> CLAN_VAULT_PRICES = Map.ofEntries(
            Map.entry("NGUYET_QUANG_CO", 30),
            Map.entry("BACH_THI_CO", 35),
            Map.entry("SINH_CO_DIEP", 30),
            Map.entry("THANH_PHONG_LUAN_CO", 40),
            Map.entry("BACH_NGOC_CO", 45),
            Map.entry("TU_TRUNG", 90),
            Map.entry("NGUYET_MANG_CO", 120),
            Map.entry("THIEN_BONG_CO", 140),
            Map.entry("BACH_NHAN_LUC_CO", 180),
            Map.entry("KIM_CHUNG_TRAO_CO", 220),
            Map.entry("XA_LOI_CO", 250)
    );

    // Map of Merchant Shop Gu Code -> Primeval stones price
    private static final Map<String, Integer> SHOP_PRICES = Map.ofEntries(
            Map.entry("HAC_THI_CO", 30),
            Map.entry("MANH_HO_CO", 35),
            Map.entry("THANH_TRU_CO", 30),
            Map.entry("BIEN_THACH_CO", 28),
            Map.entry("HOA_CAU_CO", 32),
            Map.entry("THUY_TIEN_CO", 30),
            Map.entry("THIEM_DIEN_CO", 35),
            Map.entry("GIAI_ME_CO", 25),
            Map.entry("TROI_BUOC_CO", 28),
            Map.entry("BACH_COT_CO", 30),
            Map.entry("DIA_THINH_NHUC_NHI_THAO", 35),
            Map.entry("DANG_KHONG_CO", 85),
            Map.entry("THIET_BI_CO", 80),
            Map.entry("TU_TI_CO", 95),
            Map.entry("LANG_BIEN_CO", 85),
            Map.entry("LIET_HOA_CO", 90),
            Map.entry("BANG_TRU_CO", 85),
            Map.entry("TU_LOI_CO", 95),
            Map.entry("TAT_PHONG_CO", 80),
            Map.entry("TOAN_CHUYEN_COT_THUONG_CO", 90),
            Map.entry("HOI_XUAN_CO", 85),
            Map.entry("THIEN_BONG_CO", 110),
            Map.entry("CUONG_THIET_THUAN_CO", 80),
            Map.entry("SUC_COT_CO", 90),
            Map.entry("HUNG_LUC_CO", 210),
            Map.entry("BAO_VIEM_CO", 220),
            Map.entry("HAN_BANG_CO", 200),
            Map.entry("LOI_TUONG_CO", 230),
            Map.entry("DAI_PHONG_BAO_CO", 200),
            Map.entry("TINH_HOA_CO", 210)
    );

    @Transactional
    public GambleRockResponse gambleRock(Long characterId) {
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân vật không tồn tại"));

        int cost = 20; // 20 Nguyên Thạch mua đá hóa thạch
        if (character.getPrimevalStones() < cost) {
            throw new IllegalStateException("Không đủ 20 Nguyên Thạch để tham gia sòng Đổ Thạch!");
        }

        character.setPrimevalStones(character.getPrimevalStones() - cost);

        int roll = random.nextInt(100);
        GambleRockResponse response;

        if (roll < 28) {
            // Jackpot: Dormant Fossil Gu!
            // 75% Rank 1, 20% Rank 2, 5% Rank 3
            int tierRoll = random.nextInt(100);
            List<String> fossilGus;
            if (tierRoll < 75) {
                // Rank 1 Pool
                fossilGus = List.of(
                        "HAC_THI_CO", "BACH_THI_CO", "THANH_TRU_CO", "MANH_HO_CO",
                        "BIEN_THACH_CO", "HOA_CAU_CO", "THUY_TIEN_CO", "THIEM_DIEN_CO",
                        "THANH_PHONG_LUAN_CO", "GIAI_ME_CO", "TROI_BUOC_CO", "BACH_COT_CO",
                        "DIA_THINH_NHUC_NHI_THAO", "SINH_CO_DIEP", "BACH_NGOC_CO", "TU_TRUNG"
                );
            } else if (tierRoll < 95) {
                // Rank 2 Pool (Hiếm)
                fossilGus = List.of(
                        "DANG_KHONG_CO", "THIET_BI_CO", "TU_TI_CO", "LANG_BIEN_CO",
                        "XA_BIEN_CO", "LIET_HOA_CO", "BANG_TRU_CO", "TU_LOI_CO",
                        "TAT_PHONG_CO", "LINH_QUANG_CO", "TOC_DO_CO", "KHONG_THU_CO",
                        "HUYET_DICH_TU_CO", "TU_HUYET_CO", "TOAN_CHUYEN_COT_THUONG_CO",
                        "THAU_KHI_CO", "TAM_TUNG_CO", "HOI_XUAN_CO", "THIEN_BONG_CO",
                        "CUONG_THIET_THUAN_CO", "LAI_THO_CAP_MO", "SUC_COT_CO"
                );
            } else {
                // Rank 3 Pool (Siêu Hiếm)
                fossilGus = List.of(
                        "BACH_NHAN_LUC_CO", "THIET_CAN_CO", "HUNG_LUC_CO", "HUNG_BIEN_CO",
                        "UNG_DYC_CO", "BAO_VIEM_CO", "HAN_BANG_CO", "LOI_TUONG_CO",
                        "DAI_PHONG_BAO_CO", "HOI_THO_TRI_TUE", "QUANG_AM_LUU_THUY_CO",
                        "DI_HE_CO", "TRIEU_HOI_CO", "HUYET_LO_CO", "HUYET_TRI_CO",
                        "THAU_TAM_CO", "THIEN_NHAN_CO", "TINH_HOA_CO", "KIM_CHUNG_TRAO_CO", "XA_LOI_CO"
                );
            }

            String guCode = fossilGus.get(random.nextInt(fossilGus.size()));
            GuDefinition guDef = guDefinitionRepository.findByCode(guCode)
                    .orElseGet(() -> guDefinitionRepository.findByCode("LAI_THO_CAP_MO").orElseThrow());

            boolean isMutated = random.nextInt(100) < 5; // 5% cơ hội đột biến
            int mutationBonus = isMutated ? 30 : 0;

            CharacterGu newGu = CharacterGu.builder()
                    .characterId(characterId)
                    .guDefinition(guDef)
                    .currentHungerHours(guDef.getMaxHungerHours())
                    .isEquipped(false)
                    .isHibernating(false)
                    .isStarving(false)
                    .isMutated(isMutated)
                    .mutationBonus(mutationBonus)
                    .build();
            newGu = characterGuRepository.save(newGu);

            int sellValue = calculateSellPrice(guDef.getRank(), isMutated);

            String title = isMutated ? "[DỊ BIẾN] " + guDef.getName() : guDef.getName();
            String msg = String.format("🎉 ĐẠI PHÁT! Vỏ đá hóa thạch nứt ra hào quang rực rỡ, phát hiện một con [%s] (%s - Chuyển %d) đang ngủ đông! Giá thu mua: %d Nguyên Thạch.",
                    title, guDef.getPathName() != null ? guDef.getPathName() : (guDef.getType() != null ? guDef.getType().getLabel() : "Cổ Trùng"), guDef.getRank(), sellValue);

            response = GambleRockResponse.builder()
                    .isSuccess(true)
                    .outcomeType("RARE_GU")
                    .rewardName(title)
                    .message(msg)
                    .remainingStones(character.getPrimevalStones())
                    .characterGuId(newGu.getId())
                    .guCode(guDef.getCode())
                    .sellValue(sellValue)
                    .isMutated(isMutated)
                    .build();
        } else if (roll < 63) {
            // Win extra stones
            int prizeStones = 25 + random.nextInt(40); // 25 to 64 stones
            character.setPrimevalStones(character.getPrimevalStones() + prizeStones);

            response = GambleRockResponse.builder()
                    .isSuccess(true)
                    .outcomeType("STONES")
                    .rewardName(prizeStones + " Nguyên Thạch")
                    .message(String.format("💰 Thắng lớn! Cắt trúng tâm thạch kết tinh ngàn năm, thu về %d viên Nguyên Thạch sáng lấp lánh!", prizeStones))
                    .remainingStones(character.getPrimevalStones())
                    .build();
        } else {
            // Rubble
            response = GambleRockResponse.builder()
                    .isSuccess(false)
                    .outcomeType("RUBBLE")
                    .rewardName("Cục đá vụn xám")
                    .message("💀 Tiếc quá! Đá hóa thạch rỗng ruột phong hóa, chỉ toàn là đất cát phàm tục vô giá trị.")
                    .remainingStones(character.getPrimevalStones())
                    .build();
        }

        characterRepository.save(character);

        GameStateEntity state = gameStateRepository.findByCharacterId(characterId).orElse(null);
        if (state != null) {
            state.setLastEventLog(response.getMessage());
            gameStateRepository.save(state);
        }

        return response;
    }

    @Transactional
    public GameStateDto sellGu(Long characterId, Long characterGuId) {
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân vật không tồn tại"));

        CharacterGu gu = characterGuRepository.findById(characterGuId)
                .orElseThrow(() -> new IllegalArgumentException("Cổ trùng không tồn tại hoặc đã được bán trước đó"));

        if (!gu.getCharacterId().equals(characterId)) {
            throw new IllegalArgumentException("Cổ trùng này không thuộc về bạn");
        }

        if ("XUAN_THU_THIEN".equals(gu.getGuDefinition().getCode())) {
            throw new IllegalStateException("Xuân Thu Thiền là bảo vật linh hồn gắn liền Không Khiếu, tuyệt đối không thể bán!");
        }

        int sellPrice = calculateSellPrice(gu.getGuDefinition().getRank(), gu.isMutated());

        characterGuRepository.delete(gu);
        character.setPrimevalStones(character.getPrimevalStones() + sellPrice);
        characterRepository.save(character);

        String title = gu.isMutated() ? "[DỊ BIẾN] " + gu.getGuDefinition().getName() : gu.getGuDefinition().getName();
        String msg = String.format("💰 Đã bán %s cho Cổ Thị Thương Đoàn, thu về %d Nguyên Thạch!", title, sellPrice);

        return getUpdatedGameState(characterId, msg);
    }

    @Transactional
    public GameStateDto buyGu(Long characterId, String guCode) {
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân vật không tồn tại"));

        GuDefinition guDef = guDefinitionRepository.findByCode(guCode)
                .orElseThrow(() -> new IllegalArgumentException("Cổ trùng không có trong danh mục Cổ Thị: " + guCode));

        int price = SHOP_PRICES.getOrDefault(guCode, getFallbackPrice(guDef.getRank()));

        if (character.getPrimevalStones() < price) {
            throw new IllegalStateException(String.format("Không đủ Nguyên Thạch để mua %s! (Cần: %d viên, bạn có: %d viên)",
                    guDef.getName(), price, character.getPrimevalStones()));
        }

        character.setPrimevalStones(character.getPrimevalStones() - price);
        characterRepository.save(character);

        CharacterGu newGu = CharacterGu.builder()
                .characterId(characterId)
                .guDefinition(guDef)
                .currentHungerHours(guDef.getMaxHungerHours())
                .isEquipped(false)
                .isHibernating(false)
                .isStarving(false)
                .isMutated(false)
                .mutationBonus(0)
                .build();
        characterGuRepository.save(newGu);

        String msg = String.format("🏮 Giao dịch thành công! Đã mua [%s] (%s - Chuyển %d) từ Thương Đoàn với giá %d Nguyên Thạch.",
                guDef.getName(), guDef.getPathName() != null ? guDef.getPathName() : (guDef.getType() != null ? guDef.getType().getLabel() : "Cổ Trùng"), guDef.getRank(), price);

        return getUpdatedGameState(characterId, msg);
    }

    @Transactional
    public GameStateDto exchangeClanGu(Long characterId, String guCode) {
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân vật không tồn tại"));

        GuDefinition guDef = guDefinitionRepository.findByCode(guCode)
                .orElseThrow(() -> new IllegalArgumentException("Cổ trùng không có trong Tàng Cổ Các gia tộc: " + guCode));

        int pointsRequired = CLAN_VAULT_PRICES.getOrDefault(guCode, 50);

        if (character.getContributionPoints() < pointsRequired) {
            throw new IllegalStateException(String.format("Không đủ Điểm Cống Hiến gia tộc! (Cần: %d điểm, bạn hiện có: %d điểm)",
                    pointsRequired, character.getContributionPoints()));
        }

        character.setContributionPoints(character.getContributionPoints() - pointsRequired);
        characterRepository.save(character);

        CharacterGu newGu = CharacterGu.builder()
                .characterId(characterId)
                .guDefinition(guDef)
                .currentHungerHours(guDef.getMaxHungerHours())
                .isEquipped(false)
                .isHibernating(false)
                .isStarving(false)
                .isMutated(false)
                .mutationBonus(0)
                .build();
        characterGuRepository.save(newGu);

        String msg = String.format("📜 Đã dùng %d Điểm Cống Hiến đổi lấy [%s] (%s) tại Tàng Cổ Các Gia Tộc Cổ Nguyệt!",
                pointsRequired, guDef.getName(), guDef.getPathName() != null ? guDef.getPathName() : (guDef.getType() != null ? guDef.getType().getLabel() : "Cổ Trùng"));

        return getUpdatedGameState(characterId, msg);
    }

    public List<MarketItemDto> getShopItems() {
        List<MarketItemDto> list = new ArrayList<>();
        SHOP_PRICES.forEach((code, price) -> {
            guDefinitionRepository.findByCode(code).ifPresent(def -> {
                list.add(MarketItemDto.builder()
                        .guCode(def.getCode())
                        .name(def.getName())
                        .rank(def.getRank())
                        .type(def.getType() != null ? def.getType().getLabel() : "Cổ Trùng")
                        .pathName(def.getPathName() != null ? def.getPathName() : "Cổ Đạo")
                        .icon(def.getIcon() != null ? def.getIcon() : "🐛")
                        .stoneCost(price != null ? price : 30)
                        .contributionCost(0)
                        .description(def.getDescription() != null ? def.getDescription() : "")
                        .build());
            });
        });
        list.sort(Comparator.comparingInt(MarketItemDto::getRank).thenComparing(MarketItemDto::getStoneCost));
        return list;
    }

    public List<MarketItemDto> getClanVaultItems() {
        List<MarketItemDto> list = new ArrayList<>();
        CLAN_VAULT_PRICES.forEach((code, cost) -> {
            guDefinitionRepository.findByCode(code).ifPresent(def -> {
                list.add(MarketItemDto.builder()
                        .guCode(def.getCode())
                        .name(def.getName())
                        .rank(def.getRank())
                        .type(def.getType() != null ? def.getType().getLabel() : "Gia Tộc")
                        .pathName(def.getPathName() != null ? def.getPathName() : "Gia Tộc")
                        .icon(def.getIcon() != null ? def.getIcon() : "🐛")
                        .stoneCost(0)
                        .contributionCost(cost != null ? cost : 30)
                        .description(def.getDescription() != null ? def.getDescription() : "")
                        .build());
            });
        });
        list.sort(Comparator.comparingInt(MarketItemDto::getContributionCost));
        return list;
    }

    private int calculateSellPrice(int rank, boolean isMutated) {
        int base;
        switch (rank) {
            case 1: base = 25; break;
            case 2: base = 60; break;
            case 3: base = 150; break;
            case 4: base = 300; break;
            case 5: base = 600; break;
            case 6: base = 1800; break;
            default: base = 20; break;
        }
        return isMutated ? (int)(base * 1.5) : base;
    }

    private int getFallbackPrice(int rank) {
        switch (rank) {
            case 1: return 35;
            case 2: return 85;
            case 3: return 200;
            case 4: return 450;
            default: return 50;
        }
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
}

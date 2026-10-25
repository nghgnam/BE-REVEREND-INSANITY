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

    // Map of Clan Vault Gu Code -> Contribution points required (Nội Khố Tàng Cổ Các Đa Dạng Từ 1 -> 6 Chuyển)
    private static final Map<String, Integer> CLAN_VAULT_PRICES = Map.ofEntries(
            // Chuyển 1
            Map.entry("NGUYET_QUANG_CO", 30),
            Map.entry("BACH_THI_CO", 35),
            Map.entry("SINH_CO_DIEP", 30),
            Map.entry("THANH_PHONG_LUAN_CO", 40),
            Map.entry("BACH_NGOC_CO", 45),
            Map.entry("THANH_DONG_XA_LOI_CO", 65),
            Map.entry("TU_TRUNG", 80),
            Map.entry("THANH_LIEN_KIEM_KHI_CO", 45),
            Map.entry("NHAP_MONG_HO_DIEP_CO", 45),
            Map.entry("TINH_QUANG_TIEN_CO", 45),
            // Chuyển 2
            Map.entry("HAC_BACH_THI_CO", 80),
            Map.entry("HO_LUC_CO", 90),
            Map.entry("NGUYET_MANG_CO", 110),
            Map.entry("THIEN_BONG_CO", 125),
            Map.entry("XICH_THIET_XA_LOI_CO", 140),
            Map.entry("NGUYET_ANH_BO_CO", 105),
            Map.entry("THAT_TINH_HO_MENH_CO", 110),
            Map.entry("KINH_HOA_THUY_NGUYET_CO", 115),
            Map.entry("HUYET_ANH_MA_DAO_CO", 115),
            Map.entry("CAM_LY_HONG_VAN_CO", 120),
            // Chuyển 3
            Map.entry("TU_VI_TUU_TRUNG", 165),
            Map.entry("TUY_TIEN_NGUYET_CO", 170),
            Map.entry("TU_TINH_CO", 175),
            Map.entry("BACH_NHAN_LUC_CO", 180),
            Map.entry("TUONG_LUC_CO", 190),
            Map.entry("KIM_CUONG_BAT_HOAI_CO", 195),
            Map.entry("KIM_CHUNG_TRAO_CO", 210),
            Map.entry("BACH_NGAN_XA_LOI_CO", 230),
            Map.entry("THO_CO", 260),
            Map.entry("LOI_MINH_KIEM_DUC_CO", 185),
            Map.entry("THIEN_MA_HUYEN_CANH_CO", 190),
            Map.entry("HUYET_NGUYET_CUONG_HOA_CO", 190),
            Map.entry("TU_KHI_DONG_LAI_CO", 195),
            Map.entry("CUU_VY_THIEN_HO_BIEN_CO", 190),
            // Chuyển 4 (Cao Giai Trưởng Lão Bảo Khố)
            Map.entry("THAT_HUONG_TUU_TRUNG", 320),
            Map.entry("THIEN_NHAN_LUC_CO", 340),
            Map.entry("LONG_LAN_CO", 360),
            Map.entry("HOANG_KIM_XA_LOI_CO", 390),
            Map.entry("TINH_HA_KIEM_TRAN_CO", 350),
            Map.entry("VINH_DA_AC_MONG_CO", 350),
            Map.entry("BAC_DAU_TINH_TRAN_CO", 355),
            Map.entry("NGUYET_THUC_THON_PHE_CO", 360),
            Map.entry("PHUONG_HOANG_CHAN_HOA_CO", 365),
            Map.entry("KIM_SI_DAI_BANG_BIEN_CO", 360),
            Map.entry("HUYEN_VU_BAT_DIET_BIEN_CO", 370),
            Map.entry("THIEN_MENH_CHI_TU_CO", 380),
            Map.entry("DAO_THIEN_THAN_THU_CO", 375),
            Map.entry("THOI_GIAN_NGUNG_DONG_CO", 385),
            Map.entry("LONG_HUYET_DOAN_THE_CO", 400),
            // Chuyển 5 (Chí Bảo Trấn Tộc Đỉnh Cấp)
            Map.entry("CUU_CHUYEN_TIEN_TUU_TRUNG", 580),
            Map.entry("VAN_NHAN_LUC_CO", 600),
            Map.entry("BAT_SON_CO", 620),
            Map.entry("TU_TINH_XA_LOI_CO", 680),
            Map.entry("VAN_KIEM_QUY_TONG_CO", 640),
            Map.entry("TRAM_THIEN_BAT_KIEM_CO", 650),
            Map.entry("DAI_MONG_THIEN_THU_CO", 640),
            Map.entry("TU_VI_DE_TINH_CO", 650),
            Map.entry("THAI_AM_THAN_NGUYET_CO", 650),
            Map.entry("TU_LA_CHIEN_THE_CO", 660),
            Map.entry("TU_TIEU_THAN_LOI_CO", 660),
            Map.entry("THANH_LONG_THIEN_UY_BIEN_CO", 670),
            Map.entry("NGHICH_THIEN_CAI_MENH_CO", 700),
            Map.entry("VAN_PHAP_BAT_XAM_CO", 680),
            Map.entry("THOI_KHONG_DAO_LUU_CO", 690),
            Map.entry("PHUONG_HOANG_NIET_BAN_CO", 720),
            Map.entry("CUU_CHUYEN_KIM_DAN_CO", 750),
            // Chuyển 6 (Tiên Cổ Mật Khố)
            Map.entry("BACH_QUA_TIEN_TUU_CO", 1500),
            Map.entry("DOC_CO_KIEM_TIEN_CO", 1600),
            Map.entry("MONG_DIEP_TIEN_CO", 1600),
            Map.entry("BAN_CO_KHAI_THIEN_CO", 1700),
            Map.entry("THIEN_DIA_PHAP_TAC_CO", 1750),
            Map.entry("HONG_MONG_TU_KHI_CO", 1800)
    );

    // Map of Merchant Shop Gu Code -> Primeval stones price (Thương Đội Đa Dạng Từ 1 -> 5 Chuyển)
    private static final Map<String, Integer> SHOP_PRICES = Map.ofEntries(
            // Chuyển 1
            Map.entry("HAC_THI_CO", 30),
            Map.entry("BACH_THI_CO", 32),
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
            Map.entry("THANH_LIEN_KIEM_KHI_CO", 38),
            Map.entry("NHAP_MONG_HO_DIEP_CO", 36),
            Map.entry("TINH_QUANG_TIEN_CO", 36),
            Map.entry("U_HON_QUY_TRAO_CO", 38),
            Map.entry("THANH_DONG_XA_LOI_CO", 60),
            // Chuyển 2
            Map.entry("HAC_BACH_THI_CO", 75),
            Map.entry("HO_LUC_CO", 90),
            Map.entry("HUYET_KHI_BAO_PHAT_CO", 85),
            Map.entry("THAN_HANH_PHU_CO", 90),
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
            Map.entry("HUYET_ANH_MA_DAO_CO", 95),
            Map.entry("TUYET_MENH_PHI_DAO_CO", 95),
            Map.entry("KINH_HOA_THUY_NGUYET_CO", 95),
            Map.entry("MONG_THUC_LINH_HON_CO", 95),
            Map.entry("XICH_THIET_XA_LOI_CO", 130),
            // Chuyển 3
            Map.entry("TU_VI_TUU_TRUNG", 185),
            Map.entry("TU_TINH_CO", 190),
            Map.entry("KIM_CUONG_BAT_HOAI_CO", 210),
            Map.entry("HUNG_LUC_CO", 210),
            Map.entry("TUONG_LUC_CO", 220),
            Map.entry("BAO_VIEM_CO", 220),
            Map.entry("HAN_BANG_CO", 200),
            Map.entry("LOI_TUONG_CO", 230),
            Map.entry("DAI_PHONG_BAO_CO", 200),
            Map.entry("TINH_HOA_CO", 210),
            Map.entry("LOI_MINH_KIEM_DUC_CO", 220),
            Map.entry("PHA_KHONG_DAO_Y_CO", 225),
            Map.entry("THIEN_MA_HUYEN_CANH_CO", 225),
            Map.entry("TINH_THAN_DAN_THACH_CO", 230),
            Map.entry("PHE_HON_MA_LIEN_CO", 230),
            Map.entry("CU_LINH_THAN_CHUY_CO", 235),
            Map.entry("BACH_NGAN_XA_LOI_CO", 250),
            // Chuyển 4
            Map.entry("THAT_HUONG_TUU_TRUNG", 390),
            Map.entry("LONG_LAN_CO", 420),
            Map.entry("TINH_HA_KIEM_TRAN_CO", 430),
            Map.entry("VINH_DA_AC_MONG_CO", 430),
            Map.entry("BAC_DAU_TINH_TRAN_CO", 440),
            Map.entry("BACH_QUY_DA_HANH_CO", 440),
            Map.entry("PHUONG_HOANG_CHAN_HOA_CO", 450),
            Map.entry("LONG_HUYET_DOAN_THE_CO", 460),
            Map.entry("BA_VUONG_DINH_LUC_CO", 450),
            Map.entry("BO_DE_NGO_DAO_CO", 460),
            Map.entry("HOANG_KIM_XA_LOI_CO", 480),
            // Chuyển 5
            Map.entry("CUU_CHUYEN_TIEN_TUU_TRUNG", 780),
            Map.entry("VAN_KIEM_QUY_TONG_CO", 820),
            Map.entry("DAI_MONG_THIEN_THU_CO", 820),
            Map.entry("TU_VI_DE_TINH_CO", 840),
            Map.entry("TU_LA_CHIEN_THE_CO", 850),
            Map.entry("PHUONG_HOANG_NIET_BAN_CO", 880),
            Map.entry("THAI_CO_THAN_TUY_CO", 900),
            Map.entry("TU_TINH_XA_LOI_CO", 920)
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
        double bonusMultiplier = 1.0;
        StringBuilder talentNote = new StringBuilder();
        if (character.hasTalent("THUONG_GIA_GIAO_HOP")) {
            bonusMultiplier += 0.15;
            talentNote.append(" [Thương Gia Giảo Hoạt +15%]");
        }
        GameStateEntity state = gameStateRepository.findByCharacterId(characterId).orElse(null);
        if (character.hasTalent("DAU_CO_TRUC_LOI") && state != null && state.getMarketEvent() != null && !state.getMarketEvent().isEmpty()) {
            bonusMultiplier += 0.10;
            talentNote.append(" [Đầu Cơ Trục Lợi +10%]");
        }
        sellPrice = (int) Math.round(sellPrice * bonusMultiplier);

        characterGuRepository.delete(gu);
        character.setPrimevalStones(character.getPrimevalStones() + sellPrice);
        characterRepository.save(character);

        String title = gu.isMutated() ? "[DỊ BIẾN] " + gu.getGuDefinition().getName() : gu.getGuDefinition().getName();
        String msg = String.format("💰 Đã bán %s cho Cổ Thị Thương Đoàn, thu về %d Nguyên Thạch!%s", title, sellPrice, talentNote);

        return getUpdatedGameState(characterId, msg);
    }

    @Transactional
    public GameStateDto buyGu(Long characterId, String guCode) {
        CharacterEntity character = characterRepository.findById(characterId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân vật không tồn tại"));

        GuDefinition guDef = guDefinitionRepository.findByCode(guCode)
                .orElseThrow(() -> new IllegalArgumentException("Cổ trùng không có trong danh mục Cổ Thị: " + guCode));

        int basePrice = SHOP_PRICES.getOrDefault(guCode, getFallbackPrice(guDef.getRank()));
        double discountMultiplier = 1.0;
        StringBuilder talentNote = new StringBuilder();
        if (character.hasTalent("THUONG_GIA_GIAO_HOP")) {
            discountMultiplier -= 0.15;
            talentNote.append(" [Thương Gia Giảo Hoạt -15%]");
        }
        GameStateEntity state = gameStateRepository.findByCharacterId(characterId).orElse(null);
        if (character.hasTalent("DAU_CO_TRUC_LOI") && state != null && state.getMarketEvent() != null && !state.getMarketEvent().isEmpty()) {
            discountMultiplier -= 0.10;
            talentNote.append(" [Đầu Cơ Trục Lợi -10%]");
        }
        int price = Math.max(5, (int) Math.round(basePrice * discountMultiplier));

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

        String msg = String.format("🏮 Giao dịch thành công! Đã mua [%s] (%s - Chuyển %d) từ Thương Đoàn với giá %d Nguyên Thạch.%s",
                guDef.getName(), guDef.getPathName() != null ? guDef.getPathName() : (guDef.getType() != null ? guDef.getType().getLabel() : "Cổ Trùng"), guDef.getRank(), price, talentNote);

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

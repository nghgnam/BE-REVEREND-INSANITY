package com.cochannhan.game.config;

import com.cochannhan.game.model.*;
import com.cochannhan.game.repository.GuDefinitionRepository;
import com.cochannhan.game.repository.GuRecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final GuDefinitionRepository guDefinitionRepository;
    private final GuRecipeRepository guRecipeRepository;

    @Override
    public void run(String... args) {
        log.info("Bắt đầu nạp / đồng bộ danh mục 15 Đại Lưu Phái Cổ Trùng thế giới Cổ Chân Nhân...");

        List<GuDefinition> allGus = buildAllGuDefinitions();
        for (GuDefinition def : allGus) {
            guDefinitionRepository.findByCode(def.getCode()).ifPresentOrElse(existing -> {
                existing.setName(def.getName());
                existing.setRank(def.getRank());
                existing.setType(def.getType());
                existing.setOptimalDistance(def.getOptimalDistance());
                existing.setPrimevalCostPercent(def.getPrimevalCostPercent());
                existing.setApCost(def.getApCost());
                existing.setBaseDamage(def.getBaseDamage());
                existing.setBaseDefense(def.getBaseDefense());
                existing.setBaseHeal(def.getBaseHeal());
                existing.setConsumable(def.isConsumable());
                existing.setBonusHp(def.getBonusHp());
                existing.setBonusAtk(def.getBonusAtk());
                existing.setBonusDef(def.getBonusDef());
                existing.setFoodType(def.getFoodType());
                existing.setMaxHungerHours(def.getMaxHungerHours());
                existing.setDescription(def.getDescription());
                existing.setIcon(def.getIcon());
                existing.setPathName(def.getPathName());
                existing.setSlotCategory(def.getSlotCategory());
                guDefinitionRepository.save(existing);
            }, () -> {
                guDefinitionRepository.save(def);
            });
        }
        log.info("Đã đồng bộ {} Cổ trùng thuộc 15 Đại Lưu Phái vào cơ sở dữ liệu.", allGus.size());

        syncRecipes();
    }

    private List<GuDefinition> buildAllGuDefinitions() {
        List<GuDefinition> list = new ArrayList<>();

        // ---------------------------------------------------------
        // NGUYỆT ĐẠO (BẢN MỆNH CỔ NGUYỆT GIA TỘC)
        // ---------------------------------------------------------
        list.add(GuDefinition.builder()
                .code("NGUYET_QUANG_CO").name("Nguyệt Quang Cổ").rank(1).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(10).apCost(1).baseDamage(40).baseDefense(0).baseHeal(0)
                .foodType("Nguyệt Lan Hoa").maxHungerHours(24).icon("🌙").pathName("Nguyệt Đạo").slotCategory("CONG_KICH")
                .description("Bắn ra lưỡi đao ánh trăng màu u lam sắc bén, tuyệt kỹ trấn sơn của Cổ Nguyệt gia tộc.").build());

        list.add(GuDefinition.builder()
                .code("NGUYET_MANG_CO").name("Nguyệt Mang Cổ").rank(2).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(18).apCost(1).baseDamage(90).baseDefense(0).baseHeal(0)
                .foodType("Nguyệt Lan Hoa").maxHungerHours(24).icon("✨").pathName("Nguyệt Đạo").slotCategory("CONG_KICH")
                .description("Hợp luyện từ Nguyệt Quang Cổ, trăng lưỡi liềm phát sáng rực rỡ xuyên thủng phòng ngự.").build());

        // ---------------------------------------------------------
        // 1.1 LỰC ĐẠO (Strength Path)
        // ---------------------------------------------------------
        list.add(GuDefinition.builder()
                .code("HAC_THI_CO").name("Hắc Thỉ Cổ").rank(1).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(8).apCost(1).baseDamage(42).baseDefense(5).baseHeal(0)
                .bonusAtk(15).bonusDef(5).bonusHp(0)
                .foodType("Thịt Lợn Rừng Đen").maxHungerHours(24).icon("🐗").pathName("Lực Đạo").slotCategory("CONG_KICH")
                .description("Gia tăng một trư chi lực của lợn rừng đen, trang bị tăng +15 Công kích và +5 Phòng ngự.").build());

        list.add(GuDefinition.builder()
                .code("BACH_THI_CO").name("Bạch Thỉ Cổ").rank(1).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(0).baseDefense(20).baseHeal(0)
                .bonusAtk(10).bonusDef(20).bonusHp(30)
                .foodType("Gạo Nếp Trắng").maxHungerHours(48).icon("🐖").pathName("Lực Đạo").slotCategory("PHU_TRO")
                .description("Vĩnh viễn cải tạo gân cốt Cổ sư, trang bị tăng +10 Công kích, +20 Phòng ngự và +30 Khí Huyết.").build());

        list.add(GuDefinition.builder()
                .code("THANH_TRU_CO").name("Thanh Trư Cổ").rank(1).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(10).apCost(1).baseDamage(15).baseDefense(45).baseHeal(0)
                .bonusDef(30).bonusHp(40)
                .foodType("Khoai Lang Xanh").maxHungerHours(36).icon("🦬").pathName("Lực Đạo").slotCategory("PHONG_NGU")
                .description("Lực đạo kết hợp hộ giáp lợn xanh, da dày thịt béo trang bị tăng +30 Phòng ngự và +40 Khí Huyết.").build());

        list.add(GuDefinition.builder()
                .code("MANH_HO_CO").name("Mãnh Hổ Cổ").rank(1).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(12).apCost(1).baseDamage(55).baseDefense(0).baseHeal(0)
                .bonusAtk(25)
                .foodType("Thịt Tươi Hổ").maxHungerHours(24).icon("🐯").pathName("Lực Đạo").slotCategory("CONG_KICH")
                .description("Ngưng tụ một hổ chi lực, mãnh hổ hạ sơn trang bị tăng bộc phát +25 Công kích.").build());

        // Nhị chuyển Lực Đạo
        list.add(GuDefinition.builder()
                .code("DANG_KHONG_CO").name("Đằng Không Cổ").rank(2).type(GuType.THAN_PHAP)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(15).apCost(1).baseDamage(35).baseDefense(15).baseHeal(0)
                .foodType("Khí Lưu Núi Cao").maxHungerHours(36).icon("🦅").pathName("Lực Đạo").slotCategory("THAN_PHAP")
                .description("Bật nhảy vọt lên không trung rồi giáng xuống cú dậm trời giáng đòn sấm sét.").build());

        list.add(GuDefinition.builder()
                .code("THIET_BI_CO").name("Thiết Bì Cổ").rank(2).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(18).apCost(1).baseDamage(0).baseDefense(85).baseHeal(0)
                .bonusDef(35)
                .foodType("Mạt Sắt Rỉ").maxHungerHours(48).icon("🛡️").pathName("Lực Đạo").slotCategory("PHONG_NGU")
                .description("Biến da thịt thành lớp da sắt đen đao thương bất nhập, trang bị tăng +35 Phòng ngự.").build());

        list.add(GuDefinition.builder()
                .code("TU_TI_CO").name("Bốn Tay Cổ (Tứ Tí Cổ)").rank(2).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(20).apCost(1).baseDamage(100).baseDefense(20).baseHeal(0)
                .bonusAtk(30)
                .foodType("Cốt Tủy Rừng Già").maxHungerHours(30).icon("🦾").pathName("Lực Đạo").slotCategory("CONG_KICH")
                .description("Mọc thêm 2 cánh tay lực đạo ảo ảnh, trang bị tăng +30 Công kích nghiền nát địch thủ.").build());

        list.add(GuDefinition.builder()
                .code("HUNG_LUC_CO").name("Hùng Lực Cổ").rank(2).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(22).apCost(1).baseDamage(110).baseDefense(25).baseHeal(0)
                .bonusAtk(30).bonusDef(20)
                .foodType("Mật Ong Rừng Già").maxHungerHours(36).icon("🐻").pathName("Lực Đạo").slotCategory("CONG_KICH")
                .description("Sức mạnh của gấu nâu cuồng bạo, trang bị tăng +30 Công kích và +20 Phòng ngự.").build());

        // Tam chuyển Lực Đạo
        list.add(GuDefinition.builder()
                .code("BACH_NHAN_LUC_CO").name("Bách Nhân Lực Cổ").rank(3).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(25).apCost(1).baseDamage(90).baseDefense(45).baseHeal(0)
                .bonusAtk(45)
                .foodType("Nhân Sâm Núi Tuyết").maxHungerHours(48).icon("👥").pathName("Lực Đạo").slotCategory("PHU_TRO")
                .description("Cường hóa thể phách đạt tới sức mạnh của 100 tráng niên hợp lại, trang bị tăng +45 Công kích.").build());

        list.add(GuDefinition.builder()
                .code("THIET_CAN_CO").name("Thiết Can Cổ").rank(3).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(22).apCost(1).baseDamage(30).baseDefense(130).baseHeal(0)
                .bonusDef(50)
                .foodType("Quặng Thép Ngàn Năm").maxHungerHours(60).icon("⛓️").pathName("Lực Đạo").slotCategory("PHONG_NGU")
                .description("Gân cốt cứng như thép tôi luyện, trang bị tăng +50 Phòng ngự giải trừ chấn thương.").build());

        list.add(GuDefinition.builder()
                .code("NGUU_LUC_CO").name("Ngưu Lực Cổ").rank(3).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(24).apCost(1).baseDamage(140).baseDefense(30).baseHeal(0)
                .bonusAtk(40).bonusHp(30)
                .foodType("Cỏ Linh Chi Núi").maxHungerHours(48).icon("🐂").pathName("Lực Đạo").slotCategory("CONG_KICH")
                .description("Khí phách trâu mộng húc đổ cổng thành, trang bị tăng +40 Công kích và +30 Khí Huyết.").build());

        list.add(GuDefinition.builder()
                .code("MA_LUC_CO").name("Mã Lực Cổ").rank(3).type(GuType.THAN_PHAP)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(20).apCost(1).baseDamage(85).baseDefense(20).baseHeal(0)
                .bonusAtk(30)
                .foodType("Đậu Nành Tinh Chế").maxHungerHours(36).icon("🐎").pathName("Lực Đạo").slotCategory("THAN_PHAP")
                .description("Sức phi ngàn dặm của tuấn mã, bứt tốc né đòn trang bị tăng +30 Công kích.").build());

        list.add(GuDefinition.builder()
                .code("CHU_TUOC_LUC_CO").name("Chu Tước Lực Cổ").rank(3).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(28).apCost(1).baseDamage(160).baseDefense(20).baseHeal(0)
                .bonusAtk(50)
                .foodType("Lông Vũ Chu Tước").maxHungerHours(48).icon("🦚").pathName("Lực Đạo").slotCategory("CONG_KICH")
                .description("Sức mạnh thần điểu phương Nam bốc lửa, đòn đánh kèm hỏa diễm trang bị tăng +50 Công kích.").build());

        // Tứ/Ngũ chuyển Lực Đạo
        list.add(GuDefinition.builder()
                .code("THIEN_NHAN_LUC_CO").name("Thiên Nhân Lực Cổ").rank(4).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(35).apCost(1).baseDamage(240).baseDefense(70).baseHeal(0)
                .bonusAtk(70)
                .foodType("Linh Dược Ngàn Năm").maxHungerHours(72).icon("💪").pathName("Lực Đạo").slotCategory("CONG_KICH")
                .description("Sở hữu sức mạnh của 1.000 người gộp lại, trang bị tăng +70 Công kích chấn động sơn hà.").build());

        list.add(GuDefinition.builder()
                .code("LUC_DAO_GIAC_LONG_CO").name("Lực Đạo Giác Long Cổ").rank(4).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(40).apCost(1).baseDamage(300).baseDefense(80).baseHeal(0)
                .bonusAtk(85)
                .foodType("Long Cốt Cổ Đại").maxHungerHours(80).icon("🐲").pathName("Lực Đạo").slotCategory("CONG_KICH")
                .description("Hóa hình long lực giác long dũng mãnh, trang bị tăng +85 Công kích nghiền nát phòng tuyến.").build());

        list.add(GuDefinition.builder()
                .code("TU_SAM_LUC_CO").name("Tứ Sâm Lực Cổ").rank(4).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(30).apCost(1).baseDamage(180).baseDefense(90).baseHeal(0)
                .bonusAtk(80).bonusDef(50)
                .foodType("Tứ Đại Linh Sâm").maxHungerHours(72).icon("🌿").pathName("Lực Đạo").slotCategory("PHU_TRO")
                .description("Ngưng tụ tinh hoa 4 loại nhân sâm thượng cổ, trang bị tăng +80 Công kích và +50 Phòng ngự.").build());

        list.add(GuDefinition.builder()
                .code("VAN_NHAN_LUC_CO").name("Vạn Nhân Lực Cổ").rank(5).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(45).apCost(1).baseDamage(380).baseDefense(110).baseHeal(0)
                .bonusAtk(120)
                .foodType("Huyết Tinh Vạn Người").maxHungerHours(96).icon("💥").pathName("Lực Đạo").slotCategory("CONG_KICH")
                .description("Đỉnh cao phàm trần! Sức mạnh của 10.000 người, trang bị tăng +120 Công kích san phẳng thành trì.").build());

        list.add(GuDefinition.builder()
                .code("BAT_SON_CO").name("Bạt Sơn Cổ").rank(5).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(50).apCost(1).baseDamage(460).baseDefense(120).baseHeal(0)
                .bonusAtk(150)
                .foodType("Thạch Tủy Ngũ Nhạc").maxHungerHours(96).icon("⛰️").pathName("Lực Đạo").slotCategory("CONG_KICH")
                .description("Lực bạt sơn hề khí cái thế! Nhấc bổng quả núi giáng xuống kẻ địch, trang bị tăng +150 Công kích.").build());

        list.add(GuDefinition.builder()
                .code("DAO_HAI_CO").name("Đảo Hải Cổ").rank(5).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(50).apCost(1).baseDamage(450).baseDefense(120).baseHeal(0)
                .bonusAtk(140)
                .foodType("Thủy Tinh Vạn Dặm").maxHungerHours(96).icon("🌊").pathName("Lực Đạo").slotCategory("CONG_KICH")
                .description("Lực lượng lật tung biển cả cuộn trào sóng thần, trang bị tăng +140 Công kích cuồng nộ.").build());

        // Tiên Cổ Lực Đạo
        list.add(GuDefinition.builder()
                .code("LUC_DAO_SON_HAC_CO").name("Lực Đạo Sơn Hạc Cổ").rank(6).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(0).apCost(1).baseDamage(680).baseDefense(220).baseHeal(0)
                .bonusAtk(220)
                .foodType("Núi Đá Tiên Giới").maxHungerHours(99999).icon("🏔️").pathName("Lực Đạo").slotCategory("CONG_KICH")
                .description("Lục Chuyển Tiên Cổ: Ngưng tụ sơn hạc khổng lồ ném bay ngọn núi, trang bị tăng +220 Công kích.").build());

        list.add(GuDefinition.builder()
                .code("CO_TU_LUC").name("Cổ Tự Lực").rank(6).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(280).baseDefense(280).baseHeal(0)
                .bonusAtk(250).bonusDef(150)
                .foodType("Ý Chí Bất Khuất").maxHungerHours(99999).icon("⚡").pathName("Lực Đạo").slotCategory("PHU_TRO")
                .description("Bảo vật độc nhất vô nhị: Khai thác sức mạnh vô tận tự thân, trang bị tăng +250 Công kích và +150 Phòng ngự!").build());

        list.add(GuDefinition.builder()
                .code("LUC_VO_LUONG_TIEN_CO").name("Lực Vô Lượng Tiên Cổ").rank(7).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(1250).baseDefense(420).baseHeal(0)
                .bonusAtk(400)
                .foodType("Đạo Ngân Lực Đạo").maxHungerHours(99999).icon("🌟").pathName("Lực Đạo").slotCategory("CONG_KICH")
                .description("Thất Chuyển Tiên Cổ: Lực lượng vô lượng vô biên, trang bị tăng +400 Công kích đấm vỡ hư không.").build());

        // ---------------------------------------------------------
        // 1.2 BIẾN HÓA ĐẠO (Transformation Path)
        // ---------------------------------------------------------
        list.add(GuDefinition.builder()
                .code("BIEN_THACH_CO").name("Biến Thạch Cổ").rank(1).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(10).apCost(1).baseDamage(10).baseDefense(40).baseHeal(0)
                .foodType("Khoáng Thạch Đen").maxHungerHours(36).icon("🪨").pathName("Biến Hóa Đạo").slotCategory("PHONG_NGU")
                .description("Hóa thân thành tảng đá cứng cáp ngụy trang và triệt tiêu 40 sát thương đòn đánh.").build());

        list.add(GuDefinition.builder()
                .code("XA_BIEN_CO").name("Xà Biến Cổ").rank(1).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(12).apCost(1).baseDamage(45).baseDefense(10).baseHeal(0)
                .foodType("Độc Trùng").maxHungerHours(30).icon("🐍").pathName("Biến Hóa Đạo").slotCategory("CONG_KICH")
                .description("Biến hóa thành độc xà uốn lượn, cắn trúng kẻ thù gây nhiễm độc chân nguyên.").build());

        list.add(GuDefinition.builder()
                .code("LANG_BIEN_CO").name("Lang Biến Cổ").rank(1).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(14).apCost(1).baseDamage(52).baseDefense(15).baseHeal(0)
                .foodType("Thịt Sói Tươi").maxHungerHours(30).icon("🐺").pathName("Biến Hóa Đạo").slotCategory("CONG_KICH")
                .description("Biến hóa móng vuốt và răng nanh sói hoang sắc bén cắn xé đối thủ.").build());

        list.add(GuDefinition.builder()
                .code("CHU_DYC_CO").name("Chu Dực Cổ").rank(2).type(GuType.THAN_PHAP)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(16).apCost(1).baseDamage(40).baseDefense(30).baseHeal(0)
                .foodType("Tơ Nhện Rừng").maxHungerHours(36).icon("🕸️").pathName("Biến Hóa Đạo").slotCategory("THAN_PHAP")
                .description("Mọc đôi cánh nhện lướt nhẹ trên không và kết mạng tơ nhện cản đường kẻ địch.").build());

        list.add(GuDefinition.builder()
                .code("UNG_DYC_CO").name("Ưng Dực Cổ").rank(2).type(GuType.THAN_PHAP)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(20).apCost(1).baseDamage(45).baseDefense(35).baseHeal(0)
                .foodType("Lông Vũ Thần Điểu").maxHungerHours(40).icon("🦅").pathName("Biến Hóa Đạo").slotCategory("THAN_PHAP")
                .description("Mọc đôi cánh đại bàng xòe rộng lướt gió, né đòn tầm xa và chuyển đổi cự ly chớp mắt.").build());

        list.add(GuDefinition.builder()
                .code("NGU_LAN_CO").name("Ngư Lân Cổ").rank(2).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(15).apCost(1).baseDamage(20).baseDefense(75).baseHeal(0)
                .bonusDef(20)
                .foodType("Rong Biển").maxHungerHours(36).icon("🐟").pathName("Biến Hóa Đạo").slotCategory("PHONG_NGU")
                .description("Toàn thân mọc vảy cá trơn nhẵn phản chấn đòn đánh và tăng +20 Phòng ngự.").build());

        list.add(GuDefinition.builder()
                .code("HUNG_BIEN_CO").name("Hùng Biến Cổ").rank(3).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(26).apCost(1).baseDamage(160).baseDefense(65).baseHeal(0)
                .bonusHp(50)
                .foodType("Cá Hồi Băng").maxHungerHours(48).icon("🐻").pathName("Biến Hóa Đạo").slotCategory("CONG_KICH")
                .description("Hóa thân thành gấu khổng lồ cuồng nộ, trang bị tăng +50 Khí Huyết và sức đập ngàn cân.").build());

        list.add(GuDefinition.builder()
                .code("GIAO_LONG_BIEN_CO").name("Giao Long Biến Cổ").rank(3).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(28).apCost(1).baseDamage(175).baseDefense(55).baseHeal(0)
                .foodType("Ngọc Trai Huyết Long").maxHungerHours(48).icon("🐉").pathName("Biến Hóa Đạo").slotCategory("CONG_KICH")
                .description("Hóa thành hung thú Giao Long lướt sóng, móng vuốt xé toạc mọi hộ thể cương khí.").build());

        list.add(GuDefinition.builder()
                .code("BO_CAP_BIEN_CO").name("Bò Cạp Biến Cổ").rank(3).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(25).apCost(1).baseDamage(150).baseDefense(45).baseHeal(0)
                .foodType("Độc Trùng").maxHungerHours(40).icon("🦂").pathName("Biến Hóa Đạo").slotCategory("CONG_KICH")
                .description("Mọc đuôi bò cạp độc chích xuyên hộ thể đối phương gây độc tính kịch liệt.").build());

        list.add(GuDefinition.builder()
                .code("AO_MANG_BIEN_CO").name("Áo Mãng Biến Cổ").rank(4).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(35).apCost(1).baseDamage(260).baseDefense(85).baseHeal(0)
                .foodType("Thịt Rắn Khổng Lồ").maxHungerHours(60).icon("🐍").pathName("Biến Hóa Đạo").slotCategory("CONG_KICH")
                .description("Hóa thân thành trăn vĩ đại nuốt chửng phòng ngự đối phương.").build());

        list.add(GuDefinition.builder()
                .code("GIAC_LONG_BIEN_CO").name("Giác Long Biến Cổ").rank(4).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(38).apCost(1).baseDamage(280).baseDefense(95).baseHeal(0)
                .foodType("Long Cốt").maxHungerHours(72).icon("🐲").pathName("Biến Hóa Đạo").slotCategory("CONG_KICH")
                .description("Hóa hình Giác Long cổ đại chấn nhiếp địch thủ.").build());

        list.add(GuDefinition.builder()
                .code("THAI_CO_THU_PHU_CO").name("Thái Cổ Thú Phụ Cổ").rank(4).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(30).apCost(1).baseDamage(120).baseDefense(120).baseHeal(0)
                .foodType("Huyết Tinh Thú").maxHungerHours(60).icon("🦁").pathName("Biến Hóa Đạo").slotCategory("PHU_TRO")
                .description("Phụ thể linh hồn dã thú thái cổ, tăng mạnh thuộc tính biến hóa.").build());

        list.add(GuDefinition.builder()
                .code("BIEN_HOA_VAN_VAT_CO").name("Biến Hóa Vạn Vật Cổ").rank(5).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(45).apCost(1).baseDamage(350).baseDefense(150).baseHeal(0)
                .foodType("Vạn Vật Tinh").maxHungerHours(96).icon("🌀").pathName("Biến Hóa Đạo").slotCategory("DAC_BIET")
                .description("Biến hóa vô cùng tận thành bất kỳ loài sinh vật hay hiện tượng nào.").build());

        list.add(GuDefinition.builder()
                .code("THAI_CO_BAT_SON_BIEN_CO").name("Thái Cổ Bạt Sơn Biến Cổ").rank(5).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(50).apCost(1).baseDamage(450).baseDefense(180).baseHeal(0)
                .foodType("Núi Tủy").maxHungerHours(96).icon("🦣").pathName("Biến Hóa Đạo").slotCategory("CONG_KICH")
                .description("Hóa thân cự thú Bạt Sơn nhấc bổng rặng núi giáng đòn hủy diệt.").build());

        list.add(GuDefinition.builder()
                .code("DAI_LUC_CHAN_VU_TIEN_CO").name("Đại Lực Chân Vũ Tiên Cổ").rank(6).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(720).baseDefense(320).baseHeal(0)
                .foodType("Huyết Mạch Thập Tuyệt").maxHungerHours(99999).icon("🥋").pathName("Biến Hóa Đạo").slotCategory("CONG_KICH")
                .description("Tiên Cổ dung hợp hoàn mỹ thể phách Thập Tuyệt Thể Đại Lực Chân Vũ bất tử bất diệt.").build());

        list.add(GuDefinition.builder()
                .code("BIEN_HOA_TIEN_CO").name("Biến Hóa Tiên Cổ").rank(6).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(650).baseDefense(250).baseHeal(0)
                .foodType("Đạo Ngân Biến Hóa").maxHungerHours(99999).icon("✨").pathName("Biến Hóa Đạo").slotCategory("DAC_BIET")
                .description("Lục Chuyển Tiên Cổ: Nắm giữ bản nguyên biến hóa, thiên biến vạn hóa tự tại khôn cùng.").build());

        list.add(GuDefinition.builder()
                .code("THUONG_CO_HOANG_THU_BIEN").name("Thượng Cổ Hoang Thú Biến").rank(7).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(0).apCost(1).baseDamage(1200).baseDefense(460).baseHeal(0)
                .foodType("Thịt Hoang Thú Cổ").maxHungerHours(99999).icon("🐋").pathName("Biến Hóa Đạo").slotCategory("CONG_KICH")
                .description("Hóa thân Viễn Cổ Long Ngư / Hà Mã / Niên Thú nuốt trời, áp đảo đối thủ.").build());

        // ---------------------------------------------------------
        // 1.3 VIÊM ĐẠO (Fire Path)
        // ---------------------------------------------------------
        list.add(GuDefinition.builder()
                .code("HOA_CAU_CO").name("Hỏa Cầu Cổ").rank(1).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(10).apCost(1).baseDamage(46).baseDefense(0).baseHeal(0)
                .foodType("Than Hồng").maxHungerHours(24).icon("🔥").pathName("Viêm Đạo").slotCategory("CONG_KICH")
                .description("Bắn cầu lửa nhỏ bùng cháy thiêu đốt đối thủ tầm xa.").build());

        list.add(GuDefinition.builder()
                .code("HOA_MACH_CO").name("Hỏa Mạch Cổ").rank(1).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(8).apCost(1).baseDamage(15).baseDefense(15).baseHeal(0)
                .foodType("Tro Tàn Hỏa Sơn").maxHungerHours(30).icon("🌋").pathName("Viêm Đạo").slotCategory("PHU_TRO")
                .description("Khai mở hỏa mạch trong cơ thể, tăng sát thương cho toàn bộ Cổ Viêm Đạo.").build());

        list.add(GuDefinition.builder()
                .code("HOA_MIEU_CO").name("Hỏa Miêu Cổ").rank(1).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(9).apCost(1).baseDamage(40).baseDefense(0).baseHeal(0)
                .foodType("Hạt Lửa").maxHungerHours(24).icon("🕯️").pathName("Viêm Đạo").slotCategory("CONG_KICH")
                .description("Phóng ra đốm lửa nhỏ bám dính gây sát thương cháy liên tục.").build());

        list.add(GuDefinition.builder()
                .code("LIET_HOA_CO").name("Liệt Hỏa Cổ").rank(2).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(18).apCost(1).baseDamage(90).baseDefense(0).baseHeal(0)
                .foodType("Hỏa Diễm Tinh").maxHungerHours(30).icon("🌋").pathName("Viêm Đạo").slotCategory("CONG_KICH")
                .description("Ngọn lửa dữ dội thiêu rụi giáp bảo hộ của đối phương.").build());

        list.add(GuDefinition.builder()
                .code("HOA_NHA_CO").name("Hỏa Nha Cổ").rank(2).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(20).apCost(1).baseDamage(95).baseDefense(0).baseHeal(0)
                .foodType("Xương Đốt Cháy").maxHungerHours(30).icon("🦅").pathName("Viêm Đạo").slotCategory("CONG_KICH")
                .description("Triệu hồi bầy quạ lửa lao tới nổ tung vào đối phương.").build());

        list.add(GuDefinition.builder()
                .code("BAO_VIEM_CO").name("Bạo Viêm Cổ").rank(2).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(22).apCost(1).baseDamage(105).baseDefense(0).baseHeal(0)
                .foodType("Dung Nham Thạch").maxHungerHours(36).icon("💥").pathName("Viêm Đạo").slotCategory("CONG_KICH")
                .description("Nổ tung luồng nhiệt hỏa diễm bạo liệt gây chấn thương diện rộng.").build());

        list.add(GuDefinition.builder()
                .code("BANG_HOA_CO").name("Băng Hỏa Cổ").rank(3).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(26).apCost(1).baseDamage(155).baseDefense(20).baseHeal(0)
                .foodType("Băng Lam Hỏa").maxHungerHours(48).icon("❄️🔥").pathName("Viêm Đạo").slotCategory("CONG_KICH")
                .description("Ngọn lửa lạnh giá dị thường, vừa thiêu đốt vừa đông cứng kẻ thù.").build());

        list.add(GuDefinition.builder()
                .code("HOA_LONG_CO").name("Hỏa Long Cổ").rank(3).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(30).apCost(1).baseDamage(175).baseDefense(0).baseHeal(0)
                .foodType("Hỏa Long Sa").maxHungerHours(48).icon("🐉").pathName("Viêm Đạo").slotCategory("CONG_KICH")
                .description("Rồng lửa uốn lượn giáng xuống đòn đánh hủy diệt.").build());

        list.add(GuDefinition.builder()
                .code("TAM_MUOI_CHAN_HOA_CO").name("Tam Muội Chân Hỏa Cổ").rank(3).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(32).apCost(1).baseDamage(185).baseDefense(0).baseHeal(0)
                .foodType("Thiên Địa Linh Hỏa").maxHungerHours(48).icon("🔥").pathName("Viêm Đạo").slotCategory("CONG_KICH")
                .description("Chân hỏa tam muội vô cùng tinh khiết, thiêu cháy cả nguyên thạch.").build());

        list.add(GuDefinition.builder()
                .code("LAM_VIEM_CO").name("Lam Viêm Cổ").rank(4).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(36).apCost(1).baseDamage(255).baseDefense(0).baseHeal(0)
                .foodType("U Lam Hàn Hỏa").maxHungerHours(60).icon("🔷").pathName("Viêm Đạo").slotCategory("CONG_KICH")
                .description("Ngọn lửa xanh lam thiêu đốt trực tiếp linh hồn và chân nguyên.").build());

        list.add(GuDefinition.builder()
                .code("HOA_VAN_CO").name("Hỏa Vân Cổ").rank(4).type(GuType.THAN_PHAP)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(30).apCost(1).baseDamage(90).baseDefense(70).baseHeal(0)
                .foodType("Mây Đỏ Khí Lưu").maxHungerHours(60).icon("☁️").pathName("Viêm Đạo").slotCategory("THAN_PHAP")
                .description("Đạp mây lửa lướt đi ngàn dặm, che khuất tầm nhìn của địch thủ.").build());

        list.add(GuDefinition.builder()
                .code("BAO_VIEM_TO_CO").name("Bạo Viêm Tố Cổ").rank(4).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(40).apCost(1).baseDamage(290).baseDefense(0).baseHeal(0)
                .foodType("Linh Hỏa Tố").maxHungerHours(72).icon("💣").pathName("Viêm Đạo").slotCategory("CONG_KICH")
                .description("Bộc phát liên hoàn hỏa pháo hủy diệt toàn bộ chiến trường.").build());

        list.add(GuDefinition.builder()
                .code("CUU_MUOI_CHAN_HOA_CO").name("Cửu Muội Chân Hỏa Cổ").rank(5).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(48).apCost(1).baseDamage(420).baseDefense(0).baseHeal(0)
                .foodType("Thái Dương Chân Hỏa").maxHungerHours(96).icon("🔆").pathName("Viêm Đạo").slotCategory("CONG_KICH")
                .description("Ngọn lửa cửu muội thiêu rụi vạn vật, đun sôi cả hồ nước lớn.").build());

        list.add(GuDefinition.builder()
                .code("DIA_HOA_VIEM_VUONG_CO").name("Địa Hỏa Viêm Vương Cổ").rank(5).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(50).apCost(1).baseDamage(460).baseDefense(100).baseHeal(0)
                .foodType("Nham Thạch Lõi Đất").maxHungerHours(96).icon("👑").pathName("Viêm Đạo").slotCategory("CONG_KICH")
                .description("Chúa tể lửa lòng đất thức tỉnh, biến chiến trường thành biển dung nham.").build());

        list.add(GuDefinition.builder()
                .code("CUU_MUOI_VIEM_TIEN_CO").name("Cửu Muội Viêm Tiên Cổ").rank(6).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(0).apCost(1).baseDamage(780).baseDefense(100).baseHeal(0)
                .foodType("Thiên Hỏa Tinh Hoa").maxHungerHours(99999).icon("☀️").pathName("Viêm Đạo").slotCategory("CONG_KICH")
                .description("Lục Chuyển Tiên Hỏa bất diệt đốt cháy cả đạo ngân đất trời.").build());

        list.add(GuDefinition.builder()
                .code("HOA_TINH_TIEN_CO").name("Hỏa Tinh Tiên Cổ").rank(6).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(300).baseDefense(200).baseHeal(0)
                .foodType("Tinh Tú Sa").maxHungerHours(99999).icon("⭐").pathName("Viêm Đạo").slotCategory("PHU_TRO")
                .description("Tiên Cổ tinh hỏa ban phát năng lượng vô tận cho Viêm Đạo.").build());

        list.add(GuDefinition.builder()
                .code("DAM_BAI_VIEM_TIEN_CO").name("Đầm Bãi Viêm Tiên Cổ").rank(7).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(0).apCost(1).baseDamage(1300).baseDefense(350).baseHeal(0)
                .foodType("Đầm Lầy Lửa").maxHungerHours(99999).icon("🌋").pathName("Viêm Đạo").slotCategory("CONG_KICH")
                .description("Thất Chuyển Tiên Cổ: Nung chảy ngàn dặm đất đá thành đầm lầy lửa sôi sục.").build());

        // ---------------------------------------------------------
        // 1.4 THỦY ĐẠO / BĂNG ĐẠO (Water & Ice Path)
        // ---------------------------------------------------------
        list.add(GuDefinition.builder()
                .code("THUY_TIEN_CO").name("Thủy Tiễn Cổ").rank(1).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(8).apCost(1).baseDamage(38).baseDefense(5).baseHeal(0)
                .foodType("Nước Suối Núi").maxHungerHours(24).icon("🏹").pathName("Thủy Đạo").slotCategory("CONG_KICH")
                .description("Mũi tên nước sắc như dao xuyên giáp từ khoảng cách xa.").build());

        list.add(GuDefinition.builder()
                .code("BANG_TRU_CO").name("Băng Trù Cổ").rank(1).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(12).apCost(1).baseDamage(20).baseDefense(50).baseHeal(0)
                .foodType("Băng Tuyết Núi Cao").maxHungerHours(36).icon("🧊").pathName("Thủy Đạo").slotCategory("PHONG_NGU")
                .description("Dựng chông băng sắc nhọn cản bước tiến và phản chấn đòn đánh.").build());

        list.add(GuDefinition.builder()
                .code("HAN_BANG_CO").name("Hàn Băng Cổ").rank(1).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(10).apCost(1).baseDamage(45).baseDefense(10).baseHeal(0)
                .foodType("Khí Lạnh").maxHungerHours(24).icon("❄️").pathName("Thủy Đạo").slotCategory("CONG_KICH")
                .description("Đóng băng mặt đất và đối thủ làm tê liệt cử động.").build());

        list.add(GuDefinition.builder()
                .code("THUY_LONG_CO").name("Thủy Long Cổ").rank(2).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(18).apCost(1).baseDamage(95).baseDefense(15).baseHeal(0)
                .foodType("Nước Ngọt Suối Sâu").maxHungerHours(36).icon("🐉").pathName("Thủy Đạo").slotCategory("CONG_KICH")
                .description("Cuộn trào rồng nước cuốn bay mọi vật cản.").build());

        list.add(GuDefinition.builder()
                .code("BANG_MOC_CO").name("Băng Mộc Cổ").rank(2).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(16).apCost(1).baseDamage(15).baseDefense(80).baseHeal(0)
                .foodType("Gỗ Đóng Băng").maxHungerHours(36).icon("🪵").pathName("Thủy Đạo").slotCategory("PHONG_NGU")
                .description("Gỗ băng kiên cố chặn đứng kiếm kích của địch thủ.").build());

        list.add(GuDefinition.builder()
                .code("BANG_PHONG_CO").name("Băng Phong Cổ").rank(2).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(18).apCost(1).baseDamage(85).baseDefense(20).baseHeal(0)
                .foodType("Gió Rét Vùng Cực").maxHungerHours(30).icon("🌬️").pathName("Thủy Đạo").slotCategory("CONG_KICH")
                .description("Gió bão băng giá rít gào làm chậm và gây sát thương tê buốt.").build());

        list.add(GuDefinition.builder()
                .code("THUY_BUC_CO").name("Thủy Bức Cổ").rank(3).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(24).apCost(1).baseDamage(150).baseDefense(15).baseHeal(0)
                .foodType("Máu Cá").maxHungerHours(48).icon("🦇").pathName("Thủy Đạo").slotCategory("CONG_KICH")
                .description("Bầy dơi nước bay lượn hút chân nguyên và sinh lực kẻ thù.").build());

        list.add(GuDefinition.builder()
                .code("BANG_SUONG_CO").name("Băng Sương Cổ").rank(3).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(26).apCost(1).baseDamage(40).baseDefense(140).baseHeal(0)
                .foodType("Sương Muối Ngàn Năm").maxHungerHours(48).icon("🌨️").pathName("Thủy Đạo").slotCategory("PHONG_NGU")
                .description("Giáp sương giá phủ kín người, giảm mạnh sát thương vật lý.").build());

        list.add(GuDefinition.builder()
                .code("TUYET_BOC_CO").name("Tuyết Bốc Cổ").rank(3).type(GuType.THAN_PHAP)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(22).apCost(1).baseDamage(60).baseDefense(40).baseHeal(0)
                .foodType("Hoa Tuyết Tinh").maxHungerHours(40).icon("❄️").pathName("Thủy Đạo").slotCategory("THAN_PHAP")
                .description("Độn thổ trong bão tuyết lướt đi thoăn thoắt không để lại dấu vết.").build());

        list.add(GuDefinition.builder()
                .code("BANG_PHONG_BAO_CO").name("Băng Phong Báo Cổ").rank(4).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(38).apCost(1).baseDamage(270).baseDefense(40).baseHeal(0)
                .foodType("Tuyết Báo Tinh").maxHungerHours(60).icon("🐆").pathName("Thủy Đạo").slotCategory("CONG_KICH")
                .description("Hóa bão tuyết thành hình báo săn mồi cắn xé tàn bạo đối phương.").build());

        list.add(GuDefinition.builder()
                .code("SUONG_TUYET_TUYET_CO").name("Sương Tuyết Tuyệt Cổ").rank(4).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(40).apCost(1).baseDamage(290).baseDefense(30).baseHeal(0)
                .foodType("Băng Phách Thạch").maxHungerHours(72).icon("🏔️").pathName("Thủy Đạo").slotCategory("CONG_KICH")
                .description("Hàn khí cực hạn đóng băng mọi sự sống trong nháy mắt.").build());

        list.add(GuDefinition.builder()
                .code("TSUNAMI_CO").name("Tsunami Cổ").rank(4).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(42).apCost(1).baseDamage(310).baseDefense(50).baseHeal(0)
                .foodType("Thủy Tinh Thạch").maxHungerHours(72).icon("🌊").pathName("Thủy Đạo").slotCategory("CONG_KICH")
                .description("Sóng thần ngập trời nhấn chìm cả doanh trại và hủy diệt trận đồ.").build());

        list.add(GuDefinition.builder()
                .code("BANG_HAI_CO").name("Băng Hải Cổ").rank(5).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(45).apCost(1).baseDamage(80).baseDefense(260).baseHeal(0)
                .foodType("Biển Băng Vĩnh Cửu").maxHungerHours(96).icon("🧊").pathName("Thủy Đạo").slotCategory("PHONG_NGU")
                .description("Biến toàn bộ Không Khiếu thành biển băng kiên cố vô song.").build());

        list.add(GuDefinition.builder()
                .code("TUYET_LANG_CO").name("Tuyết Lãng Cổ").rank(5).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(48).apCost(1).baseDamage(440).baseDefense(70).baseHeal(0)
                .foodType("Sóng Tuyết Bắc Nguyên").maxHungerHours(96).icon("🌊").pathName("Thủy Đạo").slotCategory("CONG_KICH")
                .description("Cuộn trào đợt sóng tuyết vùi lấp cả vạn quân mã.").build());

        list.add(GuDefinition.builder()
                .code("BANG_TUYET_TIEN_CO").name("Băng Tuyết Tiên Cổ").rank(6).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(0).apCost(1).baseDamage(750).baseDefense(200).baseHeal(0)
                .foodType("Thiên Băng Tinh Hoa").maxHungerHours(99999).icon("❄️").pathName("Thủy Đạo").slotCategory("CONG_KICH")
                .description("Lục Chuyển Tiên Cổ: Nắm giữ uy năng băng tuyết tuyệt đối của trời đất.").build());

        list.add(GuDefinition.builder()
                .code("THUY_LUU_TIEN_CO").name("Thủy Lưu Tiên Cổ").rank(6).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(200).baseDefense(350).baseHeal(200)
                .foodType("Dòng Nước Sinh Mệnh").maxHungerHours(99999).icon("💧").pathName("Thủy Đạo").slotCategory("PHU_TRO")
                .description("Nước chảy không ngừng, hồi phục sinh lực và hóa giải thương tổn.").build());

        list.add(GuDefinition.builder()
                .code("BANG_PHACH_TIEN_CO").name("Băng Phách Tiên Cổ").rank(7).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(0).apCost(1).baseDamage(1280).baseDefense(400).baseHeal(0)
                .foodType("Băng Phách Đạo Ngân").maxHungerHours(99999).icon("💎").pathName("Thủy Đạo").slotCategory("CONG_KICH")
                .description("Thất Chuyển Tiên Cổ: Đóng băng linh hồn kẻ địch thành tượng đá vĩnh hằng.").build());

        // ---------------------------------------------------------
        // 1.5 LÔI ĐẠO / PHONG ĐẠO (Lightning & Wind Path)
        // ---------------------------------------------------------
        list.add(GuDefinition.builder()
                .code("THIEM_DIEN_CO").name("Thiểm Điện Cổ").rank(1).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(12).apCost(1).baseDamage(48).baseDefense(0).baseHeal(0)
                .foodType("Lôi Thạch").maxHungerHours(24).icon("⚡").pathName("Lôi Đạo").slotCategory("CONG_KICH")
                .description("Phóng tia sét chớp lóe giật tê liệt và sát thương mục tiêu.").build());

        list.add(GuDefinition.builder()
                .code("THANH_PHONG_LUAN_CO").name("Thanh Phong Luân Cổ").rank(1).type(GuType.THAN_PHAP)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(10).apCost(1).baseDamage(10).baseDefense(20).baseHeal(0)
                .foodType("Gió Núi").maxHungerHours(36).icon("🌀").pathName("Phong Đạo").slotCategory("THAN_PHAP")
                .description("Hai bánh xe gió xoay dưới chân, tăng tốc độ di chuyển né tránh.").build());

        list.add(GuDefinition.builder()
                .code("TAT_PHONG_CO").name("Tật Phong Cổ").rank(1).type(GuType.THAN_PHAP)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(12).apCost(1).baseDamage(20).baseDefense(25).baseHeal(0)
                .foodType("Khí Lưu").maxHungerHours(30).icon("🍃").pathName("Phong Đạo").slotCategory("THAN_PHAP")
                .description("Gió lốc cuốn quanh cơ thể, bứt tốc né đòn trong gang tấc.").build());

        list.add(GuDefinition.builder()
                .code("TU_LOI_CO").name("Tử Lôi Cổ").rank(2).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(20).apCost(1).baseDamage(98).baseDefense(0).baseHeal(0)
                .foodType("Tử Thạch").maxHungerHours(36).icon("🟣").pathName("Lôi Đạo").slotCategory("CONG_KICH")
                .description("Tia sét màu tím bạo liệt đánh xuyên giáp đối phương.").build());

        list.add(GuDefinition.builder()
                .code("PHONG_TRAN_CO").name("Phong Trận Cổ").rank(2).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(18).apCost(1).baseDamage(90).baseDefense(10).baseHeal(0)
                .foodType("Gió Cuồng Phong").maxHungerHours(36).icon("🌪️").pathName("Phong Đạo").slotCategory("CONG_KICH")
                .description("Tạo trận đao gió cuồng bạo chém loạn xạ vào kẻ địch.").build());

        list.add(GuDefinition.builder()
                .code("LOI_TUONG_CO").name("Lôi Tương Cổ").rank(2).type(GuType.TIEU_THU)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(15).apCost(1).baseDamage(115).baseDefense(0).baseHeal(0)
                .consumable(true)
                .foodType("Lôi Thạch").maxHungerHours(48).icon("⚡").pathName("Lôi Đạo").slotCategory("DAC_BIET")
                .description("Cổ trùng tiêu hao: Bóp vỡ giải phóng quả cầu sét bộc phá kinh hoàng.").build());

        list.add(GuDefinition.builder()
                .code("BAO_LOI_CO").name("Bão Lôi Cổ").rank(3).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(28).apCost(1).baseDamage(168).baseDefense(0).baseHeal(0)
                .foodType("Sét Đánh Ngọn Cây").maxHungerHours(48).icon("🌩️").pathName("Lôi Đạo").slotCategory("CONG_KICH")
                .description("Sấm chớp bão bùng giáng xuống liên hoàn làm tê liệt hoàn toàn đối thủ.").build());

        list.add(GuDefinition.builder()
                .code("PHONG_VAN_CO").name("Phong Vân Cổ").rank(3).type(GuType.THAN_PHAP)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(24).apCost(1).baseDamage(50).baseDefense(50).baseHeal(0)
                .foodType("Mây Trắng").maxHungerHours(48).icon("☁️").pathName("Phong Đạo").slotCategory("THAN_PHAP")
                .description("Gió mây hòa quyện, thoắt ẩn thoắt hiện né tránh đòn đánh.").build());

        list.add(GuDefinition.builder()
                .code("CUONG_PHONG_CO").name("Cuồng Phong Cổ").rank(3).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(26).apCost(1).baseDamage(160).baseDefense(20).baseHeal(0)
                .foodType("Gió Lốc").maxHungerHours(48).icon("🌪️").pathName("Phong Đạo").slotCategory("CONG_KICH")
                .description("Cuồng phong gầm thét cuốn bay vũ khí và giáp của đối thủ.").build());

        list.add(GuDefinition.builder()
                .code("BAO_LOI_TIEN_CO").name("Bạo Lôi Tiễn Cổ").rank(4).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(38).apCost(1).baseDamage(285).baseDefense(0).baseHeal(0)
                .foodType("Lôi Thạch Tinh").maxHungerHours(60).icon("⚡").pathName("Lôi Đạo").slotCategory("CONG_KICH")
                .description("Mũi tên sấm sét tốc độ ánh sáng bắn xuyên thấu tim đối thủ.").build());

        list.add(GuDefinition.builder()
                .code("PHONG_BAO_CO").name("Phong Báo Cổ").rank(4).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(36).apCost(1).baseDamage(265).baseDefense(30).baseHeal(0)
                .foodType("Khí Bão Lốc").maxHungerHours(60).icon("🌪️").pathName("Phong Đạo").slotCategory("CONG_KICH")
                .description("Bão tố xoáy sâu xé rách vách Không Khiếu của địch.").build());

        list.add(GuDefinition.builder()
                .code("CUONG_LONG_BAO_CO").name("Cuồng Long Bão Cổ").rank(4).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(40).apCost(1).baseDamage(295).baseDefense(40).baseHeal(0)
                .foodType("Phong Tinh Thạch").maxHungerHours(72).icon("🐉").pathName("Phong Đạo").slotCategory("CONG_KICH")
                .description("Cơn lốc rồng cuồng loạn nghiền nát mọi chướng ngại vật.").build());

        list.add(GuDefinition.builder()
                .code("TU_DIEN_CUONG_LOI_CO").name("Tử Điện Cuồng Lôi Cổ").rank(5).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(48).apCost(1).baseDamage(450).baseDefense(0).baseHeal(0)
                .foodType("Thiên Lôi Thạch").maxHungerHours(96).icon("⚡").pathName("Lôi Đạo").slotCategory("CONG_KICH")
                .description("Sấm sét màu tím cuồng bạo giáng xuống như thiên kiếp giáng lâm.").build());

        list.add(GuDefinition.builder()
                .code("DAI_PHONG_BAO_CO").name("Đại Phong Bạo Cổ").rank(5).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(46).apCost(1).baseDamage(430).baseDefense(60).baseHeal(0)
                .foodType("Mắt Bão Tố").maxHungerHours(96).icon("🌪️").pathName("Phong Đạo").slotCategory("CONG_KICH")
                .description("Trận đại phong bạo quét sạch toàn bộ sinh linh trong phạm vi trăm dặm.").build());

        list.add(GuDefinition.builder()
                .code("CUONG_LOI_TIEN_CO").name("Cuồng Lôi Tiên Cổ").rank(6).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(0).apCost(1).baseDamage(820).baseDefense(0).baseHeal(0)
                .foodType("Thiên Kiếp Lôi Đình").maxHungerHours(99999).icon("⚡").pathName("Lôi Đạo").slotCategory("CONG_KICH")
                .description("Lục Chuyển Tiên Cổ: Nắm giữ thần uy lôi đình thiên kiếp trảm sát tiên ma.").build());

        list.add(GuDefinition.builder()
                .code("THANH_PHONG_TIEN_CO").name("Thanh Phong Tiên Cổ").rank(6).type(GuType.THAN_PHAP)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(0).apCost(1).baseDamage(250).baseDefense(300).baseHeal(0)
                .foodType("Cửu Thiên Cương Phong").maxHungerHours(99999).icon("🍃").pathName("Phong Đạo").slotCategory("THAN_PHAP")
                .description("Lục Chuyển Tiên Cổ: Thân như thanh phong lướt chín tầng mây tự do tự tại.").build());

        list.add(GuDefinition.builder()
                .code("LOI_DINH_TIEN_CO").name("Lôi Đình Tiên Cổ").rank(7).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(0).apCost(1).baseDamage(1350).baseDefense(200).baseHeal(0)
                .foodType("Đạo Ngân Lôi Đạo").maxHungerHours(99999).icon("🌟").pathName("Lôi Đạo").slotCategory("CONG_KICH")
                .description("Thất Chuyển Tiên Cổ: Một tiếng sét đánh nát hư không, hủy diệt tiên khiếu.").build());

        // ---------------------------------------------------------
        // 1.6 TRÍ ĐẠO (Wisdom Path)
        // ---------------------------------------------------------
        list.add(GuDefinition.builder()
                .code("GIAI_ME_CO").name("Giải Mê Cổ").rank(1).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(5).apCost(1).baseDamage(0).baseDefense(0).baseHeal(0)
                .foodType("Mực Tàu").maxHungerHours(48).icon("🧩").pathName("Trí Đạo").slotCategory("PHU_TRO")
                .description("Khai thông trí óc, giải đáp câu đố và nhìn thấu ảo trận cơ bản.").build());

        list.add(GuDefinition.builder()
                .code("LINH_QUANG_CO").name("Linh Quang Cổ").rank(1).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(6).apCost(1).baseDamage(0).baseDefense(0).baseHeal(0)
                .foodType("Trí Tuệ Hương").maxHungerHours(48).icon("💡").pathName("Trí Đạo").slotCategory("PHU_TRO")
                .description("Tia sáng linh cảm lóe lên giúp tính toán nhanh phương kế đối phó kẻ địch.").build());

        list.add(GuDefinition.builder()
                .code("TU_KY_CO").name("Tư Ký Cổ").rank(2).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(10).apCost(1).baseDamage(0).baseDefense(20).baseHeal(0)
                .foodType("Giấy Tuyên Thành").maxHungerHours(48).icon("📜").pathName("Trí Đạo").slotCategory("PHU_TRO")
                .description("Ghi nhớ mọi chiêu thức đối thủ đã thi triển để tìm kẽ hở phản kích.").build());

        list.add(GuDefinition.builder()
                .code("NIEM_DAU_CO").name("Niệm Đầu Cổ").rank(2).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(12).apCost(1).baseDamage(30).baseDefense(10).baseHeal(0)
                .foodType("Tinh Thần Sa").maxHungerHours(48).icon("🧠").pathName("Trí Đạo").slotCategory("PHU_TRO")
                .description("Sinh ra hàng vạn niệm đầu va chạm, gia tốc suy nghĩ trong trận chiến.").build());

        list.add(GuDefinition.builder()
                .code("TRI_Y_CO").name("Trí Ý Cổ").rank(2).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(14).apCost(1).baseDamage(40).baseDefense(15).baseHeal(0)
                .foodType("Hương Trầm").maxHungerHours(48).icon("🔮").pathName("Trí Đạo").slotCategory("PHU_TRO")
                .description("Ngưng tụ ý niệm sắc bén làm nhiễu loạn tâm thần đối thủ.").build());

        list.add(GuDefinition.builder()
                .code("DA_TU_CO").name("Đa Tự Cổ").rank(3).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(20).apCost(1).baseDamage(50).baseDefense(30).baseHeal(0)
                .foodType("Mực Ngàn Năm").maxHungerHours(60).icon("📚").pathName("Trí Đạo").slotCategory("PHU_TRO")
                .description("Suy nghĩ nhiều tầng, giăng bẫy đối thủ bằng mưu kế trùng điệp.").build());

        list.add(GuDefinition.builder()
                .code("TA_NIEM_CO").name("Tả Niệm Cổ").rank(3).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(22).apCost(1).baseDamage(140).baseDefense(20).baseHeal(0)
                .foodType("Tà Khí Tinh").maxHungerHours(60).icon("👿").pathName("Trí Đạo").slotCategory("CONG_KICH")
                .description("Phóng ra tà niệm ăn mòn ý chí và khả năng điều khiển Cổ trùng của địch.").build());

        list.add(GuDefinition.builder()
                .code("BACH_TU_CO").name("Bách Tư Cổ").rank(3).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(24).apCost(1).baseDamage(0).baseDefense(50).baseHeal(0)
                .foodType("Trà Tỉnh Thần").maxHungerHours(60).icon("🍵").pathName("Trí Đạo").slotCategory("PHU_TRO")
                .description("Trăm suy nghĩ hòa làm một, giải mã mọi sát chiêu đối thủ thi triển.").build());

        list.add(GuDefinition.builder()
                .code("DINH_TAM_CO").name("Định Tâm Cổ").rank(4).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(25).apCost(1).baseDamage(0).baseDefense(160).baseHeal(0)
                .foodType("Hoa Sen Trắng").maxHungerHours(72).icon("🪷").pathName("Trí Đạo").slotCategory("PHONG_NGU")
                .description("Tâm như mặt hồ tĩnh lặng, miễn nhiễm mọi hiệu ứng hoảng loạn và thôi miên.").build());

        list.add(GuDefinition.builder()
                .code("TINH_NHAN_CO").name("Tinh Nhãn Cổ").rank(4).type(GuType.TRINH_SAT)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(28).apCost(1).baseDamage(80).baseDefense(40).baseHeal(0)
                .foodType("Ánh Sao Đêm").maxHungerHours(72).icon("👁️").pathName("Trí Đạo").slotCategory("PHU_TRO")
                .description("Đôi mắt tinh tú nhìn thấu vận mệnh và sơ hở chí mạng của kẻ địch.").build());

        list.add(GuDefinition.builder()
                .code("TRI_TUE_MAN_CO").name("Trí Tuệ Mẫn Cổ").rank(4).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(30).apCost(1).baseDamage(100).baseDefense(60).baseHeal(0)
                .foodType("Tinh Sa Trí Tuệ").maxHungerHours(72).icon("✨").pathName("Trí Đạo").slotCategory("PHU_TRO")
                .description("Trí tuệ mẫn tiệp suy tính trước ba bước đi của đối thủ.").build());

        list.add(GuDefinition.builder()
                .code("TRI_TON_CO").name("Trí Tôn Cổ").rank(5).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(40).apCost(1).baseDamage(150).baseDefense(120).baseHeal(0)
                .foodType("Ngọc Tủy Trí Tuệ").maxHungerHours(96).icon("👑").pathName("Trí Đạo").slotCategory("PHU_TRO")
                .description("Đỉnh cao trí đạo phàm trần, áp đảo tinh thần đối thủ bằng uy áp trí tuệ.").build());

        list.add(GuDefinition.builder()
                .code("SUY_DIEN_CO").name("Suy Diễn Cổ").rank(5).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(45).apCost(1).baseDamage(200).baseDefense(100).baseHeal(0)
                .foodType("Đạo Sa").maxHungerHours(96).icon("🌀").pathName("Trí Đạo").slotCategory("DAC_BIET")
                .description("Tự suy diễn và sáng tạo ra các sát chiêu phàm cấp mới từ kinh nghiệm chiến đấu.").build());

        list.add(GuDefinition.builder()
                .code("DINH_SACH_CO").name("Định Sách Cổ").rank(6).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(300).baseDefense(300).baseHeal(0)
                .foodType("Mưu Kế Tiên Gia").maxHungerHours(99999).icon("♟️").pathName("Trí Đạo").slotCategory("PHU_TRO")
                .description("Lục Chuyển Tiên Cổ: Một khi đã định mưu thì vạn sự tất thành.").build());

        list.add(GuDefinition.builder()
                .code("MUU_DINH_CO").name("Mưu Định Cổ").rank(6).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(320).baseDefense(320).baseHeal(0)
                .foodType("Thiên Địa Bàn Cờ").maxHungerHours(99999).icon("📜").pathName("Trí Đạo").slotCategory("PHU_TRO")
                .description("Tiên Cổ tính toán mọi biến số thiên địa trước khi khai chiến.").build());

        list.add(GuDefinition.builder()
                .code("TRI_GIAC_TIEN_CO").name("Tri Giác Tiên Cổ").rank(7).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(400).baseDefense(400).baseHeal(0)
                .foodType("Cảm Ứng Đạo").maxHungerHours(99999).icon("🔮").pathName("Trí Đạo").slotCategory("PHU_TRO")
                .description("Thất Chuyển Tiên Cổ: Tri giác bao trùm vạn dặm biết trước mọi biến động.").build());

        list.add(GuDefinition.builder()
                .code("TU_TUC_TIEN_CO").name("Tự Túc Tiên Cổ").rank(7).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(420).baseDefense(420).baseHeal(0)
                .foodType("Chân Lý Vô Ngã").maxHungerHours(99999).icon("🧘").pathName("Trí Đạo").slotCategory("PHU_TRO")
                .description("Tâm cảnh tự túc vô cầu, không màng danh lợi áp chế ma niệm.").build());

        list.add(GuDefinition.builder()
                .code("TRI_TUE_TIEN_CO").name("Trí Tuệ Tiên Cổ").rank(9).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(1500).baseDefense(800).baseHeal(0)
                .foodType("Tuổi Thọ Sinh Mệnh").maxHungerHours(99999).icon("👑").pathName("Trí Đạo").slotCategory("DAC_BIET")
                .description("Cửu Chuyển Tiên Cổ ngự tại Đãng Hồn Sơn: Vầng quang trí tuệ vô thượng, suy diễn bí mật vạn cổ.").build());

        // ---------------------------------------------------------
        // 1.7 VẬN ĐẠO (Luck Path)
        // ---------------------------------------------------------
        list.add(GuDefinition.builder()
                .code("HAO_VAN_CO_PHAM").name("Hảo Vận Cổ Phàm").rank(1).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(5).apCost(1).baseDamage(0).baseDefense(10).baseHeal(0)
                .foodType("Đồng Xu May Mắn").maxHungerHours(48).icon("🍀").pathName("Vận Đạo").slotCategory("PHU_TRO")
                .description("Phàm Cổ may rủi nhỏ, gia tăng nhẹ tỉ lệ bạo kích và né đòn hiểm.").build());

        list.add(GuDefinition.builder()
                .code("KHI_VAN_PHAM_CO").name("Khí Vận Phàm Cổ").rank(3).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(15).apCost(1).baseDamage(20).baseDefense(30).baseHeal(0)
                .foodType("Khói May Mắn").maxHungerHours(60).icon("✨").pathName("Vận Đạo").slotCategory("PHU_TRO")
                .description("Củng cố khí vận bản thân, hóa nguy thành an trong những tình huống hiểm nghèo.").build());

        list.add(GuDefinition.builder()
                .code("KHI_VAN_CO").name("Khí Vận Cổ").rank(6).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(200).baseDefense(200).baseHeal(0)
                .foodType("Khí Lưu May Mắn").maxHungerHours(99999).icon("🎲").pathName("Vận Đạo").slotCategory("PHU_TRO")
                .description("Lục Chuyển Tiên Cổ: Nắm bắt khí vận thiên địa, ra đòn luôn trúng yếu huyệt.").build());

        list.add(GuDefinition.builder()
                .code("HAO_VAN_CO").name("Hảo Vận Cổ").rank(6).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(220).baseDefense(220).baseHeal(0)
                .foodType("Ngũ Sắc Khí").maxHungerHours(99999).icon("🌈").pathName("Vận Đạo").slotCategory("PHU_TRO")
                .description("Gia tăng phúc duyên ngập tràn, luyện Cổ tỉ lệ thành công cực cao.").build());

        list.add(GuDefinition.builder()
                .code("SUY_VAN_CO").name("Suy Vận Cổ").rank(6).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(0).apCost(1).baseDamage(550).baseDefense(100).baseHeal(0)
                .foodType("Hắc Khí Xui Xẻo").maxHungerHours(99999).icon("🖤").pathName("Vận Đạo").slotCategory("CONG_KICH")
                .description("Giáng xui xẻo ngập đầu đối phương, khiến chiêu thức của địch tự phản phệ.").build());

        list.add(GuDefinition.builder()
                .code("LIEN_VAN_TIEN_CO").name("Liên Vận Tiên Cổ").rank(6).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(0).apCost(1).baseDamage(0).baseDefense(350).baseHeal(0)
                .foodType("Sợi Tơ Nhân Duyên").maxHungerHours(99999).icon("🔗").pathName("Vận Đạo").slotCategory("PHU_TRO")
                .description("Trói buộc và chia sẻ khí vận với cường giả thiên mệnh.").build());

        list.add(GuDefinition.builder()
                .code("DINH_HON_VAN_TIEN_CO").name("Định Hồn Vận Tiên Cổ").rank(7).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(600).baseDefense(400).baseHeal(0)
                .foodType("Hồn Sa").maxHungerHours(99999).icon("🧿").pathName("Vận Đạo").slotCategory("DAC_BIET")
                .description("Thất Chuyển: Khóa chặt vận mệnh và linh hồn, ngăn chặn đối phương đoạt vận.").build());

        list.add(GuDefinition.builder()
                .code("CHUNG_SINH_VAN_TIEN_CO").name("Chúng Sinh Vận Tiên Cổ").rank(8).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(900).baseDefense(600).baseHeal(0)
                .foodType("Khí Vận Nhân Gian").maxHungerHours(99999).icon("👥").pathName("Vận Đạo").slotCategory("PHU_TRO")
                .description("Bát Chuyển: Gom tụ vận mệnh của ức vạn chúng sinh gia trì bản thân.").build());

        list.add(GuDefinition.builder()
                .code("THIEN_DIA_VAN_TIEN_CO").name("Thiên Địa Vận Tiên Cổ").rank(9).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(2000).baseDefense(1000).baseHeal(0)
                .foodType("Bản Nguyên Thiên Địa").maxHungerHours(99999).icon("🌌").pathName("Vận Đạo").slotCategory("DAC_BIET")
                .description("Cửu Chuyển Tiên Cổ do Cự Dương Ma Tôn sáng tạo: Nắm giữ vận mệnh càn khôn đất trời.").build());

        // ---------------------------------------------------------
        // 1.8 THỜI GIAN ĐẠO / KHÔNG GIAN ĐẠO (Time & Space Path)
        // ---------------------------------------------------------
        list.add(GuDefinition.builder()
                .code("QUANG_AM_CO").name("Quang Âm Cổ").rank(1).type(GuType.THAN_PHAP)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(8).apCost(1).baseDamage(15).baseDefense(20).baseHeal(0)
                .foodType("Cát Thời Gian").maxHungerHours(48).icon("⏳").pathName("Thời Gian Đạo").slotCategory("THAN_PHAP")
                .description("Cảm nhận dòng chảy thời gian xung quanh để né tránh chuẩn xác.").build());

        list.add(GuDefinition.builder()
                .code("TOC_DO_CO").name("Tốc Độ Cổ").rank(2).type(GuType.THAN_PHAP)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(12).apCost(1).baseDamage(35).baseDefense(25).baseHeal(0)
                .foodType("Bụi Không Khí").maxHungerHours(36).icon("⚡").pathName("Thời Gian Đạo").slotCategory("THAN_PHAP")
                .description("Gia tốc chuyển động cơ thể trong nháy mắt.").build());

        list.add(GuDefinition.builder()
                .code("KHONG_KHIEU_CO").name("Không Khiếu Cổ").rank(1).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(5).apCost(1).baseDamage(0).baseDefense(10).baseHeal(0)
                .foodType("Chân Nguyên Thạch").maxHungerHours(72).icon("🔮").pathName("Không Gian Đạo").slotCategory("PHU_TRO")
                .description("Mở rộng không gian chứa đựng Chân Nguyên trong Đan Điền.").build());

        list.add(GuDefinition.builder()
                .code("DI_HE_CO").name("Di Hề Cổ").rank(3).type(GuType.THAN_PHAP)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(20).apCost(1).baseDamage(45).baseDefense(45).baseHeal(0)
                .foodType("Không Gian Sa").maxHungerHours(48).icon("🌀").pathName("Không Gian Đạo").slotCategory("THAN_PHAP")
                .description("Dịch chuyển vị trí tức thời trong phạm vi chiến trường tầm gần.").build());

        list.add(GuDefinition.builder()
                .code("SUC_DIA_THANH_THON_CO").name("Súc Địa Thành Thốn Cổ").rank(5).type(GuType.THAN_PHAP)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(35).apCost(1).baseDamage(100).baseDefense(100).baseHeal(0)
                .foodType("Lõi Không Gian").maxHungerHours(96).icon("🥾").pathName("Không Gian Đạo").slotCategory("THAN_PHAP")
                .description("Một bước đi vượt ngàn dặm, thu hẹp không gian đất trời dưới chân.").build());

        list.add(GuDefinition.builder()
                .code("QUANG_AM_LUU_THUY_CO").name("Quang Âm Lưu Thủy Cổ").rank(6).type(GuType.THAN_PHAP)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(450).baseDefense(300).baseHeal(0)
                .foodType("Nước Sông Quang Âm").maxHungerHours(99999).icon("🌊").pathName("Thời Gian Đạo").slotCategory("THAN_PHAP")
                .description("Thôi động dòng chảy Quang Âm, đảo ngược sát chiêu kẻ thù trong tích tắc.").build());

        list.add(GuDefinition.builder()
                .code("HOI_HAN_CO").name("Hối Hận Cổ").rank(6).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(500).baseDefense(300).baseHeal(0)
                .foodType("Giọt Lệ Ăn Năn").maxHungerHours(99999).icon("💧").pathName("Thời Gian Đạo").slotCategory("DAC_BIET")
                .description("Đưa tâm thức đối phương chìm đắm trong hối hận tột cùng, tự hủy tu vi.").build());

        list.add(GuDefinition.builder()
                .code("TRU_DAO_TIEN_CO").name("Trụ Đạo Tiên Cổ").rank(6).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(600).baseDefense(400).baseHeal(0)
                .foodType("Thời Gian Đạo Ngân").maxHungerHours(99999).icon("⏳").pathName("Thời Gian Đạo").slotCategory("DAC_BIET")
                .description("Lục Chuyển Tiên Cổ: Nắm giữ quy luật Trụ Đạo, gia tốc thời gian trong Tiên Khiếu.").build());

        list.add(GuDefinition.builder()
                .code("XUAN_THU_THIEN").name("Xuân Thu Thiền").rank(9).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(0).baseDefense(0).baseHeal(0)
                .foodType("Nước Sông Quang Âm").maxHungerHours(999999).icon("🦗").pathName("Thời Gian Đạo").slotCategory("TIEN_CO_NGAM")
                .description("Bảo vật nghịch thiên: Quay ngược dòng thời gian, trùng sinh trở về kiếp trước!").build());

        list.add(GuDefinition.builder()
                .code("DINH_KHONG_TIEN_CO").name("Định Không Tiên Cổ").rank(6).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(0).apCost(1).baseDamage(500).baseDefense(500).baseHeal(0)
                .foodType("Tinh Thạch Hư Không").maxHungerHours(99999).icon("🪨").pathName("Không Gian Đạo").slotCategory("DAC_BIET")
                .description("Lục Chuyển Tiên Cổ: Khóa cứng không gian, ngăn chặn dịch chuyển và phi kiếm.").build());

        list.add(GuDefinition.builder()
                .code("GIOI_CANH_TIEN_CO").name("Giới Cảnh Tiên Cổ").rank(7).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(800).baseDefense(600).baseHeal(0)
                .foodType("Màng Ranh Giới").maxHungerHours(99999).icon("🌐").pathName("Không Gian Đạo").slotCategory("DAC_BIET")
                .description("Thất Chuyển: Xuyên phá ranh giới giữa ngũ vực cửu thiên.").build());

        list.add(GuDefinition.builder()
                .code("THAI_CO_KHONG_GIAN_TIEN_CO").name("Thái Cổ Không Gian Tiên Cổ").rank(8).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(1500).baseDefense(900).baseHeal(0)
                .foodType("Bản Nguyên Hư Không").maxHungerHours(99999).icon("🌌").pathName("Không Gian Đạo").slotCategory("DAC_BIET")
                .description("Bát Chuyển: Cắt đứt không gian thành từng mảnh vụn hủy diệt quân địch.").build());

        // ---------------------------------------------------------
        // 1.9 NÔ ĐẠO & VU ĐẠO (Slave & Summon Path)
        // ---------------------------------------------------------
        list.add(GuDefinition.builder()
                .code("TROI_BUOC_CO").name("Trói Buộc Cổ").rank(1).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(8).apCost(1).baseDamage(30).baseDefense(10).baseHeal(0)
                .foodType("Dây Leo").maxHungerHours(24).icon("🪢").pathName("Nô Đạo").slotCategory("CONG_KICH")
                .description("Phóng dây thừng vô hình trói buộc hành động của thú hoang hoặc nô lệ.").build());

        list.add(GuDefinition.builder()
                .code("KHONG_SOI_CO").name("Khống Sói Cổ").rank(1).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(10).apCost(1).baseDamage(40).baseDefense(10).baseHeal(0)
                .foodType("Thịt Tươi Sói").maxHungerHours(36).icon("🐺").pathName("Nô Đạo").slotCategory("PHU_TRO")
                .description("Khuất phục bầy sói hoang dã làm tay sai hỗ trợ chiến đấu.").build());

        list.add(GuDefinition.builder()
                .code("KHONG_TRO_CO").name("Khống Trợ Cổ").rank(1).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(8).apCost(1).baseDamage(25).baseDefense(15).baseHeal(0)
                .foodType("Hạt Kê").maxHungerHours(36).icon("🐖").pathName("Nô Đạo").slotCategory("PHU_TRO")
                .description("Thu phục đàn lợn rừng hỗ trợ càn quét chiến trường.").build());

        list.add(GuDefinition.builder()
                .code("KHONG_HUM_CO").name("Khống Hùm Cổ").rank(2).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(18).apCost(1).baseDamage(85).baseDefense(20).baseHeal(0)
                .foodType("Thịt Hổ").maxHungerHours(36).icon("🐯").pathName("Nô Đạo").slotCategory("CONG_KICH")
                .description("Thu phục mãnh hổ làm tiên phong cắn xé quân địch.").build());

        list.add(GuDefinition.builder()
                .code("CUONG_THI_NO_CO").name("Cương Thi Nô Cổ").rank(2).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(20).apCost(1).baseDamage(60).baseDefense(90).baseHeal(0)
                .foodType("Tử Khí Mộ Phần").maxHungerHours(48).icon("🧟").pathName("Nô Đạo").slotCategory("PHONG_NGU")
                .description("Điều khiển cương thi mình đồng da sắt làm lá chắn đỡ đòn.").build());

        list.add(GuDefinition.builder()
                .code("THACH_NHAN_KHONG_CO").name("Thạch Nhân Khống Cổ").rank(2).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(22).apCost(1).baseDamage(50).baseDefense(100).baseHeal(0)
                .foodType("Quặng Thạch").maxHungerHours(48).icon("🗿").pathName("Nô Đạo").slotCategory("PHONG_NGU")
                .description("Nô dịch người đá khổng lồ cản bước tiến quân địch.").build());

        list.add(GuDefinition.builder()
                .code("KHONG_THU_VUONG_CO").name("Khống Thú Vương Cổ").rank(3).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(26).apCost(1).baseDamage(150).baseDefense(40).baseHeal(0)
                .foodType("Huyết Tinh Thú Vương").maxHungerHours(48).icon("👑").pathName("Nô Đạo").slotCategory("CONG_KICH")
                .description("Thu phục Thú Vương chỉ huy cả ngàn con thú hoang tàn sát đối thủ.").build());

        list.add(GuDefinition.builder()
                .code("BACH_LANG_NO_CO").name("Bách Lang Nô Cổ").rank(3).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(28).apCost(1).baseDamage(165).baseDefense(35).baseHeal(0)
                .foodType("Máu Sói Tinh").maxHungerHours(48).icon("🐺").pathName("Nô Đạo").slotCategory("CONG_KICH")
                .description("Triệu tập bầy sói 100 con vây hãm cắn xé đối thủ không ngừng nghỉ.").build());

        list.add(GuDefinition.builder()
                .code("NO_LE_CO").name("Nô Lệ Cổ").rank(3).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(20).apCost(1).baseDamage(50).baseDefense(50).baseHeal(0)
                .foodType("Hợp Đồng Nô Lệ").maxHungerHours(60).icon("⛓️").pathName("Nô Đạo").slotCategory("PHU_TRO")
                .description("Khắc ấn nô lệ lên linh hồn đối phương ép phục tùng tuyệt đối.").build());

        list.add(GuDefinition.builder()
                .code("BACH_THU_NO_CO").name("Bách Thú Nô Cổ").rank(4).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(35).apCost(1).baseDamage(260).baseDefense(60).baseHeal(0)
                .foodType("Thịt Trâu Tươi").maxHungerHours(72).icon("🦬").pathName("Nô Đạo").slotCategory("CONG_KICH")
                .description("Thống lĩnh bầy thú hàng ngàn con tạo nên triều dâng thú hoang dẫm nát phòng tuyến.").build());

        list.add(GuDefinition.builder()
                .code("MAO_NHAN_NO_CO").name("Mao Nhân Nô Cổ").rank(4).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(30).apCost(1).baseDamage(80).baseDefense(80).baseHeal(0)
                .foodType("Rượu Ngon Mao Nhân").maxHungerHours(72).icon("🦍").pathName("Nô Đạo").slotCategory("PHU_TRO")
                .description("Bắt giữ dị nhân Mao Nhân chuyên phục vụ việc luyện Cổ cho Cổ sư.").build());

        list.add(GuDefinition.builder()
                .code("THIEN_THU_VUONG_CO").name("Thiên Thú Vương Cổ").rank(5).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(48).apCost(1).baseDamage(420).baseDefense(100).baseHeal(0)
                .foodType("Lõi Thú Vương").maxHungerHours(96).icon("🐅").pathName("Nô Đạo").slotCategory("CONG_KICH")
                .description("Thu phục Thiên Thú Vương lãnh đạo vạn thú càn quét cả giang sơn.").build());

        list.add(GuDefinition.builder()
                .code("VAN_NHAN_NO_CO").name("Vạn Nhân Nô Cổ").rank(5).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(50).apCost(1).baseDamage(380).baseDefense(120).baseHeal(0)
                .foodType("Linh Hồn Nô Lệ").maxHungerHours(96).icon("👥").pathName("Nô Đạo").slotCategory("DAC_BIET")
                .description("Nô dịch một lúc một vạn người tạo nên quân đoàn nô lệ cảm tử.").build());

        list.add(GuDefinition.builder()
                .code("VAN_THU_VUONG_CO").name("Vạn Thú Vương Cổ").rank(6).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(0).apCost(1).baseDamage(800).baseDefense(300).baseHeal(0)
                .foodType("Thịt Hoang Thú").maxHungerHours(99999).icon("🐉").pathName("Nô Đạo").slotCategory("CONG_KICH")
                .description("Tiên Cổ thống lĩnh Hoang thú vạn dặm, tiếng rống làm rạn nứt cả Tiên Khiếu.").build());

        list.add(GuDefinition.builder()
                .code("BACH_HAI_TIEN_CO").name("Bách Hài Tiên Cổ").rank(6).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(0).apCost(1).baseDamage(750).baseDefense(350).baseHeal(0)
                .foodType("Xương Tiên Cổ").maxHungerHours(99999).icon("💀").pathName("Vu Đạo").slotCategory("CONG_KICH")
                .description("Triệu hồi quân đoàn bạch cốt cương thi tiên cấp bất tử đánh tan tiên trận.").build());

        list.add(GuDefinition.builder()
                .code("KHIEU_NGAO_TIEN_CO").name("Khiếu Ngạo Tiên Cổ").rank(7).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(1200).baseDefense(500).baseHeal(0)
                .foodType("Hồn Thú Thái Cổ").maxHungerHours(99999).icon("🦅").pathName("Nô Đạo").slotCategory("DAC_BIET")
                .description("Thất Chuyển: Khiếu ngạo giang sơn, áp chế toàn bộ dị thú và cương thi đối phương.").build());

        // ---------------------------------------------------------
        // 1.10 HUYẾT ĐẠO (Blood Path) - MA ĐẠO
        // ---------------------------------------------------------
        list.add(GuDefinition.builder()
                .code("HUYET_DICH_CO").name("Huyết Đích Cổ").rank(1).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(10).apCost(1).baseDamage(45).baseDefense(0).baseHeal(0)
                .foodType("Máu Tươi").maxHungerHours(24).icon("🩸").pathName("Huyết Đạo").slotCategory("CONG_KICH")
                .description("Bắn ra giọt máu sắc lẹm đâm thủng da thịt đối phương.").build());

        list.add(GuDefinition.builder()
                .code("HUYET_KHI_CO").name("Huyết Khí Cổ").rank(1).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(8).apCost(1).baseDamage(15).baseDefense(10).baseHeal(25)
                .foodType("Máu Động Vật").maxHungerHours(30).icon("🩸").pathName("Huyết Đạo").slotCategory("PHU_TRO")
                .description("Kích thích huyết khí dâng trào hồi phục nhanh vết thương nhỏ.").build());

        list.add(GuDefinition.builder()
                .code("HUYET_TRONG_CO").name("Huyết Trọng Cổ").rank(2).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(18).apCost(1).baseDamage(90).baseDefense(0).baseHeal(0)
                .foodType("Máu Đậm Đặc").maxHungerHours(36).icon("🩸").pathName("Huyết Đạo").slotCategory("CONG_KICH")
                .description("Làm máu của kẻ thù đông đặc nặng nề, giảm tốc độ và tổn thương nội tạng.").build());

        list.add(GuDefinition.builder()
                .code("HUYET_MACH_CO").name("Huyết Mạch Cổ").rank(2).type(GuType.TRINH_SAT)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(15).apCost(1).baseDamage(0).baseDefense(0).baseHeal(0)
                .foodType("Giọt Máu Thân Nhân").maxHungerHours(48).icon("🩸").pathName("Huyết Đạo").slotCategory("PHU_TRO")
                .description("Cảm ứng huyết mạch họ hàng thân tộc trong phạm vi trăm dặm.").build());

        list.add(GuDefinition.builder()
                .code("HUYET_LO_CO").name("Huyết Lô Cổ").rank(3).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(25).apCost(1).baseDamage(120).baseDefense(0).baseHeal(0)
                .foodType("Máu Người Thân Tộc").maxHungerHours(48).icon("⚱️").pathName("Huyết Đạo").slotCategory("DAC_BIET")
                .description("Tàn sát tộc nhân máu mủ ruột rà ngưng tụ huyết tẩm để cưỡng ép tăng Tư Chất Không Khiếu!").build());

        list.add(GuDefinition.builder()
                .code("TU_HUYET_CO").name("Tụ Huyết Cổ").rank(3).type(GuType.TRI_LIEU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(20).apCost(1).baseDamage(0).baseDefense(20).baseHeal(95)
                .foodType("Huyết Tinh").maxHungerHours(48).icon("💉").pathName("Huyết Đạo").slotCategory("PHU_TRO")
                .description("Hút máu tươi trên chiến trường bồi bổ thể chất Cổ sư.").build());

        list.add(GuDefinition.builder()
                .code("HUYET_DICH_TU_CO").name("Huyết Đích Tử Cổ").rank(4).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(35).apCost(1).baseDamage(280).baseDefense(0).baseHeal(0)
                .foodType("Máu Não").maxHungerHours(60).icon("🩸").pathName("Huyết Đạo").slotCategory("CONG_KICH")
                .description("Bay lượn trên không chụp đứt đầu kẻ địch hút cạn máu.").build());

        list.add(GuDefinition.builder()
                .code("HUYET_SAT_CO").name("Huyết Sát Cổ").rank(4).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(38).apCost(1).baseDamage(300).baseDefense(30).baseHeal(0)
                .foodType("Máu Sát Khí").maxHungerHours(72).icon("⚔️").pathName("Huyết Đạo").slotCategory("CONG_KICH")
                .description("Bộc phát huyết sát cuồng bạo xé rách mọi giáp trụ hộ thân.").build());

        list.add(GuDefinition.builder()
                .code("HUYET_BIEN_CO").name("Huyết Biển Cổ").rank(5).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(48).apCost(1).baseDamage(440).baseDefense(80).baseHeal(0)
                .foodType("Hồ Máu").maxHungerHours(96).icon("🌊").pathName("Huyết Đạo").slotCategory("CONG_KICH")
                .description("Dâng tràn biển máu nhấn chìm và làm tan biến thể xác kẻ thù.").build());

        list.add(GuDefinition.builder()
                .code("HUYET_THO_CO").name("Huyết Thọ Cổ").rank(5).type(GuType.TIEU_THU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(50).apCost(1).baseDamage(0).baseDefense(0).baseHeal(0)
                .consumable(true)
                .foodType("Huyết Tinh Vạn Người").maxHungerHours(9999).icon("⏳").pathName("Huyết Đạo").slotCategory("DAC_BIET")
                .description("Hút cạn huyết mạch sinh linh để cưỡng ép kéo dài tuổi thọ một cách tà ác.").build());

        list.add(GuDefinition.builder()
                .code("HUYET_HAI_TIEN_CO").name("Huyết Hải Tiên Cổ").rank(6).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(0).apCost(1).baseDamage(850).baseDefense(200).baseHeal(200)
                .foodType("Huyết Hải Tinh Hoa").maxHungerHours(99999).icon("🌊").pathName("Huyết Đạo").slotCategory("CONG_KICH")
                .description("Tiên Cổ do Huyết Thế Gia Tổ sáng tạo: Biển máu bất tận nuốt chửng đạo ngân đối phương.").build());

        list.add(GuDefinition.builder()
                .code("HUYET_MACH_TIEN_CO").name("Huyết Mạch Tiên Cổ").rank(6).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(300).baseDefense(300).baseHeal(300)
                .foodType("Chân Huyết Tiên Cổ").maxHungerHours(99999).icon("🩸").pathName("Huyết Đạo").slotCategory("PHU_TRO")
                .description("Nắm giữ cội nguồn huyết mạch vạn linh, cải tạo thể chất đạt cảnh giới bất tử.").build());

        list.add(GuDefinition.builder()
                .code("HUYET_LONG_TIEN_CO").name("Huyết Long Tiên Cổ").rank(7).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(0).apCost(1).baseDamage(1400).baseDefense(350).baseHeal(0)
                .foodType("Huyết Nhục Thái Cổ Long").maxHungerHours(99999).icon("🐉").pathName("Huyết Đạo").slotCategory("CONG_KICH")
                .description("Thất Chuyển: Rồng máu khổng lồ ngự trị cõi chết cắn nuốt vạn tiên.").build());

        // ---------------------------------------------------------
        // 1.11 CỐT ĐẠO (Bone Path)
        // ---------------------------------------------------------
        list.add(GuDefinition.builder()
                .code("BACH_COT_CO").name("Bạch Cốt Cổ").rank(1).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(10).apCost(1).baseDamage(44).baseDefense(15).baseHeal(0)
                .foodType("Bột Xương").maxHungerHours(24).icon("🦴").pathName("Cốt Đạo").slotCategory("CONG_KICH")
                .description("Mọc gai xương trắng sắc bén từ khớp ngón tay đâm xuyên da thịt.").build());

        list.add(GuDefinition.builder()
                .code("COT_THUONG_CO").name("Cốt Thương Cổ").rank(1).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(12).apCost(1).baseDamage(48).baseDefense(10).baseHeal(0)
                .foodType("Xương Lợn").maxHungerHours(30).icon("🗡️").pathName("Cốt Đạo").slotCategory("CONG_KICH")
                .description("Phóng ngọn giáo xương trắng sắc lẹm đâm thủng giáp đối phương.").build());

        list.add(GuDefinition.builder()
                .code("TOAN_CHUYEN_COT_THUONG_CO").name("Toàn Chuyển Cốt Thương Cổ").rank(2).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(22).apCost(1).baseDamage(98).baseDefense(10).baseHeal(0)
                .foodType("Ống Xương Lợn Rừng").maxHungerHours(36).icon("🗡️").pathName("Cốt Đạo").slotCategory("CONG_KICH")
                .description("Bắn thương xương xoay tròn như mũi khoan xuyên thủng mọi giáp trụ.").build());

        list.add(GuDefinition.builder()
                .code("COT_GIAP_CO").name("Cốt Giáp Cổ").rank(2).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(18).apCost(1).baseDamage(0).baseDefense(85).baseHeal(0)
                .bonusDef(30)
                .foodType("Xương Động Vật").maxHungerHours(48).icon("🛡️").pathName("Cốt Đạo").slotCategory("PHONG_NGU")
                .description("Bộ giáp xương bao bọc toàn thân, trang bị tăng +30 Phòng ngự giảm chấn thương nặng.").build());

        list.add(GuDefinition.builder()
                .code("COT_DYC_CO").name("Cốt Dực Cổ").rank(2).type(GuType.THAN_PHAP)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(20).apCost(1).baseDamage(35).baseDefense(35).baseHeal(0)
                .foodType("Cánh Dơi Cốt").maxHungerHours(40).icon("🦇").pathName("Cốt Đạo").slotCategory("THAN_PHAP")
                .description("Mọc đôi cánh xương trắng sắc lẹm lướt gió chém kẻ thù.").build());

        list.add(GuDefinition.builder()
                .code("SAM_LA_COT_TRAN_CO").name("Sâm La Cốt Trản Cổ").rank(3).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(30).apCost(1).baseDamage(55).baseDefense(160).baseHeal(0)
                .foodType("Xương Trắng Ngàn Năm").maxHungerHours(60).icon("🏰").pathName("Cốt Đạo").slotCategory("PHONG_NGU")
                .description("Dựng nên pháo đài chông xương trắng sừng sững phòng ngự tuyệt đối.").build());

        list.add(GuDefinition.builder()
                .code("BACH_COT_XA_CO").name("Bách Cốt Xa Cổ").rank(3).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(32).apCost(1).baseDamage(175).baseDefense(45).baseHeal(0)
                .foodType("Xương Hàng Trăm Người").maxHungerHours(60).icon("🛒").pathName("Cốt Đạo").slotCategory("CONG_KICH")
                .description("Xe chiến xa làm từ trăm bộ xương nghiền nát quân địch.").build());

        list.add(GuDefinition.builder()
                .code("COT_TINH_CO").name("Cốt Tinh Cổ").rank(3).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(25).apCost(1).baseDamage(60).baseDefense(60).baseHeal(0)
                .foodType("Tủy Xương").maxHungerHours(60).icon("🦴").pathName("Cốt Đạo").slotCategory("PHU_TRO")
                .description("Luyện hóa tinh hoa xương cốt gia cố khung xương bản thân.").build());

        list.add(GuDefinition.builder()
                .code("COT_THU_VUONG_CO").name("Cốt Thú Vương Cổ").rank(4).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(38).apCost(1).baseDamage(280).baseDefense(80).baseHeal(0)
                .foodType("Xương Thú Vương").maxHungerHours(72).icon("🦴").pathName("Cốt Đạo").slotCategory("CONG_KICH")
                .description("Bộ xương khổng lồ của Thú Vương càn quét trận địa.").build());

        list.add(GuDefinition.builder()
                .code("BIEN_COT_CO").name("Biến Cốt Cổ").rank(4).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(35).apCost(1).baseDamage(100).baseDefense(100).baseHeal(0)
                .foodType("Dầu Cốt").maxHungerHours(72).icon("🩻").pathName("Cốt Đạo").slotCategory("DAC_BIET")
                .description("Tùy ý biến hóa cấu trúc xương cơ thể thành các loại vũ khí sắc bén.").build());

        list.add(GuDefinition.builder()
                .code("BACH_COT_MON_CO").name("Bạch Cốt Môn Cổ").rank(5).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(48).apCost(1).baseDamage(80).baseDefense(280).baseHeal(0)
                .foodType("Cửa Xương Khổng Lồ").maxHungerHours(96).icon("🚪").pathName("Cốt Đạo").slotCategory("PHONG_NGU")
                .description("Cánh cổng xương trắng khổng lồ mở ra ngăn chặn mọi đòn tấn công hủy diệt.").build());

        list.add(GuDefinition.builder()
                .code("THAI_CO_COT_LONG_CO").name("Thái Cổ Cốt Long Cổ").rank(5).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_TRUNG).primevalCostPercent(50).apCost(1).baseDamage(450).baseDefense(120).baseHeal(0)
                .foodType("Long Cốt Thái Cổ").maxHungerHours(96).icon("🐉").pathName("Cốt Đạo").slotCategory("CONG_KICH")
                .description("Bộ xương rồng thái cổ bay lượn trên trời giáng đòn hủy diệt.").build());

        list.add(GuDefinition.builder()
                .code("BACH_COT_TIEN_DINH_CO").name("Bạch Cốt Tiên Đình Cổ").rank(6).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(400).baseDefense(800).baseHeal(0)
                .foodType("Tiên Cốt").maxHungerHours(99999).icon("🏛️").pathName("Cốt Đạo").slotCategory("PHONG_NGU")
                .description("Tiên đình cung điện bằng xương trắng bất khả xâm phạm.").build());

        list.add(GuDefinition.builder()
                .code("COT_VUONG_TIEN_CO").name("Cốt Vương Tiên Cổ").rank(7).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(1300).baseDefense(450).baseHeal(0)
                .foodType("Đạo Ngân Cốt Đạo").maxHungerHours(99999).icon("👑").pathName("Cốt Đạo").slotCategory("CONG_KICH")
                .description("Chúa tể xương cốt vạn cổ thống lĩnh thế giới bạch cốt.").build());

        // ---------------------------------------------------------
        // 1.12 THÂU ĐẠO (Theft Path)
        // ---------------------------------------------------------
        list.add(GuDefinition.builder()
                .code("THAU_KHI_CO").name("Thâu Khí Cổ").rank(2).type(GuType.CONG_KICH)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(12).apCost(1).baseDamage(40).baseDefense(10).baseHeal(0)
                .foodType("Chân Nguyên Thạch").maxHungerHours(36).icon("🥷").pathName("Thâu Đạo").slotCategory("CONG_KICH")
                .description("Ăn trộm Chân Nguyên trực tiếp từ Không Khiếu đối phương trong trận đấu.").build());

        list.add(GuDefinition.builder()
                .code("THAU_TAM_CO").name("Thâu Tâm Cổ").rank(3).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(24).apCost(1).baseDamage(65).baseDefense(25).baseHeal(0)
                .foodType("Ý Niệm Vui Buồn").maxHungerHours(48).icon("🎭").pathName("Thâu Đạo").slotCategory("PHU_TRO")
                .description("Trộm đoạt tâm niệm đối thủ, khiến chúng mất tập trung và rơi vào hoảng loạn.").build());

        list.add(GuDefinition.builder()
                .code("AN_Y_CO").name("Ẩn Ý Cổ").rank(3).type(GuType.THAN_PHAP)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(20).apCost(1).baseDamage(20).baseDefense(40).baseHeal(0)
                .foodType("Bóng Tối").maxHungerHours(48).icon("👤").pathName("Thâu Đạo").slotCategory("THAN_PHAP")
                .description("Ẩn giấu ý niệm và hành tung không để bất kỳ ai phát giác.").build());

        list.add(GuDefinition.builder()
                .code("HAI_DAO_CO").name("Hái Đào Cổ").rank(4).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(35).apCost(1).baseDamage(75).baseDefense(35).baseHeal(0)
                .foodType("Đào Tiên Trộm").maxHungerHours(60).icon("🍑").pathName("Thâu Đạo").slotCategory("DAC_BIET")
                .description("Bóp hạ bộ/cướp đồ: Thò tay cướp Cổ trùng trực tiếp từ Không Khiếu của địch!").build());

        list.add(GuDefinition.builder()
                .code("DAO_THIEN_TIEN_CO").name("Đạo Thiên Tiên Cổ").rank(6).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(600).baseDefense(400).baseHeal(0)
                .foodType("Đạo Ngân Thâu Đạo").maxHungerHours(99999).icon("🥷").pathName("Thâu Đạo").slotCategory("DAC_BIET")
                .description("Do Đạo Thiên Ma Tôn sáng tạo: Trộm cắp đạo ngân và pháp tắc thiên địa.").build());

        list.add(GuDefinition.builder()
                .code("THAU_DAO_TIEN_CO").name("Thâu Đạo Tiên Cổ").rank(7).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(1000).baseDefense(500).baseHeal(0)
                .foodType("Chân Nguyên Tiên Khiếu").maxHungerHours(99999).icon("🌟").pathName("Thâu Đạo").slotCategory("DAC_BIET")
                .description("Cướp Cổ trùng, Đạo Ngân và Chân Nguyên trực tiếp từ Tiên Khiếu kẻ địch!").build());

        // ---------------------------------------------------------
        // 1.13 THỰC ĐẠO (Food Path)
        // ---------------------------------------------------------
        list.add(GuDefinition.builder()
                .code("BU_DAP_CO").name("Bù Đắp Cổ").rank(1).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(5).apCost(1).baseDamage(0).baseDefense(10).baseHeal(30)
                .foodType("Thức Ăn Thô").maxHungerHours(48).icon("🥣").pathName("Thực Đạo").slotCategory("PHU_TRO")
                .description("Hấp thụ dinh dưỡng bù đắp thể lực hao tổn của Cổ sư.").build());

        list.add(GuDefinition.builder()
                .code("TU_TRUNG").name("Tửu Trùng").rank(2).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(0).baseDefense(15).baseHeal(0)
                .foodType("Hương Tửu Thơm").maxHungerHours(48).icon("🍶").pathName("Thực Đạo").slotCategory("PHU_TRO")
                .description("Tửu Trùng Nhị Chuyển: Tinh luyện chân nguyên lên 1 tiểu cảnh giới. Khi trang bị sẽ tăng mạnh +35 điểm tiến độ cọ rửa vách Không Khiếu!").build());

        list.add(GuDefinition.builder()
                .code("TU_VI_TUU_TRUNG").name("Tứ Vị Tửu Trùng").rank(3).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(0).baseDefense(30).baseHeal(0)
                .foodType("Tứ Đại Danh Tửu").maxHungerHours(72).icon("🍷").pathName("Thực Đạo").slotCategory("PHU_TRO")
                .description("Tứ Vị Tửu Trùng Tam Chuyển: Tinh luyện chân nguyên cao cấp. Khi trang bị sẽ tăng mạnh +50 điểm tiến độ cọ rửa vách Không Khiếu!").build());

        list.add(GuDefinition.builder()
                .code("THUC_LUONG_CO").name("Thực Lương Cổ").rank(2).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(8).apCost(1).baseDamage(0).baseDefense(20).baseHeal(40)
                .foodType("Lương Thực").maxHungerHours(60).icon("🌾").pathName("Thực Đạo").slotCategory("PHU_TRO")
                .description("Tạo ra lương thực nuôi sống bản thân và đàn Cổ trùng.").build());

        list.add(GuDefinition.builder()
                .code("VAN_VI_CO").name("Vạn Vị Cổ").rank(4).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(25).apCost(1).baseDamage(50).baseDefense(50).baseHeal(80)
                .foodType("Gia Vị Vạn Vật").maxHungerHours(72).icon("🍲").pathName("Thực Đạo").slotCategory("PHU_TRO")
                .description("Nếm trải vạn vị càn khôn bồi bổ toàn diện kinh mạch thể phách.").build());

        list.add(GuDefinition.builder()
                .code("AN_DAT_TIEN_CO").name("Ăn Đất Tiên Cổ").rank(6).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(200).baseDefense(400).baseHeal(0)
                .foodType("Đất Màu Mỡ").maxHungerHours(99999).icon("🌱").pathName("Thực Đạo").slotCategory("PHU_TRO")
                .description("Ăn đất để duy trì sự sống nuôi dưỡng Tiên Cổ mà không lo tốn tài nguyên.").build());

        list.add(GuDefinition.builder()
                .code("AN_DONG_TIEN_CO").name("Ăn Đóng Tiên Cổ").rank(6).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(200).baseDefense(400).baseHeal(0)
                .foodType("Băng Giá").maxHungerHours(99999).icon("❄️").pathName("Thực Đạo").slotCategory("PHU_TRO")
                .description("Hấp thụ hàn khí băng đóng nuôi sống các Tiên Cổ Thủy Băng Đạo.").build());

        list.add(GuDefinition.builder()
                .code("TICH_THUC_TIEN_CO").name("Tích Thực Tiên Cổ").rank(6).type(GuType.PHU_TRO)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(150).baseDefense(350).baseHeal(200)
                .foodType("Mọi Thức Ăn").maxHungerHours(99999).icon("🍱").pathName("Thực Đạo").slotCategory("PHU_TRO")
                .description("Tích trữ lương thực vô tận giải quyết triệt để bài toán thức ăn của Tiên Khiếu.").build());

        // ---------------------------------------------------------
        // 1.14 TÍN ĐẠO / TRINH SÁT (Information & Scout Path)
        // ---------------------------------------------------------
        list.add(GuDefinition.builder()
                .code("DIA_THINH_NHUC_NHI_THAO").name("Địa Thính Nhục Nhĩ Thảo").rank(1).type(GuType.TRINH_SAT)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(5).apCost(1).baseDamage(0).baseDefense(0).baseHeal(0)
                .foodType("Thịt Tươi").maxHungerHours(72).icon("👂").pathName("Trinh Sát").slotCategory("PHU_TRO")
                .description("Tai thịt bám rễ vào đất, nghe trộm vạn vật và nhìn thấu bài ngửa của đối phương.").build());

        list.add(GuDefinition.builder()
                .code("TAM_TUNG_CO").name("Tầm Tung Cổ").rank(1).type(GuType.TRINH_SAT)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(8).apCost(1).baseDamage(0).baseDefense(10).baseHeal(0)
                .foodType("Khói Thơm").maxHungerHours(36).icon("🧭").pathName("Trinh Sát").slotCategory("PHU_TRO")
                .description("Lần theo hơi thở dấu vết mục tiêu trong phạm vi trăm dặm.").build());

        list.add(GuDefinition.builder()
                .code("TIN_COC_CO").name("Tín Cốc Cổ").rank(1).type(GuType.TRINH_SAT)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(6).apCost(1).baseDamage(0).baseDefense(0).baseHeal(0)
                .foodType("Thóc Gạo").maxHungerHours(48).icon("🕊️").pathName("Tín Đạo").slotCategory("PHU_TRO")
                .description("Chim bồ câu truyền tin cậy bay nhanh như chớp.").build());

        list.add(GuDefinition.builder()
                .code("DINH_HUONG_CO").name("Định Hướng Cổ").rank(2).type(GuType.TRINH_SAT)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(10).apCost(1).baseDamage(0).baseDefense(15).baseHeal(0)
                .foodType("Từ Thạch").maxHungerHours(48).icon("🧭").pathName("Trinh Sát").slotCategory("PHU_TRO")
                .description("La bàn thần bí chỉ rõ phương hướng trong sương mù và mê trận.").build());

        list.add(GuDefinition.builder()
                .code("HOI_THO_TRINH_SAT_CO").name("Hơi Thở Trinh Sát Cổ").rank(2).type(GuType.TRINH_SAT)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(12).apCost(1).baseDamage(0).baseDefense(10).baseHeal(0)
                .foodType("Khí Lưu").maxHungerHours(36).icon("👃").pathName("Trinh Sát").slotCategory("PHU_TRO")
                .description("Ngửi thấy hơi thở Cổ trùng ẩn giấu của kẻ thù xung quanh.").build());

        list.add(GuDefinition.builder()
                .code("TRUY_HON_CO").name("Truy Hồn Cổ").rank(3).type(GuType.TRINH_SAT)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(22).apCost(1).baseDamage(30).baseDefense(15).baseHeal(0)
                .foodType("Hồn Phách Sa").maxHungerHours(48).icon("👻").pathName("Trinh Sát").slotCategory("PHU_TRO")
                .description("Khóa chặt dao động linh hồn mục tiêu, chạy trốn tới chân trời góc bể cũng bị tìm ra.").build());

        list.add(GuDefinition.builder()
                .code("THIEN_NHAN_CO").name("Thiên Nhãn Cổ").rank(3).type(GuType.TRINH_SAT)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(20).apCost(1).baseDamage(15).baseDefense(25).baseHeal(0)
                .foodType("Giọt Lệ Phượng Hoàng").maxHungerHours(48).icon("👁️").pathName("Trinh Sát").slotCategory("PHU_TRO")
                .description("Mắt trời nhìn thấu mọi ngụy trang, vô hiệu hóa sát chiêu tàng hình.").build());

        list.add(GuDefinition.builder()
                .code("DONG_MINH_THE_DOC_CO").name("Đồng Minh Thề Độc Cổ").rank(3).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(25).apCost(1).baseDamage(0).baseDefense(0).baseHeal(0)
                .foodType("Máu Thề").maxHungerHours(9999).icon("📜").pathName("Tín Đạo").slotCategory("DAC_BIET")
                .description("Lập thề độc liên minh, kẻ nào bội phản lập tức chịu vạn độc cắn xé tim gan.").build());

        list.add(GuDefinition.builder()
                .code("HAI_THE_SON_MINH_CO").name("Hải Thệ Sơn Minh Cổ").rank(4).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(35).apCost(1).baseDamage(0).baseDefense(50).baseHeal(0)
                .foodType("Đá Biển Ngàn Năm").maxHungerHours(9999).icon("⛰️").pathName("Tín Đạo").slotCategory("DAC_BIET")
                .description("Khóa chặt giao kèo liên minh vĩnh cửu, vi phạm sẽ bị đạo ngân thiên địa trừng phạt.").build());

        list.add(GuDefinition.builder()
                .code("VAN_LY_TIN_CO").name("Vạn Lý Tín Cổ").rank(5).type(GuType.TRINH_SAT)
                .optimalDistance(CombatDistance.TAM_XA).primevalCostPercent(40).apCost(1).baseDamage(0).baseDefense(0).baseHeal(0)
                .foodType("Ánh Trăng").maxHungerHours(96).icon("🦅").pathName("Tín Đạo").slotCategory("PHU_TRO")
                .description("Truyền tin tức vượt vạn dặm tức thời giữa các sơn trại và liên minh.").build());

        list.add(GuDefinition.builder()
                .code("THUC_NGON_CO").name("Thực Ngôn Cổ").rank(5).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(45).apCost(1).baseDamage(0).baseDefense(0).baseHeal(0)
                .foodType("Lời Thề Độc").maxHungerHours(96).icon("👅").pathName("Tín Đạo").slotCategory("DAC_BIET")
                .description("Nuốt lời và giải hóa toàn bộ lời thề độc để đâm lén đồng minh mà không bị phản phệ!").build());

        list.add(GuDefinition.builder()
                .code("TIN_DAO_TIEN_CO").name("Tín Đạo Tiên Cổ").rank(6).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(200).baseDefense(400).baseHeal(0)
                .foodType("Thiên Địa Tín Khí").maxHungerHours(99999).icon("📜").pathName("Tín Đạo").slotCategory("DAC_BIET")
                .description("Tiên Cổ giao kèo tối cao giữa các bậc Cổ Tiên.").build());

        list.add(GuDefinition.builder()
                .code("THIEN_HA_TRI_TIEN_CO").name("Thiên Hạ Tri Tiên Cổ").rank(7).type(GuType.TRINH_SAT)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(500).baseDefense(500).baseHeal(0)
                .foodType("Tin Đồn Thiên Hạ").maxHungerHours(99999).icon("🌐").pathName("Tín Đạo").slotCategory("PHU_TRO")
                .description("Mọi bí mật dưới gầm trời thiên hạ đều không thể thoát khỏi tai mắt Tiên Cổ.").build());

        // ---------------------------------------------------------
        // 1.15 PHÒNG NGỰ, TRỊ LIỆU & ĐẶC BIỆT (Support, Defense, Relic)
        // ---------------------------------------------------------
        // Phòng Ngự
        list.add(GuDefinition.builder()
                .code("BACH_NGOC_CO").name("Bạch Ngọc Cổ").rank(2).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(15).apCost(1).baseDamage(0).baseDefense(85).baseHeal(0)
                .bonusDef(30)
                .foodType("Bạch Ngọc Thạch").maxHungerHours(36).icon("🛡️").pathName("Phòng Ngự").slotCategory("PHONG_NGU")
                .description("Toàn thân bao phủ bạch ngọc tinh khiết, trang bị tăng +30 Phòng ngự chặn đứng đao kiếm.").build());

        list.add(GuDefinition.builder()
                .code("THIEN_BONG_CO").name("Thiên Bồng Cổ").rank(3).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(22).apCost(1).baseDamage(0).baseDefense(145).baseHeal(0)
                .bonusDef(60)
                .foodType("Thịt Lợn Đen").maxHungerHours(36).icon("🥋").pathName("Phòng Ngự").slotCategory("PHONG_NGU")
                .description("Áo giáp ánh sáng Thiên Bồng bảo hộ toàn diện, trang bị tăng +60 Phòng ngự.").build());

        list.add(GuDefinition.builder()
                .code("KIM_CHUNG_TRAO_CO").name("Kim Chung Tráo Cổ").rank(4).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(32).apCost(1).baseDamage(0).baseDefense(220).baseHeal(0)
                .bonusDef(100)
                .foodType("Vàng Ròng").maxHungerHours(48).icon("🔔").pathName("Phòng Ngự").slotCategory("PHONG_NGU")
                .description("Chuông vàng úp xuống, vạn tiễn nan khai, trang bị tăng +100 Phòng ngự kim cương bất hoại.").build());

        list.add(GuDefinition.builder()
                .code("CUONG_THIET_THUAN_CO").name("Cương Thiết Thuẫn Cổ").rank(5).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(42).apCost(1).baseDamage(30).baseDefense(350).baseHeal(0)
                .bonusDef(150)
                .foodType("Quặng Thép").maxHungerHours(60).icon("🛡️").pathName("Phòng Ngự").slotCategory("PHONG_NGU")
                .description("Tấm khiên thép kiên cố đỡ đòn đánh trực diện, trang bị tăng +150 Phòng ngự.").build());

        list.add(GuDefinition.builder()
                .code("LAI_THO_CAP_MO").name("Lại Thổ Cáp Mô").rank(2).type(GuType.PHONG_NGU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(18).apCost(1).baseDamage(20).baseDefense(75).baseHeal(0)
                .bonusDef(25)
                .foodType("Bùn Đầm Lầy").maxHungerHours(48).icon("🐸").pathName("Phòng Ngự").slotCategory("PHONG_NGU")
                .description("Cóc bùn lười biếng ngủ đông trong đá cổ, có khả năng phòng ngự và phản đòn.").build());

        // Trị Liệu (Consumable & Equipable)
        list.add(GuDefinition.builder()
                .code("SINH_CO_DIEP").name("Sinh Cơ Diệp").rank(1).type(GuType.TIEU_THU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(10).apCost(1).baseDamage(0).baseDefense(0).baseHeal(60)
                .consumable(true)
                .foodType("Lá Ngô Đồng").maxHungerHours(24).icon("🍃").pathName("Trị Liệu").slotCategory("PHU_TRO")
                .description("Cổ trùng tiêu thụ: Lá xanh tươi tốt dán lên da thịt, nhanh chóng cầm máu và hồi 100% Khí Huyết.").build());

        list.add(GuDefinition.builder()
                .code("HOI_XUAN_CO").name("Hồi Xuân Cổ").rank(2).type(GuType.TIEU_THU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(15).apCost(1).baseDamage(0).baseDefense(0).baseHeal(120)
                .consumable(true)
                .foodType("Nhựa Cây Cổ Thụ").maxHungerHours(36).icon("🌸").pathName("Trị Liệu").slotCategory("PHU_TRO")
                .description("Cổ trùng tiêu thụ: Bùng nổ sinh cơ mùa xuân, nhanh chóng hồi phục trọn vẹn Khí Huyết và Chân Nguyên.").build());

        list.add(GuDefinition.builder()
                .code("CUU_TU_NHAT_SINH_CO").name("Cửu Tử Nhất Sinh Cổ").rank(4).type(GuType.TIEU_THU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(25).apCost(1).baseDamage(0).baseDefense(0).baseHeal(300)
                .consumable(true)
                .foodType("Giọt Lệ Sinh Mệnh").maxHungerHours(72).icon("❤️‍🩹").pathName("Trị Liệu").slotCategory("PHU_TRO")
                .description("Cổ trùng tiêu thụ: Thần dược cứu mạng, hồi sinh từ cõi chết hồi phục 100% Khí Huyết trong giây phút nguy kịch.").build());

        list.add(GuDefinition.builder()
                .code("TINH_HOA_CO").name("Tịnh Hóa Cổ").rank(3).type(GuType.TRI_LIEU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(25).apCost(1).baseDamage(0).baseDefense(35).baseHeal(85)
                .foodType("Nước Mưa Đầu Mùa").maxHungerHours(48).icon("✨").pathName("Trị Liệu").slotCategory("PHU_TRO")
                .description("Thanh tẩy toàn bộ độc tố, tà niệm và trạng thái bất lợi.").build());

        // ĐẶC BIỆT: XÁ LỢI CỔ TỪ 1-5 CHUYỂN (TIÊU THỤ: ÉP TĂNG 1 TIỂU CẢNH GIỚI)
        list.add(GuDefinition.builder()
                .code("THANH_DONG_XA_LOI_CO").name("Thanh Đồng Xá Lợi Cổ").rank(1).type(GuType.TIEU_THU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(0).baseDefense(0).baseHeal(0)
                .consumable(true)
                .foodType("Hương Khói Trầm").maxHungerHours(9999).icon("🟢").pathName("Đặc Biệt").slotCategory("DAC_BIET")
                .description("Cổ trùng tiêu thụ quý hiếm: Ép tăng 1 tiểu cảnh giới tu vi trực tiếp cho Cổ sư Nhất Chuyển!").build());

        list.add(GuDefinition.builder()
                .code("XICH_THIET_XA_LOI_CO").name("Xích Thiết Xá Lợi Cổ").rank(2).type(GuType.TIEU_THU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(0).baseDefense(0).baseHeal(0)
                .consumable(true)
                .foodType("Hương Khói Trầm").maxHungerHours(9999).icon("🔴").pathName("Đặc Biệt").slotCategory("DAC_BIET")
                .description("Cổ trùng tiêu thụ quý hiếm: Ép tăng 1 tiểu cảnh giới tu vi trực tiếp cho Cổ sư Nhị Chuyển!").build());

        list.add(GuDefinition.builder()
                .code("BACH_NGAN_XA_LOI_CO").name("Bạch Ngân Xá Lợi Cổ").rank(3).type(GuType.TIEU_THU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(0).baseDefense(0).baseHeal(0)
                .consumable(true)
                .foodType("Hương Khói Trầm").maxHungerHours(9999).icon("⚪").pathName("Đặc Biệt").slotCategory("DAC_BIET")
                .description("Cổ trùng tiêu thụ quý hiếm: Ép tăng 1 tiểu cảnh giới tu vi trực tiếp cho Cổ sư Tam Chuyển!").build());

        list.add(GuDefinition.builder()
                .code("HOANG_KIM_XA_LOI_CO").name("Hoàng Kim Xá Lợi Cổ").rank(4).type(GuType.TIEU_THU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(0).baseDefense(0).baseHeal(0)
                .consumable(true)
                .foodType("Hương Khói Trầm").maxHungerHours(9999).icon("🟡").pathName("Đặc Biệt").slotCategory("DAC_BIET")
                .description("Cổ trùng tiêu thụ quý hiếm: Ép tăng 1 tiểu cảnh giới tu vi trực tiếp cho Cổ sư Tứ Chuyển!").build());

        list.add(GuDefinition.builder()
                .code("TU_TINH_XA_LOI_CO").name("Tử Tinh Xá Lợi Cổ").rank(5).type(GuType.TIEU_THU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(0).baseDefense(0).baseHeal(0)
                .consumable(true)
                .foodType("Hương Khói Trầm").maxHungerHours(9999).icon("🟣").pathName("Đặc Biệt").slotCategory("DAC_BIET")
                .description("Cổ trùng tiêu thụ chí bảo: Ép tăng 1 tiểu cảnh giới tu vi trực tiếp cho Cổ sư Ngũ Chuyển!").build());

        list.add(GuDefinition.builder()
                .code("XA_LOI_CO").name("Xá Lợi Cổ").rank(3).type(GuType.TIEU_THU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(0).baseDefense(0).baseHeal(0)
                .consumable(true)
                .foodType("Hương Khói Trầm").maxHungerHours(9999).icon("🔮").pathName("Đặc Biệt").slotCategory("DAC_BIET")
                .description("Cổ trùng tiêu thụ: Đột phá tiểu cảnh giới tu vi trong chớp mắt mà không cần cọ rửa vách khiếu.").build());

        list.add(GuDefinition.builder()
                .code("THO_CO").name("Thọ Cổ").rank(3).type(GuType.TIEU_THU)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(0).apCost(1).baseDamage(0).baseDefense(0).baseHeal(0)
                .consumable(true)
                .foodType("Thiên Địa Thọ Khí").maxHungerHours(99999).icon("⏳").pathName("Đặc Biệt").slotCategory("DAC_BIET")
                .description("Cổ trùng tiêu thụ vô giá: Tăng vĩnh viễn 20 năm tuổi thọ mà không bị bất kỳ phản phệ hay tai kiếp nào.").build());

        list.add(GuDefinition.builder()
                .code("NHAN_BI_CO").name("Nhân Bì Cổ").rank(3).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(20).apCost(1).baseDamage(0).baseDefense(20).baseHeal(0)
                .foodType("Da Người").maxHungerHours(48).icon("🎭").pathName("Đặc Biệt").slotCategory("DAC_BIET")
                .description("Cải trang hoàn hảo, lột da giả dạng dung mạo và khí tức của NPC khác.").build());

        list.add(GuDefinition.builder()
                .code("SUC_COT_CO").name("Súc Cốt Cổ").rank(2).type(GuType.DAC_BIET)
                .optimalDistance(CombatDistance.CAN_CHIEN).primevalCostPercent(15).apCost(1).baseDamage(0).baseDefense(25).baseHeal(0)
                .foodType("Giấm Gạo Lâu Năm").maxHungerHours(36).icon("🩻").pathName("Đặc Biệt").slotCategory("DAC_BIET")
                .description("Thu nhỏ vóc dáng, đổi hình dạng khung xương để ngụy trang và né tránh sát thương hiểm.").build());

        return list;
    }

    private void syncRecipes() {
        List<GuRecipe> recipes = List.of(
                // ==================== 1. NGUYỆT ĐẠO (MOON PATH) ====================
                GuRecipe.builder()
                        .recipeName("Hợp luyện Nguyệt Mang Cổ")
                        .primaryGuCode("NGUYET_QUANG_CO").secondaryGuCode("TU_TRUNG")
                        .materialName("Ngọc Thạch Phấn").environmentCondition("Ban đêm")
                        .resultGuCode("NGUYET_MANG_CO")
                        .stoneCost(35).successRate(75).backlashDamage(25).isUnlocked(true).build(),

                // ==================== 2. LỰC ĐẠO (STRENGTH PATH) ====================
                // Rank 2: Tứ Tí Cổ
                GuRecipe.builder()
                        .recipeName("Lực Đạo Biến Hình: Tứ Tí Cổ")
                        .primaryGuCode("HAC_THI_CO").secondaryGuCode("THIET_BI_CO")
                        .materialName("Huyết Bì Thạch").environmentCondition("Bình thường")
                        .resultGuCode("TU_TI_CO")
                        .stoneCost(35).successRate(75).backlashDamage(20).isUnlocked(true).build(),

                // Rank 2: Hùng Lực Cổ
                GuRecipe.builder()
                        .recipeName("Dã Thú Uy Lực: Hùng Lực Cổ")
                        .primaryGuCode("THANH_TRU_CO").secondaryGuCode("MANH_HO_CO")
                        .materialName("Hùng Đảm Sa").environmentCondition("Bình thường")
                        .resultGuCode("HUNG_LUC_CO")
                        .stoneCost(35).successRate(75).backlashDamage(20).isUnlocked(true).build(),

                // Rank 3: Bách Nhân Lực Cổ
                GuRecipe.builder()
                        .recipeName("Lực Đạo Thăng Hoa: Bách Nhân Lực")
                        .primaryGuCode("HAC_THI_CO").secondaryGuCode("BACH_THI_CO")
                        .materialName("Huyết Lợn Rừng").environmentCondition("Đã tích đủ Đạo Ngân")
                        .resultGuCode("BACH_NHAN_LUC_CO")
                        .stoneCost(45).successRate(70).backlashDamage(25).isUnlocked(true).build(),

                // Rank 3: Ngưu Lực Cổ
                GuRecipe.builder()
                        .recipeName("Cương Cốt Lực: Ngưu Lực Cổ")
                        .primaryGuCode("BACH_THI_CO").secondaryGuCode("THANH_TRU_CO")
                        .materialName("Cốt Tủy Bò Rừng").environmentCondition("Bình thường")
                        .resultGuCode("NGUU_LUC_CO")
                        .stoneCost(45).successRate(70).backlashDamage(25).isUnlocked(true).build(),

                // Rank 3: Mã Lực Cổ
                GuRecipe.builder()
                        .recipeName("Trường Cường Hãn Lực: Mã Lực Cổ")
                        .primaryGuCode("MANH_HO_CO").secondaryGuCode("DANG_KHONG_CO")
                        .materialName("Thiên Lý Mã Cốt").environmentCondition("Bình thường")
                        .resultGuCode("MA_LUC_CO")
                        .stoneCost(45).successRate(70).backlashDamage(25).isUnlocked(true).build(),

                // Rank 3: Chu Tước Lực Cổ
                GuRecipe.builder()
                        .recipeName("Hỏa Diễm Thần Lực: Chu Tước Lực Cổ")
                        .primaryGuCode("HUNG_LUC_CO").secondaryGuCode("LIET_HOA_CO")
                        .materialName("Xích Tước Lông Vũ").environmentCondition("Trong hầm lửa")
                        .resultGuCode("CHU_TUOC_LUC_CO")
                        .stoneCost(50).successRate(65).backlashDamage(30).isUnlocked(true).build(),

                // Rank 4: Thiên Nhân Lực Cổ
                GuRecipe.builder()
                        .recipeName("Cự Lực Đỉnh Phong: Thiên Nhân Lực")
                        .primaryGuCode("BACH_NHAN_LUC_CO").secondaryGuCode("NGUU_LUC_CO")
                        .materialName("Vạn Cổ Thần Sa").environmentCondition("Đã tích đủ Đạo Ngân")
                        .resultGuCode("THIEN_NHAN_LUC_CO")
                        .stoneCost(70).successRate(65).backlashDamage(35).isUnlocked(true).build(),

                // Rank 4: Lực Đạo Giác Long Cổ
                GuRecipe.builder()
                        .recipeName("Long Lực Hóa Hình: Lực Đạo Giác Long")
                        .primaryGuCode("MA_LUC_CO").secondaryGuCode("CHU_TUOC_LUC_CO")
                        .materialName("Chân Long Chi Tủy").environmentCondition("Trên đỉnh núi cao")
                        .resultGuCode("LUC_DAO_GIAC_LONG_CO")
                        .stoneCost(75).successRate(60).backlashDamage(40).isUnlocked(true).build(),

                // Rank 4: Tứ Sâm Lực Cổ
                GuRecipe.builder()
                        .recipeName("Dưỡng Khí Tráng Thể: Tứ Sâm Lực Cổ")
                        .primaryGuCode("THIEN_NHAN_LUC_CO").secondaryGuCode("TU_TI_CO")
                        .materialName("Ngàn Năm Nhân Sâm Tinh").environmentCondition("Bình thường")
                        .resultGuCode("TU_SAM_LUC_CO")
                        .stoneCost(80).successRate(60).backlashDamage(40).isUnlocked(true).build(),

                // Rank 5: Vạn Nhân Lực Cổ
                GuRecipe.builder()
                        .recipeName("Vạn Tượng Quy Nhất: Vạn Nhân Lực")
                        .primaryGuCode("THIEN_NHAN_LUC_CO").secondaryGuCode("LUC_DAO_GIAC_LONG_CO")
                        .materialName("Thiên Cương Thạch Tủy").environmentCondition("Đã tích đủ Đạo Ngân")
                        .resultGuCode("VAN_NHAN_LUC_CO")
                        .stoneCost(120).successRate(55).backlashDamage(50).isUnlocked(true).build(),

                // Rank 5: Bạt Sơn Cổ
                GuRecipe.builder()
                        .recipeName("Đại Lực Thần Lực: Bạt Sơn Cổ")
                        .primaryGuCode("VAN_NHAN_LUC_CO").secondaryGuCode("TU_SAM_LUC_CO")
                        .materialName("Thạch Tủy Ngũ Nhạc").environmentCondition("Trên đỉnh núi cao")
                        .resultGuCode("BAT_SON_CO")
                        .stoneCost(130).successRate(55).backlashDamage(55).isUnlocked(true).build(),

                // Rank 5: Đảo Hải Cổ
                GuRecipe.builder()
                        .recipeName("Phúc Địa Chấn Ba: Đảo Hải Cổ")
                        .primaryGuCode("VAN_NHAN_LUC_CO").secondaryGuCode("TSUNAMI_CO")
                        .materialName("Hải Nhãn Tinh Tuyền").environmentCondition("Hồ nước tĩnh")
                        .resultGuCode("DAO_HAI_CO")
                        .stoneCost(140).successRate(50).backlashDamage(60).isUnlocked(true).build(),

                // Rank 6: Tiên Cổ Tự Lực
                GuRecipe.builder()
                        .recipeName("Tiên Đạo Thăng Hoa: Cổ Tự Lực (Tiên Cổ)")
                        .primaryGuCode("BAT_SON_CO").secondaryGuCode("DAO_HAI_CO")
                        .materialName("Thái Cổ Hồng Hoang Thạch").environmentCondition("Đã tích đủ Đạo Ngân")
                        .resultGuCode("CO_TU_LUC")
                        .stoneCost(500).successRate(25).backlashDamage(150).isUnlocked(true).build(),

                // ==================== 3. BIẾN HÓA ĐẠO (TRANSFORMATION PATH) ====================
                // Rank 2: Ưng Dực Cổ
                GuRecipe.builder()
                        .recipeName("Hóa Cánh Phi Hành: Ưng Dực Cổ")
                        .primaryGuCode("CHU_DYC_CO").secondaryGuCode("TAT_PHONG_CO")
                        .materialName("Lông Vũ Kim Sí Điểu").environmentCondition("Trên đỉnh núi cao")
                        .resultGuCode("UNG_DYC_CO")
                        .stoneCost(40).successRate(75).backlashDamage(20).isUnlocked(true).build(),

                // Rank 3: Hùng Biến Cổ
                GuRecipe.builder()
                        .recipeName("Hóa Thân Man Hoang: Hùng Biến Cổ")
                        .primaryGuCode("XA_BIEN_CO").secondaryGuCode("HUNG_LUC_CO")
                        .materialName("Gấu Hùng Tinh Huyết").environmentCondition("Bình thường")
                        .resultGuCode("HUNG_BIEN_CO")
                        .stoneCost(50).successRate(70).backlashDamage(25).isUnlocked(true).build(),

                // Rank 3: Giao Long Biến Cổ
                GuRecipe.builder()
                        .recipeName("Giao Thể Thần Uy: Giao Long Biến Cổ")
                        .primaryGuCode("LANG_BIEN_CO").secondaryGuCode("THUY_LONG_CO")
                        .materialName("Giao Tê Huyết Tinh").environmentCondition("Hồ nước tĩnh")
                        .resultGuCode("GIAO_LONG_BIEN_CO")
                        .stoneCost(55).successRate(65).backlashDamage(30).isUnlocked(true).build(),

                // Rank 4: Giác Long Biến Cổ
                GuRecipe.builder()
                        .recipeName("Hóa Long Đằng Vân: Giác Long Biến Cổ")
                        .primaryGuCode("GIAO_LONG_BIEN_CO").secondaryGuCode("NGU_LAN_CO")
                        .materialName("Long Lân Tinh Sa").environmentCondition("Đã tích đủ Đạo Ngân")
                        .resultGuCode("GIAC_LONG_BIEN_CO")
                        .stoneCost(80).successRate(60).backlashDamage(40).isUnlocked(true).build(),

                // Rank 4: Áo Mãng Biến Cổ
                GuRecipe.builder()
                        .recipeName("Cự Mãng Thôn Thiên: Áo Mãng Biến Cổ")
                        .primaryGuCode("GIAO_LONG_BIEN_CO").secondaryGuCode("HUNG_BIEN_CO")
                        .materialName("Cự Mãng Thạch Bì").environmentCondition("Ban đêm")
                        .resultGuCode("AO_MANG_BIEN_CO")
                        .stoneCost(80).successRate(60).backlashDamage(40).isUnlocked(true).build(),

                // Rank 5: Biến Hóa Vạn Vật Cổ
                GuRecipe.builder()
                        .recipeName("Thiên Địa Huyền Cơ: Biến Hóa Vạn Vật")
                        .primaryGuCode("GIAC_LONG_BIEN_CO").secondaryGuCode("AO_MANG_BIEN_CO")
                        .materialName("Vạn Hóa Thần Tủy").environmentCondition("Đã tích đủ Đạo Ngân")
                        .resultGuCode("BIEN_HOA_VAN_VAT_CO")
                        .stoneCost(160).successRate(50).backlashDamage(65).isUnlocked(true).build(),

                // ==================== 4. VIÊM ĐẠO (FIRE PATH) ====================
                // Rank 2: Bạo Viêm Cổ
                GuRecipe.builder()
                        .recipeName("Viêm Đạo Trùng Điệp: Bạo Viêm Cổ")
                        .primaryGuCode("HOA_CAU_CO").secondaryGuCode("LIET_HOA_CO")
                        .materialName("Than Dung Nham").environmentCondition("Trong hầm lửa")
                        .resultGuCode("BAO_VIEM_CO")
                        .stoneCost(40).successRate(75).backlashDamage(25).isUnlocked(true).build(),

                // Rank 3: Hỏa Long Cổ
                GuRecipe.builder()
                        .recipeName("Xích Hỏa Hóa Long: Hỏa Long Cổ")
                        .primaryGuCode("BAO_VIEM_CO").secondaryGuCode("HOA_NHA_CO")
                        .materialName("Hỏa Long Vảy").environmentCondition("Trong hầm lửa")
                        .resultGuCode("HOA_LONG_CO")
                        .stoneCost(50).successRate(70).backlashDamage(30).isUnlocked(true).build(),

                // Rank 3: Băng Hỏa Cổ (Xung khắc đặc biệt)
                GuRecipe.builder()
                        .recipeName("Hàn Viêm Hợp Thể: Băng Hỏa Cổ")
                        .primaryGuCode("LIET_HOA_CO").secondaryGuCode("HAN_BANG_CO")
                        .materialName("Băng Tinh Hỏa Sa").environmentCondition("Băng hỏa giao hòa")
                        .resultGuCode("BANG_HOA_CO")
                        .stoneCost(55).successRate(40).backlashDamage(60).isUnlocked(true).build(),

                // Rank 3: Tam Muội Chân Hỏa Cổ
                GuRecipe.builder()
                        .recipeName("Chân Hỏa Thuần Khiết: Tam Muội Chân Hỏa")
                        .primaryGuCode("LIET_HOA_CO").secondaryGuCode("HOA_LONG_CO")
                        .materialName("Tam Muội Hỏa Thạch").environmentCondition("Trong hầm lửa")
                        .resultGuCode("TAM_MUOI_CHAN_HOA_CO")
                        .stoneCost(60).successRate(65).backlashDamage(35).isUnlocked(true).build(),

                // Rank 4: Lam Viêm Cổ
                GuRecipe.builder()
                        .recipeName("U Minh Hỏa Độc: Lam Viêm Cổ")
                        .primaryGuCode("TAM_MUOI_CHAN_HOA_CO").secondaryGuCode("BANG_HOA_CO")
                        .materialName("Lam Hỏa Chu Sa").environmentCondition("Ban đêm")
                        .resultGuCode("LAM_VIEM_CO")
                        .stoneCost(85).successRate(60).backlashDamage(45).isUnlocked(true).build(),

                // Rank 4: Hỏa Vân Cổ
                GuRecipe.builder()
                        .recipeName("Thiêu Thiên Xích Vân: Hỏa Vân Cổ")
                        .primaryGuCode("TAM_MUOI_CHAN_HOA_CO").secondaryGuCode("PHONG_VAN_CO")
                        .materialName("Xích Vân Sa").environmentCondition("Trên đỉnh núi cao")
                        .resultGuCode("HOA_VAN_CO")
                        .stoneCost(85).successRate(60).backlashDamage(45).isUnlocked(true).build(),

                // Rank 5: Cửu Muội Chân Hỏa Cổ
                GuRecipe.builder()
                        .recipeName("Cực Nhiệt Thần Lửa: Cửu Muội Chân Hỏa")
                        .primaryGuCode("LAM_VIEM_CO").secondaryGuCode("HOA_VAN_CO")
                        .materialName("Cửu Muội Thần Hỏa Sa").environmentCondition("Trong hầm lửa")
                        .resultGuCode("CUU_MUOI_CHAN_HOA_CO")
                        .stoneCost(150).successRate(50).backlashDamage(70).isUnlocked(true).build(),

                // Rank 5: Địa Hỏa Viêm Vương Cổ
                GuRecipe.builder()
                        .recipeName("Chúa Tể Địa Hỏa: Địa Hỏa Viêm Vương")
                        .primaryGuCode("CUU_MUOI_CHAN_HOA_CO").secondaryGuCode("BAT_SON_CO")
                        .materialName("Địa Hỏa Tinh Hạch").environmentCondition("Trong hầm lửa")
                        .resultGuCode("DIA_HOA_VIEM_VUONG_CO")
                        .stoneCost(160).successRate(50).backlashDamage(75).isUnlocked(true).build(),

                // ==================== 5. THỦY & BĂNG ĐẠO ====================
                // Rank 2: Băng Phong Cổ
                GuRecipe.builder()
                        .recipeName("Băng Đao Tuyết Vũ: Băng Phong Cổ")
                        .primaryGuCode("BANG_TRU_CO").secondaryGuCode("HAN_BANG_CO")
                        .materialName("Tuyết Sa Bắc Cực").environmentCondition("Hồ nước đóng băng")
                        .resultGuCode("BANG_PHONG_CO")
                        .stoneCost(40).successRate(75).backlashDamage(20).isUnlocked(true).build(),

                // Rank 3: Thủy Bức Cổ
                GuRecipe.builder()
                        .recipeName("U Dạ Dực Ba: Thủy Bức Cổ")
                        .primaryGuCode("THUY_LONG_CO").secondaryGuCode("BANG_PHONG_CO")
                        .materialName("Cánh Dơi Băng").environmentCondition("Ban đêm")
                        .resultGuCode("THUY_BUC_CO")
                        .stoneCost(50).successRate(70).backlashDamage(25).isUnlocked(true).build(),

                // Rank 3: Băng Sương Cổ
                GuRecipe.builder()
                        .recipeName("Ngưng Sương Kết Băng: Băng Sương Cổ")
                        .primaryGuCode("BANG_MOK_CO").secondaryGuCode("HAN_BANG_CO")
                        .materialName("Sương Trắng Ngàn Năm").environmentCondition("Hồ nước đóng băng")
                        .resultGuCode("BANG_SUONG_CO")
                        .stoneCost(50).successRate(70).backlashDamage(25).isUnlocked(true).build(),

                // Rank 4: Tsunami Cổ
                GuRecipe.builder()
                        .recipeName("Thủy Hỏa Xung Đột: Tsunami Cổ")
                        .primaryGuCode("THUY_TIEN_CO").secondaryGuCode("HOA_CAU_CO")
                        .materialName("Hàn Hỏa Thạch").environmentCondition("Hồ nước tĩnh")
                        .resultGuCode("TSUNAMI_CO")
                        .stoneCost(70).successRate(30).backlashDamage(70).isUnlocked(true).build(),

                // Rank 4: Băng Phong Báo Cổ
                GuRecipe.builder()
                        .recipeName("Hàn Triều Tuyệt Vọng: Băng Phong Báo Cổ")
                        .primaryGuCode("BANG_SUONG_CO").secondaryGuCode("TUYET_BOC_CO")
                        .materialName("Băng Phong Báo Tinh").environmentCondition("Đêm giông bão")
                        .resultGuCode("BANG_PHONG_BAO_CO")
                        .stoneCost(80).successRate(60).backlashDamage(40).isUnlocked(true).build(),

                // Rank 5: Băng Hải Cổ
                GuRecipe.builder()
                        .recipeName("Băng Hà Vô Tận: Băng Hải Cổ")
                        .primaryGuCode("BANG_PHONG_BAO_CO").secondaryGuCode("TSUNAMI_CO")
                        .materialName("Vạn Niên Hàn Băng Sa").environmentCondition("Hồ nước đóng băng")
                        .resultGuCode("BANG_HAI_CO")
                        .stoneCost(140).successRate(50).backlashDamage(60).isUnlocked(true).build(),

                // ==================== 6. LÔI & PHONG ĐẠO ====================
                // Rank 2: Lôi Tương Cổ
                GuRecipe.builder()
                        .recipeName("Lôi Đạo Lôi Tương: Lôi Tương Cổ")
                        .primaryGuCode("THIEM_DIEN_CO").secondaryGuCode("TU_LOI_CO")
                        .materialName("Lôi Đình Ngọc").environmentCondition("Đêm sấm sét")
                        .resultGuCode("LOI_TUONG_CO")
                        .stoneCost(45).successRate(75).backlashDamage(25).isUnlocked(true).build(),

                // Rank 3: Bão Lôi Cổ
                GuRecipe.builder()
                        .recipeName("Lôi Đình Vạn Xích: Bão Lôi Cổ")
                        .primaryGuCode("TU_LOI_CO").secondaryGuCode("LOI_TUONG_CO")
                        .materialName("Lôi Đình Thạch Anh").environmentCondition("Đêm sấm sét")
                        .resultGuCode("BAO_LOI_CO")
                        .stoneCost(55).successRate(70).backlashDamage(30).isUnlocked(true).build(),

                // Rank 3: Cuồng Phong Cổ
                GuRecipe.builder()
                        .recipeName("Gió Lốc Bạt Trụ: Cuồng Phong Cổ")
                        .primaryGuCode("PHONG_TRAO_CO").secondaryGuCode("THANH_PHONG_LUAN_CO")
                        .materialName("Cuồng Phong Tinh Thạch").environmentCondition("Đêm giông bão")
                        .resultGuCode("CUONG_PHONG_CO")
                        .stoneCost(50).successRate(70).backlashDamage(25).isUnlocked(true).build(),

                // Rank 4: Bạo Lôi Tiễn Cổ
                GuRecipe.builder()
                        .recipeName("Lôi Tiễn Diệt Địch: Bạo Lôi Tiễn")
                        .primaryGuCode("BAO_LOI_CO").secondaryGuCode("CUONG_PHONG_CO")
                        .materialName("Bạo Lôi Tiễn Trúc").environmentCondition("Đêm giông bão")
                        .resultGuCode("BAO_LOI_TIEN_CO")
                        .stoneCost(80).successRate(60).backlashDamage(40).isUnlocked(true).build(),

                // Rank 5: Tử Điện Cuồng Lôi Cổ
                GuRecipe.builder()
                        .recipeName("Thiên Kiếp Tử Điện: Tử Điện Cuồng Lôi")
                        .primaryGuCode("BAO_LOI_TIEN_CO").secondaryGuCode("CUONG_LONG_BAO_CO")
                        .materialName("Tử Điện Thiên Lôi Sa").environmentCondition("Đêm sấm sét")
                        .resultGuCode("TU_DIEN_CUONG_LOI_CO")
                        .stoneCost(150).successRate(50).backlashDamage(65).isUnlocked(true).build(),

                // Rank 5: Đại Phong Bạo Cổ
                GuRecipe.builder()
                        .recipeName("Phong Đạo Gia Tốc: Đại Phong Bạo Cổ")
                        .primaryGuCode("THANH_PHONG_LUAN_CO").secondaryGuCode("TAT_PHONG_CO")
                        .materialName("Phong Tinh Thạch").environmentCondition("Đêm giông bão")
                        .resultGuCode("DAI_PHONG_BAO_CO")
                        .stoneCost(140).successRate(55).backlashDamage(50).isUnlocked(true).build(),

                // ==================== 7. TRÍ ĐẠO (WISDOM PATH) ====================
                // Rank 2: Tư Ký Cổ
                GuRecipe.builder()
                        .recipeName("Trí Đạo Suy Diễn: Tư Ký Cổ")
                        .primaryGuCode("GIAI_ME_CO").secondaryGuCode("LINH_QUANG_CO")
                        .materialName("Trí Tuệ Sa").environmentCondition("Ban đêm")
                        .resultGuCode("TU_KY_CO")
                        .stoneCost(40).successRate(75).backlashDamage(20).isUnlocked(true).build(),

                // Rank 3: Đa Tư Cổ
                GuRecipe.builder()
                        .recipeName("Tâm Trí Thông Tuệ: Đa Tư Cổ")
                        .primaryGuCode("TU_KY_CO").secondaryGuCode("NIEM_DAU_CO")
                        .materialName("Tuệ Tâm Thảo").environmentCondition("Bình thường")
                        .resultGuCode("DA_TU_CO")
                        .stoneCost(50).successRate(70).backlashDamage(25).isUnlocked(true).build(),

                // Rank 3: Bách Tư Cổ
                GuRecipe.builder()
                        .recipeName("Bách Niệm Quy Nhất: Bách Tư Cổ")
                        .primaryGuCode("DA_TU_CO").secondaryGuCode("TRI_Y_CO")
                        .materialName("Bách Niệm Chu Sa").environmentCondition("Bình thường")
                        .resultGuCode("BACH_TU_CO")
                        .stoneCost(55).successRate(70).backlashDamage(25).isUnlocked(true).build(),

                // Rank 4: Định Tâm Cổ
                GuRecipe.builder()
                        .recipeName("Tâm Nhược Chỉ Thủy: Định Tâm Cổ")
                        .primaryGuCode("BACH_TU_CO").secondaryGuCode("TA_NIEM_CO")
                        .materialName("Định Hồn Huyền Tinh").environmentCondition("Đền cổ trang nghiêm")
                        .resultGuCode("DINH_TAM_CO")
                        .stoneCost(80).successRate(60).backlashDamage(35).isUnlocked(true).build(),

                // Rank 5: Trí Tôn Cổ
                GuRecipe.builder()
                        .recipeName("Bất Diệt Trí Đạo: Trí Tôn Cổ")
                        .primaryGuCode("DINH_TAM_CO").secondaryGuCode("TINH_NHAN_CO")
                        .materialName("Trí Thần Linh Thạch").environmentCondition("Đã tích đủ Đạo Ngân")
                        .resultGuCode("TRI_TON_CO")
                        .stoneCost(150).successRate(50).backlashDamage(55).isUnlocked(true).build(),

                // Rank 5: Suy Diễn Cổ
                GuRecipe.builder()
                        .recipeName("Thiên Cơ Động Tri: Suy Diễn Cổ")
                        .primaryGuCode("TRI_TON_CO").secondaryGuCode("DA_TU_CO")
                        .materialName("Thiên Cơ Diễn Toán Bàn").environmentCondition("Ban đêm")
                        .resultGuCode("SUY_DIEN_CO")
                        .stoneCost(160).successRate(50).backlashDamage(60).isUnlocked(true).build(),

                // ==================== 8. HUYẾT ĐẠO (BLOOD PATH - MA ĐẠO) ====================
                // Rank 2: Huyết Trọng Cổ
                GuRecipe.builder()
                        .recipeName("Huyết Khí Trọng Lực: Huyết Trọng Cổ")
                        .primaryGuCode("HUYET_DICH_CO").secondaryGuCode("HUYET_KHI_CO")
                        .materialName("Huyết Khí Thạch").environmentCondition("Ban đêm")
                        .resultGuCode("HUYET_TRONG_CO")
                        .stoneCost(40).successRate(75).backlashDamage(25).isUnlocked(true).build(),

                // Rank 3: Huyết Lô Cổ (Ma Đạo Tàn Tộc Tăng Tư Chất)
                GuRecipe.builder()
                        .recipeName("Ma Công Đoạt Mệnh: Huyết Lô Cổ")
                        .primaryGuCode("HUYET_TRONG_CO").secondaryGuCode("HUYET_MACH_CO")
                        .materialName("Huyết Tộc Chi Hồn").environmentCondition("Hầm mộ u ám")
                        .resultGuCode("HUYET_LO_CO")
                        .stoneCost(65).successRate(65).backlashDamage(45).isUnlocked(true).build(),

                // Rank 4: Huyết Đích Tử Cổ
                GuRecipe.builder()
                        .recipeName("Cách Không Thủ Cấp: Huyết Đích Tử")
                        .primaryGuCode("HUYET_LO_CO").secondaryGuCode("TU_HUYET_CO")
                        .materialName("Huyết Sa Quán Đỉnh").environmentCondition("Hầm mộ u ám")
                        .resultGuCode("HUYET_DICH_TU_CO")
                        .stoneCost(90).successRate(55).backlashDamage(50).isUnlocked(true).build(),

                // Rank 5: Huyết Biển Cổ
                GuRecipe.builder()
                        .recipeName("Huyết Hải Vô Biên: Huyết Biển Cổ")
                        .primaryGuCode("HUYET_DICH_TU_CO").secondaryGuCode("HUYET_SAT_CO")
                        .materialName("Huyết Hải Tinh Sa").environmentCondition("Đã tích đủ Đạo Ngân")
                        .resultGuCode("HUYET_BIEN_CO")
                        .stoneCost(150).successRate(50).backlashDamage(70).isUnlocked(true).build(),

                // Rank 5: Huyết Thọ Cổ
                GuRecipe.builder()
                        .recipeName("Tế Máu Tục Mệnh: Huyết Thọ Cổ")
                        .primaryGuCode("HUYET_BIEN_CO").secondaryGuCode("THO_CO")
                        .materialName("Huyết Tủy Trường Sinh").environmentCondition("Hầm mộ u ám")
                        .resultGuCode("HUYET_THO_CO")
                        .stoneCost(180).successRate(45).backlashDamage(80).isUnlocked(true).build(),

                // ==================== 9. CỐT ĐẠO (BONE PATH) ====================
                // Rank 2: Toàn Chuyển Cốt Thương Cổ
                GuRecipe.builder()
                        .recipeName("Xoáy Phá Cốt Tiễn: Toàn Chuyển Cốt Thương")
                        .primaryGuCode("COT_THUONG_CO").secondaryGuCode("BACH_COT_CO")
                        .materialName("Nguyên Cốt Thạch").environmentCondition("Bình thường")
                        .resultGuCode("TOAN_CHUYEN_COT_THUONG_CO")
                        .stoneCost(40).successRate(75).backlashDamage(20).isUnlocked(true).build(),

                // Rank 3: Sâm La Cốt Trản
                GuRecipe.builder()
                        .recipeName("Cốt Đạo Ma Công: Sâm La Cốt Trản")
                        .primaryGuCode("BACH_COT_CO").secondaryGuCode("TOAN_CHUYEN_COT_THUONG_CO")
                        .materialName("Huyết Cốt Phấn").environmentCondition("Hầm mộ u ám")
                        .resultGuCode("SAM_LA_COT_TRAN_CO")
                        .stoneCost(55).successRate(70).backlashDamage(30).isUnlocked(true).build(),

                // Rank 4: Cốt Thú Vương Cổ
                GuRecipe.builder()
                        .recipeName("Bạch Cốt Hung Uy: Cốt Thú Vương Cổ")
                        .primaryGuCode("SAM_LA_COT_TRAN_CO").secondaryGuCode("COT_DYC_CO")
                        .materialName("Thú Cốt Ngàn Năm").environmentCondition("Hầm mộ u ám")
                        .resultGuCode("COT_THU_VUONG_CO")
                        .stoneCost(85).successRate(60).backlashDamage(40).isUnlocked(true).build(),

                // Rank 5: Thái Cổ Cốt Long Cổ
                GuRecipe.builder()
                        .recipeName("Khôi Phục Long Cốt: Thái Cổ Cốt Long")
                        .primaryGuCode("COT_THU_VUONG_CO").secondaryGuCode("BIEN_COT_CO")
                        .materialName("Thái Cổ Long Cốt").environmentCondition("Đã tích đủ Đạo Ngân")
                        .resultGuCode("THAI_CO_COT_LONG_CO")
                        .stoneCost(160).successRate(50).backlashDamage(65).isUnlocked(true).build(),

                // ==================== 10. NÔ ĐẠO & VU ĐẠO ====================
                // Rank 2: Khống Hùm Cổ
                GuRecipe.builder()
                        .recipeName("Bách Thú Quy Phục: Khống Hùm Cổ")
                        .primaryGuCode("KHONG_SOI_CO").secondaryGuCode("MANH_HO_CO")
                        .materialName("Bách Thú Đảm").environmentCondition("Bình thường")
                        .resultGuCode("KHONG_HUM_CO")
                        .stoneCost(40).successRate(75).backlashDamage(20).isUnlocked(true).build(),

                // Rank 3: Bách Lang Nô Cổ
                GuRecipe.builder()
                        .recipeName("Thống Lĩnh Bầy Sói: Bách Lang Nô Cổ")
                        .primaryGuCode("KHONG_SOI_CO").secondaryGuCode("BACH_NHAN_LUC_CO")
                        .materialName("Sói Chúa Nha").environmentCondition("Ban đêm")
                        .resultGuCode("BACH_LANG_NO_CO")
                        .stoneCost(55).successRate(70).backlashDamage(25).isUnlocked(true).build(),

                // Rank 4: Bách Thú Nô Cổ
                GuRecipe.builder()
                        .recipeName("Nô Dịch Bách Thú: Bách Thú Nô Cổ")
                        .primaryGuCode("KHONG_THU_VUONG_CO").secondaryGuCode("BACH_LANG_NO_CO")
                        .materialName("Vạn Thú Hồn Châu").environmentCondition("Bình thường")
                        .resultGuCode("BACH_THU_NO_CO")
                        .stoneCost(85).successRate(60).backlashDamage(40).isUnlocked(true).build(),

                // Rank 5: Thiên Thú Vương Cổ
                GuRecipe.builder()
                        .recipeName("Vạn Thú Chi Vương: Thiên Thú Vương Cổ")
                        .primaryGuCode("BACH_THU_NO_CO").secondaryGuCode("THIEN_NHAN_LUC_CO")
                        .materialName("Thiên Thú Hoàng Tinh").environmentCondition("Đã tích đủ Đạo Ngân")
                        .resultGuCode("THIEN_THU_VUONG_CO")
                        .stoneCost(150).successRate(50).backlashDamage(60).isUnlocked(true).build(),

                // ==================== 11. PHÒNG NGỰ ĐẠO (DEFENSE PATH) ====================
                // Rank 3: Thiên Bồng Cổ
                GuRecipe.builder()
                        .recipeName("Phòng Ngự Hợp Thể: Thiên Bồng Cổ")
                        .primaryGuCode("BACH_NGOC_CO").secondaryGuCode("THIET_BI_CO")
                        .materialName("Kim Cương Phấn").environmentCondition("Hồ nước tĩnh")
                        .resultGuCode("THIEN_BONG_CO")
                        .stoneCost(45).successRate(70).backlashDamage(25).isUnlocked(true).build(),

                // Rank 4: Kim Chung Tráo Cổ
                GuRecipe.builder()
                        .recipeName("Kim Cương Bất Hoại: Kim Chung Tráo Cổ")
                        .primaryGuCode("THIEN_BONG_CO").secondaryGuCode("THIET_CAN_CO")
                        .materialName("Vàng Ròng Ngàn Năm").environmentCondition("Đền cổ trang nghiêm")
                        .resultGuCode("KIM_CHUNG_TRAO_CO")
                        .stoneCost(75).successRate(65).backlashDamage(35).isUnlocked(true).build(),

                // ==================== 12. TRỊ LIỆU & THỰC ĐẠO ====================
                // Rank 3: Tịnh Hóa Cổ
                GuRecipe.builder()
                        .recipeName("Trị Liệu Tịnh Hóa: Tịnh Hóa Cổ")
                        .primaryGuCode("SINH_CO_DIEP").secondaryGuCode("HOI_XUAN_CO")
                        .materialName("Ngọc Lộ Cam Tuyền").environmentCondition("Sáng sớm tinh mơ")
                        .resultGuCode("TINH_HOA_CO")
                        .stoneCost(45).successRate(70).backlashDamage(20).isUnlocked(true).build(),

                // Rank 3: Tứ Vị Tửu Trùng
                GuRecipe.builder()
                        .recipeName("Tứ Vị Tửu Trùng Bí Phương")
                        .primaryGuCode("TU_TRUNG").secondaryGuCode("BU_DAP_CO")
                        .materialName("Tứ Quý Tửu Phấn").environmentCondition("Hầm ủ rượu thơm")
                        .resultGuCode("TU_VI_TUU_TRUNG")
                        .stoneCost(60).successRate(70).backlashDamage(30).isUnlocked(true).build(),

                // ==================== 13. XÁ LỢI CỔ (RELIC GU: 1 - 5 CHUYỂN) ====================
                // Rank 2: Xích Thiết Xá Lợi Cổ
                GuRecipe.builder()
                        .recipeName("Hợp luyện Xích Thiết Xá Lợi Cổ (Nhị Chuyển)")
                        .primaryGuCode("THANH_DONG_XA_LOI_CO").secondaryGuCode("TU_TRUNG")
                        .materialName("Xích Thiết Tinh Sa").environmentCondition("Đã tích đủ Đạo Ngân")
                        .resultGuCode("XICH_THIET_XA_LOI_CO")
                        .stoneCost(70).successRate(65).backlashDamage(35).isUnlocked(true).build(),

                // Rank 3: Bạch Ngân Xá Lợi Cổ
                GuRecipe.builder()
                        .recipeName("Hợp luyện Bạch Ngân Xá Lợi Cổ (Tam Chuyển)")
                        .primaryGuCode("XICH_THIET_XA_LOI_CO").secondaryGuCode("TU_VI_TUU_TRUNG")
                        .materialName("Bạch Ngân Tinh Hoa").environmentCondition("Đêm trăng tròn")
                        .resultGuCode("BACH_NGAN_XA_LOI_CO")
                        .stoneCost(120).successRate(60).backlashDamage(50).isUnlocked(true).build(),

                // Rank 4: Hoàng Kim Xá Lợi Cổ
                GuRecipe.builder()
                        .recipeName("Hợp luyện Hoàng Kim Xá Lợi Cổ (Tứ Chuyển)")
                        .primaryGuCode("BACH_NGAN_XA_LOI_CO").secondaryGuCode("TU_VI_TUU_TRUNG")
                        .materialName("Hoàng Kim Tinh Sa").environmentCondition("Đêm trăng tròn")
                        .resultGuCode("HOANG_KIM_XA_LOI_CO")
                        .stoneCost(180).successRate(55).backlashDamage(60).isUnlocked(true).build(),

                // Rank 5: Tử Tinh Xá Lợi Cổ
                GuRecipe.builder()
                        .recipeName("Hợp luyện Tử Tinh Xá Lợi Cổ (Ngũ Chuyển)")
                        .primaryGuCode("HOANG_KIM_XA_LOI_CO").secondaryGuCode("BAT_SON_CO")
                        .materialName("Tử Tinh Thần Thạch").environmentCondition("Trên đỉnh núi cao")
                        .resultGuCode("TU_TINH_XA_LOI_CO")
                        .stoneCost(300).successRate(50).backlashDamage(80).isUnlocked(true).build()
        );

        for (GuRecipe recipe : recipes) {
            guRecipeRepository.findByPrimaryGuCodeAndSecondaryGuCode(recipe.getPrimaryGuCode(), recipe.getSecondaryGuCode())
                    .ifPresentOrElse(existing -> {
                        existing.setRecipeName(recipe.getRecipeName());
                        existing.setMaterialName(recipe.getMaterialName());
                        existing.setResultGuCode(recipe.getResultGuCode());
                        existing.setEnvironmentCondition(recipe.getEnvironmentCondition());
                        existing.setStoneCost(recipe.getStoneCost());
                        existing.setSuccessRate(recipe.getSuccessRate());
                        existing.setBacklashDamage(recipe.getBacklashDamage());
                        guRecipeRepository.save(existing);
                    }, () -> {
                        guRecipeRepository.save(recipe);
                    });
        }
        log.info("Đã đồng bộ {} Bí Phương Luyện Cổ kinh điển theo chuẩn Tam Trụ Môi Trường vào cơ sở dữ liệu.", recipes.size());
    }
}

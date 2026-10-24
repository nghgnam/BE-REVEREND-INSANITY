package com.cochannhan.game.model;

public enum GuType {
    CONG_KICH("Công Kích", "Tấn công tiêu hao sinh lực địch thủ"),
    PHONG_NGU("Phòng Ngự", "Hộ giáp, giảm thiểu sát thương nhận vào"),
    THAN_PHAP("Thân Pháp", "Tăng tốc độ, né tránh và điều chỉnh cự ly"),
    TRINH_SAT("Trinh Sát", "Dò xét hư thực, lộ diện Cổ trùng đối phương"),
    TRI_LIEU("Trị Liệu", "Hồi phục vết thương và khí huyết"),
    PHU_TRO("Phụ Trợ", "Tinh luyện chân nguyên, cường hóa thuộc tính"),
    DAC_BIET("Đặc Biệt", "Cổ trùng kỳ dị, công năng thần bí"),
    TIEU_THU("Tiêu Thụ", "Cổ trùng sử dụng trực tiếp tiêu hao, mang lại hiệu ứng kỳ diệu");

    private final String label;
    private final String description;

    GuType(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() { return label; }
    public String getDescription() { return description; }
}

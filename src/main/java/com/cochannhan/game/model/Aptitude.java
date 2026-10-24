package com.cochannhan.game.model;

public enum Aptitude {
    GIAP_DANG("Giáp Đẳng (A)", 80, 99, 50, "#F59E0B", "Gia tộc trọng vọng, tài nguyên dồi dào, hồi chân nguyên cực nhanh nhưng bị giám sát gắt gao."),
    AT_DANG("Ất Đẳng (B)", 60, 79, 35, "#3B82F6", "Tầng lớp rường cột trung lưu, lao dịch cống hiến cho sơn trại."),
    BINH_DANG("Bính Đẳng (C)", 40, 59, 25, "#10B981", "Bị gia tộc ghẻ lạnh, tài nguyên ít ỏi, bắt buộc phải mưu mô cướp đoạt để nghịch thiên cải mệnh."),
    DINH_DANG("Đinh Đẳng (D)", 20, 39, 15, "#9CA3AF", "Tư chất đáy cùng, chân nguyên khô cạn, vô cùng trắc trở.");

    private final String displayName;
    private final int minPercentage;
    private final int maxPercentage;
    private final int recoveryRatePerTurn;
    private final String colorCode;
    private final String description;

    Aptitude(String displayName, int minPercentage, int maxPercentage, int recoveryRatePerTurn, String colorCode, String description) {
        this.displayName = displayName;
        this.minPercentage = minPercentage;
        this.maxPercentage = maxPercentage;
        this.recoveryRatePerTurn = recoveryRatePerTurn;
        this.colorCode = colorCode;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public int getMinPercentage() { return minPercentage; }
    public int getMaxPercentage() { return maxPercentage; }
    public int getRecoveryRatePerTurn() { return recoveryRatePerTurn; }
    public String getColorCode() { return colorCode; }
    public String getDescription() { return description; }
}

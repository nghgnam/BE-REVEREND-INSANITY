package com.cochannhan.game.model;

public enum CultivationRank {
    NHAT_CHUYEN(1, "Nhất Chuyển", "Thanh Đồng Chân Nguyên", "#10B981", 100),
    NHI_CHUYEN(2, "Nhị Chuyển", "Xích Thiết Chân Nguyên", "#EF4444", 250),
    TAM_CHUYEN(3, "Tam Chuyển", "Bạch Ngân Chân Nguyên", "#E2E8F0", 600),
    TU_CHUYEN(4, "Tứ Chuyển", "Hoàng Kim Chân Nguyên", "#FBBF24", 1500),
    NGU_CHUYEN(5, "Ngũ Chuyển", "Tử Tinh Chân Nguyên", "#A855F7", 4000);

    private final int rankNumber;
    private final String rankName;
    private final String essenceType;
    private final String themeColor;
    private final int baseEssencePool;

    CultivationRank(int rankNumber, String rankName, String essenceType, String themeColor, int baseEssencePool) {
        this.rankNumber = rankNumber;
        this.rankName = rankName;
        this.essenceType = essenceType;
        this.themeColor = themeColor;
        this.baseEssencePool = baseEssencePool;
    }

    public int getRankNumber() { return rankNumber; }
    public String getRankName() { return rankName; }
    public String getEssenceType() { return essenceType; }
    public String getThemeColor() { return themeColor; }
    public int getBaseEssencePool() { return baseEssencePool; }
}

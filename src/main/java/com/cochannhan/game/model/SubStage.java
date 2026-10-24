package com.cochannhan.game.model;

public enum SubStage {
    SO_GIAI("Sơ Giai", 1, 1.0, 100),
    TRUNG_GIAI("Trung Giai", 2, 1.25, 200),
    CAO_GIAI("Cao Giai", 3, 1.5, 350),
    DINH_PHONG("Đỉnh Phong", 4, 2.0, 500);

    private final String stageName;
    private final int level;
    private final double multiplier;
    private final int breakThroughExpRequired;

    SubStage(String stageName, int level, double multiplier, int breakThroughExpRequired) {
        this.stageName = stageName;
        this.level = level;
        this.multiplier = multiplier;
        this.breakThroughExpRequired = breakThroughExpRequired;
    }

    public String getStageName() { return stageName; }
    public int getLevel() { return level; }
    public double getMultiplier() { return multiplier; }
    public int getBreakThroughExpRequired() { return breakThroughExpRequired; }
}

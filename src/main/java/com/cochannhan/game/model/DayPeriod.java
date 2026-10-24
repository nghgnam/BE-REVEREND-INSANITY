package com.cochannhan.game.model;

public enum DayPeriod {
    SANG("Buổi Sáng", "Bình minh vừa hé, đệ tử gia tộc bắt đầu ngày mới."),
    TRUA("Buổi Trưa", "Mặt trời lên cao, thương nhân buôn bán tấp nập."),
    CHIEU("Buổi Chiều", "Hoàng hôn buông xuống, các đội tuần tra trở về."),
    DEM("Buổi Đêm", "Bóng tối bao trùm, thời điểm ma đạo hành động.");

    private final String title;
    private final String description;

    DayPeriod(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }

    public DayPeriod next() {
        return switch (this) {
            case SANG -> TRUA;
            case TRUA -> CHIEU;
            case CHIEU -> DEM;
            case DEM -> SANG;
        };
    }
}

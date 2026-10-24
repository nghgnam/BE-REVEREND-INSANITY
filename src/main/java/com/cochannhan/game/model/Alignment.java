package com.cochannhan.game.model;

public enum Alignment {
    CHINH_DAO("Chính Đạo", "Tuân theo gia quy, giữ gìn uy danh, được gia tộc che chở"),
    MA_DAO("Ma Đạo", "Tàn nhẫn thực dụng, coi trọng lợi ích tối thượng, bị chính phái truy nã");

    private final String label;
    private final String detail;

    Alignment(String label, String detail) {
        this.label = label;
        this.detail = detail;
    }

    public String getLabel() { return label; }
    public String getDetail() { return detail; }
}

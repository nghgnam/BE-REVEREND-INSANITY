package com.cochannhan.game.model;

public enum CombatDistance {
    CAN_CHIEN("Cận Chiến", 1),
    TAM_TRUNG("Tầm Trung", 2),
    TAM_XA("Tầm Xa", 3);

    private final String label;
    private final int distanceValue;

    CombatDistance(String label, int distanceValue) {
        this.label = label;
        this.distanceValue = distanceValue;
    }

    public String getLabel() { return label; }
    public int getDistanceValue() { return distanceValue; }
}

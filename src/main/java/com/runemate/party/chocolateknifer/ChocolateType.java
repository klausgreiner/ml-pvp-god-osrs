package com.runemate.party.chocolateknifer;

public enum ChocolateType {
    CHOCOLATE_BAR("Chocolate bar", "Chocolate dust", 1),
    CHOCOLATE_SLICE("Chocolate slice", "Chocolate dust", 1);

    private final String chocolateName;
    private final String dustName;
    private final int requiredLevel;

    ChocolateType(final String chocolateName, final String dustName, final int requiredLevel) {
        this.chocolateName = chocolateName;
        this.dustName = dustName;
        this.requiredLevel = requiredLevel;
    }

    public String getChocolateName() {
        return chocolateName;
    }

    public String getDustName() {
        return dustName;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }
}

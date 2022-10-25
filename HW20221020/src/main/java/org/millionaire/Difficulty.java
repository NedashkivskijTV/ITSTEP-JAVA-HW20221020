package org.millionaire;

public enum Difficulty {
    D_01(100, false),
    D_02(500, false),
    D_03(1_000, false),
    D_04(2_000, false),
    D_05(5_000, true),
    D_06(10_000, false),
    D_07(20_000, false),
    D_08(30_000, false),
    D_09(40_000, false),
    D_10(50_000, true),
    D_11(75_000, false),
    D_12(150_000, false),
    D_13(200_000, false),
    D_14(500_000, false),
    D_15(1_000_000, true);

    private final int price;
    private final boolean isFireproof;

    Difficulty(int price, boolean isFireproof) {
        this.price = price;
        this.isFireproof = isFireproof;
    }

    public int getPrice() {
        return price;
    }

    public boolean isFireproof() {
        return isFireproof;
    }
}

package net.teamc.aegis.model;


import java.util.Locale;

public enum ColorCode {
    GREEN(1, "Safe"),
    GRAY(2, "At Risk"),
    YELLOW(3, "Find Some Companions"),
    ORANGE(4, "Find Some More Companions"),
    RED(5, "GET A GUN!"),
    DARK_RED(6, "DO NOT GO!!!");

    ColorCode(int value, String tag) {
        colorValue = value;
        label = tag;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "(%1$d, %2$s)", colorValue, label);
    }

    public int getColorValue() {
        return colorValue;
    }

    private final int colorValue;
    private final String label;

}

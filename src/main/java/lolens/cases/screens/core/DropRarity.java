package lolens.cases.screens.core;

import io.wispforest.owo.ui.core.Color;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public enum DropRarity {

    // rarity value is used to compare and sort. todo: data-driven rarities

    BLANK(Color.ofArgb(00000000), 0),
    COMMON(Color.ofFormatting(Formatting.GRAY), 100),
    UNCOMMON(Color.ofFormatting(Formatting.GREEN), 200),
    RARE(Color.ofFormatting(Formatting.BLUE), 300),
    EPIC(Color.ofFormatting(Formatting.LIGHT_PURPLE), 400),
    LEGENDARY(Color.ofFormatting(Formatting.GOLD), 500),
    MYTHIC(Color.ofFormatting(Formatting.RED), 600);

    private final Color color;
    private final int sortOrder;

    private DropRarity(Color color, int id) {
        this.color = color;
        this.sortOrder = id;
    }

    public Color getColor() {
        return this.color;
    }

    @Nullable
    public static Color getColorById(int id) {
        for (DropRarity rarity : DropRarity.values()) {
            if (rarity.getRarityValue() == id) {
                return rarity.getColor();
            }
        }
        return null;
    }

    @Nullable
    public static DropRarity getByName(String name) {
        for (DropRarity rarity : DropRarity.values()) {
            if (rarity.name().equals(name)) {
                return rarity;
            }
        }
        return null;
    }

    @Nullable
    public static DropRarity getById(int id) {
        for (DropRarity rarity : DropRarity.values()) {
            if (rarity.getRarityValue() == id) {
                return rarity;
            }
        }
        return null;
    }

    public int argb() {
        return this.color.argb();
    }

    public int rgb() {
        return this.color.rgb();
    }

    public int getRarityValue() {
        return this.sortOrder;
    }
}

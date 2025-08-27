package lolens.cases.core;

import net.minecraft.util.Rarity;

public class LootCrateItemProperties {

    private String id; // NBT field used to distinct crates from each other
    private boolean glowing = false; // makes crate glow like enchanted
    private int color = 0xFFFFFFFF; // todo: layered texture // now does nothing iirc
    private String name = "Unnamed case"; // display name of a crate
    private Rarity rarity = Rarity.UNCOMMON; // display name color

    // used by chat commands to display all properties
    public String toStringLN() {
        return  "- Item Properties:\n" +
                "id=" + id + "\n" +
                "glowing=" + glowing + "\n" +
                "color=" + color + "\n" +
                "name=" + name + "\n" +
                "rarity=" + rarity + "\n";
    }

    public boolean isGlowing() {
        return glowing;
    }

    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

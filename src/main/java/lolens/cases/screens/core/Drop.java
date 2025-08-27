package lolens.cases.screens.core;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class Drop {
    private ItemStack stack;
    private DropRarity rarity;
    private int weight;

    public Drop(ItemStack stack, DropRarity rarity, int weight) {
        this.stack = stack;
        this.rarity = rarity;
        this.weight = weight;
    }


    public ItemStack getStack() {
        return stack;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public DropRarity getRarity() {
        return rarity;
    }

    public int getRarityValue() {
        return this.rarity.getRarityValue();
    }

    public void setRarity(DropRarity rarity) {
        this.rarity = rarity;
    }

    public static Drop getDefault() {
        return new Drop(new ItemStack(Items.DIRT), DropRarity.COMMON, 1);
    }

    @Override
    public String toString() {
        return "LootCrateDrop{" +
                "stack=" + stack +
                ", rarity=" + rarity +
                ", weight=" + weight +
                '}';
    }
}

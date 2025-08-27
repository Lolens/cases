package lolens.cases.core;

import lolens.cases.screens.core.Drop;
import lolens.cases.screens.core.DropRarity;
import lolens.cases.util.CasesUtils;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LootCrate {

    private final LootCrateItemProperties lootCrateItemProperties = new LootCrateItemProperties();
    private final LootCrateScreenProperties lootCrateScreenProperties = new LootCrateScreenProperties();
    private final List<Drop> drops = new ArrayList<>();

    public String dropsToStringLN() {
        StringBuilder drops = new StringBuilder();
        drops.append("- Drops:\n");

        float[] dropChances = CasesUtils.getDropChances(this.drops);

        for (int i = 0; i < this.drops.size(); i++) {
            Drop drop = this.drops.get(i);
            drops.append(String.format("x%d %s | Rarity: %s | Weight: %d | Drop chance: %.3f\n",
                    drop.getStack().getCount(),
                    drop.getStack().getItem().getName().getString(),
                    drop.getRarity(),
                    drop.getWeight(),
                    dropChances[i]));
        }
        return drops.toString();
    }

    public String dropsToStringLN(int idToShow) {
        StringBuilder drops = new StringBuilder();
        drops.append("- Drops:\n");

        float dropChance = CasesUtils.getDropChance(this.drops, idToShow);

        for (int i = 0; i < this.drops.size(); i++) {
            if (idToShow != i) continue;
            Drop drop = this.drops.get(i);
            drops.append(String.format("x%d %s | Rarity: %s | Weight: %d | Drop chance: %.3f\n",
                    drop.getStack().getCount(),
                    drop.getStack().getItem().getName().getString(),
                    drop.getRarity(),
                    drop.getWeight(),
                    dropChance
            ));
        }
        return drops.toString();
    }

    public String toStringLN() {
        return "\n========== " + this.lootCrateItemProperties.getName() + " ==========\n" +
                this.lootCrateItemProperties.toStringLN() +
                this.lootCrateScreenProperties.toStringLN() +
                dropsToStringLN();
    }

    public LootCrateScreenProperties getScreenProperties() {
        return lootCrateScreenProperties;
    }

    public LootCrateItemProperties getItemProperties() {
        return lootCrateItemProperties;
    }

    public List<Drop> getDrops() {
        return drops;
    }

    public void addWeightedDrop(ItemStack stack, DropRarity rarity, int weight) {
        this.drops.add(new Drop(stack, rarity, weight));
    }

}

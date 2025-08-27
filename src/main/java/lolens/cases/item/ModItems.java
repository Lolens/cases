package lolens.cases.item;

import lolens.cases.Cases;
import lolens.cases.item.custom.LootCrateItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {

    public static final Item CASE = registerItem("case",
            new LootCrateItem(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(Cases.MOD_ID, name), item);
    }

    public static void register() {
        Cases.LOGGER.info("Registering items for " + Cases.MOD_ID);
    }

}

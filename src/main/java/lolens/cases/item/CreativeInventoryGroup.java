package lolens.cases.item;

import lolens.cases.Cases;
import lolens.cases.core.LootCrateManager;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class CreativeInventoryGroup {

    public static final ItemGroup CASES_ITEMS_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(Cases.MOD_ID, "cases"),
            FabricItemGroup.builder().displayName(Text.translatable("itemgroup.cases"))
                    .icon(Items.CHEST::getDefaultStack).entries((displayContext, entries) -> {

                        /*
                        looks like minecraft adding entries after player gets all cases data from server upon initial sync upon joining server, so it gets overwritten by this builder,
                        so I've duplicated this code from LootCrateUtil.refreshCreativeTabLootCrates(). Other item group updates are not affected by this.
                         */
                        List<String> ids = LootCrateManager.getInstance().getLootCrateIds();
                        for (String id : ids) {
                            NbtCompound tag = new NbtCompound();
                            tag.putString("caseId", id);
                            ItemStack lootCrateStack = new ItemStack(ModItems.CASE);
                            lootCrateStack.setNbt(tag);
                            entries.add(lootCrateStack);
                        }

                    }).build());

    public static void register() {
        Cases.LOGGER.info("Registering Item Groups for " + Cases.MOD_ID);
    }

}

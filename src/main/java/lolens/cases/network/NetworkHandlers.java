package lolens.cases.network;

import io.wispforest.owo.network.OwoNetChannel;
import lolens.cases.Cases;
import lolens.cases.core.LootCrate;
import lolens.cases.core.LootCrateItemProperties;
import lolens.cases.core.LootCrateManager;
import lolens.cases.core.LootCrateScreenProperties;
import lolens.cases.network.packets.S2C_CaseSyncPacket;
import lolens.cases.screens.core.Drop;
import lolens.cases.util.CasesUtils;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class NetworkHandlers {

    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(new Identifier(Cases.MOD_ID, "main"));

    public static void register() {

        CHANNEL.registerClientbound(S2C_CaseSyncPacket.class, (message, access) -> {

            if (message.shouldClearExisting()) LootCrateManager.getInstance().clear();

            LootCrate crate = new LootCrate();

            LootCrateItemProperties ip = crate.getItemProperties();
            LootCrateScreenProperties sp = crate.getScreenProperties();

            // general item data
            ip.setId(message.id());
            ip.setGlowing(message.glowing());
            ip.setColor(message.color());
            ip.setName(message.name());
            ip.setRarity(message.rarity());

            // general gui
            sp.setHorizontalGUISize(message.horizontalGUISize());
            sp.setSpinContainerVerticalSize(message.spinContainerVerticalSize());
            sp.setItemsContainerVerticalSize(message.itemsContainerVerticalSize());
            sp.setButtonContainerVerticalSize(message.buttonContainerVerticalSize());

            // items grid
            sp.setItemGridRows(message.itemGridRows());
            sp.setItemGridColumns(message.itemGridColumns());
            sp.setItemsGridContainerMargin(message.itemsGridContainerMargin());
            sp.setItemsGridElementSize(message.itemsGridElementSize());

            sp.setItemsGridDisplayTooltip(message.itemsGridDisplayTooltip());
            sp.setItemsGridDisplayStackCount(message.itemsGridDisplayStackCount());
            sp.setItemsGridStacksOnHoverScale(message.itemsGridStacksOnHoverScale());
            sp.setItemsGridStacksOnHoverScaleMultiplier(message.itemsGridStacksOnHoverScaleMultiplier());

            sp.setItemsGridOutlineColor(message.itemsGridOutlineColor());

            sp.setSortType(message.sortByRarity());

            //spin grid
            sp.setSpinGridColumns(message.spinGridColumns());
            sp.setSpinGridElementSize(message.spinGridElementSize());
            sp.setSpinGridAnimationDuration(message.spinGridAnimationDuration());

            sp.setSpinGridDisplayTooltip(message.spinGridDisplayTooltip());
            sp.setSpinGridDisplayStackCount(message.spinGridDisplayStackCount());
            sp.setSpinGridStacksOnHoverScale(message.spinGridStacksOnHoverScale());
            sp.setSpinGridStacksOnHoverScaleMultiplier(message.spinGridStacksOnHoverScaleMultiplier());

            sp.setSpinGridStacksScaleWhileRolling(message.spinGridStacksScaleWhileRolling());

            sp.setSpinGridOutlineColor(message.spinGridOutlineColor());

            // sounds
            sp.setSlotChangeSound(SoundEvent.of(new Identifier(message.slotChangeSound())));

            // easing
            sp.setExcludedEasings(message.excludedEasingsIndexes());

            // drops
            for (Drop lcd : message.drops()) {
                crate.addWeightedDrop(lcd.getStack(), lcd.getRarity(), lcd.getWeight());
            }

            LootCrateManager.getInstance().addLootCrate(ip.getId(), crate);
            if (message.shouldRefreshCreativeTab()) CasesUtils.refreshCreativeTabLootCrates();
        });

    }

}

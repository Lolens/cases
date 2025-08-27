package lolens.cases.network.packets;

import lolens.cases.screens.core.Drop;
import lolens.cases.screens.core.SortType;
import net.minecraft.util.Rarity;

import java.util.List;

public record S2C_CaseSyncPacket(

        // general item data
        String id,
        boolean glowing,
        int color,
        String name,
        Rarity rarity,

        // general gui
        int horizontalGUISize,
        int spinContainerVerticalSize,
        int itemsContainerVerticalSize,
        int buttonContainerVerticalSize,

        // items grid
        int itemGridRows,
        int itemGridColumns,
        int itemsGridContainerMargin,
        int itemsGridElementSize,

        boolean itemsGridDisplayTooltip,
        boolean itemsGridDisplayStackCount,
        boolean itemsGridStacksOnHoverScale,
        float itemsGridStacksOnHoverScaleMultiplier,

        int itemsGridOutlineColor,

        SortType sortByRarity,

        // spin grid
        int spinGridColumns,
        int spinGridElementSize,
        int spinGridAnimationDuration,

        boolean spinGridDisplayTooltip,
        boolean spinGridDisplayStackCount,
        boolean spinGridStacksOnHoverScale,
        float spinGridStacksOnHoverScaleMultiplier,

        boolean spinGridStacksScaleWhileRolling,

        int spinGridOutlineColor,

        // sounds
        String slotChangeSound,

        // easing
        int[] excludedEasingsIndexes,

        // drops
        List<Drop> drops,

        // other
        boolean shouldClearExisting,
        boolean shouldRefreshCreativeTab
) {}

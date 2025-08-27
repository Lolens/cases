package lolens.cases.screens.core.builders;

import lolens.cases.core.LootCrateScreenProperties;
import lolens.cases.screens.LootCrateScreen;
import lolens.cases.screens.core.Drop;
import lolens.cases.screens.core.components.DropComponent;
import lolens.cases.screens.core.components.WinningDropComponent;

public final class DropComponentBuilder {
    private final int size;
    private final LootCrateScreen parentScreen;
    private final LootCrateScreenProperties properties;
    private Drop drop;
    private boolean isWinning;
    private boolean isItemsGrid;

    private DropComponentBuilder(int size, LootCrateScreen parentScreen, LootCrateScreenProperties properties) {
        this.size = size;
        this.parentScreen = parentScreen;
        this.properties = properties;
    }

    public static DropComponentBuilder create(int size, LootCrateScreen parentScreen, LootCrateScreenProperties properties) {
        return new DropComponentBuilder(size, parentScreen, properties);
    }

    public DropComponentBuilder withDrop(Drop drop) {
        this.drop = drop;
        return this;
    }

    public DropComponentBuilder asWinning(boolean bool) {
        this.isWinning = bool;
        return this;
    }

    public DropComponentBuilder forItemsGrid(boolean isItemsGrid) {
        this.isItemsGrid = isItemsGrid;
        return this;
    }

    public DropComponent build() {
        DropComponent component = isWinning
                ? new WinningDropComponent(size, parentScreen)
                : new DropComponent(size, drop, parentScreen);

        boolean showTooltip = isItemsGrid
                ? properties.isItemsGridDisplayTooltip()
                : properties.isSpinGridDisplayTooltip();

        boolean showStackCount = isItemsGrid
                ? properties.isItemsGridDisplayStackCount()
                : properties.isSpinGridDisplayStackCount();

        boolean scaleOnHover = isItemsGrid
                ? properties.isItemsGridStacksOnHoverScale()
                : properties.isSpinGridStacksOnHoverScale();

        float hoverScaleMultiplier = isItemsGrid
                ? properties.getItemsGridStacksOnHoverScaleMultiplier()
                : properties.getSpinGridStacksOnHoverScaleMultiplier();

        component
                .setTooltipFromStack(showTooltip)
                .showOverlay(showStackCount)
                .scalesOnHover(scaleOnHover)
                .setMaxOnHoverScale(hoverScaleMultiplier);

        if (!isItemsGrid) {
            component.setScalesWhileRolling(properties.isSpinGridStacksScaleWhileRolling());
        }

        return component;
    }
}
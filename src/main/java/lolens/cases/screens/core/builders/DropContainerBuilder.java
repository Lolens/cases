package lolens.cases.screens.core.builders;

import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.container.FlowLayout;
import lolens.cases.core.LootCrateScreenProperties;
import lolens.cases.screens.core.components.DropComponent;

public final class DropContainerBuilder {
    private final LootCrateScreenProperties properties;
    private boolean isItemsGrid;

    private DropContainerBuilder(LootCrateScreenProperties properties) {
        this.properties = properties;
    }

    public static DropContainerBuilder create(LootCrateScreenProperties properties) {
        return new DropContainerBuilder(properties);
    }

    public DropContainerBuilder forItemsGrid(boolean isItemsGrid) {
        this.isItemsGrid = isItemsGrid;
        return this;
    }

    public FlowLayout build(DropComponent dropComponent) {
        int elementSize = isItemsGrid
                ? properties.getItemsGridElementSize()
                : properties.getSpinGridElementSize();

        int outlineColor = isItemsGrid
                ? properties.getItemsGridOutlineColor()
                : properties.getSpinGridOutlineColor();

        FlowLayout container = Containers.verticalFlow(Sizing.fixed(16), Sizing.fixed(16));
        container.padding(Insets.of(1));
        container.sizing(Sizing.fixed(elementSize));
        container.surface(Surface.outline(outlineColor));

        if (isItemsGrid) {
            container.margins(Insets.of(properties.getItemsGridContainerMargin()));
        }

        dropComponent.sizing(Sizing.fixed(elementSize - 2));
        container.child(dropComponent);

        return container;
    }
}
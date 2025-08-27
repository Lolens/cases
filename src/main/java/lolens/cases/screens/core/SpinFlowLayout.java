package lolens.cases.screens.core;

import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.core.Component;
import io.wispforest.owo.ui.core.Sizing;
import lolens.cases.screens.LootCrateScreen;
import lolens.cases.screens.core.components.DropComponent;

public class SpinFlowLayout extends FlowLayout {

    private Component child;
    private GridLayout spinGrid;
    private final LootCrateScreen parentScreen;


    public SpinFlowLayout(Sizing horizontalSizing, Sizing verticalSizing, LootCrateScreen parentScreen) {
        super(horizontalSizing, verticalSizing, Algorithm.VERTICAL);
        this.parentScreen = parentScreen;
    }

    @Override
    protected void updateLayout() {
        super.updateLayout();
        checkSlotChanged();
    }

    private int midX = 0;
    private int midY = 0;

    private void calculateMid() {
        if (midX == 0 || midY == 0) {
            this.midX = (this.x + this.width / 2) + this.parentScreen.getSpinGridElementSize(); // + this.parentScreen.getSpinGridElementSize() to childAt() to not get selector
            this.midY = (this.y + this.height / 2);
        }
    }


    private void checkSlotChanged() {
        if (this.spinGrid == null) {
            this.spinGrid = this.childById(GridLayout.class, "spin-grid");
        }

        calculateMid();

        Component child = this.childAt(midX, midY);

        if (child != null) {

            if (child instanceof DropComponent) {

                if (this.child == null) {
                    this.child = child;
                    return;
                }


                if (this.child != child) {
                    this.child = child;
                    this.parentScreen.playSpinSound();
                }
            }
        }
    }

}

package lolens.cases.screens;

import io.wispforest.owo.ui.base.BaseOwoHandledScreen;
import io.wispforest.owo.ui.component.BoxComponent;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.GridLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import lolens.cases.core.LootCrateManager;
import lolens.cases.core.LootCrateScreenProperties;
import lolens.cases.handlers.LootCrateScreenHandler;
import lolens.cases.network.packets.C2S_RequestItemPacket;
import lolens.cases.screens.core.*;
import lolens.cases.screens.core.builders.DropComponentBuilder;
import lolens.cases.screens.core.builders.DropContainerBuilder;
import lolens.cases.screens.core.components.DropComponent;
import lolens.cases.util.CasesUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class LootCrateScreen extends BaseOwoHandledScreen<FlowLayout, LootCrateScreenHandler> {

    // Naming-wise
    // Here spinGrid is that bar with items that rolls and itemsGrid is a display grid that lists possible drops

    /*
     Only tested GUI on 1920x1080 screen. So I don't know how it's going to work.
     Probably as GUI is fixed in pixels then of smaller screen they will be bigger and vice-versa
     */

    // editable
    private final int itemGridRows;
    private final int itemGridColumns;
    private final int itemsGridContainerMargin;
    private final int itemsGridElementSize;

    private final int horizontalGUISize;
    private final int spinContainerVerticalSize;
    private final int itemsContainerVerticalSize;
    private final int buttonContainerVerticalSize;

    private final boolean itemsGridDisplayTooltip;
    private final boolean itemsGridDisplayStackCount;
    private final boolean spinGridDisplayTooltip;
    private final boolean spinGridDisplayStackCount;

    private final boolean spinGridStacksScaleWhileRolling;
    private final boolean itemsGridStacksOnHoverScale;
    private final float itemsGridStacksOnHoverScaleMultiplier; // used if scales

    private final int spinGridColumns;
    private final int spinGridElementSize;
    private final int spinGridAnimationDuration; // ms

    private final int spinGridOutlineColor;
    private final int itemsGridOutlineColor;
    private final SortType sortType;
    private final boolean spinGridStacksOnHoverScale;
    private final float spinGridStacksOnHoverScaleMultiplier;

    private final SoundEvent slotChangeSound;

    private final int[] excludedEasings;

    // not editable
    private final Random random = Random.create();

    private final ArrayList<Drop> dropList = new ArrayList<>(LootCrateManager.getInstance().getLootCrate(this.handler.getCaseId()).getDrops());

    private int spinGridItemOffset = 0;
    private boolean rollInProgress = false;
    private int spinGridVisibleColumns;
    private int winningSlotColumn;

    public LootCrateScreen(LootCrateScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        LootCrateScreenProperties properties = handler.getCaseProperties();

        // Probably don't need that much new fields. But at the time it was written I didn't know about it

        this.horizontalGUISize = properties.getHorizontalGUISize();
        this.spinContainerVerticalSize = properties.getSpinContainerVerticalSize();
        this.itemsContainerVerticalSize = properties.getItemsContainerVerticalSize();
        this.buttonContainerVerticalSize = properties.getButtonContainerVerticalSize();

        this.itemGridRows = properties.getItemGridRows();
        this.itemGridColumns = properties.getItemGridColumns();
        this.itemsGridContainerMargin = properties.getItemsGridContainerMargin();
        this.itemsGridElementSize = properties.getItemsGridElementSize();

        this.itemsGridDisplayTooltip = properties.isItemsGridDisplayTooltip();
        this.itemsGridDisplayStackCount = properties.isItemsGridDisplayStackCount();
        this.itemsGridStacksOnHoverScale = properties.isItemsGridStacksOnHoverScale();
        this.itemsGridStacksOnHoverScaleMultiplier = properties.getItemsGridStacksOnHoverScaleMultiplier();

        this.itemsGridOutlineColor = properties.getItemsGridOutlineColor();

        this.sortType = properties.getSortType();

        this.spinGridColumns = properties.getSpinGridColumns();
        this.spinGridElementSize = properties.getSpinGridElementSize();
        this.spinGridAnimationDuration = properties.getSpinGridAnimationDuration();

        this.spinGridDisplayTooltip = properties.isSpinGridDisplayTooltip();
        this.spinGridDisplayStackCount = properties.isSpinGridDisplayStackCount();
        this.spinGridStacksOnHoverScale = properties.isSpinGridStacksOnHoverScale();
        this.spinGridStacksOnHoverScaleMultiplier = properties.getSpinGridStacksOnHoverScaleMultiplier();

        this.spinGridStacksScaleWhileRolling = properties.isSpinGridStacksScaleWhileRolling();

        this.spinGridOutlineColor = properties.getSpinGridOutlineColor();

        this.slotChangeSound = properties.getSlotChangeSound();

        this.excludedEasings = properties.getExcludedEasings();

        this.playerInventoryTitleY = 10000;

    }

    public int getSpinGridElementSize() {
        return spinGridElementSize;
    }

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::horizontalFlow);
    }


    public void playSpinSound() {
        if (rollInProgress) {
            float volumeDeviation = 0.05f - random.nextFloat() / 10;
            float pitchDeviation = 0.05f - random.nextFloat() / 10;
            handler.playSoundToPlayer(slotChangeSound, 0.5f + volumeDeviation, 0.7f + pitchDeviation);
        }
    }

    private void fillSpinLayout(SpinFlowLayout spinLayout) {
        int padding = (spinLayout.verticalSizing().get().value - spinGridElementSize) / 2;
        spinLayout.padding(Insets.of(padding, padding, 3, 3));

        GridLayout spinGrid = Containers.grid(Sizing.fixed(spinGridElementSize * spinGridColumns), Sizing.fixed(spinGridElementSize), 1, spinGridColumns);
        spinGrid.zIndex(5);
        spinGrid.id("spin-grid");
        spinGrid.positioning(Positioning.absolute(-spinGridItemOffset, 0));
        spinLayout.child(spinGrid);

        for (int i = 0; i < spinGridColumns; i++) {

            FlowLayout container;
            if (i == this.winningSlotColumn) {
                container = createDropContainer(CasesUtils.getRandomLootCrateDrop(this.dropList), true, false);
            } else {
                container = createDropContainer(CasesUtils.getRandomLootCrateDrop(this.dropList), false, false);
            }
            container.id(String.valueOf(i));
            spinGrid.child(container, 0, i);
        }
    }

    private void sortByRarity() {
        if (this.sortType == SortType.NONE) return; // NONE
        Comparator<Drop> comparator = Comparator.comparingInt(Drop::getRarityValue); // ASC
        if (this.sortType == SortType.DESC) { // DESC
            comparator = comparator.reversed();
        }
        this.dropList.sort(comparator);
    }

    private void fillItemsGrid(GridLayout grid) {
        int row = 0;
        int column = 0;
        for (Drop drop : this.dropList) {

            FlowLayout container = createDropContainer(drop, false, true);
            grid.child(container, row, column);

            column++;
            if (column >= itemGridColumns) {
                column = 0;
                row++;
            }

            if (row >= itemGridRows) {
                break;
            }
        }
    }

    private void calculateSpinGridOffset() {
        int mid = this.horizontalGUISize / 2;

        for (int i = 0; i < this.horizontalGUISize; i += this.spinGridElementSize) {

            if (i > mid) {
                this.spinGridItemOffset = (i - mid) / 2;
                break;
            }
        }
    }

    private void calculateVisibleColumns() {
        for (int i = -this.spinGridItemOffset; i < this.horizontalGUISize; i += this.spinGridElementSize) {
            this.spinGridVisibleColumns++;
        }
    }

    private void calculateWinningSlotColumn() {
        this.winningSlotColumn = this.spinGridColumns - this.spinGridVisibleColumns;
    }

    private void addSelector(SpinFlowLayout spinLayout, Color color) {
        BoxComponent selector = Components.box(Sizing.fixed(1), Sizing.fixed(this.spinGridElementSize - 2));
        selector.id("selector-box");
        selector.fill(true);
        selector.color(color);
        selector.positioning(Positioning.relative(50, 50));
        selector.zIndex(1000);
        spinLayout.child(selector);
    }

    private void animateRoll(FlowLayout subMain, FlowLayout spinLayout) {
        GridLayout grid = spinLayout.childById(GridLayout.class, "spin-grid");

        int leftX = grid.positioning().get().x + spinLayout.width() / 2 - spinLayout.padding().get().left();

        int y = this.winningSlotColumn * this.spinGridElementSize;
        int winningSlotItemLeftPos = leftX - y - 1 + spinGridItemOffset; // -1 to pass layout outline

        var gridAnimation = grid.positioning().animate(
                spinGridAnimationDuration,
                SpinEasing.randomExcluding(excludedEasings),
                Positioning.absolute(winningSlotItemLeftPos - random.nextBetween(0, this.spinGridElementSize - 3), grid.positioning().get().y));

        var rollAnimation = Animation.compose(gridAnimation);

        rollAnimation.forwards();

        gridAnimation.finished().subscribe((direction, looping) -> {
            animatePostRoll(grid, spinLayout);
            this.rollInProgress = false;
        });
    }

    private void animatePostRoll(GridLayout grid, FlowLayout spinLayout) {

        var waitAnim = grid.verticalSizing().animate(1000, Easing.QUADRATIC, Sizing.fixed(grid.verticalSizing().get().value));
        waitAnim.forwards();

        waitAnim.finished().subscribe((direction, looping) -> {
            var closeAnimV = spinLayout.verticalSizing().animate(500, Easing.QUADRATIC, Sizing.fixed(25));
            var closeAnimH = spinLayout.horizontalSizing().animate(500, Easing.QUADRATIC, Sizing.fixed(125));

            var closeAnimComposed = Animation.compose(closeAnimV, closeAnimH);
            closeAnimComposed.forwards();

            closeAnimV.finished().subscribe((direction1, looping1) -> {
                this.close();
                this.handler.getWinningDrop().ifPresentOrElse(
                        drop -> {
                            this.client.setScreen(new RewardScreen(drop));
                        },
                        () -> {
                            this.client.player.sendMessage(Text.literal("Encountered error while getting reward").formatted(Formatting.RED));
                            this.client.setScreen(null);
                        });
            });
        });
    }

    private FlowLayout createDropContainer(Drop drop, boolean isWinning, boolean isItemsGrid) {
        DropComponent dropComponent = DropComponentBuilder.create(16, this, this.getScreenHandler().getCaseProperties())
                .withDrop(drop)
                .asWinning(isWinning)
                .forItemsGrid(isItemsGrid)
                .build();

        return DropContainerBuilder.create(this.getScreenHandler().getCaseProperties()) // mb merge with other builder ^
                .forItemsGrid(isItemsGrid)
                .build(dropComponent);
    }


    @Override
    protected void build(FlowLayout rootComponent) {

        rootComponent.surface(Surface.VANILLA_TRANSLUCENT);
        rootComponent.sizing(Sizing.fill(100));

        FlowLayout subMainFlowLayout = Containers.verticalFlow(
                Sizing.fixed(this.horizontalGUISize),
                Sizing.fixed(this.buttonContainerVerticalSize + this.itemsContainerVerticalSize + spinContainerVerticalSize)
        );
        subMainFlowLayout.positioning(Positioning.relative(50, 50));

        subMainFlowLayout.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
        rootComponent.child(subMainFlowLayout);

        SpinFlowLayout spinLayout = new SpinFlowLayout(Sizing.fixed(this.horizontalGUISize), Sizing.fixed(this.spinContainerVerticalSize), this);
        spinLayout.surface(Surface.DARK_PANEL);

        FlowLayout scrollItemLayout = Containers.verticalFlow(Sizing.fixed(this.horizontalGUISize), Sizing.fixed(this.itemsContainerVerticalSize));
        scrollItemLayout.surface(Surface.DARK_PANEL);
        scrollItemLayout.padding(Insets.of(3, 3, 3, 3));

        FlowLayout buttonLayout = Containers.verticalFlow(Sizing.fixed(this.horizontalGUISize), Sizing.fixed(this.buttonContainerVerticalSize));
        buttonLayout.surface(Surface.DARK_PANEL);
        buttonLayout.padding(Insets.of(3, 3, 3, 3));

        subMainFlowLayout.child(spinLayout);

        subMainFlowLayout.child(scrollItemLayout);
        subMainFlowLayout.child(buttonLayout);

        ScrollContainer scrollContainer = Containers.verticalScroll(Sizing.fill(100), Sizing.fill(100), scrollItemLayout);

        GridLayout itemsGrid = Containers.grid(Sizing.fixed(90), Sizing.fill(90), itemGridRows, itemGridColumns);
        itemsGrid.horizontalAlignment(HorizontalAlignment.CENTER);
        itemsGrid.sizing(Sizing.fill(100), Sizing.content());

        scrollContainer.child(itemsGrid);
        scrollItemLayout.child(scrollContainer);

        calculateVisibleColumns();
        calculateWinningSlotColumn();

        calculateSpinGridOffset();

        fillSpinLayout(spinLayout);

        sortByRarity();
        fillItemsGrid(itemsGrid);

        addSelector(spinLayout, Color.ofArgb(0xbb949494));

        ButtonComponent buttonComponent = Components.button(Text.of("Spin"), (button) -> {

            this.rollInProgress = true;
            button.active = false;

            var scrollItemAnim = scrollItemLayout.verticalSizing().animate(
                    1000,
                    Easing.QUADRATIC,
                    Sizing.fixed(0)
            );

            var buttonAnim = buttonLayout.verticalSizing().animate(
                    1000,
                    Easing.QUADRATIC,
                    Sizing.fixed(0)
            );
            var closeAnimation = Animation.compose(scrollItemAnim, buttonAnim);

            C2S_RequestItemPacket packet = new C2S_RequestItemPacket(this.handler.getCaseId());
            handler.sendMessage(packet);

            scrollItemAnim.finished().subscribe((direction, var) -> {
                scrollItemLayout.remove();
                buttonLayout.remove();
                animateRoll(subMainFlowLayout, spinLayout);
            });

            closeAnimation.forwards();
        });
        buttonComponent.positioning(Positioning.relative(50, 50));

        buttonLayout.child(buttonComponent);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

}

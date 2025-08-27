package lolens.cases.core;

import io.wispforest.owo.ui.core.Color;
import lolens.cases.screens.core.SortType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Formatting;

public class LootCrateScreenProperties {

    private int horizontalGUISize = 250;
    private int spinContainerVerticalSize = 70;
    private int itemsContainerVerticalSize = 150;
    private int buttonContainerVerticalSize = 35;

    private int itemGridRows = 50;
    private int itemGridColumns = 6;
    private int itemsGridContainerMargin = 2;
    private int itemsGridElementSize = 30;

    private boolean itemsGridDisplayTooltip = true;
    private boolean itemsGridDisplayStackCount = true;
    private boolean itemsGridStacksOnHoverScale = true;
    private float itemsGridStacksOnHoverScaleMultiplier = 1.2f; // used if scales

    private int itemsGridOutlineColor = Color.ofFormatting(Formatting.DARK_GRAY).argb();

    private SortType sortType = SortType.ASC;

    private int spinGridColumns = 20;
    private int spinGridElementSize = 30;
    private int spinGridAnimationDuration = 4000; // ms

    private boolean spinGridDisplayTooltip = true;
    private boolean spinGridDisplayStackCount = true;
    boolean spinGridStacksOnHoverScale = false;
    float spinGridStacksOnHoverScaleMultiplier = 1.2f;

    private boolean spinGridStacksScaleWhileRolling = false;

    private int spinGridOutlineColor = Color.ofFormatting(Formatting.DARK_GRAY).argb();

    private SoundEvent slotChangeSound = SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;

    private int[] excludedEasings = new int[0];

    // used by chat commands to display all properties
    public String toStringLN() {
        return  "- Screen Properties:\n" +
                "itemGridRows=" + itemGridRows + "\n" +
                "itemGridColumns=" + itemGridColumns + "\n" +
                "itemsGridContainerMargin=" + itemsGridContainerMargin + "\n" +
                "itemsGridElementSize=" + itemsGridElementSize + "\n" +
                "horizontalGUISize=" + horizontalGUISize + "\n" +
                "spinContainerVerticalSize=" + spinContainerVerticalSize + "\n" +
                "itemsContainerVerticalSize=" + itemsContainerVerticalSize + "\n" +
                "buttonContainerVerticalSize=" + buttonContainerVerticalSize + "\n" +
                "itemsGridDisplayTooltip=" + itemsGridDisplayTooltip + "\n" +
                "itemsGridDisplayStackCount=" + itemsGridDisplayStackCount + "\n" +
                "spinGridDisplayStackCount=" + spinGridDisplayStackCount + "\n" +
                "spinGridDisplayTooltip=" + spinGridDisplayTooltip + "\n" +
                "spinGridStacksScaleWhileRolling=" + spinGridStacksScaleWhileRolling + "\n" +
                "itemsGridStacksOnHoverScale=" + itemsGridStacksOnHoverScale + "\n" +
                "itemsGridStacksOnHoverScaleMultiplier=" + itemsGridStacksOnHoverScaleMultiplier + "\n" +
                "spinGridColumns=" + spinGridColumns + "\n" +
                "spinGridElementSize=" + spinGridElementSize + "\n" +
                "spinGridAnimationDuration=" + spinGridAnimationDuration + "\n" +
                "spinGridOutlineColor=" + spinGridOutlineColor + "\n" +
                "itemsGridOutlineColor=" + itemsGridOutlineColor + "\n" +
                "slotChangeSound=" + slotChangeSound.getId() + "\n";
    }

    public int[] getExcludedEasings() {
        return excludedEasings;
    }

    public void setExcludedEasings(int[] excludedEasings) {
        this.excludedEasings = excludedEasings;
    }

    public int getItemGridRows() {
        return itemGridRows;
    }

    public int getItemGridColumns() {
        return itemGridColumns;
    }

    public int getItemsGridContainerMargin() {
        return itemsGridContainerMargin;
    }

    public int getItemsGridElementSize() {
        return itemsGridElementSize;
    }

    public int getHorizontalGUISize() {
        return horizontalGUISize;
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }

    public int getSpinContainerVerticalSize() {
        return spinContainerVerticalSize;
    }

    public int getItemsContainerVerticalSize() {
        return itemsContainerVerticalSize;
    }

    public int getButtonContainerVerticalSize() {
        return buttonContainerVerticalSize;
    }

    public boolean isItemsGridDisplayTooltip() {
        return itemsGridDisplayTooltip;
    }

    public boolean isSpinGridStacksOnHoverScale() {
        return spinGridStacksOnHoverScale;
    }

    public void setSpinGridStacksOnHoverScale(boolean spinGridStacksOnHoverScale) {
        this.spinGridStacksOnHoverScale = spinGridStacksOnHoverScale;
    }

    public float getSpinGridStacksOnHoverScaleMultiplier() {
        return spinGridStacksOnHoverScaleMultiplier;
    }

    public void setSpinGridStacksOnHoverScaleMultiplier(float spinGridStacksOnHoverScaleMultiplier) {
        this.spinGridStacksOnHoverScaleMultiplier = spinGridStacksOnHoverScaleMultiplier;
    }

    public boolean isItemsGridDisplayStackCount() {
        return itemsGridDisplayStackCount;
    }

    public boolean isSpinGridDisplayStackCount() {
        return spinGridDisplayStackCount;
    }

    public boolean isSpinGridDisplayTooltip() {
        return spinGridDisplayTooltip;
    }

    public boolean isSpinGridStacksScaleWhileRolling() {
        return spinGridStacksScaleWhileRolling;
    }

    public boolean isItemsGridStacksOnHoverScale() {
        return itemsGridStacksOnHoverScale;
    }

    public float getItemsGridStacksOnHoverScaleMultiplier() {
        return itemsGridStacksOnHoverScaleMultiplier;
    }

    public int getSpinGridColumns() {
        return spinGridColumns;
    }

    public int getSpinGridElementSize() {
        return spinGridElementSize;
    }

    public int getSpinGridAnimationDuration() {
        return spinGridAnimationDuration;
    }

    public int getSpinGridOutlineColor() {
        return spinGridOutlineColor;
    }

    public int getItemsGridOutlineColor() {
        return itemsGridOutlineColor;
    }

    public SoundEvent getSlotChangeSound() {
        return slotChangeSound;
    }

    public void setItemGridRows(int itemGridRows) {
        this.itemGridRows = itemGridRows;
    }

    public void setItemGridColumns(int itemGridColumns) {
        this.itemGridColumns = itemGridColumns;
    }

    public void setItemsGridContainerMargin(int itemsGridContainerMargin) {
        this.itemsGridContainerMargin = itemsGridContainerMargin;
    }

    public void setItemsGridElementSize(int itemsGridElementSize) {
        this.itemsGridElementSize = itemsGridElementSize;
    }

    public void setHorizontalGUISize(int horizontalGUISize) {
        this.horizontalGUISize = horizontalGUISize;
    }

    public void setSpinContainerVerticalSize(int spinContainerVerticalSize) {
        this.spinContainerVerticalSize = spinContainerVerticalSize;
    }

    public void setItemsContainerVerticalSize(int itemsContainerVerticalSize) {
        this.itemsContainerVerticalSize = itemsContainerVerticalSize;
    }

    public void setButtonContainerVerticalSize(int buttonContainerVerticalSize) {
        this.buttonContainerVerticalSize = buttonContainerVerticalSize;
    }

    public void setItemsGridDisplayTooltip(boolean itemsGridDisplayTooltip) {
        this.itemsGridDisplayTooltip = itemsGridDisplayTooltip;
    }

    public void setItemsGridDisplayStackCount(boolean itemsGridDisplayStackCount) {
        this.itemsGridDisplayStackCount = itemsGridDisplayStackCount;
    }

    public void setSpinGridDisplayStackCount(boolean spinGridDisplayStackCount) {
        this.spinGridDisplayStackCount = spinGridDisplayStackCount;
    }

    public void setSpinGridDisplayTooltip(boolean spinGridDisplayTooltip) {
        this.spinGridDisplayTooltip = spinGridDisplayTooltip;
    }

    public void setSpinGridStacksScaleWhileRolling(boolean spinGridStacksScaleWhileRolling) {
        this.spinGridStacksScaleWhileRolling = spinGridStacksScaleWhileRolling;
    }

    public void setItemsGridStacksOnHoverScale(boolean itemsGridStacksOnHoverScale) {
        this.itemsGridStacksOnHoverScale = itemsGridStacksOnHoverScale;
    }

    public void setItemsGridStacksOnHoverScaleMultiplier(float itemsGridStacksOnHoverScaleMultiplier) {
        this.itemsGridStacksOnHoverScaleMultiplier = itemsGridStacksOnHoverScaleMultiplier;
    }

    public void setSpinGridColumns(int spinGridColumns) {
        this.spinGridColumns = spinGridColumns;
    }

    public void setSpinGridElementSize(int spinGridElementSize) {
        this.spinGridElementSize = spinGridElementSize;
    }

    public void setSpinGridAnimationDuration(int spinGridAnimationDuration) {
        this.spinGridAnimationDuration = spinGridAnimationDuration;
    }

    public void setSpinGridOutlineColor(int spinGridOutlineColor) {
        this.spinGridOutlineColor = spinGridOutlineColor;
    }

    public void setItemsGridOutlineColor(int itemsGridOutlineColor) {
        this.itemsGridOutlineColor = itemsGridOutlineColor;
    }

    public void setSlotChangeSound(SoundEvent slotChangeSound) {
        this.slotChangeSound = slotChangeSound;
    }

}

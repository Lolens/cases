package lolens.cases.screens.core.components;

import io.wispforest.owo.ui.base.BaseComponent;
import io.wispforest.owo.ui.component.BoxComponent;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.AnimatableProperty;
import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.Sizing;
import lolens.cases.screens.core.Drop;
import lolens.cases.screens.core.DropRarity;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DropComponent extends BaseComponent {


    //item
    private int textOffsetX;
    private int textOffsetY;

    // on hover scaling
    private float currentOnHoverScale = 1.0f;
    private float currentTargetOnHoverScale = 1.0f;
    private float maxOnHoverScale = 1.2f;
    private float onHoverScaleSpeed = 0.3f;

    private boolean scalesOnHover = false;

    public DropComponent scalesOnHover(boolean scalesOnHover) {
        this.scalesOnHover = scalesOnHover;
        return this;
    }

    // roll scaling
    private boolean scalesWhileRolling = false;
    private FlowLayout gridParent;

    private float gridParentMid;
    private float gridParentWidth;
    private float rollScale = 1.0f;
    private float minScale = 0.85f;
    private float maxScale = 1.15f;


    public void setScalesWhileRolling(boolean scalesWhileRolling) {
        this.scalesWhileRolling = scalesWhileRolling;
    }

    public void setMaxOnHoverScale(float maxOnHoverScale) {
        this.maxOnHoverScale = maxOnHoverScale;
    }

    private final int defaultOffset = 17;
    // to position numbers as in vanilla you may use 16, but it does not look good in cases gui

    protected static final Matrix4f ITEM_SCALING = new Matrix4f().scaling(16, -16, 16);

    protected final VertexConsumerProvider.Immediate entityBuffers;
    protected final ItemRenderer itemRenderer;
    protected ItemStack stack;
    protected boolean showOverlay = false;
    protected boolean setTooltipFromStack = false;

    // box
    protected boolean fill = true;
    protected BoxComponent.GradientDirection direction = BoxComponent.GradientDirection.BOTTOM_TO_TOP;

    protected final AnimatableProperty<Color> startColor = AnimatableProperty.of(DropRarity.BLANK.getColor());
    protected final AnimatableProperty<Color> endColor = AnimatableProperty.of(DropRarity.BLANK.getColor());

    private DropComponent(Sizing sizing, ItemStack stack, Screen parentScreen) {
        this.sizing(Sizing.fixed(sizing.value));
        this.entityBuffers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        this.itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        this.stack = stack;
    }

    public float calculateScale(float elementX) {
        if (this.parent() != null) {
            if (this.gridParent == null) {
                this.gridParent = (FlowLayout) this.parent().parent().parent();
                this.gridParentWidth = this.gridParent.x() + this.gridParent.width();
                this.gridParentMid = this.gridParent.x() + (float) this.gridParent.width() / 2;
            }
        }

        float distanceFromCenter = Math.abs(elementX - gridParentMid);
        float normalizedDistance = distanceFromCenter / (gridParentWidth / 2);
        normalizedDistance = Math.min(1.0f, normalizedDistance);

        return maxScale - (normalizedDistance * 5.0f * (maxScale - minScale));
    }

    public DropComponent(int sizing, ItemStack stack, Screen parentScreen) {
        this(Sizing.fixed(sizing), stack, parentScreen);
    }

    public DropComponent(int sizing, Drop drop, Screen parentScreen) {
        this(sizing, drop.getStack(), parentScreen);
        this.startColor(drop.getRarity().getColor());
    }


    // calculate text offset
    private void autoOffset() {
        int size = Math.min(this.verticalSizing().get().value, this.horizontalSizing().get().value);
        this.setTextOffset(size - defaultOffset);
    }

    private void setTextOffset(int textOffset) {
        this.textOffsetX = textOffset;
        this.textOffsetY = textOffset;
    }

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);
        this.startColor.update(delta);
        this.endColor.update(delta);

        if (scalesOnHover) {
            if (this.hovered) {
                currentTargetOnHoverScale = maxOnHoverScale;
            } else {
                currentTargetOnHoverScale = 1.0f;
            }

            currentOnHoverScale += (currentTargetOnHoverScale - currentOnHoverScale) * (onHoverScaleSpeed * delta);
        }

        if (scalesWhileRolling) {
            this.rollScale = this.calculateScale(this.x() + (float) this.width() / 2);
        }
    }

    @Override
    public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
        // box
        final int startColor = this.startColor.get().argb();
        final int endColor = this.endColor.get().argb();

        if (this.fill) {
            switch (this.direction) {
                case TOP_TO_BOTTOM -> context.drawGradientRect(this.x, this.y, this.width, this.height,
                        startColor, startColor, endColor, endColor);
                case RIGHT_TO_LEFT -> context.drawGradientRect(this.x, this.y, this.width, this.height,
                        endColor, startColor, startColor, endColor);
                case BOTTOM_TO_TOP -> context.drawGradientRect(this.x, this.y, this.width, this.height,
                        endColor, endColor, startColor, startColor);
                case LEFT_TO_RIGHT -> context.drawGradientRect(this.x, this.y, this.width, this.height,
                        startColor, endColor, endColor, startColor);
            }
        } else {
            context.drawRectOutline(this.x, this.y, this.width, this.height, startColor);
        }

        // item
        final boolean notSideLit = !this.itemRenderer.getModel(this.stack, null, null, 0).isSideLit();
        if (notSideLit) {
            DiffuseLighting.disableGuiDepthLighting();
        }

        var matrices = context.getMatrices();
        matrices.push();

        // Translate to the root of the component
        matrices.translate(this.x, this.y, 100);

        // Scale according to component size and translate to the center
        matrices.scale(this.width / 16f, this.height / 16f, 1);
        matrices.translate(8.0, 8.0, 0.0);

        // Vanilla scaling and y inversion
        if (notSideLit) {
            matrices.scale(16, -16, 16);
        } else {
            matrices.multiplyPositionMatrix(ITEM_SCALING);
        }

        if (scalesOnHover) {
            matrices.scale(currentOnHoverScale, currentOnHoverScale, currentOnHoverScale);
        }

        if (scalesWhileRolling) {
            matrices.scale(rollScale, rollScale, rollScale);
        }

        this.itemRenderer.renderItem(this.stack, ModelTransformationMode.GUI, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, matrices, entityBuffers, null, 0);
        this.entityBuffers.draw();

        // Clean up
        matrices.pop();

        if (this.showOverlay) {
            this.autoOffset();

            int posX = this.x + this.textOffsetX;
            int posY = this.y + this.textOffsetY;

            context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, this.stack, posX, posY);
        }
        if (notSideLit) {
            DiffuseLighting.enableGuiDepthLighting();
        }
    }

    public DropComponent fill(boolean fill) {
        this.fill = fill;
        return this;
    }


    public boolean fill() {
        return this.fill;
    }

    public DropComponent direction(BoxComponent.GradientDirection direction) {
        this.direction = direction;
        return this;
    }

    public BoxComponent.GradientDirection direction() {
        return this.direction;
    }

    public DropComponent color(Color color) {
        this.startColor.set(color);
        this.endColor.set(color);
        return this;
    }

    public DropComponent startColor(Color startColor) {
        this.startColor.set(startColor);
        return this;
    }

    public AnimatableProperty<Color> startColor() {
        return this.startColor;
    }

    public DropComponent endColor(Color endColor) {
        this.endColor.set(endColor);
        return this;
    }

    public AnimatableProperty<Color> endColor() {
        return this.endColor;
    }

    public static List<TooltipComponent> tooltipFromItem(ItemStack stack, @Nullable PlayerEntity player, @Nullable TooltipContext context) {
        if (context == null) {
            context = MinecraftClient.getInstance().options.advancedItemTooltips ? TooltipContext.ADVANCED : TooltipContext.BASIC;
        }

        var tooltip = new ArrayList<TooltipComponent>();
        stack.getTooltip(player, context)
                .stream()
                .map(Text::asOrderedText)
                .map(TooltipComponent::of)
                .forEach(tooltip::add);

        stack.getTooltipData().ifPresent(data -> {
            tooltip.add(1, Objects.requireNonNullElseGet(
                    TooltipComponentCallback.EVENT.invoker().getComponent(data),
                    () -> TooltipComponent.of(data)
            ));
        });

        return tooltip;
    }

    protected void updateTooltipForStack() {
        if (!this.setTooltipFromStack) return;

        if (!this.stack.isEmpty()) {
            this.tooltip(tooltipFromItem(this.stack, MinecraftClient.getInstance().player, null));
        } else {
            this.tooltip((List<TooltipComponent>) null);
        }
    }

    public DropComponent setTooltipFromStack(boolean setTooltipFromStack) {
        this.setTooltipFromStack = setTooltipFromStack;
        this.updateTooltipForStack();

        return this;
    }

    public boolean setTooltipFromStack() {
        return setTooltipFromStack;
    }

    public DropComponent stack(ItemStack stack) {
        this.stack = stack;
        this.updateTooltipForStack();

        return this;
    }

    public ItemStack stack() {
        return this.stack;
    }

    public DropComponent showOverlay(boolean drawOverlay) {
        this.showOverlay = drawOverlay;
        return this;
    }

    public boolean showOverlay() {
        return this.showOverlay;
    }

}

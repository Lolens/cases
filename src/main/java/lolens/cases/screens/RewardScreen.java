package lolens.cases.screens;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.core.*;
import lolens.cases.screens.core.Drop;
import lolens.cases.screens.core.components.DropComponent;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;


public class RewardScreen extends BaseOwoScreen<FlowLayout> {

    private Drop drop;

    public RewardScreen(Drop drop) {
        super();
        this.drop = drop;
    }

    @Override
    protected @NotNull OwoUIAdapter createAdapter() {
        return OwoUIAdapter.create(this, Containers::horizontalFlow);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_E) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void build(FlowLayout mainContainer) {

        mainContainer.surface(Surface.VANILLA_TRANSLUCENT);

        FlowLayout subMainContainer = Containers.verticalFlow(Sizing.fixed(125), Sizing.fixed(25));
        subMainContainer.positioning(Positioning.relative(50, 50));
        subMainContainer.surface(Surface.DARK_PANEL);

        DropComponent dropComponent = new DropComponent(50, drop, this);
        dropComponent.verticalSizing(Sizing.fixed(0));
        dropComponent.positioning(Positioning.relative(50, 55));
        subMainContainer.child(dropComponent);

        LabelComponent wonText = Components.label(Text.of("You've got\n" + drop.getRarity().name() + " drop!"));
        wonText.horizontalTextAlignment(HorizontalAlignment.CENTER);

        LabelComponent itemText = Components.label(Text.of(drop.getStack().getCount() + "x " + drop.getStack().getName().getString()));
        itemText.horizontalTextAlignment(HorizontalAlignment.CENTER);

        wonText.positioning(Positioning.relative(50, 10));
        itemText.positioning(Positioning.relative(50, 90));

        subMainContainer.child(wonText);
        subMainContainer.child(itemText);
        mainContainer.child(subMainContainer);

        var itemExpandAnim = dropComponent.verticalSizing().animate(250, Easing.QUADRATIC, Sizing.fixed(50));
        var containerExpandAnim = subMainContainer.verticalSizing().animate(500, Easing.QUADRATIC, Sizing.fixed(125));

        var expandAnim = Animation.compose(itemExpandAnim, containerExpandAnim);
        expandAnim.forwards();

    }
}

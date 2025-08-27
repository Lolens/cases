package lolens.cases.screens.core.components;

import lolens.cases.screens.LootCrateScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class WinningDropComponent extends DropComponent {
    public WinningDropComponent(int sizing, LootCrateScreen parentScreen) {
        super(sizing, new ItemStack(Items.BARRIER), parentScreen);
        this.parentScreen = parentScreen;
    }

    private boolean dropSetOnce;
    private final LootCrateScreen parentScreen;

    @Override
    public void update(float delta, int mouseX, int mouseY) {
        super.update(delta, mouseX, mouseY);

        this.parentScreen.getScreenHandler().getWinningDrop().ifPresent(drop -> { // checking if handler got S2C packet and has set winning drop
            if (!dropSetOnce) {
                this.stack = drop.getStack();
                this.startColor(drop.getRarity().getColor());
                this.dropSetOnce = true;
            }
        });
    }
}

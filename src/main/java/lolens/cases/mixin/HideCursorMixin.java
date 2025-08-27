package lolens.cases.mixin;

import lolens.cases.screens.LootCrateScreen;
import lolens.cases.screens.RewardScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.glfw.GLFW.glfwSetInputMode;

@Mixin(MinecraftClient.class)
public class HideCursorMixin {

    // hides cursor at specific screen (cursor hiding method from some mod that hides cursor)
    @Inject(at = @At("TAIL"), method = "setScreen")
    private void setScreen(Screen screen, CallbackInfo ci) {

        if (screen instanceof RewardScreen) {
            long handle = MinecraftClient.getInstance().getWindow().getHandle();
            glfwSetInputMode(handle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        }
    }

}

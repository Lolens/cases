package lolens.cases.compat.rei;

import lolens.cases.screens.LootCrateScreen;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.OverlayDecider;
import me.shedaniel.rei.api.client.registry.screen.OverlayRendererProvider;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.Nullable;

public class REIIntegration implements REIClientPlugin {

    @SuppressWarnings("UnstableApiUsage")
    private static @Nullable OverlayRendererProvider.Sink renderSink = null;

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerDecider(new OverlayDecider() {

            @Override
            public <R extends Screen> boolean isHandingScreen(Class<R> aClass) {

                return LootCrateScreen.class == aClass;
            }

            @Override
            public <R extends Screen> ActionResult shouldScreenBeOverlaid(R screen) {
                return ActionResult.FAIL;
            }

            @Override
            public double getPriority() {
                return 10;
            }

            @Override
            @SuppressWarnings("UnstableApiUsage")
            public OverlayRendererProvider getRendererProvider() {
                return new OverlayRendererProvider() {
                    @Override
                    public void onApplied(Sink sink) {
                        renderSink = null;
                    }

                    @Override
                    public void onRemoved() {
                        renderSink = null;
                    }
                };
            }

        });
    }
}

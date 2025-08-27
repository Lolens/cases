package lolens.cases;

import lolens.cases.screens.LootCrateScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class CasesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        HandledScreens.register(Cases.CASE_SCREEN_HANDLER, LootCrateScreen::new);

        // BuiltinItemRendererRegistry.INSTANCE.register(ModItems.CASE, new LootCrateItemRenderer("case")); // Not today :(

    }

}

package lolens.cases;

import lolens.cases.command.Commands;
import lolens.cases.config.CasesConfig;
import lolens.cases.core.LootCrateManager;
import lolens.cases.handlers.LootCrateScreenHandler;
import lolens.cases.item.CreativeInventoryGroup;
import lolens.cases.item.ModItems;
import lolens.cases.network.NetworkHandlers;
import lolens.cases.network.Serializers;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;


public class Cases implements ModInitializer {
    public static final String MOD_ID = "cases";

    public static final ScreenHandlerType<LootCrateScreenHandler> CASE_SCREEN_HANDLER = new ExtendedScreenHandlerType<>(LootCrateScreenHandler::new);

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final CasesConfig CONFIG = CasesConfig.createAndLoad();

    @Override
    public void onInitialize() {

        setLoggerLevel();

        CONFIG.optionForKey(CONFIG.keys.LOGGER_LEVEL).observe(o -> {
            setLoggerLevel();
        });

        ModItems.register();
        CreativeInventoryGroup.register();
        Serializers.register();
        NetworkHandlers.register();
        Commands.register();

        Registry.register(Registries.SCREEN_HANDLER, Identifier.of(MOD_ID, "case_screen_handler"), CASE_SCREEN_HANDLER);
        LootCrateManager.getInstance().refresh();
        LOGGER.info("Cases initialized!");
    }

    private void setLoggerLevel() {
        switch (CONFIG.LOGGER_LEVEL()) {
            case INFO -> Configurator.setLevel(LOGGER, Level.INFO);
            case WARN -> Configurator.setLevel(LOGGER, Level.WARN);
            default -> Configurator.setLevel(LOGGER, Level.ERROR);
        }
    }
}
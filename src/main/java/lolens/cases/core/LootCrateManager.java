package lolens.cases.core;

import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import io.wispforest.owo.moddata.ModDataLoader;
import lolens.cases.Cases;
import lolens.cases.util.LootCrateConsumer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class LootCrateManager {
    private static final LootCrateManager INSTANCE = new LootCrateManager();
    private final Map<String, LootCrate> lootCratesMap = new HashMap<>();

    private LootCrateManager() {
    }

    public static LootCrateManager getInstance() {
        return INSTANCE;
    }

    // adds crate to the manager
    public void addLootCrate(String lootCrateId, LootCrate lootCrate) {
        lootCratesMap.put(lootCrateId, lootCrate);
    }

    // get all loot crates id
    public ArrayList<String> getLootCrateIds() {
        return new ArrayList<>(lootCratesMap.keySet());
    }

    // get loot crate by its id
    @Nullable
    public LootCrate getLootCrate(String caseId) {
        return lootCratesMap.get(caseId);
    }

    // get loot crate item properties by its id
    @Nullable
    public LootCrateItemProperties getLootCrateItemProperties(String lootCrateId) {
        LootCrate lootCrate = getLootCrate(lootCrateId);

        if (lootCrate != null) {
            // todo cache if possible
            return lootCrate.getItemProperties();
        }
        Cases.LOGGER.warn("Tried getting item properties for caseId={} but theres no loot crate with that id.", lootCrateId);
        return null;
    }


    // get loot crate GUI properties by its id that are applied to screen when player use loot crate
    @Nullable
    public LootCrateScreenProperties getLootCrateScreenProperties(String lootCrateId) {
        LootCrate lootCrate = getLootCrate(lootCrateId);
        if (lootCrate != null) return lootCrate.getScreenProperties();

        Cases.LOGGER.warn("Tried getting screen properties for caseId={} but theres no loot crate with that id.", lootCrateId);
        return null;
    }

    // todo safe refresh (abort refresh and preserve old data if encountered error)

    // clears all existing loot crate data from manager and updates with new .json files
    // DOES NOT SYNC
    public void refresh() {
        clear();

        try {
        ModDataLoader.load(new LootCrateConsumer());
        } catch (JsonSyntaxException jsonSyntaxException) {
            Cases.LOGGER.warn("Encountered error while loading loot crates data files. Ensure that these files are not malformed.", jsonSyntaxException);
        } catch (JsonIOException jsonIOException) {
            Cases.LOGGER.warn("Parser was unable to read loot crate data files.", jsonIOException);
        }
    }

    // clears all existing loot crate data from manager
    public void clear() {
        lootCratesMap.clear();
    }
}

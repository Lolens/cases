package lolens.cases.util;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lolens.cases.*;
import lolens.cases.core.*;
import lolens.cases.screens.core.*;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.Registries;
import net.minecraft.sound.*;
import net.minecraft.util.*;
import io.wispforest.owo.moddata.ModDataConsumer;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LootCrateConsumer implements ModDataConsumer {
    private static final SoundEvent DEFAULT_SOUND = SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP;
    private static final int DEFAULT_COLOR = 0xFFFFFFFF;

    @Override
    public String getDataSubdirectory() {
        return "lootcrates";
    }

    @Override
    public void acceptParsedFile(Identifier id, JsonObject object) {
        Cases.LOGGER.info("Loading file: {}", id.getPath());

        try {
            LootCrate lootCrate = new LootCrate();

            parseItemProperties(object.getAsJsonObject("itemProperties"), lootCrate.getItemProperties());

            parseScreenProperties(object.getAsJsonObject("screenProperties"), lootCrate.getScreenProperties());

            parseDrops(object.get("drops"), lootCrate);
            if (lootCrate.getDrops().isEmpty())
                throw new IllegalArgumentException("Loot crate must contain at least 1 drop");

            String crateId = lootCrate.getItemProperties().getId();
            LootCrateManager.getInstance().addLootCrate(crateId, lootCrate);
            Cases.LOGGER.info("Successfully loaded loot crate with id: {}", crateId);
        } catch (Exception e) {
            Cases.LOGGER.error("Cases encountered error while parsing file: {}", id.getPath());
            Cases.LOGGER.error("The exception is: ", e);
        }
    }

    private void parseItemProperties(JsonObject json, LootCrateItemProperties props) {
        if (json == null) return;

        Optional.ofNullable(json.get("id")).ifPresentOrElse(
                e -> props.setId(e.getAsString()),
                () -> {
                    throw new IllegalArgumentException("ItemProperties must have an 'id' field");
                }
        );

        Optional.ofNullable(json.get("rarity")).ifPresent(e ->
                props.setRarity(Rarity.valueOf(e.getAsString().toUpperCase()))
        );

        parseColor(json.getAsJsonObject("color")).ifPresent(props::setColor);
        Optional.ofNullable(json.get("name")).ifPresent(e -> props.setName(e.getAsString()));
        Optional.ofNullable(json.get("glowing")).ifPresent(e -> props.setGlowing(e.getAsBoolean()));
    }

    private void parseScreenProperties(JsonObject json, LootCrateScreenProperties props) {
        if (json == null) return;

        parseGeneralGuiProperties(json, props);
        parseDisplayGridProperties(json.getAsJsonObject("displayGrid"), props);
        parseSpinGridProperties(json.getAsJsonObject("spinGrid"), props);
    }

    private void parseGeneralGuiProperties(JsonObject json, LootCrateScreenProperties props) {
        getInt(json, "horizontalGUISize").ifPresent(props::setHorizontalGUISize);
        getInt(json, "spinContainerVerticalSize").ifPresent(props::setSpinContainerVerticalSize);
        getInt(json, "itemsContainerVerticalSize").ifPresent(props::setItemsContainerVerticalSize);
        getInt(json, "buttonContainerVerticalSize").ifPresent(props::setButtonContainerVerticalSize);
    }

    private void parseDisplayGridProperties(JsonObject json, LootCrateScreenProperties props) {
        if (json == null) return;

        getInt(json, "rows").ifPresent(props::setItemGridRows);
        getInt(json, "columns").ifPresent(props::setItemGridColumns);
        getInt(json, "elementMargin").ifPresent(props::setItemsGridContainerMargin);
        getInt(json, "elementSize").ifPresent(props::setItemsGridElementSize);

        getBoolean(json, "showStackTooltip").ifPresent(props::setItemsGridDisplayTooltip);
        getBoolean(json, "showStackCount").ifPresent(props::setItemsGridDisplayStackCount);
        getBoolean(json, "scaleOnHover").ifPresent(props::setItemsGridStacksOnHoverScale);
        getFloat(json, "scaleOnHoverMultiplier").ifPresent(props::setItemsGridStacksOnHoverScaleMultiplier);

        parseColor(json.getAsJsonObject("outlineColor")).ifPresent(props::setItemsGridOutlineColor);
        getString(json, "sorting").ifPresent(s -> props.setSortType(parseSortType(s)));
    }

    private void parseSpinGridProperties(JsonObject json, LootCrateScreenProperties props) {
        if (json == null) return;

        getInt(json, "columns").ifPresent(props::setSpinGridColumns);
        getInt(json, "elementSize").ifPresent(props::setSpinGridElementSize);
        getInt(json, "animationDuration").ifPresent(props::setSpinGridAnimationDuration);

        getBoolean(json, "showStackTooltip").ifPresent(props::setSpinGridDisplayTooltip);
        getBoolean(json, "showStackCount").ifPresent(props::setSpinGridDisplayStackCount);
        getBoolean(json, "scaleOnHover").ifPresent(props::setSpinGridStacksOnHoverScale);
        getFloat(json, "scaleOnHoverMultiplier").ifPresent(props::setSpinGridStacksOnHoverScaleMultiplier);
        getBoolean(json, "scaleWhileRolling").ifPresent(props::setSpinGridStacksScaleWhileRolling);

        parseColor(json.getAsJsonObject("outlineColor")).ifPresent(props::setSpinGridOutlineColor);
        getString(json, "slotChangeSound").ifPresent(s -> props.setSlotChangeSound(parseSound(s)));
        parseEasingNames(json.getAsJsonArray("excludedEasings")).ifPresent(props::setExcludedEasings);
    }

    private void parseDrops(JsonElement element, LootCrate lootCrate) {
        if (element == null) return;

        JsonArray drops = element.getAsJsonArray();
        int totalWeight = 0;

        for (JsonElement dropElement : drops) {
            JsonObject drop = dropElement.getAsJsonObject();
            ItemStack stack = createItemStack(drop);
            DropRarity rarity = parseDropRarity(drop.get("rarity"));
            int weight = drop.get("weight").getAsInt();

            lootCrate.addWeightedDrop(stack, rarity, weight);
            totalWeight += weight;
        }

        if (totalWeight <= 0) {
            throw new IllegalArgumentException("Total drops weight must be greater than zero");
        }
    }

    private ItemStack createItemStack(JsonObject drop) {
        if (!drop.has("id")) {
            throw new JsonSyntaxException("Item entry must have an 'id' field");
        }

        String itemId = drop.get("id").getAsString().toLowerCase();

        int count = drop.has("count") ? drop.get("count").getAsInt() : 1;
        if (count < 1) {
            throw new JsonSyntaxException("Item count must be at least 1 or blank");
        }

        Identifier itemIdentifier = Identifier.tryParse(itemId);
        if (itemIdentifier == null) {
            throw new JsonSyntaxException("Invalid item ID: " + itemId);
        }

        Item item = Registries.ITEM.get(itemIdentifier);
        if (item == Items.AIR && !itemId.equals("minecraft:air")) { // if registry does not have item it returns air
            throw new JsonSyntaxException("Unknown item: " + itemId);
        }

        ItemStack itemStack = new ItemStack(item, count);

        if (drop.has("nbt")) {
            try {
                NbtCompound nbt = parseNbt(drop.get("nbt"));
                if (nbt != null) {
                    itemStack.setNbt(nbt);
                }
            } catch (Exception e) {
                Cases.LOGGER.error("Failed to parse NBT for item {}: {}", itemId, e.getMessage());
            }
        }

        return itemStack;
    }

    @Nullable
    private NbtCompound parseNbt(@Nullable JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }

        try {
            return StringNbtReader.parse(element.getAsString());
        } catch (CommandSyntaxException | JsonParseException e) {
            Cases.LOGGER.error("Invalid NBT data: {}", element.toString(), e);
            return null;
        }
    }

    private DropRarity parseDropRarity(JsonElement element) {
        String rarity = element.getAsString().toUpperCase();
        return Optional.ofNullable(DropRarity.getByName(rarity))
                .orElseThrow(() -> new IllegalArgumentException("Rarity '" + rarity + "' not found"));
    }

    private SortType parseSortType(String value) {
        return switch (value.toUpperCase()) {
            case "ASC", "ASCENDING" -> SortType.ASC;
            case "DESC", "DESCENDING" -> SortType.DESC;
            case "NONE" -> SortType.NONE;
            default -> throw new IllegalArgumentException("Sorting type '" + value + "' not found");
        };
    }


    private Optional<int[]> parseEasingNames(JsonArray array) {
        if (array == null) return Optional.empty();
        return Optional.of(array.asList().stream()
                .map(JsonElement::getAsString)
                .mapToInt(SpinEasing::getIndexByName)
                .toArray());
    }

    private SoundEvent parseSound(String soundId) {
        return Optional.ofNullable(Identifier.tryParse(soundId.toLowerCase()))
                .map(SoundEvent::of)
                .orElseThrow(() -> new IllegalArgumentException("Sound with id '" + soundId + "' not found."));
    }

    private Optional<Integer> parseColor(JsonObject json) {
        if (json == null) return Optional.empty();

        var hex = json.get("hex");
        if (hex != null) {
            String str = hex.getAsString();
            return Optional.of(Integer.parseUnsignedInt(str, 16));
        } else {
            var a = json.get("A");
            var r = json.get("R");
            var g = json.get("G");
            var b = json.get("B");
            if (a != null && r != null && g != null && b != null) {
                return Optional.of(ColorHelper.Argb.getArgb(a.getAsInt(), r.getAsInt(), g.getAsInt(), b.getAsInt()));
            }
        }
        return Optional.of(DEFAULT_COLOR);
    }

    private Optional<String> getString(JsonObject json, String key) {
        return Optional.ofNullable(json.get(key)).map(JsonElement::getAsString);
    }

    private Optional<Integer> getInt(JsonObject json, String key) {
        return Optional.ofNullable(json.get(key)).map(JsonElement::getAsInt);
    }

    private Optional<Float> getFloat(JsonObject json, String key) {
        return Optional.ofNullable(json.get(key)).map(JsonElement::getAsFloat);
    }

    private Optional<Boolean> getBoolean(JsonObject json, String key) {
        return Optional.ofNullable(json.get(key)).map(JsonElement::getAsBoolean);
    }
}
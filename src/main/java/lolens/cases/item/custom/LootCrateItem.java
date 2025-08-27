package lolens.cases.item.custom;

import lolens.cases.Cases;
import lolens.cases.core.LootCrateItemProperties;
import lolens.cases.core.LootCrateManager;
import lolens.cases.util.CasesUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LootCrateItem extends Item {
    public LootCrateItem(Settings settings) {
        super(settings);
    }

    // todo get rid of "case" in the code as it clashes with reserved name

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (!world.isClient && getLootCrateItemProperties(stack) != null) {
            CasesUtils.openHandledScreen(user, getLootCrateId(stack));
        }
        return TypedActionResult.success(user.getStackInHand(hand), world.isClient());
    }

    @Nullable
    public static String getLootCrateId(ItemStack stack) {
        if (!stack.hasNbt()) {
            logInvalidNbt(stack, "missing NBT data");
            return null;
        }

        NbtCompound nbt = stack.getNbt();
        if (!nbt.contains("caseId")) {
            logInvalidNbt(stack, "missing caseId tag");
            return null;
        }

        String caseId = nbt.getString("caseId");
        if (caseId.isEmpty()) {
            logInvalidNbt(stack, "empty caseId");
            return null;
        }

        return caseId;
    }


    private static LootCrateItemProperties getLootCrateItemProperties(ItemStack stack) {
        // Получаем ID кейса (с уже встроенной валидацией)
        String caseId = getLootCrateId(stack);
        if (caseId == null) {
            return null;
        }

        // Получаем свойства
        LootCrateItemProperties properties = LootCrateManager.getInstance().getLootCrateItemProperties(caseId);
        if (properties == null) {
            logInvalidNbt(stack, "loot crate properties is null");
            return null;
        }

        return properties;
    }

    private static void logInvalidNbt(ItemStack stack, String reason) {
        String holderName;
        try {
            holderName = stack.getHolder() != null
                    ? stack.getHolder().getName().getString()
                    : "unknown entity";
        } catch (Exception e) {
            holderName = "error_getting_holder";
        }

        Cases.LOGGER.warn("{} tried using loot crate with invalid NBT: {}", holderName, reason);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        LootCrateItemProperties properties = getLootCrateItemProperties(stack);
        return properties != null && properties.isGlowing();
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        LootCrateItemProperties properties = getLootCrateItemProperties(stack);
        return properties != null ? properties.getRarity() : Rarity.COMMON;
    }

    @Override
    public Text getName(ItemStack stack) {
        LootCrateItemProperties properties = getLootCrateItemProperties(stack);
        return properties != null ? Text.of(properties.getName()) : Text.of("ERROR: Can't get item properties!");
    }


}

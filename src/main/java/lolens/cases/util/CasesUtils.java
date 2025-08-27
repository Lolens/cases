package lolens.cases.util;

import lolens.cases.Cases;
import lolens.cases.core.LootCrate;
import lolens.cases.core.LootCrateItemProperties;
import lolens.cases.core.LootCrateManager;
import lolens.cases.core.LootCrateScreenProperties;
import lolens.cases.handlers.LootCrateScreenHandler;
import lolens.cases.item.ModItems;
import lolens.cases.network.NetworkHandlers;
import lolens.cases.network.packets.S2C_CaseSyncPacket;
import lolens.cases.screens.core.Drop;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static lolens.cases.item.CreativeInventoryGroup.CASES_ITEMS_GROUP;

public class CasesUtils {

    public static void openHandledScreen(PlayerEntity player, String crateId) {
        if (!player.getWorld().isClient) {

            player.openHandledScreen(new ExtendedScreenHandlerFactory() {
                @Override
                public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
                    buf.writeString(crateId);
                }

                @Override
                public Text getDisplayName() {
                    return Text.empty();
                }

                @Override
                public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
                    return new LootCrateScreenHandler(syncId);
                }
            });
        }
    }

    public static byte claimAllRewards(PlayerEntity player) {
        if (!RewardHelper.hasRewards((IEntityDataSaver) player)) {
            return 1;
        }
        if (!player.isAlive()) {
            return 2;
        }

        do {
            Optional<ItemStack> reward = RewardHelper.popReward((IEntityDataSaver) player);
            reward.ifPresent(itemStack -> player.getInventory().offerOrDrop(itemStack));
        } while (RewardHelper.hasRewards((IEntityDataSaver) player));

        return 0;
    }

    public static byte claimReward(PlayerEntity player) {
        if (!RewardHelper.hasRewards((IEntityDataSaver) player)) return 1; // do not have reward to claim
        if (!player.isAlive()) return 2; // ded..

        Optional<ItemStack> reward = RewardHelper.popReward((IEntityDataSaver) player);

        reward.ifPresent(itemStack -> player.getInventory().offerOrDrop(reward.get()));
        return 0;
    }

    public static boolean isHoldingRequestedLootCrateInHand(PlayerEntity player, String id, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!stack.isOf(ModItems.CASE)) return false;
        return stack.hasNbt() && stack.getNbt().getString("caseId").equals(id);
    }

    @Nullable
    public static Hand getHandWithRequestedLootCrateId(PlayerEntity player, String id) {
        if (isHoldingRequestedLootCrateInHand(player, id, Hand.MAIN_HAND)) {
            return Hand.MAIN_HAND;
        }
        if (isHoldingRequestedLootCrateInHand(player, id, Hand.OFF_HAND)) {
            return Hand.OFF_HAND;
        }
        return null;
    }


    public static Drop getRandomLootCrateDrop(@NotNull List<Drop> drops) {
        if (drops.isEmpty()) {
            throw new IllegalArgumentException("Tried getting random loot crate drop, but drops were empty");
        }

        int totalWeight = 0;
        for (Drop drop : drops) {
            totalWeight += drop.getWeight();

            if (drop.getWeight() <= 0) {
                throw new IllegalArgumentException("drop weight must be positive, but \'" + drop.getStack().getItem().getName() + "\' had weight 0 or less");            }
        }

        int randomValue = new Random().nextInt(totalWeight); // new random ?
        int currentWeight = 0;

        for (Drop drop : drops) {
            currentWeight += drop.getWeight();
            if (randomValue < currentWeight) {
                return drop;
            }
        }

        throw new IllegalStateException("Failed to select a random drop (totalWeight=" + totalWeight + ", randomValue=" + randomValue + ")");
    }

    public static void refreshCreativeTabLootCrates() {
        CASES_ITEMS_GROUP.getDisplayStacks().removeIf(itemStack -> itemStack.isOf(ModItems.CASE));

        List<String> ids = LootCrateManager.getInstance().getLootCrateIds();

        for (String id : ids) {
            NbtCompound tag = new NbtCompound();
            tag.putString("caseId", id);
            ItemStack lootCrateStack = new ItemStack(ModItems.CASE);
            lootCrateStack.setNbt(tag);
            CASES_ITEMS_GROUP.getDisplayStacks().add(lootCrateStack);
        }
    }

    // updates known loot crates and syncs with all players
    public static void refreshAll(MinecraftServer server) {
        LootCrateManager.getInstance().refresh();
        syncAll(server);
    }

    public static float getDropChance(List<Drop> drops, int targetId) { // id in json element order
        int totalWeight = 0;
        for (Drop drop : drops) {
            totalWeight += drop.getWeight();
        }
        return (float) drops.get(targetId).getWeight() / totalWeight;
    }

    public static float[] getDropChances(List<Drop> drops) {
        int totalWeight = 0;
        for (Drop drop : drops) {
            totalWeight += drop.getWeight();
        }

        float[] dropChances = new float[drops.size()];

        for (int i = 0; i < drops.size(); i++) {
            Drop drop = drops.get(i);
            dropChances[i] = (float) drop.getWeight() / totalWeight;
        }

        return dropChances;
    }

    // sync every player on the server
    public static void syncAll(MinecraftServer server) {
        List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
        for (ServerPlayerEntity player : players) {
            syncPlayer(player);
        }
    }


    // send all crate manager data to the client to sync it
    public static void syncPlayer(ServerPlayerEntity player) {
        List<String> ids = LootCrateManager.getInstance().getLootCrateIds();

        for (int i = 0; i < ids.size(); i++) {
            LootCrate crate = LootCrateManager.getInstance().getLootCrate(ids.get(i));

            LootCrateItemProperties ip = crate.getItemProperties();
            LootCrateScreenProperties sp = crate.getScreenProperties();
            NetworkHandlers.CHANNEL.serverHandle(player).send(new S2C_CaseSyncPacket(

                    // general item data
                    ip.getId(),
                    ip.isGlowing(),
                    ip.getColor(),
                    ip.getName(),
                    ip.getRarity(),

                    // general gui
                    sp.getHorizontalGUISize(),
                    sp.getSpinContainerVerticalSize(),
                    sp.getItemsContainerVerticalSize(),
                    sp.getButtonContainerVerticalSize(),

                    // items grid
                    sp.getItemGridRows(),
                    sp.getItemGridColumns(),
                    sp.getItemsGridContainerMargin(),
                    sp.getItemsGridElementSize(),

                    sp.isItemsGridDisplayStackCount(),
                    sp.isItemsGridDisplayTooltip(),
                    sp.isItemsGridStacksOnHoverScale(),
                    sp.getItemsGridStacksOnHoverScaleMultiplier(),

                    sp.getItemsGridOutlineColor(),

                    sp.getSortType(),

                    // spin grid
                    sp.getSpinGridColumns(),
                    sp.getSpinGridElementSize(),
                    sp.getSpinGridAnimationDuration(),

                    sp.isSpinGridDisplayTooltip(),
                    sp.isSpinGridDisplayStackCount(),
                    sp.isSpinGridStacksOnHoverScale(),
                    sp.getSpinGridStacksOnHoverScaleMultiplier(),

                    sp.isSpinGridStacksScaleWhileRolling(),


                    sp.getSpinGridOutlineColor(),

                    sp.getSlotChangeSound().getId().toString(),

                    sp.getExcludedEasings(),

                    crate.getDrops(),

                    (i == 0), // first packet refreshes existing cases
                    (i == ids.size() - 1) // last packet updates creative tab using new data

            ));
        }

    }


}

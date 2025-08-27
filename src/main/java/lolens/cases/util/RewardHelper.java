package lolens.cases.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RewardHelper {
    private static final String REWARDS_TAG = "Rewards";

    /*
    basically everything is stored as stack in NBT and player gets reward upon closing screen with loot crate
    or by using /cases claim command
     */

    public static void pushReward(IEntityDataSaver player, ItemStack reward) {
        if (reward.isEmpty()) return;

        NbtCompound data = player.cases$getPersistentData();
        NbtList rewards = getRewardsList(data);

        NbtCompound rewardTag = reward.writeNbt(new NbtCompound());
        rewards.add(rewardTag);

        data.put(REWARDS_TAG, rewards);
    }

    public static Optional<ItemStack> popReward(IEntityDataSaver player) {
        NbtCompound data = player.cases$getPersistentData();
        if (!data.contains(REWARDS_TAG)) {
            return Optional.empty();
        }

        NbtList rewards = data.getList(REWARDS_TAG, NbtElement.COMPOUND_TYPE);
        if (rewards.isEmpty()) {
            return Optional.empty();
        }

        NbtCompound rewardTag = rewards.getCompound(rewards.size() - 1);
        rewards.remove(rewards.size() - 1);

        if (rewards.isEmpty()) {
            data.remove(REWARDS_TAG);
        } else {
            data.put(REWARDS_TAG, rewards);
        }

        return Optional.of(ItemStack.fromNbt(rewardTag));
    }

    public static boolean hasRewards(IEntityDataSaver player) {
        NbtCompound data = player.cases$getPersistentData();
        return data.contains(REWARDS_TAG) && !data.getList(REWARDS_TAG, NbtElement.COMPOUND_TYPE).isEmpty();
    }

    public static int getRemainingRewardCount(IEntityDataSaver player) {
        NbtCompound data = player.cases$getPersistentData();
        NbtList list = getRewardsList(data);
        return list.size();
    }

    // returns list of uncollected ItemStack rewards player has
    public static List<ItemStack> peekAllRewards(IEntityDataSaver player) {
        List<ItemStack> result = new ArrayList<>();
        NbtCompound data = player.cases$getPersistentData();

        if (data.contains(REWARDS_TAG)) {
            NbtList rewards = data.getList(REWARDS_TAG, NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < rewards.size(); i++) {
                ItemStack stack = ItemStack.fromNbt(rewards.getCompound(i));
                if (stack != null) {
                    result.add(stack);
                }
            }
        }

        return result;
    }

    public static void clearRewards(IEntityDataSaver player) {
        player.cases$getPersistentData().remove(REWARDS_TAG);
    }

    private static NbtList getRewardsList(NbtCompound data) {
        return data.contains(REWARDS_TAG) ? data.getList(REWARDS_TAG, NbtElement.COMPOUND_TYPE) : new NbtList();
    }

}

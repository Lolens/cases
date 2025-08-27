package lolens.cases.handlers;

import io.wispforest.owo.client.screens.OwoScreenHandler;
import lolens.cases.Cases;
import lolens.cases.core.LootCrateManager;
import lolens.cases.core.LootCrateScreenProperties;
import lolens.cases.network.packets.C2S_RequestItemPacket;
import lolens.cases.network.packets.S2C_RequestItemPacket;
import lolens.cases.screens.core.Drop;
import lolens.cases.screens.core.DropRarity;
import lolens.cases.util.IEntityDataSaver;
import lolens.cases.util.CasesUtils;
import lolens.cases.util.RewardHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

import java.util.Optional;

public class LootCrateScreenHandler extends ScreenHandler implements OwoScreenHandler {

    private Optional<Drop> winningDrop = Optional.empty(); // using optional to not cause NullPointer while filling spin grid while client still haven't got answer

    private String caseId;

    private LootCrateScreenProperties lootCrateScreenProperties;

    public LootCrateScreenProperties getCaseProperties() {
        return lootCrateScreenProperties;
    }

    public Optional<Drop> getWinningDrop() {
        return this.winningDrop;
    }

    public String getCaseId() {
        return caseId;
    }

    public LootCrateScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId);
        String caseId = buf.readString();
        this.caseId = caseId;
        this.lootCrateScreenProperties = LootCrateManager.getInstance().getLootCrateScreenProperties(this.caseId);
    }

    public LootCrateScreenHandler(int syncId) {
        super(Cases.CASE_SCREEN_HANDLER, syncId);
        this.addClientboundMessage(S2C_RequestItemPacket.class, this::handleS2C); // S2C
        this.addServerboundMessage(C2S_RequestItemPacket.class, this::handleC2S); // C2S
    }

    private void handleC2S(C2S_RequestItemPacket c2SRequestItemPacket) {
        var hand = CasesUtils.getHandWithRequestedLootCrateId(player(), c2SRequestItemPacket.caseId());

        if (hand == null) {
            Cases.LOGGER.warn("player {} tried opening loot crate with caseId={} but wasn't holding it", player().getName().getString(), c2SRequestItemPacket.caseId());
            return;
        }

        Drop drop = CasesUtils.getRandomLootCrateDrop(LootCrateManager.getInstance().getLootCrate(c2SRequestItemPacket.caseId()).getDrops());

        if (!player().isCreative()) {
            ItemStack stack = player().getStackInHand(hand);
            stack.decrement(1);
            player().setStackInHand(hand, stack);
            player().getInventory().markDirty();
        }

        RewardHelper.pushReward((IEntityDataSaver) player(), drop.getStack());
        S2C_RequestItemPacket packet = new S2C_RequestItemPacket(drop.getStack(), drop.getRarityValue());

        Cases.LOGGER.info("C2S_RequestItemPacket: { caseId = {} }", c2SRequestItemPacket.caseId());

        sendMessage(packet);
    }

    private void handleS2C(S2C_RequestItemPacket s2CRequestItemPacket) {
        ItemStack stack = s2CRequestItemPacket.stack();
        DropRarity rarity = DropRarity.getById(s2CRequestItemPacket.ColorId());
        Cases.LOGGER.info("S2C_RequestItemPacket: { Rarity= {} | Stack = {} }", DropRarity.getById(s2CRequestItemPacket.ColorId()).name(), s2CRequestItemPacket.stack());
        this.winningDrop = Optional.of(new Drop(stack, rarity, 0));
    }

    public void playSoundToPlayer(SoundEvent soundEvent, float volume, float pitch) {
        player().playSound(soundEvent, SoundCategory.MASTER, volume, pitch);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        CasesUtils.claimReward(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}

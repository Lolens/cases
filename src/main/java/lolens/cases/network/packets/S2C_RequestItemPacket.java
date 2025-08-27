package lolens.cases.network.packets;

import net.minecraft.item.ItemStack;

public record S2C_RequestItemPacket(ItemStack stack, int ColorId) {
}

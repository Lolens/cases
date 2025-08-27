package lolens.cases.network;

import io.wispforest.owo.network.serialization.PacketBufSerializer;
import lolens.cases.screens.core.Drop;
import lolens.cases.screens.core.DropRarity;
import net.minecraft.network.PacketByteBuf;

public class Serializers {
    public static void register() {
        PacketBufSerializer.register(
                Drop.class,
                Serializers::writeLootCrateDrop,
                Serializers::readLootCrateDrop
        );
    }

    private static Drop readLootCrateDrop(PacketByteBuf buf) {
        return new Drop(buf.readItemStack(), buf.readEnumConstant(DropRarity.class), buf.readInt());
    }


    private static void writeLootCrateDrop(PacketByteBuf buf, Drop lcd) {
        buf.writeItemStack(lcd.getStack());
        buf.writeEnumConstant(lcd.getRarity());
        buf.writeInt(lcd.getWeight());
    }
}

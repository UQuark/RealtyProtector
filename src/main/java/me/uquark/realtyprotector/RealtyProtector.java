package me.uquark.realtyprotector;

import me.uquark.realtyprotector.block.Blocks;
import me.uquark.realtyprotector.item.Items;
import net.fabricmc.api.ModInitializer;

public class RealtyProtector implements ModInitializer {
    public static final String modid = "realtyprotector";

    @Override
    public void onInitialize() {
        Blocks.PROTECTOR_BLOCK.register();
        Items.PROTECTION_CURSOR.register();
    }
}

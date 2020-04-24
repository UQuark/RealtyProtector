package me.uquark.realtyprotector.block;

import me.uquark.quarkcore.block.AbstractBlock;
import me.uquark.realtyprotector.RealtyProtector;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class ProtectorBlock extends AbstractBlock {
    protected ProtectorBlock() {
        super(
            RealtyProtector.modid,
            "protector_block",
            FabricBlockSettings.copy(Blocks.OBSIDIAN).lightLevel(15),
            new Item.Settings().group(ItemGroup.DECORATIONS).maxCount(64)
        );
    }
}

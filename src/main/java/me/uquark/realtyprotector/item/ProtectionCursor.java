package me.uquark.realtyprotector.item;

import me.uquark.quarkcore.item.AbstractItem;
import me.uquark.realtyprotector.RealtyProtector;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class ProtectionCursor extends AbstractItem {
    public ProtectionCursor() {
        super(RealtyProtector.modid, "protection_cursor", new Item.Settings().maxCount(1).group(ItemGroup.TOOLS));
    }
}

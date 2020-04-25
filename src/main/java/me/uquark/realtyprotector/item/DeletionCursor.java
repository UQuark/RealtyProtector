package me.uquark.realtyprotector.item;

import me.uquark.quarkcore.item.AbstractItem;
import me.uquark.realtyprotector.RealtyProtector;
import me.uquark.realtyprotector.RealtyProtectorServer;
import me.uquark.realtyprotector.data.RegionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

import java.sql.SQLException;

public class DeletionCursor extends AbstractItem {
    public DeletionCursor() {
        super(RealtyProtector.modid, "deletion_cursor", new Item.Settings().maxCount(1).group(ItemGroup.TOOLS).maxDamage(0));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (RealtyProtectorServer.INSTANCE == null)
            return ActionResult.FAIL;
        if (context.getWorld().isClient || context.getPlayer() == null)
            return ActionResult.PASS;
        PlayerEntity player = context.getPlayer();
        RegionManager.RegionInfo regionInfo = null;
        try {
            regionInfo = RealtyProtectorServer.INSTANCE.regionManager.getRegionInfo(context.getBlockPos());
        } catch (SQLException e) {
            // do nothing
        }
        if (regionInfo == null)
            regionInfo = new RegionManager.RegionInfo(-1, null, "Unnamed", null, null);
        switch (deleteRegion(context.getBlockPos(), player)) {
            case OK:
                player.addChatMessage(new TranslatableText("message.realtyprotector.region_removed", regionInfo.name), false);
                if (!player.isCreative()) {
                    ItemStack stack = new ItemStack(Items.PROTECTION_CURSOR, 1);
                    stack.setCustomName(new LiteralText(regionInfo.name));
                    player.giveItemStack(stack);
                }
                return ActionResult.SUCCESS;
            case NoRegion:
                player.addChatMessage(new TranslatableText("message.realtyprotector.no_region"), false);
                return ActionResult.FAIL;
            case NotOwner:
                player.addChatMessage(new TranslatableText("message.realtyprotector.not_owner"), false);
                return ActionResult.FAIL;
            case ClientIsNotEnabled:
                player.addChatMessage(new TranslatableText("message.realtyprotector.client_is_not_enabled"), false);
                return ActionResult.FAIL;
            case Fail:
                player.addChatMessage(new TranslatableText("message.realtyprotector.unknown_error"), false);
                return ActionResult.FAIL;
        }
        return ActionResult.FAIL;
    }

    private RegionManager.RegionDeletionResult deleteRegion(BlockPos pos, PlayerEntity player) {
        if (RealtyProtectorServer.INSTANCE == null)
            return RegionManager.RegionDeletionResult.ClientIsNotEnabled;
        try {
            return RealtyProtectorServer.INSTANCE.regionManager.deleteRegion(pos, player);
        } catch (SQLException e) {
            e.printStackTrace();
            return RegionManager.RegionDeletionResult.Fail;
        }
    }
}

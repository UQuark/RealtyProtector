package me.uquark.realtyprotector.item;

import me.uquark.quarkcore.item.AbstractItem;
import me.uquark.realtyprotector.RealtyProtector;
import me.uquark.realtyprotector.RealtyProtectorServer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class ProtectionCursor extends AbstractItem {
    public HashMap<PlayerEntity, ArrayList<BlockPos>> playerTable = new HashMap<>();

    public ProtectionCursor() {
        super(RealtyProtector.modid, "protection_cursor", new Item.Settings().maxCount(1).group(ItemGroup.TOOLS));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().isClient || context.getPlayer() == null)
            return ActionResult.PASS;
        if (!playerTable.containsKey(context.getPlayer()))
            playerTable.put(context.getPlayer(), new ArrayList<>());
        ArrayList<BlockPos> positions = playerTable.get(context.getPlayer());
        if (positions.size() == 2)
            positions.clear();
        switch (positions.size()) {
            case 0:
                context.getPlayer().addChatMessage(new LiteralText("First position set"), false);
                break;
            case 1:
                context.getPlayer().addChatMessage(new LiteralText("Second position set"), false);
                break;
        }
        positions.add(context.getBlockPos());
        return ActionResult.SUCCESS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        String regionName = "Region";
        if (user.isSneaking()) {
            if (user.getMainHandStack().hasCustomName())
                regionName = user.getMainHandStack().getName().asString();
            if (registerRegion(user, regionName)) {
                user.addChatMessage(new LiteralText("New region registered"), false);
                user.getMainHandStack().decrement(1);
                return TypedActionResult.success(user.getMainHandStack());
            }
        }
        return TypedActionResult.fail(user.getMainHandStack());
    }

    private boolean registerRegion(PlayerEntity player, String name) {
        if (playerTable.get(player) == null)
            return false;
        if (playerTable.get(player).size() < 2)
            return false;
        BlockPos pos1 = playerTable.get(player).get(0);
        BlockPos pos2 = playerTable.get(player).get(1);
        try {
            if (RealtyProtectorServer.INSTANCE == null)
                return false;
            return (RealtyProtectorServer.INSTANCE.regionManager.addRegion(name, pos1, pos2, player, null) > 0);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

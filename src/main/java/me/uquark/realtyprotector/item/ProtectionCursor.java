package me.uquark.realtyprotector.item;

import me.uquark.quarkcore.item.AbstractItem;
import me.uquark.realtyprotector.RealtyProtector;
import me.uquark.realtyprotector.RealtyProtectorServer;
import me.uquark.realtyprotector.data.RegionManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.sql.SQLException;
import java.util.*;

public class ProtectionCursor extends AbstractItem {
    public HashMap<PlayerEntity, ProtectionCursorParameters> playerTable = new HashMap<>();

    private class ProtectionCursorParameters {
        public List<BlockPos> positions = new ArrayList<>();
        public Set<PlayerEntity> members = new HashSet<>();
    }

    public ProtectionCursor() {
        super(RealtyProtector.modid, "protection_cursor", new Item.Settings().maxCount(1).group(ItemGroup.TOOLS).maxDamage(0));
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (RealtyProtectorServer.INSTANCE == null)
            return ActionResult.FAIL;
        if (context.getWorld().isClient || context.getPlayer() == null)
            return ActionResult.PASS;
        if (!playerTable.containsKey(context.getPlayer()))
            playerTable.put(context.getPlayer(), new ProtectionCursorParameters());
        ProtectionCursorParameters parameters = playerTable.get(context.getPlayer());
        if (parameters.positions.size() == 2)
            parameters.positions.clear();
        switch (parameters.positions.size()) {
            case 0:
                context.getPlayer().sendMessage(new TranslatableText("message.realtyprotector.first_position_set"), false);
                break;
            case 1:
                context.getPlayer().sendMessage(new TranslatableText("message.realtyprotector.second_position_set"), false);
                break;
        }
        parameters.positions.add(context.getBlockPos());
        return ActionResult.SUCCESS;
    }

    public boolean hitPlayer(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (RealtyProtectorServer.INSTANCE == null)
            return super.postHit(stack, target, attacker);
        if (target == null || attacker == null)
            return super.postHit(stack, target, attacker);
        if (target.world.isClient || attacker.world.isClient)
            return super.postHit(stack, target, attacker);
        if (!(target instanceof PlayerEntity) || !(attacker instanceof PlayerEntity))
            return super.postHit(stack, target, attacker);
        PlayerEntity playerAttacker = (PlayerEntity) attacker;
        PlayerEntity playerTarget = (PlayerEntity) target;
        if (!playerTable.containsKey(playerAttacker))
            playerTable.put(playerAttacker, new ProtectionCursorParameters());
        ProtectionCursorParameters parameters = playerTable.get(playerAttacker);
        if (parameters.members.add(playerTarget))
            playerAttacker.sendMessage(new TranslatableText("message.realtyprotector.player_added", target.getName().asString()), false);
        return super.postHit(stack, target, attacker);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        String regionName = "Unnamed";
        if (user.isSneaking() && !world.isClient) {
            if (user.getMainHandStack().hasCustomName())
                regionName = user.getMainHandStack().getName().asString();
            switch (registerRegion(user, regionName)) {
                case OK:
                    user.sendMessage(new TranslatableText("message.realtyprotector.region_registered", regionName), false);
                    user.getMainHandStack().decrement(1);
                    playerTable.remove(user);
                    return TypedActionResult.success(user.getMainHandStack());
                case TooBig:
                    user.sendMessage(new TranslatableText("message.realtyprotector.too_big_region", RegionManager.MAX_VOLUME), false);
                    return TypedActionResult.fail(user.getMainHandStack());
                case Overlap:
                    user.sendMessage(new TranslatableText("message.realtyprotector.regions_overlap"), false);
                    return TypedActionResult.fail(user.getMainHandStack());
                case NotEnoughPoints:
                    user.sendMessage(new TranslatableText("message.realtyprotector.not_enough_points"), false);
                    return TypedActionResult.fail(user.getMainHandStack());
                case ClientIsNotEnabled:
                    user.sendMessage(new TranslatableText("message.realtyprotector.client_is_not_enabled"), false);
                    return TypedActionResult.fail(user.getMainHandStack());
                case Fail:
                    user.sendMessage(new TranslatableText("message.realtyprotector.unknown_error"), false);
                    return TypedActionResult.fail(user.getMainHandStack());
            }
        }
        return TypedActionResult.fail(user.getMainHandStack());
    }

    private RegionManager.RegionRegistrationResult registerRegion(PlayerEntity player, String name) {
        if (RealtyProtectorServer.INSTANCE == null)
            return RegionManager.RegionRegistrationResult.ClientIsNotEnabled;
        ProtectionCursorParameters parameters = playerTable.get(player);
        if (parameters == null)
            parameters = new ProtectionCursorParameters();
        if (parameters.positions.size() < 2)
            return RegionManager.RegionRegistrationResult.NotEnoughPoints;
        BlockPos pos1 = playerTable.get(player).positions.get(0);
        BlockPos pos2 = playerTable.get(player).positions.get(1);
        try {
            return RealtyProtectorServer.INSTANCE.regionManager.registerRegion(name, pos1, pos2, player, parameters.members);
        } catch (SQLException e) {
            e.printStackTrace();
            return RegionManager.RegionRegistrationResult.Fail;
        }
    }
}

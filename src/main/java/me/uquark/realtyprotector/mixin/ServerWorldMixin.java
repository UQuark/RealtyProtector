package me.uquark.realtyprotector.mixin;

import me.uquark.realtyprotector.RealtyProtectorServer;
import me.uquark.realtyprotector.data.RegionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.sql.SQLException;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Inject(method = "canPlayerModifyAt", at = @At("HEAD"), cancellable = true)
    public void canPlayerModifyAt(PlayerEntity player, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        if (RealtyProtectorServer.INSTANCE == null)
            return;
        RegionManager regionManager = RealtyProtectorServer.INSTANCE.regionManager;
        if (regionManager == null)
            return;

        boolean canModify = false;
        try {
            canModify = regionManager.canPlayerModifyAt(player, pos);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (canModify)
            info.setReturnValue(true);
        else {
            player.addChatMessage(new TranslatableText("message.realtyprotector.region_protected"), false);
            info.setReturnValue(false);
        }
    }
}

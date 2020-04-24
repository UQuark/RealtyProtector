package me.uquark.realtyprotector.mixin;

import me.uquark.realtyprotector.RealtyProtectorServer;
import me.uquark.realtyprotector.data.RegionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Inject(method = "canPlayerModifyAt", at = @At("HEAD"), cancellable = true)
    public void canPlayerModifyAt(PlayerEntity player, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        if (RealtyProtectorServer.INSTANCE == null)
            return;
        RegionManager regionManager = RealtyProtectorServer.INSTANCE.regionManager;
        if (regionManager == null)
            return;

        if (regionManager.canPlayerModifyAt(player, pos))
            info.setReturnValue(true);
        else {
            player.addChatMessage(new LiteralText("This region is protected"), false);
            info.setReturnValue(false);
        }
    }
}

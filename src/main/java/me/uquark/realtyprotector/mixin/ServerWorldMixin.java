package me.uquark.realtyprotector.mixin;

import me.uquark.quarkcore.base.AbstractMod;
import me.uquark.realtyprotector.RealtyProtector;
import me.uquark.realtyprotector.data.RegionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin {
    @Inject(method = "canPlayerModifyAt", at = @At("HEAD"), cancellable = true)
    public void canPlayerModifyAt(PlayerEntity player, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(((RealtyProtector) RealtyProtector.INSTANCE).regionManager.canPlayerModifyAt(player, pos));
    }
}

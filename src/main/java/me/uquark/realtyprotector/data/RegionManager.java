package me.uquark.realtyprotector.data;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class RegionManager {
    public boolean canPlayerModifyAt(PlayerEntity player, BlockPos pos) {
        return pos.getX() % 2 != 0;
    }
}

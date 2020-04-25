package me.uquark.realtyprotector.mixin;

import me.uquark.realtyprotector.item.ProtectionCursor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
    }

    @Inject(method = "attack", at = @At("HEAD"))
    public void attack(Entity target, CallbackInfo info) {
        PlayerEntity self = this.world.getPlayerByUuid(getUuid());
        if (self == null)
            return;
        ItemStack stack = self.getMainHandStack();
        if (stack.getItem() instanceof ProtectionCursor && target instanceof LivingEntity)
            ((ProtectionCursor)stack.getItem()).hitPlayer(stack, (LivingEntity) target, self);
    }
}

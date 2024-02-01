package de.cheaterpaul.simpleshopsfix.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wolforce.simpleshops.util.UtilItemStack;

@Pseudo
@Mixin(targets = "wolforce.simpleshops.SimpleShopTileEntity")
public abstract class SimpleShopTileEntityMixin {


    @Shadow public abstract void spawn(Level world, Vec3 pos, ItemStack stack);

    @Inject(method = "spawn(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/item/ItemStack;I)V", at = @At("HEAD"), cancellable = true)
    private void fixSpawn(Level world, Vec3 pos, ItemStack stack, int amount, CallbackInfo ci) {
        int maxStackSize = stack.getMaxStackSize();
        int fullStacks = amount / maxStackSize;
        for (int i = 0; i < fullStacks; i++){
            spawn(world, pos, UtilItemStack.setCount(stack, maxStackSize));
        }
        int remainder = amount % maxStackSize;
        if(remainder > 0){
            spawn(world, pos, UtilItemStack.setCount(stack, remainder));
        }
        ci.cancel();
    }
}

package de.cheaterpaul.simpleshopsfix.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "wolforce.simpleshops.SimpleShopTileEntity")
public abstract class SimpleShopTileEntityMixin {


    @Shadow public abstract void spawn(Level world, Vec3 pos, ItemStack stack);

    /**
     * Fix stack size
     */
    @Inject(method = "spawn(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/item/ItemStack;I)V", at = @At("HEAD"), cancellable = true)
    private void fixSpawn(Level world, Vec3 pos, ItemStack stack, int amount, CallbackInfo ci) {
        int maxStackSize = stack.getMaxStackSize();
        int fullStacks = amount / maxStackSize;
        for (int i = 0; i < fullStacks; i++){
            spawn(world, pos, setCount(stack, maxStackSize));
        }
        int remainder = amount % maxStackSize;
        if(remainder > 0){
            spawn(world, pos, setCount(stack, remainder));
        }
        ci.cancel();
    }

    @Unique
    private static ItemStack setCount(ItemStack stack, int newCount) {
        ItemStack newStack = stack.copy();
        newStack.setCount(newCount);
        return newStack;
    }
}

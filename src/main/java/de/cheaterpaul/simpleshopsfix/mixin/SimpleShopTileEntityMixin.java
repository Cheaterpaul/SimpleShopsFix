package de.cheaterpaul.simpleshopsfix.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Debug
@Mixin(targets = "wolforce.simpleshops.SimpleShopTileEntity")
public abstract class SimpleShopTileEntityMixin {


    @Shadow public abstract void spawn(Level world, Vec3 pos, ItemStack stack);

    @Shadow public abstract ItemStack getCost();

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

    @Inject(method = "tryBuy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z", shift = At.Shift.BEFORE), cancellable = true)
    private void checkNBT(Player player, ItemStack input, boolean isCreative, CallbackInfo ci) {
        if (!ItemStack.isSameItemSameTags(input, getCost())) {
            ci.cancel();
        }
    }

    @Unique
    private static ItemStack setCount(ItemStack stack, int newCount) {
        ItemStack newStack = stack.copy();
        newStack.setCount(newCount);
        return newStack;
    }
}

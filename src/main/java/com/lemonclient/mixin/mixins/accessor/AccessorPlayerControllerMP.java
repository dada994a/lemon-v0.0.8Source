package com.lemonclient.mixin.mixins.accessor;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({PlayerControllerMP.class})
public interface AccessorPlayerControllerMP {
   @Accessor("blockHitDelay")
   int getBlockHitDelay();

   @Accessor("blockHitDelay")
   void setBlockHitDelay(int var1);

   @Accessor("isHittingBlock")
   void setIsHittingBlock(boolean var1);

   @Accessor("currentPlayerItem")
   int getCurrentPlayerItem();

   @Invoker("syncCurrentPlayItem")
   void invokeSyncCurrentPlayItem();
}

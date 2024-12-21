package com.lemonclient.mixin.mixins.accessor;

import net.minecraft.network.play.client.CPacketPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({CPacketPlayer.class})
public interface AccessorCPacketPlayer {
   @Accessor("x")
   void setX(double var1);

   @Accessor("y")
   void setY(double var1);

   @Accessor("z")
   void setZ(double var1);

   @Accessor("yaw")
   void setYaw(float var1);

   @Accessor("pitch")
   void setPitch(float var1);

   @Accessor("onGround")
   void setOnGround(boolean var1);

   @Accessor("moving")
   boolean getMoving();

   @Accessor("rotating")
   boolean getRotating();
}

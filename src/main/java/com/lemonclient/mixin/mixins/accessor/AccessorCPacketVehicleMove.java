package com.lemonclient.mixin.mixins.accessor;

import net.minecraft.network.play.client.CPacketVehicleMove;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({CPacketVehicleMove.class})
public interface AccessorCPacketVehicleMove {
   @Accessor("y")
   void setY(double var1);

   @Accessor("x")
   void setX(double var1);

   @Accessor("z")
   void setZ(double var1);

   @Accessor("yaw")
   void setYaw(float var1);

   @Accessor("pitch")
   void setPitch(float var1);
}

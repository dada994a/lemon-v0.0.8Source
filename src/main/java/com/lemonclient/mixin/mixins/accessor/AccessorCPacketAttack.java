package com.lemonclient.mixin.mixins.accessor;

import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({CPacketUseEntity.class})
public interface AccessorCPacketAttack {
   @Accessor("entityId")
   int getId();

   @Accessor("entityId")
   void setId(int var1);

   @Accessor("action")
   void setAction(Action var1);
}

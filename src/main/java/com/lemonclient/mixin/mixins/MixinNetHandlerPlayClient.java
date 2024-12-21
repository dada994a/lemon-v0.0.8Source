package com.lemonclient.mixin.mixins;

import com.lemonclient.api.event.events.DeathEvent;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.manager.managers.TotemPopManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({NetHandlerPlayClient.class})
public class MixinNetHandlerPlayClient {
   @Inject(
      method = {"handleEntityMetadata"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void handleEntityMetadataHook(SPacketEntityMetadata sPacketEntityMetadata, CallbackInfo callbackInfo) {
      Entity getEntityByID;
      EntityPlayer entityPlayer;
      if (Minecraft.func_71410_x().field_71441_e != null && (getEntityByID = Minecraft.func_71410_x().field_71441_e.func_73045_a(sPacketEntityMetadata.func_149375_d())) instanceof EntityPlayer && (entityPlayer = (EntityPlayer)getEntityByID).func_110143_aJ() <= 0.0F) {
         LemonClient.EVENT_BUS.post(new DeathEvent(entityPlayer));
         if (TotemPopManager.INSTANCE.sendMsgs) {
            TotemPopManager.INSTANCE.death(entityPlayer);
         }
      }

   }
}

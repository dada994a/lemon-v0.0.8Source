package com.lemonclient.mixin.mixins;

import com.lemonclient.api.event.events.EntityRemovedEvent;
import com.lemonclient.client.LemonClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WorldClient.class})
public abstract class MixinWorldClient {
   @Inject(
      method = {"onEntityRemoved"},
      at = {@At("HEAD")}
   )
   public void onEntityRemoved(Entity entity, CallbackInfo info) {
      LemonClient.EVENT_BUS.post(new EntityRemovedEvent(entity));
   }
}

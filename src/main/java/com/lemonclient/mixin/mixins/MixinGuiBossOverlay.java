package com.lemonclient.mixin.mixins;

import com.lemonclient.api.event.events.BossbarEvent;
import com.lemonclient.client.LemonClient;
import net.minecraft.client.gui.GuiBossOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiBossOverlay.class})
public class MixinGuiBossOverlay {
   @Inject(
      method = {"renderBossHealth"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderBossHealth(CallbackInfo callbackInfo) {
      BossbarEvent event = new BossbarEvent();
      LemonClient.EVENT_BUS.post(event);
      if (event.isCancelled()) {
         callbackInfo.cancel();
      }

   }
}

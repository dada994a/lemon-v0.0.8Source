package com.lemonclient.mixin.mixins;

import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.NoRender;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiIngame.class})
public class MixinPlayerOverlay {
   @Inject(
      method = {"renderPumpkinOverlay"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void renderPumpkinOverlayHook(ScaledResolution scaledRes, CallbackInfo callbackInfo) {
      NoRender noRender = (NoRender)ModuleManager.getModule(NoRender.class);
      if (noRender.isEnabled() && (Boolean)noRender.noOverlay.getValue()) {
         callbackInfo.cancel();
      }

   }

   @Inject(
      method = {"renderPotionEffects"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void renderPotionEffectsHook(ScaledResolution scaledRes, CallbackInfo callbackInfo) {
      NoRender noRender = (NoRender)ModuleManager.getModule(NoRender.class);
      if (noRender.isEnabled() && (Boolean)noRender.noOverlay.getValue()) {
         callbackInfo.cancel();
      }

   }
}

package com.lemonclient.mixin.mixins;

import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.NoRender;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Render.class})
public class MixinRender {
   @Inject(
      method = {"renderLivingLabel"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderLivingLabel(Entity entityIn, String str, double x, double y, double z, int maxDistance, CallbackInfo ci) {
      NoRender noRender = (NoRender)ModuleManager.getModule(NoRender.class);
      if ((Boolean)noRender.nameTag.getValue()) {
         ci.cancel();
      }

   }
}

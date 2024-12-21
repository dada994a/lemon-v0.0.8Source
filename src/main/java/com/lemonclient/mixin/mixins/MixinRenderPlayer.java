package com.lemonclient.mixin.mixins;

import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.Nametags;
import com.lemonclient.client.module.modules.render.NoRender;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderPlayer.class})
public abstract class MixinRenderPlayer {
   @Inject(
      method = {"renderEntityName*"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void renderLivingLabel(AbstractClientPlayer entity, double x, double y, double z, String name, double distanceSq, CallbackInfo callbackInfo) {
      if (entity.func_70005_c_().length() == 0) {
         callbackInfo.cancel();
      }

      if (ModuleManager.isModuleEnabled(Nametags.class)) {
         callbackInfo.cancel();
      }

      NoRender noRender = (NoRender)ModuleManager.getModule(NoRender.class);
      if ((Boolean)noRender.nameTag.getValue()) {
         callbackInfo.cancel();
      }

   }
}

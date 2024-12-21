package com.lemonclient.mixin.mixins;

import com.lemonclient.api.event.events.NewRenderEntityEvent;
import com.lemonclient.client.LemonClient;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({RenderEnderCrystal.class})
public class MixinRenderCrystal {
   @Redirect(
      method = {"doRender"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"
)
   )
   public void renderModelBaseHook(ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      NewRenderEntityEvent event = new NewRenderEntityEvent(modelBase, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      LemonClient.EVENT_BUS.post(event);
      if (!event.isCancelled()) {
         modelBase.func_78088_a(event.entityIn, 0.0F, event.limbSwingAmount, event.ageInTicks, 0.0F, 0.0F, event.scale);
      }

   }
}

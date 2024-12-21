package com.lemonclient.mixin.mixins;

import com.lemonclient.api.event.events.TransformSideFirstPersonEvent;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.NoRender;
import com.lemonclient.client.module.modules.render.ViewModel;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemRenderer.class})
public class MixinItemRenderer {
   @Inject(
      method = {"transformSideFirstPerson"},
      at = {@At("HEAD")}
   )
   public void transformSideFirstPerson(EnumHandSide hand, float p_187459_2_, CallbackInfo callbackInfo) {
      TransformSideFirstPersonEvent event = new TransformSideFirstPersonEvent(hand);
      LemonClient.EVENT_BUS.post(event);
   }

   @Inject(
      method = {"transformEatFirstPerson"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void transformEatFirstPerson(float p_187454_1_, EnumHandSide hand, ItemStack stack, CallbackInfo callbackInfo) {
      TransformSideFirstPersonEvent event = new TransformSideFirstPersonEvent(hand);
      LemonClient.EVENT_BUS.post(event);
      ViewModel viewModel = (ViewModel)ModuleManager.getModule(ViewModel.class);
      if (viewModel.isEnabled() && (Boolean)viewModel.cancelEating.getValue()) {
         callbackInfo.cancel();
      }

   }

   @Inject(
      method = {"transformFirstPerson"},
      at = {@At("HEAD")}
   )
   public void transformFirstPerson(EnumHandSide hand, float p_187453_2_, CallbackInfo callbackInfo) {
      TransformSideFirstPersonEvent event = new TransformSideFirstPersonEvent(hand);
      LemonClient.EVENT_BUS.post(event);
   }

   @Inject(
      method = {"renderOverlays"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void renderOverlays(float partialTicks, CallbackInfo callbackInfo) {
      NoRender noRender = (NoRender)ModuleManager.getModule(NoRender.class);
      if (noRender.isEnabled() && (Boolean)noRender.noOverlay.getValue()) {
         callbackInfo.cancel();
      }

   }
}

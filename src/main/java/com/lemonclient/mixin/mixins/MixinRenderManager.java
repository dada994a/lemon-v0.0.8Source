package com.lemonclient.mixin.mixins;

import com.lemonclient.api.event.events.RenderEntityEvent;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.NoFallingBlocks;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityXPOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RenderManager.class})
public class MixinRenderManager {
   @Inject(
      method = {"renderEntity"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void renderEntityHead(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean p_188391_10_, CallbackInfo callbackInfo) {
      RenderEntityEvent.Head renderEntityHeadEvent = new RenderEntityEvent.Head(entityIn, RenderEntityEvent.Type.TEXTURE);
      LemonClient.EVENT_BUS.post(renderEntityHeadEvent);
      if (entityIn instanceof EntityEnderPearl || entityIn instanceof EntityXPOrb || entityIn instanceof EntityExpBottle || entityIn instanceof EntityEnderCrystal) {
         RenderEntityEvent.Head renderEntityEvent = new RenderEntityEvent.Head(entityIn, RenderEntityEvent.Type.COLOR);
         LemonClient.EVENT_BUS.post(renderEntityEvent);
         if (renderEntityEvent.isCancelled()) {
            callbackInfo.cancel();
         }
      }

      if (renderEntityHeadEvent.isCancelled()) {
         callbackInfo.cancel();
      }

   }

   @Inject(
      method = {"shouldRender"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onShouldRender(Entity entity, ICamera camera, double camX, double camY, double camZ, CallbackInfoReturnable<Boolean> cir) {
      if (ModuleManager.isModuleEnabled(NoFallingBlocks.class) && entity instanceof EntityFallingBlock) {
         cir.setReturnValue(false);
      }

   }

   @Inject(
      method = {"renderEntity"},
      at = {@At("RETURN")},
      cancellable = true
   )
   public void renderEntityReturn(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean p_188391_10_, CallbackInfo callbackInfo) {
      RenderEntityEvent.Return renderEntityReturnEvent = new RenderEntityEvent.Return(entityIn, RenderEntityEvent.Type.TEXTURE);
      LemonClient.EVENT_BUS.post(renderEntityReturnEvent);
      if (entityIn instanceof EntityEnderPearl || entityIn instanceof EntityXPOrb || entityIn instanceof EntityExpBottle || entityIn instanceof EntityEnderCrystal) {
         RenderEntityEvent.Return renderEntityEvent = new RenderEntityEvent.Return(entityIn, RenderEntityEvent.Type.COLOR);
         LemonClient.EVENT_BUS.post(renderEntityEvent);
         if (renderEntityEvent.isCancelled()) {
            callbackInfo.cancel();
         }
      }

      if (renderEntityReturnEvent.isCancelled()) {
         callbackInfo.cancel();
      }

   }
}

package com.lemonclient.mixin.mixins;

import com.lemonclient.api.event.events.NewRenderEntityEvent;
import com.lemonclient.api.event.events.RenderEntityEvent;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.NoRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.Profile;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({RenderLivingBase.class})
public abstract class MixinRenderLivingBase<T extends EntityLivingBase> extends Render<T> {
   @Shadow
   protected ModelBase field_77045_g;
   protected final Minecraft mc = Minecraft.func_71410_x();
   private boolean isClustered;

   public MixinRenderLivingBase(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
      super(renderManagerIn);
      this.field_77045_g = modelBaseIn;
      this.field_76989_e = shadowSizeIn;
   }

   protected MixinRenderLivingBase() {
      super((RenderManager)null);
   }

   @Inject(
      method = {"renderModel"},
      at = {@At("HEAD")},
      cancellable = true
   )
   void doRender(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo ci) {
      NewRenderEntityEvent event = new NewRenderEntityEvent(this.field_77045_g, entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
      if (this.func_180548_c(entityIn)) {
         NoRender noRender = (NoRender)ModuleManager.getModule(NoRender.class);
         if (noRender.isEnabled() && (Boolean)noRender.noCluster.getValue() && this.mc.field_71439_g.func_70032_d(entityIn) < 1.0F && entityIn != this.mc.field_71439_g) {
            GlStateManager.func_187408_a(Profile.TRANSPARENT_MODEL);
            this.isClustered = true;
            if (noRender.incrementNoClusterRender()) {
               ci.cancel();
            }
         } else {
            this.isClustered = false;
         }

         RenderEntityEvent.Head renderEntityHeadEvent = new RenderEntityEvent.Head(entityIn, RenderEntityEvent.Type.COLOR);
         LemonClient.EVENT_BUS.post(renderEntityHeadEvent);
         GlStateManager.func_187408_a(Profile.TRANSPARENT_MODEL);
         LemonClient.EVENT_BUS.post(event);
         GlStateManager.func_187440_b(Profile.TRANSPARENT_MODEL);
         if (event.isCancelled()) {
            ci.cancel();
         }

      }
   }

   @Inject(
      method = {"renderModel"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void renderModel(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo callbackInfo) {
      if (this.func_180548_c(entitylivingbaseIn)) {
         NoRender noRender = (NoRender)ModuleManager.getModule(NoRender.class);
         if (noRender.isEnabled() && (Boolean)noRender.noCluster.getValue() && this.mc.field_71439_g.func_70032_d(entitylivingbaseIn) < 1.0F && entitylivingbaseIn != this.mc.field_71439_g) {
            GlStateManager.func_187408_a(Profile.TRANSPARENT_MODEL);
            this.isClustered = true;
            if (noRender.incrementNoClusterRender()) {
               callbackInfo.cancel();
            }
         } else {
            this.isClustered = false;
         }

         RenderEntityEvent.Head renderEntityHeadEvent = new RenderEntityEvent.Head(entitylivingbaseIn, RenderEntityEvent.Type.COLOR);
         LemonClient.EVENT_BUS.post(renderEntityHeadEvent);
         if (renderEntityHeadEvent.isCancelled()) {
            callbackInfo.cancel();
         }

      }
   }

   @Inject(
      method = {"renderModel"},
      at = {@At("RETURN")},
      cancellable = true
   )
   protected void renderModelReturn(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, CallbackInfo callbackInfo) {
      RenderEntityEvent.Return renderEntityReturnEvent = new RenderEntityEvent.Return(entitylivingbaseIn, RenderEntityEvent.Type.COLOR);
      LemonClient.EVENT_BUS.post(renderEntityReturnEvent);
      if (!renderEntityReturnEvent.isCancelled()) {
         callbackInfo.cancel();
      }

   }

   @Inject(
      method = {"renderLayers"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void renderLayers(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn, CallbackInfo callbackInfo) {
      if (this.isClustered && !((NoRender)ModuleManager.getModule(NoRender.class)).getNoClusterRender()) {
         callbackInfo.cancel();
      }

   }

   @Redirect(
      method = {"setBrightness"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/renderer/GlStateManager;glTexEnvi(III)V",
   ordinal = 6
)
   )
   protected void glTexEnvi0(int target, int parameterName, int parameter) {
      if (!this.isClustered) {
         GlStateManager.func_187399_a(target, parameterName, parameter);
      }

   }

   @Redirect(
      method = {"setBrightness"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/renderer/GlStateManager;glTexEnvi(III)V",
   ordinal = 7
)
   )
   protected void glTexEnvi1(int target, int parameterName, int parameter) {
      if (!this.isClustered) {
         GlStateManager.func_187399_a(target, parameterName, parameter);
      }

   }

   @Redirect(
      method = {"setBrightness"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/renderer/GlStateManager;glTexEnvi(III)V",
   ordinal = 8
)
   )
   protected void glTexEnvi2(int target, int parameterName, int parameter) {
      if (!this.isClustered) {
         GlStateManager.func_187399_a(target, parameterName, parameter);
      }

   }
}

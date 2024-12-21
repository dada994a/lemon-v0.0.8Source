package com.lemonclient.mixin.mixins;

import com.lemonclient.api.event.events.PlayerJumpEvent;
import com.lemonclient.api.event.events.WaterPushEvent;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.exploits.Portal;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityPlayer.class})
public abstract class MixinEntityPlayer {
   @Shadow
   public abstract String func_70005_c_();

   @ModifyConstant(
      method = {"getPortalCooldown"},
      constant = {@Constant(
   intValue = 10
)}
   )
   private int getPortalCooldownHook(int n) {
      int intValue = n;
      Portal portal = (Portal)ModuleManager.getModule(Portal.class);
      if (portal.isEnabled() && (Boolean)portal.fastPortal.getValue()) {
         intValue = (Integer)portal.cooldown.getValue();
      }

      return intValue;
   }

   @Inject(
      method = {"jump"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void onJump(CallbackInfo callbackInfo) {
      if (Minecraft.func_71410_x().field_71439_g.func_70005_c_() == this.func_70005_c_()) {
         LemonClient.EVENT_BUS.post(new PlayerJumpEvent());
      }

   }

   @Inject(
      method = {"isPushedByWater"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onPushedByWater(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
      WaterPushEvent event = new WaterPushEvent();
      LemonClient.EVENT_BUS.post(event);
      if (event.isCancelled()) {
         callbackInfoReturnable.setReturnValue(false);
      }

   }
}

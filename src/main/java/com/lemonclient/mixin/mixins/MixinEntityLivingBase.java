package com.lemonclient.mixin.mixins;

import com.lemonclient.client.module.modules.render.SwingSpeed;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityLivingBase.class})
public class MixinEntityLivingBase {
   @Inject(
      method = {"getArmSwingAnimationEnd"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void getArmSwingAnimationEnd(CallbackInfoReturnable<Integer> info) {
      if (SwingSpeed.INSTANCE.isEnabled()) {
         info.setReturnValue(SwingSpeed.INSTANCE.speed.getValue());
      }

   }
}

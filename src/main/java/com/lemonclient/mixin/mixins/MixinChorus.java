package com.lemonclient.mixin.mixins;

import com.lemonclient.api.event.events.ChorusEvent;
import com.lemonclient.client.LemonClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemChorusFruit;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemChorusFruit.class})
public class MixinChorus extends ItemFood {
   public MixinChorus(int amount, float saturation) {
      super(amount, saturation, false);
   }

   @Inject(
      method = {"onItemUseFinish"},
      at = {@At("HEAD")}
   )
   public void attemptTeleportHook(ItemStack stack, World worldIn, EntityLivingBase entityLiving, CallbackInfoReturnable<ItemStack> cir) {
      ChorusEvent event = new ChorusEvent();
      LemonClient.EVENT_BUS.post(event);
   }
}

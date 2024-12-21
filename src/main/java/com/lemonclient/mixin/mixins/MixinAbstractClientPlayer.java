package com.lemonclient.mixin.mixins;

import com.lemonclient.api.util.render.CapeUtil;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.Cape;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractClientPlayer.class})
public abstract class MixinAbstractClientPlayer {
   @Shadow
   @Nullable
   protected abstract NetworkPlayerInfo func_175155_b();

   @Inject(
      method = {"getLocationCape"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void getLocationCape(CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
      UUID uuid = ((NetworkPlayerInfo)Objects.requireNonNull(this.func_175155_b())).func_178845_a().getId();
      if (ModuleManager.isModuleEnabled(Cape.class) && CapeUtil.hasCape(uuid)) {
         callbackInfoReturnable.setReturnValue(new ResourceLocation("lemonclient:cape.png"));
      }

   }
}

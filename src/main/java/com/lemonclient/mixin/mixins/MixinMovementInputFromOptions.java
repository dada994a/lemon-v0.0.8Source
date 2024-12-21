package com.lemonclient.mixin.mixins;

import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.movement.PlayerTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
   value = {MovementInputFromOptions.class},
   priority = 10000
)
public abstract class MixinMovementInputFromOptions extends MovementInput {
   @Redirect(
      method = {"updatePlayerMoveState"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/settings/KeyBinding;isKeyDown()Z"
)
   )
   public boolean isKeyPressed(KeyBinding keyBinding) {
      int keyCode = keyBinding.func_151463_i();
      if (keyCode > 0 && keyCode < 256) {
         PlayerTweaks playerTweaks = (PlayerTweaks)ModuleManager.getModule(PlayerTweaks.class);
         if (playerTweaks.isEnabled() && (Boolean)playerTweaks.guiMove.getValue() && Minecraft.func_71410_x().field_71462_r != null && !(Minecraft.func_71410_x().field_71462_r instanceof GuiChat)) {
            return Keyboard.isKeyDown(keyCode);
         }
      }

      return keyBinding.func_151470_d();
   }
}

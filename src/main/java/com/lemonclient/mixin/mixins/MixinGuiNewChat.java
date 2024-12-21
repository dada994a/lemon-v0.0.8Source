package com.lemonclient.mixin.mixins;

import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.misc.ChatModifier;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({GuiNewChat.class})
public abstract class MixinGuiNewChat {
   @Redirect(
      method = {"drawChat"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"
)
   )
   private void drawRectBackgroundClean(int left, int top, int right, int bottom, int color) {
      ChatModifier chatModifier = (ChatModifier)ModuleManager.getModule(ChatModifier.class);
      if (!chatModifier.isEnabled() || !(Boolean)chatModifier.clearBkg.getValue()) {
         Gui.func_73734_a(left, top, right, bottom, color);
      }

   }
}

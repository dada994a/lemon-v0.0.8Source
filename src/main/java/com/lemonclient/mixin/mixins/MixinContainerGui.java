package com.lemonclient.mixin.mixins;

import com.lemonclient.api.util.misc.MapPeek;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiContainer.class})
public class MixinContainerGui extends GuiScreen {
   MapPeek peek = new MapPeek();

   @Inject(
      method = {"drawScreen(IIF)V"},
      at = {@At("RETURN")}
   )
   public void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo info) {
      try {
         this.peek.draw(mouseX, mouseY, (GuiContainer)this.field_146297_k.field_71462_r);
      } catch (Exception var6) {
         var6.printStackTrace();
      }

   }
}

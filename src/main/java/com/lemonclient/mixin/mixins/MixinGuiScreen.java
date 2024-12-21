package com.lemonclient.mixin.mixins;

import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.render.ShulkerViewer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiScreen.class})
public class MixinGuiScreen {
   @Inject(
      method = {"renderToolTip"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void renderToolTip(ItemStack stack, int x, int y, CallbackInfo callbackInfo) {
      ShulkerViewer shulkerViewer = (ShulkerViewer)ModuleManager.getModule(ShulkerViewer.class);
      if (shulkerViewer.isEnabled() && stack.func_77973_b() instanceof ItemShulkerBox && stack.func_77978_p() != null && stack.func_77978_p().func_150297_b("BlockEntityTag", 10) && stack.func_77978_p().func_74775_l("BlockEntityTag").func_150297_b("Items", 9)) {
         callbackInfo.cancel();
         shulkerViewer.renderShulkerPreview(stack, x + 6, y - 33, 162, 66);
      }

   }
}

package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

@Module.Declaration(
   name = "32kTotem",
   category = Category.Combat
)
public class Anti32kTotem extends Module {
   IntegerSetting slot = this.registerInteger("Slot", 1, 1, 9);

   public void fast() {
      if ((!(mc.field_71462_r instanceof GuiContainer) || mc.field_71462_r instanceof GuiInventory) && mc.field_71439_g.field_71071_by.func_70301_a((Integer)this.slot.getValue() - 1).func_77973_b() != Items.field_190929_cY) {
         for(int i = 9; i < 36; ++i) {
            if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == Items.field_190929_cY) {
               mc.field_71442_b.func_187098_a(0, i, (Integer)this.slot.getValue() - 1, ClickType.SWAP, mc.field_71439_g);
               break;
            }
         }
      }

   }

   public String getHudInfo() {
      int totems = mc.field_71439_g.field_71071_by.field_70462_a.stream().filter((itemStack) -> {
         return itemStack.func_77973_b() == Items.field_190929_cY;
      }).mapToInt(ItemStack::func_190916_E).sum();
      if (mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_190929_cY) {
         ++totems;
      }

      return "[" + ChatFormatting.WHITE + "Totem " + totems + ChatFormatting.GRAY + "]";
   }
}

package com.lemonclient.client.module.modules.misc;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Arrays;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;

@Module.Declaration(
   name = "AutoNametag",
   category = Category.Misc
)
public class AutoNametag extends Module {
   ModeSetting modeSetting = this.registerMode("Mode", Arrays.asList("Any", "Wither"), "Wither");
   DoubleSetting range = this.registerDouble("Range", 3.5D, 0.0D, 10.0D);
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", true);
   BooleanSetting check = this.registerBoolean("Switch Check", true);
   BooleanSetting disable = this.registerBoolean("Auto Disable", true);
   private String currentName = "";
   private int currentSlot = -1;

   public void onUpdate() {
      this.findNameTags();
      this.useNameTag();
   }

   private void switchTo(int slot) {
      if (slot > -1 && slot < 9 && (!(Boolean)this.check.getValue() || mc.field_71439_g.field_71071_by.field_70461_c != slot)) {
         if ((Boolean)this.packetSwitch.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
         } else {
            mc.field_71439_g.field_71071_by.field_70461_c = slot;
         }

         mc.field_71442_b.func_78765_e();
      }

   }

   private void useNameTag() {
      int originalSlot = mc.field_71439_g.field_71071_by.field_70461_c;
      Iterator var2 = mc.field_71441_e.func_72910_y().iterator();

      while(true) {
         Entity w;
         int oldslot;
         label48:
         do {
            while(var2.hasNext()) {
               w = (Entity)var2.next();
               String var4 = (String)this.modeSetting.getValue();
               byte var5 = -1;
               switch(var4.hashCode()) {
               case -1703702509:
                  if (var4.equals("Wither")) {
                     var5 = 0;
                  }
                  break;
               case 65996:
                  if (var4.equals("Any")) {
                     var5 = 1;
                  }
               }

               switch(var5) {
               case 0:
                  if (w instanceof EntityWither && !w.func_145748_c_().func_150260_c().equals(this.currentName) && (double)mc.field_71439_g.func_70032_d(w) <= (Double)this.range.getValue()) {
                     oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
                     this.selectNameTags();
                     mc.field_71442_b.func_187097_a(mc.field_71439_g, w, EnumHand.MAIN_HAND);
                     this.switchTo(oldslot);
                  }
                  break;
               case 1:
                  continue label48;
               }
            }

            mc.field_71439_g.field_71071_by.field_70461_c = originalSlot;
            return;
         } while(!(w instanceof EntityMob) && !(w instanceof EntityAnimal));

         if (!w.func_145748_c_().func_150260_c().equals(this.currentName) && (double)mc.field_71439_g.func_70032_d(w) <= (Double)this.range.getValue()) {
            oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
            this.selectNameTags();
            mc.field_71442_b.func_187097_a(mc.field_71439_g, w, EnumHand.MAIN_HAND);
            this.switchTo(oldslot);
         }
      }
   }

   private void selectNameTags() {
      if (this.currentSlot != -1 && this.isNametag(this.currentSlot)) {
         this.switchTo(this.currentSlot);
      } else {
         if ((Boolean)this.disable.getValue()) {
            this.disable();
         }

      }
   }

   private void findNameTags() {
      for(int i = 0; i < 9; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (stack != ItemStack.field_190927_a && !(stack.func_77973_b() instanceof ItemBlock) && this.isNametag(i)) {
            this.currentName = stack.func_82833_r();
            this.currentSlot = i;
         }
      }

   }

   private boolean isNametag(int i) {
      ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
      Item tag = stack.func_77973_b();
      return tag instanceof ItemNameTag && !stack.func_82833_r().equals("Name Tag");
   }
}

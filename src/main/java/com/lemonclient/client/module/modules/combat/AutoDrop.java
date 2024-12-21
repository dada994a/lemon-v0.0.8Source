package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Arrays;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

@Module.Declaration(
   name = "AutoDrop",
   category = Category.Combat
)
public class AutoDrop extends Module {
   IntegerSetting delay = this.registerInteger("Drop Delay", 10, 0, 20);
   ModeSetting mode = this.registerMode("Sharpness", Arrays.asList("Sharp5", "Sharp32k", "Both"), "Both");
   private final Timing timer = new Timing();

   public void onUpdate() {
      String var1 = (String)this.mode.getValue();
      byte var2 = -1;
      switch(var1.hashCode()) {
      case -1819698773:
         if (var1.equals("Sharp5")) {
            var2 = 2;
         }
         break;
      case -678831646:
         if (var1.equals("Sharp32k")) {
            var2 = 0;
         }
         break;
      case 2076577:
         if (var1.equals("Both")) {
            var2 = 1;
         }
      }

      boolean holding5;
      EntityItem entityItem;
      switch(var2) {
      case 0:
         if (this.isSuperWeapon(mc.field_71439_g.func_184614_ca()) && this.timer.passedDs((double)(Integer)this.delay.getValue())) {
            holding5 = false;
            entityItem = mc.field_71439_g.func_71040_bB(!holding5);
            this.timer.reset();
            break;
         }
      case 1:
         if (this.checkSword(mc.field_71439_g.func_184614_ca()) && this.timer.passedDs((double)(Integer)this.delay.getValue())) {
            holding5 = false;
            entityItem = mc.field_71439_g.func_71040_bB(!holding5);
         }
      case 2:
         if (this.checkSharpness5(mc.field_71439_g.func_184614_ca()) && this.timer.passedDs((double)(Integer)this.delay.getValue())) {
            holding5 = false;
            entityItem = mc.field_71439_g.func_71040_bB(!holding5);
         }
      }

   }

   private boolean checkSword(ItemStack stack) {
      if (stack.func_77978_p() == null) {
         return false;
      } else if (stack.func_77986_q().func_150303_d() == 0) {
         return false;
      } else {
         NBTTagList enchants = (NBTTagList)stack.func_77978_p().func_74781_a("ench");

         for(int i = 0; i < enchants.func_74745_c(); ++i) {
            NBTTagCompound enchant = enchants.func_150305_b(i);
            if (enchant.func_74762_e("id") == 16) {
               int lvl = enchant.func_74762_e("lvl");
               if (lvl > 4) {
                  return true;
               }
               break;
            }
         }

         return false;
      }
   }

   private boolean isSuperWeapon(ItemStack item) {
      if (item == null) {
         return false;
      } else if (item.func_77978_p() == null) {
         return false;
      } else if (item.func_77986_q().func_150303_d() == 0) {
         return false;
      } else {
         NBTTagList enchants = (NBTTagList)item.func_77978_p().func_74781_a("ench");

         for(int i = 0; i < enchants.func_74745_c(); ++i) {
            NBTTagCompound enchant = enchants.func_150305_b(i);
            if (enchant.func_74762_e("id") == 16) {
               int lvl = enchant.func_74762_e("lvl");
               if (lvl >= 16) {
                  return true;
               }
               break;
            }
         }

         return false;
      }
   }

   private boolean checkSharpness5(ItemStack stack) {
      if (stack.func_77978_p() == null) {
         return false;
      } else if (stack.func_77986_q().func_150303_d() == 0) {
         return false;
      } else {
         NBTTagList enchants = (NBTTagList)stack.func_77978_p().func_74781_a("ench");

         for(int i = 0; i < enchants.func_74745_c(); ++i) {
            NBTTagCompound enchant = enchants.func_150305_b(i);
            if (enchant.func_74762_e("id") == 16) {
               int lvl = enchant.func_74762_e("lvl");
               if (lvl == 5) {
                  return true;
               }
               break;
            }
         }

         return false;
      }
   }
}

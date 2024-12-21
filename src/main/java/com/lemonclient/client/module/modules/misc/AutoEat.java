package com.lemonclient.client.module.modules.misc;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

@Module.Declaration(
   name = "AutoEat",
   category = Category.Misc
)
public class AutoEat extends Module {
   IntegerSetting health = this.registerInteger("Health", 10, 1, 36);
   BooleanSetting equal = this.registerBoolean("Equal", false);
   boolean eating = false;

   public void onDisable() {
      this.stopEating();
   }

   public void onTick() {
      if (EntityUtil.isDead(mc.field_71439_g)) {
         if (this.eating) {
            this.stopEating();
         }

      } else {
         if (this.shouldEat()) {
            EnumHand hand = null;
            if (this.isValid(mc.field_71439_g.func_184614_ca())) {
               hand = EnumHand.MAIN_HAND;
            }

            if (this.isValid(mc.field_71439_g.func_184592_cb())) {
               hand = EnumHand.OFF_HAND;
            }

            if (hand != null) {
               this.eat(hand);
            } else {
               int slot = this.findHotbarFood();
               if (slot != -1) {
                  mc.field_71439_g.field_71071_by.field_70461_c = slot;
               }
            }
         } else if (this.eating) {
            this.stopEating();
         }

      }
   }

   private int findHotbarFood() {
      for(int i = 0; i < 9; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (stack != ItemStack.field_190927_a && this.isValid(stack)) {
            return i;
         }
      }

      return -1;
   }

   private boolean shouldEat() {
      if ((Boolean)this.equal.getValue()) {
         return mc.field_71439_g.func_110143_aJ() + mc.field_71439_g.func_110139_bj() <= (float)(Integer)this.health.getValue();
      } else {
         return mc.field_71439_g.func_110143_aJ() + mc.field_71439_g.func_110139_bj() < (float)(Integer)this.health.getValue();
      }
   }

   private void eat(EnumHand hand) {
      if (!this.eating || !mc.field_71439_g.func_184587_cr() || mc.field_71439_g.func_184600_cs() != hand) {
         KeyBinding.func_74510_a(mc.field_71474_y.field_74313_G.func_151463_i(), true);
         mc.field_71442_b.func_187101_a(mc.field_71439_g, mc.field_71441_e, hand);
      }

      this.eating = true;
   }

   private void stopEating() {
      KeyBinding.func_74510_a(mc.field_71474_y.field_74313_G.func_151463_i(), false);
      this.eating = false;
   }

   private boolean isValid(ItemStack itemStack) {
      Item item = itemStack.field_151002_e;
      return item instanceof ItemFood && item != Items.field_185161_cS && !this.isBadFood(itemStack, (ItemFood)item) && mc.field_71439_g.func_71043_e(item == Items.field_151153_ao);
   }

   private boolean isBadFood(ItemStack itemStack, ItemFood item) {
      return item == Items.field_151078_bh || item == Items.field_151070_bp || item == Items.field_151170_bI || item == Items.field_151115_aP && (itemStack.func_77960_j() == 3 || itemStack.func_77960_j() == 2);
   }
}

package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemAir;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;

@Module.Declaration(
   name = "Pull32k",
   category = Category.Combat
)
public class Pull32k extends Module {
   BooleanSetting force = this.registerBoolean("Force Switch", true);
   IntegerSetting slot = this.registerInteger("Slot", 1, 1, 9, () -> {
      return (Boolean)this.force.getValue();
   });
   DoubleSetting range = this.registerDouble("Range", 7.5D, 0.0D, 64.0D);
   boolean foundsword = false;

   public void onUpdate() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if ((Double)this.range.getValue() != 0.0D) {
            EntityPlayer enemy = PlayerUtil.getNearestPlayer((Double)this.range.getValue());
            if (enemy == null) {
               return;
            }
         }

         boolean foundair = false;
         int enchantedSwordIndex = -1;

         int i;
         ItemStack itemStack;
         for(i = 0; i < 9; ++i) {
            itemStack = (ItemStack)mc.field_71439_g.field_71071_by.field_70462_a.get(i);
            if (EnchantmentHelper.func_77506_a(Enchantments.field_185302_k, itemStack) >= 25) {
               enchantedSwordIndex = i;
               this.foundsword = true;
            }

            if (!this.foundsword) {
               enchantedSwordIndex = -1;
            }
         }

         if (enchantedSwordIndex != -1) {
            if (mc.field_71439_g.field_71071_by.field_70461_c != enchantedSwordIndex) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(enchantedSwordIndex));
               mc.field_71439_g.field_71071_by.field_70461_c = enchantedSwordIndex;
               mc.field_71442_b.func_78765_e();
            }
         } else if (mc.field_71439_g.field_71070_bA != null && mc.field_71439_g.field_71070_bA instanceof ContainerHopper && mc.field_71439_g.field_71070_bA.field_75151_b != null && !mc.field_71439_g.field_71070_bA.field_75151_b.isEmpty()) {
            for(i = 0; i < 5; ++i) {
               if (EnchantmentHelper.func_77506_a(Enchantments.field_185302_k, ((Slot)mc.field_71439_g.field_71070_bA.field_75151_b.get(0)).field_75224_c.func_70301_a(i)) >= 20) {
                  enchantedSwordIndex = i;
                  break;
               }
            }

            if (enchantedSwordIndex == -1) {
               return;
            }

            for(i = 0; i < 9; ++i) {
               itemStack = (ItemStack)mc.field_71439_g.field_71071_by.field_70462_a.get(i);
               if (itemStack.func_77973_b() instanceof ItemAir || this.checkStuff(i)) {
                  if (mc.field_71439_g.field_71071_by.field_70461_c != i) {
                     mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(i));
                     mc.field_71439_g.field_71071_by.field_70461_c = i;
                     mc.field_71442_b.func_78765_e();
                  }

                  foundair = true;
                  break;
               }
            }

            if (!foundair && (Boolean)this.force.getValue()) {
               i = (Integer)this.slot.getValue() - 1;
               if (mc.field_71439_g.field_71071_by.field_70461_c != i) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(i));
                  mc.field_71439_g.field_71071_by.field_70461_c = i;
                  mc.field_71442_b.func_78765_e();
               }

               foundair = true;
            }

            if (foundair) {
               mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71070_bA.field_75152_c, enchantedSwordIndex, mc.field_71439_g.field_71071_by.field_70461_c, ClickType.SWAP, mc.field_71439_g);
            }
         }

      }
   }

   public boolean checkStuff(int slot) {
      return EnchantmentHelper.func_77506_a(Enchantments.field_185302_k, (ItemStack)mc.field_71439_g.field_71071_by.field_70462_a.get(slot)) == 5;
   }
}

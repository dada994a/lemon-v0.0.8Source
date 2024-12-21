package com.lemonclient.api.util.world;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public class ItemUtil {
   private static final Minecraft mc = Minecraft.func_71410_x();

   public static boolean isArmorUnderPercent(EntityPlayer player, float percent) {
      for(int i = 3; i >= 0; --i) {
         ItemStack stack = (ItemStack)player.field_71071_by.field_70460_b.get(i);
         if (getDamageInPercent(stack) < percent) {
            return true;
         }
      }

      return false;
   }

   public static int getItemFromHotbar(Class<?> clazz) {
      int slot = -1;

      for(int i = 8; i >= 0; --i) {
         if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b().getClass() == clazz) {
            slot = i;
            break;
         }
      }

      return slot;
   }

   public static int getItemFromHotbar(Item item) {
      int slot = -1;

      for(int i = 8; i >= 0; --i) {
         if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == item) {
            slot = i;
            break;
         }
      }

      return slot;
   }

   public static int getBlockFromHotbar(Block block) {
      int slot = -1;

      for(int i = 8; i >= 0; --i) {
         if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == Item.func_150898_a(block)) {
            slot = i;
            break;
         }
      }

      return slot;
   }

   public static int getItemSlot(Item item) {
      int slot = -1;

      for(int i = 44; i >= 0; --i) {
         if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == item) {
            if (i < 9) {
               i += 36;
            }

            slot = i;
            break;
         }
      }

      return slot;
   }

   public static int getItemCount(Item item) {
      int count = 0;

      for(int i = 44; i >= 0; --i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (stack.func_77973_b() == item) {
            count += stack.func_190916_E();
         }
      }

      if (mc.field_71439_g.func_184582_a(EntityEquipmentSlot.OFFHAND).func_77973_b() == item) {
         count += mc.field_71439_g.func_184582_a(EntityEquipmentSlot.OFFHAND).func_190916_E();
      }

      return count;
   }

   public static int getRoundedDamage(ItemStack stack) {
      return (int)getDamageInPercent(stack);
   }

   public static boolean hasDurability(ItemStack stack) {
      Item item = stack.func_77973_b();
      return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemShield;
   }

   public static float getDamageInPercent(ItemStack stack) {
      float green = ((float)stack.func_77958_k() - (float)stack.func_77952_i()) / (float)stack.func_77958_k();
      float red = 1.0F - green;
      return (float)(100 - (int)(red * 100.0F));
   }
}

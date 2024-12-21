package com.lemonclient.api.util.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;

public class InventoryUtil {
   private static final Minecraft mc = Minecraft.func_71410_x();
   public static final ItemStack ILLEGAL_STACK;

   public static void switchTo(int slot) {
      if (mc.field_71439_g.field_71071_by.field_70461_c != slot && slot > -1 && slot < 9) {
         mc.field_71439_g.field_71071_by.field_70461_c = slot;
         mc.field_71442_b.func_78750_j();
      }

   }

   public static void switchToBypass(int slot) {
      Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
         if (mc.field_71439_g.field_71071_by.field_70461_c != slot && slot > -1 && slot < 9) {
            int lastSlot = mc.field_71439_g.field_71071_by.field_70461_c;
            int targetSlot = hotbarToInventory(slot);
            int currentSlot = hotbarToInventory(lastSlot);
            mc.field_71442_b.func_187098_a(0, targetSlot, 0, ClickType.PICKUP, mc.field_71439_g);
            mc.field_71442_b.func_187098_a(0, currentSlot, 0, ClickType.PICKUP, mc.field_71439_g);
            mc.field_71442_b.func_187098_a(0, targetSlot, 0, ClickType.PICKUP, mc.field_71439_g);
         }

      });
   }

   public static void switchToBypassAlt(int slot) {
      Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
         if (mc.field_71439_g.field_71071_by.field_70461_c != slot && slot > -1 && slot < 9) {
            Locks.acquire(Locks.WINDOW_CLICK_LOCK, () -> {
               mc.field_71442_b.func_187098_a(0, slot, mc.field_71439_g.field_71071_by.field_70461_c, ClickType.SWAP, mc.field_71439_g);
            });
         }

      });
   }

   public static void bypassSwitch(int slot) {
      if (slot >= 0) {
         mc.field_71442_b.func_187100_a(slot);
      }

   }

   public static int hotbarToInventory(int slot) {
      if (slot == -2) {
         return 45;
      } else {
         return slot > -1 && slot < 9 ? 36 + slot : slot;
      }
   }

   public static void swap(int InvSlot, int newSlot) {
      mc.field_71442_b.func_187098_a(0, InvSlot, 0, ClickType.PICKUP, mc.field_71439_g);
      mc.field_71442_b.func_187098_a(0, newSlot, 0, ClickType.PICKUP, mc.field_71439_g);
      mc.field_71442_b.func_187098_a(0, InvSlot, 0, ClickType.PICKUP, mc.field_71439_g);
      mc.field_71442_b.func_78765_e();
   }

   public static int getHotBarPressure(String mode) {
      for(int i = 0; i < 9; ++i) {
         if (mode.equals("Pressure")) {
            if (isPressure(mc.field_71439_g.field_71071_by.func_70301_a(i))) {
               return i;
            }
         } else if (isString(mc.field_71439_g.field_71071_by.func_70301_a(i))) {
            return i;
         }
      }

      return -1;
   }

   public static boolean isString(ItemStack stack) {
      if (stack != ItemStack.field_190927_a && !(stack.func_77973_b() instanceof ItemBlock)) {
         return stack.func_77973_b() == Items.field_151007_F;
      } else {
         return false;
      }
   }

   public static boolean isPressure(ItemStack stack) {
      return stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemBlock ? ((ItemBlock)stack.func_77973_b()).func_179223_d() instanceof BlockPressurePlate : false;
   }

   public static Map<Integer, ItemStack> getInventoryAndHotbarSlots() {
      HashMap<Integer, ItemStack> fullInventorySlots = new HashMap();

      for(int current = 9; current <= 44; ++current) {
         fullInventorySlots.put(current, mc.field_71439_g.field_71069_bz.func_75138_a().get(current));
      }

      return fullInventorySlots;
   }

   public static boolean isBlock(Item item, Class clazz) {
      if (item instanceof ItemBlock) {
         Block block = ((ItemBlock)item).func_179223_d();
         return clazz.isInstance(block);
      } else {
         return false;
      }
   }

   public static void click(int windowIdIn, int slotIdIn, int usedButtonIn, ClickType modeIn, ItemStack clickedItemIn, short actionNumberIn) {
      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketClickWindow(windowIdIn, slotIdIn, usedButtonIn, modeIn, clickedItemIn, actionNumberIn));
   }

   public static int findCrystalBlockSlot() {
      int slot = -1;
      List<ItemStack> mainInventory = mc.field_71439_g.field_71071_by.field_70462_a;

      for(int i = 0; i < 9; ++i) {
         ItemStack stack = (ItemStack)mainInventory.get(i);
         if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemBlock) {
            Block block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
            if (block.func_176194_O().func_177622_c().field_149782_v > 6.0F) {
               slot = i;
               break;
            }
         }
      }

      return slot;
   }

   public static void illegalSync() {
      if (mc.field_71439_g != null) {
         click(0, 0, 0, ClickType.PICKUP, ILLEGAL_STACK, (short)0);
      }

   }

   public static int findObsidianSlot(boolean offHandActived, boolean activeBefore) {
      int slot = -1;
      List<ItemStack> mainInventory = mc.field_71439_g.field_71071_by.field_70462_a;

      for(int i = 0; i < 9; ++i) {
         ItemStack stack = (ItemStack)mainInventory.get(i);
         if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemBlock) {
            Block block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
            if (block instanceof BlockObsidian) {
               slot = i;
               break;
            }
         }
      }

      return slot;
   }

   public static int findEChestSlot(boolean offHandActived, boolean activeBefore) {
      int slot = -1;
      List<ItemStack> mainInventory = mc.field_71439_g.field_71071_by.field_70462_a;

      for(int i = 0; i < 9; ++i) {
         ItemStack stack = (ItemStack)mainInventory.get(i);
         if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemBlock) {
            Block block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
            if (block instanceof BlockEnderChest) {
               slot = i;
               break;
            }
         }
      }

      return slot;
   }

   public static int findSkullSlot() {
      int slot = -1;
      List<ItemStack> mainInventory = mc.field_71439_g.field_71071_by.field_70462_a;

      for(int i = 0; i < 9; ++i) {
         ItemStack stack = (ItemStack)mainInventory.get(i);
         if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemSkull) {
            return i;
         }
      }

      return slot;
   }

   public static int findTotemSlot(int lower, int upper) {
      int slot = -1;
      List<ItemStack> mainInventory = mc.field_71439_g.field_71071_by.field_70462_a;

      for(int i = lower; i <= upper; ++i) {
         ItemStack stack = (ItemStack)mainInventory.get(i);
         if (stack != ItemStack.field_190927_a && stack.func_77973_b() == Items.field_190929_cY) {
            slot = i;
            break;
         }
      }

      return slot;
   }

   public static int findFirstItemSlot(Class<? extends Item> itemToFind, int lower, int upper) {
      int slot = -1;
      List<ItemStack> mainInventory = mc.field_71439_g.field_71071_by.field_70462_a;

      for(int i = lower; i <= upper; ++i) {
         ItemStack stack = (ItemStack)mainInventory.get(i);
         if (stack != ItemStack.field_190927_a && itemToFind.isInstance(stack.func_77973_b()) && itemToFind.isInstance(stack.func_77973_b())) {
            slot = i;
            break;
         }
      }

      return slot;
   }

   public static int findStackInventory(Item input, boolean withHotbar) {
      for(int i = withHotbar ? 0 : 9; i < 36; ++i) {
         Item item = mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b();
         if (Item.func_150891_b(input) == Item.func_150891_b(item)) {
            return i + (i < 9 ? 36 : 0);
         }
      }

      return -1;
   }

   public static int getItemSlot(Item input) {
      if (mc.field_71439_g == null) {
         return 0;
      } else {
         for(int i = 0; i < mc.field_71439_g.field_71069_bz.func_75138_a().size(); ++i) {
            if (i != 0 && i != 5 && i != 6 && i != 7 && i != 8) {
               ItemStack s = (ItemStack)mc.field_71439_g.field_71069_bz.func_75138_a().get(i);
               if (!s.func_190926_b() && s.func_77973_b() == input) {
                  return i;
               }
            }
         }

         return -1;
      }
   }

   public static int getItemInHotbar(Item p_Item) {
      for(int l_I = 0; l_I < 9; ++l_I) {
         ItemStack l_Stack = mc.field_71439_g.field_71071_by.func_70301_a(l_I);
         if (l_Stack != ItemStack.field_190927_a && l_Stack.func_77973_b() == p_Item) {
            return l_I;
         }
      }

      return -1;
   }

   public static int getPotion(String potion) {
      for(int l_I = 0; l_I < 36; ++l_I) {
         ItemStack l_Stack = mc.field_71439_g.field_71071_by.func_70301_a(l_I);
         if (l_Stack != ItemStack.field_190927_a && l_Stack.func_77973_b() == Items.field_185155_bH && ((ResourceLocation)Objects.requireNonNull(PotionUtils.func_185191_c(mc.field_71439_g.field_71071_by.func_70301_a(l_I)).getRegistryName())).func_110623_a().contains(potion)) {
            return l_I;
         }
      }

      return -1;
   }

   public static int findFirstBlockSlot(Class<? extends Block> blockToFind, int lower, int upper) {
      int slot = -1;
      List<ItemStack> mainInventory = mc.field_71439_g.field_71071_by.field_70462_a;

      for(int i = lower; i <= upper; ++i) {
         ItemStack stack = (ItemStack)mainInventory.get(i);
         if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemBlock && blockToFind.isInstance(((ItemBlock)stack.func_77973_b()).func_179223_d())) {
            slot = i;
            break;
         }
      }

      return slot;
   }

   public static List<Integer> findAllItemSlots(Class<? extends Item> itemToFind) {
      List<Integer> slots = new ArrayList();
      List<ItemStack> mainInventory = mc.field_71439_g.field_71071_by.field_70462_a;

      for(int i = 0; i < 36; ++i) {
         ItemStack stack = (ItemStack)mainInventory.get(i);
         if (stack != ItemStack.field_190927_a && itemToFind.isInstance(stack.func_77973_b())) {
            slots.add(i);
         }
      }

      return slots;
   }

   public static List<Integer> findAllBlockSlots(Class<? extends Block> blockToFind) {
      List<Integer> slots = new ArrayList();
      List<ItemStack> mainInventory = mc.field_71439_g.field_71071_by.field_70462_a;

      for(int i = 0; i < 36; ++i) {
         ItemStack stack = (ItemStack)mainInventory.get(i);
         if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemBlock && blockToFind.isInstance(((ItemBlock)stack.func_77973_b()).func_179223_d())) {
            slots.add(i);
         }
      }

      return slots;
   }

   public static int findToolForBlockState(IBlockState iBlockState, int lower, int upper) {
      int slot = -1;
      List<ItemStack> mainInventory = mc.field_71439_g.field_71071_by.field_70462_a;
      double foundMaxSpeed = 0.0D;

      for(int i = lower; i <= upper; ++i) {
         ItemStack itemStack = (ItemStack)mainInventory.get(i);
         if (itemStack != ItemStack.field_190927_a) {
            float breakSpeed = itemStack.func_150997_a(iBlockState);
            int efficiencySpeed = EnchantmentHelper.func_77506_a(Enchantments.field_185305_q, itemStack);
            if (breakSpeed > 1.0F) {
               breakSpeed = (float)((double)breakSpeed + (efficiencySpeed > 0 ? Math.pow((double)efficiencySpeed, 2.0D) + 1.0D : 0.0D));
               if ((double)breakSpeed > foundMaxSpeed) {
                  foundMaxSpeed = (double)breakSpeed;
                  slot = i;
               }
            }
         }
      }

      return slot;
   }

   public static int getEmptyCounts() {
      if (mc.field_71439_g == null) {
         return 0;
      } else {
         int count = 0;

         for(int i = 0; i <= 35; ++i) {
            ItemStack stack = (ItemStack)mc.field_71439_g.field_71071_by.field_70462_a.get(i);
            if (stack == ItemStack.field_190927_a || stack.func_77973_b() == Items.field_190931_a) {
               ++count;
            }
         }

         return count;
      }
   }

   static {
      ILLEGAL_STACK = new ItemStack(Item.func_150898_a(Blocks.field_150357_h));
   }
}

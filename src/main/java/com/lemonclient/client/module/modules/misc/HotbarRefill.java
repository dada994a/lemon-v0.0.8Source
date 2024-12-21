package com.lemonclient.client.module.modules.misc;

import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.misc.Pair;
import com.lemonclient.api.util.player.InventoryUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

@Module.Declaration(
   name = "HotbarRefill",
   category = Category.Misc
)
public class HotbarRefill extends Module {
   IntegerSetting threshold = this.registerInteger("Threshold", 32, 1, 63);
   IntegerSetting tickDelay = this.registerInteger("Tick Delay", 2, 1, 10);
   private int delayStep = 0;

   public void onUpdate() {
      if (mc.field_71439_g != null) {
         if (!(mc.field_71462_r instanceof GuiContainer)) {
            if (this.delayStep < (Integer)this.tickDelay.getValue()) {
               ++this.delayStep;
            } else {
               this.delayStep = 0;
               Pair<Integer, Integer> slots = this.findReplenishableHotbarSlot();
               if (slots != null) {
                  int inventorySlot = (Integer)slots.getKey();
                  int hotbarSlot = (Integer)slots.getValue();
                  mc.field_71442_b.func_187098_a(0, inventorySlot, 0, ClickType.QUICK_MOVE, mc.field_71439_g);
               }
            }
         }
      }
   }

   private Pair<Integer, Integer> findReplenishableHotbarSlot() {
      List<ItemStack> inventory = mc.field_71439_g.field_71071_by.field_70462_a;

      for(int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
         ItemStack stack = (ItemStack)inventory.get(hotbarSlot);
         if (stack.func_77985_e() && !stack.field_190928_g && stack.func_77973_b() != Items.field_190931_a && stack.field_77994_a < stack.func_77976_d() && stack.field_77994_a <= (Integer)this.threshold.getValue()) {
            int inventorySlot = this.findCompatibleInventorySlot(stack);
            if (inventorySlot != -1) {
               return new Pair(inventorySlot, hotbarSlot);
            }
         }
      }

      return null;
   }

   private int findCompatibleInventorySlot(ItemStack hotbarStack) {
      Item item = hotbarStack.func_77973_b();
      List potentialSlots;
      if (item instanceof ItemBlock) {
         potentialSlots = InventoryUtil.findAllBlockSlots(((ItemBlock)item).func_179223_d().getClass());
      } else {
         potentialSlots = InventoryUtil.findAllItemSlots(item.getClass());
      }

      potentialSlots = (List)potentialSlots.stream().filter((integer) -> {
         return integer > 8 && integer < 36;
      }).sorted(Comparator.comparingInt((interger) -> {
         return -interger;
      })).collect(Collectors.toList());
      Iterator var4 = potentialSlots.iterator();

      int slot;
      do {
         if (!var4.hasNext()) {
            return -1;
         }

         slot = (Integer)var4.next();
      } while(!this.isCompatibleStacks(hotbarStack, mc.field_71439_g.field_71071_by.func_70301_a(slot)));

      return slot;
   }

   private boolean isCompatibleStacks(ItemStack stack1, ItemStack stack2) {
      if (!stack1.func_77973_b().equals(stack2.func_77973_b())) {
         return false;
      } else {
         if (stack1.func_77973_b() instanceof ItemBlock && stack2.func_77973_b() instanceof ItemBlock) {
            Block block1 = ((ItemBlock)stack1.func_77973_b()).func_179223_d();
            Block block2 = ((ItemBlock)stack2.func_77973_b()).func_179223_d();
            if (!block1.field_149764_J.equals(block2.field_149764_J)) {
               return false;
            }
         }

         if (!stack1.func_82833_r().equals(stack2.func_82833_r())) {
            return false;
         } else {
            return stack1.func_77952_i() == stack2.func_77952_i();
         }
      }
   }
}

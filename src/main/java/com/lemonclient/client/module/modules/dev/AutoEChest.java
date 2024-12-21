package com.lemonclient.client.module.modules.dev;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketClickWindow;

@Module.Declaration(
   name = "AutoSwitchEChest",
   category = Category.Dev
)
public class AutoEChest extends Module {
   IntegerSetting count = this.registerInteger("Count", 16, 1, 64);
   IntegerSetting backCount = this.registerInteger("SwitchBack Count", 121, 1, 256);
   BooleanSetting update = this.registerBoolean("UpdateController", true);
   int slot;
   int slot2;
   boolean switched = false;

   private void windowClick(int slot, int to) {
      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketClickWindow(mc.field_71439_g.field_71069_bz.field_75152_c, slot, to, ClickType.SWAP, ItemStack.field_190927_a, mc.field_71439_g.field_71070_bA.func_75136_a(mc.field_71439_g.field_71071_by)));
      if ((Boolean)this.update.getValue()) {
         mc.field_71442_b.func_78765_e();
      }

   }

   public void onUpdate() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         int slot = BurrowUtil.findHotbarBlock(BlockObsidian.class);
         int echest = BurrowUtil.findInventoryBlock(BlockEnderChest.class);
         if (slot != -1 && echest != -1) {
            ItemStack stack = (ItemStack)mc.field_71439_g.field_71071_by.field_70462_a.get(slot);
            if (stack.field_77994_a <= (Integer)this.count.getValue()) {
               this.windowClick(echest, slot);
               this.slot = echest;
               this.slot2 = slot;
               this.switched = true;
            }

            if (this.switched) {
               int obsiCount = BurrowUtil.getCount(BlockObsidian.class);
               if (obsiCount >= (Integer)this.backCount.getValue()) {
                  this.windowClick(this.slot, this.slot2);
               }

            }
         }
      }
   }
}

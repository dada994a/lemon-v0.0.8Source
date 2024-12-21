package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;

@Module.Declaration(
   name = "Quiver",
   category = Category.Combat
)
public class Quiver extends Module {
   IntegerSetting tickDelay = this.registerInteger("TickDelay", 3, 0, 8);

   public void onUpdate() {
      if (mc.field_71439_g != null) {
         if (mc.field_71439_g.field_71071_by.func_70448_g().func_77973_b() instanceof ItemBow && mc.field_71439_g.func_184587_cr() && mc.field_71439_g.func_184612_cw() >= (Integer)this.tickDelay.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new Rotation(mc.field_71439_g.field_71109_bG, -90.0F, mc.field_71439_g.field_70122_E));
            mc.field_71442_b.func_78766_c(mc.field_71439_g);
         }

         List<Integer> arrowSlots = getItemInventory(Items.field_185167_i);
         if ((Integer)arrowSlots.get(0) == -1) {
            return;
         }

         int speedSlot = true;
         int strengthSlot = true;
         Iterator var4 = arrowSlots.iterator();

         while(var4.hasNext()) {
            Integer slot = (Integer)var4.next();
            if (PotionUtils.func_185191_c(mc.field_71439_g.field_71071_by.func_70301_a(slot)).getRegistryName().func_110623_a().contains("swiftness")) {
               int var6 = slot;
            } else if (((ResourceLocation)Objects.requireNonNull(PotionUtils.func_185191_c(mc.field_71439_g.field_71071_by.func_70301_a(slot)).getRegistryName())).func_110623_a().contains("strength")) {
               int var7 = slot;
            }
         }
      }

   }

   public static List<Integer> getItemInventory(Item item) {
      List<Integer> ints = new ArrayList();

      for(int i = 9; i < 36; ++i) {
         Item target = mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b();
         if (item instanceof ItemBlock && ((ItemBlock)item).func_179223_d().equals(item)) {
            ints.add(i);
         }
      }

      if (ints.size() == 0) {
         ints.add(-1);
      }

      return ints;
   }
}

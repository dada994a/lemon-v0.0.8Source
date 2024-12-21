package com.lemonclient.client.module.modules.dev;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.misc.Pair;
import com.lemonclient.api.util.player.InventoryUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Iterator;
import java.util.List;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

@Module.Declaration(
   name = "SwordSwitch",
   category = Category.Dev
)
public class SwordSwitch extends Module {
   BooleanSetting disable = this.registerBoolean("Disable", true);

   public void onUpdate() {
      new Pair(0.0F, -1);
      Pair<Float, Integer> newSlot = this.findSwordSlot();
      if ((Integer)newSlot.getValue() != -1) {
         mc.field_71439_g.field_71071_by.field_70461_c = (Integer)newSlot.getValue();
         if ((Boolean)this.disable.getValue()) {
            this.disable();
         }

      } else {
         MessageBus.sendClientPrefixMessage("Cant find sword", Notification.Type.ERROR);
         this.disable();
      }
   }

   private Pair<Float, Integer> findSwordSlot() {
      List<Integer> items = InventoryUtil.findAllItemSlots(ItemSword.class);
      List<ItemStack> inventory = mc.field_71439_g.field_71071_by.field_70462_a;
      float bestModifier = 0.0F;
      int correspondingSlot = -1;
      Iterator var5 = items.iterator();

      while(var5.hasNext()) {
         Integer integer = (Integer)var5.next();
         if (integer <= 8) {
            ItemStack stack = (ItemStack)inventory.get(integer);
            float modifier = (EnchantmentHelper.func_152377_a(stack, EnumCreatureAttribute.UNDEFINED) + 1.0F) * ((ItemSword)stack.func_77973_b()).func_150931_i();
            if (modifier > bestModifier) {
               bestModifier = modifier;
               correspondingSlot = integer;
            }
         }
      }

      return new Pair(bestModifier, correspondingSlot);
   }
}

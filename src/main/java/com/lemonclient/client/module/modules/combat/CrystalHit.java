package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.world.combat.CrystalUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Comparator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketHeldItemChange;

@Module.Declaration(
   name = "CrystalHit",
   category = Category.Combat
)
public class CrystalHit extends Module {
   IntegerSetting range = this.registerInteger("Range", 4, 0, 10);
   IntegerSetting delay = this.registerInteger("Delay", 0, 0, 40);
   BooleanSetting packet = this.registerBoolean("Packet Break", false);
   BooleanSetting swing = this.registerBoolean("Swing", false);
   BooleanSetting antiWeakness = this.registerBoolean("Anti Weakness", false);
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", true);
   BooleanSetting check = this.registerBoolean("Switch Check", true);
   BooleanSetting silent = this.registerBoolean("Silent Switch", false);
   private boolean isAttacking = false;
   private int oldSlot = -1;

   public void onUpdate() {
      EntityEnderCrystal crystal = (EntityEnderCrystal)mc.field_71441_e.field_72996_f.stream().filter((entity) -> {
         return entity instanceof EntityEnderCrystal;
      }).map((entity) -> {
         return (EntityEnderCrystal)entity;
      }).min(Comparator.comparing((c) -> {
         return mc.field_71439_g.func_70032_d(c);
      })).orElse((Object)null);
      if (crystal != null && mc.field_71439_g.func_70032_d(crystal) <= (float)(Integer)this.range.getValue()) {
         int delaytime = 0;
         if (delaytime >= (Integer)this.delay.getValue()) {
            if ((Boolean)this.antiWeakness.getValue() && mc.field_71439_g.func_70644_a(MobEffects.field_76437_t)) {
               if (!this.isAttacking) {
                  this.oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
                  this.isAttacking = true;
               }

               int newSlot = -1;

               for(int i = 0; i < 9; ++i) {
                  ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
                  if (stack != ItemStack.field_190927_a) {
                     if (stack.func_77973_b() instanceof ItemSword) {
                        newSlot = i;
                        break;
                     }

                     if (stack.func_77973_b() instanceof ItemTool) {
                        newSlot = i;
                     }
                  }
               }

               if (newSlot != -1) {
                  this.switchTo(newSlot);
               }
            }

            if (!(Boolean)this.packet.getValue()) {
               CrystalUtil.breakCrystal((Entity)crystal, (Boolean)this.swing.getValue());
            } else {
               CrystalUtil.breakCrystalPacket(crystal, (Boolean)this.swing.getValue());
            }

            if ((Boolean)this.silent.getValue()) {
               this.switchTo(this.oldSlot);
            }
         }
      } else {
         if (this.oldSlot != -1) {
            mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
            this.oldSlot = -1;
         }

         this.isAttacking = false;
      }

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
}

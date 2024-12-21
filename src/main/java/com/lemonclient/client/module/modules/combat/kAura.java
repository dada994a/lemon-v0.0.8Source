package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Arrays;
import java.util.Iterator;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;

@Module.Declaration(
   name = "32kAura",
   category = Category.Combat
)
public class kAura extends Module {
   private int hasWaited;
   ModeSetting time = this.registerMode("Time Mode", Arrays.asList("Tick", "onUpdate", "Both", "Fast"), "Tick");
   ModeSetting mode = this.registerMode("Mode", Arrays.asList("CPS", "CPT"), "CPS");
   IntegerSetting range = this.registerInteger("Range", 6, 0, 20);
   BooleanSetting only32k = this.registerBoolean("32k Only", true);
   BooleanSetting xin = this.registerBoolean("Xin", true);
   BooleanSetting packet = this.registerBoolean("Packet Attack", true);
   BooleanSetting swing = this.registerBoolean("Swing", true);
   BooleanSetting autoswitch = this.registerBoolean("Auto Switch", true);
   BooleanSetting packetswitch = this.registerBoolean("Packet Switch", true);
   BooleanSetting playersOnly = this.registerBoolean("Players only", true);
   IntegerSetting hit = this.registerInteger("Hit", 20, 0, Integer.MAX_VALUE);

   public void onUpdate() {
      if (((String)this.time.getValue()).equals("onUpdate") || ((String)this.time.getValue()).equals("Both")) {
         this.attack();
      }

   }

   public void onTick() {
      if (((String)this.time.getValue()).equals("Tick") || ((String)this.time.getValue()).equals("Both")) {
         this.attack();
      }

   }

   public void fast() {
      if (((String)this.time.getValue()).equals("Fast")) {
         this.attack();
      }

   }

   private void attack() {
      if (!(Boolean)this.xin.getValue()) {
         int reqDelay;
         if (((String)this.mode.getValue()).equals("CPS")) {
            reqDelay = (int)Math.round(20.0D / (double)(Integer)this.hit.getValue());
         } else {
            reqDelay = (int)Math.round(1.0D / (double)(Integer)this.hit.getValue());
         }

         if (this.hasWaited < reqDelay) {
            ++this.hasWaited;
            return;
         }
      }

      this.hasWaited = 0;
      Iterator var4 = mc.field_71441_e.func_72910_y().iterator();

      while(true) {
         Entity entity;
         do {
            do {
               do {
                  do {
                     do {
                        do {
                           if (!var4.hasNext()) {
                              return;
                           }

                           entity = (Entity)var4.next();
                        } while(!(entity instanceof EntityLivingBase));
                     } while(entity == mc.field_71439_g);
                  } while(mc.field_71439_g.func_70032_d(entity) > (float)(Integer)this.range.getValue());
               } while(((EntityLivingBase)entity).func_110143_aJ() <= 0.0F);
            } while(!(entity instanceof EntityPlayer) && (Boolean)this.playersOnly.getValue());

            if ((Boolean)this.autoswitch.getValue()) {
               int oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
               this.equipBestWeapon();
               if (!this.isSuperWeapon(mc.field_71439_g.func_184614_ca()) && (Boolean)this.only32k.getValue()) {
                  this.switchtoslot(oldSlot);
               }
            }
         } while((Boolean)this.only32k.getValue() && !this.isSuperWeapon(mc.field_71439_g.func_184614_ca()));

         if (!SocialManager.isFriend(entity.func_70005_c_())) {
            boolean attack = true;
            if ((Boolean)this.xin.getValue() && mc.field_71439_g.func_184825_o(0.0F) < 1.0F) {
               attack = false;
            }

            if (attack) {
               if ((Boolean)this.packet.getValue()) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketUseEntity(entity));
               } else {
                  mc.field_71442_b.func_78764_a(mc.field_71439_g, entity);
               }

               if ((Boolean)this.swing.getValue()) {
                  mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
               }
            }
         }
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

   private void switchtoslot(int slot) {
      if ((Boolean)this.packetswitch.getValue()) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
      } else {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
         mc.field_71439_g.field_71071_by.field_70461_c = slot;
      }

   }

   private void equipBestWeapon() {
      int bestSlot = -1;
      double maxDamage = 0.0D;

      for(int i = 0; i < 9; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (!stack.field_190928_g) {
            double damage;
            if (stack.func_77973_b() instanceof ItemTool) {
               damage = (double)(((ItemTool)stack.func_77973_b()).field_77865_bY + EnchantmentHelper.func_152377_a(stack, EnumCreatureAttribute.UNDEFINED));
               if (damage > maxDamage) {
                  maxDamage = damage;
                  bestSlot = i;
               }
            } else if (stack.func_77973_b() instanceof ItemSword) {
               damage = (double)(((ItemSword)stack.func_77973_b()).func_150931_i() + EnchantmentHelper.func_152377_a(stack, EnumCreatureAttribute.UNDEFINED));
               if (damage > maxDamage) {
                  maxDamage = damage;
                  bestSlot = i;
               }
            }
         }
      }

      if (bestSlot != -1) {
         this.switchtoslot(bestSlot);
      }

   }
}

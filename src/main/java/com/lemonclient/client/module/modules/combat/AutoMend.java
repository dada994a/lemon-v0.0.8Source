package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.event.Phase;
import com.lemonclient.api.event.events.OnUpdateWalkingPlayerEvent;
import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.PlayerPacket;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.combat.DamageUtil;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.manager.managers.PlayerPacketManager;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec2f;

@Module.Declaration(
   name = "AutoMend",
   category = Category.Combat
)
public class AutoMend extends Module {
   BooleanSetting rotate = this.registerBoolean("Rotate", true);
   BooleanSetting silentSwitch = this.registerBoolean("Packet Switch", true);
   IntegerSetting delay = this.registerInteger("Delay", 0, 0, 1000);
   IntegerSetting minDamage = this.registerInteger("Min Damage", 50, 1, 100);
   IntegerSetting maxHeal = this.registerInteger("Repair To", 90, 1, 100);
   BooleanSetting takeOff = this.registerBoolean("TakeOff", true);
   IntegerSetting takeOffDelay = this.registerInteger("TakeOff Delay", 0, 0, 1000);
   BooleanSetting predict = this.registerBoolean("Predict", true);
   BooleanSetting crystal = this.registerBoolean("Crystal Check", true);
   DoubleSetting biasDamage = this.registerDouble("Bias Damage", 1.0D, 0.0D, 3.0D);
   BooleanSetting health = this.registerBoolean("Health Check", true);
   IntegerSetting minHealth = this.registerInteger("Min Health", 16, 0, 36, () -> {
      return (Boolean)this.health.getValue();
   });
   BooleanSetting player = this.registerBoolean("Enemy Check", true);
   DoubleSetting maxSpeed = this.registerDouble("Max Speed", 10.0D, 0.0D, 50.0D, () -> {
      return (Boolean)this.player.getValue();
   });
   int tookOff;
   Timing timer = new Timing();
   Timing takeOffTimer = new Timing();
   char toMend = 0;
   @EventHandler
   private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener((event) -> {
      if ((Boolean)this.rotate.getValue()) {
         if (event.getPhase() == Phase.PRE) {
            PlayerPacket packet = new PlayerPacket(this, new Vec2f(PlayerPacketManager.INSTANCE.getServerSideRotation().field_189982_i, 90.0F));
            PlayerPacketManager.INSTANCE.addPacket(packet);
         }
      }
   }, new Predicate[0]);
   @EventHandler
   private final Listener<PacketEvent.Send> sendListener = new Listener((event) -> {
      if ((Boolean)this.rotate.getValue()) {
         if (event.getPacket() instanceof Rotation) {
            ((Rotation)event.getPacket()).field_149476_e = PlayerPacketManager.INSTANCE.getServerSideRotation().field_189982_i;
         }

         if (event.getPacket() instanceof PositionRotation) {
            ((PositionRotation)event.getPacket()).field_149476_e = PlayerPacketManager.INSTANCE.getServerSideRotation().field_189982_i;
         }

      }
   }, new Predicate[0]);

   public void onEnable() {
      this.tookOff = 0;
   }

   public void onTick() {
      if (mc.field_71439_g != null && mc.field_71441_e != null && !mc.field_71439_g.field_70128_L && mc.field_71439_g.field_70173_aa >= 10) {
         if ((Boolean)this.crystal.getValue() && this.crystalDamage()) {
            this.setDisabledMessage("Lethal crystal nearby");
            this.disable();
         } else if ((Boolean)this.health.getValue() && mc.field_71439_g.func_110143_aJ() + mc.field_71439_g.func_110139_bj() < (float)(Integer)this.minHealth.getValue()) {
            this.setDisabledMessage("Low health");
            this.disable();
         } else if ((Boolean)this.player.getValue() && this.checkNearbyPlayers()) {
            this.setDisabledMessage("Players nearby");
            this.disable();
         } else if (this.findXPSlot() == -1) {
            this.setDisabledMessage("No xp bottle found in hotbar");
            this.disable();
         } else if (this.checkFinished()) {
            this.setDisabledMessage("Finished mending armors");
            this.disable();
         } else if (this.timer.passedMs((long)(Integer)this.delay.getValue())) {
            this.timer.reset();
            int sumOfDamage = 0;
            List<ItemStack> armour = mc.field_71439_g.field_71071_by.field_70460_b;

            int i;
            for(i = 0; i < armour.size(); ++i) {
               ItemStack itemStack = (ItemStack)armour.get(i);
               if (!itemStack.field_190928_g) {
                  float damageOnArmor = (float)(itemStack.func_77958_k() - itemStack.func_77952_i());
                  float damagePercent = 100.0F - 100.0F * (1.0F - damageOnArmor / (float)itemStack.func_77958_k());
                  if (damagePercent <= (float)(Integer)this.maxHeal.getValue()) {
                     if (damagePercent <= (float)(Integer)this.minDamage.getValue()) {
                        this.toMend |= (char)(1 << i);
                     }

                     if ((Boolean)this.predict.getValue()) {
                        sumOfDamage += (int)((float)(itemStack.func_77958_k() * (Integer)this.maxHeal.getValue()) / 100.0F - (float)(itemStack.func_77958_k() - itemStack.func_77952_i()));
                     }
                  } else {
                     this.toMend &= (char)(~(1 << i));
                  }
               }
            }

            if (this.toMend > 0) {
               if ((Boolean)this.predict.getValue()) {
                  i = mc.field_71441_e.field_72996_f.stream().filter((entity) -> {
                     return entity instanceof EntityXPOrb;
                  }).filter((entity) -> {
                     return entity.func_70068_e(mc.field_71439_g) <= 1.0D;
                  }).mapToInt((entity) -> {
                     return ((EntityXPOrb)entity).field_70530_e;
                  }).sum();
                  if (i * 2 < sumOfDamage) {
                     this.mendArmor();
                  }
               } else {
                  this.mendArmor();
               }
            }

         }
      } else {
         this.disable();
      }
   }

   private void run(int slot, Runnable runnable) {
      int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
      if (slot >= 0 && slot != oldslot) {
         if ((Boolean)this.silentSwitch.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
         } else {
            mc.field_71439_g.field_71071_by.field_70461_c = slot;
         }

         runnable.run();
         if ((Boolean)this.silentSwitch.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(oldslot));
         } else {
            mc.field_71439_g.field_71071_by.field_70461_c = oldslot;
         }

      } else {
         runnable.run();
      }
   }

   private void mendArmor() {
      int newSlot = this.findXPSlot();
      if (newSlot != -1) {
         this.run(newSlot, () -> {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
         });
         if ((Boolean)this.takeOff.getValue()) {
            this.takeArmorOff();
         }

      }
   }

   private void takeArmorOff() {
      for(int slot = 5; slot <= 8; ++slot) {
         ItemStack item = this.getArmor(slot);
         double max_dam = (double)item.func_77958_k();
         double dam_left = (double)(item.func_77958_k() - item.func_77952_i());
         double percent = dam_left / max_dam * 100.0D;
         if (percent >= (double)(Integer)this.maxHeal.getValue() && item.func_77973_b() != Items.field_190931_a) {
            if (!this.notInInv(Items.field_190931_a)) {
               return;
            }

            if (!this.takeOffTimer.passedMs((long)(Integer)this.takeOffDelay.getValue())) {
               return;
            }

            this.takeOffTimer.reset();
            boolean hasEmpty = false;

            int l_l;
            for(l_l = 0; l_l < 36; ++l_l) {
               ItemStack l_Stack = mc.field_71439_g.field_71071_by.func_70301_a(l_l);
               if (l_Stack.field_190928_g) {
                  hasEmpty = true;
                  break;
               }
            }

            if (hasEmpty) {
               mc.field_71442_b.func_187098_a(0, slot, 0, ClickType.QUICK_MOVE, mc.field_71439_g);
            } else {
               for(l_l = 1; l_l < 5; ++l_l) {
                  if (mc.field_71439_g.field_71069_bz.func_75139_a(l_l).func_75211_c().field_190928_g) {
                     mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, slot, 0, ClickType.PICKUP, mc.field_71439_g);
                     mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71069_bz.field_75152_c, l_l, 0, ClickType.PICKUP, mc.field_71439_g);
                  }
               }
            }
         }
      }

   }

   private ItemStack getArmor(int first) {
      return (ItemStack)mc.field_71439_g.field_71069_bz.func_75138_a().get(first);
   }

   public Boolean notInInv(Item itemOfChoice) {
      int n = 0;
      if (itemOfChoice == mc.field_71439_g.func_184592_cb().func_77973_b()) {
         return true;
      } else {
         for(int i = 35; i >= 0; --i) {
            Item item = mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b();
            if (item == itemOfChoice) {
               return true;
            }

            ++n;
         }

         return n <= 35;
      }
   }

   private int findXPSlot() {
      int slot = -1;

      for(int i = 0; i < 9; ++i) {
         if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() == Items.field_151062_by) {
            slot = i;
            break;
         }
      }

      return slot;
   }

   private boolean crystalDamage() {
      Iterator var1 = mc.field_71441_e.field_72996_f.iterator();

      Entity t;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         t = (Entity)var1.next();
      } while(!(t instanceof EntityEnderCrystal) || !(mc.field_71439_g.func_70032_d(t) <= 12.0F) || !((double)DamageUtil.calculateDamage(mc.field_71439_g, (EntityEnderCrystal)t) * (Double)this.biasDamage.getValue() >= (double)mc.field_71439_g.func_110143_aJ()));

      return true;
   }

   private boolean checkNearbyPlayers() {
      AxisAlignedBB box = new AxisAlignedBB(mc.field_71439_g.field_70165_t - 0.5D, mc.field_71439_g.field_70163_u - 0.5D, mc.field_71439_g.field_70161_v - 0.5D, mc.field_71439_g.field_70165_t + 0.5D, mc.field_71439_g.field_70163_u + 2.5D, mc.field_71439_g.field_70161_v + 0.5D);
      Iterator var2 = mc.field_71441_e.field_73010_i.iterator();

      EntityPlayer entity;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         entity = (EntityPlayer)var2.next();
      } while(EntityUtil.basicChecksEntity(entity) || mc.field_71439_g.field_71174_a.func_175104_a(entity.func_70005_c_()) == null || LemonClient.speedUtil.getPlayerSpeed(entity) >= (Double)this.maxSpeed.getValue() || !box.func_72326_a(entity.func_174813_aQ()));

      return true;
   }

   private boolean checkFinished() {
      int finished = 0;

      for(int slot = 5; slot <= 8; ++slot) {
         ItemStack item = this.getArmor(slot);
         if (this.getItemDamage(slot) >= (Integer)this.maxHeal.getValue() || item == ItemStack.field_190927_a) {
            ++finished;
         }
      }

      return finished >= 4;
   }

   private int getItemDamage(int slot) {
      ItemStack itemStack = mc.field_71439_g.field_71069_bz.func_75139_a(slot).func_75211_c();
      float green = ((float)itemStack.func_77958_k() - (float)itemStack.func_77952_i()) / (float)itemStack.func_77958_k();
      float red = 1.0F - green;
      return 100 - (int)(red * 100.0F);
   }
}

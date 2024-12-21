package com.lemonclient.client.module.modules.dev;

import com.lemonclient.api.event.Phase;
import com.lemonclient.api.event.events.EntityRemovedEvent;
import com.lemonclient.api.event.events.OnUpdateWalkingPlayerEvent;
import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.InventoryUtil;
import com.lemonclient.api.util.player.PlayerPacket;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.client.manager.managers.PlayerPacketManager;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayer.PositionRotation;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(
   name = "AutoPot",
   category = Category.Dev,
   priority = 1001
)
public class AutoPot extends Module {
   ModeSetting page = this.registerMode("Page", Arrays.asList("General", "BadPot"), "General");
   BooleanSetting hp = this.registerBoolean("Health Potion", false, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   IntegerSetting health = this.registerInteger("Health", 16, 0, 20, () -> {
      return (Boolean)this.hp.getValue() && ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting equal = this.registerBoolean("Equal", false, () -> {
      return (Boolean)this.hp.getValue() && ((String)this.page.getValue()).equals("General");
   });
   IntegerSetting healthSlot = this.registerInteger("Health Slot", 1, 1, 9, () -> {
      return (Boolean)this.hp.getValue() && ((String)this.page.getValue()).equals("General");
   });
   IntegerSetting hpDelay = this.registerInteger("Health Delay", 50, 0, 1000, () -> {
      return (Boolean)this.hp.getValue() && ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting speed = this.registerBoolean("Swiftness", false, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   IntegerSetting time = this.registerInteger("Time Left", 5, 0, 30, () -> {
      return (Boolean)this.speed.getValue() && ((String)this.page.getValue()).equals("General");
   });
   IntegerSetting swiftnessSlot = this.registerInteger("Swiftness Slot", 1, 1, 9, () -> {
      return (Boolean)this.speed.getValue() && ((String)this.page.getValue()).equals("General");
   });
   IntegerSetting speedDelay = this.registerInteger("Swiftness Delay", 50, 0, 1000, () -> {
      return (Boolean)this.speed.getValue() && ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting only = this.registerBoolean("On GroundOnly", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting silentSwitch = this.registerBoolean("Packet Switch", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   BooleanSetting update = this.registerBoolean("Update Controller", true, () -> {
      return ((String)this.page.getValue()).equals("General");
   });
   IntegerSetting delay = this.registerInteger("Delay (Ticks)", 10, 0, 100, () -> {
      return ((String)this.page.getValue()).equals("BadPot");
   });
   DoubleSetting factor = this.registerDouble("Factor", 0.75D, 0.0D, 1.5D, () -> {
      return ((String)this.page.getValue()).equals("BadPot");
   });
   DoubleSetting range = this.registerDouble("Range", 4.0D, 0.0D, 10.0D, () -> {
      return ((String)this.page.getValue()).equals("BadPot");
   });
   IntegerSetting badSlot = this.registerInteger("BadPot Slot", 1, 1, 9, () -> {
      return ((String)this.page.getValue()).equals("BadPot");
   });
   BooleanSetting weak = this.registerBoolean("Weakness", false, () -> {
      return ((String)this.page.getValue()).equals("BadPot");
   });
   BooleanSetting jump = this.registerBoolean("JumpBoost", false, () -> {
      return ((String)this.page.getValue()).equals("BadPot");
   });
   BooleanSetting poison = this.registerBoolean("Poison", false, () -> {
      return ((String)this.page.getValue()).equals("BadPot");
   });
   BooleanSetting slow = this.registerBoolean("Slowness", false, () -> {
      return ((String)this.page.getValue()).equals("BadPot");
   });
   HashMap<Integer, Long> weaknessTime = new HashMap();
   HashMap<Integer, Long> jumpBoostTime = new HashMap();
   HashMap<Integer, Long> poisonTime = new HashMap();
   HashMap<Integer, Long> slownessTime = new HashMap();
   Timing hpTimer = new Timing();
   Timing speedTimer = new Timing();
   Timing badPotTimer = new Timing();
   int potionSlot;
   int potSlot;
   boolean working = false;
   @EventHandler
   private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         this.working = false;
         if ((Boolean)this.only.getValue() || mc.field_71439_g.func_180799_ab() || mc.field_71439_g.func_70090_H()) {
            boolean inAir = true;
            Vec3d[] var3 = new Vec3d[]{new Vec3d(0.3D, 0.0D, 0.3D), new Vec3d(0.3D, 0.0D, -0.3D), new Vec3d(-0.3D, 0.0D, 0.3D), new Vec3d(-0.3D, 0.0D, -0.3D)};
            int var4 = var3.length;

            int var5;
            Vec3d vec3d;
            BlockPos pos;
            for(var5 = 0; var5 < var4; ++var5) {
               vec3d = var3[var5];
               pos = (new BlockPos(mc.field_71439_g.field_70165_t + vec3d.field_72450_a, mc.field_71439_g.field_70163_u + 0.5D, mc.field_71439_g.field_70161_v + vec3d.field_72449_c)).func_177979_c(3);
               if (!BlockUtil.isAir(pos)) {
                  inAir = false;
                  break;
               }
            }

            if (inAir) {
               var3 = new Vec3d[]{new Vec3d(0.3D, 0.0D, 0.3D), new Vec3d(0.3D, 0.0D, -0.3D), new Vec3d(-0.3D, 0.0D, 0.3D), new Vec3d(-0.3D, 0.0D, -0.3D)};
               var4 = var3.length;

               for(var5 = 0; var5 < var4; ++var5) {
                  vec3d = var3[var5];
                  pos = (new BlockPos(mc.field_71439_g.field_70165_t + vec3d.field_72450_a, mc.field_71439_g.field_70163_u + 0.5D, mc.field_71439_g.field_70161_v + vec3d.field_72449_c)).func_177979_c(2);
                  if (!BlockUtil.isAir(pos)) {
                     inAir = false;
                     break;
                  }
               }
            }

            if (inAir) {
               var3 = new Vec3d[]{new Vec3d(0.3D, 0.0D, 0.3D), new Vec3d(0.3D, 0.0D, -0.3D), new Vec3d(-0.3D, 0.0D, 0.3D), new Vec3d(-0.3D, 0.0D, -0.3D)};
               var4 = var3.length;

               for(var5 = 0; var5 < var4; ++var5) {
                  vec3d = var3[var5];
                  pos = (new BlockPos(mc.field_71439_g.field_70165_t + vec3d.field_72450_a, mc.field_71439_g.field_70163_u + 0.5D, mc.field_71439_g.field_70161_v + vec3d.field_72449_c)).func_177977_b();
                  if (!BlockUtil.isAir(pos)) {
                     inAir = false;
                     break;
                  }
               }
            }

            if (inAir) {
               return;
            }
         }

         if (this.potionSlot == -1) {
            this.potionSlot = this.getPotion();
         }

         if (this.potSlot == -1) {
            this.potSlot = this.getBadPot();
         }

         if (this.potionSlot != -1 || this.potSlot != -1) {
            this.working = true;
            int oldslot;
            if (this.potionSlot > 8) {
               if (mc.field_71462_r instanceof GuiContainer && !(mc.field_71462_r instanceof GuiInventory)) {
                  return;
               }

               oldslot = this.potionSlot == InventoryUtil.getPotion("swiftness") ? (Integer)this.swiftnessSlot.getValue() : (Integer)this.healthSlot.getValue();
               mc.field_71442_b.func_187098_a(0, this.potionSlot, oldslot - 1, ClickType.SWAP, mc.field_71439_g);
               mc.field_71439_g.field_71070_bA.func_75142_b();
               this.potionSlot = oldslot - 1;
            }

            if (this.potSlot > 8) {
               if (mc.field_71462_r instanceof GuiContainer && !(mc.field_71462_r instanceof GuiInventory)) {
                  return;
               }

               mc.field_71442_b.func_187098_a(0, this.potSlot, (Integer)this.badSlot.getValue() - 1, ClickType.SWAP, mc.field_71439_g);
               mc.field_71439_g.field_71070_bA.func_75142_b();
               this.potSlot = (Integer)this.badSlot.getValue() - 1;
            }

            if (event.getPhase() == Phase.PRE) {
               PlayerPacket packet = new PlayerPacket(this, new Vec2f(PlayerPacketManager.INSTANCE.getServerSideRotation().field_189982_i, 90.0F));
               PlayerPacketManager.INSTANCE.addPacket(packet);
            }

            if (event.getPhase() == Phase.POST && (PlayerPacketManager.INSTANCE.getPrevServerSideRotation().field_189983_j > 80.0F || PlayerPacketManager.INSTANCE.getServerSideRotation().field_189983_j > 80.0F)) {
               oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
               boolean switched = false;
               if (this.potionSlot != -1) {
                  if (this.potionSlot != oldslot) {
                     this.switchTo(this.potionSlot);
                     switched = true;
                  }

                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
               }

               if (this.potSlot != -1) {
                  if (this.potSlot != oldslot) {
                     this.switchTo(this.potSlot);
                     switched = true;
                  }

                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
               }

               if (switched) {
                  this.switchTo(oldslot);
               }

               this.potionSlot = this.potSlot = -1;
            }

         }
      }
   }, new Predicate[0]);
   @EventHandler
   private final Listener<PacketEvent.Send> sendListener = new Listener((event) -> {
      if (this.working) {
         if (event.getPacket() instanceof Rotation) {
            ((Rotation)event.getPacket()).field_149473_f = 90.0F;
         }

         if (event.getPacket() instanceof PositionRotation) {
            ((PositionRotation)event.getPacket()).field_149473_f = 90.0F;
         }
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<PacketEvent.PostReceive> receiveListener = new Listener((event) -> {
      if (event.getPacket() instanceof SPacketDestroyEntities) {
         IntStream var10000 = Arrays.stream(((SPacketDestroyEntities)event.getPacket()).func_149098_c());
         HashMap var10001 = this.weaknessTime;
         var10000.forEach(var10001::remove);
         var10000 = Arrays.stream(((SPacketDestroyEntities)event.getPacket()).func_149098_c());
         var10001 = this.jumpBoostTime;
         var10000.forEach(var10001::remove);
         var10000 = Arrays.stream(((SPacketDestroyEntities)event.getPacket()).func_149098_c());
         var10001 = this.poisonTime;
         var10000.forEach(var10001::remove);
         var10000 = Arrays.stream(((SPacketDestroyEntities)event.getPacket()).func_149098_c());
         var10001 = this.slownessTime;
         var10000.forEach(var10001::remove);
      }

      if (event.getPacket() instanceof SPacketEntityStatus && ((SPacketEntityStatus)event.getPacket()).func_149160_c() == 35) {
         this.weaknessTime.remove(((SPacketEntityStatus)event.getPacket()).func_149161_a(mc.field_71441_e).field_145783_c);
         this.jumpBoostTime.remove(((SPacketEntityStatus)event.getPacket()).func_149161_a(mc.field_71441_e).field_145783_c);
         this.poisonTime.remove(((SPacketEntityStatus)event.getPacket()).func_149161_a(mc.field_71441_e).field_145783_c);
         this.slownessTime.remove(((SPacketEntityStatus)event.getPacket()).func_149161_a(mc.field_71441_e).field_145783_c);
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<EntityRemovedEvent> entityRemovedEventListener = new Listener((event) -> {
      if (event.getEntity() instanceof EntityPotion) {
         List<PotionEffect> effectList = PotionUtils.func_185189_a(((EntityPotion)event.getEntity()).func_184543_l());
         PotionEffect weakness = null;
         PotionEffect jumpBoost = null;
         PotionEffect poison = null;
         PotionEffect slowness = null;
         Iterator var7 = effectList.iterator();

         while(var7.hasNext()) {
            PotionEffect effect = (PotionEffect)var7.next();
            if (effect.func_188419_a() == MobEffects.field_76437_t) {
               weakness = effect;
            }

            if (effect.func_188419_a() == MobEffects.field_76430_j) {
               jumpBoost = effect;
            }

            if (effect.func_188419_a() == MobEffects.field_76436_u) {
               poison = effect;
            }

            if (effect.func_188419_a() == MobEffects.field_76421_d) {
               slowness = effect;
            }
         }

         AxisAlignedBB box = event.getEntity().field_70121_D.func_72314_b(4.0D, 2.0D, 4.0D);
         mc.field_71441_e.field_73010_i.stream().filter((p) -> {
            return mc.field_71439_g.field_71174_a.func_175104_a(p.func_70005_c_()) != null;
         }).filter(EntityUtil::isAlive).filter((p) -> {
            return box.func_72326_a(p.field_70121_D);
         }).forEach((p) -> {
            double distanceSq = event.getEntity().func_70068_e(p);
            if (distanceSq < 16.0D) {
               double factor = Math.sqrt(distanceSq) * (Double)this.factor.getValue();
               double duration;
               if (weakness != null) {
                  duration = factor * (double)weakness.func_76459_b();
                  this.weaknessTime.put(p.func_145782_y(), (long)((double)System.currentTimeMillis() + duration * 50.0D));
               }

               if (jumpBoost != null) {
                  duration = factor * (double)jumpBoost.func_76459_b();
                  this.jumpBoostTime.put(p.func_145782_y(), (long)((double)System.currentTimeMillis() + duration * 50.0D));
               }

               if (poison != null) {
                  duration = factor * (double)poison.func_76459_b();
                  this.poisonTime.put(p.func_145782_y(), (long)((double)System.currentTimeMillis() + duration * 50.0D));
               }

               if (slowness != null) {
                  duration = factor * (double)slowness.func_76459_b();
                  this.slownessTime.put(p.func_145782_y(), (long)((double)System.currentTimeMillis() + duration * 50.0D));
               }
            }

         });
      }

   }, new Predicate[0]);

   public void fast() {
      Iterator var1 = mc.field_71441_e.field_73010_i.iterator();

      while(var1.hasNext()) {
         EntityPlayer player = (EntityPlayer)var1.next();
         int id = player.func_145782_y();
         long time = System.currentTimeMillis();
         if (this.weaknessTime.containsKey(id) && (Long)this.weaknessTime.get(id) <= time) {
            this.weaknessTime.remove(id);
         }

         if (this.jumpBoostTime.containsKey(id) && (Long)this.jumpBoostTime.get(id) <= time) {
            this.jumpBoostTime.remove(id);
         }

         if (this.poisonTime.containsKey(id) && (Long)this.poisonTime.get(id) <= time) {
            this.poisonTime.remove(id);
         }

         if (this.slownessTime.containsKey(id) && (Long)this.slownessTime.get(id) <= time) {
            this.slownessTime.remove(id);
         }
      }

   }

   private int getPotion() {
      if ((Boolean)this.hp.getValue() && this.healthCheck() && this.hpTimer.passedMs((long)(Integer)this.hpDelay.getValue())) {
         this.hpTimer.reset();
         int slot = InventoryUtil.getPotion("healing");
         if (slot != -1) {
            return slot;
         }
      }

      if ((Boolean)this.speed.getValue() && (!mc.field_71439_g.func_70644_a(MobEffects.field_76424_c) || ((PotionEffect)Objects.requireNonNull(mc.field_71439_g.func_70660_b(MobEffects.field_76424_c))).func_76459_b() <= (Integer)this.time.getValue() * 20) && this.speedTimer.passedMs((long)(Integer)this.speedDelay.getValue())) {
         this.speedTimer.reset();
         return InventoryUtil.getPotion("swiftness");
      } else {
         return -1;
      }
   }

   private int getBadPot() {
      if (this.badPotTimer.passedTick((double)(Integer)this.delay.getValue())) {
         this.badPotTimer.reset();
         Iterator var1 = mc.field_71441_e.field_73010_i.iterator();

         while(var1.hasNext()) {
            EntityPlayer player = (EntityPlayer)var1.next();
            if (mc.field_71439_g.field_71174_a.func_175104_a(player.func_70005_c_()) != null && !EntityUtil.basicChecksEntity(player) && !((double)mc.field_71439_g.func_70032_d(player) > (Double)this.range.getValue())) {
               int slot;
               if ((Boolean)this.weak.getValue() && !this.weaknessTime.containsKey(player.func_145782_y())) {
                  slot = InventoryUtil.getPotion("weakness");
                  if (slot != -1) {
                     return slot;
                  }
               }

               if ((Boolean)this.jump.getValue() && !this.jumpBoostTime.containsKey(player.func_145782_y())) {
                  slot = InventoryUtil.getPotion("leaping");
                  if (slot != -1) {
                     return slot;
                  }
               }

               if ((Boolean)this.poison.getValue() && !this.poisonTime.containsKey(player.func_145782_y())) {
                  slot = InventoryUtil.getPotion("poison");
                  if (slot != -1) {
                     return slot;
                  }
               }

               if ((Boolean)this.slow.getValue() && !this.slownessTime.containsKey(player.func_145782_y())) {
                  return InventoryUtil.getPotion("slowness");
               }
            }
         }
      }

      return -1;
   }

   private boolean healthCheck() {
      if ((Boolean)this.equal.getValue()) {
         return mc.field_71439_g.func_110143_aJ() <= (float)(Integer)this.health.getValue();
      } else {
         return mc.field_71439_g.func_110143_aJ() < (float)(Integer)this.health.getValue();
      }
   }

   private void switchTo(int slot) {
      if ((Boolean)this.silentSwitch.getValue()) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
      } else {
         mc.field_71439_g.field_71071_by.field_70461_c = slot;
      }

      if ((Boolean)this.update.getValue()) {
         mc.field_71442_b.func_78765_e();
      }

   }
}

package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.misc.Pair;
import com.lemonclient.api.util.player.InventoryUtil;
import com.lemonclient.api.util.player.PlayerPacket;
import com.lemonclient.api.util.player.RotationUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.TimerUtils;
import com.lemonclient.client.manager.managers.PlayerPacketManager;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.network.play.client.CPacketUseEntity.Action;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(
   name = "KillAura",
   category = Category.Combat
)
public class KillAura extends Module {
   ModeSetting itemUsed = this.registerMode("Item used", Arrays.asList("Sword", "All"), "Sword");
   ModeSetting enemyPriority = this.registerMode("Enemy Priority", Arrays.asList("Closest", "Health"), "Closest");
   BooleanSetting silentaura = this.registerBoolean("SilentAura", false);
   BooleanSetting tp = this.registerBoolean("Tp Aura", false);
   BooleanSetting e = this.registerBoolean("SA", false, () -> {
      return (Boolean)this.tp.getValue();
   });
   BooleanSetting strafe = this.registerBoolean("Shift Strafe", true);
   BooleanSetting caCheck = this.registerBoolean("AC Check", false);
   BooleanSetting criticals = this.registerBoolean("Criticals", true);
   BooleanSetting stopspring = this.registerBoolean("Stop Spring", true);
   BooleanSetting rotation = this.registerBoolean("Rotation", true);
   BooleanSetting autoSwitch = this.registerBoolean("Switch", false);
   BooleanSetting swing = this.registerBoolean("Swing", false);
   DoubleSetting switchHealth = this.registerDouble("Min Switch Health", 0.0D, 0.0D, 20.0D);
   DoubleSetting range = this.registerDouble("Range", 5.0D, 0.0D, 25.0D);
   DoubleSetting srange = this.registerDouble("Silent Range", 5.0D, 0.0D, 6.0D);
   private boolean isAttacking = false;
   Entity target;
   public static boolean SA;
   Pair<Float, Integer> newSlot = new Pair(0.0F, -1);
   int temp;
   @EventHandler
   private final Listener<PacketEvent.Send> listener = new Listener((event) -> {
      if (event.getPacket() instanceof CPacketUseEntity && (Boolean)this.criticals.getValue() && ((CPacketUseEntity)event.getPacket()).func_149565_c() == Action.ATTACK && mc.field_71439_g.field_70122_E && this.isAttacking) {
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + 0.10000000149011612D, mc.field_71439_g.field_70161_v, false));
         mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v, false));
      }

   }, new Predicate[0]);

   public void onUpdate() {
      if (!(Boolean)this.silentaura.getValue()) {
         SA = false;
      }

      if (mc.field_71439_g != null && mc.field_71439_g.func_70089_S()) {
         double rangeSq = (Double)this.range.getValue() * (Double)this.range.getValue();
         Optional<EntityPlayer> optionalTarget = mc.field_71441_e.field_73010_i.stream().filter((entity) -> {
            return !EntityUtil.basicChecksEntity(entity);
         }).filter((entity) -> {
            return mc.field_71439_g.func_70068_e(entity) <= rangeSq;
         }).min(Comparator.comparing((e) -> {
            return ((String)this.enemyPriority.getValue()).equals("Closest") ? mc.field_71439_g.func_70068_e(e) : (double)e.func_110143_aJ();
         }));
         boolean sword = ((String)this.itemUsed.getValue()).equalsIgnoreCase("Sword");
         boolean all = ((String)this.itemUsed.getValue()).equalsIgnoreCase("All");
         if (optionalTarget.isPresent()) {
            this.newSlot = this.findSwordSlot();
            this.temp = mc.field_71439_g.field_71071_by.field_70461_c;
            if (!(Boolean)this.silentaura.getValue() && !this.shouldAttack(sword, all)) {
               SA = false;
               mc.field_71439_g.field_71071_by.field_70461_c = this.temp;
            } else {
               if ((Integer)this.newSlot.getValue() == -1) {
                  return;
               }

               if ((Boolean)this.autoSwitch.getValue() && (double)(mc.field_71439_g.func_110143_aJ() + mc.field_71439_g.func_110139_bj()) >= (Double)this.switchHealth.getValue() && (Integer)this.newSlot.getValue() != -1) {
                  mc.field_71439_g.field_71071_by.field_70461_c = (Integer)this.newSlot.getValue();
               }

               this.target = (Entity)optionalTarget.get();
               if ((Boolean)this.rotation.getValue()) {
                  Vec2f rotation = RotationUtil.getRotationTo(this.target.func_174813_aQ());
                  PlayerPacket packet = new PlayerPacket(this, rotation);
                  PlayerPacketManager.INSTANCE.addPacket(packet);
               }

               SA = true;
               this.attack(this.target);
            }
         } else {
            SA = false;
         }

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

   private boolean shouldAttack(boolean sword, boolean all) {
      Item item = mc.field_71439_g.func_184614_ca().func_77973_b();
      return all || sword && item instanceof ItemSword && !(Boolean)this.caCheck.getValue();
   }

   private void attack(Entity e) {
      if (mc.field_71439_g.func_184825_o(0.0F) >= 1.0F) {
         this.isAttacking = true;
         SA = true;
         boolean switched = false;
         if ((Boolean)this.stopspring.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, net.minecraft.network.play.client.CPacketEntityAction.Action.STOP_SPRINTING));
         }

         if ((Boolean)this.silentaura.getValue()) {
            if ((Integer)this.newSlot.getValue() == -1) {
               SA = false;
               return;
            }

            if (mc.field_71439_g.field_71071_by.field_70461_c != (Integer)this.newSlot.getValue()) {
               if (mc.field_71439_g.func_70068_e(e) > (Double)this.srange.getValue() * (Double)this.srange.getValue()) {
                  return;
               }

               switched = true;
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange((Integer)this.newSlot.getValue()));
            }
         }

         double lastX = mc.field_71439_g.field_70165_t;
         double lastY = mc.field_71439_g.field_70163_u;
         double lastZ = mc.field_71439_g.field_70161_v;
         if ((!switched || (Boolean)this.e.getValue()) && (Boolean)this.tp.getValue()) {
            this.tpGoBrrrrr(e.field_70165_t, e.field_70163_u, e.field_70161_v);
         }

         mc.field_71442_b.func_78764_a(mc.field_71439_g, e);
         if ((!switched || (Boolean)this.e.getValue()) && (Boolean)this.tp.getValue()) {
            this.tpGoBrrrrr(lastX, lastY, lastZ);
         }

         if ((Boolean)this.strafe.getValue() && mc.field_71474_y.field_74311_E.func_151470_d()) {
            double r = Math.toRadians((double)mc.field_71439_g.field_70173_aa) * 6.19208751D;
            double dist = Math.sqrt(Math.pow(e.field_70165_t - mc.field_71439_g.field_70165_t, 2.0D) + Math.pow(e.field_70161_v - mc.field_71439_g.field_70161_v, 2.0D));
            double dx = e.field_70165_t + (dist > 8.0D ? 0.0D : Math.sin(r) * 8.0D) - mc.field_71439_g.field_70165_t;
            double dz = e.field_70161_v + (dist > 8.0D ? 0.0D : Math.cos(r) * 8.0D) - mc.field_71439_g.field_70161_v;
            double dy = e.field_70163_u - mc.field_71439_g.field_70163_u;
            double speed = 1.0D;
            mc.field_71439_g.field_70159_w = Math.max(Math.min(dx, speed), -speed);
            mc.field_71439_g.field_70179_y = Math.max(Math.min(dz, speed), -speed);
            TimerUtils.setSpeed(1.2F);
            mc.field_71439_g.field_70181_x = (double)((mc.field_71439_g.field_71158_b.field_78901_c ? 1 : 0) + (mc.field_71439_g.field_71158_b.field_78899_d ? -1 : 0)) + Math.max(Math.min(dy, 1.0D), -1.0D);
         }

         if (switched) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(this.temp));
         }

         if ((Boolean)this.swing.getValue()) {
            mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
         }

         this.isAttacking = false;
      }

   }

   public void tpGoBrrrrr(double x, double y, double z) {
      EntityPlayerSP player = mc.field_71439_g;
      double dist = 5.0D;

      for(int i = 0; i < 20 && dist > 1.0D; ++i) {
         double dx = x - player.field_70165_t;
         double dy = y - player.field_70163_u;
         double dz = z - player.field_70161_v;
         double hdist = Math.sqrt(dx * dx + dz * dz);
         double rx = Math.atan2(dx, dz);
         double ry = Math.atan2(dy, hdist);
         dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
         double o = dist > 1.0D ? 1.0D : dist;
         Vec3d vec = new Vec3d(Math.sin(rx) * Math.cos(ry) * o, o * Math.sin(ry * 1.0D), Math.cos(rx) * Math.cos(ry) * o);
         mc.field_71439_g.func_70091_d(MoverType.SELF, vec.field_72450_a, vec.field_72448_b, vec.field_72449_c);
         vClip2(0.0D, true);
      }

   }

   public static void vClip2(double d, boolean onGround) {
      mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + d, mc.field_71439_g.field_70161_v, onGround));
   }
}

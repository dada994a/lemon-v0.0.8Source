package com.lemonclient.client.module.modules.movement;

import com.lemonclient.api.event.LemonClientEvent;
import com.lemonclient.api.event.events.MotionUpdateEvent;
import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.event.events.PlayerJumpEvent;
import com.lemonclient.api.event.events.PlayerMoveEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.world.MotionUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import java.util.Arrays;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.MovementInput;

@Module.Declaration(
   name = "StrafeBypass",
   category = Category.Movement,
   priority = 999
)
public class StrafeBypass extends Module {
   ModeSetting mode = this.registerMode("Mode", Arrays.asList("Strict", "Normal"), "Normal");
   BooleanSetting boost = this.registerBoolean("DamageBoost", false);
   BooleanSetting randomBoost = this.registerBoolean("RandomBoost", false);
   BooleanSetting debug = this.registerBoolean("Debug", false);
   public Timing rdBoostTimer = new Timing();
   public float boostFactor = 4.0F;
   public long detectionTime;
   public boolean lagDetected;
   public double boostSpeed;
   public int stage = 1;
   private double lastDist;
   private double moveSpeed;
   @EventHandler
   private final Listener<PacketEvent.Receive> receiveListener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if (event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity)event.getPacket()).func_149412_c() == mc.field_71439_g.func_145782_y() && !((SpeedPlus)ModuleManager.getModule(SpeedPlus.class)).isEnabled()) {
            this.boostSpeed = Math.max(Math.hypot((double)((float)((SPacketEntityVelocity)event.getPacket()).field_149415_b / 8000.0F), (double)((float)((SPacketEntityVelocity)event.getPacket()).field_149414_d / 8000.0F)), this.boostSpeed);
         }

         if (event.getPacket() instanceof SPacketPlayerPosLook) {
            this.detectionTime = System.currentTimeMillis();
            this.lagDetected = true;
            this.rdBoostTimer.reset();
            this.boostFactor = 6.0F;
         }

      }
   }, new Predicate[0]);
   @EventHandler
   private final Listener<MotionUpdateEvent> motionUpdateEventListener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if (event.getEra() == LemonClientEvent.Era.PRE) {
            if (System.currentTimeMillis() - this.detectionTime > 3182L) {
               this.lagDetected = false;
            }

            if (event.stage == 1) {
               this.lastDist = Math.sqrt((mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q) * (mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q) + (mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s) * (mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s));
            }

         }
      }
   }, new Predicate[0]);
   @EventHandler
   private final Listener<PlayerJumpEvent> jumpEventListener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if (!mc.field_71439_g.func_70090_H() && !mc.field_71439_g.func_180799_ab()) {
            event.cancel();
         }

      }
   }, new Predicate[0]);
   @EventHandler
   private final Listener<PlayerMoveEvent> moveEventListener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if (!mc.field_71439_g.func_70090_H() && !mc.field_71439_g.func_180799_ab()) {
            if ((double)mc.field_71439_g.field_71158_b.field_192832_b == 0.0D && (double)mc.field_71439_g.field_71158_b.field_78902_a == 0.0D) {
               event.setX(0.0D);
               event.setZ(0.0D);
               event.setSpeed(0.0D);
               return;
            }

            if (mc.field_71439_g.field_70122_E) {
               this.stage = 2;
            }

            label123: {
               switch(this.stage) {
               case 0:
                  ++this.stage;
                  this.lastDist = 0.0D;
                  break label123;
               case 3:
                  this.moveSpeed = this.lastDist - (((String)this.mode.getValue()).equals("Normal") ? 0.6896D : 0.795D) * (this.lastDist - this.getBaseMoveSpeed());
                  break label123;
               }

               if ((!mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.func_174813_aQ().func_72317_d(0.0D, mc.field_71439_g.field_70181_x, 0.0D)).isEmpty() || mc.field_71439_g.field_70124_G) && this.stage > 0) {
                  this.stage = mc.field_71439_g.field_191988_bg == 0.0F && mc.field_71439_g.field_70702_br == 0.0F ? 0 : 1;
               }

               this.moveSpeed = this.lastDist - this.lastDist / 159.0D;
            }

            if ((Boolean)this.boost.getValue() && this.boostSpeed != 0.0D && MotionUtil.moving(mc.field_71439_g)) {
               this.moveSpeed += this.boostSpeed;
               this.boostSpeed = 0.0D;
            }

            if ((Boolean)this.randomBoost.getValue() && this.rdBoostTimer.passedMs(3500L) && !this.lagDetected && MotionUtil.moving(mc.field_71439_g) && mc.field_71439_g.field_70122_E) {
               this.moveSpeed += this.moveSpeed / (double)this.boostFactor;
               if ((Boolean)this.debug.getValue()) {
                  MessageBus.sendClientPrefixMessage("RandomBoost", Notification.Type.INFO);
               }

               this.boostFactor = 4.0F;
               this.rdBoostTimer.reset();
            }

            if (!mc.field_71474_y.field_74314_A.func_151470_d() && mc.field_71439_g.field_70122_E) {
               this.moveSpeed = this.getBaseMoveSpeed();
            } else {
               this.moveSpeed = Math.max(this.moveSpeed, this.getBaseMoveSpeed());
            }

            if ((double)mc.field_71439_g.field_71158_b.field_192832_b != 0.0D && (double)mc.field_71439_g.field_71158_b.field_78902_a != 0.0D) {
               MovementInput var10000 = mc.field_71439_g.field_71158_b;
               var10000.field_192832_b *= (float)Math.sin(0.7853981633974483D);
               var10000 = mc.field_71439_g.field_71158_b;
               var10000.field_78902_a *= (float)Math.cos(0.7853981633974483D);
            }

            event.setX(((double)mc.field_71439_g.field_71158_b.field_192832_b * this.moveSpeed * -Math.sin(Math.toRadians((double)mc.field_71439_g.field_70177_z)) + (double)mc.field_71439_g.field_71158_b.field_78902_a * this.moveSpeed * Math.cos(Math.toRadians((double)mc.field_71439_g.field_70177_z))) * (((String)this.mode.getValue()).equals("Normal") ? 0.993D : 0.99D));
            event.setZ(((double)mc.field_71439_g.field_71158_b.field_192832_b * this.moveSpeed * Math.cos(Math.toRadians((double)mc.field_71439_g.field_70177_z)) - (double)mc.field_71439_g.field_71158_b.field_78902_a * this.moveSpeed * -Math.sin(Math.toRadians((double)mc.field_71439_g.field_70177_z))) * (((String)this.mode.getValue()).equals("Normal") ? 0.993D : 0.99D));
            ++this.stage;
         }

      }
   }, new Predicate[0]);

   public double getBaseMoveSpeed() {
      double result = 0.2873D;
      if (mc.field_71439_g.func_70660_b(MobEffects.field_76424_c) != null) {
         result += 0.2873D * ((double)mc.field_71439_g.func_70660_b(MobEffects.field_76424_c).func_76458_c() + 1.0D) * 0.2D;
      }

      if (mc.field_71439_g.func_70660_b(MobEffects.field_76421_d) != null) {
         result -= 0.2873D * ((double)mc.field_71439_g.func_70660_b(MobEffects.field_76421_d).func_76458_c() + 1.0D) * 0.15D;
      }

      return result;
   }
}

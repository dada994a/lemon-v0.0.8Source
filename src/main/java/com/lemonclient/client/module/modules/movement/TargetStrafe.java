package com.lemonclient.client.module.modules.movement;

import com.lemonclient.api.event.events.MotionUpdateEvent;
import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.event.events.PlayerMoveEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.RotationUtil;
import com.lemonclient.api.util.world.MotionUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(
   name = "TargetStrafe",
   category = Category.Movement
)
public class TargetStrafe extends Module {
   IntegerSetting range = this.registerInteger("TargetRange", 20, 0, 256);
   BooleanSetting jump = this.registerBoolean("Jump", true);
   BooleanSetting antiStuck = this.registerBoolean("AntiStuck", true);
   DoubleSetting distanceSetting = this.registerDouble("PreferredDistance", 1.0D, 0.0D, 10.0D);
   DoubleSetting maxDistance = this.registerDouble("MaxDistance", 10.0D, 1.0D, 32.0D);
   DoubleSetting turnAmount = this.registerDouble("TurnAmount", 5.0D, 1.0D, 90.0D);
   String pattern = "%.1f";
   Timing lagBackCoolDown = new Timing();
   Timing boostTimer = new Timing();
   long detectionTime;
   boolean checkCoolDown = false;
   double boostSpeed;
   double boostSpeed2;
   double lastDist;
   int level = 1;
   double moveSpeed;
   @EventHandler
   private final Listener<PacketEvent.Receive> receiveListener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if (event.getPacket() instanceof SPacketPlayerPosLook) {
            this.lastDist = 0.0D;
            this.moveSpeed = Math.min(this.getBaseMoveSpeed(), this.getBaseMoveSpeed());
            this.detectionTime = System.currentTimeMillis();
            if (!this.checkCoolDown) {
               this.lagBackCoolDown.reset();
               this.checkCoolDown = true;
            }
         }

         if (event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity)event.getPacket()).func_149412_c() == mc.field_71439_g.func_145782_y()) {
            this.boostSpeed = Math.hypot((double)((float)((SPacketEntityVelocity)event.getPacket()).field_149415_b / 8000.0F), (double)((float)((SPacketEntityVelocity)event.getPacket()).field_149414_d / 8000.0F));
            this.boostSpeed2 = this.boostSpeed;
         }

      }
   }, new Predicate[0]);
   @EventHandler
   private final Listener<MotionUpdateEvent> motionUpdateEventListener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if (!((HoleSnap)ModuleManager.getModule(HoleSnap.class)).isEnabled()) {
            try {
               if (this.lagBackCoolDown.passedMs((long)Double.parseDouble(String.format(this.pattern, 1000.0D)))) {
                  this.checkCoolDown = false;
                  this.lagBackCoolDown.reset();
               }

               if (event.stage == 1) {
                  this.lastDist = Math.sqrt((mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q) * (mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q) + (mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s) * (mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s));
               }
            } catch (NumberFormatException var3) {
            }

         }
      }
   }, new Predicate[0]);
   @EventHandler
   private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener((event) -> {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         EntityPlayer target = PlayerUtil.getNearestPlayer((double)(Integer)this.range.getValue());
         if (target != null) {
            if (mc.field_71439_g.func_180799_ab() || mc.field_71439_g.func_70090_H() || mc.field_71439_g.field_70134_J) {
               return;
            }

            if (mc.field_71439_g.field_70122_E) {
               this.level = 2;
            }

            if (round(mc.field_71439_g.field_70163_u - (double)((int)mc.field_71439_g.field_70163_u), 3) == round(0.138D, 3) && (Boolean)this.jump.getValue()) {
               EntityPlayerSP player = mc.field_71439_g;
               player.field_70181_x -= 0.07D;
               event.setY(event.getY() - 0.08316090325960147D);
               EntityPlayerSP player2 = mc.field_71439_g;
               player2.field_70163_u -= 0.08316090325960147D;
            }

            if (this.level != 1 || mc.field_71439_g.field_191988_bg == 0.0F && mc.field_71439_g.field_70702_br == 0.0F) {
               if (this.level == 2) {
                  this.level = 3;
                  if (MotionUtil.moving(mc.field_71439_g)) {
                     if (!mc.field_71439_g.func_180799_ab() && mc.field_71439_g.field_70122_E && (Boolean)this.jump.getValue()) {
                        event.setY(mc.field_71439_g.field_70181_x = 0.4D);
                     }

                     this.moveSpeed *= 1.433D;
                  }
               } else if (this.level == 3) {
                  this.level = 4;
                  this.moveSpeed = this.lastDist - 0.6553D * (this.lastDist - this.getBaseMoveSpeed() + 0.04D);
               } else {
                  if (mc.field_71439_g.field_70122_E && (mc.field_71441_e.func_184144_a(mc.field_71439_g, mc.field_71439_g.field_70121_D.func_72317_d(0.0D, mc.field_71439_g.field_70181_x, 0.0D)).size() > 0 || mc.field_71439_g.field_70124_G)) {
                     this.level = 1;
                  }

                  this.moveSpeed = this.lastDist - this.lastDist / 201.0D;
               }
            } else {
               this.level = 2;
               this.moveSpeed = 1.418D * this.getBaseMoveSpeed();
            }

            if (MotionUtil.moving(mc.field_71439_g) && this.boostSpeed2 != 0.0D) {
               if (this.boostTimer.passedMs(1L)) {
                  this.moveSpeed = this.boostSpeed2;
                  this.boostTimer.reset();
               }

               this.boostSpeed2 = 0.0D;
            }

            this.moveSpeed = Math.max(this.moveSpeed, this.getBaseMoveSpeed());
            if (mc.field_71439_g.field_70123_F && (Boolean)this.antiStuck.getValue()) {
               this.switchDirection();
            }

            this.doStrafeAtSpeed(event, RotationUtil.getRotationTo(target.func_174791_d()).field_189982_i, target.func_174791_d());
         }

      }
   }, -100, new Predicate[0]);
   int direction = 1;

   public static double round(double n, int n2) {
      if (n2 < 0) {
         throw new IllegalArgumentException();
      } else {
         return (new BigDecimal(n)).setScale(n2, RoundingMode.HALF_UP).doubleValue();
      }
   }

   private void switchDirection() {
      this.direction = -this.direction;
   }

   private void doStrafeAtSpeed(PlayerMoveEvent event, float rotation, Vec3d target) {
      float rotationYaw = rotation + 90.0F * (float)this.direction;
      double disX = mc.field_71439_g.field_70165_t - target.field_72450_a;
      double disZ = mc.field_71439_g.field_70161_v - target.field_72449_c;
      double distance = Math.sqrt(disX * disX + disZ * disZ);
      if (distance < (Double)this.maxDistance.getValue()) {
         if (distance > (Double)this.distanceSetting.getValue()) {
            rotationYaw = (float)((double)rotationYaw - (Double)this.turnAmount.getValue() * (double)this.direction);
         } else if (distance < (Double)this.distanceSetting.getValue()) {
            rotationYaw = (float)((double)rotationYaw + (Double)this.turnAmount.getValue() * (double)this.direction);
         }
      } else {
         rotationYaw = rotation;
      }

      if ((Boolean)this.jump.getValue() && mc.field_71439_g.field_70122_E) {
         mc.field_71439_g.func_70664_aZ();
      }

      event.setX(this.moveSpeed * Math.cos(Math.toRadians((double)(rotationYaw + 90.0F))));
      event.setZ(this.moveSpeed * Math.sin(Math.toRadians((double)(rotationYaw + 90.0F))));
   }

   public double getBaseMoveSpeed() {
      double n = 0.2873D;
      if (mc.field_71439_g.func_70644_a(MobEffects.field_76424_c)) {
         n *= 1.0D + 0.2D * (double)(((PotionEffect)Objects.requireNonNull(mc.field_71439_g.func_70660_b(MobEffects.field_76424_c))).func_76458_c() + 1);
      }

      return n;
   }
}

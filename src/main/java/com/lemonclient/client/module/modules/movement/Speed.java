package com.lemonclient.client.module.modules.movement;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.event.events.PlayerMoveEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.misc.Timer;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.MotionUtil;
import com.lemonclient.api.util.world.TimerUtils;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.potion.PotionEffect;

@Module.Declaration(
   name = "Speed",
   category = Category.Movement
)
public class Speed extends Module {
   private final Timer timer = new Timer();
   public int yl;
   ModeSetting mode = this.registerMode("Mode", Arrays.asList("Strafe", "GroundStrafe", "OnGround", "Fake", "YPort"), "Strafe");
   DoubleSetting speed = this.registerDouble("Speed", 2.0D, 0.0D, 10.0D, () -> {
      return ((String)this.mode.getValue()).equals("Strafe") || ((String)this.mode.getValue()).equalsIgnoreCase("Beta");
   });
   BooleanSetting jump = this.registerBoolean("Jump", true, () -> {
      return ((String)this.mode.getValue()).equals("Strafe") || ((String)this.mode.getValue()).equalsIgnoreCase("Beta");
   });
   BooleanSetting boost = this.registerBoolean("Boost", false, () -> {
      return ((String)this.mode.getValue()).equalsIgnoreCase("Strafe") || ((String)this.mode.getValue()).equalsIgnoreCase("Beta");
   });
   DoubleSetting multiply = this.registerDouble("Multiply", 0.8D, 0.1D, 1.0D, () -> {
      return (Boolean)this.boost.getValue() && this.boost.isVisible();
   });
   DoubleSetting max = this.registerDouble("Maximum", 0.5D, 0.0D, 1.0D, () -> {
      return (Boolean)this.boost.getValue() && this.boost.isVisible();
   });
   DoubleSetting gSpeed = this.registerDouble("Ground Speed", 0.3D, 0.0D, 0.5D, () -> {
      return ((String)this.mode.getValue()).equals("GroundStrafe");
   });
   DoubleSetting yPortSpeed = this.registerDouble("Speed YPort", 0.06D, 0.01D, 0.15D, () -> {
      return ((String)this.mode.getValue()).equals("YPort");
   });
   DoubleSetting onGroundSpeed = this.registerDouble("Speed OnGround", 1.5D, 0.01D, 3.0D, () -> {
      return ((String)this.mode.getValue()).equalsIgnoreCase("OnGround");
   });
   BooleanSetting strictOG = this.registerBoolean("Head Block Only", false, () -> {
      return ((String)this.mode.getValue()).equalsIgnoreCase("OnGround");
   });
   DoubleSetting jumpHeight = this.registerDouble("Jump Speed", 0.41D, 0.0D, 1.0D, () -> {
      return ((String)this.mode.getValue()).equalsIgnoreCase("Strafe") && (Boolean)this.jump.getValue() || ((String)this.mode.getValue()).equalsIgnoreCase("Beta") && (Boolean)this.jump.getValue();
   });
   IntegerSetting jumpDelay = this.registerInteger("Jump Delay", 300, 0, 1000, () -> {
      return ((String)this.mode.getValue()).equalsIgnoreCase("Strafe") && (Boolean)this.jump.getValue() || ((String)this.mode.getValue()).equalsIgnoreCase("Beta") && (Boolean)this.jump.getValue();
   });
   BooleanSetting useTimer = this.registerBoolean("Timer", false);
   DoubleSetting timerVal = this.registerDouble("Timer Speed", 1.088D, 0.8D, 1.2D);
   private boolean slowDown;
   private double playerSpeed;
   private double velocity;
   Timer kbTimer = new Timer();
   @EventHandler
   private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener((event) -> {
      if (!mc.field_71439_g.func_180799_ab() && !mc.field_71439_g.func_70090_H() && !mc.field_71439_g.func_70617_f_() && !mc.field_71439_g.field_70134_J) {
         double offset;
         if (((String)this.mode.getValue()).equalsIgnoreCase("Strafe")) {
            offset = (Double)this.jumpHeight.getValue();
            if (mc.field_71439_g.field_70122_E && (Boolean)this.jump.getValue()) {
               mc.field_71439_g.func_70664_aZ();
            }

            if (mc.field_71439_g.field_70122_E && MotionUtil.moving(mc.field_71439_g) && this.timer.hasReached((long)(Integer)this.jumpDelay.getValue())) {
               if (mc.field_71439_g.func_70644_a(MobEffects.field_76430_j)) {
                  offset += (double)((float)(((PotionEffect)Objects.requireNonNull(mc.field_71439_g.func_70660_b(MobEffects.field_76430_j))).func_76458_c() + 1) * 0.1F);
               }

               if ((Boolean)this.jump.getValue()) {
                  event.setY(mc.field_71439_g.field_70181_x = offset);
               }

               this.playerSpeed = MotionUtil.getBaseMoveSpeed() * (EntityUtil.isColliding(0.0D, -0.5D, 0.0D) instanceof BlockLiquid && !EntityUtil.isInLiquid() ? 0.91D : (Double)this.speed.getValue());
               this.slowDown = true;
               this.timer.reset();
            } else if (!this.slowDown && !mc.field_71439_g.field_70123_F) {
               this.playerSpeed -= this.playerSpeed / 159.0D;
            } else {
               this.playerSpeed -= EntityUtil.isColliding(0.0D, -0.8D, 0.0D) instanceof BlockLiquid && !EntityUtil.isInLiquid() ? 0.4D : 0.7D * MotionUtil.getBaseMoveSpeed();
               this.slowDown = false;
            }

            this.playerSpeed = Math.max(this.playerSpeed, MotionUtil.getBaseMoveSpeed());
            if ((Boolean)this.boost.getValue() && !this.kbTimer.hasReached(50L)) {
               this.playerSpeed += Math.min(this.velocity * (Double)this.multiply.getValue(), (Double)this.max.getValue());
            }

            double[] dir = MotionUtil.forward(this.playerSpeed);
            event.setX(dir[0]);
            event.setZ(dir[1]);
         }

         if (((String)this.mode.getValue()).equalsIgnoreCase("GroundStrafe")) {
            this.playerSpeed = (Double)this.gSpeed.getValue();
            this.playerSpeed *= MotionUtil.getBaseMoveSpeed() / 0.2873D;
            if (mc.field_71439_g.field_70122_E) {
               double[] dirx = MotionUtil.forward(this.playerSpeed);
               event.setX(dirx[0]);
               event.setZ(dirx[1]);
            }
         } else if (((String)this.mode.getValue()).equalsIgnoreCase("OnGround")) {
            if (mc.field_71439_g.field_70123_F) {
               return;
            }

            offset = 0.4D;
            if (mc.field_71441_e.func_184143_b(mc.field_71439_g.field_70121_D.func_72317_d(0.0D, 0.4D, 0.0D))) {
               offset = 2.0D - mc.field_71439_g.field_70121_D.field_72337_e;
            } else if ((Boolean)this.strictOG.getValue()) {
               return;
            }

            EntityPlayerSP var10000 = mc.field_71439_g;
            var10000.field_70163_u -= offset;
            mc.field_71439_g.field_70181_x = -1000.0D;
            mc.field_71439_g.field_70726_aT = 0.3F;
            mc.field_71439_g.field_70140_Q = 44.0F;
            if (mc.field_71439_g.field_70122_E) {
               var10000 = mc.field_71439_g;
               var10000.field_70163_u += offset;
               mc.field_71439_g.field_70181_x = offset;
               mc.field_71439_g.field_82151_R = 44.0F;
               var10000 = mc.field_71439_g;
               var10000.field_70159_w *= (Double)this.onGroundSpeed.getValue();
               var10000 = mc.field_71439_g;
               var10000.field_70179_y *= (Double)this.onGroundSpeed.getValue();
               mc.field_71439_g.field_70726_aT = 0.0F;
            }
         }

      }
   }, new Predicate[0]);
   @EventHandler
   private final Listener<PacketEvent.Receive> receiveListener = new Listener((event) -> {
      if (event.getPacket() instanceof SPacketExplosion) {
         this.velocity = (double)(Math.abs(((SPacketExplosion)event.getPacket()).field_149152_f) + Math.abs(((SPacketExplosion)event.getPacket()).field_149159_h));
         this.kbTimer.reset();
      }

      if (event.getPacket() instanceof SPacketEntityVelocity) {
         if (((SPacketEntityVelocity)event.getPacket()).func_149412_c() != mc.field_71439_g.func_145782_y()) {
            return;
         }

         if (this.velocity < (double)(Math.abs(((SPacketEntityVelocity)event.getPacket()).field_149415_b) + Math.abs(((SPacketEntityVelocity)event.getPacket()).field_149414_d))) {
            this.velocity = (double)(Math.abs(((SPacketEntityVelocity)event.getPacket()).field_149415_b) + Math.abs(((SPacketEntityVelocity)event.getPacket()).field_149414_d));
            this.kbTimer.reset();
         }
      }

   }, new Predicate[0]);

   public void onEnable() {
      this.playerSpeed = MotionUtil.getBaseMoveSpeed();
      this.yl = (int)mc.field_71439_g.field_70163_u;
   }

   public void onDisable() {
      this.timer.reset();
   }

   public void onUpdate() {
      if (mc.field_71439_g != null && mc.field_71441_e != null) {
         if (((String)this.mode.getValue()).equalsIgnoreCase("YPort")) {
            this.handleYPortSpeed();
         }

         if ((Boolean)this.useTimer.getValue()) {
            TimerUtils.setTickLength(50.0F / ((Double)this.timerVal.getValue()).floatValue());
         }

      } else {
         this.disable();
      }
   }

   private void handleYPortSpeed() {
      if (MotionUtil.moving(mc.field_71439_g) && (!mc.field_71439_g.func_70090_H() || !mc.field_71439_g.func_180799_ab()) && !mc.field_71439_g.field_70123_F) {
         if (mc.field_71439_g.field_70122_E) {
            mc.field_71439_g.func_70664_aZ();
            MotionUtil.setSpeed(mc.field_71439_g, MotionUtil.getBaseMoveSpeed() + (Double)this.yPortSpeed.getValue());
         } else {
            mc.field_71439_g.field_70181_x = -1.0D;
         }

      }
   }

   public String getHudInfo() {
      return "[" + ChatFormatting.WHITE + (String)this.mode.getValue() + ChatFormatting.GRAY + "]";
   }
}

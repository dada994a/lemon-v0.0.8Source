package com.lemonclient.client.module.modules.movement;

import com.lemonclient.api.event.events.PacketEvent;
import com.lemonclient.api.event.events.PlayerMoveEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.MotionUtil;
import com.lemonclient.api.util.world.TimerUtils;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(
   name = "LiquidSpeed",
   category = Category.Movement
)
public class LiquidSpeed extends Module {
   DoubleSetting timerVal = this.registerDouble("Timer Speed", 1.0D, 1.0D, 2.0D);
   DoubleSetting XZWater = this.registerDouble("XZ Water", 5.75D, 0.01D, 8.0D);
   DoubleSetting upWater = this.registerDouble("Y+ Water", 2.69D, 0.01D, 8.0D);
   DoubleSetting downWater = this.registerDouble("Y- Water", 0.8D, 0.01D, 8.0D);
   DoubleSetting XZBoostWater = this.registerDouble("XZ Boost Water", 6.0D, 1.0D, 8.0D);
   DoubleSetting yBoostWater = this.registerDouble("Y Boost Water", 2.9D, 0.1D, 8.0D);
   DoubleSetting XZLava = this.registerDouble("XZ Lava", 3.8D, 0.01D, 8.0D);
   DoubleSetting upLava = this.registerDouble("Y+ Lava", 2.69D, 0.01D, 8.0D);
   DoubleSetting downLava = this.registerDouble("Y- Lava", 4.22D, 0.01D, 8.0D);
   DoubleSetting XZBoostLava = this.registerDouble("XZ Boost Lava", 4.0D, 1.0D, 8.0D);
   DoubleSetting yBoostLava = this.registerDouble("Y Boost Lava", 2.0D, 0.1D, 8.0D);
   DoubleSetting jitter = this.registerDouble("Jitter", 1.0D, 1.0D, 20.0D);
   BooleanSetting groundIgnore = this.registerBoolean("Ground Ignore", true);
   Vec3d[] sides = new Vec3d[]{new Vec3d(0.3D, 0.0D, 0.3D), new Vec3d(0.3D, 0.0D, -0.3D), new Vec3d(-0.3D, 0.0D, 0.3D), new Vec3d(-0.3D, 0.0D, -0.3D)};
   double moveSpeed = 0.0D;
   double motionY = 0.0D;
   @EventHandler
   private final Listener<PacketEvent.Receive> receiveListener = new Listener((event) -> {
      if (event.getPacket() instanceof SPacketPlayerPosLook) {
         this.reset();
      }

   }, new Predicate[0]);
   @EventHandler
   private final Listener<PlayerMoveEvent> playerMoveEventListener = new Listener((event) -> {
      if (mc.field_71439_g != null && mc.field_71441_e != null) {
         if (mc.field_71439_g.func_70090_H() || mc.field_71439_g.func_180799_ab()) {
            if (!(Boolean)this.groundIgnore.getValue() && mc.field_71439_g.field_70122_E) {
               this.stopMotion(event);
               this.reset();
            } else if (mc.field_71439_g.func_70090_H()) {
               this.waterSwim(event);
            } else if (mc.field_71439_g.func_180799_ab()) {
               this.lavaSwim(event);
            } else {
               this.reset();
            }

         }
      }
   }, new Predicate[0]);

   public void onDisable() {
      this.reset();
   }

   private boolean intersect(BlockPos pos) {
      return mc.field_71439_g.field_70121_D.func_72326_a(mc.field_71441_e.func_180495_p(pos).func_185918_c(mc.field_71441_e, pos));
   }

   private boolean inLiquid(Material material) {
      Vec3d vec = mc.field_71439_g.func_174791_d();
      Vec3d[] var3 = this.sides;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Vec3d side = var3[var5];
         BlockPos blockPos = new BlockPos(vec.func_178787_e(side));
         if (this.intersect(blockPos)) {
            IBlockState blockState = BlockUtil.getState(blockPos);
            if (!(blockState instanceof BlockLiquid)) {
               return false;
            }

            if (((BlockLiquid)blockState).field_149764_J != material) {
               return false;
            }
         }
      }

      return true;
   }

   private void lavaSwim(PlayerMoveEvent moveEvent) {
      this.ySwim(moveEvent, (Double)this.yBoostLava.getValue(), (Double)this.upLava.getValue(), (Double)this.downLava.getValue());
      boolean jump = mc.field_71439_g.field_71158_b.field_78901_c;
      boolean sneak = mc.field_71439_g.field_71158_b.field_78899_d;
      if (jump && sneak || !jump && !sneak) {
         TimerUtils.setTimerSpeed(1.0F);
      } else {
         TimerUtils.setTimerSpeed(((Double)this.timerVal.getValue()).floatValue());
      }

      if (mc.field_71439_g.field_71158_b.field_192832_b == 0.0F && mc.field_71439_g.field_71158_b.field_78902_a == 0.0F) {
         this.stopMotion(moveEvent);
      } else {
         double yaw = MotionUtil.calcMoveYaw();
         this.moveSpeed = Math.min(Math.max(this.moveSpeed * (Double)this.XZBoostLava.getValue(), 0.05D), (Double)this.XZLava.getValue() / 20.0D);
         moveEvent.setX(-Math.sin(yaw) * this.moveSpeed);
         moveEvent.setZ(Math.cos(yaw) * this.moveSpeed);
      }

   }

   private void waterSwim(PlayerMoveEvent moveEvent) {
      this.ySwim(moveEvent, (Double)this.yBoostWater.getValue(), (Double)this.upWater.getValue(), (Double)this.downWater.getValue() * 20.0D);
      boolean jump = mc.field_71439_g.field_71158_b.field_78901_c;
      boolean sneak = mc.field_71439_g.field_71158_b.field_78899_d;
      if (jump && sneak || !jump && !sneak) {
         TimerUtils.setTimerSpeed(1.0F);
      } else {
         TimerUtils.setTimerSpeed(((Double)this.timerVal.getValue()).floatValue());
      }

      if (mc.field_71439_g.field_71158_b.field_192832_b == 0.0F && mc.field_71439_g.field_71158_b.field_78902_a == 0.0F) {
         this.stopMotion(moveEvent);
      } else {
         double yaw = MotionUtil.calcMoveYaw();
         double multiplier = this.applySpeedPotionEffects();
         this.moveSpeed = Math.min(Math.max(this.moveSpeed * (Double)this.XZBoostWater.getValue(), 0.075D), (Double)this.XZWater.getValue() / 20.0D);
         if (mc.field_71439_g.field_71158_b.field_78899_d && !mc.field_71439_g.field_71158_b.field_78901_c) {
            double downMotion = mc.field_71439_g.field_70181_x * 0.25D;
            this.moveSpeed = Math.min(this.moveSpeed, Math.max(this.moveSpeed + downMotion, 0.0D));
         }

         this.moveSpeed *= multiplier;
         moveEvent.setX(-Math.sin(yaw) * this.moveSpeed);
         moveEvent.setZ(Math.cos(yaw) * this.moveSpeed);
      }

   }

   private double applySpeedPotionEffects() {
      double result = 1.0D;
      if (mc.field_71439_g.func_70660_b(MobEffects.field_76424_c) != null) {
         result += ((double)mc.field_71439_g.func_70660_b(MobEffects.field_76424_c).func_76458_c() + 1.0D) * 0.2D;
      }

      if (mc.field_71439_g.func_70660_b(MobEffects.field_76421_d) != null) {
         result -= ((double)mc.field_71439_g.func_70660_b(MobEffects.field_76421_d).func_76458_c() + 1.0D) * 0.15D;
      }

      return result;
   }

   private void ySwim(PlayerMoveEvent moveEvent, double vBoost, double upSpeed, double downSpeed) {
      boolean jump = mc.field_71439_g.field_71158_b.field_78901_c;
      boolean sneak = mc.field_71439_g.field_71158_b.field_78899_d;
      this.motionY = Math.pow(0.1D, (Double)this.jitter.getValue());
      if (!jump || !sneak) {
         if (jump) {
            this.motionY = Math.min(this.motionY + vBoost / 20.0D, upSpeed / 20.0D);
         }

         if (sneak) {
            this.motionY = Math.max(this.motionY - vBoost / 20.0D, -downSpeed / 20.0D);
         }
      }

      moveEvent.setY(this.motionY);
   }

   private void stopMotion(PlayerMoveEvent event) {
      event.setX(0.0D);
      event.setZ(0.0D);
      this.moveSpeed = 0.0D;
   }

   private void reset() {
      this.moveSpeed = 0.0D;
      this.motionY = 0.0D;
   }
}

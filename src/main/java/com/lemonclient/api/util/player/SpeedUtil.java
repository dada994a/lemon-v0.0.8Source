package com.lemonclient.api.util.player;

import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SpeedUtil {
   static Minecraft mc = Minecraft.func_71410_x();
   public static final double LAST_JUMP_INFO_DURATION_DEFAULT = 3.0D;
   public static boolean didJumpThisTick = false;
   public static boolean isJumping = false;
   public double firstJumpSpeed = 0.0D;
   public double lastJumpSpeed = 0.0D;
   public double percentJumpSpeedChanged = 0.0D;
   public double jumpSpeedChanged = 0.0D;
   public boolean didJumpLastTick = false;
   public long jumpInfoStartTime = 0L;
   public boolean wasFirstJump = true;
   public double speedometerCurrentSpeed = 0.0D;
   public HashMap<EntityPlayer, SpeedUtil.Info> playerInfo = new HashMap();

   public static void setDidJumpThisTick(boolean val) {
      didJumpThisTick = val;
   }

   public static void setIsJumping(boolean val) {
      isJumping = val;
   }

   public float lastJumpInfoTimeRemaining() {
      return (float)(Minecraft.func_71386_F() - this.jumpInfoStartTime) / 1000.0F;
   }

   public void update() {
      double distTraveledLastTickX = mc.field_71439_g.field_70165_t - mc.field_71439_g.field_70169_q;
      double distTraveledLastTickZ = mc.field_71439_g.field_70161_v - mc.field_71439_g.field_70166_s;
      this.speedometerCurrentSpeed = distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ;
      if (didJumpThisTick && (!mc.field_71439_g.field_70122_E || isJumping)) {
         if (!this.didJumpLastTick) {
            this.wasFirstJump = this.lastJumpSpeed == 0.0D;
            this.percentJumpSpeedChanged = this.speedometerCurrentSpeed != 0.0D ? this.speedometerCurrentSpeed / this.lastJumpSpeed - 1.0D : -1.0D;
            this.jumpSpeedChanged = this.speedometerCurrentSpeed - this.lastJumpSpeed;
            this.jumpInfoStartTime = Minecraft.func_71386_F();
            this.lastJumpSpeed = this.speedometerCurrentSpeed;
            this.firstJumpSpeed = this.wasFirstJump ? this.lastJumpSpeed : 0.0D;
         }

         this.didJumpLastTick = didJumpThisTick;
      } else {
         this.didJumpLastTick = false;
         this.lastJumpSpeed = 0.0D;
      }

      this.updatePlayers();
   }

   public void updatePlayers() {
      Iterator var1 = mc.field_71441_e.field_73010_i.iterator();

      while(var1.hasNext()) {
         EntityPlayer player = (EntityPlayer)var1.next();
         int distance = 20;
         if (mc.field_71439_g.func_70068_e(player) < (double)(distance * distance)) {
            Vec3d lastPos = null;
            if (this.playerInfo.get(player) != null) {
               SpeedUtil.Info info = (SpeedUtil.Info)this.playerInfo.get(player);
               lastPos = info.pos;
            }

            this.playerInfo.put(player, new SpeedUtil.Info(player, lastPos));
         }
      }

   }

   public double getPlayerSpeed(EntityPlayer player) {
      if (player == null) {
         return 0.0D;
      } else {
         return this.playerInfo.get(player) == null ? 0.0D : this.turnIntoKpH(((SpeedUtil.Info)this.playerInfo.get(player)).speed);
      }
   }

   public Vec3d getPlayerLastPos(EntityPlayer player) {
      if (player == null) {
         return null;
      } else {
         return this.playerInfo.get(player) == null ? null : ((SpeedUtil.Info)this.playerInfo.get(player)).lastPos;
      }
   }

   public double getPlayerMoveYaw(EntityPlayer player) {
      if (player == null) {
         return 0.0D;
      } else {
         return this.playerInfo.get(player) == null ? 0.0D : ((SpeedUtil.Info)this.playerInfo.get(player)).yaw;
      }
   }

   public double turnIntoKpH(double input) {
      return (double)MathHelper.func_76133_a(input) * 71.2729367892D;
   }

   public double getSpeedKpH() {
      double speedometerkphdouble = this.turnIntoKpH(this.speedometerCurrentSpeed);
      speedometerkphdouble = (double)Math.round(10.0D * speedometerkphdouble) / 10.0D;
      return speedometerkphdouble;
   }

   public double getSpeedMpS() {
      double speedometerMpsdouble = this.turnIntoKpH(this.speedometerCurrentSpeed) / 3.6D;
      speedometerMpsdouble = (double)Math.round(10.0D * speedometerMpsdouble) / 10.0D;
      return speedometerMpsdouble;
   }

   public static double calcSpeed(EntityPlayer player) {
      double distTraveledLastTickX = player.field_70165_t - player.field_70169_q;
      double distTraveledLastTickZ = player.field_70161_v - player.field_70166_s;
      return distTraveledLastTickX * distTraveledLastTickX + distTraveledLastTickZ * distTraveledLastTickZ;
   }

   public static class Info {
      double speed;
      Vec3d pos;
      Vec3d lastPos;
      double yaw;

      public Info(EntityPlayer player, Vec3d lastPos) {
         this.speed = SpeedUtil.calcSpeed(player);
         this.pos = player.func_174791_d();
         this.yaw = (double)RotationUtil.getRotationTo(this.pos, new Vec3d(player.field_70169_q, player.field_70167_r, player.field_70166_s)).field_189982_i;
         this.lastPos = lastPos;
      }
   }
}

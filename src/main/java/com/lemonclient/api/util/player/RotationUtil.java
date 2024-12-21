package com.lemonclient.api.util.player;

import com.lemonclient.api.util.world.BlockUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class RotationUtil {
   private static final Minecraft mc = Minecraft.func_71410_x();

   public static EnumFacing getFacing(double rotationYaw) {
      return EnumFacing.func_176731_b(MathHelper.func_76128_c(rotationYaw * 4.0D / 360.0D + 0.5D) & 3);
   }

   public static Vec2f getRotationTo(AxisAlignedBB box) {
      EntityPlayerSP player = mc.field_71439_g;
      if (player == null) {
         return Vec2f.field_189974_a;
      } else {
         Vec3d eyePos = player.func_174824_e(1.0F);
         if (player.func_174813_aQ().func_72326_a(box)) {
            return getRotationTo(eyePos, box.func_189972_c());
         } else {
            double x = MathHelper.func_151237_a(eyePos.field_72450_a, box.field_72340_a, box.field_72336_d);
            double y = MathHelper.func_151237_a(eyePos.field_72448_b, box.field_72338_b, box.field_72337_e);
            double z = MathHelper.func_151237_a(eyePos.field_72449_c, box.field_72339_c, box.field_72334_f);
            return getRotationTo(eyePos, new Vec3d(x, y, z));
         }
      }
   }

   public static Vec2f getRotationTo(Vec3d posTo) {
      EntityPlayerSP player = mc.field_71439_g;
      return player != null ? getRotationTo(player.func_174824_e(1.0F), posTo) : Vec2f.field_189974_a;
   }

   public static Vec2f getRotationTo(Vec3d posFrom, Vec3d posTo) {
      return getRotationFromVec(posTo.func_178788_d(posFrom));
   }

   public static Vec2f getRotationFromVec(Vec3d vec) {
      double lengthXZ = Math.hypot(vec.field_72450_a, vec.field_72449_c);
      double yaw = normalizeAngle(Math.toDegrees(Math.atan2(vec.field_72449_c, vec.field_72450_a)) - 90.0D);
      double pitch = normalizeAngle(Math.toDegrees(-Math.atan2(vec.field_72448_b, lengthXZ)));
      return new Vec2f((float)yaw, (float)pitch);
   }

   public static double normalizeAngle(double angle) {
      angle %= 360.0D;
      if (angle >= 180.0D) {
         angle -= 360.0D;
      }

      if (angle < -180.0D) {
         angle += 360.0D;
      }

      return angle;
   }

   public static float normalizeAngle(float angle) {
      angle %= 360.0F;
      if (angle >= 180.0F) {
         angle -= 360.0F;
      }

      if (angle < -180.0F) {
         angle += 360.0F;
      }

      return angle;
   }

   public static boolean isInFov(BlockPos pos) {
      return pos != null && (mc.field_71439_g.func_174818_b(pos) < 4.0D || yawDist(pos) < (double)(getHalvedfov() + 2.0F));
   }

   public static boolean isInFov(Entity entity) {
      return entity != null && (mc.field_71439_g.func_70068_e(entity) < 4.0D || yawDist(entity) < (double)(getHalvedfov() + 2.0F));
   }

   public static double yawDist(BlockPos pos) {
      if (pos != null) {
         Vec3d difference = (new Vec3d(pos)).func_178788_d(mc.field_71439_g.func_174824_e(mc.func_184121_ak()));
         double d = Math.abs((double)mc.field_71439_g.field_70177_z - (Math.toDegrees(Math.atan2(difference.field_72449_c, difference.field_72450_a)) - 90.0D)) % 360.0D;
         return d > 180.0D ? 360.0D - d : d;
      } else {
         return 0.0D;
      }
   }

   public static double yawDist(Entity e) {
      if (e != null) {
         Vec3d difference = e.func_174791_d().func_72441_c(0.0D, (double)(e.func_70047_e() / 2.0F), 0.0D).func_178788_d(mc.field_71439_g.func_174824_e(mc.func_184121_ak()));
         double d = Math.abs((double)mc.field_71439_g.field_70177_z - (Math.toDegrees(Math.atan2(difference.field_72449_c, difference.field_72450_a)) - 90.0D)) % 360.0D;
         return d > 180.0D ? 360.0D - d : d;
      } else {
         return 0.0D;
      }
   }

   public static float transformYaw() {
      float yaw = mc.field_71439_g.field_70177_z % 360.0F;
      if (mc.field_71439_g.field_70177_z > 0.0F) {
         if (yaw > 180.0F) {
            yaw = -180.0F + (yaw - 180.0F);
         }
      } else if (yaw < -180.0F) {
         yaw = 180.0F + yaw + 180.0F;
      }

      return yaw < 0.0F ? 180.0F + yaw : -180.0F + yaw;
   }

   public static boolean isInFov(Vec3d vec3d, Vec3d other) {
      if (mc.field_71439_g.field_70125_A > 30.0F) {
         if (other.field_72448_b > mc.field_71439_g.field_70163_u) {
            return true;
         }
      } else if (mc.field_71439_g.field_70125_A < -30.0F && other.field_72448_b < mc.field_71439_g.field_70163_u) {
         return true;
      }

      float angle = BlockUtil.calcAngleNoY(vec3d, other)[0] - transformYaw();
      if (angle < -270.0F) {
         return true;
      } else {
         float fov = mc.field_71474_y.field_74334_X / 2.0F;
         return angle < fov + 10.0F && angle > -fov - 10.0F;
      }
   }

   public static float getFov() {
      return mc.field_71474_y.field_74334_X;
   }

   public static float getHalvedfov() {
      return getFov() / 2.0F;
   }
}

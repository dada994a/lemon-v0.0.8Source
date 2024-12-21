package com.lemonclient.api.util.world;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MathUtil {
   public static int clamp(int num, int min, int max) {
      return num < min ? min : Math.min(num, max);
   }

   public static float clamp(float num, float min, float max) {
      return num < min ? min : Math.min(num, max);
   }

   public static double clamp(double num, double min, double max) {
      return num < min ? min : Math.min(num, max);
   }

   public static long clamp(long num, long min, long max) {
      return num < min ? min : Math.min(num, max);
   }

   public static BigDecimal clamp(BigDecimal num, BigDecimal min, BigDecimal max) {
      return smallerThan(num, min) ? min : (biggerThan(num, max) ? max : num);
   }

   public static Vec3d roundVec(Vec3d vec3d, int places) {
      return new Vec3d(round(vec3d.field_72450_a, places), round(vec3d.field_72448_b, places), round(vec3d.field_72449_c, places));
   }

   public static List<Vec3d> getBlockBlocks(Entity entity) {
      ArrayList<Vec3d> vec3ds = new ArrayList();
      AxisAlignedBB bb = entity.func_174813_aQ();
      double y = entity.field_70163_u;
      double minX = round(bb.field_72340_a, 0);
      double minZ = round(bb.field_72339_c, 0);
      double maxX = round(bb.field_72336_d, 0);
      double maxZ = round(bb.field_72334_f, 0);
      if (minX != maxX) {
         vec3ds.add(new Vec3d(minX, y, minZ));
         vec3ds.add(new Vec3d(maxX, y, minZ));
         if (minZ != maxZ) {
            vec3ds.add(new Vec3d(minX, y, maxZ));
            vec3ds.add(new Vec3d(maxX, y, maxZ));
            return vec3ds;
         }
      } else if (minZ != maxZ) {
         vec3ds.add(new Vec3d(minX, y, minZ));
         vec3ds.add(new Vec3d(minX, y, maxZ));
         return vec3ds;
      }

      vec3ds.add(entity.func_174791_d());
      return vec3ds;
   }

   public static boolean biggerThan(BigDecimal bigger, BigDecimal than) {
      return bigger.compareTo(than) > 0;
   }

   public static boolean smallerThan(BigDecimal smaller, BigDecimal than) {
      return smaller.compareTo(than) < 0;
   }

   public static double round(double value, int places) {
      return places < 0 ? value : (new BigDecimal(value)).setScale(places, RoundingMode.HALF_UP).doubleValue();
   }

   public static float round(float value, int places) {
      return places < 0 ? value : (new BigDecimal((double)value)).setScale(places, RoundingMode.HALF_UP).floatValue();
   }

   public static float round(float value, int places, float min, float max) {
      return MathHelper.func_76131_a(places < 0 ? value : (new BigDecimal((double)value)).setScale(places, RoundingMode.HALF_UP).floatValue(), min, max);
   }

   public static Vec3d interpolateEntity(Entity entity, float time) {
      return new Vec3d(entity.field_70142_S + (entity.field_70165_t - entity.field_70142_S) * (double)time, entity.field_70137_T + (entity.field_70163_u - entity.field_70137_T) * (double)time, entity.field_70136_U + (entity.field_70161_v - entity.field_70136_U) * (double)time);
   }

   public static float rad(float angle) {
      return (float)((double)angle * 3.141592653589793D / 180.0D);
   }

   public static float[] calcAngleNoY(Vec3d from, Vec3d to) {
      double difX = to.field_72450_a - from.field_72450_a;
      double difZ = to.field_72449_c - from.field_72449_c;
      return new float[]{(float)MathHelper.func_76138_g(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0D)};
   }

   public static Double calculateDoubleChange(double oldDouble, double newDouble, int step, int currentStep) {
      return oldDouble + (newDouble - oldDouble) * (double)Math.max(0, Math.min(step, currentStep)) / (double)step;
   }

   public static float[] calcAngle(Vec3d from, Vec3d to) {
      double difX = to.field_72450_a - from.field_72450_a;
      double difY = (to.field_72448_b - from.field_72448_b) * -1.0D;
      double difZ = to.field_72449_c - from.field_72449_c;
      double dist = (double)MathHelper.func_76133_a(difX * difX + difZ * difZ);
      return new float[]{(float)MathHelper.func_76138_g(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0D), (float)MathHelper.func_76138_g(Math.toDegrees(Math.atan2(difY, dist)))};
   }

   public static double square(double input) {
      return input * input;
   }

   public static double[] directionSpeed(double speed) {
      Minecraft mc = Minecraft.func_71410_x();
      float forward = mc.field_71439_g.field_71158_b.field_192832_b;
      float side = mc.field_71439_g.field_71158_b.field_78902_a;
      float yaw = mc.field_71439_g.field_70126_B + (mc.field_71439_g.field_70177_z - mc.field_71439_g.field_70126_B) * mc.func_184121_ak();
      if (forward != 0.0F) {
         if (side > 0.0F) {
            yaw += (float)(forward > 0.0F ? -45 : 45);
         } else if (side < 0.0F) {
            yaw += (float)(forward > 0.0F ? 45 : -45);
         }

         side = 0.0F;
         if (forward > 0.0F) {
            forward = 1.0F;
         } else if (forward < 0.0F) {
            forward = -1.0F;
         }
      }

      double sin = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
      double cos = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
      double posX = (double)forward * speed * cos + (double)side * speed * sin;
      double posZ = (double)forward * speed * sin - (double)side * speed * cos;
      return new double[]{posX, posZ};
   }
}

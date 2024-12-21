package com.lemonclient.api.util.misc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class MathUtil {
   private static final Minecraft mc = Minecraft.func_71410_x();
   public static Random rnd = new Random();

   public static int getRandom(int min, int max) {
      return rnd.nextInt(max - min + 1) + min;
   }

   public static ArrayList moveItemToFirst(ArrayList list, int index) {
      ArrayList newlist = new ArrayList();
      newlist.add(list.get(index));

      for(int i = 0; i < list.size(); ++i) {
         if (i != index) {
            newlist.add(list.get(index));
         }
      }

      return new ArrayList(list);
   }

   public static float[] calcAngle(Vec3d from, Vec3d to) {
      double difX = to.field_72450_a - from.field_72450_a;
      double difY = (to.field_72448_b - from.field_72448_b) * -1.0D;
      double difZ = to.field_72449_c - from.field_72449_c;
      double dist = (double)MathHelper.func_76133_a(difX * difX + difZ * difZ);
      return new float[]{(float)MathHelper.func_76138_g(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0D), (float)MathHelper.func_76138_g(Math.toDegrees(Math.atan2(difY, dist)))};
   }

   public static Vec2f calcAngleRotate(Vec3d from, Vec3d to) {
      double difX = to.field_72450_a - from.field_72450_a;
      double difY = (to.field_72448_b - from.field_72448_b) * -1.0D;
      double difZ = to.field_72449_c - from.field_72449_c;
      double dist = (double)MathHelper.func_76133_a(difX * difX + difZ * difZ);
      return new Vec2f((float)MathHelper.func_76138_g(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0D), (float)MathHelper.func_76138_g(Math.toDegrees(Math.atan2(difY, dist))));
   }

   public static float square(float v1) {
      return v1 * v1;
   }

   public static double square(Double v1) {
      return v1 * v1;
   }

   public static double calculateDistanceWithPartialTicks(double n, double n2, float renderPartialTicks) {
      return n2 + (n - n2) * (double)mc.func_184121_ak();
   }

   public static Vec3d interpolateEntityClose(Entity entity, float renderPartialTicks) {
      return new Vec3d(calculateDistanceWithPartialTicks(entity.field_70165_t, entity.field_70142_S, renderPartialTicks) - mc.func_175598_ae().field_78725_b, calculateDistanceWithPartialTicks(entity.field_70163_u, entity.field_70137_T, renderPartialTicks) - mc.func_175598_ae().field_78726_c, calculateDistanceWithPartialTicks(entity.field_70161_v, entity.field_70136_U, renderPartialTicks) - mc.func_175598_ae().field_78723_d);
   }

   public static double radToDeg(double rad) {
      return rad * 57.295780181884766D;
   }

   public static double degToRad(double deg) {
      return deg * 0.01745329238474369D;
   }

   public static Vec3d direction(float yaw) {
      return new Vec3d(Math.cos(degToRad((double)(yaw + 90.0F))), 0.0D, Math.sin(degToRad((double)(yaw + 90.0F))));
   }

   public static double round(double value, int places) {
      return places < 0 ? value : (new BigDecimal(value)).setScale(places, RoundingMode.HALF_UP).doubleValue();
   }

   public static float clamp(float val, float min, float max) {
      if (val <= min) {
         val = min;
      }

      if (val >= max) {
         val = max;
      }

      return val;
   }

   public static float wrap(float val) {
      val %= 360.0F;
      if (val >= 180.0F) {
         val -= 360.0F;
      }

      if (val < -180.0F) {
         val += 360.0F;
      }

      return val;
   }

   public static double map(double value, double a, double b, double c, double d) {
      value = (value - a) / (b - a);
      return c + value * (d - c);
   }

   public static double linear(double from, double to, double incline) {
      return from < to - incline ? from + incline : (from > to + incline ? from - incline : to);
   }

   public static double parabolic(double from, double to, double incline) {
      return from + (to - from) / incline;
   }

   public static double getDistance(Vec3d pos, double x, double y, double z) {
      double deltaX = pos.field_72450_a - x;
      double deltaY = pos.field_72448_b - y;
      double deltaZ = pos.field_72449_c - z;
      return (double)MathHelper.func_76133_a(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
   }

   public static double[] calcIntersection(double[] line, double[] line2) {
      double a1 = line[3] - line[1];
      double b1 = line[0] - line[2];
      double c1 = a1 * line[0] + b1 * line[1];
      double a2 = line2[3] - line2[1];
      double b2 = line2[0] - line2[2];
      double c2 = a2 * line2[0] + b2 * line2[1];
      double delta = a1 * b2 - a2 * b1;
      return new double[]{(b2 * c1 - b1 * c2) / delta, (a1 * c2 - a2 * c1) / delta};
   }

   public static double calculateAngle(double x1, double y1, double x2, double y2) {
      double angle = Math.toDegrees(Math.atan2(x2 - x1, y2 - y1));
      angle += Math.ceil(-angle / 360.0D) * 360.0D;
      return angle;
   }
}

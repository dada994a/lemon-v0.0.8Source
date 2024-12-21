package com.lemonclient.api.util.player;

import com.lemonclient.api.util.world.BlockUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MathUtil {
   public static Minecraft mc = Minecraft.func_71410_x();
   private static final Random random = new Random();

   public static double length(Vec3d vec3d) {
      return Math.sqrt(lengthSQ(vec3d));
   }

   public static double multiply(double n) {
      return n * n;
   }

   public static float cos(float n) {
      return MathHelper.func_76134_b(n);
   }

   public static boolean areVec3dsAlignedRetarded(Vec3d vec3d, Vec3d vec3d2) {
      return (new BlockPos(vec3d)).equals(new BlockPos(vec3d2.field_72450_a, vec3d.field_72448_b, vec3d2.field_72449_c));
   }

   public static double square(double n) {
      return n * n;
   }

   public static float rad(float angle) {
      return (float)((double)angle * 3.141592653589793D / 180.0D);
   }

   public static double getRandom(double n, double n2) {
      return MathHelper.func_151237_a(n + random.nextDouble() * n2, n, n2);
   }

   public static double[] differentDirectionSpeed(double n) {
      Minecraft getMinecraft = Minecraft.func_71410_x();
      float moveForward = getMinecraft.field_71439_g.field_71158_b.field_192832_b;
      float moveStrafe = getMinecraft.field_71439_g.field_71158_b.field_78902_a;
      float n2 = getMinecraft.field_71439_g.field_70126_B + (getMinecraft.field_71439_g.field_70177_z - getMinecraft.field_71439_g.field_70126_B) * getMinecraft.func_184121_ak();
      if (moveForward != 0.0F) {
         if (moveStrafe > 0.0F) {
            n2 += (float)(moveForward > 0.0F ? -45 : 45);
         } else if (moveStrafe < 0.0F) {
            n2 += (float)(moveForward > 0.0F ? 45 : -45);
         }

         moveStrafe = 0.0F;
         if (moveForward > 0.0F) {
            moveForward = 1.0F;
         } else if (moveForward < 0.0F) {
            moveForward = -1.0F;
         }
      }

      double sin = Math.sin(Math.toRadians((double)(n2 + 90.0F)));
      double cos = Math.cos(Math.toRadians((double)(n2 + 90.0F)));
      return new double[]{(double)moveForward * n * cos + (double)moveStrafe * n * sin, (double)moveForward * n * sin - (double)moveStrafe * n * cos};
   }

   public static Vec3d direction(float n) {
      return new Vec3d(Math.cos(degToRad((double)(n + 90.0F))), 0.0D, Math.sin(degToRad((double)(n + 90.0F))));
   }

   public static float round(float n, int newScale) {
      if (newScale < 0) {
         throw new IllegalArgumentException();
      } else {
         return BigDecimal.valueOf((double)n).setScale(newScale, RoundingMode.FLOOR).floatValue();
      }
   }

   public static double clamp(double a, double n, double b) {
      return a < n ? n : Math.min(a, b);
   }

   public static double angleBetweenVecs(Vec3d vec3d, Vec3d vec3d2) {
      return -(Math.atan2(vec3d.field_72450_a - vec3d2.field_72450_a, vec3d.field_72449_c - vec3d2.field_72449_c) / 3.141592653589793D) * 360.0D / 2.0D + 180.0D;
   }

   public static List<Vec3d> getBlockBlocks(Entity entity) {
      ArrayList<Vec3d> list = new ArrayList();
      AxisAlignedBB getEntityBoundingBox = entity.func_174813_aQ();
      double posY = entity.field_70163_u;
      double round = round(getEntityBoundingBox.field_72340_a, 0);
      double round2 = round(getEntityBoundingBox.field_72339_c, 0);
      double round3 = round(getEntityBoundingBox.field_72336_d, 0);
      double round4 = round(getEntityBoundingBox.field_72334_f, 0);
      Vec3d e5;
      Vec3d e6;
      BlockPos blockPos5;
      BlockPos blockPos6;
      if (round != round3) {
         e5 = new Vec3d(round, posY, round2);
         e6 = new Vec3d(round3, posY, round2);
         blockPos5 = new BlockPos(e5);
         blockPos6 = new BlockPos(e6);
         if (BlockUtil.isBlockUnSolid(blockPos5) && BlockUtil.isBlockUnSolid(blockPos6)) {
            list.add(e5);
            list.add(e6);
         }

         if (round2 != round4) {
            Vec3d e3 = new Vec3d(round, posY, round4);
            Vec3d e4 = new Vec3d(round3, posY, round4);
            BlockPos blockPos3 = new BlockPos(e5);
            BlockPos blockPos4 = new BlockPos(e6);
            if (BlockUtil.isBlockUnSolid(blockPos3) && BlockUtil.isBlockUnSolid(blockPos4)) {
               list.add(e3);
               list.add(e4);
               return list;
            }
         }

         if (list.isEmpty()) {
            list.add(entity.func_174791_d());
         }

         return list;
      } else if (round2 != round4) {
         e5 = new Vec3d(round, posY, round2);
         e6 = new Vec3d(round, posY, round4);
         blockPos5 = new BlockPos(e5);
         blockPos6 = new BlockPos(e6);
         if (BlockUtil.isBlockUnSolid(blockPos5) && BlockUtil.isBlockUnSolid(blockPos6)) {
            list.add(e5);
            list.add(e6);
         }

         if (list.isEmpty()) {
            list.add(entity.func_174791_d());
         }

         return list;
      } else {
         list.add(entity.func_174791_d());
         return list;
      }
   }

   public static float sin(float n) {
      return MathHelper.func_76126_a(n);
   }

   public static int clamp(int a, int n, int b) {
      return a < n ? n : Math.min(a, b);
   }

   public static double square(float n) {
      return (double)(n * n);
   }

   public static String getDirectionFromPlayer(double n, double n2) {
      double n3 = Math.toDegrees(Math.atan2(-(mc.field_71439_g.field_70165_t - n), -(mc.field_71439_g.field_70161_v - n2))) + (double)mc.field_71439_g.field_70177_z;
      if (n3 < 0.0D) {
         n3 += 360.0D;
      }

      if (!(n3 > 315.0D) && !(n3 <= 45.0D)) {
         if (n3 > 45.0D && n3 <= 135.0D) {
            return "to your left";
         } else if (n3 > 135.0D && n3 <= 225.0D) {
            return "behind you";
         } else {
            return n3 > 225.0D && n3 <= 315.0D ? "to your right" : String.valueOf(ChatFormatting.OBFUSCATED + "living in your walls");
         }
      } else {
         return "in front of you";
      }
   }

   public static float roundFloat(double val, int newScale) {
      return BigDecimal.valueOf(val).setScale(newScale, RoundingMode.FLOOR).floatValue();
   }

   public static float wrap(float n) {
      float n2 = n % 360.0F;
      if (n2 >= 180.0F) {
         n2 -= 360.0F;
      }

      if (n2 < -180.0F) {
         n2 += 360.0F;
      }

      return n2;
   }

   public static Vec3d roundVec(Vec3d vec3d, int n) {
      return new Vec3d(round(vec3d.field_72450_a, n), round(vec3d.field_72448_b, n), round(vec3d.field_72449_c, n));
   }

   public static double getIncremental(double n, double n2) {
      double n3 = 1.0D / n2;
      return (double)Math.round(n * n3) / n3;
   }

   public static Vec3d calculateLine(Vec3d vec3d, Vec3d vec3d2, double n) {
      double sqrt = Math.sqrt(multiply(vec3d2.field_72450_a - vec3d.field_72450_a) + multiply(vec3d2.field_72448_b - vec3d.field_72448_b) + multiply(vec3d2.field_72449_c - vec3d.field_72449_c));
      return new Vec3d(vec3d.field_72450_a + (vec3d2.field_72450_a - vec3d.field_72450_a) / sqrt * n, vec3d.field_72448_b + (vec3d2.field_72448_b - vec3d.field_72448_b) / sqrt * n, vec3d.field_72449_c + (vec3d2.field_72449_c - vec3d.field_72449_c) / sqrt * n);
   }

   public static double round(double val, int newScale) {
      if (newScale < 0) {
         throw new IllegalArgumentException();
      } else {
         return BigDecimal.valueOf(val).setScale(newScale, RoundingMode.FLOOR).doubleValue();
      }
   }

   public static boolean areVec3dsAligned(Vec3d vec3d, Vec3d vec3d2) {
      return areVec3dsAlignedRetarded(vec3d, vec3d2);
   }

   public static String getTimeOfDay() {
      int value = Calendar.getInstance().get(11);
      if (value < 12) {
         return "Good Morning ";
      } else if (value < 16) {
         return "Good Afternoon ";
      } else {
         return value < 21 ? "Good Evening " : "Good Night ";
      }
   }

   public static double[] directionSpeed(double n) {
      float moveForward = mc.field_71439_g.field_71158_b.field_192832_b;
      float moveStrafe = mc.field_71439_g.field_71158_b.field_78902_a;
      float n2 = mc.field_71439_g.field_70126_B + (mc.field_71439_g.field_70177_z - mc.field_71439_g.field_70126_B) * mc.func_184121_ak();
      if (moveForward != 0.0F) {
         if (moveStrafe > 0.0F) {
            n2 += (float)(moveForward > 0.0F ? -45 : 45);
         } else if (moveStrafe < 0.0F) {
            n2 += (float)(moveForward > 0.0F ? 45 : -45);
         }

         moveStrafe = 0.0F;
         if (moveForward > 0.0F) {
            moveForward = 1.0F;
         } else if (moveForward < 0.0F) {
            moveForward = -1.0F;
         }
      }

      double sin = Math.sin(Math.toRadians((double)(n2 + 90.0F)));
      double cos = Math.cos(Math.toRadians((double)(n2 + 90.0F)));
      return new double[]{(double)moveForward * n * cos + (double)moveStrafe * n * sin, (double)moveForward * n * sin - (double)moveStrafe * n * cos};
   }

   public static Vec3d extrapolatePlayerPosition(EntityPlayer entityPlayer, int n) {
      Vec3d calculateLine = calculateLine(new Vec3d(entityPlayer.field_70142_S, entityPlayer.field_70137_T, entityPlayer.field_70136_U), new Vec3d(entityPlayer.field_70165_t, entityPlayer.field_70163_u, entityPlayer.field_70161_v), (multiply(entityPlayer.field_70159_w) + multiply(entityPlayer.field_70181_x) + multiply(entityPlayer.field_70179_y)) * (double)n);
      return new Vec3d(calculateLine.field_72450_a, entityPlayer.field_70163_u, calculateLine.field_72449_c);
   }

   public static Vec3d interpolateEntity(Entity entity, float n) {
      return new Vec3d(entity.field_70142_S + (entity.field_70165_t - entity.field_70142_S) * (double)n, entity.field_70137_T + (entity.field_70163_u - entity.field_70137_T) * (double)n, entity.field_70136_U + (entity.field_70161_v - entity.field_70136_U) * (double)n);
   }

   public static double wrapDegrees(double n) {
      return MathHelper.func_76138_g(n);
   }

   public static float clamp(float a, float n, float b) {
      return a < n ? n : Math.min(a, b);
   }

   public static double radToDeg(double n) {
      return n * 57.295780181884766D;
   }

   public static Vec3d getInterpolatedRenderPos(Entity entity, float n) {
      return interpolateEntity(entity, n).func_178786_a(Minecraft.func_71410_x().func_175598_ae().field_78725_b, Minecraft.func_71410_x().func_175598_ae().field_78726_c, Minecraft.func_71410_x().func_175598_ae().field_78723_d);
   }

   public static double lengthSQ(Vec3d vec3d) {
      return square(vec3d.field_72450_a) + square(vec3d.field_72448_b) + square(vec3d.field_72449_c);
   }

   public static double dot(Vec3d vec3d, Vec3d vec3d2) {
      return vec3d.field_72450_a * vec3d2.field_72450_a + vec3d.field_72448_b * vec3d2.field_72448_b + vec3d.field_72449_c * vec3d2.field_72449_c;
   }

   public static float[] calcAngleNoY(Vec3d vec3d, Vec3d vec3d2) {
      return new float[]{(float)MathHelper.func_76138_g(Math.toDegrees(Math.atan2(vec3d2.field_72449_c - vec3d.field_72449_c, vec3d2.field_72450_a - vec3d.field_72450_a)) - 90.0D)};
   }

   public static float[] calcAngle(Vec3d vec3d, Vec3d vec3d2) {
      double x = vec3d2.field_72450_a - vec3d.field_72450_a;
      double y = (vec3d2.field_72448_b - vec3d.field_72448_b) * -1.0D;
      double y2 = vec3d2.field_72449_c - vec3d.field_72449_c;
      return new float[]{(float)MathHelper.func_76138_g(Math.toDegrees(Math.atan2(y2, x)) - 90.0D), (float)MathHelper.func_76138_g(Math.toDegrees(Math.atan2(y, (double)MathHelper.func_76133_a(x * x + y2 * y2))))};
   }

   public static float wrapDegrees(float n) {
      return MathHelper.func_76142_g(n);
   }

   public static double degToRad(double n) {
      return n * 0.01745329238474369D;
   }

   public static float getRandom(float n, float n2) {
      return MathHelper.func_76131_a(n + random.nextFloat() * n2, n, n2);
   }
}

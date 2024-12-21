package com.lemonclient.api.util.world;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public class MotionUtil {
   public static boolean isMoving(EntityLivingBase entity) {
      return entity.field_191988_bg != 0.0F || entity.field_70702_br != 0.0F || entity.field_70701_bs != 0.0F || entity.field_70181_x > -0.078D;
   }

   public static boolean moving(EntityLivingBase entity) {
      return entity.field_191988_bg != 0.0F || entity.field_70702_br != 0.0F;
   }

   public static double getMotion(EntityPlayer entity) {
      return Math.abs(entity.field_70159_w) + Math.abs(entity.field_70179_y);
   }

   public static void setSpeed(EntityLivingBase entity, double speed) {
      double[] dir = forward(speed);
      entity.field_70159_w = dir[0];
      entity.field_70179_y = dir[1];
   }

   public static double getBaseMoveSpeed() {
      double result = 0.2873D;
      if (Minecraft.func_71410_x().field_71439_g.func_70644_a(MobEffects.field_76424_c)) {
         result += 0.2873D * (double)(((PotionEffect)Objects.requireNonNull(Minecraft.func_71410_x().field_71439_g.func_70660_b(MobEffects.field_76424_c))).func_76458_c() + 1) * 0.2D;
      }

      if (Minecraft.func_71410_x().field_71439_g.func_70644_a(MobEffects.field_76421_d)) {
         result -= 0.2873D * (double)(((PotionEffect)Objects.requireNonNull(Minecraft.func_71410_x().field_71439_g.func_70660_b(MobEffects.field_76421_d))).func_76458_c() + 1) * 0.15D;
      }

      return result;
   }

   public static double[] forward(double speed) {
      float forward = Minecraft.func_71410_x().field_71439_g.field_71158_b.field_192832_b;
      float side = Minecraft.func_71410_x().field_71439_g.field_71158_b.field_78902_a;
      float yaw = Minecraft.func_71410_x().field_71439_g.field_70126_B + (Minecraft.func_71410_x().field_71439_g.field_70177_z - Minecraft.func_71410_x().field_71439_g.field_70126_B) * Minecraft.func_71410_x().func_184121_ak();
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

   public static double[] forward(double speed, float yaw) {
      float forward = 1.0F;
      float side = 0.0F;
      double sin = Math.sin(Math.toRadians((double)(yaw + 90.0F)));
      double cos = Math.cos(Math.toRadians((double)(yaw + 90.0F)));
      double posX = (double)forward * speed * cos + (double)side * speed * sin;
      double posZ = (double)forward * speed * sin - (double)side * speed * cos;
      return new double[]{posX, posZ};
   }

   public static double calcMoveYaw() {
      float yawIn = Minecraft.func_71410_x().field_71439_g.field_70177_z;
      float moveForward = getRoundedMovementInput(Minecraft.func_71410_x().field_71439_g.field_71158_b.field_192832_b);
      float moveString = getRoundedMovementInput(Minecraft.func_71410_x().field_71439_g.field_71158_b.field_78902_a);
      float strafe = 90.0F * moveString;
      strafe *= moveForward != 0.0F ? moveForward * 0.5F : 1.0F;
      float yaw = yawIn - strafe;
      yaw -= moveForward < 0.0F ? 180.0F : 0.0F;
      return Math.toRadians((double)yaw);
   }

   public static float getRoundedMovementInput(float input) {
      if (input > 0.0F) {
         return 1.0F;
      } else {
         return input < 0.0F ? -1.0F : 0.0F;
      }
   }
}

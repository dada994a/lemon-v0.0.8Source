package com.lemonclient.api.util.player;

import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.HoleUtil;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;

public class PredictUtil {
   static final Minecraft mc = Minecraft.func_71410_x();

   public static EntityPlayer predictPlayer(EntityLivingBase entity, PredictUtil.PredictSettings settings) {
      double[] posVec = new double[]{entity.field_70165_t, entity.field_70163_u, entity.field_70161_v};
      double motionX = entity.field_70165_t - entity.field_70142_S;
      double motionY = entity.field_70163_u - entity.field_70137_T;
      double motionZ = entity.field_70161_v - entity.field_70136_U;
      boolean isHole = false;
      int var10002;
      if (settings.manualOutHole && motionY > 0.2D) {
         if (HoleUtil.isHole(EntityUtil.getPosition(entity), false, true, false).getType() != HoleUtil.HoleType.NONE && BlockUtil.getBlock(EntityUtil.getPosition(entity).func_177982_a(0, 2, 0)) instanceof BlockAir) {
            isHole = true;
         } else if (settings.aboveHoleManual && HoleUtil.isHole(EntityUtil.getPosition(entity).func_177982_a(0, -1, 0), false, true, false).getType() != HoleUtil.HoleType.NONE) {
            isHole = true;
         }

         if (isHole) {
            var10002 = posVec[1]++;
         }
      }

      boolean allowPredictStair = false;
      int stairPredicted = 0;
      if (settings.stairPredict) {
         allowPredictStair = Math.hypot(motionX, motionZ) > settings.speedActivationStairs;
      }

      for(int i = 0; i < settings.tick; ++i) {
         boolean predictedStair = false;
         double[] newPosVec;
         if (!settings.splitXZ) {
            newPosVec = (double[])posVec.clone();
            newPosVec[0] += motionX;
            newPosVec[2] += motionZ;
            if (calculateRaytrace(posVec, newPosVec)) {
               posVec = (double[])newPosVec.clone();
            } else if (settings.stairPredict && allowPredictStair) {
               if (BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 1.0D, newPosVec[2])) && BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 2.0D, newPosVec[2])) && stairPredicted++ < settings.nStairs) {
                  var10002 = posVec[1]++;
                  predictedStair = true;
               } else if (BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 2.0D, newPosVec[2])) && BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 3.0D, newPosVec[2])) && stairPredicted++ < settings.nStairs) {
                  var10002 = posVec[1]++;
                  predictedStair = true;
               }
            }
         } else {
            newPosVec = (double[])posVec.clone();
            newPosVec[0] += motionX;
            if (calculateRaytrace(posVec, newPosVec)) {
               posVec = (double[])newPosVec.clone();
            } else if (settings.stairPredict && allowPredictStair) {
               if (BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 1.0D, newPosVec[2])) && BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 2.0D, newPosVec[2])) && stairPredicted++ < settings.nStairs) {
                  var10002 = posVec[1]++;
                  predictedStair = true;
               } else if (BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 2.0D, newPosVec[2])) && BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 3.0D, newPosVec[2])) && stairPredicted++ < settings.nStairs) {
                  posVec[1] += 2.0D;
                  predictedStair = true;
               }
            }

            newPosVec = (double[])posVec.clone();
            newPosVec[2] += motionZ;
            if (calculateRaytrace(posVec, newPosVec)) {
               posVec = (double[])newPosVec.clone();
            } else if (settings.stairPredict && allowPredictStair) {
               if (BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 1.0D, newPosVec[2])) && BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 2.0D, newPosVec[2])) && stairPredicted++ < settings.nStairs) {
                  var10002 = posVec[1]++;
                  predictedStair = true;
               } else if (BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 2.0D, newPosVec[2])) && BlockUtil.isAirBlock(new BlockPos(newPosVec[0], newPosVec[1] + 3.0D, newPosVec[2])) && stairPredicted++ < settings.nStairs) {
                  var10002 = posVec[1]++;
                  predictedStair = true;
               }
            }
         }

         if (settings.calculateY && !isHole && !predictedStair) {
            newPosVec = (double[])posVec.clone();
            double decreasePow = (double)settings.startDecrease / Math.pow(10.0D, (double)settings.exponentStartDecrease);
            double decreasePowY = (double)settings.decreaseY / Math.pow(10.0D, (double)settings.exponentDecreaseY);
            if (!entity.func_70090_H() && !entity.func_180799_ab() && !entity.func_184613_cA()) {
               motionY += decreasePowY;
               if (Math.abs(motionY) > decreasePow) {
                  motionY = decreasePowY;
               }

               newPosVec[1] += -1.0D * motionY;
            } else {
               decreasePowY = 0.0D;
               newPosVec[1] += motionY;
            }

            if (calculateRaytrace(posVec, newPosVec)) {
               posVec = (double[])newPosVec.clone();
            } else {
               motionY -= decreasePowY;
            }
         }
      }

      EntityOtherPlayerMP clonedPlayer = new EntityOtherPlayerMP(mc.field_71441_e, new GameProfile(UUID.fromString("fdee323e-7f0c-4c15-8d1c-0f277442342a"), entity.func_70005_c_()));
      clonedPlayer.func_70107_b(posVec[0], posVec[1], posVec[2]);
      if (entity instanceof EntityPlayer) {
         clonedPlayer.field_71071_by.func_70455_b(((EntityPlayer)entity).field_71071_by);
      }

      clonedPlayer.func_70606_j(entity.func_110143_aJ());
      clonedPlayer.field_70169_q = entity.field_70169_q;
      clonedPlayer.field_70167_r = entity.field_70167_r;
      clonedPlayer.field_70166_s = entity.field_70166_s;
      Iterator var21 = entity.func_70651_bq().iterator();

      while(var21.hasNext()) {
         PotionEffect effect = (PotionEffect)var21.next();
         clonedPlayer.func_70690_d(effect);
      }

      return clonedPlayer;
   }

   public static boolean calculateRaytrace(double[] posVec, double[] newPosVec) {
      RayTraceResult result = mc.field_71441_e.func_72933_a(new Vec3d(posVec[0], posVec[1], posVec[2]), new Vec3d(newPosVec[0], newPosVec[1], newPosVec[2]));
      RayTraceResult result1 = mc.field_71441_e.func_72933_a(new Vec3d(posVec[0] + 0.3D, posVec[1], posVec[2] + 0.3D), new Vec3d(newPosVec[0] - 0.3D, newPosVec[1], newPosVec[2] - 0.3D));
      RayTraceResult result2 = mc.field_71441_e.func_72933_a(new Vec3d(posVec[0] + 0.3D, posVec[1], posVec[2] - 0.3D), new Vec3d(newPosVec[0] - 0.3D, newPosVec[1], newPosVec[2] + 0.3D));
      RayTraceResult result3 = mc.field_71441_e.func_72933_a(new Vec3d(posVec[0] - 0.3D, posVec[1], posVec[2] + 0.3D), new Vec3d(newPosVec[0] + 0.3D, newPosVec[1], newPosVec[2] - 0.3D));
      RayTraceResult result4 = mc.field_71441_e.func_72933_a(new Vec3d(posVec[0] - 0.3D, posVec[1], posVec[2] - 0.3D), new Vec3d(newPosVec[0] + 0.3D, newPosVec[1], newPosVec[2] + 0.3D));
      if (result != null && result.field_72313_a != Type.ENTITY) {
         return false;
      } else {
         return (result1 == null || result1.field_72313_a == Type.ENTITY) && (result2 == null || result2.field_72313_a == Type.ENTITY) && (result3 == null || result3.field_72313_a == Type.ENTITY) && (result4 == null || result4.field_72313_a == Type.ENTITY);
      }
   }

   public static class PredictSettings {
      final int tick;
      final boolean calculateY;
      final int startDecrease;
      final int exponentStartDecrease;
      final int decreaseY;
      final int exponentDecreaseY;
      final boolean splitXZ;
      final boolean manualOutHole;
      final boolean aboveHoleManual;
      final boolean stairPredict;
      final int nStairs;
      final double speedActivationStairs;

      public PredictSettings(int tick, boolean calculateY, int startDecrease, int exponentStartDecrease, int decreaseY, int exponentDecreaseY, boolean splitXZ, boolean manualOutHole, boolean aboveHoleManual, boolean stairPredict, int nStairs, double speedActivationStairs) {
         this.tick = tick;
         this.calculateY = calculateY;
         this.startDecrease = startDecrease;
         this.exponentStartDecrease = exponentStartDecrease;
         this.decreaseY = decreaseY;
         this.exponentDecreaseY = exponentDecreaseY;
         this.splitXZ = splitXZ;
         this.manualOutHole = manualOutHole;
         this.aboveHoleManual = aboveHoleManual;
         this.stairPredict = stairPredict;
         this.nStairs = nStairs;
         this.speedActivationStairs = speedActivationStairs;
      }
   }
}

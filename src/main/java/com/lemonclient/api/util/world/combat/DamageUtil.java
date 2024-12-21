package com.lemonclient.api.util.world.combat;

import com.lemonclient.client.module.modules.qwq.LemonAura;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.EnumDifficulty;

public class DamageUtil {
   public static float calculateDamage(EntityLivingBase entity, EntityEnderCrystal crystal) {
      return calculateCrystalDamage(entity, crystal.field_70165_t, crystal.field_70163_u, crystal.field_70161_v);
   }

   public static float calculateDamage(EntityLivingBase entity, double posX, double posY, double posZ, float size, String mode) {
      Vec3d entityPos = entity.func_174791_d();
      AxisAlignedBB entityBox = entity.func_174813_aQ();
      MutableBlockPos mutableBlockPos = new MutableBlockPos();
      boolean isPlayer = entity instanceof EntityPlayer;
      if (isPlayer && entity.field_70170_p.func_175659_aa() == EnumDifficulty.PEACEFUL) {
         return 0.0F;
      } else {
         float damage = calcRawDamage(entity, entityPos, entityBox, posX, posY, posZ, size * 2.0F, mutableBlockPos, mode);
         if (isPlayer) {
            damage = calcDifficultyDamage(entity, damage);
         }

         return calcReductionDamage(entity, damage);
      }
   }

   public static float calculateCrystalDamage(EntityLivingBase entity, double posX, double posY, double posZ) {
      Vec3d entityPos = entity.func_174791_d();
      MutableBlockPos mutableBlockPos = new MutableBlockPos();
      boolean isPlayer = entity instanceof EntityPlayer;
      if (isPlayer && entity.field_70170_p.func_175659_aa() == EnumDifficulty.PEACEFUL) {
         return 0.0F;
      } else {
         mutableBlockPos.func_181079_c((int)posX, (int)posY - 1, (int)posZ);
         float damage;
         if (isPlayer && posY - entityPos.field_72448_b > 1.5652173822904127D && isResistant(entity.field_70170_p.func_180495_p(mutableBlockPos))) {
            damage = 1.0F;
         } else {
            float scaledDist = (float)(entityPos.func_72438_d(new Vec3d(posX, posY, posZ)) / 12.0D);
            if (scaledDist > 1.0F) {
               damage = 0.0F;
            } else {
               float blockDensity = (Boolean)LemonAura.INSTANCE.ignoreTerrain.getValue() ? ignoreTerrainDensity(new Vec3d(posX, posY, posZ), entity.func_174813_aQ(), entity, "Crystal") : entity.field_70170_p.func_72842_a(new Vec3d(posX, posY, posZ), entity.func_174813_aQ());
               float factor = (1.0F - scaledDist) * blockDensity;
               damage = Math.abs((factor * factor + factor) * 12.0F * 3.5F);
            }
         }

         if (isPlayer) {
            damage = calcDifficultyDamage(entity, damage);
         }

         return calcReductionDamage(entity, damage);
      }
   }

   public static float ignoreTerrainDensity(Vec3d vec, AxisAlignedBB bb, EntityLivingBase entity, String mode) {
      double d0 = 1.0D / ((bb.field_72336_d - bb.field_72340_a) * 2.0D + 1.0D);
      double d1 = 1.0D / ((bb.field_72337_e - bb.field_72338_b) * 2.0D + 1.0D);
      double d2 = 1.0D / ((bb.field_72334_f - bb.field_72339_c) * 2.0D + 1.0D);
      double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
      double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;
      if (d0 >= 0.0D && d1 >= 0.0D && d2 >= 0.0D) {
         int j2 = 0;
         int k2 = 0;

         for(float f = 0.0F; f <= 1.0F; f = (float)((double)f + d0)) {
            for(float f1 = 0.0F; f1 <= 1.0F; f1 = (float)((double)f1 + d1)) {
               for(float f2 = 0.0F; f2 <= 1.0F; f2 = (float)((double)f2 + d2)) {
                  double d5 = bb.field_72340_a + (bb.field_72336_d - bb.field_72340_a) * (double)f;
                  double d6 = bb.field_72338_b + (bb.field_72337_e - bb.field_72338_b) * (double)f1;
                  double d7 = bb.field_72339_c + (bb.field_72334_f - bb.field_72339_c) * (double)f2;
                  Vec3d newVec = new Vec3d(d5 + d3, d6, d7 + d4);
                  RayTraceResult result = entity.field_70170_p.func_72933_a(newVec, vec);
                  if (result == null) {
                     ++j2;
                  } else {
                     IBlockState state = com.lemonclient.api.util.world.BlockUtil.getState(result.func_178782_a());
                     if (getRaytrace(entity, mode, result.func_178782_a(), state).equals("SKIP")) {
                        ++j2;
                     }
                  }

                  ++k2;
               }
            }
         }

         return (float)j2 / (float)k2;
      } else {
         return 0.0F;
      }
   }

   public static float calcReductionDamage(EntityLivingBase entity, float damage) {
      PotionEffect potionEffect = entity.func_70660_b(MobEffects.field_76429_m);
      float resistance = potionEffect == null ? 1.0F : Math.max(1.0F - (float)(potionEffect.func_76458_c() + 1) * 0.2F, 0.0F);
      float blastReduction = 1.0F - (float)Math.min(calcTotalEPF(entity), 20) / 25.0F;
      return CombatRules.func_189427_a(damage, (float)entity.func_70658_aO(), (float)entity.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e()) * resistance * blastReduction;
   }

   public static int calcTotalEPF(EntityLivingBase entity) {
      int epf = 0;
      Iterator var2 = entity.func_184193_aE().iterator();

      while(var2.hasNext()) {
         ItemStack itemStack = (ItemStack)var2.next();
         NBTTagList nbtTagList = itemStack.func_77986_q();

         for(int i = 0; i <= nbtTagList.func_74745_c(); ++i) {
            NBTTagCompound nbtTagCompound = nbtTagList.func_150305_b(i);
            int id = nbtTagCompound.func_74762_e("id");
            int level = nbtTagCompound.func_74765_d("lvl");
            if (id == 0) {
               epf += level;
            } else if (id == 3) {
               epf += level * 2;
            }
         }
      }

      return epf;
   }

   public static float calcDifficultyDamage(EntityLivingBase entity, float damage) {
      switch(entity.field_70170_p.func_175659_aa()) {
      case PEACEFUL:
         return 0.0F;
      case EASY:
         return Math.min(damage * 0.5F + 1.0F, damage);
      case HARD:
         return damage * 1.5F;
      default:
         return damage;
      }
   }

   public static float calcDamage(EntityLivingBase entity, double crystalX, double crystalY, double crystalZ) {
      Vec3d entityPos = entity.func_174791_d();
      AxisAlignedBB entityBox = entity.func_174813_aQ();
      MutableBlockPos mutableBlockPos = new MutableBlockPos();
      boolean isPlayer = entity instanceof EntityPlayer;
      if (isPlayer && entity.field_70170_p.func_175659_aa() == EnumDifficulty.PEACEFUL) {
         return 0.0F;
      } else {
         mutableBlockPos.func_181079_c((int)crystalX, (int)crystalY - 1, (int)crystalZ);
         float damage;
         if (isPlayer && crystalY - entityPos.field_72448_b > 1.5652173822904127D && isResistant(entity.field_70170_p.func_180495_p(mutableBlockPos))) {
            damage = 1.0F;
         } else {
            damage = calcRawDamage(entity, entityPos, entityBox, crystalX, crystalY, crystalZ, 12.0F, mutableBlockPos, "Crystal");
         }

         if (isPlayer) {
            damage = calcDifficultyDamage(entity, damage);
         }

         return calcReductionDamage(entity, damage);
      }
   }

   private static float calcRawDamage(EntityLivingBase entity, Vec3d entityPos, AxisAlignedBB entityBox, double posX, double posY, double posZ, float doubleSize, MutableBlockPos mutableBlockPos, String mode) {
      float scaledDist = (float)(entityPos.func_72438_d(new Vec3d(posX, posY, posZ)) / (double)doubleSize);
      if (scaledDist > 1.0F) {
         return 0.0F;
      } else {
         float factor = (1.0F - scaledDist) * ignoreTerrainDensity(new Vec3d(posX, posY, posZ), entityBox, entity, mode);
         return (factor * factor + factor) * doubleSize * 3.5F + 1.0F;
      }
   }

   public static boolean isResistant(IBlockState blockState) {
      return blockState.func_185904_a() != Material.field_151579_a && !(blockState instanceof BlockLiquid) && (double)blockState.func_177230_c().field_149781_w >= 19.7D;
   }

   private static float getExposureAmount(EntityLivingBase entity, AxisAlignedBB entityBox, double posX, double posY, double posZ, MutableBlockPos mutableBlockPos, String mode) {
      double width = entityBox.field_72336_d - entityBox.field_72340_a;
      double height = entityBox.field_72337_e - entityBox.field_72338_b;
      double gridMultiplierXZ = 1.0D / (width * 2.0D + 1.0D);
      double gridMultiplierY = 1.0D / (height * 2.0D + 1.0D);
      double gridXZ = width * gridMultiplierXZ;
      double gridY = height * gridMultiplierY;
      int sizeXZ = (int)(1.0D / gridMultiplierXZ);
      int sizeY = (int)(1.0D / gridMultiplierY);
      double xzOffset = (1.0D - gridMultiplierXZ * (double)sizeXZ) / 2.0D;
      int total = 0;
      int count = 0;

      for(int yIndex = 0; yIndex <= sizeY; ++yIndex) {
         for(int xIndex = 0; xIndex <= sizeXZ; ++xIndex) {
            for(int zIndex = 0; zIndex <= sizeXZ; ++zIndex) {
               double x = gridXZ * (double)xIndex + xzOffset + entityBox.field_72340_a;
               double y = gridY * (double)yIndex + entityBox.field_72338_b;
               double z = gridXZ * (double)zIndex + xzOffset + entityBox.field_72339_c;
               ++total;
               if (!fastRayTrace(entity, x, y, z, posX, posY, posZ, 20, mutableBlockPos, mode)) {
                  ++count;
               }
            }
         }
      }

      return (float)count / (float)total;
   }

   public static boolean fastRayTrace(EntityLivingBase entity, double startX, double startY, double startZ, double endX, double endY, double endZ, int maxAttempt, MutableBlockPos mutableBlockPos, String mode) {
      double currentX = startX;
      double currentY = startY;
      double currentZ = startZ;
      int currentBlockX = (int)startX;
      int currentBlockY = (int)startY;
      int currentBlockZ = (int)startZ;
      mutableBlockPos.func_181079_c(currentBlockX, currentBlockY, currentBlockZ);
      IBlockState startBlockState = com.lemonclient.api.util.world.BlockUtil.getState(mutableBlockPos);
      String var26 = getRaytrace(entity, mode, mutableBlockPos, startBlockState);
      byte var27 = -1;
      switch(var26.hashCode()) {
      case 2060885:
         if (var26.equals("CALC")) {
            var27 = 1;
         }
         break;
      case 2366700:
         if (var26.equals("MISC")) {
            var27 = 0;
         }
      }

      switch(var27) {
      case 0:
         return false;
      case 1:
         if (rayTraceBlock(entity, startBlockState, mutableBlockPos, startX, startY, startZ, endX, endY, endZ)) {
            return true;
         }
      default:
         int endBlockX = (int)endX;
         int endBlockY = (int)endY;
         int endBlockZ = (int)endZ;
         int var29 = maxAttempt;

         while(var29-- >= 0) {
            if (currentBlockX == endBlockX && currentBlockY == endBlockY && currentBlockZ == endBlockZ) {
               return false;
            }

            int nextX = 999;
            int nextY = 999;
            int nextZ = 999;
            double stepX = 999.0D;
            double stepY = 999.0D;
            double stepZ = 999.0D;
            double diffX = endX - currentX;
            double diffY = endY - currentY;
            double diffZ = endZ - currentZ;
            if (endBlockX > currentBlockX) {
               nextX = currentBlockX + 1;
               stepX = ((double)nextX - currentX) / diffX;
            } else if (endBlockX < currentBlockX) {
               nextX = currentBlockX;
               stepX = ((double)currentBlockX - currentX) / diffX;
            }

            if (endBlockY > currentBlockY) {
               nextY = currentBlockY + 1;
               stepY = ((double)nextY - currentY) / diffY;
            } else if (endBlockY < currentBlockY) {
               nextY = currentBlockY;
               stepY = ((double)currentBlockY - currentY) / diffY;
            }

            if (endBlockZ > currentBlockZ) {
               nextZ = currentBlockZ + 1;
               stepZ = ((double)nextZ - currentZ) / diffZ;
            } else if (endBlockZ < currentBlockZ) {
               nextZ = currentBlockZ;
               stepZ = ((double)currentBlockZ - currentZ) / diffZ;
            }

            if (stepX < stepY && stepX < stepZ) {
               currentX = (double)nextX;
               currentY += diffY * stepX;
               currentZ += diffZ * stepX;
               currentBlockX = nextX - (endBlockX - currentBlockX >>> 31);
               currentBlockY = (int)currentY;
               currentBlockZ = (int)currentZ;
            } else if (stepY < stepZ) {
               currentX += diffX * stepY;
               currentY = (double)nextY;
               currentZ += diffZ * stepY;
               currentBlockX = (int)currentX;
               currentBlockY = nextY - (endBlockY - currentBlockY >>> 31);
               currentBlockZ = (int)currentZ;
            } else {
               currentX += diffX * stepZ;
               currentY += diffY * stepZ;
               currentZ = (double)nextZ;
               currentBlockX = (int)currentX;
               currentBlockY = (int)currentY;
               currentBlockZ = nextZ - (endBlockZ - currentBlockZ >>> 31);
            }

            mutableBlockPos.func_181079_c(currentBlockX, currentBlockY, currentBlockZ);
            if (!entity.field_70170_p.func_189509_E(mutableBlockPos) && entity.field_70170_p.func_175723_af().func_177746_a(mutableBlockPos)) {
               IBlockState blockState = com.lemonclient.api.util.world.BlockUtil.getState(mutableBlockPos);
               String var46 = getRaytrace(entity, mode, mutableBlockPos, blockState);
               byte var47 = -1;
               switch(var46.hashCode()) {
               case 2060885:
                  if (var46.equals("CALC")) {
                     var47 = 1;
                  }
                  break;
               case 2366700:
                  if (var46.equals("MISC")) {
                     var47 = 0;
                  }
               }

               switch(var47) {
               case 0:
                  return false;
               case 1:
                  if (rayTraceBlock(entity, blockState, mutableBlockPos, currentX, currentY, currentZ, endX, endY, endZ)) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   public static String getRaytrace(EntityLivingBase entity, String mode, BlockPos pos, IBlockState blockState) {
      byte var5 = -1;
      switch(mode.hashCode()) {
      case -1582753002:
         if (mode.equals("Crystal")) {
            var5 = 0;
         }
         break;
      case 66657:
         if (mode.equals("Bed")) {
            var5 = 1;
         }
         break;
      case 2092661:
         if (mode.equals("Calc")) {
            var5 = 2;
         }
         break;
      case 2578847:
         if (mode.equals("Skip")) {
            var5 = 3;
         }
      }

      switch(var5) {
      case 0:
         if (isResistant(blockState)) {
            return "CALC";
         }

         return "SKIP";
      case 1:
         Block block = blockState.func_177230_c();
         if (block != Blocks.field_150350_a && block != Blocks.field_150324_C && isResistant(blockState)) {
            return "CALC";
         }

         return "SKIP";
      case 2:
         return "Calc";
      case 3:
         return "Skip";
      default:
         return blockState.func_185890_d(entity.field_70170_p, pos) != null ? "CALC" : "SKIP";
      }
   }

   public static boolean rayTraceBlock(EntityLivingBase entity, IBlockState blockState, MutableBlockPos blockPos, double x1, double y1, double z1, double x2, double y2, double z2) {
      float x1f = (float)(x1 - (double)blockPos.func_177958_n());
      float y1f = (float)(y1 - (double)blockPos.func_177956_o());
      float z1f = (float)(z1 - (double)blockPos.func_177952_p());
      float x2f = (float)(x2 - (double)blockPos.func_177958_n());
      float y2f = (float)(y2 - (double)blockPos.func_177956_o());
      float z2f = (float)(z2 - (double)blockPos.func_177952_p());
      AxisAlignedBB box = blockState.func_185900_c(entity.field_70170_p, blockPos);
      float minX = (float)box.field_72340_a;
      float minY = (float)box.field_72338_b;
      float minZ = (float)box.field_72339_c;
      float maxX = (float)box.field_72336_d;
      float maxY = (float)box.field_72337_e;
      float maxZ = (float)box.field_72334_f;
      float xDiff = x2f - x1f;
      float yDiff = y2f - y1f;
      float zDiff = z2f - z1f;
      double factor;
      if (xDiff * xDiff >= 1.0E-7F) {
         factor = (double)((minX - x1f) / xDiff);
         if (!in(factor, 0.0D, 1.0D)) {
            factor = (double)((maxX - x1f) / xDiff);
         }

         if (in(factor, 0.0D, 1.0D) && in((double)y1f + (double)yDiff * factor, (double)minY, (double)maxY) && in((double)z1f + (double)zDiff * factor, (double)minZ, (double)maxZ)) {
            return true;
         }
      }

      if (yDiff * yDiff >= 1.0E-7F) {
         factor = (double)((minY - y1f) / yDiff);
         if (!in(factor, 0.0D, 1.0D)) {
            factor = (double)((maxY - y1f) / yDiff);
         }

         if (in(factor, 0.0D, 1.0D) && in((double)x1f + (double)xDiff * factor, (double)minX, (double)maxX) && in((double)z1f + (double)zDiff * factor, (double)minZ, (double)maxZ)) {
            return true;
         }
      }

      if ((double)(zDiff * zDiff) >= 1.0E-7D) {
         factor = (double)((minZ - z1f) / zDiff);
         if (!in(factor, 0.0D, 1.0D)) {
            factor = (double)((maxZ - z1f) / zDiff);
         }

         return in(factor, 0.0D, 1.0D) && in((double)x1f + (double)xDiff * factor, (double)minX, (double)maxX) && in((double)y1f + (double)yDiff * factor, (double)minY, (double)maxY);
      } else {
         return false;
      }
   }

   public static boolean in(double number, double floor, double ceil) {
      return number >= floor && number <= ceil;
   }
}

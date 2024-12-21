package com.lemonclient.api.util.player;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;

public class RayTraceUtil {
   public static Minecraft mc = Minecraft.func_71410_x();

   public static float[] hitVecToPlaceVec(BlockPos pos, Vec3d hitVec) {
      float x = (float)(hitVec.field_72450_a - (double)pos.func_177958_n());
      float y = (float)(hitVec.field_72448_b - (double)pos.func_177956_o());
      float z = (float)(hitVec.field_72449_c - (double)pos.func_177952_p());
      return new float[]{x, y, z};
   }

   public static RayTraceResult getRayTraceResult(float yaw, float pitch) {
      return getRayTraceResult(yaw, pitch, mc.field_71442_b.func_78757_d());
   }

   public static RayTraceResult getRayTraceResultWithEntity(float yaw, float pitch, Entity from) {
      return getRayTraceResult(yaw, pitch, mc.field_71442_b.func_78757_d(), from);
   }

   public static RayTraceResult getRayTraceResult(float yaw, float pitch, float distance) {
      return getRayTraceResult(yaw, pitch, distance, mc.field_71439_g);
   }

   public static RayTraceResult getRayTraceResult(float yaw, float pitch, float d, Entity from) {
      Vec3d vec3d = getEyePos(from);
      Vec3d lookVec = getVec3d(yaw, pitch);
      Vec3d rotations = vec3d.func_72441_c(lookVec.field_72450_a * (double)d, lookVec.field_72448_b * (double)d, lookVec.field_72449_c * (double)d);
      return (RayTraceResult)Optional.ofNullable(mc.field_71441_e.func_147447_a(vec3d, rotations, false, false, false)).orElseGet(() -> {
         return new RayTraceResult(Type.MISS, new Vec3d(0.5D, 1.0D, 0.5D), EnumFacing.UP, BlockPos.field_177992_a);
      });
   }

   public static Vec3d getVec3d(float yaw, float pitch) {
      float vx = -MathHelper.func_76126_a(MathUtil.rad(yaw)) * MathHelper.func_76134_b(MathUtil.rad(pitch));
      float vz = MathHelper.func_76134_b(MathUtil.rad(yaw)) * MathHelper.func_76134_b(MathUtil.rad(pitch));
      float vy = -MathHelper.func_76126_a(MathUtil.rad(pitch));
      return new Vec3d((double)vx, (double)vy, (double)vz);
   }

   public static Vec3d getEyePos(Entity entity) {
      return new Vec3d(entity.field_70165_t, getEyeHeight(entity), entity.field_70161_v);
   }

   public static double getEyeHeight(Entity entity) {
      return entity.field_70163_u + (double)entity.func_70047_e();
   }

   public static boolean canBeSeen(double x, double y, double z, Entity by) {
      return canBeSeen(new Vec3d(x, y, z), by.field_70165_t, by.field_70163_u, by.field_70161_v, by.func_70047_e());
   }

   public static boolean canBeSeen(Vec3d toSee, Entity by) {
      return canBeSeen(toSee, by.field_70165_t, by.field_70163_u, by.field_70161_v, by.func_70047_e());
   }

   public static boolean canBeSeen(Vec3d toSee, double x, double y, double z, float eyeHeight) {
      Vec3d start = new Vec3d(x, y + (double)eyeHeight, z);
      return mc.field_71441_e.func_147447_a(start, toSee, false, true, false) == null;
   }

   public static boolean canBeSeen(Entity toSee, EntityLivingBase by) {
      return by.func_70685_l(toSee);
   }

   public static boolean raytracePlaceCheck(Entity entity, BlockPos pos) {
      return getFacing(entity, pos, false) != null;
   }

   public static EnumFacing getFacing(Entity entity, BlockPos pos, boolean verticals) {
      EnumFacing[] var3 = EnumFacing.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EnumFacing facing = var3[var5];
         RayTraceResult result = mc.field_71441_e.func_147447_a(getEyePos(entity), new Vec3d((double)pos.func_177958_n() + 0.5D + (double)facing.func_176730_m().func_177958_n() * 1.0D / 2.0D, (double)pos.func_177956_o() + 0.5D + (double)facing.func_176730_m().func_177956_o() * 1.0D / 2.0D, (double)pos.func_177952_p() + 0.5D + (double)facing.func_176730_m().func_177952_p() * 1.0D / 2.0D), false, true, false);
         if (result != null && result.field_72313_a == Type.BLOCK && result.func_178782_a().equals(pos)) {
            return facing;
         }
      }

      if (verticals) {
         if ((double)pos.func_177956_o() > mc.field_71439_g.field_70163_u + (double)mc.field_71439_g.func_70047_e()) {
            return EnumFacing.DOWN;
         } else {
            return EnumFacing.UP;
         }
      } else {
         return null;
      }
   }
}

package com.lemonclient.api.util.world;

import com.lemonclient.api.util.misc.Wrapper;
import com.lemonclient.api.util.world.combat.CrystalUtil;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;

public class BlockUtil {
   public static final List shulkerList;
   public static final List blackList;
   public static final List unSolidBlocks;
   public static final List airBlocks;
   private static final Minecraft mc;
   static EnumFacing[] facing;

   public static Vec3d[] convertVec3ds(Vec3d vec3d, Vec3d[] input) {
      Vec3d[] output = new Vec3d[input.length];

      for(int i = 0; i < input.length; ++i) {
         output[i] = vec3d.func_178787_e(input[i]);
      }

      return output;
   }

   public static Vec3d[] convertVec3ds(EntityPlayer entity, Vec3d[] input) {
      return convertVec3ds(entity.func_174791_d(), input);
   }

   public static NonNullList<BlockPos> getBox(float range) {
      NonNullList<BlockPos> positions = NonNullList.func_191196_a();
      positions.addAll(EntityUtil.getSphere(new BlockPos(Math.floor(mc.field_71439_g.field_70165_t), Math.floor(mc.field_71439_g.field_70163_u), Math.floor(mc.field_71439_g.field_70161_v)), (double)range, 0.0D, false, true, 0));
      return positions;
   }

   public static NonNullList<BlockPos> getBox(float range, BlockPos pos) {
      NonNullList<BlockPos> positions = NonNullList.func_191196_a();
      positions.addAll(EntityUtil.getSphere(pos, (double)range, 0.0D, false, true, 0));
      return positions;
   }

   public static boolean isBlockUnSolid(BlockPos blockPos) {
      Block block = getBlock(blockPos);
      return isBlockUnSolid(block) || !block.field_149787_q;
   }

   public static boolean canOpen(BlockPos blockPos) {
      return canOpen(mc.field_71441_e.func_180495_p(blockPos).func_177230_c());
   }

   public static boolean isAir(BlockPos blockPos) {
      return isAir(mc.field_71441_e.func_180495_p(blockPos).func_177230_c());
   }

   public static boolean isAirBlock(BlockPos blockPos) {
      return isAirBlock(mc.field_71441_e.func_180495_p(blockPos).func_177230_c());
   }

   public static boolean raytraceCheck(BlockPos pos, float height) {
      return mc.field_71441_e.func_147447_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double)mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d((double)pos.func_177958_n(), (double)((float)pos.func_177956_o() + height), (double)pos.func_177952_p()), false, true, false) == null;
   }

   public static boolean canBePlace(BlockPos pos) {
      return !checkPlayer(pos) && canReplace(pos);
   }

   public static boolean canBePlace(BlockPos pos, double distance) {
      if (mc.field_71439_g.func_70011_f((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() + 0.5D, (double)pos.func_177952_p() + 0.5D) > distance) {
         return false;
      } else {
         return !checkPlayer(pos) && canReplace(pos);
      }
   }

   public static boolean checkPlayer(BlockPos pos) {
      Iterator var1 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(pos)).iterator();

      Entity entity;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         entity = (Entity)var1.next();
      } while(entity.field_70128_L || entity instanceof EntityItem || entity instanceof EntityXPOrb || entity instanceof EntityExpBottle || entity instanceof EntityArrow || entity instanceof EntityEnderCrystal);

      return true;
   }

   public static EnumFacing getBestNeighboring(BlockPos pos, EnumFacing facing) {
      EnumFacing[] var2 = EnumFacing.field_82609_l;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnumFacing i = var2[var4];
         if ((facing == null || !pos.func_177972_a(i).equals(pos.func_177967_a(facing, -1))) && i != EnumFacing.DOWN) {
            Iterator var6 = getPlacableFacings(pos.func_177972_a(i), true, true).iterator();

            while(var6.hasNext()) {
               EnumFacing side = (EnumFacing)var6.next();
               if (canClick(pos.func_177972_a(i).func_177972_a(side))) {
                  return i;
               }
            }
         }
      }

      EnumFacing bestFacing = null;
      double distance = 0.0D;
      EnumFacing[] var14 = EnumFacing.field_82609_l;
      int var15 = var14.length;

      label55:
      for(int var16 = 0; var16 < var15; ++var16) {
         EnumFacing i = var14[var16];
         if ((facing == null || !pos.func_177972_a(i).equals(pos.func_177967_a(facing, -1))) && i != EnumFacing.DOWN) {
            Iterator var11 = getPlacableFacings(pos.func_177972_a(i), true, false).iterator();

            while(true) {
               EnumFacing side;
               do {
                  do {
                     if (!var11.hasNext()) {
                        continue label55;
                     }

                     side = (EnumFacing)var11.next();
                  } while(!canClick(pos.func_177972_a(i).func_177972_a(side)));
               } while(bestFacing != null && !(mc.field_71439_g.func_174818_b(pos.func_177972_a(i)) < distance));

               bestFacing = i;
               distance = mc.field_71439_g.func_174818_b(pos.func_177972_a(i));
            }
         }
      }

      return null;
   }

   public static double distanceToXZ(double x, double z) {
      double dx = mc.field_71439_g.field_70165_t - x;
      double dz = mc.field_71439_g.field_70161_v - z;
      return Math.sqrt(dx * dx + dz * dz);
   }

   public static void placeBlock(BlockPos pos, boolean rotate, boolean packet, boolean strict, boolean raytrace, boolean swing) {
      placeBlock(pos, EnumHand.MAIN_HAND, rotate, packet, strict, raytrace, swing);
   }

   public static void placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean attackEntity, boolean strict, boolean raytrace, boolean swing) {
      if (attackEntity) {
         CrystalUtil.breakCrystal(pos, swing);
      }

      placeBlock(pos, hand, rotate, packet, strict, raytrace, swing);
   }

   public static boolean canBlockFacing(BlockPos pos) {
      boolean airCheck = false;
      EnumFacing[] var2 = EnumFacing.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnumFacing side = var2[var4];
         if (canClick(pos.func_177972_a(side))) {
            airCheck = true;
         }
      }

      return airCheck;
   }

   public static boolean strictPlaceCheck(BlockPos pos, boolean strict, boolean raytrace) {
      if (!strict) {
         return true;
      } else {
         Iterator var3 = getPlacableFacings(pos, true, raytrace).iterator();

         EnumFacing side;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            side = (EnumFacing)var3.next();
         } while(!canClick(pos.func_177972_a(side)));

         return true;
      }
   }

   public static boolean canClick(BlockPos pos) {
      return mc.field_71441_e.func_180495_p(pos).func_177230_c().func_176209_a(mc.field_71441_e.func_180495_p(pos), false);
   }

   public static void placeCrystal(BlockPos pos, boolean rotate) {
      boolean offhand = mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP;
      BlockPos obsPos = pos.func_177977_b();
      RayTraceResult result = mc.field_71441_e.func_72933_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double)mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() - 0.5D, (double)pos.func_177952_p() + 0.5D));
      EnumFacing facing = result != null && result.field_178784_b != null ? result.field_178784_b : EnumFacing.UP;
      EnumFacing opposite = facing.func_176734_d();
      Vec3d vec = (new Vec3d(obsPos)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e(new Vec3d(opposite.func_176730_m()));
      if (rotate) {
         EntityUtil.faceVector(vec);
      }

      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(obsPos, facing, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
      mc.field_71439_g.func_184609_a(offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
   }

   public static boolean canPlaceCrystal(BlockPos pos, double distance) {
      if (mc.field_71439_g.func_70011_f((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() + 0.5D, (double)pos.func_177952_p() + 0.5D) > distance) {
         return false;
      } else {
         BlockPos obsPos = pos.func_177977_b();
         BlockPos boost = obsPos.func_177984_a();
         BlockPos boost2 = obsPos.func_177981_b(2);
         return (getBlock(obsPos) == Blocks.field_150357_h || getBlock(obsPos) == Blocks.field_150343_Z) && getBlock(boost) == Blocks.field_150350_a && getBlock(boost2) == Blocks.field_150350_a && mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
      }
   }

   public static boolean canPlaceCrystal(BlockPos pos) {
      BlockPos obsPos = pos.func_177977_b();
      BlockPos boost = obsPos.func_177984_a();
      BlockPos boost2 = obsPos.func_177981_b(2);
      return (getBlock(obsPos) == Blocks.field_150357_h || getBlock(obsPos) == Blocks.field_150343_Z) && getBlock(boost) == Blocks.field_150350_a && getBlock(boost2) == Blocks.field_150350_a && mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
   }

   public static List<EnumFacing> getPlacableFacings(BlockPos pos, boolean strictDirection, boolean rayTrace) {
      ArrayList<EnumFacing> validFacings = new ArrayList();
      EnumFacing[] var4 = EnumFacing.values();
      int var5 = var4.length;

      int var6;
      EnumFacing side;
      for(var6 = 0; var6 < var5; ++var6) {
         side = var4[var6];
         if (!getRaytrace(pos, side)) {
            getPlaceFacing(pos, strictDirection, validFacings, side);
         }
      }

      var4 = EnumFacing.values();
      var5 = var4.length;

      for(var6 = 0; var6 < var5; ++var6) {
         side = var4[var6];
         if (!rayTrace || !getRaytrace(pos, side)) {
            getPlaceFacing(pos, strictDirection, validFacings, side);
         }
      }

      return validFacings;
   }

   public static List<EnumFacing> getTrapPlacableFacings(BlockPos pos, boolean strictDirection, boolean rayTrace) {
      ArrayList<EnumFacing> validFacings = new ArrayList();
      EnumFacing[] var4 = facing;
      int var5 = var4.length;

      int var6;
      EnumFacing side;
      for(var6 = 0; var6 < var5; ++var6) {
         side = var4[var6];
         if (!getRaytrace(pos, side)) {
            getPlaceFacing(pos, strictDirection, validFacings, side);
         }
      }

      var4 = facing;
      var5 = var4.length;

      for(var6 = 0; var6 < var5; ++var6) {
         side = var4[var6];
         if (!rayTrace || !getRaytrace(pos, side)) {
            getPlaceFacing(pos, strictDirection, validFacings, side);
         }
      }

      return validFacings;
   }

   private static boolean getRaytrace(BlockPos pos, EnumFacing side) {
      Vec3d testVec = (new Vec3d(pos)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(side.func_176730_m())).func_186678_a(0.5D));
      RayTraceResult result = mc.field_71441_e.func_72933_a(mc.field_71439_g.func_174824_e(1.0F), testVec);
      return result != null && result.field_72313_a != Type.MISS;
   }

   private static void getPlaceFacing(BlockPos pos, boolean strictDirection, ArrayList<EnumFacing> validFacings, EnumFacing side) {
      BlockPos neighbour = pos.func_177972_a(side);
      if (strictDirection) {
         Vec3d eyePos = mc.field_71439_g.func_174824_e(1.0F);
         Vec3d blockCenter = new Vec3d((double)neighbour.func_177958_n() + 0.5D, (double)neighbour.func_177956_o() + 0.5D, (double)neighbour.func_177952_p() + 0.5D);
         IBlockState blockState2 = mc.field_71441_e.func_180495_p(neighbour);
         boolean isFullBox = blockState2.func_177230_c() == Blocks.field_150350_a || blockState2.func_185913_b();
         ArrayList<EnumFacing> validAxis = new ArrayList();
         validAxis.addAll(checkAxis(eyePos.field_72450_a - blockCenter.field_72450_a, EnumFacing.WEST, EnumFacing.EAST, !isFullBox));
         validAxis.addAll(checkAxis(eyePos.field_72448_b - blockCenter.field_72448_b, EnumFacing.DOWN, EnumFacing.UP, true));
         validAxis.addAll(checkAxis(eyePos.field_72449_c - blockCenter.field_72449_c, EnumFacing.NORTH, EnumFacing.SOUTH, !isFullBox));
         if (!validAxis.contains(side.func_176734_d())) {
            return;
         }
      }

      IBlockState blockState;
      if ((blockState = mc.field_71441_e.func_180495_p(neighbour)).func_177230_c().func_176209_a(blockState, false) && !blockState.func_185904_a().func_76222_j()) {
         validFacings.add(side);
      }
   }

   public static ArrayList<EnumFacing> checkAxis(double diff, EnumFacing negativeSide, EnumFacing positiveSide, boolean bothIfInRange) {
      ArrayList<EnumFacing> valid = new ArrayList();
      if (diff < -0.5D) {
         valid.add(negativeSide);
      }

      if (diff > 0.5D) {
         valid.add(positiveSide);
      }

      if (bothIfInRange) {
         if (!valid.contains(negativeSide)) {
            valid.add(negativeSide);
         }

         if (!valid.contains(positiveSide)) {
            valid.add(positiveSide);
         }
      }

      return valid;
   }

   public static boolean canPlaceEnum(BlockPos pos, boolean strict, boolean raytrace) {
      return !canBlockFacing(pos) ? false : strictPlaceCheck(pos, strict, raytrace);
   }

   public static boolean checkEntity(BlockPos pos) {
      Iterator var1 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(pos)).iterator();

      Entity entity;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         entity = (Entity)var1.next();
      } while(entity.field_70128_L || entity instanceof EntityItem || entity instanceof EntityXPOrb || entity instanceof EntityExpBottle || entity instanceof EntityArrow);

      return true;
   }

   public static boolean canPlace(BlockPos pos, double distance, boolean strict, boolean raytrace) {
      if (mc.field_71439_g.func_70011_f((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() + 0.5D, (double)pos.func_177952_p() + 0.5D) > distance) {
         return false;
      } else if (!canBlockFacing(pos)) {
         return false;
      } else if (!canReplace(pos)) {
         return false;
      } else if (!strictPlaceCheck(pos, strict, raytrace)) {
         return false;
      } else {
         return !checkEntity(pos);
      }
   }

   public static boolean canPlace(BlockPos pos, boolean strict, boolean raytrace) {
      if (mc.field_71439_g.func_70011_f((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() + 0.5D, (double)pos.func_177952_p() + 0.5D) > 6.0D) {
         return false;
      } else if (!canBlockFacing(pos)) {
         return false;
      } else if (!canReplace(pos)) {
         return false;
      } else if (!strictPlaceCheck(pos, strict, raytrace)) {
         return false;
      } else {
         return !checkEntity(pos);
      }
   }

   public static boolean canPlaceWithoutBase(BlockPos pos, boolean strict, boolean raytrace, boolean base) {
      if (!canBlockFacing(pos) && !base) {
         return false;
      } else if (!canReplace(pos)) {
         return false;
      } else if (!strictPlaceCheck(pos, strict, raytrace) && !base) {
         return false;
      } else {
         return !checkEntity(pos);
      }
   }

   public static void placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean strict, boolean raytrace, boolean swing) {
      EnumFacing side = getFirstFacing(pos, strict, raytrace);
      if (side != null) {
         BlockPos neighbour = pos.func_177972_a(side);
         EnumFacing opposite = side.func_176734_d();
         Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
         boolean sneaking = false;
         if (!ColorMain.INSTANCE.sneaking && blackList.contains(getBlock(neighbour))) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
            sneaking = true;
         }

         if (rotate) {
            faceVector(hitVec);
         }

         rightClickBlock(neighbour, hitVec, hand, opposite, packet, swing);
         if (sneaking) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
         }

      }
   }

   public static boolean placeBlockBoolean(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean strict, boolean raytrace, boolean swing) {
      EnumFacing side = getFirstFacing(pos, strict, raytrace);
      if (side == null) {
         return false;
      } else {
         BlockPos neighbour = pos.func_177972_a(side);
         EnumFacing opposite = side.func_176734_d();
         Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
         boolean sneaking = false;
         if (!ColorMain.INSTANCE.sneaking && blackList.contains(getBlock(neighbour))) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
            sneaking = true;
         }

         if (rotate) {
            faceVector(hitVec);
         }

         rightClickBlock(neighbour, hitVec, hand, opposite, packet, swing);
         if (sneaking) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
         }

         return true;
      }
   }

   public static void faceVector(Vec3d vec) {
      float[] rotations = EntityUtil.getLegitRotations(vec);
      EntityUtil.sendPlayerRot(rotations[0], rotations[1], mc.field_71439_g.field_70122_E);
   }

   public static boolean posHasCrystal(BlockPos pos) {
      Iterator var1 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(pos)).iterator();

      Entity entity;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         entity = (Entity)var1.next();
      } while(!(entity instanceof EntityEnderCrystal) || !(new BlockPos(entity.field_70165_t, entity.field_70163_u, entity.field_70161_v)).equals(pos));

      return true;
   }

   public static boolean canReplace(BlockPos pos) {
      if (pos == null) {
         return false;
      } else {
         return getState(pos).func_185904_a().func_76222_j() || isAir(pos);
      }
   }

   public static boolean canReplace(Vec3d vec3d) {
      if (vec3d == null) {
         return false;
      } else {
         BlockPos pos = new BlockPos(vec3d);
         return getState(pos).func_185904_a().func_76222_j() || isAir(pos);
      }
   }

   public static boolean isBlockUnSolid(Block block) {
      return unSolidBlocks.contains(block);
   }

   public static boolean canOpen(Block block) {
      return blackList.contains(block);
   }

   public static boolean isAir(Block block) {
      return airBlocks.contains(block);
   }

   public static boolean isAirBlock(Block block) {
      return block == Blocks.field_150350_a;
   }

   public static double blockDistance2d(double blockposx, double blockposz, Entity owo) {
      double deltaX = owo.field_70165_t - blockposx;
      double deltaZ = owo.field_70161_v - blockposz;
      return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
   }

   public static EnumFacing getRayTraceFacing(BlockPos pos) {
      RayTraceResult result = mc.field_71441_e.func_72933_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double)mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() - 0.5D, (double)pos.func_177952_p() + 0.5D));
      return result != null && result.field_178784_b != null ? result.field_178784_b : EnumFacing.UP;
   }

   public static EnumFacing getRayTraceFacing(BlockPos pos, EnumFacing facing) {
      RayTraceResult result = mc.field_71441_e.func_72933_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double)mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() - 0.5D, (double)pos.func_177952_p() + 0.5D));
      return result != null && result.field_178784_b != null ? result.field_178784_b : facing;
   }

   public static IBlockState getState(BlockPos pos) {
      return mc.field_71441_e.func_180495_p(pos);
   }

   public static float[] calcAngle(Vec3d from, Vec3d to) {
      double difX = to.field_72450_a - from.field_72450_a;
      double difY = (to.field_72448_b - from.field_72448_b) * -1.0D;
      double difZ = to.field_72449_c - from.field_72449_c;
      double dist = (double)MathHelper.func_76133_a(difX * difX + difZ * difZ);
      return new float[]{(float)MathHelper.func_76138_g(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0D), (float)MathHelper.func_76138_g(Math.toDegrees(Math.atan2(difY, dist)))};
   }

   public static Rotation getFaceVectorPacket(Vec3d vec, Boolean roundAngles) {
      float[] rotations = getNeededRotations2(vec);
      Rotation e = new Rotation(rotations[0], roundAngles ? (float)MathHelper.func_180184_b((int)rotations[1], 360) : rotations[1], mc.field_71439_g.field_70122_E);
      mc.field_71439_g.field_71174_a.func_147297_a(e);
      return e;
   }

   public static float[] calcAngleNoY(Vec3d from, Vec3d to) {
      double difX = to.field_72450_a - from.field_72450_a;
      double difZ = to.field_72449_c - from.field_72449_c;
      return new float[]{(float)MathHelper.func_76138_g(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0D)};
   }

   public static BlockPos[] toBlockPos(Vec3d[] vec3ds) {
      BlockPos[] list = new BlockPos[vec3ds.length];

      for(int i = 0; i < vec3ds.length; ++i) {
         list[i] = new BlockPos(vec3ds[i]);
      }

      return list;
   }

   public static boolean hasNeighbour(BlockPos blockPos) {
      boolean canPlace = false;
      EnumFacing[] var2 = EnumFacing.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnumFacing side = var2[var4];
         BlockPos neighbour = blockPos.func_177972_a(side);
         if (mc.field_71441_e.func_180495_p(neighbour).func_185904_a().func_76222_j()) {
            canPlace = true;
         }
      }

      return canPlace;
   }

   public static boolean canPlaceBlock(BlockPos pos) {
      return (getBlock(pos) == Blocks.field_150350_a || getBlock(pos) instanceof BlockLiquid) && hasNeighbour(pos) && !blackList.contains(getBlock(pos));
   }

   public static boolean canPlaceBlockFuture(BlockPos pos) {
      return (getBlock(pos) == Blocks.field_150350_a || getBlock(pos) instanceof BlockLiquid) && !blackList.contains(getBlock(pos));
   }

   public static void rightClickBlock(BlockPos pos, EnumFacing facing, boolean packet) {
      Vec3d hitVec = (new Vec3d(pos)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(facing.func_176730_m())).func_186678_a(0.5D));
      if (packet) {
         rightClickBlock(pos, hitVec, EnumHand.MAIN_HAND, facing);
      } else {
         mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, pos, facing, hitVec, EnumHand.MAIN_HAND);
         mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
      }

   }

   public static void rightClickBlock(BlockPos pos, EnumFacing facing, Vec3d hVec, boolean packet) {
      Vec3d hitVec = (new Vec3d(pos)).func_178787_e(hVec).func_178787_e((new Vec3d(facing.func_176730_m())).func_186678_a(0.5D));
      if (packet) {
         rightClickBlock(pos, hitVec, EnumHand.MAIN_HAND, facing);
      } else {
         mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, pos, facing, hitVec, EnumHand.MAIN_HAND);
         mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
      }

   }

   public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction) {
      float f = (float)(vec.field_72450_a - (double)pos.func_177958_n());
      float f1 = (float)(vec.field_72448_b - (double)pos.func_177956_o());
      float f2 = (float)(vec.field_72449_c - (double)pos.func_177952_p());
      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
      mc.field_71467_ac = 4;
   }

   public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet, boolean swing) {
      if (packet) {
         float f = (float)(vec.field_72450_a - (double)pos.func_177958_n());
         float f1 = (float)(vec.field_72448_b - (double)pos.func_177956_o());
         float f2 = (float)(vec.field_72449_c - (double)pos.func_177952_p());
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
      } else {
         mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, pos, direction, vec, hand);
      }

      if (swing) {
         mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
      }

      mc.field_71467_ac = 4;
   }

   public static int isPositionPlaceable(BlockPos pos, boolean rayTrace) {
      return isPositionPlaceable(pos, rayTrace, true);
   }

   public static EnumFacing getFirstFacing(BlockPos pos, boolean strict, boolean raytrace) {
      Iterator iterator;
      if (!strict) {
         iterator = getPossibleSides(pos).iterator();
         if (iterator.hasNext()) {
            return (EnumFacing)iterator.next();
         }
      } else {
         iterator = getPlacableFacings(pos, true, raytrace).iterator();

         while(iterator.hasNext()) {
            EnumFacing side = (EnumFacing)iterator.next();
            if (canClick(pos.func_177972_a(side))) {
               return side;
            }
         }
      }

      return null;
   }

   public static EnumFacing getTrapFirstFacing(BlockPos pos, boolean strict, boolean raytrace) {
      Iterator iterator;
      if (!strict) {
         iterator = getTrapPossibleSides(pos).iterator();
         if (iterator.hasNext()) {
            return (EnumFacing)iterator.next();
         }
      } else {
         iterator = getTrapPlacableFacings(pos, true, raytrace).iterator();

         while(iterator.hasNext()) {
            EnumFacing side = (EnumFacing)iterator.next();
            if (canClick(pos.func_177972_a(side))) {
               return side;
            }
         }
      }

      return null;
   }

   public static int isPositionPlaceable(BlockPos pos, boolean rayTrace, boolean entityCheck) {
      Block block = mc.field_71441_e.func_180495_p(pos).func_177230_c();
      if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) {
         return 0;
      } else if (!rayTracePlaceCheck(pos, rayTrace, 0.0F)) {
         return -1;
      } else {
         Iterator var4;
         if (entityCheck) {
            var4 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(pos)).iterator();

            while(var4.hasNext()) {
               Entity entity = (Entity)var4.next();
               if (!(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                  return 1;
               }
            }
         }

         var4 = getPossibleSides(pos).iterator();

         EnumFacing side;
         do {
            if (!var4.hasNext()) {
               return 2;
            }

            side = (EnumFacing)var4.next();
         } while(!canBeClicked(pos.func_177972_a(side)));

         return 3;
      }
   }

   public static List<EnumFacing> getPossibleSides(BlockPos pos) {
      List<EnumFacing> facings = new ArrayList();
      if (mc.field_71441_e != null && pos != null) {
         EnumFacing[] var2 = EnumFacing.field_82609_l;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            EnumFacing side = var2[var4];
            BlockPos neighbour = pos.func_177972_a(side);
            IBlockState blockState = mc.field_71441_e.func_180495_p(neighbour);
            if (blockState.func_177230_c().func_176209_a(blockState, false) && !blockState.func_185904_a().func_76222_j() && canBeClicked(neighbour)) {
               facings.add(side);
            }
         }

         return facings;
      } else {
         return facings;
      }
   }

   public static List<EnumFacing> getTrapPossibleSides(BlockPos pos) {
      List<EnumFacing> facings = new ArrayList();
      if (mc.field_71441_e != null && pos != null) {
         EnumFacing[] var2 = facing;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            EnumFacing side = var2[var4];
            BlockPos neighbour = pos.func_177972_a(side);
            IBlockState blockState = mc.field_71441_e.func_180495_p(neighbour);
            if (blockState != null && blockState.func_177230_c().func_176209_a(blockState, false) && !blockState.func_185904_a().func_76222_j()) {
               facings.add(side);
            }
         }

         return facings;
      } else {
         return facings;
      }
   }

   public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck, float height) {
      return !shouldCheck || mc.field_71441_e.func_147447_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double)mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d((double)pos.func_177958_n(), (double)((float)pos.func_177956_o() + height), (double)pos.func_177952_p()), false, true, false) == null;
   }

   public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck) {
      return rayTracePlaceCheck(pos, shouldCheck, 1.0F);
   }

   public static boolean rayTracePlaceCheck(BlockPos pos) {
      return rayTracePlaceCheck(pos, true);
   }

   public static Block getBlock(BlockPos pos) {
      return getState(pos).func_177230_c();
   }

   public static Block getBlock(double x, double y, double z) {
      return mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
   }

   public static boolean canBeClicked(BlockPos pos) {
      return getBlock(pos).func_176209_a(getState(pos), false);
   }

   public static boolean canBeClicked(Vec3d vec3d) {
      return getBlock(new BlockPos(vec3d)).func_176209_a(getState(new BlockPos(vec3d)), false);
   }

   public static void faceVectorPacketInstant(Vec3d vec, Boolean roundAngles) {
      float[] rotations = getNeededRotations2(vec);
      mc.field_71439_g.field_71174_a.func_147297_a(new Rotation(rotations[0], roundAngles ? (float)MathHelper.func_180184_b((int)rotations[1], 360) : rotations[1], mc.field_71439_g.field_70122_E));
   }

   public static void faceVectorPacketInstant2(Vec3d vec) {
      float[] rotations = getLegitRotations(vec);
      Wrapper.getPlayer().field_71174_a.func_147297_a(new Rotation(rotations[0], rotations[1], Wrapper.getPlayer().field_70122_E));
   }

   public static float[] getLegitRotations(Vec3d vec) {
      Vec3d eyesPos = getEyesPos();
      double diffX = vec.field_72450_a - eyesPos.field_72450_a;
      double diffY = vec.field_72448_b - eyesPos.field_72448_b;
      double diffZ = vec.field_72449_c - eyesPos.field_72449_c;
      double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
      float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
      float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
      return new float[]{Wrapper.getPlayer().field_70177_z + MathHelper.func_76142_g(yaw - Wrapper.getPlayer().field_70177_z), Wrapper.getPlayer().field_70125_A + MathHelper.func_76142_g(pitch - Wrapper.getPlayer().field_70125_A)};
   }

   private static float[] getNeededRotations2(Vec3d vec) {
      Vec3d eyesPos = getEyesPos();
      double diffX = vec.field_72450_a - eyesPos.field_72450_a;
      double diffY = vec.field_72448_b - eyesPos.field_72448_b;
      double diffZ = vec.field_72449_c - eyesPos.field_72449_c;
      double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
      float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
      float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
      return new float[]{mc.field_71439_g.field_70177_z + MathHelper.func_76142_g(yaw - mc.field_71439_g.field_70177_z), mc.field_71439_g.field_70125_A + MathHelper.func_76142_g(pitch - mc.field_71439_g.field_70125_A)};
   }

   public static Vec3d getEyesPos() {
      return new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double)mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v);
   }

   public static double blockDistance(double blockposx, double blockposy, double blockposz, Entity owo) {
      double deltaX = owo.field_70165_t - blockposx;
      double deltaY = owo.field_70163_u - blockposy;
      double deltaZ = owo.field_70161_v - blockposz;
      return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
   }

   public static List<BlockPos> getCircle(BlockPos loc, int y, float r, boolean hollow) {
      List<BlockPos> circleblocks = new ArrayList();
      int cx = loc.func_177958_n();
      int cz = loc.func_177952_p();

      for(int x = cx - (int)r; (float)x <= (float)cx + r; ++x) {
         for(int z = cz - (int)r; (float)z <= (float)cz + r; ++z) {
            double dist = (double)((cx - x) * (cx - x) + (cz - z) * (cz - z));
            if (dist < (double)(r * r) && (!hollow || dist >= (double)((r - 1.0F) * (r - 1.0F)))) {
               BlockPos l = new BlockPos(x, y, z);
               circleblocks.add(l);
            }
         }
      }

      return circleblocks;
   }

   public static EnumFacing getPlaceableSide(BlockPos pos) {
      EnumFacing[] var1 = EnumFacing.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         EnumFacing side = var1[var3];
         BlockPos neighbour = pos.func_177972_a(side);
         if (mc.field_71441_e.func_180495_p(neighbour).func_177230_c().func_176209_a(mc.field_71441_e.func_180495_p(neighbour), false)) {
            IBlockState blockState = mc.field_71441_e.func_180495_p(neighbour);
            if (!blockState.func_185904_a().func_76222_j()) {
               return side;
            }
         }
      }

      return null;
   }

   public static EnumFacing getPlaceableSideExlude(BlockPos pos, ArrayList<EnumFacing> excluding) {
      EnumFacing[] var2 = EnumFacing.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnumFacing side = var2[var4];
         if (!excluding.contains(side)) {
            BlockPos neighbour = pos.func_177972_a(side);
            if (mc.field_71441_e.func_180495_p(neighbour).func_177230_c().func_176209_a(mc.field_71441_e.func_180495_p(neighbour), false)) {
               IBlockState blockState = mc.field_71441_e.func_180495_p(neighbour);
               if (!blockState.func_185904_a().func_76222_j()) {
                  return side;
               }
            }
         }
      }

      return null;
   }

   public static Vec3d getCenterOfBlock(double playerX, double playerY, double playerZ) {
      double newX = Math.floor(playerX) + 0.5D;
      double newY = Math.floor(playerY);
      double newZ = Math.floor(playerZ) + 0.5D;
      return new Vec3d(newX, newY, newZ);
   }

   static {
      shulkerList = Arrays.asList(Blocks.field_190977_dl, Blocks.field_190978_dm, Blocks.field_190979_dn, Blocks.field_190980_do, Blocks.field_190981_dp, Blocks.field_190982_dq, Blocks.field_190983_dr, Blocks.field_190984_ds, Blocks.field_190985_dt, Blocks.field_190986_du, Blocks.field_190987_dv, Blocks.field_190988_dw, Blocks.field_190989_dx, Blocks.field_190990_dy, Blocks.field_190991_dz, Blocks.field_190975_dA);
      blackList = Arrays.asList(Blocks.field_150486_ae, Blocks.field_150447_bR, Blocks.field_150477_bB, Blocks.field_150467_bQ, Blocks.field_150471_bO, Blocks.field_150430_aB, Blocks.field_150441_bU, Blocks.field_150413_aR, Blocks.field_150416_aS, Blocks.field_150455_bV, Blocks.field_180390_bo, Blocks.field_180391_bp, Blocks.field_180392_bq, Blocks.field_180386_br, Blocks.field_180385_bs, Blocks.field_180387_bt, Blocks.field_150382_bo, Blocks.field_150367_z, Blocks.field_150409_cd, Blocks.field_150442_at, Blocks.field_150323_B, Blocks.field_150421_aI, Blocks.field_150461_bJ, Blocks.field_150324_C, Blocks.field_150460_al, Blocks.field_180413_ao, Blocks.field_180414_ap, Blocks.field_180412_aq, Blocks.field_180411_ar, Blocks.field_180410_as, Blocks.field_180409_at, Blocks.field_150414_aQ, Blocks.field_150381_bn, Blocks.field_150380_bt, Blocks.field_150438_bZ, Blocks.field_185776_dc, Blocks.field_150483_bI, Blocks.field_185777_dd, Blocks.field_150462_ai, Blocks.field_150444_as, Blocks.field_150472_an, shulkerList);
      unSolidBlocks = Arrays.asList(Blocks.field_150433_aE, Blocks.field_150404_cg, Blocks.field_185764_cQ, Blocks.field_150465_bP, Blocks.field_150457_bL, Blocks.field_150473_bD, Blocks.field_150479_bC, Blocks.field_150468_ap, Blocks.field_150437_az, Blocks.field_150488_af, Blocks.field_150350_a, Blocks.field_150427_aO, Blocks.field_150384_bq, Blocks.field_150355_j, Blocks.field_150358_i, Blocks.field_150353_l, Blocks.field_150356_k, Blocks.field_150345_g, Blocks.field_150328_O, Blocks.field_150327_N, Blocks.field_150338_P, Blocks.field_150337_Q, Blocks.field_150464_aj, Blocks.field_150459_bM, Blocks.field_150469_bN, Blocks.field_185773_cZ, Blocks.field_150436_aH, Blocks.field_150393_bb, Blocks.field_150394_bc, Blocks.field_150392_bi, Blocks.field_150388_bm, Blocks.field_150375_by, Blocks.field_185766_cS, Blocks.field_185765_cR, Blocks.field_150329_H, Blocks.field_150330_I, Blocks.field_150395_bd, Blocks.field_150480_ab, Blocks.field_150448_aq, Blocks.field_150408_cc, Blocks.field_150319_E, Blocks.field_150318_D, Blocks.field_150478_aa, Blocks.field_150429_aA, Blocks.field_150321_G, Blocks.field_150332_K, Blocks.field_180384_M, Blocks.field_150331_J, Blocks.field_150320_F, Blocks.field_150486_ae, Blocks.field_150447_bR, Blocks.field_150477_bB, Blocks.field_150467_bQ, Blocks.field_150471_bO, Blocks.field_150430_aB, Blocks.field_150441_bU, Blocks.field_150413_aR, Blocks.field_150416_aS, Blocks.field_150455_bV, Blocks.field_180390_bo, Blocks.field_180391_bp, Blocks.field_180392_bq, Blocks.field_180386_br, Blocks.field_180385_bs, Blocks.field_180387_bt, Blocks.field_150382_bo, Blocks.field_150367_z, Blocks.field_150409_cd, Blocks.field_150442_at, Blocks.field_150323_B, Blocks.field_150421_aI, Blocks.field_150461_bJ, Blocks.field_150324_C, Blocks.field_150460_al, Blocks.field_180413_ao, Blocks.field_180414_ap, Blocks.field_180412_aq, Blocks.field_180411_ar, Blocks.field_180410_as, Blocks.field_180409_at, Blocks.field_150414_aQ, Blocks.field_150381_bn, Blocks.field_150380_bt, shulkerList);
      airBlocks = Arrays.asList(Blocks.field_150350_a, Blocks.field_150353_l, Blocks.field_150356_k, Blocks.field_150355_j, Blocks.field_150358_i, Blocks.field_150480_ab, Blocks.field_150395_bd, Blocks.field_150431_aC, Blocks.field_150329_H);
      mc = Minecraft.func_71410_x();
      facing = new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.WEST, EnumFacing.EAST};
   }
}

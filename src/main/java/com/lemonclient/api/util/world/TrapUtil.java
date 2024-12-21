package com.lemonclient.api.util.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import jdk.nashorn.internal.objects.NativeMath;
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
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;

public class TrapUtil {
   private static final Minecraft mc = Minecraft.func_71410_x();
   public static final Vec3d[] antiDropOffsetList = new Vec3d[]{new Vec3d(0.0D, -2.0D, 0.0D)};
   public static final Vec3d[] platformOffsetList = new Vec3d[]{new Vec3d(0.0D, -1.0D, 0.0D), new Vec3d(0.0D, -1.0D, -1.0D), new Vec3d(0.0D, -1.0D, 1.0D), new Vec3d(-1.0D, -1.0D, 0.0D), new Vec3d(1.0D, -1.0D, 0.0D)};
   public static final Vec3d[] legOffsetList = new Vec3d[]{new Vec3d(-1.0D, 0.0D, 0.0D), new Vec3d(1.0D, 0.0D, 0.0D), new Vec3d(0.0D, 0.0D, -1.0D), new Vec3d(0.0D, 0.0D, 1.0D)};
   public static final Vec3d[] OffsetList = new Vec3d[]{new Vec3d(1.0D, 1.0D, 0.0D), new Vec3d(-1.0D, 1.0D, 0.0D), new Vec3d(0.0D, 1.0D, 1.0D), new Vec3d(0.0D, 1.0D, -1.0D), new Vec3d(0.0D, 2.0D, 0.0D)};
   public static final Vec3d[] antiStepOffsetList = new Vec3d[]{new Vec3d(-1.0D, 2.0D, 0.0D), new Vec3d(1.0D, 2.0D, 0.0D), new Vec3d(0.0D, 2.0D, 1.0D), new Vec3d(0.0D, 2.0D, -1.0D)};
   public static final Vec3d[] antiScaffoldOffsetList = new Vec3d[]{new Vec3d(0.0D, 3.0D, 0.0D)};

   public static void placeBlock(BlockPos pos) {
      EnumFacing[] var1 = EnumFacing.field_82609_l;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         EnumFacing side = var1[var3];
         BlockPos neighbor = pos.func_177972_a(side);
         IBlockState neighborState = mc.field_71441_e.func_180495_p(neighbor);
         if (neighborState.func_177230_c().func_176209_a(neighborState, false)) {
            boolean sneak = !mc.field_71439_g.func_70093_af() && neighborState.func_177230_c().func_180639_a(mc.field_71441_e, pos, mc.field_71441_e.func_180495_p(pos), mc.field_71439_g, EnumHand.MAIN_HAND, side, 0.5F, 0.5F, 0.5F);
            if (sneak) {
               mc.func_147114_u().func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
            }

            mc.func_147114_u().func_147297_a(new CPacketPlayerTryUseItemOnBlock(neighbor, side.func_176734_d(), EnumHand.MAIN_HAND, 0.5F, 0.5F, 0.5F));
            mc.func_147114_u().func_147297_a(new CPacketAnimation(EnumHand.MAIN_HAND));
            if (sneak) {
               mc.func_147114_u().func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
            }
         }
      }

   }

   public static boolean canPlaceCrystal(BlockPos pos, boolean checkSecond) {
      Chunk chunk = mc.field_71441_e.func_175726_f(pos);
      Block block = chunk.func_177435_g(pos).func_177230_c();
      if (block != Blocks.field_150357_h && block != Blocks.field_150343_Z) {
         return false;
      } else {
         BlockPos boost = pos.func_177967_a(EnumFacing.UP, 1);
         return chunk.func_177435_g(boost).func_177230_c() == Blocks.field_150350_a && chunk.func_177435_g(pos.func_177967_a(EnumFacing.UP, 2)).func_177230_c() == Blocks.field_150350_a ? mc.field_71441_e.func_175647_a(Entity.class, new AxisAlignedBB((double)boost.func_177958_n(), (double)boost.func_177956_o(), (double)boost.func_177952_p(), (double)(boost.func_177958_n() + 1), (double)(boost.func_177956_o() + (checkSecond ? 2 : 1)), (double)(boost.func_177952_p() + 1)), (e) -> {
            return !(e instanceof EntityEnderCrystal);
         }).isEmpty() : false;
      }
   }

   public static List<BlockPos> getSphere(float radius) {
      List<BlockPos> sphere = new ArrayList();
      BlockPos pos = new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v);
      int posX = pos.func_177958_n();
      int posY = pos.func_177956_o();
      int posZ = pos.func_177952_p();

      for(int x = posX - (int)radius; (float)x <= (float)posX + radius; ++x) {
         for(int z = posZ - (int)radius; (float)z <= (float)posZ + radius; ++z) {
            for(int y = posY - (int)radius; (float)y < (float)posY + radius; ++y) {
               if ((float)((posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y)) < radius * radius) {
                  sphere.add(new BlockPos(x, y, z));
               }
            }
         }
      }

      return sphere;
   }

   public static int isPositionPlaceable(BlockPos pos, boolean entityCheck) {
      try {
         Block block = mc.field_71441_e.func_180495_p(pos).func_177230_c();
         if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow)) {
            return 0;
         } else {
            Iterator var3;
            if (entityCheck) {
               var3 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(pos)).iterator();

               while(var3.hasNext()) {
                  Entity entity = (Entity)var3.next();
                  if (!entity.field_70128_L && !(entity instanceof EntityItem) && !(entity instanceof EntityXPOrb)) {
                     return 1;
                  }
               }
            }

            var3 = getPossibleSides(pos).iterator();

            EnumFacing side;
            do {
               if (!var3.hasNext()) {
                  return 2;
               }

               side = (EnumFacing)var3.next();
            } while(!canBeClicked(pos.func_177972_a(side)));

            return 3;
         }
      } catch (Exception var5) {
         var5.printStackTrace();
         return 0;
      }
   }

   public static boolean canBeClicked(BlockPos pos) {
      return getBlock(pos).func_176209_a(getState(pos), false);
   }

   private static Block getBlock(BlockPos pos) {
      return getState(pos).func_177230_c();
   }

   private static IBlockState getState(BlockPos pos) {
      return mc.field_71441_e.func_180495_p(pos);
   }

   public static List<EnumFacing> getPossibleSides(BlockPos pos) {
      List<EnumFacing> facings = new ArrayList(6);
      EnumFacing[] var2 = EnumFacing.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnumFacing side = var2[var4];
         BlockPos neighbour = pos.func_177972_a(side);
         if (mc.field_71441_e.func_180495_p(neighbour).func_177230_c().func_176209_a(mc.field_71441_e.func_180495_p(neighbour), false)) {
            IBlockState blockState = mc.field_71441_e.func_180495_p(neighbour);
            if (!blockState.func_185904_a().func_76222_j()) {
               facings.add(side);
            }
         }
      }

      return facings;
   }

   public static Vec3d[] getHelpingBlocks(Vec3d vec3d) {
      return new Vec3d[]{new Vec3d(vec3d.field_72450_a, vec3d.field_72448_b - 1.0D, vec3d.field_72449_c), new Vec3d(vec3d.field_72450_a != 0.0D ? vec3d.field_72450_a * 2.0D : vec3d.field_72450_a, vec3d.field_72448_b, vec3d.field_72450_a != 0.0D ? vec3d.field_72449_c : vec3d.field_72449_c * 2.0D), new Vec3d(vec3d.field_72450_a == 0.0D ? vec3d.field_72450_a + 1.0D : vec3d.field_72450_a, vec3d.field_72448_b, vec3d.field_72450_a == 0.0D ? vec3d.field_72449_c : vec3d.field_72449_c + 1.0D), new Vec3d(vec3d.field_72450_a == 0.0D ? vec3d.field_72450_a - 1.0D : vec3d.field_72450_a, vec3d.field_72448_b, vec3d.field_72450_a == 0.0D ? vec3d.field_72449_c : vec3d.field_72449_c - 1.0D), new Vec3d(vec3d.field_72450_a, vec3d.field_72448_b + 1.0D, vec3d.field_72449_c)};
   }

   public static List<Vec3d> getOffsetList(int y, boolean floor) {
      List<Vec3d> offsets = new ArrayList(5);
      offsets.add(new Vec3d(-1.0D, (double)y, 0.0D));
      offsets.add(new Vec3d(1.0D, (double)y, 0.0D));
      offsets.add(new Vec3d(0.0D, (double)y, -1.0D));
      offsets.add(new Vec3d(0.0D, (double)y, 1.0D));
      if (floor) {
         offsets.add(new Vec3d(0.0D, (double)(y - 1), 0.0D));
      }

      return offsets;
   }

   public static Vec3d[] getOffsets(int y, boolean floor) {
      List<Vec3d> offsets = getOffsetList(y, floor);
      Vec3d[] array = new Vec3d[offsets.size()];
      return (Vec3d[])offsets.toArray(array);
   }

   public static Vec3d[] getUnsafeBlockArray(Entity entity, int height, boolean floor) {
      List<Vec3d> list = getUnsafeBlocks(entity, height, floor);
      Vec3d[] array = new Vec3d[list.size()];
      return (Vec3d[])list.toArray(array);
   }

   public static boolean isSafe(Entity entity, int height, boolean floor) {
      return getUnsafeBlocks(entity, height, floor).size() == 0;
   }

   public static List<Vec3d> getUnsafeBlocks(Entity entity, int height, boolean floor) {
      return getUnsafeBlocksFromVec3d(entity.func_174791_d(), height, floor);
   }

   public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
      List<Vec3d> vec3ds = new ArrayList(5);
      Vec3d[] var4 = getOffsets(height, floor);
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Vec3d vector = var4[var6];
         Block block = mc.field_71441_e.func_180495_p((new BlockPos(pos)).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c)).func_177230_c();
         if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) {
            vec3ds.add(vector);
         }
      }

      return vec3ds;
   }

   public static Vec3d[] getTrapOffsets(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
      List<Vec3d> offsets = getTrapOffsetsList(antiScaffold, antiStep, legs, platform, antiDrop);
      Vec3d[] array = new Vec3d[offsets.size()];
      return (Vec3d[])offsets.toArray(array);
   }

   public static List<Vec3d> getTrapOffsetsList(boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
      List<Vec3d> offsets = new ArrayList(getOffsetList(1, false));
      offsets.add(new Vec3d(0.0D, 2.0D, 0.0D));
      if (antiScaffold) {
         offsets.add(new Vec3d(0.0D, 3.0D, 0.0D));
      }

      if (antiStep) {
         offsets.addAll(getOffsetList(2, false));
      }

      if (legs) {
         offsets.addAll(getOffsetList(0, false));
      }

      if (platform) {
         offsets.addAll(getOffsetList(-1, false));
         offsets.add(new Vec3d(0.0D, -1.0D, 0.0D));
      }

      if (antiDrop) {
         offsets.add(new Vec3d(0.0D, -2.0D, 0.0D));
      }

      return offsets;
   }

   public static boolean isTrapped(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
      return getUntrappedBlocks(player, antiScaffold, antiStep, legs, platform, antiDrop).size() == 0;
   }

   public static boolean isTrappedExtended(int extension, EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
      return getUntrappedBlocksExtended(extension, player, antiScaffold, antiStep, legs, platform, antiDrop, raytrace).size() == 0;
   }

   public static List<Vec3d> getBlockBlocks(Entity entity) {
      List<Vec3d> vec3ds = new ArrayList(8);
      AxisAlignedBB bb = entity.func_174813_aQ();
      double y = entity.field_70163_u;
      double minX = NativeMath.round(bb.field_72340_a, 0);
      double minZ = NativeMath.round(bb.field_72339_c, 0);
      double maxX = NativeMath.round(bb.field_72336_d, 0);
      double maxZ = NativeMath.round(bb.field_72334_f, 0);
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

   public static List<Vec3d> getUntrappedBlocksExtended(int extension, EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
      List<Vec3d> placeTargets = new ArrayList();
      Iterator var10;
      Vec3d vec3d;
      if (extension == 1) {
         placeTargets.addAll(targets(player.func_174791_d(), antiScaffold, antiStep, legs, platform, antiDrop, raytrace));
      } else {
         int extend = 1;

         for(var10 = getBlockBlocks(player).iterator(); var10.hasNext(); ++extend) {
            vec3d = (Vec3d)var10.next();
            if (extend > extension) {
               break;
            }

            placeTargets.addAll(targets(vec3d, antiScaffold, antiStep, legs, platform, antiDrop, raytrace));
         }
      }

      List<Vec3d> removeList = new ArrayList();
      var10 = placeTargets.iterator();

      while(var10.hasNext()) {
         vec3d = (Vec3d)var10.next();
         BlockPos pos = new BlockPos(vec3d);
         if (isPositionPlaceable(pos, raytrace) == -1) {
            removeList.add(vec3d);
         }
      }

      var10 = removeList.iterator();

      while(var10.hasNext()) {
         vec3d = (Vec3d)var10.next();
         placeTargets.remove(vec3d);
      }

      return placeTargets;
   }

   public static List<Vec3d> getUntrappedBlocks(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop) {
      List<Vec3d> vec3ds = new ArrayList();
      if (!antiStep && getUnsafeBlocks(player, 2, false).size() == 4) {
         vec3ds.addAll(getUnsafeBlocks(player, 2, false));
      }

      Vec3d[] trapOffsets = getTrapOffsets(antiScaffold, antiStep, legs, platform, antiDrop);

      for(int i = 0; i < trapOffsets.length; ++i) {
         Vec3d vector = trapOffsets[i];
         BlockPos targetPos = (new BlockPos(player.func_174791_d())).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c);
         Block block = mc.field_71441_e.func_180495_p(targetPos).func_177230_c();
         if (block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow) {
            vec3ds.add(vector);
         }
      }

      return vec3ds;
   }

   public static List<Vec3d> targets(Vec3d vec3d, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean raytrace) {
      List<Vec3d> placeTargets = new ArrayList();
      if (antiDrop) {
         Collections.addAll(placeTargets, convertVec3ds(vec3d, antiDropOffsetList));
      }

      if (platform) {
         Collections.addAll(placeTargets, convertVec3ds(vec3d, platformOffsetList));
      }

      if (legs) {
         Collections.addAll(placeTargets, convertVec3ds(vec3d, legOffsetList));
      }

      Collections.addAll(placeTargets, convertVec3ds(vec3d, OffsetList));
      if (antiStep) {
         Collections.addAll(placeTargets, convertVec3ds(vec3d, antiStepOffsetList));
      } else {
         List<Vec3d> vec3ds = getUnsafeBlocksFromVec3d(vec3d, 2, false);
         if (vec3ds.size() == 4) {
            Iterator var9 = vec3ds.iterator();

            label35:
            while(var9.hasNext()) {
               Vec3d vector = (Vec3d)var9.next();
               BlockPos position = (new BlockPos(vec3d)).func_177963_a(vector.field_72450_a, vector.field_72448_b, vector.field_72449_c);
               switch(isPositionPlaceable(position, raytrace)) {
               case -1:
               case 1:
               case 2:
                  break;
               case 0:
               default:
                  break label35;
               case 3:
                  placeTargets.add(vec3d.func_178787_e(vector));
                  break label35;
               }
            }
         }
      }

      if (antiScaffold) {
         Collections.addAll(placeTargets, convertVec3ds(vec3d, antiScaffoldOffsetList));
      }

      return placeTargets;
   }

   public static Vec3d[] convertVec3ds(Vec3d vec3d, Vec3d[] input) {
      Vec3d[] output = new Vec3d[input.length];
      int length = input.length;

      for(int i = 0; i < length; ++i) {
         output[i] = vec3d.func_178787_e(input[i]);
      }

      return output;
   }
}

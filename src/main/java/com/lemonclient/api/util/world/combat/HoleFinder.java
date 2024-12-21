package com.lemonclient.api.util.world.combat;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class HoleFinder {
   private static final Minecraft mc = Minecraft.func_71410_x();
   private static final Vec3i[] OFFSETS_2x2 = new Vec3i[]{new Vec3i(0, 0, 0), new Vec3i(1, 0, 0), new Vec3i(0, 0, 1), new Vec3i(1, 0, 1)};
   public static final Set<Block> NO_BLAST;
   public static final Set<Block> UNSAFE;

   public static boolean isAir(BlockPos pos) {
      return mc.field_71441_e.func_175623_d(pos);
   }

   public static boolean[] isHole(BlockPos pos, boolean above) {
      boolean[] result = new boolean[]{false, true};
      return isAir(pos) && isAir(pos.func_177984_a()) && (!above || isAir(pos.func_177981_b(2))) ? is1x1(pos, result) : result;
   }

   public static boolean[] is1x1(BlockPos pos) {
      return is1x1(pos, new boolean[]{false, true});
   }

   public static boolean[] is1x1(BlockPos pos, boolean[] result) {
      EnumFacing[] var2 = EnumFacing.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnumFacing facing = var2[var4];
         if (facing != EnumFacing.UP) {
            BlockPos offset = pos.func_177972_a(facing);
            IBlockState state = mc.field_71441_e.func_180495_p(offset);
            if (state.func_177230_c() != Blocks.field_150357_h) {
               if (!NO_BLAST.contains(state.func_177230_c())) {
                  return result;
               }

               result[1] = false;
            }
         }
      }

      result[0] = true;
      return result;
   }

   public static boolean is2x1(BlockPos pos) {
      return is2x1(pos, true);
   }

   public static boolean is2x1(BlockPos pos, boolean upper) {
      if (!upper || isAir(pos) && isAir(pos.func_177984_a()) && !isAir(pos.func_177977_b())) {
         int airBlocks = 0;
         EnumFacing[] var3 = EnumFacing.field_176754_o;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            EnumFacing facing = var3[var5];
            BlockPos offset = pos.func_177972_a(facing);
            if (isAir(offset)) {
               if (!isAir(offset.func_177984_a())) {
                  return false;
               }

               if (isAir(offset.func_177977_b())) {
                  return false;
               }

               EnumFacing[] var8 = EnumFacing.field_176754_o;
               int var9 = var8.length;

               for(int var10 = 0; var10 < var9; ++var10) {
                  EnumFacing offsetFacing = var8[var10];
                  if (offsetFacing != facing.func_176734_d()) {
                     IBlockState state = mc.field_71441_e.func_180495_p(offset.func_177972_a(offsetFacing));
                     if (!NO_BLAST.contains(state.func_177230_c())) {
                        return false;
                     }
                  }
               }

               ++airBlocks;
            }

            if (airBlocks > 1) {
               return false;
            }
         }

         return airBlocks == 1;
      } else {
         return false;
      }
   }

   public static boolean is2x2Partial(BlockPos pos) {
      Set<BlockPos> positions = new HashSet();
      Vec3i[] var2 = OFFSETS_2x2;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Vec3i vec = var2[var4];
         positions.add(pos.func_177971_a(vec));
      }

      boolean airBlock = false;
      Iterator var12 = positions.iterator();

      while(var12.hasNext()) {
         BlockPos holePos = (BlockPos)var12.next();
         if (!isAir(holePos) || !isAir(holePos.func_177984_a()) || isAir(holePos.func_177977_b())) {
            return false;
         }

         if (isAir(holePos.func_177981_b(2))) {
            airBlock = true;
         }

         EnumFacing[] var14 = EnumFacing.field_176754_o;
         int var6 = var14.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            EnumFacing facing = var14[var7];
            BlockPos offset = holePos.func_177972_a(facing);
            if (!positions.contains(offset)) {
               IBlockState state = mc.field_71441_e.func_180495_p(offset);
               if (!NO_BLAST.contains(state.func_177230_c())) {
                  return false;
               }
            }
         }
      }

      return airBlock;
   }

   public static boolean is2x2(BlockPos pos) {
      return is2x2(pos, true);
   }

   public static boolean is2x2(BlockPos pos, boolean upper) {
      if (upper && !isAir(pos)) {
         return false;
      } else if (is2x2Partial(pos)) {
         return true;
      } else {
         BlockPos l = pos.func_177982_a(-1, 0, 0);
         boolean airL = isAir(l);
         if (airL && is2x2Partial(l)) {
            return true;
         } else {
            BlockPos r = pos.func_177982_a(0, 0, -1);
            boolean airR = isAir(r);
            if (airR && is2x2Partial(r)) {
               return true;
            } else {
               return (airL || airR) && is2x2Partial(pos.func_177982_a(-1, 0, -1));
            }
         }
      }
   }

   public static boolean is2x2single(BlockPos pos, boolean upper) {
      return upper && !isAir(pos) ? false : is2x2Partial(pos);
   }

   static {
      NO_BLAST = Sets.newHashSet(new Block[]{Blocks.field_150357_h, Blocks.field_150343_Z, Blocks.field_150467_bQ, Blocks.field_150477_bB});
      UNSAFE = Sets.newHashSet(new Block[]{Blocks.field_150343_Z, Blocks.field_150467_bQ, Blocks.field_150477_bB});
   }
}

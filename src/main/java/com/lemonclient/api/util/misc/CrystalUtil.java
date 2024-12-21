package com.lemonclient.api.util.misc;

import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.BlockUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;

public class CrystalUtil {
   public static Minecraft mc = Minecraft.func_71410_x();
   private static final List<Block> valid;

   public static void placeCrystal(BlockPos pos, boolean rotate) {
      boolean offhand = mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP;
      BlockPos obsPos = pos.func_177977_b();
      RayTraceResult result = mc.field_71441_e.func_72933_a(new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double)mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v), new Vec3d((double)pos.func_177958_n() + 0.5D, (double)pos.func_177956_o() - 0.5D, (double)pos.func_177952_p() + 0.5D));
      EnumFacing facing = result != null && result.field_178784_b != null ? result.field_178784_b : EnumFacing.UP;
      EnumFacing opposite = facing.func_176734_d();
      Vec3d vec = (new Vec3d(obsPos)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e(new Vec3d(opposite.func_176730_m()));
      if (rotate) {
         BlockUtil.faceVector(vec);
      }

      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(obsPos, facing, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
      mc.field_71439_g.func_184609_a(offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
   }

   public static boolean placeCrystal(BlockPos pos, EnumHand hand, boolean packet, boolean rotate, boolean swing) {
      EnumFacing facing = EnumFacing.UP;
      EnumFacing opposite = facing.func_176734_d();
      Vec3d vec = (new Vec3d(pos)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e(new Vec3d(opposite.func_176730_m()));
      if (rotate) {
         BlockUtil.faceVector(vec);
      }

      if (packet) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, 0.0F, 0.0F, 0.0F));
      } else {
         mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, pos, facing, vec, hand);
      }

      if (swing) {
         mc.field_71439_g.func_184609_a(hand);
      }

      return true;
   }

   public static boolean isNull(RayTraceResult result, Entity entity) {
      return result == null || result.field_178784_b == null || result.field_72308_g == entity;
   }

   public static boolean calculateRaytrace(Entity entity) {
      if (entity == null) {
         return true;
      } else {
         Vec3d vec = PlayerUtil.getEyeVec();
         Vec3d vec3d = entity.func_174791_d();
         RayTraceResult result = mc.field_71441_e.func_72933_a(vec, vec3d);
         if (isNull(result, entity)) {
            return true;
         } else {
            double x = entity.field_70121_D.field_72336_d - entity.field_70121_D.field_72340_a;
            double y = entity.field_70121_D.field_72337_e - entity.field_70121_D.field_72338_b;
            double z = entity.field_70121_D.field_72334_f - entity.field_70121_D.field_72339_c;

            for(double addX = -x; addX <= x; addX += x) {
               for(double addY = 0.0D; addY <= y; addY += y) {
                  for(double addZ = -z; addZ <= z; addZ += z) {
                     result = mc.field_71441_e.func_72933_a(vec, vec3d.func_72441_c(addX, addY, addZ));
                     if (isNull(result, entity)) {
                        return true;
                     }
                  }
               }
            }

            return false;
         }
      }
   }

   public static boolean isNull(RayTraceResult result, BlockPos pos) {
      if (result != null && result.func_178782_a() != pos) {
         if (result.field_72313_a == Type.ENTITY) {
            double distance = (double)mc.field_71439_g.func_70032_d(result.field_72308_g);
            return distance <= PlayerUtil.getDistanceI(pos);
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public static boolean calculateRaytrace(BlockPos pos) {
      Vec3d vec = PlayerUtil.getEyeVec();
      Vec3d vec3d = new Vec3d(pos);
      RayTraceResult result = mc.field_71441_e.func_72933_a(vec, vec3d);
      if (isNull(result, pos)) {
         return true;
      } else {
         double x = 0.5D;
         double y = 0.5D;
         double z = 0.5D;

         for(double addX = 0.0D; addX <= 1.0D; addX += x) {
            for(double addY = 0.0D; addY <= 1.0D; addY += y) {
               for(double addZ = 0.0D; addZ <= 1.0D; addZ += z) {
                  result = mc.field_71441_e.func_72933_a(vec, vec3d.func_72441_c(addX, addY, addZ));
                  if (isNull(result, pos)) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   public static boolean calculateRaytrace(EntityPlayer player, BlockPos pos) {
      Vec3d vec = new Vec3d(player.field_70165_t, player.field_70163_u + (double)player.func_70047_e(), player.field_70161_v);
      Vec3d vec3d = new Vec3d(pos);
      RayTraceResult result = mc.field_71441_e.func_72933_a(vec, vec3d);
      if (isNull(result, pos)) {
         return true;
      } else {
         double x = 0.5D;
         double y = 0.5D;
         double z = 0.5D;

         for(double addX = 0.0D; addX <= 1.0D; addX += x) {
            for(double addY = 0.0D; addY <= 1.0D; addY += y) {
               for(double addZ = 0.0D; addZ <= 1.0D; addZ += z) {
                  result = mc.field_71441_e.func_72933_a(vec, vec3d.func_72441_c(addX, addY, addZ));
                  if (isNull(result, pos)) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   public static RayTraceResult rayTraceBlocks(Vec3d start, Vec3d end) {
      return rayTraceBlocks(start, end, false, false, false);
   }

   public static RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUnCollidableBlock) {
      if (!Double.isNaN(vec31.field_72450_a) && !Double.isNaN(vec31.field_72448_b) && !Double.isNaN(vec31.field_72449_c)) {
         if (!Double.isNaN(vec32.field_72450_a) && !Double.isNaN(vec32.field_72448_b) && !Double.isNaN(vec32.field_72449_c)) {
            int i = MathHelper.func_76128_c(vec32.field_72450_a);
            int j = MathHelper.func_76128_c(vec32.field_72448_b);
            int k = MathHelper.func_76128_c(vec32.field_72449_c);
            int l = MathHelper.func_76128_c(vec31.field_72450_a);
            int i1;
            int j1;
            BlockPos blockpos = new BlockPos(l, i1 = MathHelper.func_76128_c(vec31.field_72448_b), j1 = MathHelper.func_76128_c(vec31.field_72449_c));
            IBlockState iblockstate = mc.field_71441_e.func_180495_p(blockpos);
            Block block = iblockstate.func_177230_c();
            if (!valid.contains(block)) {
               block = Blocks.field_150350_a;
               iblockstate = Blocks.field_150350_a.func_176194_O().func_177621_b();
            }

            if ((!ignoreBlockWithoutBoundingBox || iblockstate.func_185890_d(mc.field_71441_e, blockpos) != Block.field_185506_k) && block.func_176209_a(iblockstate, stopOnLiquid)) {
               return iblockstate.func_185910_a(mc.field_71441_e, blockpos, vec31, vec32);
            } else {
               RayTraceResult raytraceresult2 = null;
               int var15 = 200;

               while(var15-- >= 0) {
                  if (Double.isNaN(vec31.field_72450_a) || Double.isNaN(vec31.field_72448_b) || Double.isNaN(vec31.field_72449_c)) {
                     return null;
                  }

                  if (l == i && i1 == j && j1 == k) {
                     return returnLastUnCollidableBlock ? raytraceresult2 : null;
                  }

                  boolean flag2 = true;
                  boolean flag = true;
                  boolean flag1 = true;
                  double d0 = 999.0D;
                  double d1 = 999.0D;
                  double d2 = 999.0D;
                  if (i > l) {
                     d0 = (double)l + 1.0D;
                  } else if (i < l) {
                     d0 = (double)l + 0.0D;
                  } else {
                     flag2 = false;
                  }

                  if (j > i1) {
                     d1 = (double)i1 + 1.0D;
                  } else if (j < i1) {
                     d1 = (double)i1 + 0.0D;
                  } else {
                     flag = false;
                  }

                  if (k > j1) {
                     d2 = (double)j1 + 1.0D;
                  } else if (k < j1) {
                     d2 = (double)j1 + 0.0D;
                  } else {
                     flag1 = false;
                  }

                  double d3 = 999.0D;
                  double d4 = 999.0D;
                  double d5 = 999.0D;
                  double d6 = vec32.field_72450_a - vec31.field_72450_a;
                  double d7 = vec32.field_72448_b - vec31.field_72448_b;
                  double d8 = vec32.field_72449_c - vec31.field_72449_c;
                  if (flag2) {
                     d3 = (d0 - vec31.field_72450_a) / d6;
                  }

                  if (flag) {
                     d4 = (d1 - vec31.field_72448_b) / d7;
                  }

                  if (flag1) {
                     d5 = (d2 - vec31.field_72449_c) / d8;
                  }

                  if (d3 == -0.0D) {
                     d3 = -1.0E-4D;
                  }

                  if (d4 == -0.0D) {
                     d4 = -1.0E-4D;
                  }

                  if (d5 == -0.0D) {
                     d5 = -1.0E-4D;
                  }

                  EnumFacing enumfacing;
                  if (d3 < d4 && d3 < d5) {
                     enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
                     vec31 = new Vec3d(d0, vec31.field_72448_b + d7 * d3, vec31.field_72449_c + d8 * d3);
                  } else if (d4 < d5) {
                     enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
                     vec31 = new Vec3d(vec31.field_72450_a + d6 * d4, d1, vec31.field_72449_c + d8 * d4);
                  } else {
                     enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                     vec31 = new Vec3d(vec31.field_72450_a + d6 * d5, vec31.field_72448_b + d7 * d5, d2);
                  }

                  l = MathHelper.func_76128_c(vec31.field_72450_a) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                  i1 = MathHelper.func_76128_c(vec31.field_72448_b) - (enumfacing == EnumFacing.UP ? 1 : 0);
                  j1 = MathHelper.func_76128_c(vec31.field_72449_c) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                  blockpos = new BlockPos(l, i1, j1);
                  IBlockState iblockstate1 = mc.field_71441_e.func_180495_p(blockpos);
                  Block block1 = iblockstate1.func_177230_c();
                  if (!valid.contains(block1)) {
                     block1 = Blocks.field_150350_a;
                     iblockstate1 = Blocks.field_150350_a.func_176194_O().func_177621_b();
                  }

                  if (!ignoreBlockWithoutBoundingBox || iblockstate1.func_185904_a() == Material.field_151567_E || iblockstate1.func_185890_d(mc.field_71441_e, blockpos) != Block.field_185506_k) {
                     if (block1.func_176209_a(iblockstate1, stopOnLiquid)) {
                        return iblockstate1.func_185910_a(mc.field_71441_e, blockpos, vec31, vec32);
                     }

                     raytraceresult2 = new RayTraceResult(Type.MISS, vec31, enumfacing, blockpos);
                  }
               }

               return returnLastUnCollidableBlock ? raytraceresult2 : null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static boolean canPlaceCrystal(BlockPos pos) {
      return BlockUtil.getBlock(pos.func_177982_a(0, 1, 0)) == Blocks.field_150350_a && BlockUtil.getBlock(pos.func_177982_a(0, 2, 0)) == Blocks.field_150350_a;
   }

   public static List<BlockPos> getSphere(BlockPos pos, float r, int h, boolean hollow, boolean sphere, int plus_y) {
      ArrayList<BlockPos> circleBlocks = new ArrayList();
      int cx = pos.func_177958_n();
      int cy = pos.func_177956_o();
      int cz = pos.func_177952_p();

      for(int x = cx - (int)r; (float)x <= (float)cx + r; ++x) {
         for(int z = cz - (int)r; (float)z <= (float)cz + r; ++z) {
            int y = sphere ? cy - (int)r : cy;

            while(true) {
               float f = (float)y;
               float f2 = sphere ? (float)cy + r : (float)(cy + h);
               if (!(f < f2)) {
                  break;
               }

               double dist = (double)((cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0));
               if (dist < (double)(r * r) && (!hollow || !(dist < (double)((r - 1.0F) * (r - 1.0F))))) {
                  BlockPos l = new BlockPos(x, y + plus_y, z);
                  circleBlocks.add(l);
               }

               ++y;
            }
         }
      }

      return circleBlocks;
   }

   public static boolean canPlaceCrystal(BlockPos blockPos, boolean specialEntityCheck, boolean onepointThirteen) {
      BlockPos boost = blockPos.func_177982_a(0, 1, 0);
      BlockPos boost2 = blockPos.func_177982_a(0, 2, 0);

      try {
         Iterator var5;
         Entity entity;
         if (!onepointThirteen) {
            if (mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150357_h && mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150343_Z) {
               return false;
            }

            if (mc.field_71441_e.func_180495_p(boost).func_177230_c() != Blocks.field_150350_a || mc.field_71441_e.func_180495_p(boost2).func_177230_c() != Blocks.field_150350_a) {
               return false;
            }

            if (!specialEntityCheck) {
               return mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
            }

            var5 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost)).iterator();

            while(var5.hasNext()) {
               entity = (Entity)var5.next();
               if (!(entity instanceof EntityEnderCrystal)) {
                  return false;
               }
            }

            var5 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost2)).iterator();

            while(var5.hasNext()) {
               entity = (Entity)var5.next();
               if (!(entity instanceof EntityEnderCrystal)) {
                  return false;
               }
            }
         } else {
            if (mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150357_h && mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150343_Z) {
               return false;
            }

            if (mc.field_71441_e.func_180495_p(boost).func_177230_c() != Blocks.field_150350_a) {
               return false;
            }

            if (!specialEntityCheck) {
               return mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost)).isEmpty();
            }

            var5 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(boost)).iterator();

            while(var5.hasNext()) {
               entity = (Entity)var5.next();
               if (!(entity instanceof EntityEnderCrystal)) {
                  return false;
               }
            }
         }

         return true;
      } catch (Exception var7) {
         var7.printStackTrace();
         return false;
      }
   }

   public static void breakCrystal(BlockPos pos, boolean swing) {
      if (pos != null) {
         Iterator var2 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(pos)).iterator();

         while(var2.hasNext()) {
            Entity entity = (Entity)var2.next();
            if (entity instanceof EntityEnderCrystal) {
               breakCrystal(entity, swing);
               break;
            }
         }

      }
   }

   public static void breakCrystalPacket(BlockPos pos, boolean swing) {
      if (pos != null) {
         Iterator var2 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(pos)).iterator();

         while(var2.hasNext()) {
            Entity entity = (Entity)var2.next();
            if (entity instanceof EntityEnderCrystal) {
               breakCrystalPacket(entity, swing);
               break;
            }
         }

      }
   }

   public static void breakCrystal(Entity crystal, boolean swing) {
      mc.field_71442_b.func_78764_a(mc.field_71439_g, crystal);
      if (swing) {
         mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
      }

   }

   public static void breakCrystalPacket(Entity crystal, boolean swing) {
      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketUseEntity(crystal));
      if (swing) {
         mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
      }

   }

   static {
      valid = Arrays.asList(Blocks.field_150343_Z, Blocks.field_150357_h, Blocks.field_150477_bB, Blocks.field_150467_bQ);
   }
}

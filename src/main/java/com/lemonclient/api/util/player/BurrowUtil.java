package com.lemonclient.api.util.player;

import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class BurrowUtil {
   public static final Minecraft mc = Minecraft.func_71410_x();
   static EnumFacing[] facing;

   public static void placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking, boolean swing) {
      if (pos != null && BlockUtil.isAir(pos)) {
         EnumFacing side = getFirstFacing(pos);
         if (side != null) {
            BlockPos neighbour = pos.func_177972_a(side);
            EnumFacing opposite = side.func_176734_d();
            Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
            boolean sneak = false;
            if (!ColorMain.INSTANCE.sneaking && BlockUtil.blackList.contains(BlockUtil.getBlock(neighbour))) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
               mc.field_71439_g.func_70095_a(true);
               sneak = true;
            }

            if (rotate) {
               faceVector(hitVec, true);
            }

            rightClickBlock(neighbour, hitVec, hand, opposite, packet, swing);
            if (sneak) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
            }

            mc.field_71467_ac = 4;
         }
      }
   }

   public static void placeBlockDown(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking, boolean swing) {
      if (pos != null && BlockUtil.isAir(pos)) {
         EnumFacing side = getFirstFacing(pos);
         if (side == null) {
            side = EnumFacing.DOWN;
         }

         BlockPos neighbour = pos.func_177972_a(side);
         EnumFacing opposite = side.func_176734_d();
         Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
         boolean sneak = false;
         if (!ColorMain.INSTANCE.sneaking && BlockUtil.blackList.contains(BlockUtil.getBlock(neighbour))) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
            mc.field_71439_g.func_70095_a(true);
            sneak = true;
         }

         if (rotate) {
            faceVector(hitVec, true);
         }

         rightClickBlock(neighbour, hitVec, hand, opposite, packet, swing);
         if (sneak) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
         }

         mc.field_71467_ac = 4;
      }
   }

   public static List<EnumFacing> getPossibleSides(BlockPos pos) {
      if (pos == null) {
         return null;
      } else {
         List<EnumFacing> facings = new ArrayList();
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
   }

   public static List<EnumFacing> getTrapdoorPossibleSides(BlockPos pos) {
      if (pos == null) {
         return null;
      } else {
         List<EnumFacing> facings = new ArrayList();
         EnumFacing[] var2 = facing;
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
   }

   public static EnumFacing getFirstFacing(BlockPos pos) {
      if (pos == null) {
         return null;
      } else {
         Iterator var1 = getPossibleSides(pos).iterator();
         if (var1.hasNext()) {
            EnumFacing facing = (EnumFacing)var1.next();
            return facing;
         } else {
            return null;
         }
      }
   }

   public static EnumFacing getBedFacing(BlockPos pos) {
      if (pos == null) {
         return null;
      } else {
         Iterator var1 = getPossibleSides(pos).iterator();

         EnumFacing facing;
         do {
            if (!var1.hasNext()) {
               return null;
            }

            facing = (EnumFacing)var1.next();
         } while(facing == EnumFacing.UP);

         return facing;
      }
   }

   public static EnumFacing getTrapdoorFacing(BlockPos pos) {
      if (pos == null) {
         return null;
      } else {
         Iterator var1 = getTrapdoorPossibleSides(pos).iterator();
         if (var1.hasNext()) {
            EnumFacing facing = (EnumFacing)var1.next();
            return facing;
         } else {
            return null;
         }
      }
   }

   public static Vec3d getEyesPos() {
      return new Vec3d(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u + (double)mc.field_71439_g.func_70047_e(), mc.field_71439_g.field_70161_v);
   }

   public static float[] getLegitRotations(Vec3d vec) {
      Vec3d eyesPos = getEyesPos();
      double diffX = vec.field_72450_a - eyesPos.field_72450_a;
      double diffY = vec.field_72448_b - eyesPos.field_72448_b;
      double diffZ = vec.field_72449_c - eyesPos.field_72449_c;
      double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
      float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
      float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
      return new float[]{mc.field_71439_g.field_70177_z + MathHelper.func_76142_g(yaw - mc.field_71439_g.field_70177_z), mc.field_71439_g.field_70125_A + MathHelper.func_76142_g(pitch - mc.field_71439_g.field_70125_A)};
   }

   public static void faceVector(Vec3d vec, boolean normalizeAngle) {
      float[] rotations = getLegitRotations(vec);
      mc.field_71439_g.field_71174_a.func_147297_a(new Rotation(rotations[0], normalizeAngle ? (float)MathHelper.func_180184_b((int)rotations[1], 360) : rotations[1], mc.field_71439_g.field_70122_E));
   }

   public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet, boolean swing) {
      if (pos != null && vec != null && hand != null && direction != null) {
         if (packet) {
            float f = (float)(vec.field_72450_a - (double)pos.func_177958_n());
            float f1 = (float)(vec.field_72448_b - (double)pos.func_177956_o());
            float f2 = (float)(vec.field_72449_c - (double)pos.func_177952_p());
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
         } else {
            mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, pos, direction, vec, hand);
         }

         if (swing) {
            mc.field_71439_g.func_184609_a(hand);
         }

      }
   }

   public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
      if (pos != null && vec != null && direction != null) {
         if (packet) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, 0.5F, 1.0F, 0.5F));
         } else {
            mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, pos, direction, vec, hand);
         }

      }
   }

   public static void rightClickBlock(BlockPos pos, EnumFacing facing, Vec3d hVec, boolean packet, boolean swing) {
      Vec3d hitVec = (new Vec3d(pos)).func_178787_e(hVec).func_178787_e((new Vec3d(facing.func_176730_m())).func_186678_a(0.5D));
      if (packet) {
         rightClickBlock(pos, hitVec, EnumHand.MAIN_HAND, facing);
      } else {
         mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, pos, facing, hitVec, EnumHand.MAIN_HAND);
      }

      if (swing) {
         mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
      }

   }

   public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction) {
      float f = (float)(vec.field_72450_a - (double)pos.func_177958_n());
      float f1 = (float)(vec.field_72448_b - (double)pos.func_177956_o());
      float f2 = (float)(vec.field_72449_c - (double)pos.func_177952_p());
      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
   }

   public static int findBlock(Class clazz, boolean inv) {
      int slot = findHotbarBlock(clazz);
      if (slot == -1 && inv) {
         slot = findInventoryBlock(clazz);
      }

      return slot;
   }

   public static int findHotbarBlock(Class clazz) {
      for(int i = 0; i < 9; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (stack != ItemStack.field_190927_a) {
            if (clazz.isInstance(stack.func_77973_b())) {
               return i;
            }

            if (stack.func_77973_b() instanceof ItemBlock) {
               Block block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
               if (clazz.isInstance(block)) {
                  return i;
               }
            }
         }
      }

      return -1;
   }

   public static int findHotbarBlock(Block blockIn) {
      for(int i = 0; i < 9; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemBlock && ((ItemBlock)stack.func_77973_b()).func_179223_d() == blockIn) {
            return i;
         }
      }

      return -1;
   }

   public static int findHotbarItem(Item input) {
      for(int i = 0; i < 9; ++i) {
         Item item = mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b();
         if (Item.func_150891_b(item) == Item.func_150891_b(input)) {
            return i;
         }
      }

      return -1;
   }

   public static int findInventoryItem(Item input) {
      for(int i = 0; i < 36; ++i) {
         Item item = mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b();
         if (Item.func_150891_b(item) == Item.func_150891_b(input)) {
            return i;
         }
      }

      return -1;
   }

   public static int findInventoryBlock(Class clazz) {
      for(int i = 9; i < 36; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (stack != ItemStack.field_190927_a) {
            if (clazz.isInstance(stack.func_77973_b())) {
               return i;
            }

            if (stack.func_77973_b() instanceof ItemBlock) {
               Block block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
               if (clazz.isInstance(block)) {
                  return i;
               }
            }
         }
      }

      return -1;
   }

   public static int getCount(Class clazz) {
      int count = 0;

      for(int i = 0; i < 36; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (stack != ItemStack.field_190927_a) {
            if (clazz.isInstance(stack.func_77973_b())) {
               count += stack.func_190916_E();
            }

            if (stack.func_77973_b() instanceof ItemBlock) {
               Block block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
               if (clazz.isInstance(block)) {
                  count += stack.func_190916_E();
               }
            }
         }
      }

      return count;
   }

   public static void switchToSlot(int slot) {
      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
      mc.field_71439_g.field_71071_by.field_70461_c = slot;
      mc.field_71442_b.func_78765_e();
   }

   static {
      facing = new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.WEST, EnumFacing.EAST};
   }
}

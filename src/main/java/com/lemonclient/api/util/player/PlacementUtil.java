package com.lemonclient.api.util.player;

import com.lemonclient.api.util.world.BlockUtil;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PlacementUtil {
   private static final Minecraft mc = Minecraft.func_71410_x();
   private static int placementConnections = 0;
   private static boolean isSneaking = false;

   public static void onEnable() {
      ++placementConnections;
   }

   public static void onDisable() {
      --placementConnections;
      if (placementConnections == 0 && isSneaking) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
         isSneaking = false;
      }

   }

   public static void stopSneaking() {
      if (isSneaking) {
         isSneaking = false;
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
      }

   }

   public static boolean placeBlock(BlockPos blockPos, EnumHand hand, boolean rotate, Class<? extends Block> blockToPlace) {
      int oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
      int newSlot = InventoryUtil.findFirstBlockSlot(blockToPlace, 0, 8);
      if (newSlot == -1) {
         return false;
      } else {
         mc.field_71439_g.field_71071_by.field_70461_c = newSlot;
         boolean output = place(blockPos, hand, rotate);
         mc.field_71439_g.field_71071_by.field_70461_c = oldSlot;
         return output;
      }
   }

   public static boolean placeItem(BlockPos blockPos, EnumHand hand, boolean rotate, Class<? extends Item> itemToPlace) {
      int oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
      int newSlot = InventoryUtil.findFirstItemSlot(itemToPlace, 0, 8);
      if (newSlot == -1) {
         return false;
      } else {
         mc.field_71439_g.field_71071_by.field_70461_c = newSlot;
         boolean output = place(blockPos, hand, rotate);
         mc.field_71439_g.field_71071_by.field_70461_c = oldSlot;
         return output;
      }
   }

   public static boolean place(BlockPos blockPos, EnumHand hand, boolean rotate) {
      return placeBlock(blockPos, hand, rotate, true, (ArrayList)null);
   }

   public static boolean place(BlockPos blockPos, EnumHand hand, boolean rotate, ArrayList<EnumFacing> forceSide) {
      return placeBlock(blockPos, hand, rotate, true, forceSide);
   }

   public static boolean holeFill(BlockPos blockPos, EnumHand hand, boolean rotate, boolean swing, ArrayList<EnumFacing> forceSide) {
      return holeFillBlock(blockPos, hand, rotate, swing, forceSide);
   }

   public static boolean holeFillawa(BlockPos blockPos, EnumHand hand, boolean rotate, boolean swing) {
      return holeFillBlockawa(blockPos, hand, rotate, swing);
   }

   public static boolean place(BlockPos blockPos, EnumHand hand, boolean rotate, boolean checkAction) {
      return placeBlock(blockPos, hand, rotate, checkAction, (ArrayList)null);
   }

   public static boolean holeFill(BlockPos blockPos, EnumHand hand, boolean rotate, boolean swing) {
      return holeFillBlock(blockPos, hand, rotate, swing, (ArrayList)null);
   }

   public static Rotation placeBlockGetRotate(BlockPos blockPos, EnumHand hand, boolean checkAction, ArrayList<EnumFacing> forceSide, boolean swingArm) {
      EntityPlayerSP player = mc.field_71439_g;
      WorldClient world = mc.field_71441_e;
      PlayerControllerMP playerController = mc.field_71442_b;
      if (player != null && world != null && playerController != null) {
         if (!world.func_180495_p(blockPos).func_185904_a().func_76222_j()) {
            return null;
         } else {
            EnumFacing side = forceSide != null ? BlockUtil.getPlaceableSideExlude(blockPos, forceSide) : BlockUtil.getPlaceableSide(blockPos);
            if (side == null) {
               return null;
            } else {
               BlockPos neighbour = blockPos.func_177972_a(side);
               EnumFacing opposite = side.func_176734_d();
               if (!BlockUtil.canBeClicked(neighbour)) {
                  return null;
               } else {
                  Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
                  Block neighbourBlock = world.func_180495_p(neighbour).func_177230_c();
                  if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
                     player.field_71174_a.func_147297_a(new CPacketEntityAction(player, Action.START_SNEAKING));
                     isSneaking = true;
                  }

                  EnumActionResult action = playerController.func_187099_a(player, world, neighbour, opposite, hitVec, hand);
                  if (!checkAction || action == EnumActionResult.SUCCESS) {
                     if (swingArm) {
                        player.func_184609_a(hand);
                        mc.field_71467_ac = 4;
                     } else {
                        player.field_71174_a.func_147297_a(new CPacketAnimation(hand));
                     }
                  }

                  return BlockUtil.getFaceVectorPacket(hitVec, true);
               }
            }
         }
      } else {
         return null;
      }
   }

   public static boolean placeBlock(BlockPos blockPos, EnumHand hand, boolean rotate, boolean checkAction, ArrayList<EnumFacing> forceSide) {
      EntityPlayerSP player = mc.field_71439_g;
      WorldClient world = mc.field_71441_e;
      PlayerControllerMP playerController = mc.field_71442_b;
      if (player != null && world != null && playerController != null) {
         if (!world.func_180495_p(blockPos).func_185904_a().func_76222_j()) {
            return false;
         } else {
            EnumFacing side = forceSide != null ? BlockUtil.getPlaceableSideExlude(blockPos, forceSide) : BlockUtil.getPlaceableSide(blockPos);
            if (side == null) {
               return false;
            } else {
               BlockPos neighbour = blockPos.func_177972_a(side);
               EnumFacing opposite = side.func_176734_d();
               if (!BlockUtil.canBeClicked(neighbour)) {
                  return false;
               } else {
                  Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
                  Block neighbourBlock = world.func_180495_p(neighbour).func_177230_c();
                  if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
                     player.field_71174_a.func_147297_a(new CPacketEntityAction(player, Action.START_SNEAKING));
                     isSneaking = true;
                  }

                  if (rotate) {
                     BlockUtil.faceVectorPacketInstant(hitVec, true);
                  }

                  EnumActionResult action = playerController.func_187099_a(player, world, neighbour, opposite, hitVec, hand);
                  if (!checkAction || action == EnumActionResult.SUCCESS) {
                     player.func_184609_a(hand);
                     mc.field_71467_ac = 4;
                  }

                  return action == EnumActionResult.SUCCESS;
               }
            }
         }
      } else {
         return false;
      }
   }

   public static boolean holeFillBlock(BlockPos blockPos, EnumHand hand, boolean rotate, boolean swing, ArrayList<EnumFacing> forceSide) {
      EntityPlayerSP player = mc.field_71439_g;
      WorldClient world = mc.field_71441_e;
      PlayerControllerMP playerController = mc.field_71442_b;
      if (player != null && world != null && playerController != null) {
         if (!world.func_180495_p(blockPos).func_185904_a().func_76222_j()) {
            return false;
         } else {
            EnumFacing side = forceSide != null ? BlockUtil.getPlaceableSideExlude(blockPos, forceSide) : BlockUtil.getPlaceableSide(blockPos);
            if (side == null) {
               return false;
            } else {
               BlockPos neighbour = blockPos.func_177972_a(side);
               EnumFacing opposite = side.func_176734_d();
               if (!BlockUtil.canBeClicked(neighbour)) {
                  return false;
               } else {
                  Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
                  Block neighbourBlock = world.func_180495_p(neighbour).func_177230_c();
                  if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
                     player.field_71174_a.func_147297_a(new CPacketEntityAction(player, Action.START_SNEAKING));
                     isSneaking = true;
                  }

                  if (rotate) {
                     BlockUtil.faceVectorPacketInstant(hitVec, true);
                  }

                  EnumActionResult action = playerController.func_187099_a(player, world, neighbour, opposite, hitVec, hand);
                  if (swing) {
                     player.func_184609_a(hand);
                  }

                  return action == EnumActionResult.SUCCESS;
               }
            }
         }
      } else {
         return false;
      }
   }

   public static boolean holeFillBlockawa(BlockPos blockPos, EnumHand hand, boolean rotate, boolean swing) {
      EntityPlayerSP player = mc.field_71439_g;
      WorldClient world = mc.field_71441_e;
      PlayerControllerMP playerController = mc.field_71442_b;
      if (player != null && world != null && playerController != null) {
         if (!world.func_180495_p(blockPos).func_185904_a().func_76222_j()) {
            return false;
         } else {
            BlockPos neighbour;
            EnumFacing opposite;
            if (!mc.field_71441_e.func_175623_d(blockPos.func_177968_d())) {
               neighbour = blockPos.func_177972_a(EnumFacing.SOUTH);
               opposite = EnumFacing.SOUTH.func_176734_d();
            } else if (!mc.field_71441_e.func_175623_d(blockPos.func_177978_c())) {
               neighbour = blockPos.func_177972_a(EnumFacing.NORTH);
               opposite = EnumFacing.NORTH.func_176734_d();
            } else if (!mc.field_71441_e.func_175623_d(blockPos.func_177974_f())) {
               neighbour = blockPos.func_177972_a(EnumFacing.EAST);
               opposite = EnumFacing.EAST.func_176734_d();
            } else {
               if (mc.field_71441_e.func_175623_d(blockPos.func_177976_e())) {
                  return false;
               }

               neighbour = blockPos.func_177972_a(EnumFacing.WEST);
               opposite = EnumFacing.WEST.func_176734_d();
            }

            if (!BlockUtil.canBeClicked(neighbour)) {
               return false;
            } else {
               Vec3d hitVec = (new Vec3d(neighbour)).func_178787_e(new Vec3d(0.5D, 0.8D, 0.5D)).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
               Block neighbourBlock = world.func_180495_p(neighbour).func_177230_c();
               if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
                  player.field_71174_a.func_147297_a(new CPacketEntityAction(player, Action.START_SNEAKING));
                  isSneaking = true;
               }

               if (rotate) {
                  BlockUtil.faceVectorPacketInstant(hitVec, true);
               }

               EnumActionResult action = playerController.func_187099_a(player, world, neighbour, opposite, hitVec, hand);
               if (swing) {
                  player.func_184609_a(hand);
               }

               return action == EnumActionResult.SUCCESS;
            }
         }
      } else {
         return false;
      }
   }

   public static boolean placePrecise(BlockPos blockPos, EnumHand hand, boolean rotate, Vec3d precise, EnumFacing forceSide, boolean onlyRotation, boolean support) {
      EntityPlayerSP player = mc.field_71439_g;
      WorldClient world = mc.field_71441_e;
      PlayerControllerMP playerController = mc.field_71442_b;
      if (player != null && world != null && playerController != null) {
         if (!world.func_180495_p(blockPos).func_185904_a().func_76222_j()) {
            return false;
         } else {
            EnumFacing side = forceSide == null ? BlockUtil.getPlaceableSide(blockPos) : forceSide;
            if (side == null) {
               return false;
            } else {
               BlockPos neighbour = blockPos.func_177972_a(side);
               EnumFacing opposite = side.func_176734_d();
               if (!BlockUtil.canBeClicked(neighbour)) {
                  return false;
               } else {
                  Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
                  Block neighbourBlock = world.func_180495_p(neighbour).func_177230_c();
                  if (!isSneaking && BlockUtil.blackList.contains(neighbourBlock) || BlockUtil.shulkerList.contains(neighbourBlock)) {
                     player.field_71174_a.func_147297_a(new CPacketEntityAction(player, Action.START_SNEAKING));
                     isSneaking = true;
                  }

                  if (rotate && !support) {
                     BlockUtil.faceVectorPacketInstant(precise == null ? hitVec : precise, true);
                  }

                  if (!onlyRotation) {
                     EnumActionResult action = playerController.func_187099_a(player, world, neighbour, opposite, precise == null ? hitVec : precise, hand);
                     if (action == EnumActionResult.SUCCESS) {
                        player.func_184609_a(hand);
                        mc.field_71467_ac = 4;
                     }

                     return action == EnumActionResult.SUCCESS;
                  } else {
                     return true;
                  }
               }
            }
         }
      } else {
         return false;
      }
   }
}

package com.lemonclient.api.util.world.combat;

import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.EntityUtil;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class CrystalUtil {
   private static final Minecraft mc = Minecraft.func_71410_x();

   public static boolean canPlaceCrystal(BlockPos blockPos, boolean newPlacement) {
      if (notValidBlock(mc.field_71441_e.func_180495_p(blockPos).func_177230_c())) {
         return false;
      } else {
         BlockPos posUp = blockPos.func_177984_a();
         if (newPlacement) {
            if (!mc.field_71441_e.func_175623_d(posUp)) {
               return false;
            }
         } else if (notValidMaterial(mc.field_71441_e.func_180495_p(posUp).func_185904_a()) || notValidMaterial(mc.field_71441_e.func_180495_p(posUp.func_177984_a()).func_185904_a())) {
            return false;
         }

         AxisAlignedBB box = new AxisAlignedBB((double)posUp.func_177958_n(), (double)posUp.func_177956_o(), (double)posUp.func_177952_p(), (double)posUp.func_177958_n() + 1.0D, (double)posUp.func_177956_o() + 2.0D, (double)posUp.func_177952_p() + 1.0D);
         return mc.field_71441_e.func_175647_a(Entity.class, box, Entity::func_70089_S).isEmpty();
      }
   }

   public static boolean canPlaceCrystalExcludingCrystals(BlockPos blockPos, boolean newPlacement) {
      if (notValidBlock(mc.field_71441_e.func_180495_p(blockPos).func_177230_c())) {
         return false;
      } else {
         BlockPos posUp = blockPos.func_177984_a();
         if (newPlacement) {
            if (!mc.field_71441_e.func_175623_d(posUp)) {
               return false;
            }
         } else if (notValidMaterial(mc.field_71441_e.func_180495_p(posUp).func_185904_a()) || notValidMaterial(mc.field_71441_e.func_180495_p(posUp.func_177984_a()).func_185904_a())) {
            return false;
         }

         AxisAlignedBB box = new AxisAlignedBB((double)posUp.func_177958_n(), (double)posUp.func_177956_o(), (double)posUp.func_177952_p(), (double)posUp.func_177958_n() + 1.0D, (double)posUp.func_177956_o() + 2.0D, (double)posUp.func_177952_p() + 1.0D);
         return mc.field_71441_e.func_175647_a(Entity.class, box, (entity) -> {
            return !entity.field_70128_L && !(entity instanceof EntityEnderCrystal);
         }).isEmpty();
      }
   }

   public static boolean notValidBlock(Block block) {
      return block != Blocks.field_150357_h && block != Blocks.field_150343_Z;
   }

   public static boolean notValidMaterial(Material material) {
      return material.func_76224_d() || !material.func_76222_j();
   }

   public static List<BlockPos> findCrystalBlocks(float placeRange, boolean mode) {
      return (List)EntityUtil.getSphere(PlayerUtil.getPlayerPos(), (double)placeRange, (double)placeRange, false, true, 0).stream().filter((pos) -> {
         return canPlaceCrystal(pos, mode);
      }).collect(Collectors.toList());
   }

   public static List<BlockPos> findCrystalBlocksExcludingCrystals(float placeRange, boolean mode) {
      return (List)EntityUtil.getSphere(PlayerUtil.getPlayerPos(), (double)placeRange, (double)placeRange, false, true, 0).stream().filter((pos) -> {
         return canPlaceCrystalExcludingCrystals(pos, mode);
      }).collect(Collectors.toList());
   }

   public static void breakCrystal(BlockPos pos, boolean swing) {
      Iterator var2 = mc.field_71441_e.func_72872_a(Entity.class, new AxisAlignedBB(pos)).iterator();

      while(var2.hasNext()) {
         Entity entity = (Entity)var2.next();
         if (entity instanceof EntityEnderCrystal) {
            mc.field_71442_b.func_78764_a(mc.field_71439_g, entity);
            if (swing) {
               mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
            }
            break;
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
}

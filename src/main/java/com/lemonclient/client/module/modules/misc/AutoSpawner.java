package com.lemonclient.client.module.modules.misc;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockDeadBush;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "AutoSpawner",
   category = Category.Misc
)
public class AutoSpawner extends Module {
   ModeSetting useMode = this.registerMode("Use Mode", Arrays.asList("Single", "Spam"), "Spam");
   BooleanSetting party = this.registerBoolean("Wither Party", false);
   ModeSetting entityMode = this.registerMode("Entity Mode", Arrays.asList("Snow", "Iron", "Wither"), "Wither");
   BooleanSetting nametagWithers = this.registerBoolean("Nametag", true);
   DoubleSetting placeRange = this.registerDouble("Place Range", 3.5D, 1.0D, 10.0D);
   IntegerSetting delay = this.registerInteger("Delay", 20, 0, 100);
   BooleanSetting rotate = this.registerBoolean("Rotate", true);
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", true);
   BooleanSetting check = this.registerBoolean("Switch Check", true);
   BooleanSetting packet = this.registerBoolean("Packet Place", true);
   BooleanSetting swing = this.registerBoolean("Swing", true);
   private static boolean isSneaking;
   private BlockPos placeTarget;
   private boolean rotationPlaceableX;
   private boolean rotationPlaceableZ;
   private int bodySlot;
   private int headSlot;
   private int buildStage;
   private int delayStep;

   private void useNameTag() {
      int originalSlot = mc.field_71439_g.field_71071_by.field_70461_c;
      Iterator var2 = mc.field_71441_e.func_72910_y().iterator();

      while(var2.hasNext()) {
         Entity w = (Entity)var2.next();
         if (w instanceof EntityWither && w.func_145748_c_().func_150260_c().equalsIgnoreCase("Wither")) {
            EntityWither wither = (EntityWither)w;
            if ((double)mc.field_71439_g.func_70032_d(wither) <= (Double)this.placeRange.getValue()) {
               this.selectNameTags();
               mc.field_71442_b.func_187097_a(mc.field_71439_g, wither, EnumHand.MAIN_HAND);
            }
         }
      }

      this.switchTo(originalSlot);
   }

   private void selectNameTags() {
      int tagSlot = -1;

      for(int i = 0; i < 9; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (stack != ItemStack.field_190927_a && !(stack.func_77973_b() instanceof ItemBlock)) {
            Item tag = stack.func_77973_b();
            if (tag instanceof ItemNameTag) {
               tagSlot = i;
            }
         }
      }

      if (tagSlot != -1) {
         this.switchTo(tagSlot);
      }
   }

   private static EnumFacing getPlaceableSide(BlockPos pos) {
      EnumFacing[] var1 = EnumFacing.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         EnumFacing side = var1[var3];
         BlockPos neighbour = pos.func_177972_a(side);
         if (mc.field_71441_e.func_180495_p(neighbour).func_177230_c().func_176209_a(mc.field_71441_e.func_180495_p(neighbour), false)) {
            IBlockState blockState = mc.field_71441_e.func_180495_p(neighbour);
            if (!blockState.func_185904_a().func_76222_j() && !(blockState.func_177230_c() instanceof BlockTallGrass) && !(blockState.func_177230_c() instanceof BlockDeadBush)) {
               return side;
            }
         }
      }

      return null;
   }

   protected void onEnable() {
      this.buildStage = 1;
      this.delayStep = 1;
   }

   private boolean checkBlocksInHotbar() {
      this.headSlot = -1;
      this.bodySlot = -1;

      for(int i = 0; i < 9; ++i) {
         ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
         if (stack != ItemStack.field_190927_a) {
            Block block;
            if (((String)this.entityMode.getValue()).equals("Wither")) {
               if (stack.func_77973_b() == Items.field_151144_bL && stack.func_77952_i() == 1) {
                  if (mc.field_71439_g.field_71071_by.func_70301_a(i).field_77994_a >= 3) {
                     this.headSlot = i;
                  }
                  continue;
               }

               if (!(stack.func_77973_b() instanceof ItemBlock)) {
                  continue;
               }

               block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
               if (block instanceof BlockSoulSand && mc.field_71439_g.field_71071_by.func_70301_a(i).field_77994_a >= 4) {
                  this.bodySlot = i;
               }
            }

            if (((String)this.entityMode.getValue()).equals("Iron")) {
               if (!(stack.func_77973_b() instanceof ItemBlock)) {
                  continue;
               }

               block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
               if ((block == Blocks.field_150428_aP || block == Blocks.field_150423_aK) && mc.field_71439_g.field_71071_by.func_70301_a(i).field_77994_a >= 1) {
                  this.headSlot = i;
               }

               if (block == Blocks.field_150339_S && mc.field_71439_g.field_71071_by.func_70301_a(i).field_77994_a >= 4) {
                  this.bodySlot = i;
               }
            }

            if (((String)this.entityMode.getValue()).equals("Snow") && stack.func_77973_b() instanceof ItemBlock) {
               block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
               if ((block == Blocks.field_150428_aP || block == Blocks.field_150423_aK) && mc.field_71439_g.field_71071_by.func_70301_a(i).field_77994_a >= 1) {
                  this.headSlot = i;
               }

               if (block == Blocks.field_150433_aE && mc.field_71439_g.field_71071_by.func_70301_a(i).field_77994_a >= 2) {
                  this.bodySlot = i;
               }
            }
         }
      }

      return this.bodySlot != -1 && this.headSlot != -1;
   }

   private boolean testStructure() {
      if (((String)this.entityMode.getValue()).equals("Wither")) {
         return this.testWitherStructure();
      } else if (((String)this.entityMode.getValue()).equals("Iron")) {
         return this.testIronGolemStructure();
      } else {
         return ((String)this.entityMode.getValue()).equals("Snow") ? this.testSnowGolemStructure() : false;
      }
   }

   private boolean testWitherStructure() {
      boolean noRotationPlaceable = true;
      this.rotationPlaceableX = true;
      this.rotationPlaceableZ = true;
      boolean isShitGrass = false;
      if (mc.field_71441_e.func_180495_p(this.placeTarget) == null) {
         return false;
      } else {
         Block block = mc.field_71441_e.func_180495_p(this.placeTarget).func_177230_c();
         if (block instanceof BlockTallGrass || block instanceof BlockDeadBush) {
            isShitGrass = true;
         }

         if (getPlaceableSide(this.placeTarget.func_177984_a()) == null) {
            return false;
         } else {
            BlockPos[] var4 = AutoSpawner.BodyParts.bodyBase;
            int var5 = var4.length;

            int var6;
            BlockPos pos;
            for(var6 = 0; var6 < var5; ++var6) {
               pos = var4[var6];
               if (this.placingIsBlocked(this.placeTarget.func_177971_a(pos))) {
                  noRotationPlaceable = false;
               }
            }

            var4 = AutoSpawner.BodyParts.ArmsX;
            var5 = var4.length;

            for(var6 = 0; var6 < var5; ++var6) {
               pos = var4[var6];
               if (this.placingIsBlocked(this.placeTarget.func_177971_a(pos)) || this.placingIsBlocked(this.placeTarget.func_177971_a(pos.func_177977_b()))) {
                  this.rotationPlaceableX = false;
               }
            }

            var4 = AutoSpawner.BodyParts.ArmsZ;
            var5 = var4.length;

            for(var6 = 0; var6 < var5; ++var6) {
               pos = var4[var6];
               if (this.placingIsBlocked(this.placeTarget.func_177971_a(pos)) || this.placingIsBlocked(this.placeTarget.func_177971_a(pos.func_177977_b()))) {
                  this.rotationPlaceableZ = false;
               }
            }

            var4 = AutoSpawner.BodyParts.headsX;
            var5 = var4.length;

            for(var6 = 0; var6 < var5; ++var6) {
               pos = var4[var6];
               if (this.placingIsBlocked(this.placeTarget.func_177971_a(pos))) {
                  this.rotationPlaceableX = false;
               }
            }

            var4 = AutoSpawner.BodyParts.headsZ;
            var5 = var4.length;

            for(var6 = 0; var6 < var5; ++var6) {
               pos = var4[var6];
               if (this.placingIsBlocked(this.placeTarget.func_177971_a(pos))) {
                  this.rotationPlaceableZ = false;
               }
            }

            return !isShitGrass && noRotationPlaceable && (this.rotationPlaceableX || this.rotationPlaceableZ);
         }
      }
   }

   private boolean testIronGolemStructure() {
      boolean noRotationPlaceable = true;
      this.rotationPlaceableX = true;
      this.rotationPlaceableZ = true;
      boolean isShitGrass = false;
      if (mc.field_71441_e.func_180495_p(this.placeTarget) == null) {
         return false;
      } else {
         Block block = mc.field_71441_e.func_180495_p(this.placeTarget).func_177230_c();
         if (block instanceof BlockTallGrass || block instanceof BlockDeadBush) {
            isShitGrass = true;
         }

         if (getPlaceableSide(this.placeTarget.func_177984_a()) == null) {
            return false;
         } else {
            BlockPos[] var4 = AutoSpawner.BodyParts.bodyBase;
            int var5 = var4.length;

            int var6;
            BlockPos pos;
            for(var6 = 0; var6 < var5; ++var6) {
               pos = var4[var6];
               if (this.placingIsBlocked(this.placeTarget.func_177971_a(pos))) {
                  noRotationPlaceable = false;
               }
            }

            var4 = AutoSpawner.BodyParts.ArmsX;
            var5 = var4.length;

            for(var6 = 0; var6 < var5; ++var6) {
               pos = var4[var6];
               if (this.placingIsBlocked(this.placeTarget.func_177971_a(pos)) || this.placingIsBlocked(this.placeTarget.func_177971_a(pos.func_177977_b()))) {
                  this.rotationPlaceableX = false;
               }
            }

            var4 = AutoSpawner.BodyParts.ArmsZ;
            var5 = var4.length;

            for(var6 = 0; var6 < var5; ++var6) {
               pos = var4[var6];
               if (this.placingIsBlocked(this.placeTarget.func_177971_a(pos)) || this.placingIsBlocked(this.placeTarget.func_177971_a(pos.func_177977_b()))) {
                  this.rotationPlaceableZ = false;
               }
            }

            var4 = AutoSpawner.BodyParts.head;
            var5 = var4.length;

            for(var6 = 0; var6 < var5; ++var6) {
               pos = var4[var6];
               if (this.placingIsBlocked(this.placeTarget.func_177971_a(pos))) {
                  noRotationPlaceable = false;
               }
            }

            return !isShitGrass && noRotationPlaceable && (this.rotationPlaceableX || this.rotationPlaceableZ);
         }
      }
   }

   private boolean testSnowGolemStructure() {
      boolean noRotationPlaceable = true;
      boolean isShitGrass = false;
      if (mc.field_71441_e.func_180495_p(this.placeTarget) == null) {
         return false;
      } else {
         Block block = mc.field_71441_e.func_180495_p(this.placeTarget).func_177230_c();
         if (block instanceof BlockTallGrass || block instanceof BlockDeadBush) {
            isShitGrass = true;
         }

         if (getPlaceableSide(this.placeTarget.func_177984_a()) == null) {
            return false;
         } else {
            BlockPos[] var4 = AutoSpawner.BodyParts.bodyBase;
            int var5 = var4.length;

            int var6;
            BlockPos pos;
            for(var6 = 0; var6 < var5; ++var6) {
               pos = var4[var6];
               if (this.placingIsBlocked(this.placeTarget.func_177971_a(pos))) {
                  noRotationPlaceable = false;
               }
            }

            var4 = AutoSpawner.BodyParts.head;
            var5 = var4.length;

            for(var6 = 0; var6 < var5; ++var6) {
               pos = var4[var6];
               if (this.placingIsBlocked(this.placeTarget.func_177971_a(pos))) {
                  noRotationPlaceable = false;
               }
            }

            return !isShitGrass && noRotationPlaceable;
         }
      }
   }

   private void switchTo(int slot) {
      if (slot > -1 && slot < 9 && (!(Boolean)this.check.getValue() || mc.field_71439_g.field_71071_by.field_70461_c != slot)) {
         if ((Boolean)this.packetSwitch.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
         } else {
            mc.field_71439_g.field_71071_by.field_70461_c = slot;
         }

         mc.field_71442_b.func_78765_e();
      }

   }

   public void onUpdate() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if ((Boolean)this.nametagWithers.getValue() && ((Boolean)this.party.getValue() || !(Boolean)this.party.getValue() && ((String)this.entityMode.getValue()).equals("Wither"))) {
            this.useNameTag();
         }

         int oldslot;
         if (this.buildStage == 1) {
            isSneaking = false;
            this.rotationPlaceableX = false;
            this.rotationPlaceableZ = false;
            if ((Boolean)this.party.getValue()) {
               this.entityMode.setValue("Wither");
            }

            if (!this.checkBlocksInHotbar()) {
               if (((String)this.useMode.getValue()).equals("Single")) {
                  this.disable();
               }

               return;
            }

            List<BlockPos> blockPosList = EntityUtil.getSphere(mc.field_71439_g.func_180425_c().func_177977_b(), (Double)this.placeRange.getValue(), (Double)this.placeRange.getValue(), false, true, 0);
            boolean noPositionInArea = true;
            Iterator var3 = blockPosList.iterator();

            while(var3.hasNext()) {
               BlockPos pos = (BlockPos)var3.next();
               this.placeTarget = pos.func_177977_b();
               if (this.testStructure()) {
                  noPositionInArea = false;
                  break;
               }
            }

            if (noPositionInArea) {
               if (((String)this.useMode.getValue()).equals("Single")) {
                  this.disable();
               }

               return;
            }

            oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
            this.switchTo(this.bodySlot);
            BlockPos[] var11 = AutoSpawner.BodyParts.bodyBase;
            int var5 = var11.length;

            int var6;
            BlockPos pos;
            for(var6 = 0; var6 < var5; ++var6) {
               pos = var11[var6];
               BurrowUtil.placeBlock(this.placeTarget.func_177971_a(pos), EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
            }

            if (((String)this.entityMode.getValue()).equals("Wither") || ((String)this.entityMode.getValue()).equals("Iron")) {
               if (this.rotationPlaceableX) {
                  var11 = AutoSpawner.BodyParts.ArmsX;
                  var5 = var11.length;

                  for(var6 = 0; var6 < var5; ++var6) {
                     pos = var11[var6];
                     BurrowUtil.placeBlock(this.placeTarget.func_177971_a(pos), EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                  }
               } else if (this.rotationPlaceableZ) {
                  var11 = AutoSpawner.BodyParts.ArmsZ;
                  var5 = var11.length;

                  for(var6 = 0; var6 < var5; ++var6) {
                     pos = var11[var6];
                     BurrowUtil.placeBlock(this.placeTarget.func_177971_a(pos), EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                  }
               }
            }

            this.switchTo(oldslot);
            this.buildStage = 2;
         } else if (this.buildStage == 2) {
            int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
            this.switchTo(this.headSlot);
            BlockPos[] var9;
            int var12;
            BlockPos pos;
            if (((String)this.entityMode.getValue()).equals("Wither")) {
               if (this.rotationPlaceableX) {
                  var9 = AutoSpawner.BodyParts.headsX;
                  oldslot = var9.length;

                  for(var12 = 0; var12 < oldslot; ++var12) {
                     pos = var9[var12];
                     BurrowUtil.placeBlock(this.placeTarget.func_177971_a(pos), EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                  }
               } else if (this.rotationPlaceableZ) {
                  var9 = AutoSpawner.BodyParts.headsZ;
                  oldslot = var9.length;

                  for(var12 = 0; var12 < oldslot; ++var12) {
                     pos = var9[var12];
                     BurrowUtil.placeBlock(this.placeTarget.func_177971_a(pos), EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                  }
               }
            }

            if (((String)this.entityMode.getValue()).equals("Iron") || ((String)this.entityMode.getValue()).equals("Snow")) {
               var9 = AutoSpawner.BodyParts.head;
               oldslot = var9.length;

               for(var12 = 0; var12 < oldslot; ++var12) {
                  pos = var9[var12];
                  BurrowUtil.placeBlock(this.placeTarget.func_177971_a(pos), EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
               }
            }

            if (isSneaking) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
               isSneaking = false;
            }

            if (((String)this.useMode.getValue()).equals("Single")) {
               this.disable();
            }

            this.switchTo(oldslot);
            this.buildStage = 3;
         } else if (this.buildStage == 3) {
            if (this.delayStep < (Integer)this.delay.getValue()) {
               ++this.delayStep;
            } else {
               this.delayStep = 1;
               this.buildStage = 1;
            }
         }

      }
   }

   private boolean placingIsBlocked(BlockPos pos) {
      Block block = mc.field_71441_e.func_180495_p(pos).func_177230_c();
      if (!(block instanceof BlockAir)) {
         return true;
      } else {
         Iterator var3 = mc.field_71441_e.func_72839_b((Entity)null, new AxisAlignedBB(pos)).iterator();

         Entity entity;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            entity = (Entity)var3.next();
         } while(entity instanceof EntityItem || entity instanceof EntityXPOrb);

         return true;
      }
   }

   private static class BodyParts {
      private static final BlockPos[] bodyBase = new BlockPos[]{new BlockPos(0, 1, 0), new BlockPos(0, 2, 0)};
      private static final BlockPos[] ArmsX = new BlockPos[]{new BlockPos(-1, 2, 0), new BlockPos(1, 2, 0)};
      private static final BlockPos[] ArmsZ = new BlockPos[]{new BlockPos(0, 2, -1), new BlockPos(0, 2, 1)};
      private static final BlockPos[] headsX = new BlockPos[]{new BlockPos(0, 3, 0), new BlockPos(-1, 3, 0), new BlockPos(1, 3, 0)};
      private static final BlockPos[] headsZ = new BlockPos[]{new BlockPos(0, 3, 0), new BlockPos(0, 3, -1), new BlockPos(0, 3, 1)};
      private static final BlockPos[] head = new BlockPos[]{new BlockPos(0, 3, 0)};
   }
}

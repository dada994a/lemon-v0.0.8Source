package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.misc.MathUtil;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.network.play.client.CPacketPlayer.Rotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(
   name = "AutoDispenser32k",
   category = Category.Combat
)
public class AutoDispenser32k extends Module {
   private static final int scale = 1;
   private static final RoundingMode roundingMode;
   private int hopperSlot;
   private int redstoneSlot;
   private int shulkerSlot;
   private int dispenserSlot;
   private int obiSlot;
   private int stage;
   DoubleSetting range = this.registerDouble("PlaceRange", 4.0D, 0.0D, 10.0D);
   DoubleSetting yRange = this.registerDouble("Y Range", 2.5D, 0.0D, 10.0D);
   DoubleSetting targetRange = this.registerDouble("Target Range", 4.0D, 0.0D, 16.0D);
   BooleanSetting placeCloseToEnemy = this.registerBoolean("Place Close To Enemy", true);
   BooleanSetting hopperWait = this.registerBoolean("HopperWait", true);
   BooleanSetting rotate = this.registerBoolean("Rotate", false);
   BooleanSetting dispenserRotate = this.registerBoolean("Dispenser Rotate", false);
   BooleanSetting packet = this.registerBoolean("Packet", false);
   BooleanSetting swing = this.registerBoolean("Swing", true);
   BooleanSetting silent = this.registerBoolean("Silent", true);
   BooleanSetting debugMessages = this.registerBoolean("Debug Messages", false);
   private int delay;
   private int waited;
   private int oldslot;
   AutoDispenser32k.DispenserPos placeTarget;

   private double getWeight(BlockPos pos, EntityPlayer target) {
      double range = target.func_70011_f((double)pos.field_177962_a + 0.5D, (double)pos.field_177960_b + 0.5D, (double)pos.field_177961_c + 0.5D);
      if (range >= (Double)this.targetRange.getValue()) {
         int y = 256 - pos.func_177956_o();
         range += (double)(y * 100);
      }

      return range;
   }

   private List<AutoDispenser32k.DispenserPos> getPlaceableBlocks() {
      List<AutoDispenser32k.DispenserPos> posList = new ArrayList();
      Iterator var2 = EntityUtil.getSphere(PlayerUtil.getEyesPos(), (Double)this.range.getValue(), (Double)this.yRange.getValue(), false, false, 0).iterator();

      while(var2.hasNext()) {
         BlockPos pos = (BlockPos)var2.next();
         EnumFacing[] var4 = EnumFacing.field_176754_o;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            EnumFacing facing = var4[var6];
            BlockPos base = pos.func_177977_b();
            BlockPos shulker = pos.func_177972_a(facing);
            BlockPos redstone = this.getRedStonePos(pos, shulker);
            if (redstone != null) {
               BlockPos hopper = pos.func_177977_b().func_177972_a(facing);
               if (this.isAreaPlaceable(base, pos, shulker, redstone, hopper)) {
                  posList.add(new AutoDispenser32k.DispenserPos(base, pos, redstone, shulker, hopper));
               }
            }
         }
      }

      return posList;
   }

   private boolean isAreaPlaceable(BlockPos base, BlockPos dispenser, BlockPos shulker, BlockPos redstone, BlockPos hopper) {
      if (this.inRange(dispenser) && this.inRange(redstone) && this.inRange(hopper)) {
         if (Math.abs((double)dispenser.field_177960_b - (mc.field_71439_g.field_70163_u + (double)mc.field_71439_g.func_70047_e())) >= 2.0D && Math.sqrt(mc.field_71439_g.func_70011_f((double)dispenser.field_177962_a + 0.5D, mc.field_71439_g.field_70163_u, (double)dispenser.field_177961_c + 0.5D)) <= 2.0D) {
            return false;
         } else if ((BurrowUtil.getFirstFacing(dispenser) == null || BurrowUtil.getFirstFacing(hopper) == null) && (this.intersectsWithEntity(base) || BurrowUtil.getFirstFacing(base) == null || !BlockUtil.canReplace(base) || !this.inRange(base))) {
            return false;
         } else {
            return this.canPlace(dispenser) && this.canPlace(shulker) && this.canPlace(redstone) && this.canPlace(hopper);
         }
      } else {
         return false;
      }
   }

   public void onEnable() {
      if (mc.field_71439_g != null && mc.field_71441_e != null && !mc.field_71439_g.field_70128_L) {
         this.placeTarget = null;
         this.oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
         this.hopperSlot = this.redstoneSlot = this.shulkerSlot = this.dispenserSlot = this.obiSlot = -1;

         int i;
         ItemStack stack;
         Block block;
         for(i = 0; i < 36; ++i) {
            stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemBlock) {
               block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
               if (BlockUtil.shulkerList.contains(block)) {
                  this.shulkerSlot = i;
                  break;
               }
            }
         }

         for(i = 0; i < 9 && (this.obiSlot == -1 || this.dispenserSlot == -1 || this.redstoneSlot == -1 || this.hopperSlot == -1); ++i) {
            stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemBlock) {
               block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
               if (block == Blocks.field_150438_bZ) {
                  this.hopperSlot = i;
               } else if (block == Blocks.field_150343_Z) {
                  this.obiSlot = i;
               } else if (block == Blocks.field_150367_z) {
                  this.dispenserSlot = i;
               } else if (block == Blocks.field_150451_bX) {
                  this.redstoneSlot = i;
               }
            }
         }

         if (this.obiSlot == -1 || this.dispenserSlot == -1 || this.shulkerSlot == -1 || this.redstoneSlot == -1 || this.hopperSlot == -1) {
            if ((Boolean)this.debugMessages.getValue()) {
               MessageBus.sendClientPrefixMessage("Item missing, " + ((ColorMain)ModuleManager.getModule(ColorMain.class)).getModuleColor() + "AutoDispenser32k" + ChatFormatting.GRAY + " disabling.", Notification.Type.ERROR);
            }

            this.disable();
         }

         this.stage = 0;
      } else {
         this.disable();
      }
   }

   public BlockPos getRedStonePos(BlockPos dispenser, BlockPos shulkerPos) {
      List<BlockPos> redstone = new ArrayList();
      EnumFacing[] var4 = EnumFacing.field_82609_l;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         EnumFacing facing = var4[var6];
         if (facing != EnumFacing.DOWN) {
            BlockPos redstonePos = dispenser.func_177972_a(facing);
            if (this.canPlace(redstonePos) && !this.isPos2(redstonePos, shulkerPos)) {
               redstone.add(redstonePos);
            }
         }
      }

      return (BlockPos)redstone.stream().min(Comparator.comparing((p) -> {
         return mc.field_71439_g.func_70011_f((double)p.func_177958_n() + 0.5D, (double)p.func_177956_o() + 0.5D, (double)p.func_177952_p() + 0.5D);
      })).orElse((Object)null);
   }

   public void onUpdate() {
      if (mc.field_71439_g != null && mc.field_71441_e != null && !mc.field_71439_g.field_70128_L) {
         switch(this.stage) {
         case 0:
            this.delay = 10;
            this.waited = 0;
            List<AutoDispenser32k.DispenserPos> canPlaceLocation = this.getPlaceableBlocks();
            EntityPlayer targetPlayer = (EntityPlayer)mc.field_71441_e.field_73010_i.stream().filter((e) -> {
               return e != mc.field_71439_g && !SocialManager.isFriend(e.func_70005_c_());
            }).min(Comparator.comparing((e) -> {
               return mc.field_71439_g.func_70032_d(e);
            })).orElse((Object)null);
            if (targetPlayer != null) {
               if ((Boolean)this.placeCloseToEnemy.getValue()) {
                  this.placeTarget = (AutoDispenser32k.DispenserPos)canPlaceLocation.stream().min(Comparator.comparing((e) -> {
                     return BlockUtil.blockDistance((double)e.hopper.field_177962_a + 0.5D, (double)e.hopper.field_177960_b + 0.5D, (double)e.hopper.field_177961_c + 0.5D, targetPlayer);
                  })).orElse((Object)null);
               } else {
                  this.placeTarget = (AutoDispenser32k.DispenserPos)canPlaceLocation.stream().max(Comparator.comparing((e) -> {
                     return this.getWeight(e.hopper, targetPlayer);
                  })).orElse((Object)null);
               }
            } else {
               this.placeTarget = (AutoDispenser32k.DispenserPos)canPlaceLocation.stream().min(Comparator.comparing((e) -> {
                  return BlockUtil.blockDistance((double)e.hopper.field_177962_a, (double)e.hopper.field_177960_b, (double)e.hopper.field_177961_c, mc.field_71439_g);
               })).orElse((Object)null);
            }

            if (this.placeTarget == null) {
               if ((Boolean)this.debugMessages.getValue()) {
                  MessageBus.sendClientPrefixMessage("No suitable place to place, " + ((ColorMain)ModuleManager.getModule(ColorMain.class)).getModuleColor() + "AutoDispenser32k" + ChatFormatting.GRAY + " disabling.", Notification.Type.ERROR);
               }

               this.disable();
            } else {
               if (BurrowUtil.getFirstFacing(this.placeTarget.dispenser) == null || BurrowUtil.getFirstFacing(this.placeTarget.hopper) == null) {
                  mc.field_71439_g.field_71071_by.field_70461_c = this.obiSlot;
                  BurrowUtil.placeBlock(this.placeTarget.base, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
               }

               mc.field_71439_g.field_71071_by.field_70461_c = this.dispenserSlot;
               float[] angle = MathUtil.calcAngle(new Vec3d(this.placeTarget.dispenser), new Vec3d(this.placeTarget.shulker));
               mc.field_71439_g.field_71174_a.func_147297_a(new Rotation(angle[0] + 180.0F, angle[1], true));
               this.placeBlock(this.placeTarget.dispenser);
               if (ColorMain.INSTANCE.sneaking) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
               }

               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(this.placeTarget.dispenser, EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
               if ((Boolean)this.silent.getValue()) {
                  mc.field_71439_g.field_71071_by.field_70461_c = this.oldslot;
               }

               ++this.stage;
            }
            break;
         case 1:
            if (mc.field_71462_r instanceof GuiDispenser) {
               if (this.shulkerSlot > 8) {
                  mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71070_bA.field_75152_c, this.shulkerSlot, 0, ClickType.QUICK_MOVE, mc.field_71439_g);
               } else {
                  mc.field_71442_b.func_187098_a(mc.field_71439_g.field_71070_bA.field_75152_c, 1, this.shulkerSlot, ClickType.SWAP, mc.field_71439_g);
               }

               mc.field_71439_g.func_71053_j();
               ++this.stage;
            }
            break;
         case 2:
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
            mc.field_71439_g.field_71071_by.field_70461_c = this.redstoneSlot;
            BurrowUtil.placeBlock(this.placeTarget.redstone, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
            if ((Boolean)this.silent.getValue()) {
               mc.field_71439_g.field_71071_by.field_70461_c = this.oldslot;
            }

            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
            ++this.stage;
            break;
         case 3:
            if (!(Boolean)this.hopperWait.getValue()) {
               mc.field_71439_g.field_71071_by.field_70461_c = this.hopperSlot;
               BurrowUtil.placeBlock(this.placeTarget.hopper, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
               if (ColorMain.INSTANCE.sneaking) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
               }

               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(this.placeTarget.hopper, EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
               mc.field_71439_g.field_71071_by.field_70461_c = (Boolean)this.silent.getValue() ? this.oldslot : this.shulkerSlot;
               this.stage = 0;
               this.disable();
            } else if (this.waited < this.delay && !(BlockUtil.getBlock(this.placeTarget.shulker) instanceof BlockShulkerBox)) {
               ++this.waited;
            } else {
               mc.field_71439_g.field_71071_by.field_70461_c = this.hopperSlot;
               BurrowUtil.placeBlock(this.placeTarget.hopper, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
               if (ColorMain.INSTANCE.sneaking) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
               }

               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(this.placeTarget.hopper, EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
               mc.field_71439_g.field_71071_by.field_70461_c = (Boolean)this.silent.getValue() ? this.oldslot : this.shulkerSlot;
               this.stage = 0;
               if ((Boolean)this.debugMessages.getValue()) {
                  MessageBus.sendClientPrefixMessage("AutoDispenser32k Place Target: " + this.placeTarget.hopper.field_177962_a + " " + this.placeTarget.hopper.field_177960_b + " " + this.placeTarget.hopper.field_177961_c + " Distance: " + BigDecimal.valueOf(mc.field_71439_g.func_174791_d().func_72438_d(new Vec3d(this.placeTarget.hopper))).setScale(1, roundingMode), Notification.Type.SUCCESS);
               }

               this.disable();
            }
         }

      } else {
         this.disable();
      }
   }

   private void placeBlock(BlockPos pos) {
      if (BlockUtil.canReplace(pos)) {
         EnumFacing side = BurrowUtil.getFirstFacing(pos);
         if (side != null) {
            BlockPos neighbour = pos.func_177972_a(side);
            EnumFacing opposite = side.func_176734_d();
            if (BlockUtil.canBeClicked(neighbour)) {
               Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
               boolean sneak = false;
               if ((BlockUtil.blackList.contains(mc.field_71441_e.func_180495_p(neighbour).func_177230_c()) || BlockUtil.shulkerList.contains(mc.field_71441_e.func_180495_p(neighbour).func_177230_c())) && !mc.field_71439_g.func_70093_af()) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
                  mc.field_71439_g.func_70095_a(true);
                  sneak = true;
               }

               if ((Boolean)this.packet.getValue()) {
                  BlockUtil.rightClickBlock(neighbour, hitVec, EnumHand.MAIN_HAND, opposite);
               } else {
                  mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
                  if ((Boolean)this.swing.getValue()) {
                     mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                  }
               }

               if ((Boolean)this.dispenserRotate.getValue()) {
                  BlockUtil.faceVector(hitVec);
               }

               if (sneak) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
                  mc.field_71439_g.func_70095_a(false);
               }

            }
         }
      }
   }

   private boolean canPlace(BlockPos pos) {
      return !this.intersectsWithEntity(pos) && BlockUtil.canReplace(pos);
   }

   private boolean intersectsWithEntity(BlockPos pos) {
      Iterator var2 = mc.field_71441_e.field_72996_f.iterator();

      Entity entity;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         entity = (Entity)var2.next();
      } while(entity.field_70128_L || entity instanceof EntityItem || entity instanceof EntityXPOrb || entity instanceof EntityExpBottle || entity instanceof EntityArrow || !(new AxisAlignedBB(pos)).func_72326_a(entity.func_174813_aQ()));

      return true;
   }

   public boolean isPos2(BlockPos pos1, BlockPos pos2) {
      if (pos1 != null && pos2 != null) {
         return pos1.field_177962_a == pos2.field_177962_a && pos1.field_177960_b == pos2.field_177960_b && pos1.field_177961_c == pos2.field_177961_c;
      } else {
         return false;
      }
   }

   private boolean inRange(BlockPos pos) {
      double x = (double)pos.field_177962_a - mc.field_71439_g.field_70165_t;
      double z = (double)pos.field_177961_c - mc.field_71439_g.field_70161_v;
      double y = (double)(pos.field_177960_b - PlayerUtil.getEyesPos().field_177960_b);
      double add = Math.sqrt(y * y) / 2.0D;
      return x * x + z * z <= ((Double)this.range.getValue() - add) * ((Double)this.range.getValue() - add) && y * y <= (Double)this.yRange.getValue() * (Double)this.yRange.getValue();
   }

   static {
      roundingMode = RoundingMode.CEILING;
   }

   static class DispenserPos {
      BlockPos base;
      BlockPos dispenser;
      BlockPos redstone;
      BlockPos shulker;
      BlockPos hopper;

      public DispenserPos(BlockPos base, BlockPos dispenser, BlockPos redstone, BlockPos shulker, BlockPos hopper) {
         this.base = base;
         this.dispenser = dispenser;
         this.redstone = redstone;
         this.shulker = shulker;
         this.hopper = hopper;
      }
   }
}

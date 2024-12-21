package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.chat.Notification;
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
import net.minecraft.block.BlockObsidian;
import net.minecraft.client.gui.inventory.GuiContainer;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(
   name = "AutoHopper32k",
   category = Category.Combat
)
public class AutoHopper32k extends Module {
   private static final int scale = 1;
   private static final RoundingMode roundingMode;
   BooleanSetting moveToHotbar = this.registerBoolean("Move 32k to Hotbar", true);
   DoubleSetting placeRange = this.registerDouble("Place range", 4.0D, 0.0D, 10.0D);
   IntegerSetting yOffset = this.registerInteger("Y Offset (Hopper)", 2, 0, 10);
   DoubleSetting targetRange = this.registerDouble("Target Range", 4.0D, 0.0D, 16.0D);
   BooleanSetting placeCloseToEnemy = this.registerBoolean("Place close to enemy", false);
   BooleanSetting placeObiOnTop = this.registerBoolean("Place Obi on Top", true);
   BooleanSetting rotate = this.registerBoolean("Rotate", false);
   BooleanSetting packet = this.registerBoolean("Packet", false);
   BooleanSetting swing = this.registerBoolean("Swing", true);
   BooleanSetting silent = this.registerBoolean("Silent", true);
   BooleanSetting debugMessages = this.registerBoolean("Debug Messages", true);
   int oldslot;
   private int swordSlot;

   private double getWeight(BlockPos pos, EntityPlayer target) {
      double range = target.func_70011_f((double)pos.field_177962_a + 0.5D, (double)pos.field_177960_b + 0.5D, (double)pos.field_177961_c + 0.5D);
      if (range >= (Double)this.targetRange.getValue()) {
         int y = 256 - pos.func_177956_o();
         range += (double)(y * 100);
      }

      return range;
   }

   protected void onEnable() {
      if (mc.field_71439_g != null && mc.field_71441_e != null && !mc.field_71439_g.field_70128_L) {
         this.oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
         int hopperSlot = -1;
         int shulkerSlot = -1;
         int obiSlot = -1;
         this.swordSlot = -1;

         for(int i = 0; i < 9 && (hopperSlot == -1 || shulkerSlot == -1 || obiSlot == -1); ++i) {
            ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemBlock) {
               Block block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
               if (block == Blocks.field_150438_bZ) {
                  hopperSlot = i;
               } else if (BlockUtil.shulkerList.contains(block)) {
                  shulkerSlot = i;
               } else if (block == Blocks.field_150343_Z) {
                  obiSlot = i;
               }
            }
         }

         if (hopperSlot == -1) {
            if ((Boolean)this.debugMessages.getValue()) {
               MessageBus.sendClientPrefixMessage("Hopper missing, " + ((ColorMain)ModuleManager.getModule(ColorMain.class)).getModuleColor() + "AutoHopper32k" + ChatFormatting.GRAY + " disabling.", Notification.Type.ERROR);
            }

            this.disable();
         } else if (shulkerSlot == -1) {
            if ((Boolean)this.debugMessages.getValue()) {
               MessageBus.sendClientPrefixMessage("Shulker missing, " + ((ColorMain)ModuleManager.getModule(ColorMain.class)).getModuleColor() + "AutoHopper32k" + ChatFormatting.GRAY + " disabling.", Notification.Type.ERROR);
            }

            this.disable();
         } else {
            double range = (Double)this.placeRange.getValue();
            double yRange = (double)(Integer)this.yOffset.getValue();
            List<BlockPos> placeTargetList = EntityUtil.getSphere(PlayerUtil.getEyesPos(), range, yRange, false, false, 0);
            List<BlockPos> placeTargetMap = new ArrayList();
            Iterator var13 = placeTargetList.iterator();

            while(var13.hasNext()) {
               BlockPos placeTargetTest = (BlockPos)var13.next();
               if (this.isAreaPlaceable(placeTargetTest)) {
                  placeTargetMap.add(placeTargetTest);
               }
            }

            EntityPlayer targetPlayer = (EntityPlayer)mc.field_71441_e.field_73010_i.stream().filter((e) -> {
               return e != mc.field_71439_g && !SocialManager.isFriend(e.func_70005_c_());
            }).min(Comparator.comparing((e) -> {
               return mc.field_71439_g.func_70032_d(e);
            })).orElse((Object)null);
            BlockPos placeTarget;
            if (targetPlayer != null) {
               if ((Boolean)this.placeCloseToEnemy.getValue()) {
                  placeTarget = (BlockPos)placeTargetMap.stream().min(Comparator.comparing((e) -> {
                     return BlockUtil.blockDistance((double)e.field_177962_a, (double)e.field_177960_b, (double)e.field_177961_c, targetPlayer);
                  })).orElse((Object)null);
               } else {
                  placeTarget = (BlockPos)placeTargetMap.stream().max(Comparator.comparing((e) -> {
                     return this.getWeight(e, targetPlayer);
                  })).orElse((Object)null);
               }
            } else {
               placeTarget = (BlockPos)placeTargetMap.stream().min(Comparator.comparing((e) -> {
                  return BlockUtil.blockDistance((double)e.field_177962_a, (double)e.field_177960_b, (double)e.field_177961_c, mc.field_71439_g);
               })).orElse((Object)null);
            }

            if (placeTarget == null) {
               if ((Boolean)this.debugMessages.getValue()) {
                  MessageBus.sendClientPrefixMessage("No valid position in range to place, " + ((ColorMain)ModuleManager.getModule(ColorMain.class)).getModuleColor() + "AutoHopper32k" + ChatFormatting.GRAY + " disabling.", Notification.Type.ERROR);
               }

               this.disable();
            } else {
               if ((Boolean)this.debugMessages.getValue()) {
                  MessageBus.sendClientPrefixMessage("AutoHopper32k Place Target: " + placeTarget.field_177962_a + " " + placeTarget.field_177960_b + " " + placeTarget.field_177961_c + " Distance: " + BigDecimal.valueOf(mc.field_71439_g.func_174791_d().func_72438_d(new Vec3d(placeTarget))).setScale(1, roundingMode), Notification.Type.SUCCESS);
               }

               mc.field_71439_g.field_71071_by.field_70461_c = hopperSlot;
               BurrowUtil.placeBlockDown(placeTarget, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
               mc.field_71439_g.field_71071_by.field_70461_c = shulkerSlot;
               BurrowUtil.placeBlockDown(placeTarget.func_177982_a(0, 1, 0), EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
               if ((Boolean)this.placeObiOnTop.getValue()) {
                  int obsidian = BurrowUtil.findHotbarBlock(BlockObsidian.class);
                  if (obsidian != -1) {
                     mc.field_71439_g.field_71071_by.field_70461_c = obsidian;
                     BurrowUtil.placeBlockDown(placeTarget.func_177982_a(0, 2, 0), EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                     if (!(Boolean)this.silent.getValue()) {
                        mc.field_71439_g.field_71071_by.field_70461_c = shulkerSlot;
                     }
                  }
               }

               if ((Boolean)this.silent.getValue()) {
                  mc.field_71439_g.field_71071_by.field_70461_c = this.oldslot;
               }

               if (ColorMain.INSTANCE.sneaking) {
                  mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
               }

               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(placeTarget, EnumFacing.DOWN, EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
               this.swordSlot = shulkerSlot + 32;
            }
         }
      } else {
         this.disable();
      }
   }

   public void onUpdate() {
      if (mc.field_71439_g != null && mc.field_71441_e != null && !mc.field_71439_g.field_70128_L) {
         if (mc.field_71462_r instanceof GuiContainer) {
            if (!(Boolean)this.moveToHotbar.getValue()) {
               this.disable();
            } else if (this.swordSlot != -1) {
               boolean swapReady = !((GuiContainer)mc.field_71462_r).field_147002_h.func_75139_a(0).func_75211_c().field_190928_g;
               if (!((GuiContainer)mc.field_71462_r).field_147002_h.func_75139_a(this.swordSlot).func_75211_c().field_190928_g) {
                  swapReady = false;
               }

               if (swapReady) {
                  mc.field_71442_b.func_187098_a(((GuiContainer)mc.field_71462_r).field_147002_h.field_75152_c, 0, this.swordSlot - 32, ClickType.SWAP, mc.field_71439_g);
                  this.disable();
               }

            }
         }
      } else {
         this.disable();
      }
   }

   private boolean isAreaPlaceable(BlockPos blockPos) {
      return this.canPlace(blockPos) && this.inRange(blockPos) && this.canPlace(blockPos.func_177984_a()) && this.inRange(blockPos.func_177984_a()) && (!(Boolean)this.placeObiOnTop.getValue() || !this.canPlace(blockPos) || this.inRange(blockPos));
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
      return x * x + z * z <= ((Double)this.placeRange.getValue() - add) * ((Double)this.placeRange.getValue() - add) && y * y <= (Double)this.placeRange.getValue() * (Double)this.placeRange.getValue();
   }

   static {
      roundingMode = RoundingMode.CEILING;
   }
}

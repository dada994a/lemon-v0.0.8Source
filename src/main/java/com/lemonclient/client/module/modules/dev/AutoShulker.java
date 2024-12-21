package com.lemonclient.client.module.modules.dev;

import com.lemonclient.api.event.events.DeathEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.InventoryUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.modules.gui.ColorMain;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketEntityAction.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Module.Declaration(
   name = "AutoShulker",
   category = Category.Dev
)
public class AutoShulker extends Module {
   BooleanSetting once = this.registerBoolean("Once", false);
   IntegerSetting counts = this.registerInteger("EmptySlots", 6, 1, 36, () -> {
      return !(Boolean)this.once.getValue();
   });
   BooleanSetting disable = this.registerBoolean("Disable After Death", true, () -> {
      return !(Boolean)this.once.getValue();
   });
   DoubleSetting range = this.registerDouble("Range", 5.0D, 0.0D, 10.0D);
   DoubleSetting yRange = this.registerDouble("YRange", 5.0D, 0.0D, 10.0D);
   DoubleSetting targetRange = this.registerDouble("Target Range", 8.0D, 0.0D, 16.0D);
   IntegerSetting tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
   BooleanSetting inventory = this.registerBoolean("Inventory", true);
   IntegerSetting Slot = this.registerInteger("Slot", 1, 1, 9);
   BooleanSetting packetPlace = this.registerBoolean("Packet Place", true);
   BooleanSetting placeSwing = this.registerBoolean("Place Swing", true);
   BooleanSetting packetSwing = this.registerBoolean("Packet Swing", true);
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", true);
   private int delayTimeTicks;
   BlockPos playerPos;
   AutoShulker.ShulkerPos blockAim;
   List<BlockPos> list = new ArrayList();
   int slot;
   boolean swapped = false;
   @EventHandler
   private final Listener<DeathEvent> deathEventListener = new Listener((event) -> {
      if (event.player == mc.field_71439_g && (Boolean)this.disable.getValue()) {
         this.disable();
      }

   }, new Predicate[0]);

   private void switchTo(int slot, Runnable runnable) {
      int oldslot = mc.field_71439_g.field_71071_by.field_70461_c;
      if (slot >= 0 && slot != oldslot) {
         if (slot < 9) {
            boolean packetSwitch = (Boolean)this.packetSwitch.getValue();
            if (packetSwitch) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
            } else {
               mc.field_71439_g.field_71071_by.field_70461_c = slot;
            }

            runnable.run();
            if (packetSwitch) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(oldslot));
            } else {
               mc.field_71439_g.field_71071_by.field_70461_c = oldslot;
            }
         }

      } else {
         runnable.run();
      }
   }

   private int getShulkerSlot() {
      for(int i = 0; i < mc.field_71439_g.field_71071_by.field_70462_a.size(); ++i) {
         if (mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b() instanceof ItemBlock && ((ItemBlock)mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b()).func_179223_d() instanceof BlockShulkerBox) {
            return i;
         }
      }

      return -1;
   }

   private boolean isPos2(BlockPos pos1, BlockPos pos2) {
      if (pos1 != null && pos2 != null) {
         return pos1.field_177962_a == pos2.field_177962_a && pos1.field_177960_b == pos2.field_177960_b && pos1.field_177961_c == pos2.field_177961_c;
      } else {
         return false;
      }
   }

   private void initValues() {
      List<BlockPos> blocks = EntityUtil.getSphere(PlayerUtil.getEyesPos(), (Double)this.range.getValue() + 1.0D, (Double)this.yRange.getValue() + 1.0D, false, true, 0);
      blocks.removeIf((p) -> {
         return ColorMain.INSTANCE.breakList.contains(p) || this.list.contains(p);
      });
      List<AutoShulker.ShulkerPos> posList = new ArrayList();
      blocks.forEach((pos) -> {
         EnumFacing facing = this.getFacing(pos);
         if (facing != null) {
            BlockPos neighbour = pos.func_177972_a(facing);
            EnumFacing opposite = facing.func_176734_d();
            Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
            if (this.inRange(hitVec)) {
               posList.add(new AutoShulker.ShulkerPos(pos, facing, neighbour, opposite, hitVec));
            }

         }
      });
      EntityPlayer target = PlayerUtil.getNearestPlayer(12.0D);
      if (target == null) {
         this.blockAim = (AutoShulker.ShulkerPos)posList.stream().min(Comparator.comparing((p) -> {
            return p.getRange(mc.field_71439_g);
         })).orElse((Object)null);
      } else {
         this.blockAim = (AutoShulker.ShulkerPos)posList.stream().max(Comparator.comparing((p) -> {
            return this.getWeight(p, target);
         })).orElse((Object)null);
      }

      if (this.blockAim != null) {
         this.list.add(this.blockAim.pos);
      }
   }

   private double getWeight(AutoShulker.ShulkerPos pos, EntityPlayer target) {
      double range = pos.getRange(target);
      if (range >= (Double)this.targetRange.getValue()) {
         int y = 256 - pos.pos.func_177956_o();
         range += (double)(y * 100);
      }

      return range;
   }

   private boolean intersectsWithEntity(BlockPos pos) {
      Iterator var2 = mc.field_71441_e.field_72996_f.iterator();

      Entity entity;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         entity = (Entity)var2.next();
      } while(entity instanceof EntityItem || !(new AxisAlignedBB(pos)).func_72326_a(entity.func_174813_aQ()));

      return true;
   }

   private EnumFacing getFacing(BlockPos pos) {
      if (!this.intersectsWithEntity(pos) && (BlockUtil.canReplace(pos) || BlockUtil.getBlock(pos) instanceof BlockShulkerBox)) {
         EnumFacing[] var2 = EnumFacing.field_82609_l;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            EnumFacing facing = var2[var4];
            if (BlockUtil.canBeClicked(pos.func_177972_a(facing)) && BlockUtil.airBlocks.contains(mc.field_71441_e.func_180495_p(pos.func_177967_a(facing, -1)).func_177230_c())) {
               return facing;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private boolean inRange(Vec3d vec) {
      double x = vec.field_72450_a - mc.field_71439_g.field_70165_t;
      double z = vec.field_72449_c - mc.field_71439_g.field_70161_v;
      double y = vec.field_72448_b - (double)PlayerUtil.getEyesPos().field_177960_b;
      double add = Math.sqrt(y * y) / 2.0D;
      return x * x + z * z <= ((Double)this.range.getValue() - add) * ((Double)this.range.getValue() - add) && y * y <= (Double)this.yRange.getValue() * (Double)this.yRange.getValue();
   }

   private boolean inRange(BlockPos pos) {
      double x = (double)pos.field_177962_a + 0.5D - mc.field_71439_g.field_70165_t;
      double z = (double)pos.field_177961_c + 0.5D - mc.field_71439_g.field_70161_v;
      double y = (double)pos.field_177960_b + 0.5D - (double)PlayerUtil.getEyesPos().field_177960_b;
      double add = Math.sqrt(y * y) / 2.0D;
      return x * x + z * z <= ((Double)this.range.getValue() - add) * ((Double)this.range.getValue() - add) && y * y <= (Double)this.yRange.getValue() * (Double)this.yRange.getValue();
   }

   public void onUpdate() {
      if (mc.field_71439_g != null) {
         if (mc.field_71462_r instanceof GuiShulkerBox) {
            this.blockAim = null;
         } else if (this.delayTimeTicks < (Integer)this.tickDelay.getValue()) {
            ++this.delayTimeTicks;
         } else {
            this.delayTimeTicks = 0;
            if ((this.slot = this.getShulkerSlot()) != -1) {
               if (!(Boolean)this.once.getValue() && InventoryUtil.getEmptyCounts() < (Integer)this.counts.getValue()) {
                  this.checkPos();
               } else if (this.blockAim == null) {
                  this.initValues();
               }

               if (this.blockAim == null) {
                  if ((Boolean)this.once.getValue()) {
                     this.disable();
                  }

               } else if (!this.inRange(this.blockAim.pos)) {
                  this.blockAim = null;
               } else {
                  if (this.slot > 8 && !this.swapped) {
                     if (!(Boolean)this.inventory.getValue()) {
                        return;
                     }

                     mc.field_71442_b.func_187098_a(0, this.slot, 0, ClickType.SWAP, mc.field_71439_g);
                     mc.field_71442_b.func_187098_a(0, (Integer)this.Slot.getValue() + 35, 0, ClickType.SWAP, mc.field_71439_g);
                     mc.field_71442_b.func_187098_a(0, this.slot, 0, ClickType.SWAP, mc.field_71439_g);
                     mc.field_71442_b.func_78765_e();
                     this.swapped = true;
                     if ((Integer)this.tickDelay.getValue() != 0) {
                        return;
                     }
                  }

                  if (!BlockUtil.isAir(this.blockAim.pos) && !BlockUtil.canReplace(this.blockAim.pos)) {
                     this.openBlock();
                  } else {
                     this.switchTo(this.slot, () -> {
                        boolean sneak = false;
                        if (BlockUtil.blackList.contains(mc.field_71441_e.func_180495_p(this.blockAim.neighbour).func_177230_c()) && !mc.field_71439_g.func_70093_af()) {
                           mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.START_SNEAKING));
                           sneak = true;
                        }

                        BurrowUtil.rightClickBlock(this.blockAim.neighbour, this.blockAim.vec, EnumHand.MAIN_HAND, this.blockAim.opposite, (Boolean)this.packetPlace.getValue());
                        if (sneak) {
                           mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
                        }

                        if ((Boolean)this.placeSwing.getValue()) {
                           this.swing();
                        }

                     });
                     if ((Integer)this.tickDelay.getValue() == 0) {
                        this.openBlock();
                     }
                  }

               }
            }
         }
      }
   }

   private void checkPos() {
      if (!this.isPos2(PlayerUtil.getPlayerPos(), this.playerPos)) {
         this.list = new ArrayList();
         this.playerPos = PlayerUtil.getPlayerPos();
      }

   }

   private void swing() {
      if ((Boolean)this.packetSwing.getValue()) {
         mc.field_71439_g.field_71174_a.func_147297_a(new CPacketAnimation(EnumHand.MAIN_HAND));
      } else {
         mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
      }

   }

   private void openBlock() {
      mc.field_71439_g.field_71174_a.func_147297_a(new CPacketEntityAction(mc.field_71439_g, Action.STOP_SNEAKING));
      EnumFacing side = EnumFacing.func_190914_a(this.blockAim.pos, mc.field_71439_g);
      BlockPos neighbour = this.blockAim.pos.func_177972_a(side);
      EnumFacing opposite = side.func_176734_d();
      Vec3d hitVec = (new Vec3d(neighbour)).func_72441_c(0.5D, 0.5D, 0.5D).func_178787_e((new Vec3d(opposite.func_176730_m())).func_186678_a(0.5D));
      mc.field_71442_b.func_187099_a(mc.field_71439_g, mc.field_71441_e, this.blockAim.pos, opposite, hitVec, EnumHand.MAIN_HAND);
      this.blockAim = null;
      if ((Boolean)this.once.getValue()) {
         this.disable();
      }

   }

   public void onEnable() {
      this.checkPos();
   }

   static class ShulkerPos {
      BlockPos pos;
      EnumFacing facing;
      Vec3d vec;
      BlockPos neighbour;
      EnumFacing opposite;

      public ShulkerPos(BlockPos pos, EnumFacing facing, BlockPos neighbour, EnumFacing opposite, Vec3d vec3d) {
         this.pos = pos;
         this.facing = facing;
         this.neighbour = neighbour;
         this.opposite = opposite;
         this.vec = vec3d;
      }

      public double getRange(EntityPlayer player) {
         return player.func_70011_f((double)this.pos.field_177962_a + 0.5D, (double)this.pos.field_177960_b + 0.5D, (double)this.pos.field_177961_c + 0.5D);
      }
   }
}

package com.lemonclient.client.module.modules.qwq;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.misc.Timing;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockTNT;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "TNTAura",
   category = Category.qwq
)
public class TNTAura extends Module {
   IntegerSetting delay = this.registerInteger("Delay", 100, 0, 2000);
   IntegerSetting range = this.registerInteger("Range", 5, 0, 10);
   BooleanSetting rotate = this.registerBoolean("Rotate", false);
   BooleanSetting packet = this.registerBoolean("Packet Place", false);
   BooleanSetting swing = this.registerBoolean("Swing", false);
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", false);
   BooleanSetting check = this.registerBoolean("Switch Check", true);
   BooleanSetting trap = this.registerBoolean("Trap", false);
   BooleanSetting doubleTnt = this.registerBoolean("Double", false);
   IntegerSetting maxTarget = this.registerInteger("Max Target", 1, 1, 10);
   IntegerSetting blocksPerTick = this.registerInteger("Blocks Per Tick", 8, 1, 20);
   DoubleSetting maxSpeed = this.registerDouble("Max Target Speed", 10.0D, 0.0D, 50.0D);
   int placed;
   Timing timer = new Timing();
   List<EntityPlayer> list = new ArrayList();
   BlockPos[] sides = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1)};
   BlockPos[] high = new BlockPos[]{new BlockPos(0, 1, 0), new BlockPos(0, 2, 0), new BlockPos(0, 3, 0)};

   private void switchTo(int slot) {
      if (slot > -1 && slot < 9 && (!(Boolean)this.check.getValue() || mc.field_71439_g.field_71071_by.field_70461_c != slot)) {
         if ((Boolean)this.packetSwitch.getValue()) {
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketHeldItemChange(slot));
         } else {
            mc.field_71439_g.field_71071_by.field_70461_c = slot;
            mc.field_71442_b.func_78765_e();
         }
      }

   }

   public void onEnable() {
      this.placed = 0;
      this.timer.reset();
      this.list = new ArrayList();
   }

   public void fast() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         int tnt = BurrowUtil.findHotbarBlock(BlockTNT.class);
         int flint = BurrowUtil.findHotbarBlock(Items.field_151033_d.getClass());
         if (tnt != -1 && flint != -1) {
            List<EntityPlayer> targetList = PlayerUtil.getNearPlayers((double)(Integer)this.range.getValue(), (Integer)this.maxTarget.getValue());
            if (!targetList.isEmpty()) {
               List<EntityPlayer> targets = new ArrayList();
               Iterator var5 = targetList.iterator();

               while(var5.hasNext()) {
                  EntityPlayer entityPlayer = (EntityPlayer)var5.next();
                  if (!this.list.contains(entityPlayer)) {
                     targets.add(entityPlayer);
                  }
               }

               if (targets.isEmpty()) {
                  this.list.clear();
                  targets.addAll(targetList);
               }

               int count = targetList.size();
               if (this.timer.passedMs((long)((Integer)this.delay.getValue() / count))) {
                  this.timer.reset();
                  this.placed = 0;

                  int oldSlot;
                  for(Iterator var23 = targetList.iterator(); var23.hasNext(); this.switchTo(oldSlot)) {
                     EntityPlayer target = (EntityPlayer)var23.next();
                     AutoEz.INSTANCE.addTargetedPlayer(target.func_70005_c_());
                     this.list.add(target);
                     BlockPos pos = EntityUtil.getPlayerPos(target);
                     BlockPos tntPos = pos.func_177981_b(2);
                     if (mc.field_71439_g.func_70032_d(target) > (float)(Integer)this.range.getValue() || LemonClient.speedUtil.getPlayerSpeed(target) > (Double)this.maxSpeed.getValue() || !BlockUtil.isAir(pos.func_177984_a())) {
                        return;
                     }

                     int obsi = BurrowUtil.findHotbarBlock(BlockObsidian.class);
                     oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
                     if (obsi == -1) {
                        return;
                     }

                     List<BlockPos> trap = new ArrayList();
                     BlockPos[] var13 = this.sides;
                     int var14 = var13.length;

                     for(int var15 = 0; var15 < var14; ++var15) {
                        BlockPos side = var13[var15];
                        BlockPos[] var17 = this.high;
                        int var18 = var17.length;

                        for(int var19 = 0; var19 < var18; ++var19) {
                           BlockPos high = var17[var19];
                           if ((Boolean)this.doubleTnt.getValue() || high.func_177956_o() != 3) {
                              BlockPos blockPos = pos.func_177971_a(side).func_177971_a(high);
                              if (!this.intersectsWithEntity(blockPos) && BlockUtil.isAir(blockPos)) {
                                 trap.add(blockPos);
                              }
                           }
                        }
                     }

                     if ((Boolean)this.trap.getValue()) {
                        BlockPos north = tntPos.func_177978_c();
                        trap.add(north.func_177984_a());
                        trap.add(north.func_177981_b(2));
                        trap.add(tntPos.func_177981_b(2));
                     }

                     this.switchTo(obsi);
                     Iterator var26 = trap.iterator();

                     while(var26.hasNext()) {
                        BlockPos trapPos = (BlockPos)var26.next();
                        ItemStack stack = mc.field_71439_g.field_71071_by.func_70301_a(obsi);
                        if (this.placed >= (Integer)this.blocksPerTick.getValue() || stack == ItemStack.field_190927_a) {
                           this.switchTo(oldSlot);
                           return;
                        }

                        if (BlockUtil.isAir(trapPos)) {
                           BurrowUtil.placeBlock(trapPos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                           ++this.placed;
                        }
                     }

                     this.switchTo(oldSlot);
                     boolean can = this.canPlace(tntPos);
                     boolean canDouble = this.canPlace(tntPos.func_177984_a());
                     if (!can) {
                        return;
                     }

                     this.switchTo(tnt);
                     BurrowUtil.placeBlock(tntPos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                     if ((Boolean)this.doubleTnt.getValue() && canDouble) {
                        BurrowUtil.placeBlock(tntPos.func_177984_a(), EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                     }

                     this.switchTo(flint);
                     EnumFacing facing = EnumFacing.DOWN;
                     mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(tntPos, facing, EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
                     if ((Boolean)this.doubleTnt.getValue() && canDouble) {
                        mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerTryUseItemOnBlock(tntPos.func_177984_a(), facing, EnumHand.MAIN_HAND, 0.0F, 0.0F, 0.0F));
                     }
                  }

               }
            }
         }
      }
   }

   private boolean canPlace(BlockPos pos) {
      return BlockUtil.hasNeighbour(pos) && BlockUtil.isAir(pos) || mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150335_W;
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
}

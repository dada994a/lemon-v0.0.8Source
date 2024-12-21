package com.lemonclient.client.module.modules.dev;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.UnmodifiableIterator;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "AntiHolePush",
   category = Category.Dev
)
public class AntiHolePush extends Module {
   ModeSetting timeMode = this.registerMode("Time Mode", Arrays.asList("onUpdate", "Tick", "Both", "Fast"), "Fast");
   BooleanSetting packet = this.registerBoolean("Packet Place", false);
   BooleanSetting swing = this.registerBoolean("Swing", false);
   BooleanSetting rotate = this.registerBoolean("Rotate", true);
   BooleanSetting strict = this.registerBoolean("Strict", true);
   BooleanSetting raytrace = this.registerBoolean("RayTrace", true);
   BooleanSetting trap = this.registerBoolean("Trap", true);
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", false);
   BooleanSetting entityCheck = this.registerBoolean("Entity Check", true);
   BooleanSetting breakPiston = this.registerBoolean("Break Piston", false);

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

   public void onUpdate() {
      if (((String)this.timeMode.getValue()).equalsIgnoreCase("onUpdate") || ((String)this.timeMode.getValue()).equalsIgnoreCase("Both")) {
         this.block();
      }

   }

   public void onTick() {
      if (((String)this.timeMode.getValue()).equalsIgnoreCase("Tick") || ((String)this.timeMode.getValue()).equalsIgnoreCase("Both")) {
         this.block();
      }

   }

   public void fast() {
      if (((String)this.timeMode.getValue()).equalsIgnoreCase("Fast")) {
         this.block();
      }

   }

   private void block() {
      if (mc.field_71439_g != null && mc.field_71441_e != null) {
         BlockPos pos = new BlockPos(mc.field_71439_g.field_70165_t, mc.field_71439_g.field_70163_u, mc.field_71439_g.field_70161_v);
         int obsidian = BurrowUtil.findHotbarBlock(BlockObsidian.class);
         if (obsidian != -1) {
            BlockPos head = pos.func_177982_a(0, 2, 0);
            BlockPos pos1 = pos.func_177982_a(1, 1, 0);
            BlockPos pos2 = pos.func_177982_a(-1, 1, 0);
            BlockPos pos3 = pos.func_177982_a(0, 1, 1);
            BlockPos pos4 = pos.func_177982_a(0, 1, -1);
            if (this.airBlock(head)) {
               List<BlockPos> posList = new ArrayList();
               BlockPos pos8;
               if (this.isPiston(pos1) && isFacing(pos1, EnumFacing.WEST)) {
                  pos8 = pos.func_177982_a(-1, 2, 0);
                  if (this.airBlock(pos2) && this.airBlock(pos8)) {
                     posList.add(pos2);
                  }

                  if ((Boolean)this.trap.getValue() && this.airBlock(head)) {
                     posList.add(pos2.func_177984_a());
                     posList.add(head);
                  }

                  if ((Boolean)this.breakPiston.getValue()) {
                     mc.field_71442_b.func_180512_c(pos1, BlockUtil.getRayTraceFacing(pos3));
                  }
               }

               if (this.isPiston(pos2) && isFacing(pos2, EnumFacing.EAST)) {
                  pos8 = pos.func_177982_a(1, 2, 0);
                  if (this.airBlock(pos1) && this.airBlock(pos8)) {
                     posList.add(pos1);
                  }

                  if ((Boolean)this.trap.getValue() && this.airBlock(head)) {
                     posList.add(pos1.func_177984_a());
                     posList.add(head);
                  }

                  if ((Boolean)this.breakPiston.getValue()) {
                     mc.field_71442_b.func_180512_c(pos2, BlockUtil.getRayTraceFacing(pos3));
                  }
               }

               if (this.isPiston(pos3) && isFacing(pos3, EnumFacing.NORTH)) {
                  pos8 = pos.func_177982_a(0, 2, -1);
                  if (this.airBlock(pos4) && this.airBlock(pos8)) {
                     posList.add(pos4);
                  }

                  if ((Boolean)this.trap.getValue() && this.airBlock(head)) {
                     posList.add(pos4.func_177984_a());
                     posList.add(head);
                  }

                  if ((Boolean)this.breakPiston.getValue()) {
                     mc.field_71442_b.func_180512_c(pos3, BlockUtil.getRayTraceFacing(pos3));
                  }
               }

               if (this.isPiston(pos4) && isFacing(pos4, EnumFacing.SOUTH)) {
                  pos8 = pos.func_177982_a(0, 2, 1);
                  if (this.airBlock(pos3) && this.airBlock(pos8)) {
                     posList.add(pos3);
                  }

                  if ((Boolean)this.trap.getValue() && this.airBlock(head)) {
                     posList.add(pos3.func_177984_a());
                     posList.add(head);
                  }

                  if ((Boolean)this.breakPiston.getValue()) {
                     mc.field_71442_b.func_180512_c(pos4, BlockUtil.getRayTraceFacing(pos3));
                  }
               }

               if (!posList.isEmpty()) {
                  this.switchTo(obsidian, () -> {
                     Iterator var2 = posList.iterator();

                     while(var2.hasNext()) {
                        BlockPos placePos = (BlockPos)var2.next();
                        this.perform(placePos);
                     }

                  });
               }

            }
         }
      }
   }

   private IBlockState getBlock(BlockPos block) {
      return mc.field_71441_e.func_180495_p(block);
   }

   private boolean airBlock(BlockPos pos) {
      return BlockUtil.airBlocks.contains(this.getBlock(pos).func_177230_c());
   }

   private void perform(BlockPos pos) {
      if ((!(Boolean)this.entityCheck.getValue() || !this.intersectsWithEntity(pos)) && BlockUtil.canPlace(pos, (Boolean)this.strict.getValue(), (Boolean)this.raytrace.getValue())) {
         BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), (Boolean)this.strict.getValue(), (Boolean)this.raytrace.getValue(), (Boolean)this.swing.getValue());
      }
   }

   public static boolean isFacing(BlockPos pos, EnumFacing enumFacing) {
      ImmutableMap<IProperty<?>, Comparable<?>> properties = mc.field_71441_e.func_180495_p(pos).func_177228_b();
      UnmodifiableIterator var3 = properties.keySet().iterator();

      IProperty prop;
      do {
         do {
            do {
               if (!var3.hasNext()) {
                  return false;
               }

               prop = (IProperty)var3.next();
            } while(prop.func_177699_b() != EnumFacing.class);
         } while(!prop.func_177701_a().equals("facing") && !prop.func_177701_a().equals("rotation"));
      } while(properties.get(prop) != enumFacing);

      return true;
   }

   private boolean isPiston(BlockPos pos) {
      return mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockPistonMoving || mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockPistonBase || mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150331_J || mc.field_71441_e.func_180495_p(pos).func_177230_c() == Blocks.field_150320_F;
   }
}

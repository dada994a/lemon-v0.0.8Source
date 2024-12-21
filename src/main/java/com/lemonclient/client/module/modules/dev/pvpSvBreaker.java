package com.lemonclient.client.module.modules.dev;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockWeb;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "PvpSvBreaker",
   category = Category.Dev
)
public class pvpSvBreaker extends Module {
   ModeSetting mode = this.registerMode("Mode", Arrays.asList("Piston", "EChest", "Web", "Down", "Flat"), "EChest");
   IntegerSetting delay = this.registerInteger("Delay", 0, 0, 20);
   DoubleSetting range = this.registerDouble("Range", 5.0D, 0.0D, 10.0D);
   DoubleSetting yRange = this.registerDouble("YRange", 2.5D, 0.0D, 10.0D);
   IntegerSetting bpt = this.registerInteger("BlocksPerTick", 4, 0, 20);
   BooleanSetting rotate = this.registerBoolean("Rotate", false);
   BooleanSetting packet = this.registerBoolean("Packet Place", false);
   BooleanSetting strict = this.registerBoolean("Strict", false);
   BooleanSetting raytrcae = this.registerBoolean("RayTrace", false);
   BooleanSetting swing = this.registerBoolean("Swing", false);
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", false);
   BooleanSetting check = this.registerBoolean("Switch Check", true);
   int waited;
   int placed;

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

   public void onTick() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         if (this.waited++ >= (Integer)this.delay.getValue()) {
            this.placed = this.waited = 0;
            String var1 = (String)this.mode.getValue();
            byte var2 = -1;
            switch(var1.hashCode()) {
            case -1904124519:
               if (var1.equals("Piston")) {
                  var2 = 3;
               }
               break;
            case 86836:
               if (var1.equals("Web")) {
                  var2 = 0;
               }
               break;
            case 2136258:
               if (var1.equals("Down")) {
                  var2 = 1;
               }
               break;
            case 2192281:
               if (var1.equals("Flat")) {
                  var2 = 2;
               }
               break;
            case 2040486332:
               if (var1.equals("EChest")) {
                  var2 = 4;
               }
            }

            switch(var2) {
            case 0:
               this.web();
               break;
            case 1:
               this.down();
               break;
            case 2:
               this.flat();
               break;
            case 3:
               this.piston();
               break;
            case 4:
               this.enderChest();
            }

         }
      }
   }

   private void web() {
      int slot = BurrowUtil.findHotbarBlock(BlockWeb.class);
      if (slot != -1) {
         int oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
         List<BlockPos> sphere = EntityUtil.getSphere(PlayerUtil.getEyesPos(), (Double)this.range.getValue() + 1.0D, (Double)this.yRange.getValue() + 1.0D, false, false, 0);
         if (!sphere.isEmpty()) {
            this.switchTo(slot);
            Iterator var4 = sphere.iterator();

            while(var4.hasNext()) {
               BlockPos pos = (BlockPos)var4.next();
               if (this.placed >= (Integer)this.bpt.getValue()) {
                  break;
               }

               if (!this.cantPlaceCrystal(pos)) {
                  BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), (Boolean)this.strict.getValue(), (Boolean)this.raytrcae.getValue(), (Boolean)this.swing.getValue());
                  ++this.placed;
               }
            }

            this.switchTo(oldSlot);
         }

      }
   }

   private void down() {
      if (!mc.field_71439_g.func_184614_ca().field_190928_g && mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBlock) {
         List<BlockPos> sphere = EntityUtil.getSphere(PlayerUtil.getEyesPos(), (Double)this.range.getValue(), (Double)this.yRange.getValue(), false, false, 0);
         sphere.removeIf((p) -> {
            return !BlockUtil.canReplace(p) || this.intersectsWithEntity(p) || BlockUtil.getFirstFacing(p, (Boolean)this.strict.getValue(), (Boolean)this.raytrcae.getValue()) == null || PlayerUtil.getPlayerPos().func_177956_o() <= p.func_177956_o();
         });

         for(Iterator var2 = sphere.iterator(); var2.hasNext(); ++this.placed) {
            BlockPos pos = (BlockPos)var2.next();
            if (this.placed >= (Integer)this.bpt.getValue()) {
               break;
            }

            BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), (Boolean)this.strict.getValue(), (Boolean)this.raytrcae.getValue(), (Boolean)this.swing.getValue());
         }

      }
   }

   private void flat() {
      if (!mc.field_71439_g.func_184614_ca().field_190928_g && mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBlock) {
         List<BlockPos> sphere = EntityUtil.getSphere(PlayerUtil.getEyesPos(), (Double)this.range.getValue(), (Double)this.yRange.getValue(), false, false, 0);
         sphere.removeIf((p) -> {
            return !BlockUtil.canReplace(p) || this.intersectsWithEntity(p) || BlockUtil.getFirstFacing(p, (Boolean)this.strict.getValue(), (Boolean)this.raytrcae.getValue()) == null || PlayerUtil.getPlayerPos().func_177956_o() - 1 != p.func_177956_o();
         });

         for(Iterator var2 = sphere.iterator(); var2.hasNext(); ++this.placed) {
            BlockPos pos = (BlockPos)var2.next();
            if (this.placed >= (Integer)this.bpt.getValue()) {
               break;
            }

            BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), (Boolean)this.strict.getValue(), (Boolean)this.raytrcae.getValue(), (Boolean)this.swing.getValue());
         }

      }
   }

   private void piston() {
      int pistonSlot = BurrowUtil.findHotbarBlock(BlockPistonBase.class);
      int redstoneSlot = BurrowUtil.findHotbarBlock(Blocks.field_150451_bX.getClass());
      if (pistonSlot != -1 || redstoneSlot != -1) {
         List<BlockPos> sphere = getSphere(PlayerUtil.getEyesPos(), (Double)this.range.getValue() + 1.0D, (Double)this.yRange.getValue() + 1.0D, false, false, 0);
         sphere.removeIf((p) -> {
            return !BlockUtil.canReplace(p) || this.intersectsWithEntity(p);
         });
         boolean hi = false;
         int slot = mc.field_71439_g.field_71071_by.field_70461_c;

         for(Iterator var6 = sphere.iterator(); var6.hasNext(); ++this.placed) {
            BlockPos pos = (BlockPos)var6.next();
            if (this.placed >= (Integer)this.bpt.getValue() || PlayerUtil.getPlayerPos().func_177956_o() <= pos.func_177956_o()) {
               break;
            }

            if (hi) {
               this.switchTo(pistonSlot);
            } else {
               this.switchTo(redstoneSlot);
            }

            hi = !hi;
            BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), (Boolean)this.strict.getValue(), (Boolean)this.raytrcae.getValue(), (Boolean)this.swing.getValue());
         }

         this.switchTo(slot);
      }
   }

   private void enderChest() {
      int slot = BurrowUtil.findHotbarBlock(BlockEnderChest.class);
      if (slot != -1) {
         int oldSlot = mc.field_71439_g.field_71071_by.field_70461_c;
         List<BlockPos> sphere = EntityUtil.getSphere(PlayerUtil.getEyesPos(), (Double)this.range.getValue() + 1.0D, (Double)this.yRange.getValue() + 1.0D, false, false, 0);
         sphere.removeIf(this::cantPlaceCrystal);
         if (!sphere.isEmpty()) {
            this.switchTo(slot);
            Iterator var4 = sphere.iterator();

            while(var4.hasNext()) {
               BlockPos pos = (BlockPos)var4.next();
               if (this.placed >= (Integer)this.bpt.getValue()) {
                  break;
               }

               if (!this.intersectsWithEntity(pos)) {
                  BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), (Boolean)this.strict.getValue(), (Boolean)this.raytrcae.getValue(), (Boolean)this.swing.getValue());
                  ++this.placed;
               }
            }

            this.switchTo(oldSlot);
         }

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
      } while(entity.field_70128_L || entity instanceof EntityItem || entity instanceof EntityXPOrb || entity instanceof EntityExpBottle || entity instanceof EntityArrow || !(new AxisAlignedBB(pos)).func_72326_a(entity.func_174813_aQ()));

      return true;
   }

   private boolean inRange(BlockPos pos) {
      double y = (double)(pos.field_177960_b - PlayerUtil.getEyesPos().field_177960_b);
      return mc.field_71439_g.func_174818_b(pos) <= (Double)this.range.getValue() * (Double)this.range.getValue() && y * y <= (Double)this.yRange.getValue() * (Double)this.yRange.getValue();
   }

   public static List<BlockPos> getSphere(BlockPos loc, Double r, Double h, boolean hollow, boolean sphere, int plus_y) {
      EnumFacing facing = mc.field_71439_g.func_174811_aO();
      List<BlockPos> circleBlocks = new ArrayList();
      double cx = (double)loc.func_177958_n();
      double cy = (double)loc.func_177956_o();
      double cz = (double)loc.func_177952_p();

      for(double y = sphere ? cy - r : cy - h; y < (sphere ? cy + r : cy + h); ++y) {
         double v = sphere ? (cy - y) * (cy - y) : 0.0D;
         double x;
         double z;
         double dist;
         BlockPos l;
         if (facing != EnumFacing.EAST && facing != EnumFacing.SOUTH) {
            for(x = cx - r; x <= cx + r; ++x) {
               for(z = cz - r; z <= cz + r; ++z) {
                  dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + v;
                  if (dist < r * r && (!hollow || !(dist < (r - 1.0D) * (r - 1.0D)))) {
                     l = new BlockPos(x, y + (double)plus_y, z);
                     circleBlocks.add(l);
                  }
               }
            }
         } else {
            for(x = cx + r; x >= cx - r; --x) {
               for(z = cz + r; z >= cz - r; --z) {
                  dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + v;
                  if (dist < r * r && (!hollow || !(dist < (r - 1.0D) * (r - 1.0D)))) {
                     l = new BlockPos(x, y + (double)plus_y, z);
                     circleBlocks.add(l);
                  }
               }
            }
         }
      }

      return circleBlocks;
   }

   private boolean cantPlaceCrystal(BlockPos p) {
      if (!this.inRange(p)) {
         return true;
      } else if (mc.field_71441_e.func_175623_d(p) && mc.field_71441_e.func_175623_d(p.func_177984_a())) {
         return mc.field_71441_e.func_180495_p(p.func_177977_b()).func_177230_c() != Blocks.field_150343_Z && mc.field_71441_e.func_180495_p(p.func_177977_b()).func_177230_c() != Blocks.field_150357_h;
      } else {
         return true;
      }
   }
}

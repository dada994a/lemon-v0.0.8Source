package com.lemonclient.client.module.modules.combat;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.player.BurrowUtil;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketPlayer.Position;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "CevBlocker",
   category = Category.Combat
)
public class CevBlocker extends Module {
   ModeSetting time = this.registerMode("Time Mode", Arrays.asList("Tick", "onUpdate", "Both", "Fast"), "Tick");
   BooleanSetting high = this.registerBoolean("High Cev", true);
   BooleanSetting pa = this.registerBoolean("Ignore Bedrock", true);
   BooleanSetting bevel = this.registerBoolean("Bevel", true);
   BooleanSetting packet = this.registerBoolean("Packet Place", true);
   BooleanSetting swing = this.registerBoolean("Swing", true);
   BooleanSetting rotate = this.registerBoolean("Rotate", true);
   BooleanSetting packetSwitch = this.registerBoolean("Packet Switch", true);
   private List<BlockPos> cevPositions = new ArrayList();

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

   public void onUpdate() {
      if (((String)this.time.getValue()).equals("onUpdate") || ((String)this.time.getValue()).equals("Both")) {
         this.doBlock();
      }

   }

   public void onTick() {
      if (((String)this.time.getValue()).equals("Tick") || ((String)this.time.getValue()).equals("Both")) {
         this.doBlock();
      }

   }

   public void fast() {
      if (((String)this.time.getValue()).equals("Fast")) {
         this.doBlock();
      }

   }

   private void doBlock() {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         BlockPos[] highpos = new BlockPos[]{new BlockPos(0, 3, 0), new BlockPos(0, 4, 0), new BlockPos(1, 2, 0), new BlockPos(-1, 2, 0), new BlockPos(0, 2, 1), new BlockPos(0, 2, -1)};
         BlockPos[] hight2 = new BlockPos[]{new BlockPos(1, 2, 1), new BlockPos(1, 2, -1), new BlockPos(-1, 2, 1), new BlockPos(-1, 2, -1)};
         BlockPos[] offsets = new BlockPos[]{new BlockPos(0, 2, 0), new BlockPos(1, 1, 0), new BlockPos(-1, 1, 0), new BlockPos(0, 1, 1), new BlockPos(0, 1, -1)};
         BlockPos[] offsets2 = new BlockPos[]{new BlockPos(1, 1, 1), new BlockPos(1, 1, -1), new BlockPos(-1, 1, 1), new BlockPos(-1, 1, -1)};
         BlockPos[] var5 = offsets;
         int var6 = offsets.length;

         int obby;
         BlockPos offset;
         for(obby = 0; obby < var6; ++obby) {
            offset = var5[obby];
            this.check(offset);
         }

         if ((Boolean)this.high.getValue()) {
            var5 = highpos;
            var6 = highpos.length;

            for(obby = 0; obby < var6; ++obby) {
               offset = var5[obby];
               this.check(offset);
            }
         }

         if ((Boolean)this.bevel.getValue()) {
            var5 = offsets2;
            var6 = offsets2.length;

            for(obby = 0; obby < var6; ++obby) {
               offset = var5[obby];
               this.check(offset);
            }

            if ((Boolean)this.high.getValue()) {
               var5 = hight2;
               var6 = hight2.length;

               for(obby = 0; obby < var6; ++obby) {
                  offset = var5[obby];
                  this.check(offset);
               }
            }
         }

         Iterator iterator = this.cevPositions.iterator();

         while(iterator.hasNext()) {
            BlockPos pos = (BlockPos)iterator.next();
            if (Objects.isNull(this.getCrystal(pos))) {
               obby = BurrowUtil.findHotbarBlock(BlockObsidian.class);
               if (obby == -1) {
                  return;
               }

               this.switchTo(obby, () -> {
                  if (mc.field_71441_e.func_175623_d(pos)) {
                     BurrowUtil.placeBlock(pos, EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                     BurrowUtil.placeBlock(pos.func_177984_a(), EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                  } else {
                     BurrowUtil.placeBlock(pos.func_177984_a(), EnumHand.MAIN_HAND, (Boolean)this.rotate.getValue(), (Boolean)this.packet.getValue(), false, (Boolean)this.swing.getValue());
                  }

               });
               iterator.remove();
            }
         }

      }
   }

   public void check(BlockPos offset) {
      BlockPos playerPos = PlayerUtil.getPlayerPos();
      BlockPos offsetPos = playerPos.func_177971_a(offset);
      Entity crystal = this.getCrystal(offsetPos);
      if (!Objects.isNull(crystal)) {
         BlockPos crystalPos = EntityUtil.getEntityPos(crystal).func_177977_b();
         if (!(Boolean)this.pa.getValue() || mc.field_71441_e.func_175623_d(crystalPos) || mc.field_71441_e.func_180495_p(crystalPos).func_177230_c() == Blocks.field_150343_Z) {
            if (!mc.field_71441_e.func_175623_d(playerPos.func_177984_a().func_177984_a())) {
               mc.field_71439_g.field_71174_a.func_147297_a(new Position(mc.field_71439_g.field_70165_t, (double)playerPos.func_177956_o() + 0.2D, mc.field_71439_g.field_70161_v, false));
            }

            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketUseEntity(crystal));
            mc.field_71439_g.field_71174_a.func_147297_a(new CPacketAnimation(EnumHand.MAIN_HAND));
            if (!this.cevPositions.contains(crystalPos)) {
               this.cevPositions.add(crystalPos);
            }

         }
      }
   }

   private Entity getCrystal(BlockPos pos) {
      return (Entity)mc.field_71441_e.field_72996_f.stream().filter((e) -> {
         return e instanceof EntityEnderCrystal;
      }).filter((e) -> {
         return EntityUtil.getEntityPos(e).func_177977_b().equals(pos);
      }).min(Comparator.comparing(this::getDistance)).orElse((Object)null);
   }

   public double getDistance(Entity e) {
      return (double)mc.field_71439_g.func_70032_d(e);
   }

   public void onDisable() {
      this.cevPositions = new ArrayList();
   }
}

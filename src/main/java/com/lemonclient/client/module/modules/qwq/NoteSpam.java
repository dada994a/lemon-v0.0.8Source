package com.lemonclient.client.module.modules.qwq;

import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "NoteSpam",
   category = Category.qwq
)
public class NoteSpam extends Module {
   ModeSetting timeMode = this.registerMode("Time Mode", Arrays.asList("onUpdate", "Tick", "Fast"), "Fast");
   DoubleSetting range = this.registerDouble("Range", 5.5D, 1.0D, 10.0D);
   IntegerSetting max = this.registerInteger("MaxBlocks", 30, 1, 150);

   public void onUpdate() {
      if (((String)this.timeMode.getValue()).equalsIgnoreCase("onUpdate")) {
         this.doNoteSpam();
      }

   }

   public void onTick() {
      if (((String)this.timeMode.getValue()).equalsIgnoreCase("Tick")) {
         this.doNoteSpam();
      }

   }

   public void fast() {
      if (((String)this.timeMode.getValue()).equalsIgnoreCase("Fast")) {
         this.doNoteSpam();
      }

   }

   private void doNoteSpam() {
      if (mc.field_71441_e != null && mc.field_71439_g != null && !mc.field_71439_g.field_70128_L) {
         int counter = 0;
         List<BlockPos> posList = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), (Double)this.range.getValue(), (Double)this.range.getValue(), false, true, 0);
         Iterator var3 = posList.iterator();

         while(var3.hasNext()) {
            BlockPos b = (BlockPos)var3.next();
            if (BlockUtil.getBlock(b) == Blocks.field_150323_B) {
               mc.field_71439_g.field_71174_a.func_147297_a(new CPacketPlayerDigging(Action.START_DESTROY_BLOCK, b, EnumFacing.UP));
               ++counter;
               if (counter > (Integer)this.max.getValue()) {
                  return;
               }
            }
         }

      }
   }
}

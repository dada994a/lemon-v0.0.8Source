package com.lemonclient.client.module.modules.render;

import com.lemonclient.api.event.events.RenderEvent;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.util.render.RenderUtil;
import com.lemonclient.api.util.world.BlockUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import io.netty.util.internal.ConcurrentSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "VoidESP",
   category = Category.Render
)
public class VoidESP extends Module {
   IntegerSetting renderDistance = this.registerInteger("Distance", 10, 1, 40);
   IntegerSetting activeYValue = this.registerInteger("Activate Y", 20, 0, 256);
   ModeSetting renderType = this.registerMode("Render", Arrays.asList("Outline", "Fill", "Both"), "Both");
   ModeSetting renderMode = this.registerMode("Mode", Arrays.asList("Box", "Flat"), "Flat");
   IntegerSetting width = this.registerInteger("Width", 1, 1, 10);
   ColorSetting color = this.registerColor("Color", new GSColor(255, 255, 0));
   private ConcurrentSet<BlockPos> voidHoles;

   public void onUpdate() {
      if (mc.field_71439_g.field_71093_bK != 1) {
         if (mc.field_71439_g.func_180425_c().func_177956_o() <= (Integer)this.activeYValue.getValue()) {
            if (this.voidHoles == null) {
               this.voidHoles = new ConcurrentSet();
            } else {
               this.voidHoles.clear();
            }

            List<BlockPos> blockPosList = BlockUtil.getCircle(getPlayerPos(), 0, (float)(Integer)this.renderDistance.getValue(), false);
            Iterator var2 = blockPosList.iterator();

            while(var2.hasNext()) {
               BlockPos blockPos = (BlockPos)var2.next();
               if (!mc.field_71441_e.func_180495_p(blockPos).func_177230_c().equals(Blocks.field_150357_h) && !this.isAnyBedrock(blockPos, VoidESP.Offsets.center)) {
                  this.voidHoles.add(blockPos);
               }
            }

         }
      }
   }

   public void onWorldRender(RenderEvent event) {
      if (mc.field_71439_g != null && this.voidHoles != null) {
         if (mc.field_71439_g.func_180425_c().func_177956_o() <= (Integer)this.activeYValue.getValue()) {
            if (!this.voidHoles.isEmpty()) {
               this.voidHoles.forEach((blockPos) -> {
                  if (((String)this.renderMode.getValue()).equalsIgnoreCase("Box")) {
                     this.drawBox(blockPos);
                  } else {
                     this.drawFlat(blockPos);
                  }

                  this.drawOutline(blockPos, (Integer)this.width.getValue());
               });
            }
         }
      }
   }

   public static BlockPos getPlayerPos() {
      return new BlockPos(Math.floor(mc.field_71439_g.field_70165_t), Math.floor(mc.field_71439_g.field_70163_u), Math.floor(mc.field_71439_g.field_70161_v));
   }

   private boolean isAnyBedrock(BlockPos origin, BlockPos[] offset) {
      BlockPos[] var3 = offset;
      int var4 = offset.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         BlockPos pos = var3[var5];
         if (mc.field_71441_e.func_180495_p(origin.func_177971_a(pos)).func_177230_c().equals(Blocks.field_150357_h)) {
            return true;
         }
      }

      return false;
   }

   private void drawFlat(BlockPos blockPos) {
      if (((String)this.renderType.getValue()).equalsIgnoreCase("Fill") || ((String)this.renderType.getValue()).equalsIgnoreCase("Both")) {
         GSColor c = new GSColor(this.color.getValue(), 50);
         if (((String)this.renderMode.getValue()).equalsIgnoreCase("Flat")) {
            RenderUtil.drawBox(blockPos, 1.0D, c, 1);
         }
      }

   }

   private void drawBox(BlockPos blockPos) {
      if (((String)this.renderType.getValue()).equalsIgnoreCase("Fill") || ((String)this.renderType.getValue()).equalsIgnoreCase("Both")) {
         GSColor c = new GSColor(this.color.getValue(), 50);
         RenderUtil.drawBox(blockPos, 1.0D, c, 63);
      }

   }

   private void drawOutline(BlockPos blockPos, int width) {
      if (((String)this.renderType.getValue()).equalsIgnoreCase("Outline") || ((String)this.renderType.getValue()).equalsIgnoreCase("Both")) {
         if (((String)this.renderMode.getValue()).equalsIgnoreCase("Box")) {
            RenderUtil.drawBoundingBox(blockPos, 1.0D, (float)width, this.color.getValue());
         }

         if (((String)this.renderMode.getValue()).equalsIgnoreCase("Flat")) {
            RenderUtil.drawBoundingBoxWithSides((BlockPos)blockPos, width, this.color.getValue(), 1);
         }
      }

   }

   private static class Offsets {
      static final BlockPos[] center = new BlockPos[]{new BlockPos(0, 0, 0), new BlockPos(0, 1, 0), new BlockPos(0, 2, 0)};
   }
}

package com.lemonclient.client.module.modules.render;

import com.lemonclient.api.event.events.RenderEvent;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.util.render.RenderUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Arrays;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;

@Module.Declaration(
   name = "BlockHighlight",
   category = Category.Render
)
public class BlockHighlight extends Module {
   ModeSetting renderLook = this.registerMode("Render", Arrays.asList("Block", "Side"), "Block");
   ModeSetting renderType = this.registerMode("Type", Arrays.asList("Outline", "Fill", "Both"), "Outline");
   IntegerSetting lineWidth = this.registerInteger("Width", 1, 1, 5);
   ColorSetting renderColor = this.registerColor("Color", new GSColor(255, 0, 0, 255));
   private int lookInt;

   public void onWorldRender(RenderEvent event) {
      RayTraceResult rayTraceResult = mc.field_71476_x;
      if (rayTraceResult != null) {
         EnumFacing enumFacing = mc.field_71476_x.field_178784_b;
         if (enumFacing != null) {
            GSColor colorWithOpacity = new GSColor(this.renderColor.getValue(), 50);
            String var5 = (String)this.renderLook.getValue();
            byte var6 = -1;
            switch(var5.hashCode()) {
            case 2576759:
               if (var5.equals("Side")) {
                  var6 = 1;
               }
               break;
            case 64279661:
               if (var5.equals("Block")) {
                  var6 = 0;
               }
            }

            switch(var6) {
            case 0:
               this.lookInt = 0;
               break;
            case 1:
               this.lookInt = 1;
            }

            if (rayTraceResult.field_72313_a == Type.BLOCK) {
               BlockPos blockPos = rayTraceResult.func_178782_a();
               AxisAlignedBB axisAlignedBB = mc.field_71441_e.func_180495_p(blockPos).func_185918_c(mc.field_71441_e, blockPos);
               if (mc.field_71441_e.func_180495_p(blockPos).func_185904_a() != Material.field_151579_a) {
                  var5 = (String)this.renderType.getValue();
                  var6 = -1;
                  switch(var5.hashCode()) {
                  case 2076577:
                     if (var5.equals("Both")) {
                        var6 = 2;
                     }
                     break;
                  case 2189731:
                     if (var5.equals("Fill")) {
                        var6 = 1;
                     }
                     break;
                  case 558407714:
                     if (var5.equals("Outline")) {
                        var6 = 0;
                     }
                  }

                  switch(var6) {
                  case 0:
                     this.renderOutline(axisAlignedBB, (Integer)this.lineWidth.getValue(), this.renderColor.getValue(), enumFacing, this.lookInt);
                     break;
                  case 1:
                     this.renderFill(axisAlignedBB, colorWithOpacity, enumFacing, this.lookInt);
                     break;
                  case 2:
                     this.renderOutline(axisAlignedBB, (Integer)this.lineWidth.getValue(), this.renderColor.getValue(), enumFacing, this.lookInt);
                     this.renderFill(axisAlignedBB, colorWithOpacity, enumFacing, this.lookInt);
                  }
               }
            }

         }
      }
   }

   public void renderOutline(AxisAlignedBB axisAlignedBB, int width, GSColor color, EnumFacing enumFacing, int lookInt) {
      if (lookInt == 0) {
         RenderUtil.drawBoundingBox(axisAlignedBB, (double)width, color);
      } else if (lookInt == 1) {
         RenderUtil.drawBoundingBoxWithSides(axisAlignedBB, width, color, this.findRenderingSide(enumFacing));
      }

   }

   public void renderFill(AxisAlignedBB axisAlignedBB, GSColor color, EnumFacing enumFacing, int lookInt) {
      int facing = 0;
      if (lookInt == 0) {
         facing = 63;
      } else if (lookInt == 1) {
         facing = this.findRenderingSide(enumFacing);
      }

      RenderUtil.drawBox(axisAlignedBB, true, 1.0D, color, facing);
   }

   private int findRenderingSide(EnumFacing enumFacing) {
      int facing = 0;
      if (enumFacing == EnumFacing.EAST) {
         facing = 32;
      } else if (enumFacing == EnumFacing.WEST) {
         facing = 16;
      } else if (enumFacing == EnumFacing.NORTH) {
         facing = 4;
      } else if (enumFacing == EnumFacing.SOUTH) {
         facing = 8;
      } else if (enumFacing == EnumFacing.UP) {
         facing = 2;
      } else if (enumFacing == EnumFacing.DOWN) {
         facing = 1;
      }

      return facing;
   }
}

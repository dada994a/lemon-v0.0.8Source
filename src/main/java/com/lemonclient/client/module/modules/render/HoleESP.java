package com.lemonclient.client.module.modules.render;

import com.google.common.collect.Sets;
import com.lemonclient.api.event.events.RenderEvent;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.player.PlayerUtil;
import com.lemonclient.api.util.player.RotationUtil;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.api.util.render.RenderUtil;
import com.lemonclient.api.util.world.EntityUtil;
import com.lemonclient.api.util.world.HoleUtil;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@Module.Declaration(
   name = "HoleESP",
   category = Category.Render
)
public class HoleESP extends Module {
   public IntegerSetting range = this.registerInteger("Range", 5, 1, 20);
   IntegerSetting Yrange = this.registerInteger("Y Range", 5, 1, 20);
   BooleanSetting single = this.registerBoolean("1x1", true);
   BooleanSetting Double = this.registerBoolean("2x1", true);
   BooleanSetting fourBlocks = this.registerBoolean("2x2", true);
   BooleanSetting custom = this.registerBoolean("Custom", true);
   ModeSetting type = this.registerMode("Render", Arrays.asList("Outline", "Fill", "Both"), "Both");
   ModeSetting mode = this.registerMode("Mode", Arrays.asList("Air", "Ground", "Flat", "Slab", "Double"), "Air");
   BooleanSetting hideOwn = this.registerBoolean("Hide Own", false);
   BooleanSetting flatOwn = this.registerBoolean("Flat Own", false);
   BooleanSetting fov = this.registerBoolean("In Fov", false);
   DoubleSetting slabHeight = this.registerDouble("Slab Height", 0.5D, 0.0D, 2.0D);
   DoubleSetting outslabHeight = this.registerDouble("Outline Height", 0.5D, 0.0D, 2.0D);
   IntegerSetting width = this.registerInteger("Width", 1, 1, 10);
   ColorSetting bedrockColor = this.registerColor("Bedrock Color", new GSColor(0, 255, 0));
   ColorSetting obsidianColor = this.registerColor("Obsidian Color", new GSColor(255, 0, 0));
   ColorSetting twobedrockColor = this.registerColor("2x1 Bedrock Color", new GSColor(0, 255, 0));
   ColorSetting twoobsidianColor = this.registerColor("2x1 Obsidian Color", new GSColor(255, 0, 0));
   ColorSetting fourColor = this.registerColor("2x2 Color", new GSColor(255, 0, 0));
   ColorSetting customColor = this.registerColor("Custom Color", new GSColor(0, 0, 255));
   IntegerSetting alpha = this.registerInteger("Alpha", 50, 0, 255);
   IntegerSetting ufoAlpha = this.registerInteger("UFOAlpha", 255, 0, 255);
   private ConcurrentHashMap<AxisAlignedBB, GSColor> holes;

   public void onUpdate() {
      if (mc.field_71439_g != null && mc.field_71441_e != null) {
         if (this.holes == null) {
            this.holes = new ConcurrentHashMap();
         } else {
            this.holes.clear();
         }

         HashSet<BlockPos> possibleHoles = Sets.newHashSet();
         List<BlockPos> blockPosList = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), (double)(Integer)this.range.getValue(), (double)(Integer)this.Yrange.getValue(), false, false, 0);
         Iterator var3 = blockPosList.iterator();

         while(true) {
            BlockPos pos;
            do {
               if (!var3.hasNext()) {
                  possibleHoles.forEach((posx) -> {
                     HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(posx, false, false, true);
                     HoleUtil.HoleType holeType = holeInfo.getType();
                     if (holeType != HoleUtil.HoleType.NONE) {
                        HoleUtil.BlockSafety holeSafety = holeInfo.getSafety();
                        AxisAlignedBB centreBlocks = holeInfo.getCentre();
                        if (centreBlocks == null) {
                           return;
                        }

                        GSColor colour;
                        if ((Boolean)this.fourBlocks.getValue() && holeType == HoleUtil.HoleType.FOUR) {
                           colour = new GSColor(this.fourColor.getValue(), 255);
                           this.holes.put(centreBlocks, colour);
                        } else if ((Boolean)this.custom.getValue() && holeType == HoleUtil.HoleType.CUSTOM) {
                           colour = new GSColor(this.customColor.getValue(), 255);
                           this.holes.put(centreBlocks, colour);
                        } else if ((Boolean)this.Double.getValue() && holeType == HoleUtil.HoleType.DOUBLE) {
                           if (holeSafety == HoleUtil.BlockSafety.UNBREAKABLE) {
                              colour = new GSColor(this.twobedrockColor.getValue(), 255);
                           } else {
                              colour = new GSColor(this.twoobsidianColor.getValue(), 255);
                           }

                           this.holes.put(centreBlocks, colour);
                        } else if ((Boolean)this.single.getValue() && holeType == HoleUtil.HoleType.SINGLE) {
                           if (holeSafety == HoleUtil.BlockSafety.UNBREAKABLE) {
                              colour = new GSColor(this.bedrockColor.getValue(), 255);
                           } else {
                              colour = new GSColor(this.obsidianColor.getValue(), 255);
                           }

                           this.holes.put(centreBlocks, colour);
                        }
                     }

                  });
                  return;
               }

               pos = (BlockPos)var3.next();
            } while((Boolean)this.fov.getValue() && !RotationUtil.isInFov(pos));

            if (mc.field_71441_e.func_180495_p(pos).func_177230_c().equals(Blocks.field_150350_a) && !mc.field_71441_e.func_180495_p(pos.func_177982_a(0, -1, 0)).func_177230_c().equals(Blocks.field_150350_a) && mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 1, 0)).func_177230_c().equals(Blocks.field_150350_a) && mc.field_71441_e.func_180495_p(pos.func_177982_a(0, 2, 0)).func_177230_c().equals(Blocks.field_150350_a)) {
               possibleHoles.add(pos);
            }
         }
      }
   }

   public void onWorldRender(RenderEvent event) {
      if (mc.field_71439_g != null && mc.field_71441_e != null && this.holes != null && !this.holes.isEmpty()) {
         this.holes.forEach(this::renderHoles);
      }
   }

   private void renderHoles(AxisAlignedBB hole, GSColor color) {
      String var3 = (String)this.type.getValue();
      byte var4 = -1;
      switch(var3.hashCode()) {
      case 2076577:
         if (var3.equals("Both")) {
            var4 = 2;
         }
         break;
      case 2189731:
         if (var3.equals("Fill")) {
            var4 = 1;
         }
         break;
      case 558407714:
         if (var3.equals("Outline")) {
            var4 = 0;
         }
      }

      switch(var4) {
      case 0:
         this.renderOutline(hole, color);
         break;
      case 1:
         this.renderFill(hole, color);
         break;
      case 2:
         this.renderOutline(hole, color);
         this.renderFill(hole, color);
      }

   }

   private void renderFill(AxisAlignedBB hole, GSColor color) {
      GSColor fillColor = new GSColor(color, (Integer)this.alpha.getValue());
      int ufoAlpha = (Integer)this.ufoAlpha.getValue() * 50 / 255;
      if (!(Boolean)this.hideOwn.getValue() || !hole.func_72326_a(mc.field_71439_g.func_174813_aQ())) {
         String var5 = (String)this.mode.getValue();
         byte var6 = -1;
         switch(var5.hashCode()) {
         case 65834:
            if (var5.equals("Air")) {
               var6 = 0;
            }
            break;
         case 2192281:
            if (var5.equals("Flat")) {
               var6 = 2;
            }
            break;
         case 2579546:
            if (var5.equals("Slab")) {
               var6 = 3;
            }
            break;
         case 2052876273:
            if (var5.equals("Double")) {
               var6 = 4;
            }
            break;
         case 2141373863:
            if (var5.equals("Ground")) {
               var6 = 1;
            }
         }

         switch(var6) {
         case 0:
            if ((Boolean)this.flatOwn.getValue() && hole.func_72326_a(mc.field_71439_g.func_174813_aQ())) {
               RenderUtil.drawBox(hole, true, 1.0D, fillColor, ufoAlpha, 1);
            } else {
               RenderUtil.drawBox(hole, true, 1.0D, fillColor, ufoAlpha, 63);
            }
            break;
         case 1:
            RenderUtil.drawBox(hole.func_72317_d(0.0D, -1.0D, 0.0D), true, 1.0D, new GSColor(fillColor, ufoAlpha), fillColor.getAlpha(), 63);
            break;
         case 2:
            RenderUtil.drawBox(hole, true, 1.0D, fillColor, ufoAlpha, 1);
            break;
         case 3:
            if ((Boolean)this.flatOwn.getValue() && hole.func_72326_a(mc.field_71439_g.func_174813_aQ())) {
               RenderUtil.drawBox(hole, true, 1.0D, fillColor, ufoAlpha, 1);
            } else {
               RenderUtil.drawBox(hole, false, (Double)this.slabHeight.getValue(), fillColor, ufoAlpha, 63);
            }
            break;
         case 4:
            if ((Boolean)this.flatOwn.getValue() && hole.func_72326_a(mc.field_71439_g.func_174813_aQ())) {
               RenderUtil.drawBox(hole, true, 1.0D, fillColor, ufoAlpha, 1);
            } else {
               RenderUtil.drawBox(hole.func_186666_e(hole.field_72337_e + 1.0D), true, 2.0D, fillColor, ufoAlpha, 63);
            }
         }

      }
   }

   private void renderOutline(AxisAlignedBB hole, GSColor color) {
      GSColor outlineColor = new GSColor(color, 255);
      if (!(Boolean)this.hideOwn.getValue() || !hole.func_72326_a(mc.field_71439_g.func_174813_aQ())) {
         String var4 = (String)this.mode.getValue();
         byte var5 = -1;
         switch(var4.hashCode()) {
         case 65834:
            if (var4.equals("Air")) {
               var5 = 0;
            }
            break;
         case 2192281:
            if (var4.equals("Flat")) {
               var5 = 2;
            }
            break;
         case 2579546:
            if (var4.equals("Slab")) {
               var5 = 3;
            }
            break;
         case 2052876273:
            if (var4.equals("Double")) {
               var5 = 4;
            }
            break;
         case 2141373863:
            if (var4.equals("Ground")) {
               var5 = 1;
            }
         }

         switch(var5) {
         case 0:
            if ((Boolean)this.flatOwn.getValue() && hole.func_72326_a(mc.field_71439_g.func_174813_aQ())) {
               RenderUtil.drawBoundingBoxWithSides((AxisAlignedBB)hole, (Integer)this.width.getValue(), outlineColor, (Integer)this.ufoAlpha.getValue(), 1);
            } else {
               RenderUtil.drawBoundingBox(hole, (double)(Integer)this.width.getValue(), outlineColor, (Integer)this.ufoAlpha.getValue());
            }
            break;
         case 1:
            RenderUtil.drawBoundingBox(hole.func_72317_d(0.0D, -1.0D, 0.0D), (double)(Integer)this.width.getValue(), new GSColor(outlineColor, (Integer)this.ufoAlpha.getValue()), outlineColor.getAlpha());
            break;
         case 2:
            RenderUtil.drawBoundingBoxWithSides((AxisAlignedBB)hole, (Integer)this.width.getValue(), outlineColor, (Integer)this.ufoAlpha.getValue(), 1);
            break;
         case 3:
            if ((Boolean)this.flatOwn.getValue() && hole.func_72326_a(mc.field_71439_g.func_174813_aQ())) {
               RenderUtil.drawBoundingBoxWithSides((AxisAlignedBB)hole, (Integer)this.width.getValue(), outlineColor, (Integer)this.ufoAlpha.getValue(), 1);
            } else {
               RenderUtil.drawBoundingBox(hole.func_186666_e(hole.field_72338_b + (Double)this.outslabHeight.getValue()), (double)(Integer)this.width.getValue(), outlineColor, (Integer)this.ufoAlpha.getValue());
            }
            break;
         case 4:
            if ((Boolean)this.flatOwn.getValue() && hole.func_72326_a(mc.field_71439_g.func_174813_aQ())) {
               RenderUtil.drawBoundingBoxWithSides((AxisAlignedBB)hole, (Integer)this.width.getValue(), outlineColor, (Integer)this.ufoAlpha.getValue(), 1);
            } else {
               RenderUtil.drawBoundingBox(hole.func_186666_e(hole.field_72337_e + 1.0D), (double)(Integer)this.width.getValue(), outlineColor, (Integer)this.ufoAlpha.getValue());
            }
         }

      }
   }
}

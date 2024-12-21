package com.lemonclient.client.module.modules.render;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemSword;

@Module.Declaration(
   name = "RenderTweaks",
   category = Category.Render
)
public class RenderTweaks extends Module {
   public BooleanSetting viewClip = this.registerBoolean("View Clip", false);
   BooleanSetting nekoAnimation = this.registerBoolean("Neko Animation", false);
   BooleanSetting lowOffhand = this.registerBoolean("Low Offhand", false);
   DoubleSetting lowOffhandSlider = this.registerDouble("Offhand Height", 1.0D, 0.1D, 1.0D);
   BooleanSetting fovChanger = this.registerBoolean("FOV", false);
   IntegerSetting fovChangerSlider = this.registerInteger("FOV Slider", 90, 70, 200);
   ItemRenderer itemRenderer;
   private float oldFOV;

   public RenderTweaks() {
      this.itemRenderer = mc.field_71460_t.field_78516_c;
   }

   public void onUpdate() {
      if ((Boolean)this.nekoAnimation.getValue() && mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemSword && (double)mc.field_71460_t.field_78516_c.field_187470_g >= 0.9D) {
         mc.field_71460_t.field_78516_c.field_187469_f = 1.0F;
         mc.field_71460_t.field_78516_c.field_187467_d = mc.field_71439_g.func_184614_ca();
      }

      if ((Boolean)this.lowOffhand.getValue()) {
         this.itemRenderer.field_187471_h = ((Double)this.lowOffhandSlider.getValue()).floatValue();
      }

      if ((Boolean)this.fovChanger.getValue()) {
         mc.field_71474_y.field_74334_X = (float)(Integer)this.fovChangerSlider.getValue();
      }

      if (!(Boolean)this.fovChanger.getValue()) {
         mc.field_71474_y.field_74334_X = this.oldFOV;
      }

   }

   public void onEnable() {
      this.oldFOV = mc.field_71474_y.field_74334_X;
   }

   public void onDisable() {
      mc.field_71474_y.field_74334_X = this.oldFOV;
   }
}

package com.lemonclient.client.module.modules.hud;

import com.lemonclient.api.event.events.Render2DEvent;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.util.font.FontUtil;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import com.lemonclient.client.module.modules.gui.ColorMain;
import net.minecraft.item.ItemFood;

@Module.Declaration(
   name = "EatTimer",
   category = Category.HUD,
   drawn = false
)
public class EatTimer extends Module {
   IntegerSetting timer = this.registerInteger("Timer", 32, 0, 100);
   int tick = 100;
   boolean holding = false;

   public void onEnable() {
      this.holding = false;
      this.tick = 100;
   }

   public void onTick() {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         ++this.tick;
         this.holding = mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemFood || mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemFood;
         if (mc.field_71439_g.func_184587_cr() && this.holding && this.tick > (Integer)this.timer.getValue()) {
            this.tick = 0;
         }

      } else {
         this.tick = 100;
      }
   }

   public void onRender2D(Render2DEvent event) {
      if (mc.field_71441_e != null && mc.field_71439_g != null) {
         if (this.holding) {
            if (this.tick <= (Integer)this.timer.getValue()) {
               double percent = (double)this.tick / (double)(Integer)this.timer.getValue();
               String text = String.format("%.1f", percent * 100.0D) + "%";
               int divider = mc.field_71474_y.field_74335_Z;
               if (divider == 0) {
                  divider = 3;
               }

               boolean font = (Boolean)((ColorMain)ModuleManager.getModule(ColorMain.class)).customFont.getValue();
               FontUtil.drawStringWithShadow(font, text, (float)(mc.field_71443_c / divider / 2 - FontUtil.getStringWidth(font, text) / 2), (float)(mc.field_71440_d / divider / 2 + 16), new GSColor(255, 255, 255));
            }
         } else {
            this.tick = 100;
         }

      } else {
         this.tick = 100;
      }
   }
}

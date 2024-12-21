package com.lemonclient.client.module.modules.hud;

import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.StringSetting;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.HUDModule;
import com.lemonclient.client.module.Module;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.setting.Labeled;
import com.lukflug.panelstudio.theme.ITheme;
import java.awt.Color;

@Module.Declaration(
   name = "Welcomer",
   category = Category.HUD,
   drawn = false
)
@HUDModule.Declaration(
   posX = 450,
   posZ = 0
)
public class Welcomer extends HUDModule {
   StringSetting prefix = this.registerString("Prefix", "Hi ");
   StringSetting suffix = this.registerString("Suffix", " :^)");
   ColorSetting color = this.registerColor("Color", new GSColor(255, 0, 0, 255));

   public void populate(ITheme theme) {
      this.component = new ListComponent(new Labeled(this.getName(), (String)null, () -> {
         return true;
      }), this.position, this.getName(), new Welcomer.WelcomerList(), 9, 1);
   }

   private class WelcomerList implements HUDList {
      private WelcomerList() {
      }

      public int getSize() {
         return 1;
      }

      public String getItem(int index) {
         return Welcomer.this.prefix.getText() + Welcomer.mc.field_71439_g.func_70005_c_() + Welcomer.this.suffix.getText();
      }

      public Color getItemColor(int index) {
         return Welcomer.this.color.getValue();
      }

      public boolean sortUp() {
         return false;
      }

      public boolean sortRight() {
         return false;
      }

      // $FF: synthetic method
      WelcomerList(Object x1) {
         this();
      }
   }
}

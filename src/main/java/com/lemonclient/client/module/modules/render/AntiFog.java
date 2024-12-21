package com.lemonclient.client.module.modules.render;

import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Arrays;

@Module.Declaration(
   name = "AntiFog",
   category = Category.Render
)
public class AntiFog extends Module {
   public static String type;
   ModeSetting mode = this.registerMode("Mode", Arrays.asList("NoFog", "Air"), "NoFog");

   public void onUpdate() {
      type = (String)this.mode.getValue();
   }
}

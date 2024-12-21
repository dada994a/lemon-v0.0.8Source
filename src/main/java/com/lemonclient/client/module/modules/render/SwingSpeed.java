package com.lemonclient.client.module.modules.render;

import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Arrays;

@Module.Declaration(
   name = "SwingSpeed",
   category = Category.Render
)
public class SwingSpeed extends Module {
   public static SwingSpeed INSTANCE;
   public IntegerSetting rotate = this.registerInteger("rotate", 6, 1, 20);
   public IntegerSetting speed = this.registerInteger("Speed", 6, 1, 50);
   public ModeSetting type = this.registerMode("Type", Arrays.asList("X", "Y", "Z"), "X");
   public boolean isPressed = false;

   public SwingSpeed() {
      INSTANCE = this;
   }
}

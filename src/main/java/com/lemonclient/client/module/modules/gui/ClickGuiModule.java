package com.lemonclient.client.module.modules.gui;

import com.lemonclient.api.setting.SettingsManager;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;

@Module.Declaration(
   name = "ClickGUI",
   category = Category.GUI,
   bind = 25,
   drawn = false
)
public class ClickGuiModule extends Module {
   public IntegerSetting scrollSpeed = this.registerInteger("Scroll Speed", 10, 1, 20);
   public IntegerSetting animationSpeed = this.registerInteger("Animation Speed", 300, 0, 1000);
   public ModeSetting scrolling = this.registerMode("Scrolling", Arrays.asList("Screen", "Container"), "Container");
   public BooleanSetting showHUD = this.registerBoolean("Show HUD Panels", false);
   public BooleanSetting csgoLayout = this.registerBoolean("CSGO Layout", false);
   public ModeSetting theme = this.registerMode("Skin", Collections.singletonList("Clear"), "Clear", () -> {
      return false;
   });
   public BooleanSetting gradient = this.registerBoolean("Gradient", true);

   public void onEnable() {
      LemonClient.INSTANCE.gameSenseGUI.enterGUI();
      this.disable();
   }

   public ColorSetting registerColor(String name, String configName, Supplier<Boolean> isVisible, boolean rainbow, boolean rainbowEnabled, boolean alphaEnabled, GSColor value) {
      ColorSetting setting = new ColorSetting(name, configName, this, isVisible, rainbow, rainbowEnabled, alphaEnabled, value);
      SettingsManager.addSetting(setting);
      return setting;
   }
}

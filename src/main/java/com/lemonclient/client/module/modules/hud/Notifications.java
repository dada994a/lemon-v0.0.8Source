package com.lemonclient.client.module.modules.hud;

import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.client.module.Category;
import com.lemonclient.client.module.Module;
import java.util.Arrays;

@Module.Declaration(
   name = "Notifications",
   category = Category.HUD,
   drawn = false
)
public class Notifications extends Module {
   public ColorSetting backGround = this.registerColor("Info BackGround", new GSColor(255, 255, 255));
   public ColorSetting successBackGround = this.registerColor("Success BackGround", new GSColor(0, 255, 0));
   public ColorSetting warningBackGround = this.registerColor("Warning BackGround", new GSColor(255, 0, 0));
   public ColorSetting errorBackGround = this.registerColor("Error BackGround", new GSColor(0, 0, 0));
   public ColorSetting disableBackGround = this.registerColor("Disable BackGround", new GSColor(255, 0, 0));
   public IntegerSetting alpha = this.registerInteger("Alpha", 168, 0, 255);
   public BooleanSetting outline = this.registerBoolean("Outline", true);
   public IntegerSetting outlineAlpha = this.registerInteger("Outline Alpha", 200, 0, 255);
   public BooleanSetting mark = this.registerBoolean("Icon", true);
   public DoubleSetting xSpeed = this.registerDouble("Animation XSpeed", 0.1D, 0.01D, 0.5D);
   public DoubleSetting ySpeed = this.registerDouble("Animation YSpeed", 0.1D, 0.01D, 5.0D);
   public IntegerSetting max = this.registerInteger("Max Count", 10, 0, 100);
   public ModeSetting mode = this.registerMode("Mode", Arrays.asList("Remove", "Cancel"), "Remove");
   public BooleanSetting disableChat = this.registerBoolean("No Chat Msg", true);
}

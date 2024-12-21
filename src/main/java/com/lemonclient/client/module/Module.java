package com.lemonclient.client.module;

import com.lemonclient.api.event.events.Render2DEvent;
import com.lemonclient.api.event.events.Render3DEvent;
import com.lemonclient.api.event.events.RenderEvent;
import com.lemonclient.api.setting.SettingsManager;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.StringSetting;
import com.lemonclient.api.util.chat.Notification;
import com.lemonclient.api.util.misc.MessageBus;
import com.lemonclient.api.util.render.GSColor;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.module.modules.gui.ColorMain;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.function.Supplier;
import me.zero.alpine.listener.Listenable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;

public abstract class Module implements Listenable {
   protected static final Minecraft mc = Minecraft.func_71410_x();
   private final String name = this.getDeclaration().name();
   private final Category category = this.getDeclaration().category();
   private final int priority = this.getDeclaration().priority();
   private int bind = this.getDeclaration().bind();
   private boolean enabled = this.getDeclaration().enabled();
   private boolean drawn = this.getDeclaration().drawn();
   private boolean toggleMsg = this.getDeclaration().toggleMsg();
   public float remainingAnimation;
   public int onUpdateTimer;
   public int onTickTimer;
   public int fastTimer;
   private String disabledMessage = "";

   private Module.Declaration getDeclaration() {
      return (Module.Declaration)this.getClass().getAnnotation(Module.Declaration.class);
   }

   public void onTick() {
   }

   public void fast() {
   }

   protected void onEnable() {
   }

   protected void onDisable() {
   }

   public void onUpdate() {
   }

   public void onRender() {
   }

   public void onWorldRender(RenderEvent event) {
   }

   public void onRender2D(Render2DEvent event) {
      ++this.remainingAnimation;
   }

   public void onRender3D(Render3DEvent event) {
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public void setDisabledMessage(String message) {
      this.disabledMessage = message;
   }

   public void enable() {
      this.setEnabled(true);
      LemonClient.EVENT_BUS.subscribe((Listenable)this);

      try {
         this.onEnable();
      } catch (Exception var6) {
         MessageBus.sendClientPrefixMessage("Disabled " + this.getName() + " due to " + var6, Notification.Type.ERROR);
         StackTraceElement[] var2 = var6.getStackTrace();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            StackTraceElement stack = var2[var4];
            System.out.println(stack.toString());
         }
      }

      if (this.toggleMsg && mc.field_71441_e != null && mc.field_71439_g != null) {
         MessageBus.sendClientDeleteMessage(((ColorMain)ModuleManager.getModule(ColorMain.class)).getModuleColor() + this.name + ChatFormatting.GRAY + " turned " + ((ColorMain)ModuleManager.getModule(ColorMain.class)).getEnabledColor() + "ON" + ChatFormatting.GRAY + ".", Notification.Type.SUCCESS, this.getName(), 0);
      }

   }

   public void disable() {
      this.setEnabled(false);
      LemonClient.EVENT_BUS.unsubscribe((Listenable)this);

      try {
         this.onDisable();
      } catch (Exception var6) {
         MessageBus.sendClientPrefixMessage("Failed to Disable " + this.getName() + "properly due to " + var6, Notification.Type.ERROR);
         StackTraceElement[] var2 = var6.getStackTrace();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            StackTraceElement stack = var2[var4];
            System.out.println(stack.toString());
         }
      }

      if (this.toggleMsg && mc.field_71441_e != null && mc.field_71439_g != null) {
         MessageBus.sendClientDeleteMessage(this.disabledMessage.isEmpty() ? ((ColorMain)ModuleManager.getModule(ColorMain.class)).getModuleColor() + this.name + ChatFormatting.GRAY + " turned " + ((ColorMain)ModuleManager.getModule(ColorMain.class)).getDisabledColor() + "OFF" + TextFormatting.GRAY + "." : this.disabledMessage, Notification.Type.DISABLE, this.getName(), 0);
      }

      this.disabledMessage = "";
   }

   public static int getIdFromString(String name) {
      StringBuilder s = new StringBuilder();
      name = name.replace("禮", "e");
      String blacklist = "[^a-z]";

      for(int i = 0; i < name.length(); ++i) {
         s.append(Integer.parseInt(String.valueOf(name.charAt(i)).replaceAll(blacklist, "e"), 36));
      }

      try {
         s = new StringBuilder(s.substring(0, 8));
      } catch (StringIndexOutOfBoundsException var4) {
         s = new StringBuilder(Integer.MAX_VALUE);
      }

      return Integer.MAX_VALUE - Integer.parseInt(s.toString().toLowerCase());
   }

   public void toggle() {
      if (this.isEnabled()) {
         this.disable();
      } else {
         this.enable();
      }

   }

   public String getName() {
      return this.name;
   }

   public Category getCategory() {
      return this.category;
   }

   public int getPriority() {
      return this.priority;
   }

   public int getBind() {
      return this.bind;
   }

   public void setBind(int bind) {
      if (bind >= 0 && bind <= 255) {
         this.bind = bind;
      }

   }

   public String getHudInfo() {
      return "";
   }

   public boolean isDrawn() {
      return this.drawn;
   }

   public void setDrawn(boolean drawn) {
      this.drawn = drawn;
   }

   public boolean isToggleMsg() {
      return this.toggleMsg;
   }

   public void setToggleMsg(boolean toggleMsg) {
      this.toggleMsg = toggleMsg;
   }

   protected IntegerSetting registerInteger(String name, int value, int min, int max) {
      IntegerSetting integerSetting = new IntegerSetting(name, this, value, min, max);
      SettingsManager.addSetting(integerSetting);
      return integerSetting;
   }

   protected IntegerSetting registerInteger(String name, int value, int min, int max, Supplier<Boolean> dipendent) {
      IntegerSetting integerSetting = new IntegerSetting(name, this, value, min, max);
      integerSetting.setVisible(dipendent);
      SettingsManager.addSetting(integerSetting);
      return integerSetting;
   }

   protected StringSetting registerString(String name, String value) {
      StringSetting stringSetting = new StringSetting(name, this, value);
      SettingsManager.addSetting(stringSetting);
      return stringSetting;
   }

   protected StringSetting registerString(String name, String value, Supplier<Boolean> dipendent) {
      StringSetting stringSetting = new StringSetting(name, this, value);
      stringSetting.setVisible(dipendent);
      SettingsManager.addSetting(stringSetting);
      return stringSetting;
   }

   protected DoubleSetting registerDouble(String name, double value, double min, double max) {
      DoubleSetting doubleSetting = new DoubleSetting(name, this, value, min, max);
      SettingsManager.addSetting(doubleSetting);
      return doubleSetting;
   }

   protected DoubleSetting registerDouble(String name, double value, double min, double max, Supplier<Boolean> dipendent) {
      DoubleSetting doubleSetting = new DoubleSetting(name, this, value, min, max);
      doubleSetting.setVisible(dipendent);
      SettingsManager.addSetting(doubleSetting);
      return doubleSetting;
   }

   protected BooleanSetting registerBoolean(String name, boolean value) {
      BooleanSetting booleanSetting = new BooleanSetting(name, this, value);
      SettingsManager.addSetting(booleanSetting);
      return booleanSetting;
   }

   protected BooleanSetting registerBoolean(String name, boolean value, Supplier<Boolean> dipendent) {
      BooleanSetting booleanSetting = new BooleanSetting(name, this, value);
      booleanSetting.setVisible(dipendent);
      SettingsManager.addSetting(booleanSetting);
      return booleanSetting;
   }

   protected ModeSetting registerMode(String name, List<String> modes, String value) {
      ModeSetting modeSetting = new ModeSetting(name, this, value, modes);
      SettingsManager.addSetting(modeSetting);
      return modeSetting;
   }

   protected ModeSetting registerMode(String name, List<String> modes, String value, Supplier<Boolean> dipendent) {
      ModeSetting modeSetting = new ModeSetting(name, this, value, modes);
      modeSetting.setVisible(dipendent);
      SettingsManager.addSetting(modeSetting);
      return modeSetting;
   }

   protected ColorSetting registerColor(String name, GSColor color) {
      ColorSetting colorSetting = new ColorSetting(name, this, false, color);
      SettingsManager.addSetting(colorSetting);
      return colorSetting;
   }

   protected ColorSetting registerColor(String name, GSColor color, Supplier<Boolean> dipendent) {
      ColorSetting colorSetting = new ColorSetting(name, this, false, color);
      colorSetting.setVisible(dipendent);
      colorSetting.alphaEnabled();
      SettingsManager.addSetting(colorSetting);
      return colorSetting;
   }

   protected ColorSetting registerColor(String name, GSColor color, Boolean alphaEnabled) {
      ColorSetting colorSetting = new ColorSetting(name, this, false, color, alphaEnabled);
      colorSetting.alphaEnabled();
      SettingsManager.addSetting(colorSetting);
      return colorSetting;
   }

   protected ColorSetting registerColor(String name, GSColor color, Supplier<Boolean> dipendent, Boolean alphaEnabled) {
      ColorSetting colorSetting = new ColorSetting(name, this, false, color, alphaEnabled);
      colorSetting.setVisible(dipendent);
      colorSetting.alphaEnabled();
      SettingsManager.addSetting(colorSetting);
      return colorSetting;
   }

   protected ColorSetting registerColor(String name) {
      return this.registerColor(name, new GSColor(90, 145, 240));
   }

   protected ColorSetting registerColor(String name, Supplier<Boolean> dipendent) {
      ColorSetting color = this.registerColor(name, new GSColor(90, 145, 240));
      color.setVisible(dipendent);
      return color;
   }

   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.TYPE})
   public @interface Declaration {
      String name();

      Category category();

      int priority() default 0;

      int bind() default 0;

      boolean enabled() default false;

      boolean drawn() default true;

      boolean toggleMsg() default false;
   }
}

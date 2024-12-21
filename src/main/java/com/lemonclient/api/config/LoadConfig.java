package com.lemonclient.api.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lemonclient.api.setting.Setting;
import com.lemonclient.api.setting.SettingsManager;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.StringSetting;
import com.lemonclient.api.util.font.CFontRenderer;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.clickgui.GuiConfig;
import com.lemonclient.client.clickgui.LemonClientGUI;
import com.lemonclient.client.command.CommandManager;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

public class LoadConfig {
   private static final String fileName = "LemonClient/";
   private static final String moduleName = "Modules/";
   private static final String mainName = "Main/";
   private static final String miscName = "Misc/";

   public static void init() {
      try {
         loadModules();
         loadEnabledModules();
         loadModuleKeybinds();
         loadDrawnModules();
         loadToggleMessageModules();
         loadCommandPrefix();
         loadCustomFont();
         loadFriendsList();
         loadIgnoressList();
         loadEnemiesList();
         loadClickGUIPositions();
      } catch (Exception var1) {
      }

   }

   private static void loadModules() {
      String moduleLocation = "LemonClient/Modules/";
      Iterator var1 = ModuleManager.getModules().iterator();

      while(var1.hasNext()) {
         Module module = (Module)var1.next();

         try {
            loadModuleDirect(moduleLocation, module);
         } catch (IOException var4) {
            var4.printStackTrace();
         }
      }

   }

   private static void loadModuleDirect(String moduleLocation, Module module) throws IOException {
      if (Files.exists(Paths.get(moduleLocation + module.getName() + ".json"), new LinkOption[0])) {
         InputStream inputStream = Files.newInputStream(Paths.get(moduleLocation + module.getName() + ".json"));

         JsonObject moduleObject;
         try {
            moduleObject = (new JsonParser()).parse(new InputStreamReader(inputStream)).getAsJsonObject();
         } catch (IllegalStateException var10) {
            return;
         }

         if (moduleObject.get("Module") != null) {
            JsonObject settingObject = moduleObject.get("Settings").getAsJsonObject();
            Iterator var5 = SettingsManager.getSettingsForModule(module).iterator();

            while(var5.hasNext()) {
               Setting setting = (Setting)var5.next();
               JsonElement dataObject = settingObject.get(setting.getConfigName());

               try {
                  if (dataObject != null && dataObject.isJsonPrimitive()) {
                     if (setting instanceof BooleanSetting) {
                        setting.setValue(dataObject.getAsBoolean());
                     } else if (setting instanceof IntegerSetting) {
                        setting.setValue(dataObject.getAsInt());
                     } else if (setting instanceof DoubleSetting) {
                        setting.setValue(dataObject.getAsDouble());
                     } else if (setting instanceof ColorSetting) {
                        ((ColorSetting)setting).fromLong(dataObject.getAsLong());
                     } else if (setting instanceof ModeSetting) {
                        setting.setValue(dataObject.getAsString());
                     } else if (setting instanceof StringSetting) {
                        setting.setValue(dataObject.getAsString());
                        ((StringSetting)setting).setText(dataObject.getAsString());
                     }
                  }
               } catch (NumberFormatException var9) {
               }
            }

            inputStream.close();
         }
      }
   }

   private static void loadEnabledModules() throws IOException {
      String enabledLocation = "LemonClient/Main/";
      Path path = Paths.get(enabledLocation + "Toggle.json");
      if (Files.exists(path, new LinkOption[0])) {
         InputStream inputStream = Files.newInputStream(path);
         JsonObject moduleObject = (new JsonParser()).parse(new InputStreamReader(inputStream)).getAsJsonObject();
         if (moduleObject.get("Modules") != null) {
            JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
            Iterator var5 = ModuleManager.getModules().iterator();

            while(var5.hasNext()) {
               Module module = (Module)var5.next();
               JsonElement dataObject = settingObject.get(module.getName());
               if (dataObject != null && dataObject.isJsonPrimitive() && dataObject.getAsBoolean()) {
                  try {
                     module.enable();
                  } catch (NullPointerException var9) {
                  }
               }
            }

            inputStream.close();
         }
      }
   }

   private static void loadModuleKeybinds() throws IOException {
      String bindLocation = "LemonClient/Main/";
      Path path = Paths.get(bindLocation + "Bind.json");
      if (Files.exists(path, new LinkOption[0])) {
         InputStream inputStream = Files.newInputStream(path);
         JsonObject moduleObject = (new JsonParser()).parse(new InputStreamReader(inputStream)).getAsJsonObject();
         if (moduleObject.get("Modules") != null) {
            JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
            Iterator var5 = ModuleManager.getModules().iterator();

            while(var5.hasNext()) {
               Module module = (Module)var5.next();
               JsonElement dataObject = settingObject.get(module.getName());
               if (dataObject != null && dataObject.isJsonPrimitive()) {
                  module.setBind(dataObject.getAsInt());
               }
            }

            inputStream.close();
         }
      }
   }

   private static void loadDrawnModules() throws IOException {
      String drawnLocation = "LemonClient/Main/";
      Path path = Paths.get(drawnLocation + "Drawn.json");
      if (Files.exists(path, new LinkOption[0])) {
         InputStream inputStream = Files.newInputStream(path);
         JsonObject moduleObject = (new JsonParser()).parse(new InputStreamReader(inputStream)).getAsJsonObject();
         if (moduleObject.get("Modules") != null) {
            JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
            Iterator var5 = ModuleManager.getModules().iterator();

            while(var5.hasNext()) {
               Module module = (Module)var5.next();
               JsonElement dataObject = settingObject.get(module.getName());
               if (dataObject != null && dataObject.isJsonPrimitive()) {
                  module.setDrawn(dataObject.getAsBoolean());
               }
            }

            inputStream.close();
         }
      }
   }

   private static void loadToggleMessageModules() throws IOException {
      String toggleMessageLocation = "LemonClient/Main/";
      Path path = Paths.get(toggleMessageLocation + "ToggleMessages.json");
      if (Files.exists(path, new LinkOption[0])) {
         InputStream inputStream = Files.newInputStream(path);
         JsonObject moduleObject = (new JsonParser()).parse(new InputStreamReader(inputStream)).getAsJsonObject();
         if (moduleObject.get("Modules") != null) {
            JsonObject toggleObject = moduleObject.get("Modules").getAsJsonObject();
            Iterator var5 = ModuleManager.getModules().iterator();

            while(var5.hasNext()) {
               Module module = (Module)var5.next();
               JsonElement dataObject = toggleObject.get(module.getName());
               if (dataObject != null && dataObject.isJsonPrimitive()) {
                  module.setToggleMsg(dataObject.getAsBoolean());
               }
            }

            inputStream.close();
         }
      }
   }

   private static void loadCommandPrefix() throws IOException {
      String prefixLocation = "LemonClient/Main/";
      Path path = Paths.get(prefixLocation + "CommandPrefix.json");
      if (Files.exists(path, new LinkOption[0])) {
         InputStream inputStream = Files.newInputStream(path);
         JsonObject mainObject = (new JsonParser()).parse(new InputStreamReader(inputStream)).getAsJsonObject();
         if (mainObject.get("Prefix") != null) {
            JsonElement prefixObject = mainObject.get("Prefix");
            if (prefixObject != null && prefixObject.isJsonPrimitive()) {
               CommandManager.setCommandPrefix(prefixObject.getAsString());
            }

            inputStream.close();
         }
      }
   }

   private static void loadCustomFont() throws IOException {
      String fontLocation = "LemonClient/Misc/";
      Path path = Paths.get(fontLocation + "CustomFont.json");
      if (Files.exists(path, new LinkOption[0])) {
         InputStream inputStream = Files.newInputStream(path);
         JsonObject mainObject = (new JsonParser()).parse(new InputStreamReader(inputStream)).getAsJsonObject();
         if (mainObject.get("Font Name") != null && mainObject.get("Font Size") != null) {
            JsonElement fontNameObject = mainObject.get("Font Name");
            String name = null;
            if (fontNameObject != null && fontNameObject.isJsonPrimitive()) {
               name = fontNameObject.getAsString();
            }

            JsonElement fontSizeObject = mainObject.get("Font Size");
            int size = -1;
            if (fontSizeObject != null && fontSizeObject.isJsonPrimitive()) {
               size = fontSizeObject.getAsInt();
            }

            JsonElement antiAliasObject = mainObject.get("Anti Alias");
            boolean alias = true;
            if (antiAliasObject != null && antiAliasObject.isJsonPrimitive()) {
               alias = antiAliasObject.getAsBoolean();
            }

            JsonElement MetricsObject = mainObject.get("Fractional Metrics");
            boolean metrics = false;
            if (MetricsObject != null && MetricsObject.isJsonPrimitive()) {
               metrics = MetricsObject.getAsBoolean();
            }

            if (name != null && size != -1) {
               LemonClient.INSTANCE.cFontRenderer = new CFontRenderer(new Font(name, 0, size), false, true);
               LemonClient.INSTANCE.cFontRenderer.setFont(new Font(name, 0, size));
               LemonClient.INSTANCE.cFontRenderer.setAntiAlias(alias);
               LemonClient.INSTANCE.cFontRenderer.setFractionalMetrics(metrics);
               LemonClient.INSTANCE.cFontRenderer.setFontName(name);
               LemonClient.INSTANCE.cFontRenderer.setFontSize(size);
            }

            inputStream.close();
         }
      }
   }

   private static void loadFriendsList() throws IOException {
      String friendLocation = "LemonClient/Misc/";
      Path path = Paths.get(friendLocation + "Friends.json");
      if (Files.exists(path, new LinkOption[0])) {
         InputStream inputStream = Files.newInputStream(path);
         JsonObject mainObject = (new JsonParser()).parse(new InputStreamReader(inputStream)).getAsJsonObject();
         if (mainObject.get("Friends") != null) {
            JsonArray friendObject = mainObject.get("Friends").getAsJsonArray();
            friendObject.forEach((object) -> {
               SocialManager.addFriend(object.getAsString());
            });
            inputStream.close();
         }
      }
   }

   private static void loadIgnoressList() throws IOException {
      String friendLocation = "LemonClient/Misc/";
      Path path = Paths.get(friendLocation + "Ignores.json");
      if (Files.exists(path, new LinkOption[0])) {
         InputStream inputStream = Files.newInputStream(path);
         JsonObject mainObject = (new JsonParser()).parse(new InputStreamReader(inputStream)).getAsJsonObject();
         if (mainObject.get("Ignores") != null) {
            JsonArray friendObject = mainObject.get("Ignores").getAsJsonArray();
            friendObject.forEach((object) -> {
               SocialManager.addIgnore(object.getAsString());
            });
            inputStream.close();
         }
      }
   }

   private static void loadEnemiesList() throws IOException {
      String enemyLocation = "LemonClient/Misc/";
      Path path = Paths.get(enemyLocation + "Enemies.json");
      if (Files.exists(path, new LinkOption[0])) {
         InputStream inputStream = Files.newInputStream(path);
         JsonObject mainObject = (new JsonParser()).parse(new InputStreamReader(inputStream)).getAsJsonObject();
         if (mainObject.get("Enemies") != null) {
            JsonArray enemyObject = mainObject.get("Enemies").getAsJsonArray();
            enemyObject.forEach((object) -> {
               SocialManager.addEnemy(object.getAsString());
            });
            inputStream.close();
         }
      }
   }

   private static void loadClickGUIPositions() {
      LemonClientGUI.gui.loadConfig(new GuiConfig("LemonClient/Main/"));
   }
}

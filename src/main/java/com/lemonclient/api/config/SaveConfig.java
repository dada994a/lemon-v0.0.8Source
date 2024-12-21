package com.lemonclient.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.lemonclient.api.setting.Setting;
import com.lemonclient.api.setting.SettingsManager;
import com.lemonclient.api.setting.values.BooleanSetting;
import com.lemonclient.api.setting.values.ColorSetting;
import com.lemonclient.api.setting.values.DoubleSetting;
import com.lemonclient.api.setting.values.IntegerSetting;
import com.lemonclient.api.setting.values.ModeSetting;
import com.lemonclient.api.setting.values.StringSetting;
import com.lemonclient.api.util.player.social.Enemy;
import com.lemonclient.api.util.player.social.Friend;
import com.lemonclient.api.util.player.social.Ignore;
import com.lemonclient.api.util.player.social.SocialManager;
import com.lemonclient.client.LemonClient;
import com.lemonclient.client.clickgui.GuiConfig;
import com.lemonclient.client.clickgui.LemonClientGUI;
import com.lemonclient.client.command.CommandManager;
import com.lemonclient.client.module.Module;
import com.lemonclient.client.module.ModuleManager;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

public class SaveConfig {
   public static final String fileName = "LemonClient/";
   private static final String moduleName = "Modules/";
   private static final String mainName = "Main/";
   private static final String miscName = "Misc/";

   public static void init() {
      try {
         saveConfig();
         saveModules();
         saveEnabledModules();
         saveModuleKeyBinds();
         saveDrawnModules();
         saveToggleMessagesModules();
         saveCommandPrefix();
         saveCustomFont();
         saveFriendsList();
         saveEnemiesList();
         saveIgnoresList();
         saveClickGUIPositions();
      } catch (IOException var1) {
         var1.printStackTrace();
      }

   }

   private static void saveConfig() throws IOException {
      Path path = Paths.get("LemonClient/");
      if (!Files.exists(path, new LinkOption[0])) {
         Files.createDirectories(path);
      }

      Path path1 = Paths.get("LemonClient/Modules/");
      if (!Files.exists(path1, new LinkOption[0])) {
         Files.createDirectories(path1);
      }

      Path path2 = Paths.get("LemonClient/Main/");
      if (!Files.exists(path2, new LinkOption[0])) {
         Files.createDirectories(path2);
      }

      Path path3 = Paths.get("LemonClient/Misc/");
      if (!Files.exists(path3, new LinkOption[0])) {
         Files.createDirectories(path3);
      }

   }

   private static void registerFiles(String location, String name) throws IOException {
      Path path = Paths.get("LemonClient/" + location + name + ".json");
      if (Files.exists(path, new LinkOption[0])) {
         File file = new File("LemonClient/" + location + name + ".json");
         file.delete();
      }

      Files.createFile(path);
   }

   private static void saveModules() throws IOException {
      Iterator var0 = ModuleManager.getModules().iterator();

      while(var0.hasNext()) {
         Module module = (Module)var0.next();

         try {
            saveModuleDirect(module);
         } catch (IOException var3) {
            var3.printStackTrace();
         }
      }

   }

   private static void saveModuleDirect(Module module) throws IOException {
      registerFiles("Modules/", module.getName());
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("LemonClient/Modules/" + module.getName() + ".json")), StandardCharsets.UTF_8);
      JsonObject moduleObject = new JsonObject();
      JsonObject settingObject = new JsonObject();
      moduleObject.add("Module", new JsonPrimitive(module.getName()));
      Iterator var5 = SettingsManager.getSettingsForModule(module).iterator();

      while(var5.hasNext()) {
         Setting setting = (Setting)var5.next();
         if (setting instanceof BooleanSetting) {
            settingObject.add(setting.getConfigName(), new JsonPrimitive((Boolean)((BooleanSetting)setting).getValue()));
         } else if (setting instanceof IntegerSetting) {
            settingObject.add(setting.getConfigName(), new JsonPrimitive((Number)((IntegerSetting)setting).getValue()));
         } else if (setting instanceof DoubleSetting) {
            settingObject.add(setting.getConfigName(), new JsonPrimitive((Number)((DoubleSetting)setting).getValue()));
         } else if (setting instanceof ColorSetting) {
            settingObject.add(setting.getConfigName(), new JsonPrimitive(((ColorSetting)setting).toLong()));
         } else if (setting instanceof ModeSetting) {
            settingObject.add(setting.getConfigName(), new JsonPrimitive((String)((ModeSetting)setting).getValue()));
         } else if (setting instanceof StringSetting) {
            settingObject.add(setting.getConfigName(), new JsonPrimitive(((StringSetting)setting).getText()));
         }
      }

      moduleObject.add("Settings", settingObject);
      String jsonString = gson.toJson((new JsonParser()).parse(moduleObject.toString()));
      fileOutputStreamWriter.write(jsonString);
      fileOutputStreamWriter.close();
   }

   private static void saveEnabledModules() throws IOException {
      registerFiles("Main/", "Toggle");
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("LemonClient/Main/Toggle.json")), StandardCharsets.UTF_8);
      JsonObject moduleObject = new JsonObject();
      JsonObject enabledObject = new JsonObject();
      Iterator var4 = ModuleManager.getModules().iterator();

      while(var4.hasNext()) {
         Module module = (Module)var4.next();
         enabledObject.add(module.getName(), new JsonPrimitive(module.isEnabled()));
      }

      moduleObject.add("Modules", enabledObject);
      String jsonString = gson.toJson((new JsonParser()).parse(moduleObject.toString()));
      fileOutputStreamWriter.write(jsonString);
      fileOutputStreamWriter.close();
   }

   private static void saveModuleKeyBinds() throws IOException {
      registerFiles("Main/", "Bind");
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("LemonClient/Main/Bind.json")), StandardCharsets.UTF_8);
      JsonObject moduleObject = new JsonObject();
      JsonObject bindObject = new JsonObject();
      Iterator var4 = ModuleManager.getModules().iterator();

      while(var4.hasNext()) {
         Module module = (Module)var4.next();
         bindObject.add(module.getName(), new JsonPrimitive(module.getBind()));
      }

      moduleObject.add("Modules", bindObject);
      String jsonString = gson.toJson((new JsonParser()).parse(moduleObject.toString()));
      fileOutputStreamWriter.write(jsonString);
      fileOutputStreamWriter.close();
   }

   private static void saveDrawnModules() throws IOException {
      registerFiles("Main/", "Drawn");
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("LemonClient/Main/Drawn.json")), StandardCharsets.UTF_8);
      JsonObject moduleObject = new JsonObject();
      JsonObject drawnObject = new JsonObject();
      Iterator var4 = ModuleManager.getModules().iterator();

      while(var4.hasNext()) {
         Module module = (Module)var4.next();
         drawnObject.add(module.getName(), new JsonPrimitive(module.isDrawn()));
      }

      moduleObject.add("Modules", drawnObject);
      String jsonString = gson.toJson((new JsonParser()).parse(moduleObject.toString()));
      fileOutputStreamWriter.write(jsonString);
      fileOutputStreamWriter.close();
   }

   private static void saveToggleMessagesModules() throws IOException {
      registerFiles("Main/", "ToggleMessages");
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("LemonClient/Main/ToggleMessages.json")), StandardCharsets.UTF_8);
      JsonObject moduleObject = new JsonObject();
      JsonObject toggleMessagesObject = new JsonObject();
      Iterator var4 = ModuleManager.getModules().iterator();

      while(var4.hasNext()) {
         Module module = (Module)var4.next();
         toggleMessagesObject.add(module.getName(), new JsonPrimitive(module.isToggleMsg()));
      }

      moduleObject.add("Modules", toggleMessagesObject);
      String jsonString = gson.toJson((new JsonParser()).parse(moduleObject.toString()));
      fileOutputStreamWriter.write(jsonString);
      fileOutputStreamWriter.close();
   }

   private static void saveCommandPrefix() throws IOException {
      registerFiles("Main/", "CommandPrefix");
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("LemonClient/Main/CommandPrefix.json")), StandardCharsets.UTF_8);
      JsonObject prefixObject = new JsonObject();
      prefixObject.add("Prefix", new JsonPrimitive(CommandManager.getCommandPrefix()));
      String jsonString = gson.toJson((new JsonParser()).parse(prefixObject.toString()));
      fileOutputStreamWriter.write(jsonString);
      fileOutputStreamWriter.close();
   }

   private static void saveCustomFont() throws IOException {
      registerFiles("Misc/", "CustomFont");
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("LemonClient/Misc/CustomFont.json")), StandardCharsets.UTF_8);
      JsonObject fontObject = new JsonObject();
      fontObject.add("Font Name", new JsonPrimitive(LemonClient.INSTANCE.cFontRenderer.getFontName()));
      fontObject.add("Font Size", new JsonPrimitive(LemonClient.INSTANCE.cFontRenderer.getFontSize()));
      fontObject.add("Anti Alias", new JsonPrimitive(LemonClient.INSTANCE.cFontRenderer.getAntiAlias()));
      fontObject.add("Fractional Metrics", new JsonPrimitive(LemonClient.INSTANCE.cFontRenderer.getFractionalMetrics()));
      String jsonString = gson.toJson((new JsonParser()).parse(fontObject.toString()));
      fileOutputStreamWriter.write(jsonString);
      fileOutputStreamWriter.close();
   }

   private static void saveFriendsList() throws IOException {
      registerFiles("Misc/", "Friends");
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("LemonClient/Misc/Friends.json")), StandardCharsets.UTF_8);
      JsonObject mainObject = new JsonObject();
      JsonArray friendArray = new JsonArray();
      Iterator var4 = SocialManager.getFriends().iterator();

      while(var4.hasNext()) {
         Friend friend = (Friend)var4.next();
         friendArray.add(friend.getName());
      }

      mainObject.add("Friends", friendArray);
      String jsonString = gson.toJson((new JsonParser()).parse(mainObject.toString()));
      fileOutputStreamWriter.write(jsonString);
      fileOutputStreamWriter.close();
   }

   private static void saveEnemiesList() throws IOException {
      registerFiles("Misc/", "Enemies");
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("LemonClient/Misc/Enemies.json")), StandardCharsets.UTF_8);
      JsonObject mainObject = new JsonObject();
      JsonArray enemyArray = new JsonArray();
      Iterator var4 = SocialManager.getEnemies().iterator();

      while(var4.hasNext()) {
         Enemy enemy = (Enemy)var4.next();
         enemyArray.add(enemy.getName());
      }

      mainObject.add("Enemies", enemyArray);
      String jsonString = gson.toJson((new JsonParser()).parse(mainObject.toString()));
      fileOutputStreamWriter.write(jsonString);
      fileOutputStreamWriter.close();
   }

   private static void saveIgnoresList() throws IOException {
      registerFiles("Misc/", "Ignores");
      Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
      OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("LemonClient/Misc/Ignores.json")), StandardCharsets.UTF_8);
      JsonObject mainObject = new JsonObject();
      JsonArray ignoreArray = new JsonArray();
      Iterator var4 = SocialManager.getIgnores().iterator();

      while(var4.hasNext()) {
         Ignore ignore = (Ignore)var4.next();
         ignoreArray.add(ignore.getName());
      }

      mainObject.add("Ignores", ignoreArray);
      String jsonString = gson.toJson((new JsonParser()).parse(mainObject.toString()));
      fileOutputStreamWriter.write(jsonString);
      fileOutputStreamWriter.close();
   }

   private static void saveClickGUIPositions() throws IOException {
      registerFiles("Main/", "ClickGUI");
      LemonClientGUI.gui.saveConfig(new GuiConfig("LemonClient/Main/"));
   }
}

package com.lemonclient.api.setting;

import com.lemonclient.client.module.Module;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SettingsManager {
   private static final ArrayList<Setting> settings = new ArrayList();

   public static void addSetting(Setting setting) {
      settings.add(setting);
   }

   public static ArrayList<Setting> getSettings() {
      return settings;
   }

   public static List<Setting> getSettingsForModule(Module module) {
      return (List)settings.stream().filter((setting) -> {
         return setting.getModule().equals(module);
      }).collect(Collectors.toList());
   }
}

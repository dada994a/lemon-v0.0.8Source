package com.lemonclient.api.setting.values;

import com.lemonclient.api.setting.Setting;
import com.lemonclient.client.module.Module;
import java.util.function.Supplier;

public class BooleanSetting extends Setting<Boolean> {
   public BooleanSetting(String name, Module module, boolean value) {
      super(value, name, module);
   }

   public BooleanSetting(String name, String configName, Module module, Supplier<Boolean> isVisible, boolean value) {
      super(value, name, configName, module, isVisible);
   }
}

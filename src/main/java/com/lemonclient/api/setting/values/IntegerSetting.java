package com.lemonclient.api.setting.values;

import com.lemonclient.api.setting.Setting;
import com.lemonclient.client.module.Module;
import java.util.function.Supplier;

public class IntegerSetting extends Setting<Integer> {
   private final int min;
   private final int max;

   public IntegerSetting(String name, Module module, int value, int min, int max) {
      super(value, name, module);
      this.min = min;
      this.max = max;
   }

   public IntegerSetting(String name, String configName, Module module, Supplier<Boolean> isVisible, int value, int min, int max) {
      super(value, name, configName, module, isVisible);
      this.min = min;
      this.max = max;
   }

   public int getMin() {
      return this.min;
   }

   public int getMax() {
      return this.max;
   }
}

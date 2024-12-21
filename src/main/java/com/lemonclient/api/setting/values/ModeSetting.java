package com.lemonclient.api.setting.values;

import com.lemonclient.api.setting.Setting;
import com.lemonclient.client.module.Module;
import java.util.List;
import java.util.function.Supplier;

public class ModeSetting extends Setting<String> {
   private final List<String> modes;

   public ModeSetting(String name, Module module, String value, List<String> modes) {
      super(value, name, module);
      this.modes = modes;
   }

   public ModeSetting(String name, String configName, Module module, String value, Supplier<Boolean> isVisible, List<String> modes) {
      super(value, name, configName, module, isVisible);
      this.modes = modes;
   }

   public List<String> getModes() {
      return this.modes;
   }

   public void increment() {
      int modeIndex = this.modes.indexOf(this.getValue());
      modeIndex = (modeIndex + 1) % this.modes.size();
      this.setValue(this.modes.get(modeIndex));
   }

   public void decrement() {
      int modeIndex = this.modes.indexOf(this.getValue());
      --modeIndex;
      if (modeIndex < 0) {
         modeIndex = this.modes.size() - 1;
      }

      this.setValue(this.modes.get(modeIndex));
   }
}

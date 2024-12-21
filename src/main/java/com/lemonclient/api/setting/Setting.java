package com.lemonclient.api.setting;

import com.lemonclient.client.module.Module;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class Setting<T> {
   private T value;
   private final String name;
   private final String configName;
   private final Module module;
   private Supplier<Boolean> isVisible;
   private final List<Setting<?>> subSettings;

   public Setting(T value, String name, String configName, Module module, Supplier<Boolean> isVisible) {
      this.subSettings = new ArrayList();
      this.value = value;
      this.name = name;
      this.configName = configName;
      this.module = module;
      this.isVisible = isVisible;
   }

   public void setVisible(Supplier<Boolean> vis) {
      this.isVisible = vis;
   }

   public Setting(T value, String name, Module module) {
      this(value, name, name.replace(" ", ""), module, () -> {
         return true;
      });
   }

   public T getValue() {
      return this.value;
   }

   public void setValue(T value) {
      this.value = value;
   }

   public String getName() {
      return this.name;
   }

   public String getConfigName() {
      return this.configName;
   }

   public Module getModule() {
      return this.module;
   }

   public boolean isVisible() {
      return (Boolean)this.isVisible.get();
   }

   public Stream<Setting<?>> getSubSettings() {
      return this.subSettings.stream();
   }

   public void addSubSetting(Setting<?> setting) {
      this.subSettings.add(setting);
   }
}

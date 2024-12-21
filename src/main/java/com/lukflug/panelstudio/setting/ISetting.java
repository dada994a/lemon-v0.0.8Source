package com.lukflug.panelstudio.setting;

import java.util.stream.Stream;

public interface ISetting<T> extends ILabeled {
   T getSettingState();

   Class<T> getSettingClass();

   default Stream<ISetting<?>> getSubSettings() {
      return null;
   }
}

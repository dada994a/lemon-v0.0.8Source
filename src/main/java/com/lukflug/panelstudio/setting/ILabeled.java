package com.lukflug.panelstudio.setting;

import com.lukflug.panelstudio.base.IBoolean;

@FunctionalInterface
public interface ILabeled {
   String getDisplayName();

   default String getDescription() {
      return null;
   }

   default IBoolean isVisible() {
      return () -> {
         return true;
      };
   }
}

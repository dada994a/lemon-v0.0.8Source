package com.lukflug.panelstudio.setting;

import java.util.Arrays;

public interface IEnumSetting extends ISetting<String> {
   void increment();

   void decrement();

   String getValueName();

   default int getValueIndex() {
      ILabeled[] stuff = this.getAllowedValues();
      String compare = this.getValueName();

      for(int i = 0; i < stuff.length; ++i) {
         if (stuff[i].getDisplayName().equals(compare)) {
            return i;
         }
      }

      return -1;
   }

   void setValueIndex(int var1);

   ILabeled[] getAllowedValues();

   default String getSettingState() {
      return this.getValueName();
   }

   default Class<String> getSettingClass() {
      return String.class;
   }

   static ILabeled[] getVisibleValues(IEnumSetting setting) {
      return (ILabeled[])Arrays.stream(setting.getAllowedValues()).filter((value) -> {
         return value.isVisible().isOn();
      }).toArray((x$0) -> {
         return new ILabeled[x$0];
      });
   }
}

package com.lukflug.panelstudio.setting;

public interface IStringSetting extends ISetting<String> {
   String getValue();

   void setValue(String var1);

   default String getSettingState() {
      return this.getValue();
   }

   default Class<String> getSettingClass() {
      return String.class;
   }
}

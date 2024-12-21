package com.lukflug.panelstudio.setting;

public interface IKeybindSetting extends ISetting<String> {
   int getKey();

   void setKey(int var1);

   String getKeyName();

   default String getSettingState() {
      return this.getKeyName();
   }

   default Class<String> getSettingClass() {
      return String.class;
   }
}

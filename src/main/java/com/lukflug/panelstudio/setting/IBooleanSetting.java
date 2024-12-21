package com.lukflug.panelstudio.setting;

import com.lukflug.panelstudio.base.IToggleable;

public interface IBooleanSetting extends ISetting<Boolean>, IToggleable {
   default Boolean getSettingState() {
      return this.isOn();
   }

   default Class<Boolean> getSettingClass() {
      return Boolean.class;
   }
}

package com.lukflug.panelstudio.setting;

import java.awt.Color;

public interface IColorSetting extends ISetting<Color> {
   Color getValue();

   void setValue(Color var1);

   Color getColor();

   boolean getRainbow();

   void setRainbow(boolean var1);

   default boolean hasAlpha() {
      return false;
   }

   default boolean allowsRainbow() {
      return true;
   }

   default boolean hasHSBModel() {
      return false;
   }

   default Color getSettingState() {
      return this.getValue();
   }

   default Class<Color> getSettingClass() {
      return Color.class;
   }
}

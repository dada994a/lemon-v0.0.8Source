package com.lukflug.panelstudio.setting;

public interface INumberSetting extends ISetting<String> {
   double getNumber();

   void setNumber(double var1);

   double getMaximumValue();

   double getMinimumValue();

   int getPrecision();

   default String getSettingState() {
      return this.getPrecision() == 0 ? "" + (int)this.getNumber() : String.format("%." + this.getPrecision() + "f", this.getNumber());
   }

   default Class<String> getSettingClass() {
      return String.class;
   }
}

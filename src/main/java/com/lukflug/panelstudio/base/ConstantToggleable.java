package com.lukflug.panelstudio.base;

public class ConstantToggleable implements IToggleable {
   protected boolean value;

   public ConstantToggleable(boolean value) {
      this.value = value;
   }

   public boolean isOn() {
      return this.value;
   }

   public void toggle() {
   }
}

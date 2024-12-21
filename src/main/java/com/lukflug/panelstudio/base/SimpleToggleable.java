package com.lukflug.panelstudio.base;

public class SimpleToggleable extends ConstantToggleable {
   public SimpleToggleable(boolean value) {
      super(value);
   }

   public void toggle() {
      this.value = !this.value;
   }
}

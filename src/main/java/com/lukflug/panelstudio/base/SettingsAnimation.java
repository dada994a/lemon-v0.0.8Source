package com.lukflug.panelstudio.base;

import java.util.function.Supplier;

public class SettingsAnimation extends Animation {
   protected final Supplier<Integer> speed;

   public SettingsAnimation(Supplier<Integer> speed, Supplier<Long> time) {
      super(time);
      this.speed = speed;
   }

   protected int getSpeed() {
      return (Integer)this.speed.get();
   }
}

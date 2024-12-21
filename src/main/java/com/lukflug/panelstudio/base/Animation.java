package com.lukflug.panelstudio.base;

import java.util.function.Supplier;

public abstract class Animation {
   protected final Supplier<Long> time;
   protected double value;
   protected double lastValue;
   protected long lastTime;

   public Animation(Supplier<Long> time) {
      this.time = time;
      this.lastTime = (Long)time.get();
   }

   public void initValue(double value) {
      this.value = value;
      this.lastValue = value;
   }

   public double getValue() {
      if (this.getSpeed() == 0) {
         return this.value;
      } else {
         double weight = (double)((Long)this.time.get() - this.lastTime) / (double)this.getSpeed();
         if (weight >= 1.0D) {
            return this.value;
         } else if (weight <= 0.0D) {
            return this.lastValue;
         } else {
            weight = this.interpolate(weight);
            return this.value * weight + this.lastValue * (1.0D - weight);
         }
      }
   }

   public double getTarget() {
      return this.value;
   }

   public void setValue(double value) {
      this.lastValue = this.getValue();
      this.value = value;
      this.lastTime = (Long)this.time.get();
   }

   protected double interpolate(double weight) {
      return (weight - 1.0D) * (weight - 1.0D) * (weight - 1.0D) + 1.0D;
   }

   protected abstract int getSpeed();
}

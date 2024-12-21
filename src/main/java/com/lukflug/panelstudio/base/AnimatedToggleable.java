package com.lukflug.panelstudio.base;

public final class AnimatedToggleable implements IToggleable {
   private final IToggleable toggle;
   private final Animation animation;

   public AnimatedToggleable(IToggleable toggle, Animation animation) {
      if (toggle != null) {
         this.toggle = toggle;
      } else {
         this.toggle = new SimpleToggleable(false);
      }

      if (animation != null) {
         this.animation = animation;
      } else {
         this.animation = new Animation(System::currentTimeMillis) {
            protected int getSpeed() {
               return 0;
            }
         };
      }

      if (this.toggle.isOn()) {
         this.animation.initValue(1.0D);
      } else {
         this.animation.initValue(0.0D);
      }

   }

   public void toggle() {
      this.toggle.toggle();
      if (this.toggle.isOn()) {
         this.animation.setValue(1.0D);
      } else {
         this.animation.setValue(0.0D);
      }

   }

   public boolean isOn() {
      return this.toggle.isOn();
   }

   public double getValue() {
      if (this.animation.getTarget() != (double)(this.toggle.isOn() ? 1 : 0)) {
         if (this.toggle.isOn()) {
            this.animation.setValue(1.0D);
         } else {
            this.animation.setValue(0.0D);
         }
      }

      return this.animation.getValue();
   }
}

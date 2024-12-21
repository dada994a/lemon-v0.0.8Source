package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import java.awt.Rectangle;

@FunctionalInterface
public interface ISliderRendererProxy extends ISliderRenderer {
   default void renderSlider(Context context, String title, String state, boolean focus, double value) {
      this.getRenderer().renderSlider(context, title, state, focus, value);
   }

   default int getDefaultHeight() {
      return this.getRenderer().getDefaultHeight();
   }

   default Rectangle getSlideArea(Context context, String title, String state) {
      return this.getRenderer().getSlideArea(context, title, state);
   }

   ISliderRenderer getRenderer();
}

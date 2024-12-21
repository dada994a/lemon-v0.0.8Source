package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import java.awt.Color;
import java.awt.Point;

@FunctionalInterface
public interface IColorPickerRendererProxy extends IColorPickerRenderer {
   default void renderPicker(Context context, boolean focus, Color color) {
      this.getRenderer().renderPicker(context, focus, color);
   }

   default Color transformPoint(Context context, Color color, Point point) {
      return this.getRenderer().transformPoint(context, color, point);
   }

   default int getDefaultHeight(int width) {
      return this.getRenderer().getDefaultHeight(width);
   }

   IColorPickerRenderer getRenderer();
}

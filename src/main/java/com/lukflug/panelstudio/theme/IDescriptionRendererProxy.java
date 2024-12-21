package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.IInterface;
import java.awt.Point;

@FunctionalInterface
public interface IDescriptionRendererProxy extends IDescriptionRenderer {
   default void renderDescription(IInterface inter, Point pos, String text) {
      this.getRenderer().renderDescription(inter, pos, text);
   }

   IDescriptionRenderer getRenderer();
}

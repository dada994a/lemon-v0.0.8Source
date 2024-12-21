package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;

@FunctionalInterface
public interface IResizeBorderRendererProxy extends IResizeBorderRenderer {
   default void drawBorder(Context context, boolean focus) {
      this.getRenderer().drawBorder(context, focus);
   }

   default int getBorder() {
      return this.getRenderer().getBorder();
   }

   IResizeBorderRenderer getRenderer();
}

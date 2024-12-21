package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;

@FunctionalInterface
public interface IButtonRendererProxy<T> extends IButtonRenderer<T> {
   default void renderButton(Context context, String title, boolean focus, T state) {
      this.getRenderer().renderButton(context, title, focus, state);
   }

   default int getDefaultHeight() {
      return this.getRenderer().getDefaultHeight();
   }

   IButtonRenderer<T> getRenderer();
}

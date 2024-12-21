package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;

@FunctionalInterface
public interface IEmptySpaceRendererProxy<T> extends IEmptySpaceRenderer<T> {
   default void renderSpace(Context context, boolean focus, T state) {
      this.getRenderer().renderSpace(context, focus, state);
   }

   IEmptySpaceRenderer<T> getRenderer();
}

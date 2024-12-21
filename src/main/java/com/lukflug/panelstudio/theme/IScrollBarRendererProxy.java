package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;

@FunctionalInterface
public interface IScrollBarRendererProxy<T> extends IScrollBarRenderer<T> {
   default int renderScrollBar(Context context, boolean focus, T state, boolean horizontal, int height, int position) {
      return this.getRenderer().renderScrollBar(context, focus, state, horizontal, height, position);
   }

   default int getThickness() {
      return this.getRenderer().getThickness();
   }

   IScrollBarRenderer<T> getRenderer();
}

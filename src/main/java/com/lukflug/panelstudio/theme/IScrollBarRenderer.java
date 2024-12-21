package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;

public interface IScrollBarRenderer<T> {
   default int renderScrollBar(Context context, boolean focus, T state, boolean horizontal, int height, int position) {
      return position;
   }

   default int getThickness() {
      return 0;
   }
}

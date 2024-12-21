package com.lukflug.panelstudio.component;

import com.lukflug.panelstudio.base.Context;

public interface IScrollSize {
   default int getScrollHeight(Context context, int componentHeight) {
      return componentHeight;
   }

   default int getComponentWidth(Context context) {
      return context.getSize().width;
   }
}

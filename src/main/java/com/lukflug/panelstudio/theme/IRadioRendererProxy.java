package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import com.lukflug.panelstudio.setting.ILabeled;
import java.awt.Rectangle;

@FunctionalInterface
public interface IRadioRendererProxy extends IRadioRenderer {
   default void renderItem(Context context, ILabeled[] items, boolean focus, int target, double state, boolean horizontal) {
      this.getRenderer().renderItem(context, items, focus, target, state, horizontal);
   }

   default int getDefaultHeight(ILabeled[] items, boolean horizontal) {
      return this.getRenderer().getDefaultHeight(items, horizontal);
   }

   default Rectangle getItemRect(Context context, ILabeled[] items, int index, boolean horizontal) {
      return this.getRenderer().getItemRect(context, items, index, horizontal);
   }

   IRadioRenderer getRenderer();
}

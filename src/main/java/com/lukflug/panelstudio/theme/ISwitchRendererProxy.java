package com.lukflug.panelstudio.theme;

import com.lukflug.panelstudio.base.Context;
import java.awt.Rectangle;

@FunctionalInterface
public interface ISwitchRendererProxy<T> extends ISwitchRenderer<T>, IButtonRendererProxy<T> {
   default Rectangle getOnField(Context context) {
      return this.getRenderer().getOnField(context);
   }

   default Rectangle getOffField(Context context) {
      return this.getRenderer().getOffField(context);
   }

   ISwitchRenderer<T> getRenderer();
}
